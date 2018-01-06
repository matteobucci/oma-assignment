package main.java.assignment.euristic;

import main.java.assignment.PassiManager;
import main.java.assignment.firstsolution.Alberto2SolutionGenerator;
import main.java.assignment.firstsolution.RandomFirstSolutionGenerator;
import main.java.assignment.improvement.ISolutionImprovator;
import main.java.assignment.improvement.SwapSolutionImprovator;
import main.java.assignment.improvement.TabuSearchImprovator;
import main.java.assignment.improvement.TabuSearchImprovatorExtreme;
import main.java.assignment.model.AssignmentModel;
import main.java.assignment.model.IModelWrapper;
import main.java.assignment.model.ModelWrapper;
import main.java.assignment.scorecalculator.DeltaScoreCalculator;
import main.java.assignment.scorecalculator.IScoreCalculator;
import main.java.assignment.scorecalculator.ScoreCalculator;
import main.java.assignment.solution.TabuSearchSolutionGenerator;
import main.java.assignment.solution.TabuSearchSolutionGeneratorExtreme;

import java.util.ArrayList;
import java.util.List;

public class IterationEuristic extends IEuristic{


    public IterationEuristic(IModelWrapper model, int secondiTotali) {
        super(model);

        this.solutionGenerator = new TabuSearchSolutionGeneratorExtreme(model);
        this.firstSolutionGenerator = new RandomFirstSolutionGenerator(model);
        this.solutionImprovator = new TabuSearchImprovatorExtreme(model);
        model.printOnlyCompleteSolutions(false);
        System.out.println("I secondi totali sono: " + secondiTotali);
    }

    @Override
    public void iterate() {

        if (model.isAssignmentComplete()) {
            if(model.isSolutionValid()){
                solutionImprovator.iterate();
            }else{
                solutionGenerator.iterate();
            }
        } else {
            System.out.println("Genero la prima soluzione");
            firstSolutionGenerator.generateFirstSolution();
        }
    }

}
