import com.ardnaxelarak.util.graph.Edge;
import com.ardnaxelarak.util.graph.EdgeProvider;
import com.ardnaxelarak.util.graph.Graphs;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

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

    BiMap<String, Point> connections = HashBiMap.create();

    for (int y = 0; y < map.length; y++) {
      for (int x = 0; x < map[y].length; x++) {
        if (isLetter(map[y][x])) {
          if (isLetter(get(map, y, x + 1))) {
            String tag = map[y][x] + "" + map[y][x + 1];
            if (get(map, y, x - 1) == '.') {
              if (x > map[0].length / 2) {
                connections.put("O" + tag, Point.create(x - 1, y));
              } else {
                connections.put("I" + tag, Point.create(x - 1, y));
              }
            } else if (get(map, y, x + 2) == '.') {
              if (x > map[0].length / 2) {
                connections.put("I" + tag, Point.create(x + 2, y));
              } else {
                connections.put("O" + tag, Point.create(x + 2, y));
              }
            } else {
              throw new IllegalStateException();
            }
          } else if (isLetter(get(map, y + 1, x))) {
            String tag = map[y][x] + "" + map[y + 1][x];
            if (get(map, y - 1, x) == '.') {
              if (y > map.length / 2) {
                connections.put("O" + tag, Point.create(x, y - 1));
              } else {
                connections.put("I" + tag, Point.create(x, y - 1));
              }
            } else if (get(map, y + 2, x) == '.') {
              if (y > map.length / 2) {
                connections.put("I" + tag, Point.create(x, y + 2));
              } else {
                connections.put("O" + tag, Point.create(x, y + 2));
              }
            } else {
              throw new IllegalStateException();
            }
          }
        }
      }
    }

    EdgeProvider<Point> edges = new P20bEdgeProvider(map);
    Table<Point, Point, Long> pointDistances =
        Graphs.getAllDistances(edges, connections.values());

    BiMap<Point, String> labels = connections.inverse();
    Table<String, String, Long> distances =
        pointDistances.cellSet().stream()
            .filter(cell -> labels.containsKey(cell.getRowKey()))
            .filter(cell -> labels.containsKey(cell.getColumnKey()))
            .collect(
                ImmutableTable.toImmutableTable(
                    cell -> labels.get(cell.getRowKey()),
                    cell -> labels.get(cell.getColumnKey()),
                    cell -> cell.getValue()));

    EdgeProvider<String> levelEdges = new P20bLevelEdgeProvider(distances);

    System.out.println(Graphs.getDistance(levelEdges, "OAA0", "OZZ0"));
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

  private static class P20bEdgeProvider implements EdgeProvider<Point> {
    private char[][] map;

    public P20bEdgeProvider(char[][] map) {
      this.map = map;
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
      return edges;
    }
  }

  private static class P20bLevelEdgeProvider implements EdgeProvider<String> {
    private ImmutableSetMultimap<String, Edge<String>> distances;

    public P20bLevelEdgeProvider(Table<String, String, Long> distances) {
      ImmutableSetMultimap.Builder<String, Edge<String>> builder =
          ImmutableSetMultimap.builder();
      Set<Table.Cell<String, String, Long>> cells = distances.cellSet();
      for (Table.Cell<String, String, Long> cell : cells) {
        builder.put(cell.getRowKey(), Edge.create(cell.getColumnKey(), cell.getValue()));
      }
      this.distances = builder.build();
    }

    public Iterable<Edge<String>> getEdges(String origin) {
      Set<Edge<String>> edges = new HashSet<>();
      String baseLoc = origin.substring(0, 3);
      int level = Integer.parseInt(origin.substring(3));
      for (Edge<String> edge : distances.get(baseLoc)) {
        String newLoc = edge.getValue();
        if (newLoc.charAt(0) == 'O') {
          if (level == 0) {
            if (!newLoc.equals("OAA") && !newLoc.equals("OZZ")) {
              continue;
            }
          } else {
            if (newLoc.equals("OAA") || newLoc.equals("OZZ")) {
              continue;
            }
          }
        }
        edges.add(Edge.create(edge.getValue() + level, edge.getCost()));
      }
      edges.add(Edge.create(otherSide(origin), 1));
      return edges;
    }
  }
}
