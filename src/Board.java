import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board {
    private int gridSize;
    private char[][] grid;
    private final List<String> placedWords = new ArrayList<>();
    private final int retryCount;

    public Board(int initialSize, int retryCount) {
        this.gridSize = initialSize;
        this.grid = new char[gridSize][gridSize];
        this.retryCount = retryCount;
        initialiseBoard();
    }

    final void initialiseBoard() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                grid[i][j] = ' ';
            }
        }
    }

    void resetBoard(String word) {
        initialiseBoard();
        placeFirstWord(word);
        placedWords.clear();
        placedWords.add(word);
    }

    private void placeFirstWord(String word) {
        int startX = gridSize / 2;
        int startY = (gridSize / 2) - (word.length() / 2);
        for (int i = 0; i < word.length(); i++) {
            grid[startX][startY + i] = word.charAt(i);
        }
        printBoard();
    }

    void printBoard() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.print('\n');

        }
    }

    void placeWords(List<String> words) {
        boolean complete = false;
        int retries = 0;
        int previousSize = Integer.MAX_VALUE;
        while (!complete && retries < retryCount) {

            Collections.shuffle(words);
            resetBoard(words.get(0));
            words.remove(0);
            while (!words.isEmpty()) {

                boolean placed = false;
                String currentWord = words.get(0);
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
                                words.remove(0);
                                break;
                            }
                        }

                    }
                    if (placed) {
                        break;
                    }
                }

                if (!placed) {
                    if (words.size() > 1) {
                        if (previousSize == words.size()) {
                            retries++;
                            resizeGrid();
                            break;

                        }

                        words.remove(0);
                        words.add(currentWord);
                        previousSize = words.size();
                    } else {
                        retries++;
                        break;
                    }
                }
            }
            trimGrid();
            complete = true;
        }

    }

    boolean checkValid(int i, int j, String word) {
        int index = checkVertical(i, j, word);
        if (index != -1) {
            // Place Word vertically
            placeWord(i - index, j, word, "ver");
            return true;
        }

        index = checkHorizontal(i, j, word);
        if (index != -1) {
            // Place Word horizontally
            placeWord(i, j - index, word, "hor");
            return true;
        }

        return false;
    }

    int checkVertical(int i, int j, String word) {
        List<Integer> indexes = getAllIndexes(word, grid[i][j]);

        for (int index : indexes) {
            boolean found = true;
            for (int ii = 0; ii < word.length(); ii++) {
                if (i + ii - index <= 0 || i + ii - index >= gridSize) {
                    found = false;
                    break;
                }
                char currentCell = grid[i + ii - index][j];
                if (currentCell != ' ' && currentCell != word.charAt(ii)) {
                    found = false;
                    break;
                }
                if (ii - index == 0) {
                    continue;
                }
                if (!checkNeighbours(i + ii - index, j, i, j)) {
                    found = false;
                    break;
                }
            }
            if (found) {
                if (!(i - index <= 0 || i - index + word.length() >= gridSize)) {
                    if (grid[i - index - 1][j] != ' ' || grid[i - index + word.length()][j] != ' ') {
                        break;
                    }
                }
                return index;
            }
        }

        return -1;
    }

    int checkHorizontal(int i, int j, String word) {
        List<Integer> indexes = getAllIndexes(word, grid[i][j]);

        for (int index : indexes) {
            boolean found = true;
            for (int ii = 0; ii < word.length(); ii++) {
                if (j + ii - index <= 0 || j + ii - index >= gridSize) {
                    found = false;
                    break;
                }

                char currentCell = grid[i][j + ii - index];
                if (currentCell != ' ' && currentCell != word.charAt(ii)) {
                    found = false;
                    break;
                }
                if (ii - index == 0) {
                    continue;
                }
                if (!checkNeighbours(i, j + ii - index, i, j)) {
                    found = false;
                    break;
                }
            }
            if (found) {
                // Check if string is valid
                if (!(j - index <= 0 || j - index + word.length() >= gridSize)) {
                    if (grid[i][j - index - 1] != ' ' || grid[i][j - index + word.length()] != ' ') {
                        break;
                    }
                }

                return index;
            }

        }

        return -1;
    }

    List<Integer> getAllIndexes(String str, char target) {
        List<Integer> indexes = new ArrayList<>();
        int index = str.indexOf(target);

        while (index >= 0) {
            indexes.add(index);
            index = str.indexOf(target, index + 1);
        }

        return indexes;
    }

    boolean checkNeighbours(int i, int j, int intersectI, int intesectJ) {

        int[] iOffset = { -1, 1, 0, 0 };
        int[] jOffset = { 0, 0, -1, 1 };

        for (int ii = 0; ii < 4; ii++) {
            int checkI = iOffset[ii] + i;
            int checkJ = jOffset[ii] + j;

            if (checkI == intersectI || checkJ == intesectJ) {
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

    void placeWord(int i, int j, String word, String dir) {
        if (dir.equalsIgnoreCase("hor")) {

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

    void export() {

    }
}
