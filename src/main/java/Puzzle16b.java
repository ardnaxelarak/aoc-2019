import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Puzzle16b {
  private static final boolean PART_B = true;

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String line = sc.nextLine();
    int[] baseArray = line.chars().map(c -> c - '0').toArray();

    int offset =
        Integer.parseInt(
            Arrays.stream(baseArray)
                .limit(7)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining()));

    int[] array;

    if (PART_B) {
      array =
          Stream.iterate(baseArray, UnaryOperator.identity())
              .limit(10000)
              .flatMapToInt(Arrays::stream)
              .toArray();
    } else {
      array = baseArray;
      offset = 0;
    }

    int times = 100;
    for (int i = 0; i < times; i++) {
      array = process(array);
    }
    System.out.println(
        Arrays.stream(array)
            .skip(offset)
            .limit(8)
            .mapToObj(Integer::toString)
            .collect(Collectors.joining()));
  }

  private static int[] process(int[] array) {
    int len = array.length;
    int[] newArray = new int[len];
    int[] partialSums = new int[len];

    int sum = 0;
    for (int i = len - 1; i >= 0; i--) {
      sum += array[i];
      partialSums[i] = sum;
    }

    for (int i = 0; i < len; i++) {
      int repeats = i + 1;
      int index = repeats - 1;
      int digit = 0;
      while (index < len) {
        digit += partialSums[index];
        index += repeats;

        if (index < len) {
          digit -= partialSums[index];
          index += repeats;
        }

        if (index < len) {
          digit -= partialSums[index];
          index += repeats;
        }

        if (index < len) {
          digit += partialSums[index];
          index += repeats;
        }
      }

      newArray[i] = Math.abs(digit) % 10;
    }
    return newArray;
  }
}
