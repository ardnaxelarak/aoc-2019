import java.util.stream.Stream;

public class Puzzle7a {
  public static void main(String[] args) {
    Intcode computer = Intcode.fromStdIn();

    Integer[] phases = new Integer[] {0, 1, 2, 3, 4};

    P7aEmitter emit = new P7aEmitter(computer);
    Permutations.listPermutations(phases, emit);

    System.out.println(emit.getMaxThrust());
  }

  private static long getThruster(Intcode computer, int[] settings) {
    if (settings.length != 5) {
      throw new IllegalArgumentException(
          "Expected 5 phase settings but got " + settings.length);
    }

    long lastvalue = 0;
    for (int i = 0; i < 5; i++) {
      ArrayIO amplifier = new ArrayIO(false, settings[i], lastvalue);
      computer.execute(amplifier);
      lastvalue = amplifier.getLastOutput();
    }
    return lastvalue;
  }

  private static class P7aEmitter implements Permutations.PermutationEmitter<Integer> {
    private Intcode computer;
    private long maxThrust = Long.MIN_VALUE;

    public P7aEmitter(Intcode computer) {
      this.computer = computer;
    }

    public void emit(Integer[] array) {
      int[] prim = Stream.of(array).mapToInt(Integer::intValue).toArray();
      long thrust = getThruster(computer, prim);
      if (thrust > maxThrust) {
        maxThrust = thrust;
      }
    }

    public long getMaxThrust() {
      return maxThrust;
    }
  }
}
