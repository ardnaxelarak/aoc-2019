import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Puzzle6b {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    Map<String, String> orbits = new HashMap<>();
    HashMap<String, List<String>> destinations = new HashMap<>();

    while (sc.hasNextLine()) {
      String line = sc.nextLine();
      String[] pieces = line.split("\\)");
      orbits.put(pieces[1], pieces[0]);
      destinations.putIfAbsent(pieces[0], new ArrayList<>());
      destinations.get(pieces[0]).add(pieces[1]);
      destinations.putIfAbsent(pieces[1], new ArrayList<>());
      destinations.get(pieces[1]).add(pieces[0]);
    }

    String start = orbits.get("YOU");
    String dest = orbits.get("SAN");

    Map<String, Integer> distance = new HashMap<>();
    distance.put(start, 0);

    List<String> queue = new ArrayList<String>();
    queue.add(start);

    while (!queue.isEmpty()) {
      String elem = queue.remove(0);
      int base = distance.get(elem);
      for (String conn : destinations.get(elem)) {
        if (!distance.containsKey(conn)) {
          distance.put(conn, base + 1);
          queue.add(conn);
        }
      }
    }

    System.out.println(distance.get(dest));
  }
}
