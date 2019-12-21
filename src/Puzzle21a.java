public class Puzzle21a {
  public static void main(String[] args) {
    String springScript =
        "OR B T\n"
        + "AND C T\n"
        + "NOT T T\n"
        + "OR D J\n"
        + "AND T J\n"
        + "NOT A T\n"
        + "OR T J\n"
        + "WALK\n";

    AsciiIO io = new AsciiIO(true, springScript);
    Intcode computer = Intcode.fromStdIn();
    computer.execute(io);
  }
}
