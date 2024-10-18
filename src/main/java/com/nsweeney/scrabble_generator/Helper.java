package com.nsweeney.scrabble_generator;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Helper {
    private Helper() {
    }

    public static int CalculateSize(List<String> words) {

        int letterTotal = 0;
        for (String word : words) {
            letterTotal += word.length();
        }

        return (int) Math.ceil(Math.sqrt(letterTotal * 10));
    }

    public static Order ParseOrderJSON(String dir) {
        ObjectMapper mapper = new ObjectMapper();
        Order order;
        try {
            order = mapper.readValue(new File(dir), Order.class);
        } catch (Exception e) {
            order = new Order(null);
            System.err.println("Error Parsing JSON: " + e);
        }

        return order;
    }
}
