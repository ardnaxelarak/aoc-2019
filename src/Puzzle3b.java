import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Puzzle3b {
  public static void main(String[] args) {
    Scanner sc = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
    String wire1 = sc.nextLine();
    String wire2 = sc.nextLine();

    Wire w1 = new Wire(wire1);
    Wire w2 = new Wire(wire2);

    List<Point> intersections = w1.getIntersections(w2);
    int minCost = Integer.MAX_VALUE;
    for (Point p : intersections) {
      if (p.getX() == 0 && p.getY() == 0 && p.getCost() == 0) {
        continue;
      }
      minCost = Math.min(p.getCost(), minCost);
    }

    System.out.println(minCost);
  }

  private static class Point {
    private int x, y;
    private int cost;

    public Point(int x, int y, int cost) {
      this.x = x;
      this.y = y;
      this.cost = cost;
    }

    public int getX() {
      return x;
    }

    public int getY() {
      return y;
    }

    public int getCost() {
      return cost;
    }
  }

  private static class Wire {
    private List<WireLine> lines;
    private Point curLocation;

    public Wire(String path) {
      lines = new ArrayList<>();
      curLocation = new Point(0, 0, 0);

      String[] pieces = path.split(",");
      for (String piece : pieces) {
        addWireLine(piece);
      }
    }

    private void addWireLine(String dir) {
      int x1 = curLocation.getX();
      int y1 = curLocation.getY();
      int baseCost = curLocation.getCost();
      int dist = Integer.parseInt(dir.substring(1));
      switch (dir.charAt(0)) {
        case 'L':
          lines.add(new WireLine(x1 - dist, y1, x1, y1, baseCost, true));
          curLocation = new Point(x1 - dist, y1, baseCost + dist);
          break;
        case 'R':
          lines.add(new WireLine(x1, y1, x1 + dist, y1, baseCost, false));
          curLocation = new Point(x1 + dist, y1, baseCost + dist);
          break;
        case 'U':
          lines.add(new WireLine(x1, y1, x1, y1 + dist, baseCost, false));
          curLocation = new Point(x1, y1 + dist, baseCost + dist);
          break;
        case 'D':
          lines.add(new WireLine(x1, y1 - dist, x1, y1, baseCost, true));
          curLocation = new Point(x1, y1 - dist, baseCost + dist);
          break;
        default:
          throw new IllegalArgumentException("Unrecognized direction: " + dir.charAt(0));
      }
    }

    public List<Point> getIntersections(Wire other) {
      List<Point> intersections = new ArrayList<>();
      for (WireLine wl1 : lines) {
        for (WireLine wl2 : other.lines) {
          intersections.addAll(wl1.getIntersections(wl2));
        }
      }
      return intersections;
    }
  }

  private static class WireLine {
    private int x1, y1;
    private int x2, y2;
    private boolean flipped;
    private int baseCost;

    private WireLine(int x1, int y1, int x2, int y2, int baseCost, boolean flipped) {
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
      this.baseCost = baseCost;
      this.flipped = flipped;
    }

    public List<Point> getIntersections(WireLine other) {
      List<Point> list = new ArrayList<Point>();

      if (other.x2 < x1 || x2 < other.x1 || other.y2 < y1 || y2 < other.y1) {
        // no intersections
      } else if (other.x2 == other.x1) {
        // other is vertical
        if (y2 == y1) {
          // this is horizontal
          int cost = getCost(other.x1, y1) + other.getCost(other.x1, y1);
          list.add(new Point(other.x1, y1, cost));
        } else if (x2 == x1) {
          // this is vertical
          int start = Math.max(y1, other.y1);
          int end = Math.min(y2, other.y2);
          for (int i = start; i <= end; i++) {
            int cost = getCost(x1, i) + other.getCost(x1, i);
            list.add(new Point(x1, i, cost));
          }
        } else {
          throw new IllegalStateException();
        }
      } else if (other.y2 == other.y1) {
        // other is horizontal
        if (y2 == y1) {
          // this is horizontal
          int start = Math.max(x1, other.x1);
          int end = Math.min(x2, other.x2);
          for (int i = start; i <= end; i++) {
            int cost = getCost(i, y1) + other.getCost(i, y1);
            list.add(new Point(i, y1, cost));
          }
        } else if (x2 == x1) {
          // this is vertical
          int cost = getCost(x1, other.y1) + other.getCost(x1, other.y1);
          list.add(new Point(x1, other.y1, cost));
        } else {
          throw new IllegalStateException();
        }
      } else {
        throw new IllegalStateException();
      }

      return list;
    }

    private int getCost(int x, int y) {
      if (x1 == x2 && x == x1) {
        if (flipped) {
          return baseCost + y2 - y;
        } else {
          return baseCost + y - y1;
        }
      } else if (y1 == y2 && y == y1) {
        if (flipped) {
          return baseCost + x2 - x;
        } else {
          return baseCost + x - x1;
        }
      } else {
        throw new IllegalArgumentException();
      }
    }
  }
}
