import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Puzzle1a {
  public static void main(String[] args) {
    Scanner sc = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
    int total = 0;
    while (sc.hasNextInt()) {
      int mass = sc.nextInt();
      int fuel = mass / 3 - 2;
      System.err.printf("%d: %d\n", mass, fuel);
      total += fuel;
    }
    System.out.println(total);
  }
}
