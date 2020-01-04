package app;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

class PermutationProvider<T> implements Runnable {

    private Function<List<T>, Boolean> delegate;
    private boolean ignoreElement;
    private int nrOfElements;
    private List<T> elements;
    private T ignoredElement;

    public PermutationProvider() {
        super();
    }

    public PermutationProvider(boolean ignoreElement, Function<List<T>, Boolean> delegate) {
        super();
        this.setIgnoreElement(ignoreElement);
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
            delegate.apply(e);
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
        if (isIgnoreElement()) {
            // We don't want to return the same list reference at each time, therefore
            // create a new list as soon as we have a permutation.
            permutateIgnoringFirst(nrOfElements, new LinkedList<>(elements), this.ignoredElement);
        } else {
            // same as above here
            permutate(nrOfElements, new LinkedList<>(elements));
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

    public boolean isIgnoreElement() {
        return ignoreElement;
    }

    public void setIgnoreElement(boolean ignoreElement) {
        this.ignoreElement = ignoreElement;
    }
    // #endregion
}