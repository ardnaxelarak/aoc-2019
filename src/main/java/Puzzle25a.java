import com.google.auto.value.AutoValue;
import com.google.common.base.*;
import com.google.common.collect.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

public class Puzzle25a {
  public static void main(String[] args) throws IOException {
    Intcode computer = Intcode.from(new Scanner(new File("inputs\\p25.txt")));
    AsciiInputIO io = new AsciiInputIO(true, System.in);
    computer.execute(io);
  }

  // solution was to hold coin, spool of cat6, fixed point, and sand
  public static class AsciiInputIO implements IntcodeIO {
    private List<StringBuilder> output;
    private Scanner input;
    private String line;
    private int index = 0;
    private boolean dispOutput;
    private long lastValue;

    public AsciiInputIO(boolean dispOutput, InputStream input) {
      this.input = new Scanner(input);
      this.dispOutput = dispOutput;
      output = new ArrayList<>();
      output.add(new StringBuilder());
    }

    public long input() {
      if (line != null && index < line.length()) {
        return line.charAt(index++);
      } else if (line != null && index == line.length()) {
        index++;
        return 10;
      } else {
        line = input.nextLine();
        while (line.isEmpty()) {
          line = input.nextLine();
        }
        index = 0;
        return line.charAt(index++);
      }
    }

    public void output(long value) {
      if (value > 127) {
        if (dispOutput) {
          System.err.println(value);
        }
        lastValue = value;
      } else {
        if (dispOutput) {
          System.err.print((char) value);
        }
        if (value == 10) {
          output.add(new StringBuilder());
        } else {
          output.get(output.size() - 1).append((char) value);
        }
      }
    }

    public long getLastValue() {
      return lastValue;
    }

    public List<String> getAsciiOutput() {
      return output.stream()
          .filter(sb -> sb.length() > 0)
          .map(sb -> sb.toString())
          .collect(ImmutableList.toImmutableList());
    }
  }
}
