public class Puzzle11b {
  public static void main(String[] args) {
    Intcode computer = Intcode.fromStdIn();
    HullPainterIO io = new HullPainterIO(true);
    computer.execute(io);
    io.print();
  }
}
