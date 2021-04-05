package com.example.basket;

import java.util.List;

public class Test {

  public static void main(String[] args) {
    List<Integer> integers = List.of(1, 2, 3, 4, 5);
    StringBuilder stringBuilder=new StringBuilder();
    integers.forEach(x -> stringBuilder.append(x+"\n\n"));
    System.out.println("stringBuilder.toString() = " + stringBuilder.toString());
  }

}
