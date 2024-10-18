package com.nsweeney.scrabble_generator;

import java.awt.image.BufferedImage;
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

    public static BufferedImage CropImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int top = 0;
        int left = 0;
        int right = width - 1;
        int bottom = height - 1;

        topLoop: for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (image.getRGB(x, y) != 0xFF000000) {
                    top = y;
                    break topLoop;
                }
            }
        }

        bottomLoop: for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                if (image.getRGB(x, y) != 0xFF000000) {
                    bottom = y;
                    break bottomLoop;
                }
            }
        }

        leftLoop: for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (image.getRGB(x, y) != 0xFF000000) {
                    left = x;
                    break leftLoop;
                }
            }
        }

        rightLoop: for (int x = width - 1; x >= 0; x--) {
            for (int y = 0; y < height; y++) {
                if (image.getRGB(x, y) != 0xFF000000) {
                    right = x;
                    break rightLoop;
                }
            }
        }

        return image.getSubimage(left, top, right - left + 1, bottom - top + 1);
    }
}
