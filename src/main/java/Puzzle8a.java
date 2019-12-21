import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Puzzle8a {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String line = sc.nextLine();

    List<int[][]> data = new ArrayList<>();

    char[] input = line.toCharArray();
    int index = 0;

    while (index < input.length) {
      int[][] layer = new int[6][25];
      for (int y = 0; y < 6; y++) {
        for (int x = 0; x < 25; x++) {
          layer[y][x] = input[index++] - '0';
        }
      }
      data.add(layer);
    }

    int[][] counts = new int[data.size()][];
    int minzeroes = Integer.MAX_VALUE;
    int value = 0;
    for (int i = 0; i < data.size(); i++) {
      counts[i] = counts(data.get(i));
      if (counts[i][0] < minzeroes) {
        minzeroes = counts[i][0];
        value = counts[i][1] * counts[i][2];
      }
    }

    System.out.println(value);
  }

  private static int[] counts(int[][] layer) {
    int[] results = new int[10];
    for (int y = 0; y < layer.length; y++) {
      for (int x = 0; x < layer[y].length; x++) {
        results[layer[y][x]]++;
      }
    }
    return results;
  }
}
