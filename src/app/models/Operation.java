package app.models;

import java.io.Serializable;
import java.util.Objects;

public class Operation implements Serializable {
    private static final long serialVersionUID = 1L;

    public static enum Type {
        READ, WRITE, COMMIT, ABORT
    }

    private Type type;
    private Operand operand;
    private Operand transaction;

    public Operation() {
        super();
    }

    /**
     * @param type    Type - type of operation
     * @param Operand operand - the subject of an operation
     * @param Operand transaction - that the operation is being part of
     */
    public Operation(Type type, Operand operand, Operand transaction) {
        super();
        this.setType(type);
        this.setOperand(operand);
        this.setTransaction(transaction);
    }

    // #region Overrides
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Operation)) {
            return false;
        }
        Operation operation = (Operation) o;
        return Objects.equals(type, operation.type) && Objects.equals(operand, operation.operand)
                && Objects.equals(transaction, operation.transaction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, operand, transaction);
    }
    // #endregion

    // #region Getters and Setters
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Operand getOperand() {
        return operand;
    }

    public void setOperand(Operand operand) {
        this.operand = operand;
    }

    public Operand getTransaction() {
        return transaction;
    }

    public void setTransaction(Operand transaction) {
        this.transaction = transaction;
    }
    // #endregion
}