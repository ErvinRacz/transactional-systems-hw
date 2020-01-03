package app.models;

import java.io.Serializable;
import java.util.Objects;

public class Operand implements Serializable, Comparable<Operand> {
    private static final long serialVersionUID = 1L;

    public static enum Type {
        DATA, TRANSACTION
    }

    private String symbol;
    private Type type;

    public Operand() {
        super();
    }

    /**
     * @param type   - type of the operand
     * @param symbol - name of the operand
     */
    public Operand(Type type, String symbol) {
        super();
        this.setSymbol(symbol);
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
        return Objects.equals(symbol, operand.symbol) && Objects.equals(type, operand.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, type);
    }

    @Override
    public int compareTo(Operand other) {
        return getSymbol().compareTo(other.getSymbol());
    }
    // #endregion

    // #region Getters and setters
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return this.symbol;
    }
    // #endregion
}