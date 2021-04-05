package com.example.basket;

import com.example.basket.models.Item;
import com.example.basket.models.Order;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class OrdersLoader {

  public List<Order> getOrders(List<String> strings) {
    return strings.stream().map(this::mapToOrder)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }


  /**
   * Map the order into the order object Order line is expected to have this format: 81 :
   * (1,53.38,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€76) (5,30.18,€9) (6,46.34,€48) 81 is the max
   * weight of the package the rest are the items available for that order
   *
   * @param orderLine line
   * @return
   */
  private Order mapToOrder(String orderLine) {
    Order toReturn = new Order();
    if (orderLine.contains(":")) {
      String[] splitWeightAndOrders = orderLine.split(":");
      String weight = splitWeightAndOrders[0].trim();
      toReturn.setMaxPackageWeight(Float.parseFloat(weight));
      //get the items from the line
      String[] items = splitWeightAndOrders[1]
          .replaceAll("[()]", "")
          .trim()
          .split(" ");
      List<Item> orderItems = Arrays.stream(items)
          .map(this::getItemFromString)
          .collect(Collectors.toList());
      toReturn.setItems(orderItems);
      return toReturn;
    } else {
      return null;
    }
  }

  /**
   * Item line has this format: 2,88.62,€98 2 is the items place in the list 88.62 is the items
   * weight €98 is the items cost
   *
   * @param item
   * @return
   */
  private Item getItemFromString(String item) {
    //weight
    String[] split = item.split(",");
    Item itemToReturn = Item.builder().id(Integer.parseInt(split[0])).build();
    //weight
    Float weight = Float.valueOf(split[1]);
    itemToReturn.setWeight(weight);
    //cost
    float cost = Float.parseFloat(split[2].replace("€", ""));
    BigDecimal costBD = BigDecimal.valueOf(cost).setScale(2, RoundingMode.HALF_EVEN);
    itemToReturn.setCost(costBD);
    return itemToReturn;
  }
}
