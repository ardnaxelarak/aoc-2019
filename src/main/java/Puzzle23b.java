import com.google.auto.value.AutoValue;
import com.google.common.base.*;
import com.google.common.collect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

public class Puzzle23b {
  private static final int COMPUTERS = 50;

  public static void main(String[] args) {
    Intcode computer = Intcode.fromStdIn();
    NetworkProcessor processor = new NetworkProcessor();
    RunnableIntcode[] runnableComputers = new RunnableIntcode[COMPUTERS];
    NetworkProcessor.NetworkIO[] ios = new NetworkProcessor.NetworkIO[COMPUTERS];
    Thread[] threads = new Thread[COMPUTERS];

    for (int i = 0; i < COMPUTERS; i++) {
      ios[i] = processor.addComputer(i);
      runnableComputers[i] = new RunnableIntcode(computer, ios[i]);
      threads[i] = new Thread(runnableComputers[i]);
      threads[i].start();
    }

    NetworkProcessor.NAT nat = processor.getNAT();
    Thread thread = new Thread(nat);
    thread.start();
  }

  @AutoValue
  static abstract class Packet {
    public abstract int getDestination();
    public abstract long getX();
    public abstract long getY();

    public static Packet create(int destination, long x, long y) {
      return new AutoValue_Puzzle23b_Packet(destination, x, y);
    }
  }

  public static class NetworkProcessor {
    private Map<Integer, BlockingQueue<Packet>> buffer;
    private NAT nat;
    private Map<Integer, NetworkIO> computers;

    public NetworkProcessor() {
      buffer = new HashMap<>();
      computers = new HashMap<>();
      for (int i = 0; i < COMPUTERS; i++) {
        buffer.put(i, new ArrayBlockingQueue<>(100));
      }
      nat = new NAT();
    }

    public NAT getNAT() {
      return nat;
    }

    public NetworkIO addComputer(int address) {
      NetworkIO io = new NetworkIO(address);
      computers.put(address, io);
      return io;
    }

    public boolean networkIdle() {
      for (NetworkIO io : computers.values()) {
        if (!io.isIdle()) {
          return false;
        }
      }
      return true;
    }

    public class NetworkIO implements IntcodeIO {
      private int id;
      private BlockingQueue<Long> inputs;
      private BlockingQueue<Long> outputs;
      private BlockingQueue<Packet> packets;
      private boolean idle;

      public NetworkIO(int id) {
        inputs = new ArrayBlockingQueue<>(100);
        outputs = new ArrayBlockingQueue<>(100);
        inputs.add((long) id);
        packets = buffer.get(id);
        idle = false;
      }

      public long input() {
        synchronized (inputs) {
          Long next = inputs.poll();
          if (next == null) {
            if (packets.isEmpty()) {
              idle = outputs.isEmpty();
              if (idle) {
                synchronized (nat) {
                  nat.notify();
                }
              }
              return -1;
            } else {
              Packet p = packets.remove();
              inputs.add(p.getY());
              idle = false;
              return p.getX();
            }
          } else {
            idle = false;
            return next;
          }
        }
      }

      public void output(long value) {
        synchronized (outputs) {
          idle = false;
          outputs.add(value);
          if (outputs.size() >= 3) {
            Packet packet =
                Packet.create(outputs.remove().intValue(), outputs.remove(), outputs.remove());
            if (packet.getDestination() < COMPUTERS) {
              buffer.get(packet.getDestination()).add(packet);
            } else if (packet.getDestination() == 255) {
              nat.setPacket(packet);
            } else {
              System.out.println(packet);
            }
          }
        }
      }

      public boolean isIdle() {
        return idle && inputs.isEmpty() && outputs.isEmpty();
      }
    }

    public class NAT implements Runnable {
      private Packet packet;
      private boolean first = true;
      private long lastY = Long.MIN_VALUE;

      public void setPacket(Packet packet) {
        this.packet = packet;
      }

      @Override
      public void run() {
        synchronized (this) {
          while (true) {
            if (networkIdle() && packet != null) {
              if (first && lastY == packet.getY()) {
                System.out.println(lastY);
                first = false;
              }
              lastY = packet.getY();
              Packet newPacket = Packet.create(0, packet.getX(), packet.getY());
              buffer.get(0).add(newPacket);
              System.err.printf("Sending %s\n", newPacket);
            }
            try {
              this.wait();
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
  }
}
