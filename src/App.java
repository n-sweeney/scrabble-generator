import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        int size = 20;
        List<String> words = new ArrayList<>();

        words.add("CAMEL");
        words.add("PANDA");
        words.add("COW");
        words.add("HORSE");
        words.add("HIPPO");
        words.add("LION");
        words.add("NATHAN");
        words.add("SWEENEY");

        Board board = new Board(size, 50);
        board.placeWords(words);
        board.printBoard();
    }
}
