public class Puzzle21b {
  public static void main(String[] args) {
    /*
     * If not D, don't jump.
     * Otherwise, if not A, jump.
     * Otherwise, if E, jump.
     * Otherwise, if not B, jump.
     * Otherwise, if C, don't jump.
     * Otherwise, if not F, jump.
     * Otherwise, if not H, don't jump.
     * Otherwise, jump.
     */
    String springScript = 
        "OR H J\n"
        + "NOT F T\n"
        + "OR T J\n"
        + "NOT C T\n"
        + "AND T J\n"
        + "NOT B T\n"
        + "OR T J\n"
        + "OR E J\n"
        + "NOT A T\n"
        + "OR T J\n"
        + "AND D J\n"
        + "RUN\n";

    SpringdroidIO io = new SpringdroidIO(springScript);
    Intcode computer = Intcode.fromStdIn();
    computer.execute(io);
  }
}
