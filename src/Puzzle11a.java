public class Puzzle11a {
  public static void main(String[] args) {
    Intcode computer = Intcode.fromStdIn();
    HullPainterIO io = new HullPainterIO(false);
    computer.execute(io);
    System.out.println(io.getPaintedPanels());
  }
}
