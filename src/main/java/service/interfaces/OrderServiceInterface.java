package service.interfaces;

import entity.Order;
import lombok.NonNull;

import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface OrderServiceInterface {
    /**
     * @param routeNumber name of rout in [0-9]{5} format
     * @param date  in  (dd.MM.yyyy hh:mm) format
     * @throws ParseException when date set in wrong format
     * @throws ValidationException when {@param routeNumber} set in wrong format
     */
    Long saveOrder(@NonNull Integer routeNumber, @NotNull String date, @NotNull Integer userId, @NotNull Integer ticketId)
            throws ParseException, ValidationException;

    /**
     * return orders for future rout by user
     * elements sorted by date from earliest to latest
     */
    List<Order> getOrdersOnFuture(@NonNull int userId);
}
