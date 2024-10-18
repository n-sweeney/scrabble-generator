package com.nsweeney.scrabble_generator;

import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        int size;

        String testDir = "JSON/test.json";
        Order testOrder = Helper.ParseOrderJSON(testDir);
        List<String> words = testOrder.words;

        size = Helper.CalculateSize(words);

        Board board = new Board(size, 50);
        board.placeWords(words);
        board.printBoard();
        board.export();
    }
}