package com.example.basket.models;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {
  private Integer id;
  private Float weight;
  private BigDecimal cost;
  private boolean skipped;
}
