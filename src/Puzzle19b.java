import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;

public class Puzzle19b {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String program = sc.nextLine();
    String[] pieces = program.split(",");
    long[] memory = Stream.of(pieces).mapToLong(Long::parseLong).toArray();

    int x = 0;
    int startX = 0;
    int y = 0;
    Map<Integer, Integer> starts = new HashMap<>();
    Map<Integer, Integer> length = new HashMap<>();
    while (true) {
      y++;
      x = startX;
      boolean found = true;
      while (getBeam(memory, y, x) == 0) {
        if (x > startX + 100) {
          found = false;
          System.err.printf("Line %d empty\n", y);
          break;
        }
        x++;
      }
      if (!found) {
        continue;
      }
      startX = x;
      int len = 0;
      while (getBeam(memory, y, x) == 1) {
        x++;
        len++;
      }

      if (len >= 100) {
        starts.put(y, startX);
        length.put(y, len);
        if (starts.containsKey(y - 99)) {
          if (startX + 99 <= starts.get(y - 99) + length.get(y - 99) - 1) {
            System.out.println(startX * 10000 + (y - 99));
            break;
          }
        }
      }
    }
  }

  private static int getBeam(long[] memory, int y, int x) {
    IO19b io = new IO19b();
    RunnableIntcode computer = new RunnableIntcode(memory, io);
    Thread thread = new Thread(computer);
    thread.start();
    int result = io.sendCommand(x, y);
    try {
      thread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return result;
  }

  private static class IO19b implements IntcodeIO {
    private final BlockingQueue<Integer> inputBuffer = new ArrayBlockingQueue<>(10);
    private final BlockingQueue<Integer> outputBuffer = new ArrayBlockingQueue<>(10);

    public long input() {
      synchronized (inputBuffer) {
        Integer next;
        while ((next = inputBuffer.poll()) == null) {
          try {
            inputBuffer.wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        return next;
      }
    }

    public void output(long value) {
      synchronized (outputBuffer) {
        outputBuffer.add((int) value);
        outputBuffer.notify();
      }
    }

    public int sendCommand(int value1, int value2) {
      synchronized (inputBuffer) {
        inputBuffer.add(value1);
        inputBuffer.add(value2);
        inputBuffer.notify();
      }

      synchronized (outputBuffer) {
        Integer next;
        while ((next = outputBuffer.poll()) == null) {
          try {
            outputBuffer.wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        return next;
      }
    }
  }
}
