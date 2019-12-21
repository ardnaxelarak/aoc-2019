import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Stream;

public class Puzzle21b {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String program = sc.nextLine();
    String[] pieces = program.split(",");
    long[] memory = Stream.of(pieces).mapToLong(Long::parseLong).toArray();

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
    List<String> springScript = new ArrayList<>();
    springScript.add("OR H J");
    springScript.add("NOT F T");
    springScript.add("OR T J");
    springScript.add("NOT C T");
    springScript.add("AND T J");
    springScript.add("NOT B T");
    springScript.add("OR T J");
    springScript.add("OR E J");
    springScript.add("NOT A T");
    springScript.add("OR T J");
    springScript.add("AND D J");
    springScript.add("RUN");

    IO21b io = new IO21b(springScript);
    Intcode computer = new Intcode(memory);
    computer.execute(io);
  }

  private static class IO21b implements IntcodeIO {
    private char[] program;
    private int index = 0;

    public IO21b(List<String> program) {
      Joiner joiner = Joiner.on('\n');
      this.program = (joiner.join(program) + "\n").toCharArray();
    }

    public long input() {
      return program[index++];
    }

    public void output(long value) {
      if (value > 127) {
        System.out.println(value);
      } else {
        System.out.print((char) value);
      }
    }
  }
}
