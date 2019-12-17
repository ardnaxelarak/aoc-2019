import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Puzzle17a {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String program = sc.nextLine();
    String[] pieces = program.split(",");
    long[] memory = Stream.of(pieces).mapToLong(Long::parseLong).toArray();

    IO17a io = new IO17a();
    Intcode computer = new Intcode(memory);
    computer.execute(io);

    char[][] camera = io.getCamera();
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

  private static class IO17a implements IntcodeIO {
    private List<StringBuilder> camera;
    public IO17a() {
      camera = new ArrayList<>();
      camera.add(new StringBuilder());
    }

    public long input() {
      throw new IllegalStateException();
    }

    public void output(long value) {
      System.out.print((char) value);
      if (value == 10) {
        camera.add(new StringBuilder());
      } else {
        camera.get(camera.size() - 1).append((char) value);
      }
    }

    public char[][] getCamera() {
      return camera.stream()
          .filter(sb -> sb.length() > 0)
          .map(sb -> sb.toString().toCharArray())
          .toArray(len -> new char[len][]);
    }
  }
}
