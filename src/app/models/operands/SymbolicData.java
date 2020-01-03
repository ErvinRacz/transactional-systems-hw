package app.models.operands;

import java.util.HashSet;
import java.util.Set;

public final class SymbolicData extends Operand {
    private static final long serialVersionUID = 1L;
    private static final Set<String> suggestedNames = new HashSet<>();

    /**
     * This set is used to restrict the naming of the symbolic data
     */
    static {
        suggestedNames.add("x");
        suggestedNames.add("y");
        suggestedNames.add("z");
    }

    public SymbolicData() {
        super();
    }

    public SymbolicData(String name) {
        super(Operand.Type.DATA, name);
        if (!suggestedNames.contains(name))
            throw new RuntimeException(
                    "Symbol can be created only with the following names: " + suggestedNames.toString());
    }
}