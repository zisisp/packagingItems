package com.example.basket;

import com.example.basket.models.Order;
import java.io.File;
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
    String absolutePath = new File(".")
        .getAbsolutePath();
    String substring = absolutePath.substring(0, absolutePath.length() - 1);//remove trailing dot
    Path path = Paths.get(substring + sourceArg);
    List<String> strings = Files.readAllLines(path);
    return ordersLoader.getOrders(strings);
  }

}
