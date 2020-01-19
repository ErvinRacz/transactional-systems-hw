package app.transaction.aspects;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import app.transaction.Assessor;
import app.transaction.Operand;
import app.transaction.Operation;
import app.transaction.Schedule;
import app.transaction.SymbolicData;

/**
 * Verifies a schedule in regards of view equivalency. Compares the subject
 * schedule to a list of serial schedules through the provided List of
 * Assessors.
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
    public boolean assess(Schedule schedule, Graph<Operation, DefaultEdge> stepGraph,
            List<Triple<Operand, String, Operand>> readFromRealations, Set<SymbolicData> symbolicDataSet) {
        for (Assessor a : assessors) {
            if (a.getSchedule().getOperations().size() == schedule.getOperations().size()
                    && a.getLiveReadFromRealations().size() == readFromRealations.size()
                    && a.getLiveReadFromRealations().containsAll(readFromRealations))
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