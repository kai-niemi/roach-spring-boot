package io.roach.spring.locking.domain;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Service
public class ProductNewsFeed {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors());

    @Autowired
    private ProductService productService;

    @Scheduled(cron = "0/15 * * * * ?")
    @SchedulerLock(lockAtLeastFor = "1m", lockAtMostFor = "3m", name = "publishNews_clusterSingleton")
    public void publishNews() {
        logger.info(">> Entered cluster singleton method");

        Page<Product> products = productService.findAll(Pageable.ofSize(128));
        while (products.hasContent()) {
            logger.info("Processing page {} of {}", products.getNumber(), products.getTotalPages());
            if (!products.hasNext()) {
                break;
            }
            products = productService.findAll(products.nextPageable());
        }

        final long startTime = System.currentTimeMillis();

        ScheduledFuture<?> f = executorService.scheduleAtFixedRate(() -> {
            logger.info("Gone fishing for {}s...", (System.currentTimeMillis() - startTime) / 1000);
        }, 0, 5, TimeUnit.SECONDS);

        try {
            // Set this to higher than lockAtMostFor to observe breach of invariant
            executorService.awaitTermination(2, TimeUnit.MINUTES);
            f.cancel(true);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        logger.info("<< Exiting cluster singleton method");
    }
}
