import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Puzzle12a {
  private static final Pattern PATTERN =
      Pattern.compile("<x=(-?\\d+),\\s*y=(-?\\d+),\\s*z=(-?\\d+)>");

  public static void main(String[] args) {
    if (args.length < 1) {
      throw new IllegalArgumentException("Need to provide number of steps");
    }

    Scanner sc = new Scanner(System.in);

    List<Moon> moons = new ArrayList<>();
    while (sc.hasNextLine()) {
      String line = sc.nextLine();
      Matcher m = PATTERN.matcher(line);
      m.matches();
      moons.add(
          new Moon(
              Integer.parseInt(m.group(1)),
              Integer.parseInt(m.group(2)),
              Integer.parseInt(m.group(3))));
    }

    int steps = Integer.parseInt(args[0]);
    for (int step = 0; step < steps; step++) {
      for (int i = 0; i < moons.size(); i++) {
        for (int j = 0; j < moons.size(); j++) {
          if (i == j) {
            continue;
          }

          moons.get(i).pullToward(moons.get(j));
        }
      }

      for (int i = 0; i < moons.size(); i++) {
        moons.get(i).applyVelocity();
      }
    }

    System.out.println(moons.stream().mapToInt(Moon::getEnergy).sum());
  }

  private static int sign(int diff) {
    if (diff > 0) {
      return 1;
    } else if (diff < 0) {
      return -1;
    } else {
      return 0;
    }
  }

  private static final class Moon {
    private int x, y, z;
    private int xv, yv, zv;

    public Moon(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;

      xv = 0;
      yv = 0;
      zv = 0;
    }

    public void pullToward(Moon other) {
      xv += sign(other.x - x);
      yv += sign(other.y - y);
      zv += sign(other.z - z);
    }

    public void applyVelocity() {
      x += xv;
      y += yv;
      z += zv;
    }

    public int getEnergy() {
      return (Math.abs(x) + Math.abs(y) + Math.abs(z))
          * (Math.abs(xv) + Math.abs(yv) + Math.abs(zv));
    }

    public String toString() {
      return String.format(
          "pos=<x=%d, y=%d, z=%d>, vel=<x=%d, y=%d, z=%d>",
          x, y, z, xv, yv, zv);
    }
  }
}
