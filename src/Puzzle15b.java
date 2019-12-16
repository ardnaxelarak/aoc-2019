import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;

public class Puzzle15b {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String program = sc.nextLine();
    String[] pieces = program.split(",");
    long[] memory = Stream.of(pieces).mapToLong(Long::parseLong).toArray();

    RD15b io = new RD15b();
    DroidHelper dh = new DroidHelper(io);
    RunnableIntcode computer = new RunnableIntcode(memory, io);
    Thread thread = new Thread(computer);
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
    System.out.println(dh.getMaxDistance(oxy.getX(), oxy.getY()));
  }

  private static class Point {
    private int x, y;

    public Point(int x, int y) {
      this.x = x;
      this.y = y;
    }

    public int getX() {
      return x;
    }

    public int getY() {
      return y;
    }
  }

  private static class RD15b implements IntcodeIO {
    private final BlockingQueue<Integer> inputBuffer = new ArrayBlockingQueue<>(10);
    private final BlockingQueue<Integer> outputBuffer = new ArrayBlockingQueue<>(10);

    public RD15b() {}

    public long input() {
      synchronized (inputBuffer) {
        Integer next;
        while ((next = inputBuffer.poll()) == null) {
          try {
            inputBuffer.wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        return next;
      }
    }

    public void output(long value) {
      synchronized (outputBuffer) {
        outputBuffer.add((int) value);
        outputBuffer.notify();
      }
    }

    public int sendCommand(int value) {
      synchronized (inputBuffer) {
        inputBuffer.add(value);
        inputBuffer.notify();
      }

      synchronized (outputBuffer) {
        Integer next;
        while ((next = outputBuffer.poll()) == null) {
          try {
            outputBuffer.wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        return next;
      }
    }
  }

  private static class DroidHelper {
    private RD15b io;
    private final Table<Integer, Integer, Integer> surface;
    private int curX, curY;

    public DroidHelper(RD15b io) {
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
      List<Integer> directions = new ArrayList<Integer>();
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
      return new Point(point.get().getRowKey(), point.get().getColumnKey());
    }

    private boolean available(int x, int y) {
      int value = surface.get(x, y);
      if (value == 1 || value == 2) {
        return true;
      }
      return false;
    }

    public int getMaxDistance(int x1, int y1) {
      Table<Integer, Integer, Integer> distance = HashBasedTable.create();
      List<Point> queue = new ArrayList<>();

      distance.put(x1, y1, 0);
      queue.add(new Point(x1, y1));
      int base = 0;
      while (!queue.isEmpty()) {
        Point p = queue.remove(0);
        int x = p.getX();
        int y = p.getY();
        base = distance.get(x, y);
        if (surface.get(x - 1, y) > 0 && !distance.contains(x - 1, y)) {
          distance.put(x - 1, y, base + 1);
          queue.add(new Point(x - 1, y));
        }
        if (surface.get(x + 1, y) > 0 && !distance.contains(x + 1, y)) {
          distance.put(x + 1, y, base + 1);
          queue.add(new Point(x + 1, y));
        }
        if (surface.get(x, y - 1) > 0 && !distance.contains(x, y - 1)) {
          distance.put(x, y - 1, base + 1);
          queue.add(new Point(x, y - 1));
        }
        if (surface.get(x, y + 1) > 0 && !distance.contains(x, y + 1)) {
          distance.put(x, y + 1, base + 1);
          queue.add(new Point(x, y + 1));
        }
      }

      return base;
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
