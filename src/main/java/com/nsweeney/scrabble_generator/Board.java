package com.nsweeney.scrabble_generator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

public class Board {
    private int gridSize;
    private char[][] grid;
    private final List<String> placedWords = new ArrayList<>();
    private List<String> words = new ArrayList<>();
    private final int retryCount;
    private int score;

    /**
     * Direction constants defined as enumerations
     */
    public enum Direction {
        horizontal,
        vertical;
    }

    /**
     * Board class constructor that generates a new board object and populates the tile contents
     *
     * @param retryCount Times to retry generating before assumed invalid
     * @param words list of words to display on the board
     */
    public Board(int retryCount, List<String> words) {
        this.gridSize = Helper.CalculateSize(words);
        this.grid = new char[gridSize][gridSize];
        this.retryCount = retryCount;
        this.words = words;

        initialiseBoard();
    }

    /**
     * Initialises the board's array with spaces
     */
    final void initialiseBoard() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                grid[i][j] = ' ';
            }
        }
    }


    /**
     * Resets board if board failed to generate and needs to be retried
     *
     * @param word random first word to place
     */
    private void resetBoard(String word) {
        initialiseBoard();
        placeFirstWord(word);
        placedWords.clear();
        placedWords.add(word);
    }

    /**
     * Places the provided word in the middle of the board
     *
     * @param word randomly chosen first word
     */
    private void placeFirstWord(String word) {
        int startX = gridSize / 2;
        int startY = (gridSize / 2) - (word.length() / 2);
        for (int i = 0; i < word.length(); i++) {
            grid[startX][startY + i] = word.charAt(i);
        }
        printBoard();
    }

    /**
     * Prints current board to terminal for debugging
     */
    void printBoard() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.print('\n');

        }
    }

    /**
     * Main control loop for placing words on the board. The words are shuffled each iteration to reduce incorrect invalid results and develop differing outputs.
     *
     */
    public boolean placeWords() {
        boolean complete = false;
        int retries = 0;
        int previousSize = Integer.MAX_VALUE;

        while (!complete && retries < retryCount) {

            List<String> currentWords = new ArrayList<>(words);
            Collections.shuffle(currentWords);

            resetBoard(currentWords.get(0));
            currentWords.remove(0);

            while (!currentWords.isEmpty()) {

                boolean placed = false;
                String currentWord = currentWords.get(0);

                for (int i = 0; i < gridSize; i++) {
                    for (int j = 0; j < gridSize; j++) {
                        char letter = grid[i][j];

                        if (letter == ' ') {
                            continue;
                        }
                        // If match
                        if (currentWord.contains(String.valueOf(letter))) {
                            // Find Potential
                            if (checkValid(i, j, currentWord)) {
                                // Placed so break and go to next word
                                placed = true;
                                currentWords.remove(0);
                                break;
                            }
                        }

                    }
                    if (placed) {
                        break;
                    }
                }

                if (!placed) {
                    if (currentWords.size() > 1) {
                        if (previousSize == currentWords.size()) {
                            retries++;
                            resizeGrid();
                            break;
                        }

                        currentWords.remove(0);
                        currentWords.add(currentWord);
                        previousSize = currentWords.size();
                    } else {
                        retries++;
                        break;
                    }
                }
            }

            if (currentWords.isEmpty()) {
                trimGrid();
                complete = true;
                score = calculateScore();
            }
        }

        return complete;
    }

    /**
     * Checks if word can be validly placed at coordinates (i, j). Validity is checked by the production of any words not present in the order's words
     *
     * @param i Row to check
     * @param j Column to check
     * @param word Word to place
     * @return True if placed; otherwise False
     */
    boolean checkValid(int i, int j, String word) {
        int index = checkVertical(i, j, word);
        if (index != -1) {
            // Place Word vertically
            placeWord(i - index, j, word, Direction.vertical);
            return true;
        }

        index = checkHorizontal(i, j, word);
        if (index != -1) {
            // Place Word horizontally
            placeWord(i, j - index, word, Direction.horizontal);
            return true;
        }

        return false;
    }

    /**
     * Checks if the word is vertically valid with tile (i, j) by getting checking the occurrences of the overlapping letter. It is valid if it doesn't create any invalid words (words not present in the order)
     *
     * @param i Intercept row to check
     * @param j Intercept column to check
     * @param word Word being checked
     * @return Word index offset
     */
    int checkVertical(int i, int j, String word) {
        List<Integer> indexes = getAllIndexes(word, grid[i][j]);

        for (int overlapIndex : indexes) {
            boolean found = true;

            for (int letterIndex = 0; letterIndex < word.length(); letterIndex++) {
                if (i + letterIndex - overlapIndex <= 0 || i + letterIndex - overlapIndex >= gridSize) {
                    found = false;
                    break;
                }

                char currentCell = grid[i + letterIndex - overlapIndex][j];

                if (currentCell != ' ' && currentCell != word.charAt(letterIndex)) {
                    found = false;
                    break;
                }

                if (letterIndex - overlapIndex == 0) {
                    continue;
                }

                if (!checkNeighbours(i + letterIndex - overlapIndex, j, i, j)) {
                    found = false;
                    break;
                }
            }

            if (found) {
                if (!(i - overlapIndex <= 0 || i - overlapIndex + word.length() >= gridSize)) {
                    if (grid[i - overlapIndex - 1][j] != ' ' || grid[i - overlapIndex + word.length()][j] != ' ') {
                        break;
                    }
                }

                return overlapIndex;
            }
        }

        return -1;
    }


    /**
     * Checks if the word is horizontally valid with tile (i, j) by getting checking the occurrences of the overlapping letter. It is valid if it doesn't create any invalid words (words not present in the order)
     *
     * @param i Row to check
     * @param j Column to check
     * @param word Word being checked
     * @return Word index offset
     */
    int checkHorizontal(int i, int j, String word) {
        List<Integer> indexes = getAllIndexes(word, grid[i][j]);

        for (int overlapIndex : indexes) {
            boolean found = true;
            for (int letterIndex = 0; letterIndex < word.length(); letterIndex++) {
                if (j + letterIndex - overlapIndex <= 0 || j + letterIndex - overlapIndex >= gridSize) {
                    found = false;
                    break;
                }

                char currentCell = grid[i][j + letterIndex - overlapIndex];
                if (currentCell != ' ' && currentCell != word.charAt(letterIndex)) {
                    found = false;
                    break;
                }
                if (letterIndex - overlapIndex == 0) {
                    continue;
                }
                if (!checkNeighbours(i, j + letterIndex - overlapIndex, i, j)) {
                    found = false;
                    break;
                }
            }
            if (found) {
                // Check if string is valid
                if (!(j - overlapIndex <= 0 || j - overlapIndex + word.length() >= gridSize)) {
                    if (grid[i][j - overlapIndex - 1] != ' ' || grid[i][j - overlapIndex + word.length()] != ' ') {
                        break;
                    }
                }

                return overlapIndex;
            }

        }

        return -1;
    }

    /**
     * Finds and returns all indexes for the target letter in the provided string
     *
     * @param str string to check
     * @param target present letter
     * @return List of indexes for target in str
     */
    List<Integer> getAllIndexes(String str, char target) {
        List<Integer> indexes = new ArrayList<>();
        int index = str.indexOf(target);

        while (index >= 0) {
            indexes.add(index);
            index = str.indexOf(target, index + 1);
        }

        return indexes;
    }

    /**
     * Checks the corresponding neighbour tiles for (i, j) to determine if any external collisions occur. E.g. word does not collide with board but extends a word into an invalid word.
     *
     * @param i current tile's row
     * @param j current tile's column
     * @param intersectI Board intersection point row
     * @param intersectJ Board intersection point column
     * @return if no collision true; otherwise false
     */
    boolean checkNeighbours(int i, int j, int intersectI, int intersectJ) {

        // Neighbour coordinates
        int[] iOffset = { -1, 1, 0, 0 };
        int[] jOffset = { 0, 0, -1, 1 };

        for (int neighbour = 0; neighbour < iOffset.length; neighbour++) {
            int checkI = iOffset[neighbour] + i;
            int checkJ = jOffset[neighbour] + j;

            // Checked in validity
            if (checkI == intersectI || checkJ == intersectJ) {
                continue;
            }

            if (checkI >= 0 && checkI < gridSize && checkJ >= 0 && checkJ < gridSize) {
                char neighborCell = grid[checkI][checkJ];

                if (neighborCell != ' ') {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Places a valid word starting at (i, j) in the provided direction
     *
     * @param i Starting row
     * @param j Starting column
     * @param word Order word
     * @param direction Word direction ("horizontal"/"vertical")
     */
    private void placeWord(int i, int j, String word, Direction direction) {
        if (direction.equals(Direction.horizontal)) {

            for (int jj = 0; jj < word.length(); jj++) {
                grid[i][j + jj] = word.charAt(jj);
            }
        } else {
            for (int ii = 0; ii < word.length(); ii++) {
                grid[i + ii][j] = word.charAt(ii);
            }
        }

        placedWords.add(word);
    }

    /**
     * Clones the board and resizes it to 2n*2n
     */
    void resizeGrid() {
        int newGridSize = gridSize * 2;
        int padding = gridSize / 2;

        char[][] newGrid = new char[newGridSize][newGridSize];

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                newGrid[padding + i][padding + j] = grid[i][j];
            }
        }

        grid = newGrid;
        gridSize = newGridSize;
    }

    /**
     * Removes any empty rows or columns to create a new square board
     */
    private void trimGrid() {
        int minX = gridSize;
        int maxX = -1;
        int minY = gridSize;
        int maxY = -1;

        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                if (grid[x][y] != ' ') {
                    if (x < minX)
                        minX = x;
                    if (x > maxX)
                        maxX = x;
                    if (y < minY)
                        minY = y;
                    if (y > maxY)
                        maxY = y;
                }
            }
        }

        int trimmedWidth = maxY - minY + 1;
        int trimmedHeight = maxX - minX + 1;

        int squareSize = Math.max(trimmedWidth, trimmedHeight);
        char[][] trimmedGrid = new char[squareSize][squareSize];

        for (int i = 0; i < squareSize; i++) {
            for (int j = 0; j < squareSize; j++) {
                trimmedGrid[i][j] = ' ';
            }
        }

        int offsetI = (squareSize - trimmedHeight) / 2;
        int offsetJ = (squareSize - trimmedWidth) / 2;

        for (int i = 0; i < trimmedHeight; i++) {
            for (int j = 0; j < trimmedWidth; j++) {
                trimmedGrid[offsetI + i][offsetJ + j] = grid[minX + i][minY + j];
            }
        }

        grid = trimmedGrid;
        gridSize = squareSize;
    }

    /**
     * Exports the current board to a png image that is stored in the provided directory
     *
     * @param dir Directory path
     */
    void export(String dir) {
        try {
            BufferedImage firstImage = ImageIO.read(Main.class.getResource("/letters/A.png"));
            int imageWidth = firstImage.getWidth();

            BufferedImage finalImage = new BufferedImage(gridSize
                    * imageWidth,
                    gridSize * imageWidth,
                    BufferedImage.TYPE_INT_ARGB);

            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    char currentLetter = grid[i][j];

                    if (currentLetter != ' ') {
                        String imageName = grid[i][j] + ".png";
                        BufferedImage img = ImageIO.read(Main.class.getResource("/letters/" + imageName));
                        finalImage.getGraphics().drawImage(img, i * imageWidth, j * imageWidth, null);
                    }
                }
            }

            Files.createDirectories(Paths.get(dir));
            ImageIO.write(Helper.CropImage(finalImage), "png", new File(dir + "boardImage.png"));
            System.out.println("image successful");

        } catch (IOException e) {
            //To Do: Error Handling
        }
    }

    /**
     * Calculates the board's score based on each word's score
     *
     * @return Current board score
     */
    private int calculateScore() {
        int currentScore = 0;
        for (String word : words) {
            for (char letter : word.toCharArray()) {
                currentScore += Helper.getLetterScore(letter);
            }
        }

        return currentScore;
    }

    /**
     * Gets and returns the board's score
     *
     * @return board's score
     */
    public int getScore() {
        return score;
    }
}
