package service;

import entity.Order;
import entity.Status;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.lang.Nullable;
import org.springframework.test.context.junit4.SpringRunner;
import repository.OrderRepository;
import service.interfaces.OrderServiceInterface;

import javax.validation.ValidationException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@Slf4j
@RunWith(SpringRunner.class)
class OrderServiceTest {

    public static final String DATE_PATTERN = "dd.MM.yyyy";
    public static final String MOCK_DATE = "1.01.1970 03:00";
    OrderRepository mock = Mockito.mock(OrderRepository.class);


    private final OrderServiceInterface orderService = new OrderService(mock);

    private List<Date> getDates() {
        try {
            return new ArrayList<>(new ArrayList<>(Arrays.asList(
                    new SimpleDateFormat(DATE_PATTERN).parse("22.06.2000"),
                    new SimpleDateFormat(DATE_PATTERN).parse("21.10.2000"),
                    new SimpleDateFormat(DATE_PATTERN).parse("20.10.2000"),
                    new SimpleDateFormat(DATE_PATTERN).parse("23.10.2000"),
                    new SimpleDateFormat(DATE_PATTERN).parse("25.10.2000"),
                    new SimpleDateFormat(DATE_PATTERN).parse("24.10.2000"),
                    new SimpleDateFormat(DATE_PATTERN).parse("02.06.2000"))));
        } catch (ParseException parseException) {
            return new ArrayList<>();
        }
    }

    @Test
    void saveOrder_Test_For_duplicate_request() throws ParseException {
        Date date = getDateForOrder(MOCK_DATE);
        int expectedId = 11;
        mockByTicket(date, expectedId);
        long id = orderService.saveOrder(1111, MOCK_DATE, 15, 1);
        assertEquals(expectedId, id);
    }

    @Test
    void saveOrder_Test_For_Wrong_Data_Format() {
        Date wrongDateFormat = new Date();

        assertThrows(ParseException.class, () ->
                orderService.saveOrder(11111, wrongDateFormat.toString(), 15, 1));
    }

    @Test
    void saveOrder_Test_For_Wrong_Route_Format() {
        assertThrows(ValidationException.class, () ->
                orderService.saveOrder(1111, MOCK_DATE, 15, 1));
    }

    @Test
    void saveOrder_Test_For_Wrong_RouteNumber_Format() throws ParseException {
        String stringDate = "22.06.2000";
        Date date = getDateForOrder(stringDate);
        int expectedId = 11;
        mockByTicket(date, expectedId);
        long id = orderService.saveOrder(11111, stringDate, 15, 1);
        assertEquals(expectedId, id);
    }

    private void mockByTicket(Date date, int expectedId) {
        when(mock.findByTicketId(1)).thenReturn(
                new Order().routNumber(10000).date(date).status(Status.PROCESSING).userId(-1).ticketId(1)
                        .id(expectedId));
    }

    @Nullable
    private Date getDateForOrder(String stringDate) {
        try {
            return new SimpleDateFormat(DATE_PATTERN).parse(stringDate);
        } catch (ParseException parseException) {
            log.error(parseException.toString());
            return null;
        }
    }

    @Test
    void getOrdersOnFuture_Test_right_Sorting() {
        var dates = getDates();
        List<Order> orders = createOrders(dates);

        Date mockedDate = dates.get(0);
        when(mock.findAllByUserIdAndDateIsAfter(1, mockedDate)).thenReturn(orders);

        dates = dates.stream().sorted()
                .filter(e -> e.after(mockedDate) || e == mockedDate).collect(Collectors.toList());

        var returnedOrdersDate = orderService.getOrdersOnFuture(1).stream()
                .map(Order::date).collect(Collectors.toList());

        assertEquals(dates, returnedOrdersDate);
    }

    private List<Order> createOrders(List<Date> dates) {
        return Arrays.asList(
                new Order().routNumber(11101).date(dates.get(0)).status(Status.PASS).userId(1).ticketId(1),
                new Order().routNumber(11320).date(dates.get(1)).status(Status.FAILED).userId(1).ticketId(2),
                new Order().routNumber(14011).date(dates.get(2)).status(Status.FAILED).userId(1).ticketId(3),
                new Order().routNumber(11505).date(dates.get(3)).status(Status.FAILED).userId(1).ticketId(4),
                new Order().routNumber(11505).date(dates.get(4)).status(Status.PASS).userId(1).ticketId(5),
                new Order().routNumber(11505).date(dates.get(5)).status(Status.PASS).userId(1).ticketId(3));
    }
}