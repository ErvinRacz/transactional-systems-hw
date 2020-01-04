package app.models;

import java.io.Serializable;
import java.util.Objects;

import app.models.operands.SymbolicData;
import app.models.operands.Transaction;

public class Operation implements Serializable {
    private static final long serialVersionUID = 1L;

    public static enum Type {
        READ, WRITE, COMMIT, ABORT
    }

    private Type type;
    private SymbolicData operand;
    private Transaction transaction;
    private boolean live;

    public Operation() {
        super();
    }

    /**
     * @param type    Type - type of operation
     * @param Operand operand - the subject of an operation
     * @param Operand transaction - that the operation is being part of
     */
    public Operation(Type type, SymbolicData operand, Transaction transaction) {
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

    @Override
    public String toString() {
        return "{" + getType().toString().charAt(0) + "" + getTransaction() + "" + getOperand() + "-" + isLive() + "}";
    }
    // #endregion

    // #region Getters and Setters
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public SymbolicData getOperand() {
        return this.operand;
    }

    public void setOperand(SymbolicData operand) {
        this.operand = operand;
    }

    public Transaction getTransaction() {
        return this.transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }
    // #endregion

}