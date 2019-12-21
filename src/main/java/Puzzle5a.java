public class Puzzle5a {
  public static void main(String[] args) {
    Intcode computer = Intcode.fromStdIn();
    ArrayIO io = new ArrayIO(true, 1);
    computer.execute(io);
    System.out.println(io.getLastOutput());
  }
}
