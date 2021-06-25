package maze;

public record Coordinate(int x, int y) {
    public static Coordinate at(int x, int y) {
        return new Coordinate(x, y);
    }
}
