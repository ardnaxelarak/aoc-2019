public class RunnableIntcode implements Runnable {
  private final Intcode computer;
  private final IntcodeIO io;

  public RunnableIntcode(Intcode computer, IntcodeIO io) {
    this.computer = computer.clone();
    this.io = io;
  }

  @Override
  public void run() {
    computer.execute(io);
  }
}
