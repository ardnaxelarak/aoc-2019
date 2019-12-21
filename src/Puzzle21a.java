import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Stream;

public class Puzzle21a {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String program = sc.nextLine();
    String[] pieces = program.split(",");
    long[] memory = Stream.of(pieces).mapToLong(Long::parseLong).toArray();

    List<String> springScript = new ArrayList<>();
    springScript.add("OR B T");
    springScript.add("AND C T");
    springScript.add("NOT T T");
    springScript.add("OR D J");
    springScript.add("AND T J");
    springScript.add("NOT A T");
    springScript.add("OR T J");
    springScript.add("WALK");

    IO21a io = new IO21a(springScript);
    Intcode computer = new Intcode(memory);
    computer.execute(io);
  }

  private static class IO21a implements IntcodeIO {
    private char[] program;
    private int index = 0;

    public IO21a(List<String> program) {
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
