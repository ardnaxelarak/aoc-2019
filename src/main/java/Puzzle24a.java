import com.google.auto.value.AutoValue;
import com.google.common.base.*;
import com.google.common.collect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

public class Puzzle24a {
  private static final int MAX = 1 << 25;
  private static final ImmutableMap<Integer, Integer> ADJACENCY = genMap();
  private static final int[] NUM_BITS = genNumBits();

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    int map = 0;
    int pos = 1;
    while (sc.hasNextLine()) {
      String line = sc.nextLine();
      for (char c : line.toCharArray()) {
        if (c == '#') {
          map |= pos;
        }
        pos <<= 1;
      }
    }

    List<Integer> states = new ArrayList<>();
    while (!states.contains(map)) {
      states.add(map);
      map = iterate(map);
    }

    System.out.println(map);
  }

  private static int iterate(int oldState) {
    int newState = 0;
    for (int i = 1; i < MAX; i <<= 1) {
      int adj = ADJACENCY.get(i) & oldState;
      int numBits = NUM_BITS[adj];
      if (numBits == 1 || ((oldState & i) == 0 && numBits == 2)) {
        newState |= i;
      }
    }
    return newState;
  }

  private static ImmutableMap<Integer, Integer> genMap() {
    ImmutableMap.Builder<Integer, Integer> map = ImmutableMap.builder();
    for (int y = 0; y < 5; y++) {
      for (int x = 0; x < 5; x++) {
        int base = 1 << (5 * y + x);
        int adj = 0;
        if (y > 0) {
          adj |= 1 << (5 * (y - 1) + x);
        }
        if (x > 0) {
          adj |= 1 << (5 * y + x - 1);
        }
        if (y < 4) {
          adj |= 1 << (5 * (y + 1) + x);
        }
        if (x < 4) {
          adj |= 1 << (5 * y + x + 1);
        }
        map.put(base, adj);
      }
    }
    return map.build();
  }

  private static int[] genNumBits() {
    int[] array = new int[MAX];
    for (int i = 0; i < MAX; i++) {
      array[i] = countBits(i);
    }
    return array;
  }

  private static int countBits(int value) {
    int count = 0;
    for (int i = 1; i < MAX; i <<= 1) {
      if ((value & i) > 0) {
        count++;
      }
    }
    return count;
  }

  private static void printMap(int map) {
    int curVal = map;
    for (int i = 0; i < 5; i++) {
      System.err.printf("%5s\n", Integer.toBinaryString(curVal & 31));
      curVal >>= 5;
    }
    System.err.println();
  }
}
