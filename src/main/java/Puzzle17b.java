public class Puzzle17b {
  public static void main(String[] args) {
    Intcode computer = Intcode.fromStdIn();
    computer.modifyMemory(0, 2);

    String program = 
        "A,B,B,A,C,A,C,A,C,B\n"
        + "L,6,R,12,R,8\n"
        + "R,8,R,12,L,12\n"
        + "R,12,L,12,L,4,L,4\n"
        + "n\n";

    AsciiIO io = new AsciiIO(true, program);
    computer.execute(io);
  }
}
