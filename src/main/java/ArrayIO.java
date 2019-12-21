public class ArrayIO implements IntcodeIO {
  private int index = 0;
  private long[] inputs;
  private long lastRead = 0;
  private boolean dispOutput;

  public ArrayIO(boolean dispOutput, long... inputs) {
    this.inputs = inputs;
    this.dispOutput = dispOutput;
  }

  public long input() {
    if (index >= inputs.length) {
      throw new IllegalStateException("No more input!");
    } else {
      return inputs[index++];
    }
  }

  public void output(long value) {
    if (dispOutput) {
      System.err.printf("Output: %d\n", value);
    }
    lastRead = value;
  }

  public long getLastOutput() {
    return lastRead;
  }
}
