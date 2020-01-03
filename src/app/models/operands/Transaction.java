package app.models.operands;

public final class Transaction extends Operand {
    private static final long serialVersionUID = 1L;

    public Transaction() {
        super();
    }

    public Transaction(String name) {
        super(Operand.Type.TRANSACTION, name);
    }
}