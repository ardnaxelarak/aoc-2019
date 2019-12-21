import com.ardnaxelarak.util.graph.Edge;
import com.ardnaxelarak.util.graph.EdgeProvider;
import com.ardnaxelarak.util.graph.Graphs;
import com.google.common.collect.ArrayListMultimap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

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

    EdgeProvider<Point> edges = new P20aEdgeProvider(map, points);
    Point start = teleports.get("AA").get(0);
    Point end = teleports.get("ZZ").get(0);
    System.out.println(Graphs.getDistance(edges, start, end));
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

  private static class P20aEdgeProvider implements EdgeProvider<Point> {
    private char[][] map;
    private Map<Point, Point> teleports;

    public P20aEdgeProvider(char[][] map, Map<Point, Point> teleports) {
      this.map = map;
      this.teleports = teleports;
    }

    public Iterable<Edge<Point>> getEdges(Point origin) {
      Set<Edge<Point>> edges = new HashSet<>();
      int x = origin.getX();
      int y = origin.getY();
      if (x > 0 && map[y][x - 1] == '.') {
        edges.add(Edge.create(Point.create(x - 1, y), 1));
      }
      if (y > 0 && map[y - 1][x] == '.') {
        edges.add(Edge.create(Point.create(x, y - 1), 1));
      }
      if (x < map[y].length - 1 && map[y][x + 1] == '.') {
        edges.add(Edge.create(Point.create(x + 1, y), 1));
      }
      if (y < map.length - 1 && map[y + 1][x] == '.') {
        edges.add(Edge.create(Point.create(x, y + 1), 1));
      }
      if (teleports.containsKey(origin)) {
        edges.add(Edge.create(teleports.get(origin), 1));
      }
      return edges;
    }
  }
}
