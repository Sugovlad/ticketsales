package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import repository.OrderRepository;
import service.interfaces.OrderServiceInterface;

import javax.validation.ValidationException;
import java.text.ParseException;


@RestController
public class OrderController {
    @Autowired
    OrderServiceInterface orderServiceInterface;
    @Autowired
    OrderRepository repository;


    @PostMapping("/saveOrder/{userId}")
    public String saveOrder(@NonNull @RequestParam Integer ticketId,
                            @NonNull @PathVariable Integer userId,
                            @NonNull @RequestParam Integer route, @RequestParam @NonNull String date) {
        try {
            return orderServiceInterface.saveOrder(route, date, userId, ticketId).toString();
        } catch (ParseException | ValidationException parseException) {
            return "{\"error\":" + "\"" + parseException.getMessage() + "\"}";
        }
    }

    @ResponseBody
    @GetMapping("/getFutureOrders/{userId}")
    public String getFutureOrders(@NonNull @PathVariable Integer userId) {
        try {
            return new ObjectMapper().writeValueAsString(orderServiceInterface.getOrdersOnFuture(userId));
        } catch (JsonProcessingException parseException) {
            return "{}";
        }
    }

}
