public class Puzzle2a {
  public static void main(String[] args) {
    Intcode computer = Intcode.fromStdIn();
    computer.modifyMemory(1, 12);
    computer.modifyMemory(2, 2);

    long[] output = computer.execute();
    System.out.println(output[0]);
  }
}
