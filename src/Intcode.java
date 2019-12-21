import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Stream;

public class Intcode {
  private static final boolean DEBUG = false;
  private static final IntcodeIO DEFAULT_IO = new IntcodeIO() {
    public long input() {
      throw new IllegalStateException("No implementation of input!");
    }

    public void output(long value) {
      System.err.printf("Output: %d\n", value);
    }
  };

  private long[] memory;
  private long[] originalMemory;
  private int relativeBase;

  public Intcode(int[] memory) {
    this(Arrays.stream(memory).asLongStream().toArray());
  }

  public Intcode(long[] memory) {
    this.originalMemory = Arrays.copyOf(memory, memory.length * 10);
  }

  public static Intcode fromStdIn() {
    Scanner sc = new Scanner(System.in);
    String program = sc.nextLine();
    String[] pieces = program.split(",");
    long[] memory = Stream.of(pieces).mapToLong(Long::parseLong).toArray();
    return new Intcode(memory);
  }

  public long[] execute() {
    return execute(DEFAULT_IO);
  }

  public long[] execute(IntcodeIO io) {
    this.memory = Arrays.copyOf(originalMemory, originalMemory.length);
    this.relativeBase = 0;

    int index = 0;
    long p1, p2;
    long code;
    while ((code = memory[index]) % 100 != 99) {
      switch ((int) (code % 100)) {
        case 1:
          debug(code, index, 3);
          p1 = readValue(code, index, 1);
          p2 = readValue(code, index, 2);
          storeValue(code, index, 3, p1 + p2);
          index += 4;
          break;
        case 2:
          debug(code, index, 3);
          p1 = readValue(code, index, 1);
          p2 = readValue(code, index, 2);
          storeValue(code, index, 3, p1 * p2);
          index += 4;
          break;
        case 3:
          debug(code, index, 1);
          storeValue(code, index, 1, io.input());
          index += 2;
          break;
        case 4:
          debug(code, index, 1);
          io.output(readValue(code, index, 1));
          index += 2;
          break;
        case 5:
          debug(code, index, 2);
          if (readValue(code, index, 1) != 0) {
            index = (int) readValue(code, index, 2);
          } else {
            index += 3;
          }
          break;
        case 6:
          debug(code, index, 2);
          if (readValue(code, index, 1) == 0) {
            index = (int) readValue(code, index, 2);
          } else {
            index += 3;
          }
          break;
        case 7:
          debug(code, index, 3);
          p1 = readValue(code, index, 1);
          p2 = readValue(code, index, 2);
          storeValue(code, index, 3, p1 < p2 ? 1 : 0);
          index += 4;
          break;
        case 8:
          debug(code, index, 3);
          p1 = readValue(code, index, 1);
          p2 = readValue(code, index, 2);
          storeValue(code, index, 3, p1 == p2 ? 1 : 0);
          index += 4;
          break;
        case 9:
          debug(code, index, 1);
          relativeBase += readValue(code, index, 1);
          index += 2;
          break;
        default:
          throw new IllegalStateException("Unexpected instruction: " + code % 100);
      }
    }
    return Arrays.copyOf(memory, memory.length);
  }

  private void debug(long code, int index, int params) {
    if (!DEBUG) {
      return;
    }
    System.err.print(code);
    for (int i = 1; i <= params; i++) {
      System.err.printf(" %d", memory[index + i]);
    }
    System.err.println();
  }

  private long readValue(long code, int index, int argnum) {
    int mode = getParameterMode(code, argnum);

    long parameter = memory[index + argnum];
    switch (mode) {
      case 0:
        return memory[(int) parameter];
      case 1:
        return parameter;
      case 2:
        return memory[(int) (relativeBase + parameter)];
      default:
        throw new IllegalArgumentException("Unexpected parameter mode: " + mode);
    }
  }

  private void storeValue(long code, int index, int argnum, long value) {
    int mode = getParameterMode(code, argnum);

    long parameter = memory[index + argnum];
    switch (mode) {
      case 0:
        memory[(int) parameter] = value;
        break;
      case 1:
        throw new IllegalArgumentException("Cannot store value in immediate mode");
      case 2:
        memory[(int) (relativeBase + parameter)] = value;
        break;
      default:
        throw new IllegalArgumentException("Unexpected parameter mode: " + mode);
    }
  }

  private int getParameterMode(long code, int argnum) {
    long mode = code / 100;
    for (int i = 1; i < argnum; i++) {
      mode /= 10;
    }
    mode = mode % 10;
    return (int) mode;
  }
}
