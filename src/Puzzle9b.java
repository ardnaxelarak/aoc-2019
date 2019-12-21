public class Puzzle9b {
  public static void main(String[] args) {
    Intcode computer = Intcode.fromStdIn();
    ArrayIO io = new ArrayIO(false, 2);
    computer.execute(io);
    System.out.println(io.getLastOutput());
  }
}
