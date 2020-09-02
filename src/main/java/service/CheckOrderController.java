package service;

import entity.Status;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class CheckOrderController {

    @GetMapping("/apply/{routNumber}")
    Status processOrder(@NonNull @PathVariable Integer routNumber) {
        return Status.values()[new Random().nextInt(Status.values().length-1)]; //extract IN_Progress status
    }
}
