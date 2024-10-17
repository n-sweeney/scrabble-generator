import java.util.List;

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
}
