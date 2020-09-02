package repository;

import entity.Order;
import entity.Status;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
    public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByStatusOrStatus(Status status, Status status1);

    Order findByDateIsAfter(Date date);

    Order findByTicketId(int id);

    List<Order> findAllByUserIdAndDateIsAfter(int id, Date date);
}
