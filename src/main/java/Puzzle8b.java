import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Puzzle8b {
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
    
    int[][] assembled = new int[6][25];
    for (int y = 0; y < 6; y++) {
      for (int x = 0; x < 25; x++) {
        index = 0;
        while (data.get(index)[y][x] == 2) {
          index++;
        }
        assembled[y][x] = data.get(index)[y][x];
        System.out.print(assembled[y][x]);
      }
      System.out.println();
    }
  }
}
