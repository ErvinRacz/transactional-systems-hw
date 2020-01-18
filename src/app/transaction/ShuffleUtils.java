package app.transaction;

import java.util.ArrayList;
import java.util.List;

public class ShuffleUtils {

    private static int getCombinationSize(int setSize, int chooseSize) {
        return getCombinationSize(setSize, chooseSize, true);
    }

    private static int getCombinationSize(int setSize, int chooseSize, boolean opt) {
        if (chooseSize == 0 || setSize <= chooseSize) {
            return 1;
        } else if (chooseSize + 1 == setSize || chooseSize == 1) {
            return setSize;
        } else if (chooseSize + 2 == setSize || chooseSize == 2) {
            return (setSize * (setSize - 1)) >> 1;
        } else if (opt) {
            long upper = setSize;
            long lower = chooseSize;
            for (int i = 1; i < chooseSize; ++i) {
                upper *= setSize - i;
                lower *= chooseSize - i;
            }
            return (int) (upper / lower);
        }
        return getCombinationSize(setSize - 1, chooseSize - 1, opt) + getCombinationSize(setSize - 1, chooseSize, opt);
    }

    private static void getCombination(int[] array, int setSize, int chooseSize, int index) {
        getCombination(array, 0, setSize, chooseSize, index);
    }

    private static void getCombination(int[] array, int begin, int setSize, int chooseSize, int index) {
        int offset = 0;
        for (int value = 0; value < setSize && offset < chooseSize; ++value) {
            final int threshold = getCombinationSize(setSize - value - 1, chooseSize - offset - 1);
            if (index < threshold) {
                array[begin + offset++] = value;
            } else if (threshold <= index) {
                index -= threshold;
            }
        }
    }

    public static <T> List<List<T>> shuffleProduct(List<T> x1, List<T> x2) {
        return shuffleProduct(x1, 0, x1.size(), x2, 0, x2.size());
    }

    public static <T> List<List<T>> shuffleProduct(List<T> x1, int begin1, int end1, List<T> x2, int begin2, int end2) {
        final int n1 = end1 - begin1;
        final int n2 = end2 - begin2;
        final int nProduct = n1 + n2;
        final int nTrem = getCombinationSize(nProduct, n1);
        final int[] indices = new int[n1];
        final List<List<T>> output = new ArrayList<List<T>>(nTrem);
        for (int i = 0; i < nTrem; ++i) {
            getCombination(indices, nProduct, n1, i);
            int last2 = 0;
            int lastOutput = 0;
            var term = new ArrayList<T>(nProduct);
            for (int k = 0; k < nProduct; k++) {
                term.add(null);
            }
            for (int k = 0; k < n1; ++k) {
                final int index = indices[k];
                for (; lastOutput < index;) {
                    term.set(lastOutput++, x2.get(begin2 + last2++));
                }
                term.set(lastOutput++, x1.get(begin1 + k));
            }
            for (; lastOutput < nProduct;) {
                term.set(lastOutput++, x2.get(begin2 + last2++));
            }
            output.add(term);
        }
        return output;
    }
}