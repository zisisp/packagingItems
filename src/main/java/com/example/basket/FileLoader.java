package com.example.basket;

import com.example.basket.models.Order;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FileLoader {

  private final OrdersLoader ordersLoader;

  public FileLoader(OrdersLoader ordersLoader) {
    this.ordersLoader = ordersLoader;
  }

  public List<Order> loadOrdersFromFile(String sourceArg) throws IOException {
    Path path = Paths.get(sourceArg);
    List<String> strings = Files.readAllLines(path);
    return ordersLoader.getOrders(strings);
  }

}
