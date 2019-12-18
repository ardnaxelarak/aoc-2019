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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Puzzle18b {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    List<String> inputs = new ArrayList<>();
    while (sc.hasNextLine()) {
      inputs.add(sc.nextLine());
    }

    int[][] map =
        inputs.stream()
            .map(line -> line.chars().map(Puzzle18b::getValue).toArray())
            .toArray(int[][]::new);

    int start = 0;
    for (int y = 0; y < map.length; y++) {
      for (int x = 0; x < map[y].length; x++) {
        if (map[y][x] == 0) {
          map[y][x] = start++;
        }
      }
    }

    // assume 4 robots
    if (start != 4) {
      throw new IllegalStateException();
    }

    Table<Integer, Integer, Integer> distances = getDistances(map);

    Set<Integer> neededKeys =
        distances.rowKeySet().stream()
            .filter(loc -> loc > 5)
            .collect(ImmutableSet.toImmutableSet());

    int[] locs = new int[] {0, 1, 2, 3};
    State state = new State(locs, 0, neededKeys);
    List<State> queue = new ArrayList<>();
    queue.add(state);
    State sol = null;

    while (!queue.isEmpty()) {
      state = queue.remove(0);
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
    private int[] curLocs;
    private int distance;
    private ImmutableSet<Integer> keysNeeded;

    public State(int[] curLocs, int distance, Set<Integer> keysNeeded) {
      this.curLocs = curLocs;
      this.distance = distance;
      this.keysNeeded = ImmutableSet.copyOf(keysNeeded);
    }

    public List<State> getNextSteps(Table<Integer, Integer, Integer> distances) {
      List<State> nextSteps = new ArrayList<>();
      for (int i = 0; i < curLocs.length; i++) {
        Map<Integer, Integer> visited = new HashMap<>();
        Map<Integer, Integer> unvisited = new HashMap<>();
        unvisited.put(curLocs[i], distance);

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

        for (Map.Entry<Integer, Integer> entry : visited.entrySet()) {
          if (!keysNeeded.contains(entry.getKey())) {
            continue;
          }

          nextSteps.add(
              new State(
                  getNewLocs(curLocs, i, entry.getKey()),
                  entry.getValue(),
                  getKeys(entry.getKey())));
        }
      }

      return nextSteps;
    }

    private Set<Integer> getKeys(int collected) {
      return keysNeeded.stream().filter(key -> key != collected).collect(ImmutableSet.toImmutableSet());
    }

    private int[] getNewLocs(int[] curLocs, int toUpdate, int newValue) {
      int[] newLocs = Arrays.copyOf(curLocs, curLocs.length);
      newLocs[toUpdate] = newValue;
      return newLocs;
    }

    public int getDistance() {
      return distance;
    }

    public Set<Integer> getKeysNeeded() {
      return keysNeeded;
    }

    public boolean sameBase(State other) {
      return Arrays.equals(curLocs, other.curLocs) && keysNeeded.equals(other.keysNeeded);
    }
  }

  private static int getValue(int c) {
    if (c == '@') {
      return 0;
    } else if (c == '.') {
      return 5;
    } else if (c == '#') {
      return -5;
    } else if (c >= 'a' && c <= 'z') {
      return c - 'a' + 6;
    } else if (c >= 'A' && c <= 'Z') {
      return -(c - 'A' + 6);
    } else {
      throw new IllegalArgumentException("Unexpected input: " + c);
    }
  }

  private static class Point {
    private int x, y;

    public Point(int x, int y) {
      this.x = x;
      this.y = y;
    }

    public int getX() {
      return x;
    }

    public int getY() {
      return y;
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

          if (spot.get() == 5) {
            queue.add(new Point(cx - 1, cy));
          } else {
            distances.put(origin, spot.get(), curDist);
          }
        }
        spot = check(map, dist, cx + 1, cy);
        if (spot.isPresent()) {
          int curDist = dist[cy][cx] + 1;
          dist[cy][cx + 1] = curDist;

          if (spot.get() == 5) {
            queue.add(new Point(cx + 1, cy));
          } else {
            distances.put(origin, spot.get(), curDist);
          }
        }
        spot = check(map, dist, cx, cy - 1);
        if (spot.isPresent()) {
          int curDist = dist[cy][cx] + 1;
          dist[cy - 1][cx] = curDist;

          if (spot.get() == 5) {
            queue.add(new Point(cx, cy - 1));
          } else {
            distances.put(origin, spot.get(), curDist);
          }
        }
        spot = check(map, dist, cx, cy + 1);
        if (spot.isPresent()) {
          int curDist = dist[cy][cx] + 1;
          dist[cy + 1][cx] = curDist;

          if (spot.get() == 5) {
            queue.add(new Point(cx, cy + 1));
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
        if (map[y][x] != 5 && map[y][x] != -5) {
          landmarks.put(map[y][x], new Point(x, y));
        }
      }
    }
    return landmarks;
  }
}
