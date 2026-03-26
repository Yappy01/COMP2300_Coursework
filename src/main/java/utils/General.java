package utils;

public class General {

    public static String formatLikes(int count) {
        if (count >= 1_000_000)
            return String.format("%.1fM", count / 1_000_000.0);
        if (count >= 1000)
            return String.format("%.1fk", count / 1000.0);
        return String.valueOf(count);
    }
}
