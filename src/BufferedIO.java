import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BufferedIO implements IntcodeIO {
  private final BlockingQueue<Integer> inputBuffer;
  private final BlockingQueue<Integer> outputBuffer;

  public BufferedIO(int size) {
    inputBuffer = new ArrayBlockingQueue<>(size);
    outputBuffer = new ArrayBlockingQueue<>(size);
  }

  public BufferedIO() {
    this(10);
  }

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

  public int sendCommand(int... inputs) {
    synchronized (inputBuffer) {
      for (int value : inputs) {
        inputBuffer.add(value);
      }
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
