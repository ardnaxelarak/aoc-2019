public class Puzzle9a {
  public static void main(String[] args) {
    Intcode computer = Intcode.fromStdIn();
    ArrayIO io = new ArrayIO(false, 1);
    computer.execute(io);
    System.out.println(io.getLastOutput());
  }
}
