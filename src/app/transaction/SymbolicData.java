package app.transaction;

import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public final class SymbolicData extends Operand {
    private static final long serialVersionUID = 1L;
    private static final String allowedChars = "xyz";

    public SymbolicData() {
        super();
    }

    public SymbolicData(final char name) {
        super(Operand.Type.SYMBOLIC_DATA, name);
        if (!StringUtils.contains(allowedChars, name))
            throw new RuntimeException(
                    "Symbol can be created only with the following names: " + allowedChars);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Operand)) {
            return false;
        }
        final Operand operand = (Operand) o;
        return Objects.equals(getName(), operand.getName()) && Objects.equals(getType(), operand.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getType());
    }
}