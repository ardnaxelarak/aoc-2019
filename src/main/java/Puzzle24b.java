import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

import com.google.auto.value.AutoValue;
import com.google.common.base.*;
import com.google.common.collect.*;
import java.math.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.function.Function;
import java.util.stream.*;

public class Puzzle24b {
  private static final int MAX = 1 << 25;
  private static final ImmutableMap<Integer, BigInteger> ADJACENCY = genMap();

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

    Map<Integer, Integer> state = ImmutableMap.of(0, map);
    System.out.printf("%3d minutes: %d bugs\n", 0, bugCount(state));

    for (int i = 1; i <= 200; i++) {
      state = iterate(state);
      if (i <= 10) {
        // printState(state);
      }
      System.out.printf("%3d minutes: %d bugs\n", i, bugCount(state));
    }
  }

  private static Map<Integer, Integer> iterate(Map<Integer, Integer> oldState) {
    ImmutableMap.Builder<Integer, Integer> newState = ImmutableMap.builder();

    int min = oldState.keySet().stream().mapToInt(i -> i).min().getAsInt();
    int max = oldState.keySet().stream().mapToInt(i -> i).max().getAsInt();

    for (int level = min - 1; level <= max + 1; level++) {
      BigInteger state = ZERO;
      if (oldState.containsKey(level + 1)) {
        state = state.or(BigInteger.valueOf(oldState.get(level + 1)));
      }
      if (oldState.containsKey(level)) {
        state = state.or(BigInteger.valueOf(oldState.get(level)).shiftLeft(25));
      }
      if (oldState.containsKey(level - 1)) {
        state = state.or(BigInteger.valueOf(oldState.get(level - 1)).shiftLeft(50));
      }

      int levelState = 0;
      for (int i = 1; i < MAX; i <<= 1) {
        BigInteger adj = ADJACENCY.get(i).and(state);
        int numBits = adj.bitCount();
        if (numBits == 1 || (state.and(BigInteger.valueOf(i).shiftLeft(25)).equals(ZERO) && numBits == 2)) {
          levelState |= i;
        }
      }
      if (levelState != 0) {
        newState.put(level, levelState);
      }
    }

    return newState.build();
  }

  private static int bugCount(Map<Integer, Integer> state) {
    int count = 0;
    for (int level : state.values()) {
      count += countBits(level);
    }
    return count;
  }

  private static ImmutableMap<Integer, BigInteger> genMap() {
    ImmutableMap.Builder<Integer, BigInteger> map = ImmutableMap.builder();
    for (int y = 0; y < 5; y++) {
      for (int x = 0; x < 5; x++) {
        int base = 1 << (5 * y + x);
        BigInteger adj = ZERO;
        if (x != 2 || y != 2) {
          if (y == 0) {
            adj = adj.setBit(50 + 5 + 2);
          } else if (y > 0) {
            adj = adj.setBit(25 + 5 * (y - 1) + x);
          }
          if (x == 0) {
            adj = adj.setBit(50 + 10 + 1);
          } else if (x > 0) {
            adj = adj.setBit(25 + 5 * y + x - 1);
          }
          if (y == 4) {
            adj = adj.setBit(50 + 15 + 2);
          } else if (y < 4) {
            adj = adj.setBit(25 + 5 * (y + 1) + x);
          }
          if (x == 4) {
            adj = adj.setBit(50 + 10 + 3);
          } else if (x < 4) {
            adj = adj.setBit(25 + 5 * y + x + 1);
          }

          if (x == 2 && y == 1) {
            adj = adj.or(BigInteger.valueOf(31));
          }
          if (x == 2 && y == 3) {
            adj = adj.or(BigInteger.valueOf(31 << 20));
          }
          if (x == 1 && y == 2) {
            adj = adj.or(BigInteger.valueOf(1082401));
          }
          if (x == 3 && y == 2) {
            adj = adj.or(BigInteger.valueOf(1082401 << 4));
          }
        }
        map.put(base, adj);
      }
    }
    return map.build();
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

  private static void printMap(BigInteger map) {
    BigInteger curVal = map;
    for (int i = 0; i < 15; i++) {
      System.err.printf(
          "%5s\n", Integer.toBinaryString(curVal.and(BigInteger.valueOf(31)).intValue()));
      curVal = curVal.shiftRight(5);
    }
    System.err.println();
  }

  private static void printState(Map<Integer, Integer> state) {
    for (Map.Entry<Integer, Integer> entry : state.entrySet()) {
      System.err.printf("%d:\n", entry.getKey());
      int curVal = entry.getValue();
      for (int i = 0; i < 5; i++) {
        System.err.printf("%5s\n", Integer.toBinaryString(curVal & 31));
        curVal >>= 5;
      }
      System.err.println();
    }
  }
}
