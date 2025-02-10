package com.nsweeney.scrabble_generator;

import java.io.File;
import java.util.*;

public class OrderProcessor {

    Queue<String> orderIDQueue;
    Map<Order, Boolean> ordersProcessed;

    public OrderProcessor(){
        orderIDQueue = new LinkedList<>();
        ordersProcessed = new HashMap<Order, Boolean>();
        CheckNewOrders();
    }

    /**
     * Checks for new orders by comparing the current output directories with the contents of the JSON directory
     */
    private void CheckNewOrders(){
        File outputFolder = new File(Helper.rootTargetDirectory);
        File JSONFolder = new File(Helper.rootJSONDirectory);

        if (!JSONFolder.exists() || !JSONFolder.isDirectory()) {
            System.out.println("No JSON directory is found at " + Helper.rootJSONDirectory);
            return;
        }

        if (!outputFolder.exists() || !outputFolder.isDirectory()) {
            System.out.println("No target directory is found at " + Helper.rootTargetDirectory);
            return;
        }

        File[] completedOrders = outputFolder.listFiles(File::isDirectory);
        File[] orders = JSONFolder.listFiles();

        Set<String> completedOrderIDs = new HashSet<>();

        for (File completedOrder : completedOrders) {
            completedOrderIDs.add(completedOrder.getName());
        }

        if (orders != null) {
            for (File orderJSON : orders) {
                String orderID = orderJSON.getName().replace(".json","");
                if (!completedOrderIDs.contains(orderID) && !orderIDQueue.contains(orderID)) {
                    orderIDQueue.add(orderID);
                }
            }
        }

        if (!orderIDQueue.isEmpty()) {
            processOrders();
        }
    }

    /**
     * Process each order that is in the queue and generate a complete poster
     */
    private void processOrders(){
        while (!orderIDQueue.isEmpty()) {
            String currentOrderID = orderIDQueue.remove();
            Order currentOrder = Helper.ParseOrderJSON(Helper.rootJSONDirectory + currentOrderID + ".json");

            Board board = new Board(50, currentOrder.getWords());
            if (board.placeWords()){ // If words have been placed
                board.printBoard();
                board.export(Helper.GenerateOrderDirectory(currentOrder.getOrderID()));

                Helper.GeneratePoster(currentOrder, board.getScore());
            } else {
                // To Do: Error handling - Currently will get re-added to queue on the next scheduled event
            }
        }
    }
}