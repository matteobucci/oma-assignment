package main.java.assignment.euristic;

import main.java.assignment.firstsolution.Alberto2SolutionGenerator;
import main.java.assignment.firstsolution.RandomFirstSolutionGenerator;
import main.java.assignment.improvement.ISolutionImprovator;
import main.java.assignment.improvement.SwapSolutionImprovator;
import main.java.assignment.improvement.TabuSearchImprovator;
import main.java.assignment.model.IModelWrapper;
import main.java.assignment.solution.RandomSolutionGenerator;
import main.java.assignment.solution.TabuSearchSolutionGenerator;

/*
E' un euristica che genera solo prime soluzioni di continuo almeno miglioriamo la generazione di queste
 */

public class FirstSolutionsEuristic extends IEuristic{

    private long startTime = System.currentTimeMillis();
    private long lastSolutionTime = System.currentTimeMillis();
    private int solutionsGenerated = 0;

    public FirstSolutionsEuristic(IModelWrapper model) {
        super(model);
        this.solutionGenerator = new TabuSearchSolutionGenerator(model);
        this.firstSolutionGenerator = new RandomFirstSolutionGenerator(model);
        model.printOnlyCompleteSolutions(false);
    }

    int passi = 0;

    @Override
    public void iterate() {
        if (model.isAssignmentComplete()) {
            if (model.isSolutionValid()) {
                showCompletementDetails();
                firstSolutionGenerator.generateFirstSolution();
            } else {
                solutionGenerator.iterate();
            }
        } else {
            System.out.println("Soluzione non completa");
            firstSolutionGenerator.generateFirstSolution();
        }
    }

    private void showCompletementDetails() {
        solutionsGenerated++;
        System.out.println("Soluzione generata! (numero " + solutionsGenerated + ")");
        long timeUsed =  (System.currentTimeMillis() - lastSolutionTime);
        lastSolutionTime = System.currentTimeMillis();
        System.out.println("Tempo di risoluzione = " + timeUsed);
        System.out.println("Media di risoluzione = " + (((System.currentTimeMillis() - startTime)/solutionsGenerated)));
        model.print();
    }


}
