public class Puzzle17a {
  public static void main(String[] args) {
    Intcode computer = Intcode.fromStdIn();
    AsciiIO io = new AsciiIO(true, "");
    computer.execute(io);

    char[][] camera =
        io.getAsciiOutput().stream().map(String::toCharArray).toArray(char[][]::new);

    int sum = 0;
    for (int y = 0; y < camera.length; y++) {
      for (int x = 0; x < camera[y].length; x++) {
        if (camera[y][x] != '#') {
          continue;
        }

        if (x == 0 || y == 0 || y >= camera.length - 1 || x >= camera[y].length - 1) {
          continue;
        }

        if (camera[y - 1][x] == '#' && camera[y + 1][x] == '#'
            && camera[y][x - 1] == '#' && camera[y][x + 1] == '#') {
          sum += x * y;
        }
      }
    }

    System.out.println(sum);
  }
}
