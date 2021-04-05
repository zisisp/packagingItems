package com.example.basket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.basket.models.Order;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import({OrdersLoader.class,FileLoader.class})
public class FileLoaderTest {

  @Autowired
  private FileLoader fileLoader;

  /**
   * Testing the following text:
   * "81 : (1,53.38,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€76) (5,30.18,€9) (6,46.34,€48)\n",
   *  "8 : (1,15.3,€34)\n",
   *  "75 : (1,85.31,€29) (2,14.55,€74) (3,3.98,€16) (4,26.24,€55) (5,63.69,€52) (6,76.25,€75) (7,60.02,€74) (8,93.18,€35) (9,89.95,€78)\n",
   *  "56 : (1,90.72,€13) (2,33.80,€40) (3,43.15,€10) (4,37.97,€16) (5,46.81,€36) (6,48.77,€79) (7,81.80,€45) (8,19.36,€79) (9,6.76,€64)\n",
   * @throws IOException
   */
  @Test
  public void testFileLoader() throws IOException {
    List<Order> orders = fileLoader.loadOrdersFromFile("src/test/resources/test.txt");
    assertEquals( 4,orders.size(),orders.size());

    assertEquals( 81f,orders.get(0).getMaxPackageWeight());
    assertEquals( 6,orders.get(0).getItems().size());
    assertEquals( 0,orders.get(0).getItems().get(0).getCost().compareTo(BigDecimal.valueOf(45)));
    assertEquals( 53.38f,orders.get(0).getItems().get(0).getWeight());
    assertEquals( 56f,orders.get(3).getMaxPackageWeight());
    assertEquals(9,orders.get(3).getItems().size());
    assertEquals( 0,orders.get(3).getItems().get(5).getCost().compareTo(BigDecimal.valueOf(79)));
    assertEquals(48.77f,orders.get(3).getItems().get(5).getWeight());
  }


}
