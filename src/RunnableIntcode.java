public class RunnableIntcode implements Runnable {
  private final Intcode computer;
  private final IntcodeIO io;

  public RunnableIntcode(int[] memory, IntcodeIO io) {
    this.computer = new Intcode(memory);
    this.io = io;
  }

  @Override
  public void run() {
    computer.execute(io);
  }
}
