package main.java.assignment.euristic;

import main.java.assignment.scorecalculator.DeltaScoreCalculator;
import main.java.assignment.scorecalculator.IDeltaScoreCalculator;
import main.java.assignment.firstsolution.Alberto2SolutionGenerator;
import main.java.assignment.improvement.SolutionImprovator;
import main.java.assignment.model.AssignmentModel;
import main.java.assignment.model.IModelWrapper;
import main.java.assignment.solution.RandomSolutionGenerator;

public class StandardEuristic extends IEuristic{

    public StandardEuristic(IModelWrapper model) {
        super(model);
        this.solutionGenerator = new RandomSolutionGenerator(model);
        this.solutionImprovator = new SolutionImprovator(model);
        this.firstSolutionGenerator = new Alberto2SolutionGenerator(model);
    }


    int passi = 0;

    @Override
    public void iterate() {
        if (model.isAssignmentComplete()) {
            if (model.isSolutionValid()) {
                solutionImprovator.iterate();
                model.stampa();
            } else {
                solutionGenerator.iterate();
                passi++;

                if (passi % 2000 == 0) {
                    firstSolutionGenerator.generateFirstSolution();
                    passi = 0;
                }
            }
        } else {
            System.out.println("Soluzione non completa");
            firstSolutionGenerator.generateFirstSolution();
        }
    }
}
