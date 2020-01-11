package app.assessor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import app.assessor.aspects.Aspect;
import app.models.Operation;
import app.models.operands.Operand;
import app.models.operands.Operand.Type;
import app.models.operands.SymbolicData;
import app.models.operands.Transaction;

/**
 * Generates step graph of a schedule.
 * 
 * @param schedule
 */
public class Assessor {

    private Graph<Operation, DefaultEdge> stepGraph;
    private Set<SymbolicData> symbolicDataSet;
    private List<Operation> schedule;
    private List<Triple<Operand, String, Operand>> liveReadFromRealations;
    private List<Triple<Operand, String, Operand>> readFromRealations;

    public Assessor() {
        super();
    }

    public Assessor(List<Operation> schedule) {
        super();
        this.setSchedule(schedule);
        this.setStepGraph(schedule);
    }

    /**
     * Generate step graph which can be later used to for further analyzing the
     * schedule.
     * 
     * @param schedule
     * @return
     */
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

        // adding the initial writes and final reads steps (transaction 0 and inifite)
        symbolicDataSet.forEach(data -> {
            g.addVertex(new Operation(Operation.Type.WRITE, data, new Transaction("0")));
            var read = new Operation(Operation.Type.READ, data, new Transaction("oo"));
            read.setLive(true);
            g.addVertex(read);
            schedule.add(read);
        });

        // creating the edges in the step graph
        for (int i = schedule.size() - 1; i >= 1; i--) {
            if (schedule.get(i).getType() == Operation.Type.READ) {
                for (int j = i - 1; j >= 0; j--) {
                    if (schedule.get(j).getType() == Operation.Type.WRITE
                            && schedule.get(j).getOperand().equals(schedule.get(i).getOperand())) {
                        g.addEdge(schedule.get(j), schedule.get(i));
                        // we can brake out of the for loop since the input of a read operation can be
                        // only one write operation
                        j = -1;
                    }
                }
            } else if (schedule.get(i).getType() == Operation.Type.WRITE) {
                for (int j = i - 1; j >= 0; j--) {
                    if (schedule.get(j).getType() == Operation.Type.READ
                            && schedule.get(j).getTransaction().equals(schedule.get(i).getTransaction())) {
                        g.addEdge(schedule.get(j), schedule.get(i));
                        // we want to go to the end of the for loop since a write operation might be
                        // based on multiple read operation
                    }
                }
            }
        }
        return g;
    }

    /**
     * Verifies a schedule in regards of a specified aspect.
     * 
     * @param aspect
     */
    public boolean verifies(Aspect aspect) {
        return aspect.assess(schedule, stepGraph, liveReadFromRealations, symbolicDataSet);
    }

    public void createLiveReadFromRelationList() {
        findLiveOperations();
        setLiveReadFromRealations(createLiveReadFromList());
    }

    public void createReadFromRelationList() {
        // findLiveOperations();
        setReadFromRealations(createReadFromList());
    }

    private void findLiveOperations() {
        schedule.subList(schedule.size() - symbolicDataSet.size(), schedule.size()).forEach(finalOp -> {
            var pathfinder = new DijkstraShortestPath<>(stepGraph);
            var paths = pathfinder.getPaths(finalOp);
            schedule.subList(0, schedule.size() - symbolicDataSet.size()).forEach(op -> {
                var path = paths.getPath(op);
                if (path != null && path.getLength() > 0) {
                    op.setLive(true);
                }
            });
        });
    }

    private ArrayList<Triple<Operand, String, Operand>> createLiveReadFromList() {
        var liveReadFromRelations = new ArrayList<Triple<Operand, String, Operand>>();
        for (int i = 0; i < schedule.size(); i++) {
            if (schedule.get(i).getType().equals(Operation.Type.READ) && schedule.get(i).isLive()) {
                int j = i - 1;
                do {
                    if (j <= 0) {
                        liveReadFromRelations.add(Triple.of(new Transaction("0"),
                                schedule.get(i).getOperand().getName(), schedule.get(i).getTransaction()));
                    } else if (schedule.get(j).getType().equals(Operation.Type.WRITE)
                            && schedule.get(j).getOperand().equals(schedule.get(i).getOperand())) {
                        liveReadFromRelations.add(Triple.of(schedule.get(j).getTransaction(),
                                schedule.get(i).getOperand().getName(), schedule.get(i).getTransaction()));
                        j = -1;
                    }
                    j--;
                } while (j >= 0);
            }
        }

        return liveReadFromRelations;
    }

    private ArrayList<Triple<Operand, String, Operand>> createReadFromList() {
        var readFromRelations = new ArrayList<Triple<Operand, String, Operand>>();
        for (int i = 0; i < schedule.size(); i++) {
            if (schedule.get(i).getType().equals(Operation.Type.READ)) {
                int j = i - 1;
                do {
                    if (j <= 0) {
                        readFromRelations.add(Triple.of(new Transaction("0"), schedule.get(i).getOperand().getName(),
                                schedule.get(i).getTransaction()));
                    } else if (schedule.get(j).getType().equals(Operation.Type.WRITE)
                            && schedule.get(j).getOperand().equals(schedule.get(i).getOperand())) {
                        readFromRelations.add(Triple.of(schedule.get(j).getTransaction(),
                                schedule.get(i).getOperand().getName(), schedule.get(i).getTransaction()));
                        j = -1;
                    }
                    j--;
                } while (j >= 0);
            }
        }

        return readFromRelations;
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

    public List<Triple<Operand, String, Operand>> getReadFromRealations() {
        return this.readFromRealations;
    }

    public void setReadFromRealations(List<Triple<Operand, String, Operand>> readFromRealations) {
        this.readFromRealations = readFromRealations;
    }

    // #endregion
}