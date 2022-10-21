package io.roach.spring.locking.domain;

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

    @Autowired
    private ProductService productService;

    @Scheduled(cron = "2 * * * * ?")
    @SchedulerLock(lockAtLeastFor = "1m", lockAtMostFor = "5m", name = "publishNews_clusterSingleton")
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

        try {
            logger.info("Thinking for 3min..");
            Thread.sleep(3*60*1_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        logger.info("<< Exiting cluster singleton method");
    }
}
