import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Stream;

public class Puzzle20b {
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
            .map(line -> line.chars().map(Puzzle20b::getValue).toArray())
            .toArray(int[][]::new);

    BiMap<String, Point> teleports = HashBiMap.create();

    for (int y = 0; y < map.length; y++) {
      for (int x = 0; x < map[y].length; x++) {
        if (isLetter(map[y][x])) {
          if (isLetter(get(map, y, x + 1))) {
            String tag = map[y][x] + "" + map[y][x + 1];
            if (get(map, y, x - 1) == '.') {
              if (x > map[0].length / 2) {
                teleports.put("O" + tag, Point.create(x - 1, y));
              } else {
                teleports.put("I" + tag, Point.create(x - 1, y));
              }
            } else if (get(map, y, x + 2) == '.') {
              if (x > map[0].length / 2) {
                teleports.put("I" + tag, Point.create(x + 2, y));
              } else {
                teleports.put("O" + tag, Point.create(x + 2, y));
              }
            } else {
              throw new IllegalStateException();
            }
          } else if (isLetter(get(map, y + 1, x))) {
            String tag = map[y][x] + "" + map[y + 1][x];
            if (get(map, y - 1, x) == '.') {
              if (y > map.length / 2) {
                teleports.put("O" + tag, Point.create(x, y - 1));
              } else {
                teleports.put("I" + tag, Point.create(x, y - 1));
              }
            } else if (get(map, y + 2, x) == '.') {
              if (y > map.length / 2) {
                teleports.put("I" + tag, Point.create(x, y + 2));
              } else {
                teleports.put("O" + tag, Point.create(x, y + 2));
              }
            } else {
              throw new IllegalStateException();
            }
          }
        }
      }
    }

    Table<String, String, Integer> distances = getDistances(canTravel, teleports);

    String start = "OAA0";
    Map<String, Integer> visited = new HashMap<>();
    Map<String, Integer> unvisited = new HashMap<>();
    unvisited.put(start, 0);

    while (!unvisited.isEmpty()) {
      String loc =
          unvisited.entrySet().stream()
              .min(Comparator.comparingInt(Map.Entry::getValue)).get().getKey();

      String baseLoc = loc.substring(0, 3);
      int level = Integer.parseInt(loc.substring(3));

      int baseDist = unvisited.get(loc);
      visited.put(loc, baseDist);
      unvisited.remove(loc);

      if (loc.equals("IZZ-1")) {
        break;
      }

      for (String newLoc : distances.columnKeySet()) {
        if (!distances.contains(baseLoc, newLoc)) {
          continue;
        }

        if (newLoc.charAt(0) == 'O') {
          if (level > 0) {
            if (newLoc.equals("OAA") || newLoc.equals("OZZ")) {
              continue;
            }
          } else {
            if (!newLoc.equals("OAA") && !newLoc.equals("OZZ")) {
              continue;
            }
          }
        }

        String fullLoc = otherSide(newLoc + level);

        if (visited.containsKey(fullLoc)) {
          continue;
        }

        int dist = baseDist + 1 + distances.get(baseLoc, newLoc);
        
        if (!unvisited.containsKey(fullLoc) || unvisited.get(fullLoc) > dist) {
          unvisited.put(fullLoc, dist);
        }
      }
    }

    System.out.println(visited.get("IZZ-1") - 1);
  }

  private static String otherSide(String loc) {
    char side = loc.charAt(0);
    String baseLoc = loc.substring(1, 3);
    int level = Integer.parseInt(loc.substring(3));
    if (side == 'O') {
      return "I" + baseLoc + (level - 1);
    } else {
      return "O" + baseLoc + (level + 1);
    }
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

  private static Table<String, String, Integer> getDistances(
      int[][] map, BiMap<String, Point> teleports) {
    BiMap<Point, String> teleportInverse = teleports.inverse();
    Table<String, String, Integer> distances = HashBasedTable.create();

    int[][] dist = new int[map.length][map[0].length];

    for (Map.Entry<String, Point> entry : teleports.entrySet()) {
      Point start = entry.getValue();
      int curX = start.getX();
      int curY = start.getY();

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

        if (!cur.equals(start) && teleportInverse.containsKey(cur)) {
          distances.put(entry.getKey(), teleportInverse.get(cur), dist[cy][cx]);
        }

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
      }
    }

    return distances;
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
