package main.java.assignment;

import main.java.assignment.model.AssignmentModel;
import main.java.assignment.model.ModelWrapper;

public interface IScoreCalculator {

    double getScore(AssignmentModel model, int conflicts);

    AssignmentModel getBestUntilNow();

}
