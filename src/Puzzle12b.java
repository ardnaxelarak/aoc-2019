import com.google.common.math.LongMath;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Puzzle12b {
  private static final Pattern PATTERN =
      Pattern.compile("<x=(-?\\d+),\\s*y=(-?\\d+),\\s*z=(-?\\d+)>");

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);

    WorldSlice[] slices = new WorldSlice[3];
    slices[0] = new WorldSlice();
    slices[1] = new WorldSlice();
    slices[2] = new WorldSlice();
    while (sc.hasNextLine()) {
      String line = sc.nextLine();
      Matcher m = PATTERN.matcher(line);
      m.matches();
      for (int i = 0; i < 3; i++) {
        slices[i].add(new MoonSlice(Integer.parseInt(m.group(i + 1))));
      }
    }

    long lcm = 1;
    for (int dim = 0; dim < 3; dim++) {
      int steps = 0;
      WorldSlice initial = slices[dim];
      WorldSlice last = initial;
      WorldSlice next;
      while (true) {
        steps++;
        if (steps % 10000 == 0) {
          System.err.printf("Dimension %d: Checked %d states\n", dim, steps);
        }
        next = last.step();
        if (next.equals(initial)) {
          System.err.printf("Dimension %d: len = %d\n", dim, steps);
          lcm = lcm(lcm, steps);
          break;
        }
        last = next;
      }
    }

    System.out.println(lcm);
  }

  private static long lcm(long a, long b) {
    return a / LongMath.gcd(a, b) * b;
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

  private static class MoonSlice {
    private int pos;
    private int vel;

    private MoonSlice(int pos, int vel) {
      this.pos = pos;
      this.vel = vel;
    }

    public MoonSlice(int pos) {
      this(pos, 0);
    }

    public MoonSlice step(List<MoonSlice> others) {
      int newVel = vel;
      for (MoonSlice slice : others) {
        newVel += sign(slice.pos - pos);
      }

      return new MoonSlice(pos + newVel, newVel);
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof MoonSlice)) {
        return false;
      }

      MoonSlice ms = (MoonSlice) o;
      return ms.pos == pos && ms.vel == vel;
    }
  }

  private static class WorldSlice extends ArrayList<MoonSlice> {
    public WorldSlice() {
      super();
    }

    public WorldSlice step() {
      WorldSlice newWorld = new WorldSlice();

      for (MoonSlice slice : this) {
        newWorld.add(slice.step(this));
      }

      return newWorld;
    }
  }
}
