package app.assessor.aspects;

import java.util.List;

import org.apache.commons.lang3.tuple.Triple;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import app.models.Operation;
import app.models.operands.Operand;

public class CSR implements Aspect {

    @Override
    public boolean assess(List<Operation> schedule, Graph<Operation, DefaultEdge> stepGraph,
            List<Triple<Operand, String, Operand>> liveReadFromRealations) {
        return false;
    }
}