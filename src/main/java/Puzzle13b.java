import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Predicate;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Puzzle13b {
  public static void main(String[] args) {
    Intcode computer = Intcode.fromStdIn();
    computer.modifyMemory(0, 2);
    AC13b io = new AC13b();
    computer.execute(io);
  }

  private static class AC13b extends JFrame implements IntcodeIO {
    private final Table<Integer, Integer, Integer> screen;
    private final BlockingQueue<Integer> inputBuffer = new ArrayBlockingQueue<>(100);
    private long score;
    private int curX, curY;
    private int state;

    public AC13b() {
      super("Puzzle 13b");
      this.screen = HashBasedTable.create();
      state = 0;
      score = 0;
      setContentPane(SCREEN_PANEL);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setSize(1000, 700);
      addKeyListener(KEY_LISTENER);
      SCREEN_PANEL.addKeyListener(KEY_LISTENER);
      setVisible(true);
    }

    public long input() {
      synchronized (inputBuffer) {
        Integer next;
        while ((next = inputBuffer.poll()) == null) {
          try {
            inputBuffer.wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        return next;
      }
    }

    public void output(long value) {
      if (state == 0) {
        curX = (int) value;
        state = 1;
      } else if (state == 1) {
        curY = (int) value;
        state = 2;
      } else if (state == 2) {
        if (curX == -1 && curY == 0) {
          score = value;
          Graphics g = SCREEN_PANEL.getGraphics();
          g.setColor(Color.white);
          g.fillRect(30, 30, getWidth() - 60, 100);
          g.setColor(Color.black);
          g.drawString(Long.toString(score), 30, 100);
        } else {
          screen.put(curX, curY, (int) value);
          Graphics g = SCREEN_PANEL.getGraphics();
          g.setColor(getColor((int) value));
          g.fillRect(30 + 10 * curX, 160 + 10 * curY, 10, 10);
        }
        state = 0;
      } else {
        throw new IllegalStateException();
      }
    }

    private final JPanel SCREEN_PANEL = new JPanel() {
      @Override
      public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.black);
        g.drawString(Long.toString(score), 30, 100);
        Set<Table.Cell<Integer, Integer, Integer>> cells = new HashSet<>(screen.cellSet());
        for (Table.Cell<Integer, Integer, Integer> cell : cells) {
          int x = cell.getRowKey();
          int y = cell.getColumnKey();
          int color = cell.getValue();
          g.setColor(getColor(color));
          g.fillRect(30 + 10 * x, 160 + 10 * y, 10, 10);
        }
      }
    };

    private final KeyAdapter KEY_LISTENER = new KeyAdapter() {
      @Override
      public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        synchronized (inputBuffer) {
          if (c == '8') {
            inputBuffer.add(-1);
            inputBuffer.notify();
          } else if (c == '9') {
            inputBuffer.add(0);
            inputBuffer.notify();
          } else if (c == '0') {
            inputBuffer.add(1);
            inputBuffer.notify();
          } else if (c == ' ') {
            int you =
                screen.cellSet().stream()
                    .filter(cell -> cell.getValue() == 3)
                    .findAny()
                    .map(Table.Cell::getRowKey)
                    .orElse(0);
            int ball =
                screen.cellSet().stream()
                    .filter(cell -> cell.getValue() == 4)
                    .findAny()
                    .map(Table.Cell::getRowKey)
                    .orElse(0);
            inputBuffer.add(sign(ball - you));
            inputBuffer.notify();
          }
        }
      }
    };

    private int sign(int value) {
      if (value < 0) {
        return -1;
      } else if (value > 0) {
        return 1;
      } else {
        return 0;
      }
    }

    private Color getColor(int pixel) {
      switch (pixel) {
        case 0:
          return Color.white;
        case 1:
          return Color.black;
        case 2:
          return Color.red;
        case 3:
          return Color.green;
        case 4:
          return Color.blue;
        default:
          throw new IllegalStateException();
      }
    }
  }
}
