import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Puzzle14a {
  private static final Pattern PATTERN = Pattern.compile("\\s*(\\d+)\\s+(\\w+)\\s*");

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    Map<String, Reaction> reactions = new HashMap<>();
    while (sc.hasNextLine()) {
      String line = sc.nextLine();
      String[] pieces = line.split(",|(=>)");
      List<Chemical> inputs = new ArrayList<>(pieces.length - 1);
      for (int i = 0; i < pieces.length - 1; i++) {
        inputs.add(Chemical.parse(pieces[i]));
      }
      Chemical output = Chemical.parse(pieces[pieces.length - 1]);
      Reaction re = new Reaction(inputs, output);
      reactions.put(output.getName(), re);
    }

    Map<String, Integer> state = new HashMap<>();
    state.put("FUEL", -1);

    Optional<Map.Entry<String, Integer>> nextToFill;
    while (true) {
      nextToFill =
          state.entrySet().stream()
              .filter(entry -> !entry.getKey().equals("ORE"))
              .filter(entry -> entry.getValue() < 0)
              .findAny();
      if (!nextToFill.isPresent()) {
        break;
      }

      Reaction re = reactions.get(nextToFill.get().getKey());
      re.create(state);
    }

    System.out.println(-state.get("ORE"));
  }

  private static class Reaction {
    ImmutableList<Chemical> inputs;
    Chemical output;

    public Reaction(List<Chemical> inputs, Chemical output) {
      this.inputs = ImmutableList.copyOf(inputs);
      this.output = output;
    }

    @Override
    public String toString() {
      return Joiner.on(", ").join(inputs) + " => " + output;
    }

    public void create(Map<String, Integer> state) {
      int needed = -state.get(output.getName());
      if (needed <= 0) {
        return;
      }

      int doses = (needed - 1) / output.getQuantity() + 1;

      for (Chemical input : inputs) {
        state.put(input.getName(), state.getOrDefault(input.getName(), 0) - doses * input.getQuantity());
      }
      state.put(output.getName(), state.get(output.getName()) + doses * output.getQuantity());
    }
  }

  private static class Chemical {
    private int quantity;
    private String name;

    public Chemical(int quantity, String name) {
      this.quantity = quantity;
      this.name = name;
    }

    public int getQuantity() {
      return quantity;
    }

    public String getName() {
      return name;
    }

    @Override
    public String toString() {
      return quantity + " " + name;
    }

    public static Chemical parse(String text) {
      Matcher m = PATTERN.matcher(text);
      m.matches();
      return new Chemical(Integer.parseInt(m.group(1)), m.group(2));
    }
  }
}
