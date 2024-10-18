package com.nsweeney.scrabble_generator;

import java.util.ArrayList;
import java.util.List;

public class Order {
    public List<String> words;

    public Order(List<String> words) {
        this.words = words;
    }

    public Order() {
        this.words = new ArrayList<String>();
    }
}
