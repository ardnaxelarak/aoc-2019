import java.util.Arrays;
import java.util.Optional;

public class Intcode {
  private static final boolean DEBUG = false;
  private static final IntcodeIO DEFAULT_IO = new IntcodeIO() {
    public int input() {
      throw new IllegalStateException("No implementation of input!");
    }

    public void output(int value) {
      System.err.printf("Output: %d\n", value);
    }
  };

  private int[] memory;

  public Intcode(int[] memory) {
    this.memory = Arrays.copyOf(memory, memory.length);
  }

  public int[] execute() {
    return execute(DEFAULT_IO);
  }

  public int[] execute(IntcodeIO io) {
    int index = 0;
    int p1, p2;
    int code;
    while ((code = memory[index]) % 100 != 99) {
      switch (code % 100) {
        case 1:
          if (DEBUG) {
            System.err.printf(
                "%d %d %d %d\n", code, memory[index + 1], memory[index + 2], memory[index + 3]);
          }
          p1 = readValue(code, index, 1);
          p2 = readValue(code, index, 2);
          storeValue(code, index, 3, p1 + p2);
          index += 4;
          break;
        case 2:
          if (DEBUG) {
            System.err.printf(
                "%d %d %d %d\n", code, memory[index + 1], memory[index + 2], memory[index + 3]);
          }
          p1 = readValue(code, index, 1);
          p2 = readValue(code, index, 2);
          storeValue(code, index, 3, p1 * p2);
          index += 4;
          break;
        case 3:
          if (DEBUG) {
            System.err.printf("%d %d\n", code, memory[index + 1]);
          }
          storeValue(code, index, 1, io.input());
          index += 2;
          break;
        case 4:
          if (DEBUG) {
            System.err.printf("%d %d\n", code, memory[index + 1]);
          }
          io.output(readValue(code, index, 1));
          index += 2;
          break;
        default:
          throw new IllegalStateException("Unexpected instruction: " + code % 100);
      }
    }
    return Arrays.copyOf(memory, memory.length);
  }

  private int readValue(int code, int index, int argnum) {
    int mode = getParameterMode(code, argnum);

    int parameter = memory[index + argnum];
    switch (mode) {
      case 0:
        return memory[parameter];
      case 1:
        return parameter;
      default:
        throw new IllegalArgumentException("Unexpected parameter mode: " + mode);
    }
  }

  private void storeValue(int code, int index, int argnum, int value) {
    int mode = getParameterMode(code, argnum);

    int parameter = memory[index + argnum];
    switch (mode) {
      case 0:
        memory[parameter] = value;
        break;
      case 1:
        throw new IllegalArgumentException("Cannot store value in immediate mode");
      default:
        throw new IllegalArgumentException("Unexpected parameter mode: " + mode);
    }
  }

  private int getParameterMode(int code, int argnum) {
    int mode = code / 100;
    for (int i = 1; i < argnum; i++) {
      mode /= 10;
    }
    mode = mode % 10;
    return mode;
  }
}
