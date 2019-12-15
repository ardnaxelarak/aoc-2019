import java.util.Scanner;
import java.util.stream.Stream;

public class Puzzle7a {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String program = sc.nextLine();
    String[] pieces = program.split(",");
    int[] memory = Stream.of(pieces).mapToInt(Integer::parseInt).toArray();

    Integer[] phases = new Integer[] {0, 1, 2, 3, 4};

    P7aEmitter emit = new P7aEmitter(memory);
    Permutations.listPermutations(phases, emit);

    System.out.println(emit.getMaxThrust());
  }

  private static long getThruster(int[] memory, int[] settings) {
    if (settings.length != 5) {
      throw new IllegalArgumentException(
          "Expected 5 phase settings but got " + settings.length);
    }

    Intcode computer = new Intcode(memory);
    long lastvalue = 0;
    for (int i = 0; i < 5; i++) {
      ArrayIO amplifier = new ArrayIO(false, settings[i], lastvalue);
      new Intcode(memory).execute(amplifier);
      lastvalue = amplifier.getLastOutput();
    }
    return lastvalue;
  }

  private static class P7aEmitter implements Permutations.PermutationEmitter<Integer> {
    private int[] memory;
    private long maxThrust = Long.MIN_VALUE;

    public P7aEmitter(int[] memory) {
      this.memory = memory;
    }

    public void emit(Integer[] array) {
      int[] prim = Stream.of(array).mapToInt(Integer::intValue).toArray();
      long thrust = getThruster(memory, prim);
      if (thrust > maxThrust) {
        maxThrust = thrust;
      }
    }

    public long getMaxThrust() {
      return maxThrust;
    }
  }
}
