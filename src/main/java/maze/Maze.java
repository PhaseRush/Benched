package maze;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
Cellular automata based maze generation
 */
public class Maze {

    public enum Tile {
        WALL("#"),
        HUNTER("@"),
        GUARDIAN("!"),
        RELIC("^"),
        SPACE(" "),
        UNEXPLORED(".");

        protected String symbol;

        Tile(String symbol) {
            this.symbol = symbol;
        }

        public boolean alwaysShow() {
            return this.equals(HUNTER) ||
                    this.equals(GUARDIAN) ||
                    this.equals(RELIC);
        }

        @Override
        public String toString() {
            return this.symbol;
        }
    }

//    public static final int WIDTH = 21;
//    public static final int HEIGHT = 17;

    public int WIDTH;
    public int HEIGHT;
    private static final ThreadLocalRandom rand = ThreadLocalRandom.current();
//    private static final Random rand = new Random(1); // seed for debugging

    private final Tile[][] maze;
    private Tile[][] prev = null;
    private final List<BufferedImage> frames = new ArrayList<>();

    // todo: tweak these numbers
    private double hamming = WIDTH + HEIGHT;
    private double numTiles = WIDTH * HEIGHT;

    @SafeVarargs
    private static <T> T choice(T... choices) {
        return choices[rand.nextInt(choices.length)];
    }

    private static int randrange(int low, int high, int step) {
        return rand.nextInt(low, high / step) * step;
    }

    private static int __randrange(int low, int high, int step) {
        int r = rand.nextInt(high - low + 1);
        return r - r % step + low;
    }


    public Set<Coordinate> neighbours(Coordinate coord, Tile target) {
        final var frontier = new HashSet<Coordinate>();
        if (coord.y() > 1 && maze[coord.y() - 2][coord.x()].equals(target)) {
            frontier.add(new Coordinate(coord.x(), coord.y() - 2));
        }
        if (coord.y() < HEIGHT - 2 && maze[coord.y() + 2][coord.x()].equals(target)) {
            frontier.add(new Coordinate(coord.x(), coord.y() + 2));
        }
        if (coord.x() > 1 && maze[coord.y()][coord.x() - 2].equals(target)) {
            frontier.add(new Coordinate(coord.x() - 2, coord.y()));
        }
        if (coord.x() < WIDTH - 2 && maze[coord.y()][coord.x() + 2].equals(target)) {
            frontier.add(new Coordinate(coord.x() + 2, coord.y()));
        }
        return frontier;
    }

    public Coordinate randomNeighbour(Coordinate coord, Tile target) {
        return choice(neighbours(coord, target).toArray(Coordinate[]::new));
    }

    private void generate() {
        // fill everything with space
        for (int i = 0; i < HEIGHT; i++) {
            Arrays.fill(maze[i], Tile.SPACE);
        }

        // fill borders
        // fill top and bottom
        for (int i = 0; i < WIDTH; i++) {
            maze[0][i] = Tile.WALL;
            maze[HEIGHT - 1][i] = Tile.WALL;
        }
        // fill left and right
        for (int i = 0; i < HEIGHT; i++) {
            maze[i][0] = Tile.WALL;
            maze[i][WIDTH - 1] = Tile.WALL;
        }

        int x, y;
        for (int i = 0; i < 2 * numTiles; i++) {
            if (i < numTiles) {
                if (rand.nextBoolean()) {
                    x = choice(0, WIDTH - 1);
                    y = randrange(0, HEIGHT - 1, 2);
                } else {
                    x = randrange(0, WIDTH - 1, 2);
                    y = choice(0, HEIGHT - 1);
                }
            } else {
                x = randrange(0, WIDTH - 1, 2);
                y = randrange(0, HEIGHT - 1, 2);
            }

            maze[y][x] = Tile.WALL;
            for (int j = 0; j < hamming; j++) {
                var adjacentWalls = neighbours(new Coordinate(x, y), Tile.WALL);
                // game rule B3/S1234:
                if (adjacentWalls.size() > 0 && adjacentWalls.size() < 4) {
                    var adjacentSpaces = neighbours(new Coordinate(x, y), Tile.SPACE);
                    if (adjacentSpaces.isEmpty()) {
                        continue;
                    }
                    var adjacentSpace = choice(adjacentSpaces.toArray(Coordinate[]::new));
                    var adjY = adjacentSpace.y();
                    var adjX = adjacentSpace.x();
                    if (maze[adjY][adjX].equals(Tile.SPACE)) {
                        maze[adjY][adjX] = Tile.WALL;
                        maze[adjY + (y - adjY) / 2][adjX + (x - adjX) / 2] = Tile.WALL;
                        x = adjX;
                        y = adjY;
                    }
                }
            }
        }
    }

    public Maze(int width, int height) {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.maze = new Tile[HEIGHT][WIDTH];
        generate();
    }

    public Tile[][] getMaze() {
        return maze;
    }

    private BufferedImage mazeToImage(Tile[][] maze) {
        StringBuilder sb = new StringBuilder();
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                sb.append(maze[i][j]).append(" ");
            }
            lines.add(sb.toString().replaceAll("#", "■"));
            sb = new StringBuilder();
        }

        BufferedImage img = new BufferedImage(1500, 1500, BufferedImage.TYPE_INT_ARGB);
        Font font = new Font("Courier New", Font.BOLD, 50);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);

        g2d.setColor(Color.WHITE);
        int currY = g2d.getFontMetrics().getHeight();
        for (int i = 0; i < lines.size(); i++) {
            g2d.drawString(lines.get(i), 0, currY);
            currY += g2d.getFontMetrics().getAscent() * 2;
//            System.out.println(g2d.getFontMetrics().getHeight());
        }
//        g2d.drawString(text, 0, fm.getAscent());
        g2d.dispose();
        return img;
    }
}