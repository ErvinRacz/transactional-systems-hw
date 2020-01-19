package app.transaction.aspects;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import app.transaction.Operand;
import app.transaction.Operation;
import app.transaction.Schedule;
import app.transaction.SymbolicData;


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
    public boolean assess(Schedule schedule, Graph<Operation, DefaultEdge> stepGraph,
            List<Triple<Operand, String, Operand>> readFromRealations, Set<SymbolicData> symbolicDataSet);
}