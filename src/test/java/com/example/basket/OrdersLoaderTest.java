package com.example.basket;

import com.example.basket.models.Order;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import(OrdersLoader.class)
public class OrdersLoaderTest {


  @Autowired
  public OrdersLoader ordersLoader;

  @Test
  void testReadingTextLinesToOrder() {
    List<String> ordersList = List.of(
        "```",
        "81 : (1,53.38,€45) (2,88.62,€98)",
        "8 : (1,53.38,€45) ",
        "```"
    );

    List<Order> orders = ordersLoader.getOrders(ordersList);
    Assertions.assertNotNull(orders);
    Assertions.assertEquals(2, orders.size());
    Assertions.assertEquals(81, (float) orders.get(0).getMaxPackageWeight());
    Assertions.assertEquals(8, (float) orders.get(1).getMaxPackageWeight());
    Assertions.assertNotNull(orders.get(0).getItems());
    Assertions
        .assertEquals(
            0, orders.get(0).getItems().get(0).getCost().compareTo(BigDecimal.valueOf(45)));
    Assertions
        .assertEquals(0,
            orders.get(0).getItems().get(1).getCost().compareTo(BigDecimal.valueOf(98)));
    Assertions
        .assertEquals(53.38f, orders.get(0).getItems().get(0).getWeight());
    Assertions
        .assertEquals(88.62f, orders.get(0).getItems().get(1).getWeight());
  }
}
