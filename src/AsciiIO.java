import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;

public class AsciiIO implements IntcodeIO {
  private List<StringBuilder> output;
  private char[] input;
  private int index = 0;
  private boolean dispOutput;
  private long lastValue;

  public AsciiIO(boolean dispOutput, String input) {
    this.input = input.toCharArray();
    this.dispOutput = dispOutput;
    output = new ArrayList<>();
    output.add(new StringBuilder());
  }

  public long input() {
    if (index >= input.length) {
      throw new IllegalStateException("No more input!");
    } else {
      return input[index++];
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

