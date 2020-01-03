package app;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

class PermutationProvider<T> {

    private Function<List<T>, Boolean> delegate;

    public PermutationProvider() {
        super();
    }

    public PermutationProvider(Function<List<T>, Boolean> delegate) {
        super();
        this.setDelegate(delegate);
    }

    /**
     * TODO: provide description
     *
     * @param input
     */
    public void permutate(int n, List<T> elements) {
        if (n == 1) {
            delegate.apply(new ArrayList<>(elements));
        } else {
            for (int i = 0; i < n - 1; i++) {
                permutate(n - 1, elements);
                if (n % 2 == 0) {
                    swap(elements, i, n - 1);
                } else {
                    swap(elements, 0, n - 1);
                }
            }
            permutate(n - 1, elements);
        }
    }

    private void swap(List<T> input, int a, int b) {
        T tmp = input.get(a);
        input.set(a, input.get(b));
        input.set(b, tmp);
    }

    // #region Getters and Setters
    public Function<List<T>, Boolean> getDelegate() {
        return delegate;
    }

    public void setDelegate(Function<List<T>, Boolean> delegate) {
        this.delegate = delegate;
    }
    // #endregion
}