import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Puzzle16a {
  public static void main(String[] args) {
    if (args.length < 1) {
      throw new IllegalArgumentException("Must specify number of phases");
    }

    Scanner sc = new Scanner(System.in);
    String line = sc.nextLine();
    int[] array = line.chars().map(c -> c - '0').toArray();

    int times = Integer.parseInt(args[0]);
    for (int i = 0; i < times; i++) {
      array = process(array);
    }
    System.out.println(
        Arrays.stream(array).limit(8).mapToObj(Integer::toString).collect(Collectors.joining()));
  }

  private static int[] process(int[] array) {
    int len = array.length;
    int[] newArray = new int[len];
    for (int i = 0; i < len; i++) {
      int start = array[i];
      int[] pattern = getPattern(i + 1);
      // System.err.println(Arrays.toString(pattern));
      int value = 0;
      for (int j = 0; j < len; j++) {
        value += array[j] * pattern[j % pattern.length];
      }
      value = Math.abs(value) % 10;
      newArray[i] = value;
    }
    return newArray;
  }

  private static int[] getPattern(int position) {
    int[] base = new int[] {0, 1, 0, -1};
    int[] pattern = new int[position * 4];
    int spot = 0;
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < position; j++) {
        if (i == 0 && j == 0) {
          pattern[position * 4 - 1] = 0;
        } else {
          pattern[i * position + j - 1] = base[i];
        }
      }
    }
    return pattern;
  }
}
