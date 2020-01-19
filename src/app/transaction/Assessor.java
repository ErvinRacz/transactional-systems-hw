package app.transaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import app.transaction.Operand.Type;
import app.transaction.aspects.Aspect;

/**
 * Generates step graph of a schedule.
 * 
 * @param schedule
 */
public class Assessor {

    private Graph<Operation, DefaultEdge> stepGraph;
    private Set<SymbolicData> symbolicDataSet;
    private Schedule schedule;
    private List<Triple<Operand, String, Operand>> liveReadFromRealations;
    private List<Triple<Operand, String, Operand>> readFromRealations;

    public Assessor() {
        super();
    }

    public Assessor(Schedule schedule) {
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
    private Graph<Operation, DefaultEdge> generateStepGraph(Schedule schedule) {
        Graph<Operation, DefaultEdge> g = new Multigraph<>(DefaultEdge.class);
        var sch = schedule.getOperations();

        symbolicDataSet = new HashSet<SymbolicData>();
        for (Operation op : sch) {
            if (op.getOperand() != null && op.getOperand().getType() == Type.SYMBOLIC_DATA) {
                symbolicDataSet.add((SymbolicData) op.getOperand());
            }
            g.addVertex(op);
            op.setLive(false);
        }

        // adding the initial writes and final reads steps (transaction 0 and inifite)
        symbolicDataSet.forEach(data -> {
            g.addVertex(new Operation(Operation.Type.WRITE, data, new Transaction('0')));
            var read = new Operation(Operation.Type.READ, data, new Transaction('i'));
            read.setLive(true);
            g.addVertex(read);
            sch.add(read);
        });

        // creating the edges in the step graph
        for (int i = sch.size() - 1; i >= 1; i--) {
            if (sch.get(i).getType() == Operation.Type.READ) {
                for (int j = i - 1; j >= 0; j--) {
                    if (sch.get(j).getType() == Operation.Type.WRITE
                            && sch.get(j).getOperand().equals(sch.get(i).getOperand())) {
                        g.addEdge(sch.get(j), sch.get(i));
                        // we can brake out of the for loop since the input of a read operation can be
                        // only one write operation
                        j = -1;
                    }
                }
            } else if (sch.get(i).getType() == Operation.Type.WRITE) {
                for (int j = i - 1; j >= 0; j--) {
                    if (sch.get(j).getType() == Operation.Type.READ
                            && sch.get(j).getTransaction().equals(sch.get(i).getTransaction())) {
                        g.addEdge(sch.get(j), sch.get(i));
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
        var sch = schedule.getOperations();
        sch.subList(sch.size() - symbolicDataSet.size(), sch.size()).forEach(finalOp -> {
            var pathfinder = new DijkstraShortestPath<>(stepGraph);
            var paths = pathfinder.getPaths(finalOp);
            sch.subList(0, sch.size() - symbolicDataSet.size()).forEach(op -> {
                var path = paths.getPath(op);
                if (path != null && path.getLength() > 0) {
                    op.setLive(true);
                }
            });
        });
    }

    private ArrayList<Triple<Operand, String, Operand>> createLiveReadFromList() {
        var liveReadFromRelations = new ArrayList<Triple<Operand, String, Operand>>();
        var sch = schedule.getOperations();
        for (int i = 0; i < sch.size(); i++) {
            if (sch.get(i).getType().equals(Operation.Type.READ) && sch.get(i).isLive()) {
                int j = i - 1;
                do {
                    if (j <= 0) {
                        liveReadFromRelations.add(Triple.of(new Transaction('0'),
                                sch.get(i).getOperand().getName() + "", sch.get(i).getTransaction()));
                    } else if (sch.get(j).getType().equals(Operation.Type.WRITE)
                            && sch.get(j).getOperand().equals(sch.get(i).getOperand())) {
                        liveReadFromRelations.add(Triple.of(sch.get(j).getTransaction(),
                                sch.get(i).getOperand().getName() + "", sch.get(i).getTransaction()));
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
        var sch = schedule.getOperations();
        for (int i = 0; i < sch.size(); i++) {
            if (sch.get(i).getType().equals(Operation.Type.READ)) {
                int j = i - 1;
                do {
                    if (j <= 0) {
                        readFromRelations.add(Triple.of(new Transaction('0'), sch.get(i).getOperand().getName() + "",
                                sch.get(i).getTransaction()));
                    } else if (sch.get(j).getType().equals(Operation.Type.WRITE)
                            && sch.get(j).getOperand().equals(sch.get(i).getOperand())) {
                        readFromRelations.add(Triple.of(sch.get(j).getTransaction(),
                                sch.get(i).getOperand().getName() + "", sch.get(i).getTransaction()));
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

    public void setStepGraph(Schedule schedule) {
        this.stepGraph = generateStepGraph(schedule);
    }

    public Set<SymbolicData> getSymbolicDataSet() {
        return this.symbolicDataSet;
    }

    public void setSymbolicDataSet(Set<SymbolicData> symbolicDataSet) {
        this.symbolicDataSet = symbolicDataSet;
    }

    public Schedule getSchedule() {
        return this.schedule;
    }

    public void setSchedule(Schedule schedule) {
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