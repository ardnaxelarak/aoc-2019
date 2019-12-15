import java.util.ArrayList;
import java.util.List;

public class CustomIO implements IntcodeIO {
  private CustomIO after = null;
  private int[] initialInputs;
  private int index = 0;
  private List<Integer> buffer = new ArrayList<>();
  private int lastValue;

  public CustomIO(int... inputs) {
    this.initialInputs = inputs;
  }

  public void setAfter(CustomIO after) {
    this.after = after;
  }

  public int input() {
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

  public void output(int value) {
    if (after != null) {
      after.addInput(value);
    }
    lastValue = value;
  }

  public void addInput(int value) {
    synchronized (this) {
      buffer.add(value);
      this.notify();
    }
  }

  public int getLastOutput() {
    return lastValue;
  }
}
