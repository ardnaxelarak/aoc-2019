import java.util.Scanner;

public class Puzzle1b {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    int total = 0;
    while (sc.hasNextInt()) {
      int startmass = sc.nextInt();
      int fuelmass = 0;
      int fuel = startmass;
      while ((fuel = Math.max(0, fuel / 3 - 2)) > 0) {
        fuelmass += fuel;
      }
      System.err.printf("%d: %d\n", startmass, fuelmass);
      total += fuelmass;
    }
    System.out.println(total);
  }
}
