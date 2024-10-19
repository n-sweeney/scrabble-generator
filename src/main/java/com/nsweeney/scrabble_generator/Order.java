package com.nsweeney.scrabble_generator;

import java.util.ArrayList;
import java.util.List;

public class Order {
    public List<String> words;
    public String topText;
    public String orderNumber;

    public Order(List<String> words, String topText, String orderNumber) {
        this.words = words;
        this.topText = topText;
        this.orderNumber = orderNumber;
    }

    public Order() {
        this.words = new ArrayList<>();
        this.topText = "";
        this.orderNumber = "";
    }
}
