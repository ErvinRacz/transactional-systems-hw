package app.models.operands;

import java.io.Serializable;
import java.util.Objects;

public abstract class Operand implements Serializable, Comparable<Operand> {
    private static final long serialVersionUID = 1L;

    public static enum Type {
        DATA, TRANSACTION
    }

    private String name;
    private Type type;

    public Operand() {
        super();
    }

    /**
     * @param type   - type of the operand
     * @param name - name of the operand
     */
    public Operand(Type type, String name) {
        super();
        this.setName(name);
        this.setType(type);
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
    public int compareTo(Operand other) {
        return getName().compareTo(other.getName());
    }

    @Override
    public String toString() {
        return getName();
    }

    // #endregion

    // #region Getters and setters
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setName(String symbol) {
        this.name = symbol;
    }

    public String getName() {
        return this.name;
    }
    // #endregion
}