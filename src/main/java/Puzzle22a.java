import com.ardnaxelarak.util.graph.Edge;
import com.ardnaxelarak.util.graph.EdgeProvider;
import com.ardnaxelarak.util.graph.Graphs;
import com.google.common.collect.ArrayListMultimap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Puzzle22a {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    List<String> lines = new ArrayList<>();
    while (sc.hasNextLine()) {
      lines.add(sc.nextLine());
    }
    ArrayList<Integer> deck = new ArrayList<>();
    for (int i = 0; i < 10007; i++) {
      deck.add(i);
    }

    for (String line : lines) {
      if (line.startsWith("deal with increment")) {
        incrementN(deck, Integer.parseInt(line.substring(20)));
      } else if (line.startsWith("deal into new stack")) {
        newStack(deck);
      } else if (line.startsWith("cut")) {
        cut(deck, Integer.parseInt(line.substring(4)));
      }
    }

    System.out.println(deck.indexOf(2019));
  }

  private static void newStack(List<Integer> deck) {
    Collections.reverse(deck);
  }

  private static void cut(List<Integer> deck, int n) {
    Collections.rotate(deck, -n);
  }

  private static void incrementN(ArrayList<Integer> deck, int n) {
    List<Integer> hold = new ArrayList<>(deck);
    int index = 0;
    for (int card : hold) {
      deck.set(index, card);
      index = (index + n) % deck.size();
    }
  }
}
