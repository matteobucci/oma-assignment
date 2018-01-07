package main.java.assignment.euristic;

import main.java.assignment.firstsolution.Alberto2SolutionGenerator;
import main.java.assignment.firstsolution.Alberto3SolutionGenerator;
import main.java.assignment.firstsolution.AlbertoSolutionGenerator;
import main.java.assignment.improvement.ISolutionImprovator;
import main.java.assignment.improvement.SwapSolutionImprovator;
import main.java.assignment.improvement.TabuSearchImprovator;
import main.java.assignment.model.IModelWrapper;
import main.java.assignment.solution.RandomSolutionGenerator;
import main.java.assignment.solution.TabuSearchSolutionGeneratorExtreme;

public class AlbertoEuristic extends IEuristic{

    public AlbertoEuristic(IModelWrapper model) {
        super(model);
        this.solutionGenerator = new RandomSolutionGenerator(model);
        tabu = new TabuSearchImprovator(model);
        swap = new SwapSolutionImprovator(model);
        this.solutionImprovator = tabu;
        this.firstSolutionGenerator = new Alberto2SolutionGenerator(model);
    }

    int cont=0;
    int passi = 0;
    long minDelayPrint = 100; //100ms
    long lastPrint = 0;
    double tempoI, tempoF;

    double lastPunteggio = 0;
    double bestScore=100000;

    ISolutionImprovator tabu;
    ISolutionImprovator swap;

    @Override
    public void iterate() {
        if (model.isAssignmentComplete()) {
            if (model.isSolutionValid()) {
                print();
                if (cont==0){ // è un pezzetto di codice solo per farci stampare lo score da cui parte per vedere i miglioramnti
                    tempoI = System.currentTimeMillis();
                    System.out.println(model.getActualScore());
                    cont=1;

                }


                solutionImprovator.iterate();
                solutionImprovator=tabu; // PER MATTE: abbiamo aggiunto questo e tolto la parte sotto per fare sempre tabù
                // perchè funziona meglio
                /*
                passi++;


                if(passi == 500){
                    solutionImprovator = swap;
                }
                if(passi == 3000){
                    solutionImprovator = tabu;
                    passi = 0;
                }

*/
            } else {
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