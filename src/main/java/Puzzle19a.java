public class Puzzle19a {
  public static void main(String[] args) {
    Intcode computer = Intcode.fromStdIn();

    int count = 0;
    for (int y = 0; y < 50; y++) {
      for (int x = 0; x < 50; x++) {
        ArrayIO io = new ArrayIO(false, x, y);
        computer.execute(io);
        if (io.getLastOutput() == 1) {
          count++;
        }
      }
    }

    System.out.println(count);
  }
}
