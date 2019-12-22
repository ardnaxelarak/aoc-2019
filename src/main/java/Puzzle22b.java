import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;

public class Puzzle22b {
  private static final long CARDS = 119315717514047L;
  private static final BigInteger BIG_CARDS = BigInteger.valueOf(CARDS);
  private static final long REPS = 101741582076661L;

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    List<String> lines = new ArrayList<>();
    while (sc.hasNextLine()) {
      lines.add(sc.nextLine());
    }
    Collections.reverse(lines);

    Vrbl v = new Vrbl(BigInteger.ONE, BigInteger.ZERO);

    for (String line : lines) {
      if (line.startsWith("deal with increment")) {
        BigInteger n = BigInteger.valueOf(Integer.parseInt(line.substring(20)));
        BigInteger inverse = n.modInverse(BIG_CARDS);
        v = v.multiply(inverse);
      } else if (line.startsWith("deal into new stack")) {
        v = v.add(BigInteger.ONE).multiply(BigInteger.valueOf(-1));
      } else if (line.startsWith("cut")) {
        int n = Integer.parseInt(line.substring(4));
        v = v.add(BigInteger.valueOf(n));
      }
    }

    long rem = REPS;
    Vrbl fin = new Vrbl(BigInteger.ONE, BigInteger.ZERO);
    while (rem > 0) {
      if ((rem & 1) == 1) {
        fin = fin.apply(v);
      }
      v = v.apply(v);
      rem >>= 1;
    }

    System.out.println(fin.eval(2020));
  }

  // (ax + b) % CARDS
  private static class Vrbl {
    private BigInteger a;
    private BigInteger b;

    public Vrbl(BigInteger a, BigInteger b) {
      this.a = a;
      this.b = b;
    }

    public Vrbl multiply(BigInteger c) {
      BigInteger a1 = a.multiply(c).mod(BIG_CARDS);
      BigInteger b1 = b.multiply(c).mod(BIG_CARDS);
      return new Vrbl(a1, b1);
    }

    public Vrbl add(BigInteger c) {
      BigInteger b1 = b.add(c).mod(BIG_CARDS);
      return new Vrbl(a, b1);
    }

    public Vrbl apply(Vrbl v) {
      return v.multiply(a).add(b);
    }

    public long eval(long value) {
      return BigInteger.valueOf(value).multiply(a).add(b).mod(BIG_CARDS).longValue();
    }
  }
}
