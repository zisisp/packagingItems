package com.example.basket.models;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectedItemPackage {

  BigDecimal totalPrice;
  List<Integer> items;
  Float totalWeight;
}
