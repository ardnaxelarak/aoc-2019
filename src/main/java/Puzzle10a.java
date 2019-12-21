import com.google.common.math.IntMath;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Puzzle10a {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    List<String> lines = new ArrayList<>();
    while (sc.hasNextLine()) {
      lines.add(sc.nextLine());
    }
    boolean[][] asteroids = lines.stream().map(Puzzle10a::getRow).toArray(boolean[][]::new);

    int maxCount = 0;
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

        maxCount = Math.max(count, maxCount);
      }
    }

    System.out.println(maxCount);
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
