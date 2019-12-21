import java.util.Arrays;

public class Permutations {
  public interface PermutationEmitter<T> {
    void emit(T[] permutation);
  }

  public static <T> void listPermutations(T[] array, PermutationEmitter<T> emitter) {
    int n = array.length;
    int[] indexes = new int[n];
    Arrays.fill(indexes, 0);

    T[] mutable = Arrays.copyOf(array, n);

    emitter.emit(Arrays.copyOf(mutable, n));

    int i = 0;
    while (i < n) {
      if (indexes[i] < i) {
        swap(mutable, i % 2 == 0 ? 0 : indexes[i], i);
        emitter.emit(Arrays.copyOf(mutable, n));
        indexes[i]++;
        i = 0;
      } else {
        indexes[i] = 0;
        i++;
      }
    }
  }

  private static <T> void swap(T[] array, int i1, int i2) {
    T temp = array[i1];
    array[i1] = array[i2];
    array[i2] = temp;
  }
}
