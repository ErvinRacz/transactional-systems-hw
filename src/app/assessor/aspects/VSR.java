package app.assessor.aspects;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import app.assessor.Assessor;
import app.models.Operation;
import app.models.operands.Operand;
import app.models.operands.SymbolicData;

/**
 * Verifies a schedule in regards of view equivalency. Provide the read from
 * relation set of the first schedule through the constructor for the
 * comparison.
 */
public class VSR implements Aspect {

    private List<Assessor> assessors;

    /**
     * @param scheduleCompareTo
     * @param rfCompareTo
     */
    public VSR(List<Assessor> compareToAssessors) {
        this.assessors = compareToAssessors;
    }

    @Override
    public boolean assess(List<Operation> schedule, Graph<Operation, DefaultEdge> stepGraph,
            List<Triple<Operand, String, Operand>> rfSecond, Set<SymbolicData> symbolicDataSet) {
        for (Assessor a : assessors) {
            if (a.getSchedule().size() == schedule.size() && a.getLiveReadFromRealations().size() == rfSecond.size()
                    && a.getLiveReadFromRealations().containsAll(rfSecond))
                return true;
        }
        return false;
    }

    // #region Getters and setters

    public List<Assessor> getAssessors() {
        return this.assessors;
    }

    public void setAssessors(List<Assessor> assessors) {
        this.assessors = assessors;
    }

    // #endregion
}
