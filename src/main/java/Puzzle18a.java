import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Puzzle18a2 {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    List<String> inputs = new ArrayList<>();
    while (sc.hasNextLine()) {
      inputs.add(sc.nextLine());
    }

    int[][] map =
        inputs.stream()
            .map(line -> line.chars().map(Puzzle18a2::getValue).toArray())
            .toArray(int[][]::new);

    Table<Integer, Integer, Integer> distances = getDistances(map);

    Set<Integer> neededKeys =
        distances.rowKeySet().stream()
            .filter(loc -> loc > 0)
            .collect(ImmutableSet.toImmutableSet());

    State state = new State(0, 0, neededKeys);
    List<State> queue = new ArrayList<>();
    queue.add(state);
    State sol = null;

    while (!queue.isEmpty()) {
      state = queue.remove(0);
      // System.err.println(state.getKeysNeeded());
      if (state.getKeysNeeded().isEmpty()) {
        if (sol == null || state.getDistance() < sol.getDistance()) {
          sol = state;
        }
      } else {
        addStates(queue, state.getNextSteps(distances));
      }
    }

    System.out.println(sol.getDistance());
  }

  private static void addStates(List<State> queue, List<State> toAdd) {
    for (State s : toAdd) {
      Optional<State> similar = queue.stream().filter(s::sameBase).findAny();
      if (similar.isPresent()) {
        if (similar.get().getDistance() > s.getDistance()) {
          queue.remove(similar.get());
          queue.add(s);
        }
      } else {
        queue.add(s);
      }
    }
  }

  private static class State {
    private int curLoc;
    private int distance;
    private ImmutableSet<Integer> keysNeeded;

    public State(int curLoc, int distance, Set<Integer> keysNeeded) {
      this.curLoc = curLoc;
      this.distance = distance;
      this.keysNeeded = ImmutableSet.copyOf(keysNeeded);
    }

    public List<State> getNextSteps(Table<Integer, Integer, Integer> distances) {
      Map<Integer, Integer> visited = new HashMap<>();
      Map<Integer, Integer> unvisited = new HashMap<>();
      List<State> nextSteps = new ArrayList<>();
      unvisited.put(curLoc, distance);

      while (!unvisited.isEmpty()) {
        int loc =
            unvisited.entrySet().stream()
                .min(Comparator.comparingInt(Map.Entry::getValue)).get().getKey();

        int baseDist = unvisited.get(loc);
        visited.put(loc, baseDist);
        unvisited.remove(loc);

        if (keysNeeded.contains(loc)) {
          continue;
        }

        for (int newLoc : distances.columnKeySet()) {
          if (!distances.contains(loc, newLoc)) {
            continue;
          }

          if (newLoc < 0 && keysNeeded.contains(-newLoc)) {
            continue;
          }

          if (visited.containsKey(newLoc)) {
            continue;
          }

          int dist = baseDist + distances.get(loc, newLoc);
          
          if (!unvisited.containsKey(newLoc) || unvisited.get(newLoc) > dist) {
            unvisited.put(newLoc, dist);
          }
        }
      }

      return visited.entrySet().stream()
          .filter(entry -> keysNeeded.contains(entry.getKey()))
          .map(
              entry -> new State(entry.getKey(), entry.getValue(), getKeys(entry.getKey())))
          .collect(ImmutableList.toImmutableList());
    }

    private Set<Integer> getKeys(int collected) {
      return keysNeeded.stream().filter(key -> key != collected).collect(ImmutableSet.toImmutableSet());
    }

    public int getDistance() {
      return distance;
    }

    public Set<Integer> getKeysNeeded() {
      return keysNeeded;
    }

    public boolean sameBase(State other) {
      return curLoc == other.curLoc && keysNeeded.equals(other.keysNeeded);
    }
  }

  private static int getValue(int c) {
    if (c == '@') {
      return 0;
    } else if (c == '.') {
      return 1;
    } else if (c == '#') {
      return -1;
    } else if (c >= 'a' && c <= 'z') {
      return c - 'a' + 2;
    } else if (c >= 'A' && c <= 'Z') {
      return -(c - 'A' + 2);
    } else {
      throw new IllegalArgumentException("Unexpected input: " + c);
    }
  }

  private static Optional<Integer> check(int[][] map, int[][] dist, int tx, int ty) {
    if (ty < 0 || ty >= map.length || tx < 0 || tx >= map[ty].length) {
      return Optional.empty();
    }

    if (dist[ty][tx] < Integer.MAX_VALUE) {
      return Optional.empty();
    }

    if (map[ty][tx] == -1) {
      return Optional.empty();
    }

    return Optional.of(map[ty][tx]);
  }

  private static Table<Integer, Integer, Integer> getDistances(int[][] map) {
    Table<Integer, Integer, Integer> distances = HashBasedTable.create();
    Map<Integer, Point> landmarks = getLandmarks(map);
    Optional<Integer> spot;

    for (Map.Entry<Integer, Point> entry : landmarks.entrySet()) {
      Point cur = entry.getValue();
      int curX = cur.getX();
      int curY = cur.getY();
      int origin = entry.getKey();

      int[][] dist = new int[map.length][map[0].length];
      for (int y = 0; y < dist.length; y++) {
        Arrays.fill(dist[y], Integer.MAX_VALUE);
      }

      List<Point> queue = new ArrayList<>();
      dist[curY][curX] = 0;
      queue.add(cur);

      while (!queue.isEmpty()) {
        cur = queue.remove(0);
        int cx = cur.getX();
        int cy = cur.getY();

        spot = check(map, dist, cx - 1, cy);
        if (spot.isPresent()) {
          int curDist = dist[cy][cx] + 1;
          dist[cy][cx - 1] = curDist;

          if (spot.get() == 1) {
            queue.add(Point.create(cx - 1, cy));
          } else {
            distances.put(origin, spot.get(), curDist);
          }
        }
        spot = check(map, dist, cx + 1, cy);
        if (spot.isPresent()) {
          int curDist = dist[cy][cx] + 1;
          dist[cy][cx + 1] = curDist;

          if (spot.get() == 1) {
            queue.add(Point.create(cx + 1, cy));
          } else {
            distances.put(origin, spot.get(), curDist);
          }
        }
        spot = check(map, dist, cx, cy - 1);
        if (spot.isPresent()) {
          int curDist = dist[cy][cx] + 1;
          dist[cy - 1][cx] = curDist;

          if (spot.get() == 1) {
            queue.add(Point.create(cx, cy - 1));
          } else {
            distances.put(origin, spot.get(), curDist);
          }
        }
        spot = check(map, dist, cx, cy + 1);
        if (spot.isPresent()) {
          int curDist = dist[cy][cx] + 1;
          dist[cy + 1][cx] = curDist;

          if (spot.get() == 1) {
            queue.add(Point.create(cx, cy + 1));
          } else {
            distances.put(origin, spot.get(), curDist);
          }
        }
      }
    }

    return distances;
  }

  private static Map<Integer, Point> getLandmarks(int[][] map) {
    Map<Integer, Point> landmarks = new HashMap<>();
    for (int y = 0; y < map.length; y++) {
      for (int x = 0; x < map[y].length; x++) {
        if (map[y][x] != 1 && map[y][x] != -1) {
          landmarks.put(map[y][x], Point.create(x, y));
        }
      }
    }
    return landmarks;
  }
}
