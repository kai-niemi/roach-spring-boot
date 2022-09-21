package io.roach.spring.pooling;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.LongStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping(value = "/workload")
public class WorkloadController {
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    @Autowired
    private AccountService accountService;

    @GetMapping
    public ResponseEntity<WorkloadForm> getFormTemplate(@RequestParam Map<String, String> requestParams) {
        WorkloadForm form = new WorkloadForm();
        form.setBatchSize(128);
        form.setNumAccounts(5_000);
        form.setDescription("Create account batches");
        return ResponseEntity.ok(form);
    }

    @PostMapping
    public ResponseEntity<StreamingResponseBody> submitForm(@RequestBody WorkloadForm form) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(outputStream -> processForm(form, outputStream));
    }

    private void processForm(WorkloadForm form, OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream, true);
        pw.printf("Creating %,d accounts with batch size %d\n", form.getNumAccounts(), form.getBatchSize());

        int batchSize = form.getBatchSize();
        int numAccounts = 0;
        while (numAccounts < form.getNumAccounts()) {
            batchSize = Math.min(Math.abs(form.getNumAccounts() - numAccounts), batchSize);
            numAccounts += batchSize;

            accountService.createAll(createBatch(batchSize));

            pw.println(numAccounts);
            pw.flush();
        }

        pw.println("Done");
    }

    private List<AccountEntity> createBatch(int size) {
        List<AccountEntity> batch = new ArrayList<>();
        LongStream.rangeClosed(1, size).forEach(value -> batch.add(createInstance()));
        return batch;
    }

    private AccountEntity createInstance() {
        AccountEntity instance = new AccountEntity();
        instance.setBalance(RANDOM.nextDouble(100.00, 5000.00));
        instance.setCurrency("USD");
        instance.setName(randomName(32));
        instance.setDescription(randomName(64));
        return instance;
    }

    private static String randomName(int min) {
        byte[] buffer = new byte[min];
        RANDOM.nextBytes(buffer);
        return ENCODER.encodeToString(buffer);
    }
}
