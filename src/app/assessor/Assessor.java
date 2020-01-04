package app.assessor;

import java.util.HashSet;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import app.models.Operation;
import app.models.operands.Operand.Type;
import app.models.operands.SymbolicData;
import app.models.operands.Transaction;

/**
 * Generates step graph.
 * 
 * @param schedule
 */
public class Assessor {

    private Graph<Operation, DefaultEdge> stepGraph;

    public Assessor() {
        super();
    }

    public Assessor(List<Operation> schedule) {
        super();
        stepGraph = generateStepGraph(schedule);
    }

    private Graph<Operation, DefaultEdge> generateStepGraph(List<Operation> schedule) {
        Graph<Operation, DefaultEdge> g = new Multigraph<>(DefaultEdge.class);

        var symbolicDataSet = new HashSet<SymbolicData>();
        for (Operation op : schedule) {
            if (op.getOperand() != null && op.getOperand().getType() == Type.DATA) {
                symbolicDataSet.add(op.getOperand());
            }
            g.addVertex(op);
        }

        // adding the initial writes and final reads steps
        symbolicDataSet.forEach(data -> {
            g.addVertex(new Operation(Operation.Type.WRITE, data, new Transaction("0")));
            g.addVertex(new Operation(Operation.Type.READ, data, new Transaction("oo")));
        });

        for (int i = schedule.size() - 1; i >= 0; i--) {
            if (schedule.get(i).getType() == Operation.Type.READ) {
                for (int j = i - 1; j >= 0; j--) {
                    if (schedule.get(j).getType() == Operation.Type.WRITE
                            && schedule.get(j).getOperand().equals(schedule.get(i).getOperand())) {
                        g.addEdge(schedule.get(j), schedule.get(i));
                        // we can breake out of the inner for loop
                        j = -1;
                    }
                }
            } else if (schedule.get(i).getType() == Operation.Type.WRITE) {
                for (int j = i - 1; j >= 0; j--) {
                    if (schedule.get(j).getType() == Operation.Type.READ
                            && schedule.get(j).getTransaction().equals(schedule.get(i).getTransaction())) {
                        g.addEdge(schedule.get(j), schedule.get(i));
                    }
                }
            }
        }

        return g;
    }

    public Graph<Operation, DefaultEdge> getStepGraph() {
        return stepGraph;
    }

    public void setStepGraph(Graph<Operation, DefaultEdge> stepGraph) {
        this.stepGraph = stepGraph;
    }
}