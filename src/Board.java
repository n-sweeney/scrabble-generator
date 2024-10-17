import java.util.ArrayList;
import java.util.List;

public class Board {
    private int gridSize;
    private char[][] grid;
    private List<String> placedWords = new ArrayList<>();

    public Board(int initialSize) {
        this.gridSize = initialSize;
        this.grid = new char[gridSize][gridSize];
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
                    words.remove(0);
                    words.add(currentWord);
                } else {
                    // Cant place error
                    break;
                }
            }
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
                if (i + ii - index < 0) {
                    continue;
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
                if (j + ii - index < 0) {
                    continue;
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
        printBoard();

    }

}
