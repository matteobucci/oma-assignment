package main.java.assignment.scorecalculator;

import main.java.assignment.model.AssignmentModel;

public interface IScoreCalculator {

    double getScore(AssignmentModel model, int conflicts);

    AssignmentModel getBestUntilNow();

}
