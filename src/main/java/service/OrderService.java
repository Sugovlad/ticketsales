package service;

import entity.Order;
import entity.Status;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import repository.OrderRepository;
import service.interfaces.OrderServiceInterface;

import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@NoArgsConstructor
public class OrderService implements OrderServiceInterface {
    OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Long saveOrder(@NonNull Integer routeNumber, @NotNull String date,
                          @NotNull Integer userId, @NotNull Integer ticketId) throws ParseException, ValidationException {

        Date parseDate;
        try {
            Order duplicateOrder = orderRepository.findByTicketId(ticketId);
            if (duplicateOrder != null) {
                return duplicateOrder.id();
            }
            parseDate = new SimpleDateFormat("dd.MM.yyyy hh:mm").parse(date);
        } catch (ParseException e) {
            throw new ParseException("wrong date format", e.getErrorOffset());
        }
        Order order = new Order().routNumber(routeNumber).date(parseDate)
                .status(Status.PROCESSING).userId(userId).ticketId(ticketId);
        return orderRepository.save(order).id();
    }

    @Override
    public List<Order> getOrdersOnFuture(@NonNull int userId) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        Date date = calendar.getTime();
        return orderRepository.findAllByUserIdAndDateIsAfter(userId, date).stream()
                .sorted((o1, o2) -> o1.date().after(o2.date()) ? 1 : o1.date() == o2.date() ? 0 : -1).collect(Collectors.toList());
    }
}
