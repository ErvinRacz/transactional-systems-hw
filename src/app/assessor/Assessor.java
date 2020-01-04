package app.assessor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;
import org.jgrapht.traverse.DepthFirstIterator;

import app.models.Operation;
import app.models.operands.Operand.Type;
import app.models.operands.Operand;
import app.models.operands.SymbolicData;
import app.models.operands.Transaction;

/**
 * Generates step graph.
 * 
 * @param schedule
 */
public class Assessor {

    private Graph<Operation, DefaultEdge> stepGraph;
    private Set<SymbolicData> symbolicDataSet;
    private List<Operation> schedule;
    private List<Triple<Operand, String, Operand>> liveReadFromRealations;

    public Assessor() {
        super();
    }

    public Assessor(List<Operation> schedule) {
        super();
        this.setSchedule(schedule);
        this.setStepGraph(schedule);
    }

    private Graph<Operation, DefaultEdge> generateStepGraph(List<Operation> schedule) {
        Graph<Operation, DefaultEdge> g = new Multigraph<>(DefaultEdge.class);

        symbolicDataSet = new HashSet<SymbolicData>();
        for (Operation op : schedule) {
            if (op.getOperand() != null && op.getOperand().getType() == Type.DATA) {
                symbolicDataSet.add(op.getOperand());
            }
            g.addVertex(op);
            op.setLive(false);
        }

        // adding the initial writes and final reads steps
        symbolicDataSet.forEach(data -> {
            g.addVertex(new Operation(Operation.Type.WRITE, data, new Transaction("0")));
            var read = new Operation(Operation.Type.READ, data, new Transaction("oo"));
            g.addVertex(read);
            schedule.add(read);
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

    public void createLiveReadFromRelationList() {
        findLiveOperations();
        setLiveReadFromRealations(createReadFromList());
    }

    private void findLiveOperations() {
        schedule.subList(schedule.size() - 1 - symbolicDataSet.size(), schedule.size()).forEach(finalOp -> {
            Iterator<Operation> iterator = new DepthFirstIterator<>(stepGraph, finalOp);
            while (iterator.hasNext()) {
                iterator.next().setLive(true);
            }
        });
    }

    private ArrayList<Triple<Operand, String, Operand>> createReadFromList() {
        var liveReadFromRelations = new ArrayList<Triple<Operand, String, Operand>>();
        for (int i = 0; i < schedule.size(); i++) {
            if (schedule.get(i).getType().equals(Operation.Type.READ) && schedule.get(i).isLive()) {
                int j = i - 1;
                do {
                    if (j <= 0) {
                        liveReadFromRelations.add(Triple.of(new Transaction("0"),
                                schedule.get(i).getOperand().getSymbol(), schedule.get(i).getTransaction()));
                    } else if (schedule.get(j).getType().equals(Operation.Type.WRITE)
                            && schedule.get(j).getOperand().equals(schedule.get(i).getOperand())) {
                        liveReadFromRelations.add(Triple.of(schedule.get(j).getTransaction(),
                                schedule.get(i).getOperand().getSymbol(), schedule.get(i).getTransaction()));
                        j = -1;
                    }
                    j--;
                } while (j >= 0);
            }
        }

        return liveReadFromRelations;
    }

    // #region Getters and Setters

    public Graph<Operation, DefaultEdge> getStepGraph() {
        return stepGraph;
    }

    public void setStepGraph(Graph<Operation, DefaultEdge> stepGraph) {
        this.stepGraph = stepGraph;
    }

    public void setStepGraph(List<Operation> schedule) {
        this.stepGraph = generateStepGraph(schedule);
    }

    public Set<SymbolicData> getSymbolicDataSet() {
        return this.symbolicDataSet;
    }

    public void setSymbolicDataSet(Set<SymbolicData> symbolicDataSet) {
        this.symbolicDataSet = symbolicDataSet;
    }

    public List<Operation> getSchedule() {
        return this.schedule;
    }

    public void setSchedule(List<Operation> schedule) {
        this.schedule = schedule;
    }

    public List<Triple<Operand, String, Operand>> getLiveReadFromRealations() {
        return liveReadFromRealations;
    }

    public void setLiveReadFromRealations(List<Triple<Operand, String, Operand>> liveReadFromRealations) {
        this.liveReadFromRealations = liveReadFromRealations;
    }
    // #endregion
}