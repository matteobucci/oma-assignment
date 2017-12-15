package main.java.assignment;

import main.java.assignment.model.AssignmentModel;

public interface IDeltaScoreCalculator {

    double getSwapCalculator(AssignmentModel model, int examIndex, int fromTimeSlot, int toTimeSlot);

}
