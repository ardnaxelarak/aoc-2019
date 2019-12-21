import java.util.ArrayList;
import java.util.List;

public class CustomIO implements IntcodeIO {
  private CustomIO after = null;
  private long[] initialInputs;
  private int index = 0;
  private List<Long> buffer = new ArrayList<>();
  private long lastValue;

  public CustomIO(long... inputs) {
    this.initialInputs = inputs;
  }

  public void setAfter(CustomIO after) {
    this.after = after;
  }

  public long input() {
    synchronized (this) {
      if (index < initialInputs.length) {
        return initialInputs[index++];
      }

      while (buffer.isEmpty()) {
        try {
          this.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      return buffer.remove(0);
    }
  }

  public void output(long value) {
    if (after != null) {
      after.addInput(value);
    }
    lastValue = value;
  }

  public void addInput(long value) {
    synchronized (this) {
      buffer.add(value);
      this.notify();
    }
  }

  public long getLastOutput() {
    return lastValue;
  }
}
