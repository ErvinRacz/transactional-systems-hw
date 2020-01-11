package app.assessor.aspects;

import java.util.List;

import org.apache.commons.lang3.tuple.Triple;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import app.models.Operation;
import app.models.operands.Operand;

/**
 * Verifies a schedule in regards of final state equivalency. Provide the read
 * from relation set of the first schedule through the constructor for the
 * comparison.
 */
public class FSR implements Aspect {

    private List<Operation> scheduleCompareTo;
    private List<Triple<Operand, String, Operand>> lrfCompareTo;

    /**
     * Provide the read from relation set of the first schedule through the
     * constructor for the comparison.
     * 
     * @param scheduleCompareTo
     * @param lrfCompareTo
     */
    public FSR(List<Operation> scheduleCompareTo, List<Triple<Operand, String, Operand>> lrfCompareTo) {
        this.scheduleCompareTo = scheduleCompareTo;
        this.lrfCompareTo = lrfCompareTo;
    }

    @Override
    public boolean assess(List<Operation> schedule, Graph<Operation, DefaultEdge> stepGraph,
            List<Triple<Operand, String, Operand>> lrfSecond) {
        return scheduleCompareTo.size() == schedule.size() && lrfCompareTo.size() == lrfSecond.size()
                && lrfCompareTo.containsAll(lrfSecond);
    }

    // #region Getters and setters

    public List<Triple<Operand, String, Operand>> getLrfFirst() {
        return this.lrfCompareTo;
    }

    public void setLrfFirst(List<Triple<Operand, String, Operand>> lrfFirst) {
        this.lrfCompareTo = lrfFirst;
    }

    public List<Operation> getScheduleFirst() {
        return this.scheduleCompareTo;
    }

    public void setScheduleFirst(List<Operation> scheduleFirst) {
        this.scheduleCompareTo = scheduleFirst;
    }
    // #endregion
}
