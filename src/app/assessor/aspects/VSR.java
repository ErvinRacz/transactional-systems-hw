package app.assessor.aspects;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import app.models.Operation;
import app.models.operands.Operand;
import app.models.operands.SymbolicData;

/**
 * Verifies a schedule in regards of view equivalency. Provide the read
 * from relation set of the first schedule through the constructor for the
 * comparison.
 */
public class VSR implements Aspect {

    private List<Operation> scheduleCompareTo;
    private List<Triple<Operand, String, Operand>> lrfCompareTo;

    /**
     * Provide the read from relation set of the first schedule through the
     * constructor for the comparison.
     * 
     * @param scheduleCompareTo
     * @param rfCompareTo
     */
    public VSR(List<Operation> scheduleCompareTo, List<Triple<Operand, String, Operand>> rfCompareTo) {
        this.scheduleCompareTo = scheduleCompareTo;
        this.lrfCompareTo = rfCompareTo;
    }

    @Override
    public boolean assess(List<Operation> schedule, Graph<Operation, DefaultEdge> stepGraph,
            List<Triple<Operand, String, Operand>> rfSecond, Set<SymbolicData> symbolicDataSet) {
        return scheduleCompareTo.size() == schedule.size() && lrfCompareTo.size() == rfSecond.size()
                && lrfCompareTo.containsAll(rfSecond);
    }

    // #region Getters and setters

    public List<Triple<Operand, String, Operand>> getRfFirst() {
        return this.lrfCompareTo;
    }

    public void setRfFirst(List<Triple<Operand, String, Operand>> lrfFirst) {
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
