public class Puzzle2b {
  public static void main(String[] args) {
    Intcode computer = Intcode.fromStdIn();
    for (int noun = 0; noun < 100; noun++) {
      for (int verb = 0; verb < 100; verb++) {
        computer.modifyMemory(1, noun);
        computer.modifyMemory(2, verb);
        long[] output = computer.execute();
        if (output[0] == 19690720) {
          System.out.println(100 * noun + verb);
          System.exit(0);
        }
      }
    }
    System.err.println("No solution found :(");
  }
}
