import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Puzzle6a {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    Map<String, String> orbits = new HashMap<>();
    while (sc.hasNextLine()) {
      String line = sc.nextLine();
      String[] pieces = line.split("\\)");
      orbits.put(pieces[1], pieces[0]);
    }

    Map<String, Integer> count = new HashMap<>();
    count.put("COM", 0);

    Map<String, String> remOrbits = new HashMap<>(orbits);
    while (!remOrbits.isEmpty()) {
      Set<Map.Entry<String, String>> entries = new HashSet<>(remOrbits.entrySet());
      for (Map.Entry<String, String> entry : entries) {
        if (count.containsKey(entry.getValue())) {
          count.put(entry.getKey(), count.get(entry.getValue()) + 1);
          remOrbits.remove(entry.getKey());
        }
      }
    }

    int total = count.values().stream().mapToInt(Integer::intValue).sum();
    System.out.println(total);
  }
}
