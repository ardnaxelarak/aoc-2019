import java.util.Scanner;

public class Puzzle4a {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);

    String line = sc.nextLine();
    String[] pieces = line.split("-");
    int min = Integer.parseInt(pieces[0]);
    int max = Integer.parseInt(pieces[1]);
    int count = 0;
    for (int i = min; i <= max; i++) {
      if (isValid(i)) {
        count++;
      }
    }

    System.out.println(count);
  }

  private static boolean isValid(int code) {
    String codestr = Integer.toString(code);
    if (codestr.length() != 6) {
      return false;
    }

    boolean match = false;
    char cur = codestr.charAt(0);
    char next;
    for (int i = 1; i < codestr.length(); i++) {
      next = codestr.charAt(i);
      if (next < cur) {
        return false;
      }
      if (next == cur) {
        match = true;
      }
      cur = next;
    }

    return match;
  }
}
