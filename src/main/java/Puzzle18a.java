import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Puzzle18a {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    List<String> inputs = new ArrayList<>();
    while (sc.hasNextLine()) {
      inputs.add(sc.nextLine());
    }

    int[][] map =
        inputs.stream()
            .map(line -> line.chars().map(Puzzle18a::getValue).toArray())
            .toArray(int[][]::new);

    Area a = new Area(map);
    List<Area> queue = new ArrayList<>();
    Area sol = null;
    queue.add(a);
    while (!queue.isEmpty()) {
      a = queue.remove(0);
      if (a.getKeys().isEmpty()) {
        if (sol == null || a.getDistance() < sol.getDistance()) {
          sol = a;
        }
      } else {
        queue.addAll(getChildren(a));
      }
    }

    System.out.println(sol.getDistance());
  }

  private static ImmutableList<Area> getChildren(final Area a) {
    return a.getKeys().stream()
        .map(key -> a.moveTo(key))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(ImmutableList.toImmutableList());
  }

  private static int getValue(int c) {
    if (c == '@') {
      return 0;
    } else if (c == '.') {
      return 1;
    } else if (c == '#') {
      return -1;
    } else if (c >= 'a' && c <= 'z') {
      return c - 'a' + 2;
    } else if (c >= 'A' && c <= 'Z') {
      return -(c - 'A' + 2);
    } else {
      throw new IllegalArgumentException("Unexpected input: " + c);
    }
  }

  public static class Area {
    private int[][] map;
    private int distance;
    private int curX, curY;
    private ImmutableList<Integer> keys;

    private Area(int[][] map, int distance, int curX, int curY, List<Integer> keys) {
      this.map = copyOf(map);
      this.distance = distance;
      this.curX = curX;
      this.curY = curY;
      this.keys = ImmutableList.copyOf(keys);
    }

    public Area(int[][] map) {
      this.map = copyOf(map);
      distance = 0;
      Point cur = find(map, 0);
      curX = cur.getX();
      curY = cur.getY();
      keys = findKeys(map);
    }

    public List<Integer> getKeys() {
      return keys;
    }

    public int getDistance() {
      return distance;
    }

    public Optional<Area> moveTo(int target) {
      Point dest = find(map, target);
      int dx = dest.getX();
      int dy = dest.getY();

      Point cur = Point.create(curX, curY);
      int[][] dist = new int[map.length][map[0].length];
      for (int y = 0; y < dist.length; y++) {
        Arrays.fill(dist[y], Integer.MAX_VALUE);
      }
      List<Point> queue = new ArrayList<>();
      dist[curY][curX] = 0;
      queue.add(cur);

      while (!queue.isEmpty()) {
        cur = queue.remove(0);
        int cx = cur.getX();
        int cy = cur.getY();

        if (check(map, dist, cx - 1, cy, target)) {
          dist[cy][cx - 1] = dist[cy][cx] + 1;
          if (cy == dy && cx - 1 == dx) {
            return Optional.of(craft(map, dist[cy][cx - 1], cx - 1, cy, target));
          }
          queue.add(Point.create(cx - 1, cy));
        }
        if (check(map, dist, cx + 1, cy, target)) {
          dist[cy][cx + 1] = dist[cy][cx] + 1;
          if (cy == dy && cx + 1 == dx) {
            return Optional.of(craft(map, dist[cy][cx + 1], cx + 1, cy, target));
          }
          queue.add(Point.create(cx + 1, cy));
        }
        if (check(map, dist, cx, cy - 1, target)) {
          dist[cy - 1][cx] = dist[cy][cx] + 1;
          if (cy - 1 == dy && cx == dx) {
            return Optional.of(craft(map, dist[cy - 1][cx], cx, cy - 1, target));
          }
          queue.add(Point.create(cx, cy - 1));
        }
        if (check(map, dist, cx, cy + 1, target)) {
          dist[cy + 1][cx] = dist[cy][cx] + 1;
          if (cy + 1 == dy && cx == dx) {
            return Optional.of(craft(map, dist[cy + 1][cx], cx, cy + 1, target));
          }
          queue.add(Point.create(cx, cy + 1));
        }
      }

      return Optional.empty();
    }

    private Area craft(int[][] map, int dist, int cx, int cy, int target) {
      int[][] newMap = copyOf(map);
      newMap[cy][cx] = 0;
      for (int y = 0; y < newMap.length; y++) {
        for (int x = 0; x < newMap[y].length; x++) {
          if (newMap[y][x] == -target) {
            newMap[y][x] = 1;
          }
        }
      }

      List<Integer> keys = new ArrayList<>(this.keys);
      keys.remove(Integer.valueOf(target));

      return new Area(newMap, this.distance + dist, cx, cy, keys);
    }
  }

  private static boolean check(int[][] map, int[][] dist, int tx, int ty, int target) {
    if (ty < 0 || ty >= map.length || tx < 0 || tx >= map[ty].length) {
      return false;
    }

    if (dist[ty][tx] < Integer.MAX_VALUE) {
      return false;
    }

    if (map[ty][tx] == 0 || map[ty][tx] == 1 || map[ty][tx] == target) {
      return true;
    }

    return false;
  }

  private static Point find(int[][] map, int target) {
    boolean found = false;
    for (int y = 0; y < map.length; y++) {
      for (int x = 0; x < map[y].length; x++) {
        if (map[y][x] == target) {
          return Point.create(x, y);
        }
      }
    }

    throw new IllegalStateException();
  }

  private static ImmutableList<Integer> findKeys(int[][] map) {
    ImmutableList.Builder<Integer> keys = ImmutableList.builder();

    for (int y = 0; y < map.length; y++) {
      for (int x = 0; x < map[y].length; x++) {
        if (map[y][x] > 1) {
          keys.add(map[y][x]);
        }
      }
    }

    return keys.build();
  }

  private static int[][] copyOf(int[][] array) {
    int[][] newArr = new int[array.length][];
    for (int i = 0; i < array.length; i++) {
      newArr[i] = Arrays.copyOf(array[i], array[i].length);
    }
    return newArr;
  }
}
