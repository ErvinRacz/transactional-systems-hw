package app;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * The delegate function
 * represents an interface through which the user can define further operations
 * to be done on the permutations.
 */
public class PermutationProvider<T> implements Runnable {

    private Function<List<T>, Boolean> delegate;
    private int nrOfElements;
    private List<T> elements;

    public PermutationProvider() {
        super();
    }

    public PermutationProvider(Function<List<T>, Boolean> delegate) {
        super();
        this.setDelegate(delegate);
    }

    private void permutate(int n, List<T> e) {
        permutateIgnoringFirst(n, e, null);
    }

    private void permutateIgnoringFirst(int n, List<T> e, T ignoredElement) {
        if (n == 1) {
            delegate.apply(new ArrayList<>(e));
        } else {
            for (int i = 0; i < n - 1; i++) {
                permutateIgnoringFirst(n - 1, e, ignoredElement);
                if (n % 2 == 0) {
                    swap(e, i, n - 1);
                } else {
                    swap(e, 0, n - 1);
                }
            }
            permutateIgnoringFirst(n - 1, e, ignoredElement);
        }
    }

    @Override
    public void run() {
        permutate(nrOfElements, elements);

    }

    /**
     * Swap two element of a list
     * 
     * @param <T>
     * @param input
     * @param a
     * @param b
     */
    public static <T> void swap(List<T> input, int a, int b) {
        final List<T> l = input;
        l.set(a, l.set(b, l.get(a)));
    }

    // #region Getters and Setters
    public Function<List<T>, Boolean> getDelegate() {
        return delegate;
    }

    public void setDelegate(Function<List<T>, Boolean> delegate) {
        this.delegate = delegate;
    }

    public int getNrOfElements() {
        return nrOfElements;
    }

    public void setNrOfElements(int nrOfElements) {
        this.nrOfElements = nrOfElements;
    }

    public List<T> getElements() {
        return elements;
    }

    public void setElements(List<T> elements) {
        this.elements = elements;
    }

    // #endregion
}