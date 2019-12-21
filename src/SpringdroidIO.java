public class SpringdroidIO implements IntcodeIO {
  private char[] program;
  private int index = 0;

  public SpringdroidIO(String program) {
    this.program = program.toCharArray();
  }

  public long input() {
    return program[index++];
  }

  public void output(long value) {
    if (value > 127) {
      System.out.println(value);
    } else {
      System.out.print((char) value);
    }
  }
}
