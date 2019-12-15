import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.math.IntMath;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Puzzle10b {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    List<String> lines = new ArrayList<>();
    while (sc.hasNextLine()) {
      lines.add(sc.nextLine());
    }
    boolean[][] asteroids = new boolean[lines.size()][];
    for (int i = 0; i < lines.size(); i++) {
      asteroids[i] = getRow(lines.get(i));
    }

    // determine best location
    int maxCount = 0;
    int bestX = 0;
    int bestY = 0;
    for (int y1 = 0; y1 < asteroids.length; y1++) {
      for (int x1 = 0; x1 < asteroids[y1].length; x1++) {
        if (!asteroids[y1][x1]) {
          continue;
        }

        int count = 0;
        for (int y2 = 0; y2 < asteroids.length; y2++) {
          for (int x2 = 0; x2 < asteroids[y2].length; x2++) {
            if (!asteroids[y2][x2]) {
              continue;
            }
            if (x1 == x2 && y1 == y2) {
              continue;
            }

            if (canSee(asteroids, x1, y1, x2, y2)) {
              count++;
            }
          }
        }

        if (count > maxCount) {
          maxCount = count;
          bestX = x1;
          bestY = y1;
        }
      }
    }

    // vaporize!
    Table<Integer, Integer, Double> angles = HashBasedTable.create();
    boolean finished = false;
    int cycle = 0;
    while (!finished) {
      finished = true;
      for (int y = 0; y < asteroids.length; y++) {
        for (int x = 0; x < asteroids.length; x++) {
          if (!asteroids[y][x]) {
            continue;
          }

          if (y == bestY && x == bestX) {
            continue;
          }

          if (!canSee(asteroids, bestX, bestY, x, y)) {
            finished = false;
            continue;
          }

          // atan2 takes (y, x) but we want to start at north and go clockwise
          // but also we've got our "y"s going down instead of up
          // I think this is right to transform into what we want? I hope so...
          double angle = Math.atan2(x - bestX, bestY - y);
          if (angle < 0) {
            angle += 2 * Math.PI;
          }

          angle += cycle * 2 * Math.PI;

          angles.put(y, x, angle);
        }
      }

      for (Table.Cell<Integer, Integer, Double> cell : angles.cellSet()) {
        asteroids[cell.getRowKey()][cell.getColumnKey()] = false;
      }
      cycle++;
    }

    List<Table.Cell<Integer, Integer, Double>> ordered = new ArrayList<>(angles.cellSet());
    ordered.sort(Comparator.comparing(Table.Cell::getValue));

    /*
    int index = 1;
    for (Table.Cell<Integer, Integer, Double> cell : ordered) {
      System.err.printf("%3d: (%d, %d)\n", index++, cell.getColumnKey(), cell.getRowKey());
    }
    */

    Table.Cell<Integer, Integer, Double> num200 = ordered.get(199);
    System.out.println(num200.getColumnKey() * 100 + num200.getRowKey());
  }

  private static boolean[] getRow(String line) {
    char[] chars = line.toCharArray();
    boolean[] row = new boolean[chars.length];

    for (int i = 0; i < chars.length; i++) {
      row[i] = chars[i] == '#';
    }
    return row;
  }

  private static boolean canSee(boolean[][] asteroids, int x1, int y1, int x2, int y2) {
    int xd = Math.abs(x2 - x1);
    int yd = Math.abs(y2 - y1);
    int gcd = IntMath.gcd(xd, yd);

    int xs = xd / gcd;
    int ys = yd / gcd;

    if (x2 < x1) {
      xs *= -1;
    }
    if (y2 < y1) {
      ys *= -1;
    }

    for (int i = 1; i < gcd; i++) {
      if (asteroids[y1 + ys * i][x1 + xs * i]) {
        return false;
      }
    }

    return true;
  }
}
