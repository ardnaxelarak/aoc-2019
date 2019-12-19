import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;

public class Puzzle19a {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String program = sc.nextLine();
    String[] pieces = program.split(",");
    long[] memory = Stream.of(pieces).mapToLong(Long::parseLong).toArray();

    int count = 0;
    for (int y = 0; y < 50; y++) {
      for (int x = 0; x < 50; x++) {
        IO19a io = new IO19a();
        RunnableIntcode computer = new RunnableIntcode(memory, io);
        Thread thread = new Thread(computer);
        thread.start();
        if (io.sendCommand(x, y) == 1) {
          count++;
        }
        try {
          thread.join();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

    System.out.println(count);
  }

  private static class IO19a implements IntcodeIO {
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
      // System.err.println(value);
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
