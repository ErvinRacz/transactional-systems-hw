package app.transaction;

import java.io.Serializable;
import java.util.Objects;

public abstract class Operand implements Serializable {
    private static final long serialVersionUID = 1L;

    public static enum Type {
        SYMBOLIC_DATA, TRANSACTION
    }

    private char name;
    private Type type;

    public Operand() {
        super();
    }

    /**
     * @param type - type of the operand
     * @param name - name of the operand
     */
    public Operand(Type type, char name) {
        super();
        this.setName(name);
        this.setType(type); 
    }

    public static <T extends Operand> T copyOf(T operandToCopy) {
        switch (operandToCopy.getType()) {
        case SYMBOLIC_DATA:
            return (T) new SymbolicData(operandToCopy.getName());
        case TRANSACTION:
            return (T) new Transaction(operandToCopy.getName());
        default:
            return null;
        }
    }

    // #region Overrides
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Operand)) {
            return false;
        }
        Operand operand = (Operand) o;
        return Objects.equals(name, operand.name) && Objects.equals(type, operand.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return getName() + "";
    }

    // #endregion

    // #region Getters and setters
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setName(char name) {
        this.name = name;
    }

    public char getName() {
        return this.name;
    }
    // #endregion
}