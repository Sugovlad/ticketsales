package service;

import entity.Order;
import entity.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import repository.OrderRepository;
import service.interfaces.OrderApplyInterface;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@EnableScheduling
@Slf4j
@Component
public class OrderApplyService implements OrderApplyInterface {
    public static final int RETRY_COUNT = 3;
    public static final String CHECK_API = "apply/";

    @Autowired
    OrderRepository orderRepository;

    @Value("${custom.apply-order-thread}")
    Integer threads;
    @Value("${custom.host}")
    String host;
    RestTemplate restTemplate = new RestTemplate();

    @Override
    @Scheduled(fixedDelay = 60000)
    @Bean
    @Transactional
    @ConditionalOnProperty(name = "custom.applyOrderThread")
    public void applyOrder() throws InterruptedException {
        List<Order> orders = getOrdersToApply();
        if (orders.isEmpty()) {
            log.info("No element with status processing");
            return;
        }
        List<Future<Order>> futures = parallelOrders(orders);
        saveAppliedOrders(futures);
    }

    private List<Order> getOrdersToApply() {
        log.info("apply order start");
        List<Order> orders = orderRepository.findAllByStatusOrStatus(Status.IN_PROGRESS, Status.PROCESSING).stream()
                .sorted((o1, o2) -> o1.status() == o2.status() ? 0 : o1.status() == Status.IN_PROGRESS ? 1 : -1)
                .collect(Collectors.toList());

        log.info("Processing order: " + orders);
        return orders;
    }

    private List<Future<Order>> parallelOrders(List<Order> orders) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(threads);

        Iterator<Order> orderIterator = orders.iterator();
        List<Callable<Order>> callables = new ArrayList<>();
        for (int i = 0; i < threads && orderIterator.hasNext(); i++) {
            Order tmpOrder = orderIterator.next();
            callables.add(() -> processOrder(tmpOrder));
        }
        return executorService.invokeAll(callables);
    }

    private void saveAppliedOrders(List<Future<Order>> futures) {
        List<Order> resultOrders = futures.stream().map(orderFuture -> {
            try {
                return orderFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error(e.toString());
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        orderRepository.saveAll(resultOrders);
    }

    private Order processOrder(Order order) {
        //retry if returned processing
        for (int i = 0; i < RETRY_COUNT && (order.status() == Status.PROCESSING || order.status() == Status.IN_PROGRESS); i++) {
            order.status(Status.IN_PROGRESS);

            Status status = restTemplate
                    .getForObject(host + CHECK_API + "/" + order.routNumber(), Status.class);
            order.status(status);
            log.info("Retry: " + Thread.currentThread());
        }
        if (order.status() == Status.IN_PROGRESS) {
            order.status(Status.PROCESSING);
        }

        log.info(order.routNumber() + " of user: " + order.userId() + " is " + order.status());
        return order;
    }
}
