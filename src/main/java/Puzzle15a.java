import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Puzzle15a {
  public static void main(String[] args) {
    Intcode computer = Intcode.fromStdIn();
    BufferedIO io = new BufferedIO();
    DroidHelper dh = new DroidHelper(io);
    RunnableIntcode runnableComputer = new RunnableIntcode(computer, io);
    Thread thread = new Thread(runnableComputer);
    thread.start();

    Random rand = new Random();
    int lastDir = 0;
    boolean painting = false;
    while (true) {
      int revDir = (lastDir - 1) ^ 1 + 1;
      List<Integer> dirs = dh.explore();
      if (dirs.size() == 1) {
        painting = true;
        dh.paint();
        dh.move(dirs.get(0));
      } else {
        dirs.remove(Integer.valueOf(revDir));
        if (dirs.size() == 0) {
          break;
        }
        if (dirs.size() > 1) {
          painting = false;
        }
        if (painting) {
          dh.paint();
        }
        dh.move(dirs.get(rand.nextInt(dirs.size())));
      }
    }
    dh.print();

    Point oxy = dh.getOxygen();
    System.out.println(dh.getDistance(0, 0, oxy.getX(), oxy.getY()));
  }

  private static class DroidHelper {
    private BufferedIO io;
    private final Table<Integer, Integer, Integer> surface;
    private int curX, curY;

    public DroidHelper(BufferedIO io) {
      this.io = io;
      curX = 0;
      curY = 0;
      surface = HashBasedTable.create();
    }

    public int move(int dir) {
      int newX = curX;
      int newY = curY;

      switch (dir) {
        case 1:
          newY--;
          break;
        case 2:
          newY++;
          break;
        case 3:
          newX--;
          break;
        case 4:
          newX++;
          break;
      }

      int result = io.sendCommand(dir);
      surface.put(newX, newY, result);
      if (result > 0) {
        curX = newX;
        curY = newY;
      }
      return result;
    }

    public List<Integer> explore() {
      List<Integer> directions = new ArrayList<>();
      if (!surface.contains(curX, curY - 1)) {
        int result = move(1);
        if (result != 0) {
          move(2);
          directions.add(1);
        }
      } else if (available(curX, curY - 1)) {
        directions.add(1);
      }
      if (!surface.contains(curX, curY + 1)) {
        int result = move(2);
        if (result != 0) {
          move(1);
          directions.add(2);
        }
      } else if (available(curX, curY + 1)) {
        directions.add(2);
      }
      if (!surface.contains(curX - 1, curY)) {
        int result = move(3);
        if (result != 0) {
          move(4);
          directions.add(3);
        }
      } else if (available(curX - 1, curY)) {
        directions.add(3);
      }
      if (!surface.contains(curX + 1, curY)) {
        int result = move(4);
        if (result != 0) {
          move(3);
          directions.add(4);
        }
      } else if (available(curX + 1, curY)) {
        directions.add(4);
      }

      return directions;
    }

    public Point getOxygen() {
      Optional<Table.Cell<Integer, Integer, Integer>> point =
          surface.cellSet().stream()
              .filter(cell -> cell.getValue() == 2 || cell.getValue() == 4)
              .findAny();
      if (!point.isPresent()) {
        throw new IllegalStateException();
      }
      return Point.create(point.get().getRowKey(), point.get().getColumnKey());
    }

    private boolean available(int x, int y) {
      int value = surface.get(x, y);
      if (value == 1 || value == 2) {
        return true;
      }
      return false;
    }

    public int getDistance(int x1, int y1, int x2, int y2) {
      Table<Integer, Integer, Integer> distance = HashBasedTable.create();
      List<Point> queue = new ArrayList<>();

      distance.put(x1, y1, 0);
      queue.add(Point.create(x1, y1));
      while (!queue.isEmpty()) {
        Point p = queue.remove(0);
        int x = p.getX();
        int y = p.getY();
        int base = distance.get(x, y);
        if (surface.get(x - 1, y) > 0 && !distance.contains(x - 1, y)) {
          distance.put(x - 1, y, base + 1);
          queue.add(Point.create(x - 1, y));
        }
        if (surface.get(x + 1, y) > 0 && !distance.contains(x + 1, y)) {
          distance.put(x + 1, y, base + 1);
          queue.add(Point.create(x + 1, y));
        }
        if (surface.get(x, y - 1) > 0 && !distance.contains(x, y - 1)) {
          distance.put(x, y - 1, base + 1);
          queue.add(Point.create(x, y - 1));
        }
        if (surface.get(x, y + 1) > 0 && !distance.contains(x, y + 1)) {
          distance.put(x, y + 1, base + 1);
          queue.add(Point.create(x, y + 1));
        }
      }

      return distance.get(x2, y2);
    }

    public void paint() {
      if (surface.get(curX, curY) < 3) {
        surface.put(curX, curY, surface.get(curX, curY) + 2);
      }
    }

    public void print() {
      int minX = 0;
      int minY = 0;
      int maxX = 0;
      int maxY = 0;

      for (Table.Cell<Integer, Integer, Integer> cell : surface.cellSet()) {
        minX = Math.min(minX, cell.getRowKey());
        maxX = Math.max(maxX, cell.getRowKey());
        minY = Math.min(minY, cell.getColumnKey());
        maxY = Math.max(maxY, cell.getColumnKey());
      }

      for (int y = minY; y <= maxY; y++) {
        for (int x = minX; x <= maxX; x++) {
          if (x == 0 && y == 0) {
            System.out.print("<");
          } else if (surface.contains(x, y)) {
            switch (surface.get(x, y)) {
              case 0:
                System.err.print("#");
                break;
              case 1:
                System.err.print(".");
                break;
              case 2:
              case 4:
                System.err.print("O");
                break;
              case 3:
                System.err.print(",");
                break;
              default:
                System.err.print("%");
                break;
            }
          } else {
            System.err.print(" ");
          }
        }
        System.err.println();
      }
    }
  }
}
