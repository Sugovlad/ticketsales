package service.interfaces;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
public interface OrderApplyInterface {

    /**
     * Get order from db and apply it
     *
     * @throws java.util.NoSuchElementException in case there's no order with processing status
     */
    @Scheduled(fixedDelay = 60000)
    void applyOrder() throws InterruptedException;
}
