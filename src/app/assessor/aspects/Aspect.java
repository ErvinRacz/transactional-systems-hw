package app.assessor.aspects;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import app.models.Operation;
import app.models.operands.Operand;
import app.models.operands.SymbolicData;

public interface Aspect {

    /**
     * Pass the schedule and its step graph + LRF which is going to be assessed.
     * 
     * @param schedule
     * @param stepGraph
     * @param liveReadFromRealations
     * @param symbolicDataSet
     * @return
     */
    public boolean assess(List<Operation> schedule, Graph<Operation, DefaultEdge> stepGraph,
            List<Triple<Operand, String, Operand>> liveReadFromRealations, Set<SymbolicData> symbolicDataSet);
}