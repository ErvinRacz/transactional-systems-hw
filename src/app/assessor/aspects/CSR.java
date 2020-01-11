package app.assessor.aspects;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import app.models.Operation;
import app.models.operands.Operand;
import app.models.operands.SymbolicData;

public class CSR implements Aspect {

    private DirectedAcyclicGraph<Operation, DefaultEdge> conflictGraph = new DirectedAcyclicGraph<Operation, DefaultEdge>(
            DefaultEdge.class);

    @Override
    public boolean assess(List<Operation> schedule, Graph<Operation, DefaultEdge> stepGraph,
            List<Triple<Operand, String, Operand>> liveReadFromRealations, Set<SymbolicData> symbolicDataSet) {
        schedule = new ArrayList<Operation>(schedule.subList(0, schedule.size() - symbolicDataSet.size()));
        schedule.forEach(op -> conflictGraph.addVertex(op));
        return isCSR(schedule);
    }

    /**
     * Checks if the history is commit serializable
     *
     * @return
     */
    public boolean isCSR(List<Operation> schedule) {
        for (int i = 0; i < schedule.size(); i++) {
            Operation first = schedule.get(i);
            for (int j = i + 1; j < schedule.size(); j++) {
                Operation second = schedule.get(j);
                if (isConflict(first, second)) {
                    try {
                        // add conflict to the graph
                        conflictGraph.addEdge(first, second);
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean isConflict(Operation first, Operation second) {
        if (!first.getTransaction().equals(second.getTransaction())) {
            if (first.getType() == Operation.Type.WRITE || second.getType() == Operation.Type.WRITE) {
                if (first.getOperand() != null && first.getOperand().equals(second.getOperand())) {
                    return true;
                }
            }
        }
        return false;
    }

    public DirectedAcyclicGraph<Operation, DefaultEdge> getConflictGraph() {
        return this.conflictGraph;
    }
}