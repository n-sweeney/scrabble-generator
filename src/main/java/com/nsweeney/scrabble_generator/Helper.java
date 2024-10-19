package com.nsweeney.scrabble_generator;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import javax.imageio.ImageIO;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Helper {

    private Helper() {
    }

    private static final Dictionary<Character, Integer> letterScore = new Hashtable<>() {
        {
            put('A', 1);
            put('B', 3);
            put('C', 3);
            put('D', 2);
            put('E', 1);
            put('F', 4);
            put('G', 2);
            put('H', 4);
            put('I', 1);
            put('J', 8);
            put('K', 5);
            put('L', 1);
            put('M', 3);
            put('N', 1);
            put('O', 1);
            put('P', 3);
            put('Q', 10);
            put('R', 1);
            put('S', 1);
            put('T', 1);
            put('U', 1);
            put('V', 4);
            put('W', 4);
            put('X', 8);
            put('Y', 4);
            put('Z', 10);

        }
    };;

    public static int getLetterScore(char l) {
        return letterScore.get(l);
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
        } catch (IOException e) {
            order = new Order();
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

    public static void GeneratePoster(Order order, int score) {
        String orderDir = "Output/" + order.orderNumber + "/";

        try {
            InputStream is = Main.class.getResourceAsStream("/fonts/Pacifico-Regular.ttf");
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(200f);

            BufferedImage overallImage = ImageIO.read(Main.class.getResource("/letters/background.png"));
            int xCentre = overallImage.getWidth() / 2;
            int yCentre = overallImage.getHeight() / 2;

            BufferedImage img = ImageIO.read(new File(orderDir + "testImage.png"));

            Graphics2D g = overallImage.createGraphics();

            int targetWidth = (int) (overallImage.getWidth() * 0.75f); // Scale to 1/4th the width of the background
            int targetHeight = (int) ((double) targetWidth / img.getWidth() * img.getHeight()); // Preserve aspect ratio

            int xImagePosition = xCentre - targetWidth / 2;
            int yImagePosition = yCentre - targetHeight / 2;

            g.drawImage(img, xImagePosition, yImagePosition, targetWidth, targetHeight, null);

            g.setFont(customFont);
            g.setColor(Color.BLACK);

            g.drawString(order.topText, xCentre - g.getFontMetrics().stringWidth(order.topText) / 2, 750);

            g.setFont(new Font("Arial", Font.BOLD, 120));
            String wordScoreText = "Word Score: " + score;
            g.drawString(
                    wordScoreText,
                    xCentre - g.getFontMetrics().stringWidth(wordScoreText) / 2, overallImage.getHeight() - 750);

            g.dispose();

            ImageIO.write(overallImage, "png", new File(orderDir + "testImageComplete.png"));
            System.out.println("Image successfully created - overall");

        } catch (FontFormatException | IOException e) {
        }
    }

    public static String GenereateOrderDirectory(String order) {
        return "Output/" + order + "/";
    }
}
