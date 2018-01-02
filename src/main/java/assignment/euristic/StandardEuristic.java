package main.java.assignment.euristic;

import main.java.assignment.firstsolution.RandomFirstSolutionGenerator;
import main.java.assignment.improvement.ISolutionImprovator;
import main.java.assignment.improvement.SwapSolutionImprovator;
import main.java.assignment.improvement.TabuSearchImprovator;
import main.java.assignment.model.IModelWrapper;
import main.java.assignment.solution.TabuSearchSolutionGenerator;

public class StandardEuristic extends IEuristic{

    public StandardEuristic(IModelWrapper model) {
        super(model);
        this.solutionGenerator = new TabuSearchSolutionGenerator(model);
        swap = new SwapSolutionImprovator(model);
        tabu = new TabuSearchImprovator(model);
        this.firstSolutionGenerator = new RandomFirstSolutionGenerator(model);
        model.printOnlyCompleteSolutions(true);
    }

    int passi = 0;
    long minDelayPrint = 100; //100ms
    long lastPrint = 0;

    double lastPunteggio = 0;

    ISolutionImprovator swap;
    ISolutionImprovator tabu;

    @Override
    public void iterate() {
        if (model.isAssignmentComplete()) {
            if (model.isSolutionValid()) {
                print();

                if(passi % 10  == 0){
                    swap.iterate();
                }else{
                    tabu.iterate();
                    System.out.println("TABUx");
                }

            } else {
                solutionGenerator.iterate();
                passi++;

                if (passi % (model.getExamsNumber()* 20)   == 0) {
                    System.out.println("Riprovo dall'inizio. Conflitti questa volta: " + model.getConflictNumber());
                    firstSolutionGenerator.generateFirstSolution();
                    System.out.println("Conflitti di partenza: " + model.getConflictNumber());
                    passi = 0;
                }
            }
        } else {
            System.out.println("Soluzione non completa");
            firstSolutionGenerator.generateFirstSolution();
        }
    }

    private void print(){
        long actual = System.currentTimeMillis();
        if(actual - lastPrint > minDelayPrint){
            lastPrint = actual;
            model.print();
        }
    }
}
