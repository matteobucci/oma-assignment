package main.java.assignment.euristic;

import main.java.assignment.PassiManager;
import main.java.assignment.firstsolution.Alberto2SolutionGenerator;
import main.java.assignment.firstsolution.AlbertoSolutionGenerator;
import main.java.assignment.firstsolution.RandomFirstSolutionGenerator;
import main.java.assignment.improvement.*;
import main.java.assignment.model.AssignmentModel;
import main.java.assignment.model.IModelWrapper;
import main.java.assignment.model.ModelWrapper;
import main.java.assignment.scorecalculator.DeltaScoreCalculator;
import main.java.assignment.scorecalculator.IScoreCalculator;
import main.java.assignment.scorecalculator.ScoreCalculator;
import main.java.assignment.solution.RandomSolutionGenerator;
import main.java.assignment.solution.TabuSearchSolutionGenerator;
import main.java.assignment.solution.TabuSearchSolutionGeneratorExtreme;

import java.util.ArrayList;
import java.util.List;

public class IterationEuristic extends IEuristic{

    boolean trovataFeas = false;
    double migliore = Double.MAX_VALUE;

    int secondiPassati = 0;
    private long startTime;
    private boolean algoritmoCambiato = false;

    int passi = 0;

    double lastMigliore = Double.MAX_VALUE;

    int secondiSwap = 0;

    AssignmentModel modelMiglioreAttuale = null;


    public IterationEuristic(IModelWrapper model, int secondiTotali) {
        super(model);

        this.solutionGenerator = new TabuSearchSolutionGeneratorExtreme(model);
        this.firstSolutionGenerator = new RandomFirstSolutionGenerator(model);
        this.solutionImprovator = new SwapTimeSlotImprovator(model);
        model.printOnlyCompleteSolutions(false);
        System.out.println("I secondi totali sono: " + secondiTotali);
    }

    @Override
    public void iterate() {



        if (model.isAssignmentComplete()) {
            if(model.isSolutionValid()){

                if(!trovataFeas){
                    //Le cose da fare appena trovo una feasable
                    startTime = System.currentTimeMillis();
                    trovataFeas = true;
                    System.out.println("Prima soluzione trovata. Punteggio: " + model.getActualScore());
                    migliore = model.getActualScore();
                    passi = 0;
                }

                secondiPassati = (int)(System.currentTimeMillis() - startTime) / 1000;

                if(secondiPassati > 10 && !algoritmoCambiato){
                    algoritmoCambiato = true;
                    solutionImprovator = new TabuSearchImprovatorExtreme(model);
                    System.out.println("Cambio algoritmo");
                    model.changeModel(model.getCalculator().getBestUntilNow());
                }

                solutionImprovator.iterate();
                passi++;


                if(model.getActualScore() < migliore){
                    migliore = model.getActualScore();
                    modelMiglioreAttuale = model.getAssignmentModel().clone();
                    System.out.println("Trovata soluzione migliore: " + model.getActualScore());
                }



                /*

                if((solutionImprovator instanceof TabuSearchImprovatorExtreme) && passi > 0 && passi % 10 == 0 && algoritmoCambiato){
                    //10, 20, 30, ...
                    if(lastMigliore == migliore){
                        model.changeModel(modelMiglioreAttuale);
                        modelMiglioreAttuale = null;
                        migliore = Double.MAX_VALUE;

                        System.out.println("Negli ultimi 10 passi non c'è miglioramento");


                        solutionImprovator = new SwapTimeSlotImprovator(model);
                        secondiSwap = secondiPassati;
                        System.out.println("Vado di swap");


                    }else{
                        lastMigliore = migliore;
                    }

                }else if(solutionImprovator instanceof  SwapTimeSlotImprovator && (secondiPassati - secondiSwap > 10)){
                    model.changeModel(modelMiglioreAttuale);
                    modelMiglioreAttuale = null;
                    migliore = Double.MAX_VALUE;

                    solutionImprovator = new TabuSearchImprovatorExtreme(model);
                    System.out.println("Vado di tabu");
                }



*/


            }else{
                solutionGenerator.iterate();
                passi++;

                if (passi % (model.getExamsNumber()*20)   == 0) {
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
