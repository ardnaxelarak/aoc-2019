import com.google.auto.value.AutoValue;
import com.google.common.base.*;
import com.google.common.collect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

public class Puzzle23a {
  private static final int COMPUTERS = 50;

  public static void main(String[] args) {
    Intcode computer = Intcode.fromStdIn();
    NetworkProcessor processor = new NetworkProcessor();
    RunnableIntcode[] runnableComputers = new RunnableIntcode[COMPUTERS];
    NetworkProcessor.NetworkIO[] ios = new NetworkProcessor.NetworkIO[COMPUTERS];
    Thread[] threads = new Thread[COMPUTERS];

    for (int i = 0; i < COMPUTERS; i++) {
      ios[i] = processor.new NetworkIO(i);
      runnableComputers[i] = new RunnableIntcode(computer, ios[i]);
      threads[i] = new Thread(runnableComputers[i]);
      threads[i].start();
    }
  }

  @AutoValue
  static abstract class Packet {
    public abstract int getDestination();
    public abstract long getX();
    public abstract long getY();

    public static Packet create(int destination, long x, long y) {
      return new AutoValue_Puzzle23a_Packet(destination, x, y);
    }
  }

  public static class NetworkProcessor {
    private Map<Integer, BlockingQueue<Packet>> buffer;

    public NetworkProcessor() {
      buffer = new HashMap<>();
      for (int i = 0; i < COMPUTERS; i++) {
        buffer.put(i, new ArrayBlockingQueue<>(100));
      }
    }

    public class NetworkIO implements IntcodeIO {
      private int id;
      private BlockingQueue<Long> inputs;
      private BlockingQueue<Long> outputs;
      private BlockingQueue<Packet> packets;

      public NetworkIO(int id) {
        inputs = new ArrayBlockingQueue<>(100);
        outputs = new ArrayBlockingQueue<>(100);
        inputs.add((long) id);
        packets = buffer.get(id);
      }

      public long input() {
        synchronized (inputs) {
          Long next = inputs.poll();
          if (next == null) {
            if (packets.isEmpty()) {
              return -1;
            } else {
              Packet p = packets.remove();
              inputs.add(p.getY());
              return p.getX();
            }
          } else {
            return next;
          }
        }
      }

      public void output(long value) {
        synchronized (outputs) {
          outputs.add(value);
          if (outputs.size() >= 3) {
            Packet packet =
                Packet.create(outputs.remove().intValue(), outputs.remove(), outputs.remove());
            if (packet.getDestination() < COMPUTERS) {
              buffer.get(packet.getDestination()).add(packet);
            } else {
              System.out.println(packet);
            }
          }
        }
      }
    }
  }
}
