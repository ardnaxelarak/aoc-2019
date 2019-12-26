import com.google.common.collect.ImmutableList;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AsciiIO implements IntcodeIO {
  private List<StringBuilder> output;
  private InputStream input;
  private boolean dispOutput;
  private long lastValue;

  public AsciiIO(boolean dispOutput, InputStream input) {
    this.input = input;
    this.dispOutput = dispOutput;
    output = new ArrayList<>();
    output.add(new StringBuilder());
  }

  public AsciiIO(boolean dispOutput, String input) {
    this(dispOutput, new ByteArrayInputStream(input.getBytes(StandardCharsets.US_ASCII)));
  }

  public long input() {
    try {
      int value = input.read();
      // some OSes use char 13 followed by char 10 to signify a newline
      // but our Intcode ASCII input only wants char 10, so we ignore all char 13s
      while (value == 13) {
        value = input.read();
      }
      return value;
    } catch (IOException e) {
      throw new IllegalStateException("Exception while trying to read input.", e);
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
