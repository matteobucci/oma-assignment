package main.java.assignment.scorecalculator;

import main.java.assignment.model.IModelWrapper;

public interface IDeltaScoreCalculator {

    double getScore(IModelWrapper model, int examIndex, int fromTimeSlot, int toTimeSlot);

}
