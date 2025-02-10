package com.nsweeney.scrabble_generator;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private List<String> words;
    private String topText;
    private String orderID;
    private boolean completed;

    /**
     * Order Constructor that creates an order object
     * @param words List of words to display
     * @param topText Text to display at the top
     * @param orderID Customer's order number
     */
    public Order(List<String> words, String topText, String orderID, boolean completed) {
        this.words = words;
        this.topText = topText;
        this.orderID = orderID;
        this.completed = completed;
    }

    /**
     * Default constructor used when loading from JSON
     */
    public Order() {
        this.words = new ArrayList<>();
        this.topText = "";
        this.orderID = "";
        this.completed = false;
    }

    // Getters and Setters
    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public String getTopText() {
        return topText;
    }

    public void setTopText(String topText) {
        this.topText = topText;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
