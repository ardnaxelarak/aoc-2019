public class Puzzle5b {
  public static void main(String[] args) {
    Intcode computer = Intcode.fromStdIn();
    ArrayIO io = new ArrayIO(true, 5);
    computer.execute(io);
    System.out.println(io.getLastOutput());
  }
}
