import com.google.auto.value.AutoValue;
import com.google.common.base.*;
import com.google.common.collect.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

public class Puzzle25a {
  // solution was to hold coin, spool of cat6, fixed point, and sand
  private static final String[] ITEMS =
      new String[] {
          "coin",
          "spool of cat6",
          "fixed point",
          "sand",
          "asterisk",
          "easter egg",
          "shell",
          "hypercube",
      };

  public static void main(String[] args) throws IOException {
    Intcode computer = Intcode.from(new Scanner(new File("inputs/p25.txt")));
    SequenceInputStream stream =
        new SequenceInputStream(new FileInputStream("inputs/p25-collect.txt"), System.in);
    AsciiIO io = new AsciiIO(true, stream);
    computer.execute(io);
  }
}
