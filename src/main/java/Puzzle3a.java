import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Puzzle3a {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String wire1 = sc.nextLine();
    String wire2 = sc.nextLine();
    Point origin = Point.create(0, 0);

    List<WireLine> lines1 = new ArrayList<>();
    String[] pieces1 = wire1.split(",");
    Point current = origin;
    for (String piece : pieces1) {
      current = WireLine.addWireLine(current.getX(), current.getY(), piece, lines1);
    }

    List<WireLine> lines2 = new ArrayList<>();
    String[] pieces2 = wire2.split(",");
    current = origin;
    for (String piece : pieces2) {
      current = WireLine.addWireLine(current.getX(), current.getY(), piece, lines2);
    }

    List<Point> intersections = new ArrayList<>();
    for (WireLine wl1 : lines1) {
      for (WireLine wl2 : lines2) {
        intersections.addAll(wl1.getIntersections(wl2));
      }
    }

    int mindist = Integer.MAX_VALUE;
    for (Point p : intersections) {
      if (p.getX() == 0 && p.getY() == 0) {
        continue;
      }
      mindist = Math.min(dist(p, origin), mindist);
    }

    System.out.println(mindist);
  }

  private static int dist(Point a, Point b) {
    return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
  }

  private static class WireLine {
    private int x1, y1;
    private int x2, y2;

    private WireLine(int x1, int y1, int x2, int y2) {
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
    }

    public static Point addWireLine(int x1, int y1, String dir, List<WireLine> list) {
      int dist = Integer.parseInt(dir.substring(1));
      switch (dir.charAt(0)) {
        case 'L':
          list.add(new WireLine(x1 - dist, y1, x1, y1));
          return Point.create(x1 - dist, y1);
        case 'R':
          list.add(new WireLine(x1, y1, x1 + dist, y1));
          return Point.create(x1 + dist, y1);
        case 'U':
          list.add(new WireLine(x1, y1, x1, y1 + dist));
          return Point.create(x1, y1 + dist);
        case 'D':
          list.add(new WireLine(x1, y1 - dist, x1, y1));
          return Point.create(x1, y1 - dist);
        default:
          throw new IllegalArgumentException();
      }
    }

    public List<Point> getIntersections(WireLine other) {
      List<Point> list = new ArrayList<Point>();

      if (other.x2 < x1 || x2 < other.x1 || other.y2 < y1 || y2 < other.y1) {
        // no intersections
      } else if (other.x2 == other.x1) {
        // other is vertical
        if (y2 == y1) {
          // this is horizontal
          list.add(Point.create(other.x1, y1));
        } else if (x2 == x1) {
          // this is vertical
          int start = Math.max(y1, other.y1);
          int end = Math.min(y2, other.y2);
          for (int i = start; i <= end; i++) {
            list.add(Point.create(x1, i));
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
            list.add(Point.create(i, y1));
          }
        } else if (x2 == x1) {
          // this is vertical
          list.add(Point.create(x1, other.y1));
        } else {
          throw new IllegalStateException();
        }
      } else {
        throw new IllegalStateException();
      }

      return list;
    }
  }
}
