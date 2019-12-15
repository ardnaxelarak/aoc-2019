public class ArrayIO implements IntcodeIO {
  private int index = 0;
  private int[] inputs;
  private int lastRead = 0;
  private boolean dispOutput;

  public ArrayIO(boolean dispOutput, int... inputs) {
    this.inputs = inputs;
    this.dispOutput = dispOutput;
  }

  public int input() {
    if (index >= inputs.length) {
      throw new IllegalStateException("No more input!");
    } else {
      return inputs[index++];
    }
  }

  public void output(int value) {
    if (dispOutput) {
      System.err.printf("Output: %d\n", value);
    }
    lastRead = value;
  }

  public int getLastOutput() {
    return lastRead;
  }
}
