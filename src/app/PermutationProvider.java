package app;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * PermutationProvider implements the Runnable interface which implies that it
 * can be assigned to thread executor services => Possibility to be paralelly
 * executed along with other PermuationProviders. The delegate function
 * represents an interface through which the user can define further operations
 * to be done on the permutations.
 */
class PermutationProvider<T> implements Runnable {

    private Function<List<T>, Boolean> delegate;
    private boolean ignoreFirstElement;
    private int nrOfElements;
    private List<T> elements;
    private T ignoredElement;

    public PermutationProvider() {
        super();
    }

    public PermutationProvider(boolean ignoreElement, Function<List<T>, Boolean> delegate) {
        super();
        this.setIgnoreFirstElement(ignoreElement);
        this.setDelegate(delegate);
    }

    private void permutate(int n, LinkedList<T> e) {
        permutateIgnoringFirst(n, e, null);
    }

    private void permutateIgnoringFirst(int n, LinkedList<T> e, T ignoredElement) {
        if (n == 1) {
            if (ignoredElement != null) {
                e.addFirst(ignoredElement);
            }
            delegate.apply(new LinkedList<>(e));
            e.removeFirst();
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
        if (isIgnoreFirstElement()) {
            // We don't want to return the same list reference at each time, therefore
            // create a new list as soon as we have a permutation.
            permutateIgnoringFirst(nrOfElements, (LinkedList<T>) elements, this.ignoredElement);
        } else {
            // same as above here
            permutate(nrOfElements, (LinkedList<T>) elements);
        }
    }

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

    public T getIgnoredElement() {
        return ignoredElement;
    }

    public void setIgnoredElement(T ignoredElement) {
        this.ignoredElement = ignoredElement;
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

    public boolean isIgnoreFirstElement() {
        return ignoreFirstElement;
    }

    public void setIgnoreFirstElement(boolean ignoreElement) {
        this.ignoreFirstElement = ignoreElement;
    }
    // #endregion
}