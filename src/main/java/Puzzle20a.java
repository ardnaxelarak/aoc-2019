import com.google.common.collect.ArrayListMultimap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Stream;

public class Puzzle20a {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    List<String> lines = new ArrayList<>();
    while (sc.hasNextLine()) {
      lines.add(sc.nextLine());
    }

    char[][] map =
        lines.stream()
            .map(String::toCharArray)
            .toArray(char[][]::new);

    int[][] canTravel =
        lines.stream()
            .map(line -> line.chars().map(Puzzle20a::getValue).toArray())
            .toArray(int[][]::new);

    ArrayListMultimap<String, Point> teleports = ArrayListMultimap.create();

    for (int y = 0; y < map.length; y++) {
      for (int x = 0; x < map[y].length; x++) {
        if (isLetter(map[y][x])) {
          if (isLetter(get(map, y, x + 1))) {
            String tag = map[y][x] + "" + map[y][x + 1];
            if (get(map, y, x - 1) == '.') {
              teleports.put(tag, Point.create(x - 1, y));
            } else if (get(map, y, x + 2) == '.') {
              teleports.put(tag, Point.create(x + 2, y));
            } else {
              throw new IllegalStateException();
            }
          } else if (isLetter(get(map, y + 1, x))) {
            String tag = map[y][x] + "" + map[y + 1][x];
            if (get(map, y - 1, x) == '.') {
              teleports.put(tag, Point.create(x, y - 1));
            } else if (get(map, y + 2, x) == '.') {
              teleports.put(tag, Point.create(x, y + 2));
            } else {
              throw new IllegalStateException();
            }
          }
        }
      }
    }

    Map<Point, Point> points = new HashMap<>();

    for (String tag : teleports.keySet()) {
      List<Point> locs = teleports.get(tag);
      if (locs.size() == 2) {
        points.put(locs.get(0), locs.get(1));
        points.put(locs.get(1), locs.get(0));
      } else if (locs.size() > 2) {
        throw new IllegalStateException();
      }
    }

    Point start = teleports.get("AA").get(0);
    Point end = teleports.get("ZZ").get(0);
    System.out.println(getDistance(canTravel, points, start, end));
  }

  private static char get(char[][] map, int y, int x) {
    if (y >= map.length || y < 0) {
      return ' ';
    }

    if (x >= map[y].length || x < 0) {
      return ' ';
    }

    return map[y][x];
  }

  private static boolean isLetter(char c) {
    return c >= 'A' && c <= 'Z';
  }

  private static int getValue(int c) {
    if (c == '.') {
      return 1;
    } else {
      return 0;
    }
  }

  private static int getDistance(
      int[][] map, Map<Point, Point> teleports, Point start, Point end) {
    int curX = start.getX();
    int curY = start.getY();

    int[][] dist = new int[map.length][map[0].length];
    for (int y = 0; y < dist.length; y++) {
      Arrays.fill(dist[y], Integer.MAX_VALUE);
    }

    List<Point> queue = new ArrayList<>();
    dist[curY][curX] = 0;
    queue.add(start);

    Point cur;

    while (!queue.isEmpty()) {
      cur = queue.remove(0);
      int cx = cur.getX();
      int cy = cur.getY();

      if (check(map, dist, cx - 1, cy)) {
        int curDist = dist[cy][cx] + 1;
        dist[cy][cx - 1] = curDist;
        queue.add(Point.create(cx - 1, cy));
      }
      if (check(map, dist, cx + 1, cy)) {
        int curDist = dist[cy][cx] + 1;
        dist[cy][cx + 1] = curDist;
        queue.add(Point.create(cx + 1, cy));
      }
      if (check(map, dist, cx, cy - 1)) {
        int curDist = dist[cy][cx] + 1;
        dist[cy - 1][cx] = curDist;
        queue.add(Point.create(cx, cy - 1));
      }
      if (check(map, dist, cx, cy + 1)) {
        int curDist = dist[cy][cx] + 1;
        dist[cy + 1][cx] = curDist;
        queue.add(Point.create(cx, cy + 1));
      }
      if (teleports.containsKey(cur)) {
        Point tp = teleports.get(cur);
        int tx = tp.getX();
        int ty = tp.getY();
        if (dist[ty][tx] == Integer.MAX_VALUE) {
          dist[ty][tx] = dist[cy][cx] + 1;
          queue.add(Point.create(tx, ty));
        }
      }
    }

    return dist[end.getY()][end.getX()];
  }

  private static boolean check(int[][] map, int[][] dist, int tx, int ty) {
    if (ty < 0 || ty >= map.length || tx < 0 || tx >= map[ty].length) {
      return false;
    }

    if (dist[ty][tx] < Integer.MAX_VALUE) {
      return false;
    }

    return map[ty][tx] > 0;
  }
}
