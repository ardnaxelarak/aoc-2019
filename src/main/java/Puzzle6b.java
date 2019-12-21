import com.ardnaxelarak.util.graph.EdgeProvider;
import com.ardnaxelarak.util.graph.EdgeProviders;
import com.ardnaxelarak.util.graph.Graphs;
import com.google.common.collect.ImmutableTable;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Puzzle6b {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    Map<String, String> orbits = new HashMap<>();

    while (sc.hasNextLine()) {
      String line = sc.nextLine();
      String[] pieces = line.split("\\)");
      orbits.put(pieces[1], pieces[0]);
    }

    EdgeProvider<String> edges =
        EdgeProviders.fromBidirectionalTable(
            orbits.entrySet().stream()
                .collect(
                    ImmutableTable.toImmutableTable(
                        entry -> entry.getKey(),
                        entry -> entry.getValue(),
                        entry -> 1L)));

    String start = orbits.get("YOU");
    String dest = orbits.get("SAN");

    System.out.println(Graphs.getDistance(edges, start, dest));
  }
}
