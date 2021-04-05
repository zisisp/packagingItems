package com.example.basket;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.basket.models.Item;
import com.example.basket.models.NotCorrectArgumentsException;
import com.example.basket.models.Order;
import com.example.basket.models.Order.OrderBuilder;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import({PackageService.class, OrdersLoader.class})
class PackageServiceTest {

  @Autowired
  public OrdersLoader ordersLoader;

  @MockBean
  public FileLoader fileLoader;

  @Autowired
  public PackageService packageService;

  @Test
  void assertNotCorrectArgumentsExceptionIsThrown() {
    assertThrows(NotCorrectArgumentsException.class,
        () -> packageService.printPackageResults(null));
  }

  @Test
  void assertExceptionForMaxPackageWeight() throws NotCorrectArgumentsException, IOException {
    Item item = Item.builder().id(1).weight(1f).cost(
        BigDecimal.valueOf(1)).build();
    OrderBuilder order = Order.builder().maxPackageWeight(101f).items(List.of(
        item));
    assertExceptionIsThrowForOrder(order);
  }

  @Test
  void assertExceptionForMaxItemCount() throws NotCorrectArgumentsException, IOException {
    Item item = Item.builder().id(1).weight(1f).cost(
        BigDecimal.valueOf(1)).build();
    List<Item> items = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      items.add(item);
    }
    OrderBuilder order = Order.builder().maxPackageWeight(100f).items(items);
    assertExceptionIsThrowForOrder(order);
  }

  @Test
  void assertExceptionForItemThatExceedsCost()
      throws NotCorrectArgumentsException, IOException {
    Item item = Item.builder().id(1).weight(1f).cost(
        BigDecimal.valueOf(101)).build();
    OrderBuilder order = Order.builder().maxPackageWeight(100f).items(
        List.of(item));
    assertExceptionIsThrowForOrder(order);
  }

  @Test
  void assertExceptionForItemThatExceedsMaxWeight()
      throws NotCorrectArgumentsException, IOException {
    Item item = Item.builder().id(1).weight(101f).cost(
        BigDecimal.valueOf(100)).build();
    OrderBuilder order = Order.builder().maxPackageWeight(100f).items(
        List.of(item));
    assertExceptionIsThrowForOrder(order);
  }

  private void assertExceptionIsThrowForOrder(OrderBuilder order)
      throws IOException, NotCorrectArgumentsException {
    Mockito.when(fileLoader.loadOrdersFromFile(Mockito.any()))
        .thenReturn(List.of(
            order.build()));
    DefaultApplicationArguments args = new DefaultApplicationArguments("1");
    assertThrows(RuntimeException.class, () -> {
      packageService
          .printPackageResults(args);
    }, "123");
  }

  @Test
  void testCorrectResultForAllCases() throws IOException, NotCorrectArgumentsException {
    List<String> ordersList = List.of("```",
        "81 : (1,53.38,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€76) (5,30.18,€9) (6,46.34,€48)\n",
        "8 : (1,15.3,€34)\n",
        "75 : (1,85.31,€29) (2,14.55,€74) (3,3.98,€16) (4,26.24,€55) (5,63.69,€52) (6,76.25,€75) (7,60.02,€74) (8,93.18,€35) (9,89.95,€78)\n",
        "56 : (1,90.72,€13) (2,33.80,€40) (3,43.15,€10) (4,37.97,€16) (5,46.81,€36) (6,48.77,€79) (7,81.80,€45) (8,19.36,€79) (9,6.76,€64)\n",
        "```");
    String expected = "```\n"
        + "\n"
        + "4\n"
        + "\n"
        + "-\n"
        + "\n"
        + "2,7\n"
        + "\n"
        + "8,9\n"
        + "\n"
        + "```";
    testComparisonResults(ordersList, expected);
  }


  @Test
  void testCorrectResultForFirstCase() throws IOException, NotCorrectArgumentsException {
    List<String> ordersList = List.of("```",
        "81 : (1,53.38,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€76) (5,30.18,€9) (6,46.34,€48)\n",
        "```");
    String expected = "```\n"
        + "\n"
        + "4\n"
        + "\n"
        + "```";
    testComparisonResults(ordersList, expected);
  }


  @Test
  void testCorrectResultForSecondCase() throws IOException, NotCorrectArgumentsException {
    List<String> ordersList = List.of("```",
        "8 : (1,15.3,€34)\n",
        "```");
    String expected = "```\n"
        + "\n"
        + "-\n"
        +"\n"
        + "```";
    testComparisonResults(ordersList, expected);
  }


  @Test
  void testCorrectResultForThirdCase() throws IOException, NotCorrectArgumentsException {
    List<String> ordersList = List.of("```",
        "75 : (1,85.31,€29) (2,14.55,€74) (3,3.98,€16) (4,26.24,€55) (5,63.69,€52) (6,76.25,€75) (7,60.02,€74) (8,93.18,€35) (9,89.95,€78)\n",
        "```");
    String expected = "```\n"
        + "\n"
        + "2,7\n"
        + "\n"
        + "```";
    testComparisonResults(ordersList, expected);
  }


  @Test
  void testCorrectResultForForthCase() throws IOException, NotCorrectArgumentsException {
    List<String> ordersList = List.of("```",
        "56 : (1,90.72,€13) (2,33.80,€40) (3,43.15,€10) (4,37.97,€16) (5,46.81,€36) (6,48.77,€79) (7,81.80,€45) (8,19.36,€79) (9,6.76,€64)\n",
        "```");
    String expected = "```\n"
        + "\n"
        + "8,9\n"
        + "\n"
        + "```";
    testComparisonResults(ordersList, expected);
  }

  private void testComparisonResults(List<String> ordersList, String expected) {
    List<Order> orders = ordersLoader.getOrders(ordersList);
    String packageResults = packageService.getPackageResults(orders);
    System.out.println("packageResults = " + packageResults);
    Assertions.assertEquals(
        expected, packageResults);
  }
}