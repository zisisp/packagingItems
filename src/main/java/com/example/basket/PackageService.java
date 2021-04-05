package com.example.basket;

import com.example.basket.models.Item;
import com.example.basket.models.NotCorrectArgumentsException;
import com.example.basket.models.Order;
import com.example.basket.models.OrderExcepetion;
import com.example.basket.models.SelectedItemPackage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

@Service
public class PackageService {

  private static final BigDecimal MAX_ITEM_COST = BigDecimal.valueOf(100);
  public static final int MAX_PACKAGE_WEIGHT = 100;
  public static final int MAX_ITEM_SIZE = 15;
  private static final Float MAX_ITEM_WEIGHT = 100f;
  public static final String NO_COMBINATION_FOUND = "-";
  public static final SelectedItemPackage EMPTY_SELECTED_ITEM = new SelectedItemPackage(
      BigDecimal.ZERO,
      new ArrayList<>(), 0f);
  private final FileLoader fileLoader;

  public PackageService(FileLoader fileLoader) {
    this.fileLoader = fileLoader;
  }

  /**
   * First arg is the file path, second is the boolean 1 or 0 if file in the same path or is abs
   *
   * @param args
   */
  public void printPackageResults(ApplicationArguments args)
      throws NotCorrectArgumentsException, IOException {
    List<Order> order = loadOrdersFromFile(args);
    String packageResults = getPackageResults(order);
    System.out.println("packageResults = " + packageResults);
  }


  String getPackageResults(List<Order> orders) {
    List<String> results = new ArrayList<>();
    for (Order order : orders) {
      results.add(processedOrder(order));
    }
    StringBuilder sb = new StringBuilder().append("```\n\n");
    results.forEach(x -> sb.append(x).append("\n\n"));
    return sb.append("```").toString();
  }

  private String processedOrder(Order order) {
    checkConstraintsInOrdersListAndThrowExceptions(order);

    return findOrderResult(order);
  }

  private String findOrderResult(Order order) {
    if (order.getItems().isEmpty()) {
      return
          NO_COMBINATION_FOUND;
    } else {
      //remove item that are not within the constraints
      List<Item> filteredItems = order.getItems().stream()
          .filter(x -> x.getWeight() <= MAX_ITEM_WEIGHT)
          .filter(x -> x.getCost().compareTo(MAX_ITEM_COST) <= 0)
          .filter(x -> order.getMaxPackageWeight() > x.getWeight())
          .sorted(Comparator.comparing(Item::getCost))
          .collect(Collectors.toList());
      if (filteredItems.isEmpty()) {
        return NO_COMBINATION_FOUND;
      } else {
        return calculateTheOptimalItemGroup(order);
      }
    }
  }

  private String calculateTheOptimalItemGroup(Order order) {
    int maxPackageWeight = order.getMaxPackageWeight().intValue();
    int itemsSize = order.getItems().size();
    SelectedItemPackage[][] matrix = new SelectedItemPackage[maxPackageWeight + 1][itemsSize + 1];
    //fill first row and column with zero
    for (int i = 0; i <= itemsSize; i++) {
      matrix[0][i] = EMPTY_SELECTED_ITEM;
    }
    for (int i = 0; i <= maxPackageWeight; i++) {
      matrix[i][0] = EMPTY_SELECTED_ITEM;
    }
    for (int itemNumber = 1; itemNumber <= itemsSize; itemNumber++) {
      updateTheMatrix(order, maxPackageWeight, matrix, itemNumber);
    }
    SelectedItemPackage selected = matrix[maxPackageWeight][itemsSize];
    printMatrix(matrix, maxPackageWeight, itemsSize);
    return selected == null ? "-" : selected.getItems().stream()
        .map(Object::toString).collect(Collectors.joining(","));
  }

  private void updateTheMatrix(Order order, int maxPackageWeight, SelectedItemPackage[][] matrix,
      int itemNumber) {
    Item itemCheck = order.getItems().get(itemNumber - 1);//list starts at 0
    //items that do not meet the constraints are skipped and we copy the value from the previous item
    if (itemCheck.isSkipped()) {
      copyPreviousValuesToThisItem(matrix, itemNumber, maxPackageWeight);
    } else {
      for (int weight = 1; weight <= maxPackageWeight; weight++) {
        SelectedItemPackage previousValue = matrix[weight][itemNumber - 1];
        SelectedItemPackage currentValue = getValueMaxPriceForThisItem(itemCheck, matrix, weight);
        boolean currentValueIsHigher;
        if (currentValue.getTotalPrice().compareTo(previousValue.getTotalPrice()) == 0
            && previousValue.getTotalPrice().intValue() > 0) {
          //if price is the same select the one with the lowest weight
          currentValueIsHigher = currentValue.getTotalWeight() < previousValue.getTotalWeight();
        } else if (previousValue != EMPTY_SELECTED_ITEM) {
          currentValueIsHigher =
              currentValue.getTotalPrice().compareTo(previousValue.getTotalPrice()) > 0;
        } else {
          currentValueIsHigher = true;
        }
        matrix[weight][itemNumber] = currentValueIsHigher ? currentValue : previousValue;
      }
    }
  }

  private void printMatrix(SelectedItemPackage[][] matrix, int maxPackageWeight, int itemNumber) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i <= maxPackageWeight; i++) {
      for (int j = 0; j <= itemNumber; j++) {
        sb.append(matrix[i][j].getTotalPrice()).append("\t");
      }
      sb.append("\n");
    }
//    System.out.println("matrix = \n" + sb.toString());
  }

  private void copyPreviousValuesToThisItem(SelectedItemPackage[][] matrix, int itemNumber,
      int maxPackageWeight) {
    for (int i = 0; i < maxPackageWeight; i++) {
      matrix[i][itemNumber] = matrix[i][itemNumber - 1];
    }
  }

  /**
   * Return the max price that can be held with this weight and this item
   *
   * @param item   the item that we are checking right now
   * @param matrix the matrix that we hold all the item values
   * @param weight the current max weight that are calculating right now
   * @return The selected max price that can be achieved for this weight up to this item id
   */
  private SelectedItemPackage getValueMaxPriceForThisItem(Item item, SelectedItemPackage[][] matrix,
      int weight) {
    if (weight < item.getWeight()) {
      return EMPTY_SELECTED_ITEM;
    } else {
      int i = Math.round(weight - item.getWeight());
      SelectedItemPackage previousSelectedItemPackage = matrix
          [i][item.getId() - 1];
      BigDecimal totalCost = previousSelectedItemPackage.getTotalPrice().add(item.getCost());
      Float totalWeight = previousSelectedItemPackage.getTotalWeight() + item.getWeight();
      List<Integer> newList = new ArrayList<>(previousSelectedItemPackage.getItems());
      newList.add(item.getId());
      return new SelectedItemPackage(totalCost, newList, totalWeight);
    }
  }

  private void checkConstraintsInOrdersListAndThrowExceptions(Order order) {
    if (order.getMaxPackageWeight() > MAX_PACKAGE_WEIGHT) {
      throw new OrderExcepetion("The maximum weight that a package must hold must be <=100");
    }
    if (order.getItems() != null && order.getItems().size() > MAX_ITEM_SIZE) {
      throw new OrderExcepetion("There may be up to 15 items in the order");
    }
    if (order.getItems() != null) {
      order.getItems()
          .forEach(x -> {
            boolean costViolation = x.getCost().compareTo(MAX_ITEM_COST) > 0;
            boolean weightViolation = x.getWeight() > MAX_ITEM_WEIGHT;
            if (costViolation) {
              x.setSkipped(true);
              throw new OrderExcepetion(
                  "Max cost of item " + x + " in order " + order + " is more than "
                      + MAX_ITEM_COST);
            }
            if (weightViolation) {
              x.setSkipped(true);
              throw new OrderExcepetion(
                  "Max weight of item " + x + " in order " + order + " is more than "
                      + MAX_ITEM_WEIGHT);
            }
          });
    }
  }

  private List<Order> loadOrdersFromFile(ApplicationArguments args)
      throws NotCorrectArgumentsException, IOException {
    if (args == null || args.getSourceArgs().length != 1) {
      String argsValues = args == null ? null : Arrays.toString(args.getSourceArgs());
      throw new NotCorrectArgumentsException("The expected command line arguments are 1. "
          + "\n That is a string with the file path of the packages list\n"
          + "The arguments that were received are:" + argsValues);

    }
    return fileLoader.loadOrdersFromFile(args.getSourceArgs()[0]);
  }

}
