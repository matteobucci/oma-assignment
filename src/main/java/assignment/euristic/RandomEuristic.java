package main.java.assignment.euristic;

import main.java.assignment.firstsolution.Alberto2SolutionGenerator;
import main.java.assignment.improvement.SwapTimeSlotImprovator;
import main.java.assignment.improvement.TabuSearchImprovatorExtreme;
import main.java.assignment.model.AssignmentModel;
import main.java.assignment.model.IModelWrapper;
import main.java.assignment.solution.RandomSolutionGenerator;

public class RandomEuristic extends IEuristic{

    boolean trovataFeas = false;
    double migliore = Double.MAX_VALUE;

    int secondiPassati = 0;
    private long startTime;
    private boolean algoritmoCambiato = false;

    int passi = 0;



    public RandomEuristic(IModelWrapper model, int secondiTotali) {
        super(model);

        this.solutionGenerator = new RandomSolutionGenerator(model);
        this.firstSolutionGenerator = new Alberto2SolutionGenerator(model);
        this.solutionImprovator = new SwapTimeSlotImprovator(model);
        model.printOnlyCompleteSolutions(false);
        System.out.println("I secondi totali sono: " + secondiTotali);
    }

    @Override
    public void iterate() {



        if (model.isAssignmentComplete()) {




            if(model.isSolutionValid()){




            }else{
                solutionGenerator.iterate();
                passi++;

                if (passi % model.getExamsNumber()    == 0) {
                    System.out.println("Riprovo dall'inizio. Conflitti questa volta: " + model.getConflictNumber());
                    firstSolutionGenerator.generateFirstSolution();
                    System.out.println("Conflitti di partenza: " + model.getConflictNumber());
                    passi = 0;
                }
            }
        } else {
            System.out.println("Genero la prima soluzione");
            firstSolutionGenerator.generateFirstSolution();
        }
    }

}
