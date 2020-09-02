package entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "`order`")
@Accessors(chain = true, fluent = true)
@Getter
@Setter
@EqualsAndHashCode
public class Order {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonProperty
    @Column(name = "rout_number")
    private Integer routNumber;

    /**
     * @throws javax.validation.ValidationException rout number must consist 5 digits
     */

    public Order routNumber(int routNumber) {
        if (routNumber < 10000 || routNumber > 99999) {
            throw new javax.validation.ValidationException("rout number must consist 5 digits");
        }
        this.routNumber = routNumber;
        return this;
    }

    @JsonProperty
    @Column(name = "date_of_fly")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private Date date;

    @JsonProperty
    @Column(name = "status_of_order")
    private Status status;

    @JsonProperty
    @Column(name = "user_id")
    private Integer userId;

    @JsonProperty
    @Column(name = "ticket_id")
    private Integer ticketId;
}
