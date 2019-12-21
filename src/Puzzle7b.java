import java.util.stream.Stream;

public class Puzzle7b {
  public static void main(String[] args) {
    Intcode computer = Intcode.fromStdIn();

    Integer[] phases = new Integer[] {5, 6, 7, 8, 9};

    P7bEmitter emit = new P7bEmitter(computer);
    Permutations.listPermutations(phases, emit);

    System.out.println(emit.getMaxThrust());
  }

  private static long getThruster(Intcode computer, int[] settings)
      throws InterruptedException {
    if (settings.length != 5) {
      throw new IllegalArgumentException(
          "Expected 5 phase settings but got " + settings.length);
    }

    CustomIO[] ios = new CustomIO[5];
    ios[0] = new CustomIO(settings[0], 0);
    for (int i = 1; i < 5; i++) {
      ios[i] = new CustomIO(settings[i]); 
    }

    for (int i = 0; i < 5; i++) {
      ios[i].setAfter(ios[(i + 1) % 5]);
    }

    RunnableIntcode[] runnableComputers = new RunnableIntcode[5];
    Thread[] threads = new Thread[5];
    for (int i = 0; i < 5; i++) {
      runnableComputers[i] = new RunnableIntcode(computer, ios[i]);
      threads[i] = new Thread(runnableComputers[i]);
      threads[i].start();
    }

    for (int i = 0; i < 5; i++) {
      threads[i].join();
    }

    return ios[4].getLastOutput();
  }

  private static class P7bEmitter implements Permutations.PermutationEmitter<Integer> {
    private Intcode computer;
    private long maxThrust = Long.MIN_VALUE;

    public P7bEmitter(Intcode computer) {
      this.computer = computer;
    }

    public void emit(Integer[] array) {
      int[] prim = Stream.of(array).mapToInt(Integer::intValue).toArray();
      try {
        long thrust = getThruster(computer, prim);
        if (thrust > maxThrust) {
          maxThrust = thrust;
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    public long getMaxThrust() {
      return maxThrust;
    }
  }
}
