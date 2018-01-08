package main.java.assignment.euristic;

import main.java.assignment.PassiManager;
import main.java.assignment.firstsolution.Alberto2SolutionGenerator;
import main.java.assignment.firstsolution.Alberto3SolutionGenerator;
import main.java.assignment.firstsolution.RandomFirstSolutionGenerator;
import main.java.assignment.improvement.ISolutionImprovator;
import main.java.assignment.improvement.SolutionImprovator;
import main.java.assignment.improvement.SwapSolutionImprovator;
import main.java.assignment.improvement.TabuSearchImprovator;
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
import java.util.Comparator;
import java.util.List;

public class StandardEuristic extends IEuristic{


    public StandardEuristic(IModelWrapper model, int secondiTotali) {
        super(model);

        swap = new SwapSolutionImprovator(model);
        tabu = new TabuSearchImprovator(model);

        this.solutionGenerator = new TabuSearchSolutionGeneratorExtreme(model);
        this.firstSolutionGenerator = new RandomFirstSolutionGenerator(model);
        this.solutionImprovator = tabu;
        this.secondiTotali = secondiTotali;
        model.printOnlyCompleteSolutions(false);
        System.out.println("I secondi totali sono: " + secondiTotali);
    }


    int secondiTotali;

    long startTime = System.currentTimeMillis();

    int fase = 0;
    boolean albertoScelto = false;
    private boolean soluzioneOrdinate = false;
    IScoreCalculator calculator = new ScoreCalculator();



    List<AssignmentModel> soluzioniValide = new ArrayList<>();

    ISolutionImprovator swap;
    ISolutionImprovator tabu;

    PassiManager passiManager = new PassiManager();

    /* Algoritmo utilizzato */
    // 1 - Cerco di generare per 30 secondi soluzioni random feasable
    //    Se non ho generato alcuna soluzione passo ad AlbertoSolutionGenerator
    //    Siccome in questo caso i conflitti sono pochi sto pochi istanti altrimenti ricomincio da capo

    int secondiPassati = 0;

    @Override
    public void iterate() {

        secondiPassati = (int)(System.currentTimeMillis() - startTime) / 1000;
     //   System.out.println("SECONDI PASSATI: " + secondiPassati);


        if (model.isAssignmentComplete()) {

                if(secondiPassati < (secondiTotali / 10) && soluzioniValide.size() < 30){
                   //Primi 30 secondi
                    if(passiManager.getPassiGenerazioneSoluzione() > (model.getExamsNumber()*30)){
                        //firstSolutionGenerator.generateFirstSolution();
                        //System.out.println("STD: Non ho trovato soluzione nei passi previsti: " + (model.getExamsNumber() * 30));
                        passiManager.reset();
                    }

                    solutionGenerator.iterate();
                    passiManager.passoGenerazioneSoluzione();

                    if(model.isSolutionValid()){
                        System.out.println("Trovata la soluzione numero : " + (soluzioniValide.size() +1));
                        soluzioniValide.add(model.getAssignmentModel().clone());
                        firstSolutionGenerator.generateFirstSolution();
                        passiManager.reset();
                    }


                }else if(secondiPassati < (secondiTotali / 5) && soluzioniValide.size() < 30){
                    //Primo minuto

                    //Continuo col vecchio algoritmo se funziona. Altrimento scelto Alberto generator
                    if(soluzioniValide.size() == 0 && !albertoScelto){
                        albertoScelto =  true;
                        System.out.println("Utilizzo per un po' Alberto generator");
                        firstSolutionGenerator = new Alberto2SolutionGenerator(model);
                    }

                    //Primi 30 secondi
                    if(albertoScelto && passiManager.getPassiGenerazioneSoluzione() > (model.getExamsNumber()* 10)){
                        firstSolutionGenerator.generateFirstSolution();
                        System.out.println("ALB: Non ho trovato soluzione nei passi previsti: " + (model.getExamsNumber() * 10));
                        passiManager.reset();
                        System.out.println("Conflitti con AlbertoGenerator = " + model.getConflictNumber());
                    }else if(!albertoScelto && passiManager.getPassiGenerazioneSoluzione() > model.getExamsNumber() * 40){
                        firstSolutionGenerator.generateFirstSolution();
                        System.out.println("STD+: Non ho trovato soluzione nei passi previsti: " + (model.getExamsNumber() * 40));
                        passiManager.reset();
                    }

                    solutionGenerator.iterate();
                    passiManager.passoGenerazioneSoluzione();

                    if(model.isSolutionValid()){
                        System.out.println("Trovata la soluzione numero : " + (soluzioniValide.size() +1));
                        soluzioniValide.add(model.getAssignmentModel().clone());
                        firstSolutionGenerator.generateFirstSolution();
                        passiManager.reset();
                    }

                }else{



                    if(secondiPassati > secondiTotali){
                        AssignmentModel best = calculator.getBestUntilNow();
                        ModelWrapper model = new ModelWrapper(best, calculator, null);
                        for(int i=0; i< model.getTimeslotsNumber(); i++){
                            model.shift();
                            System.out.println("Punteggio post shift: " + model.getActualScore());
                        }
                        System.exit(0);
                    }


                    if(soluzioniValide.isEmpty()){
                        System.out.println("Nessuna soluzione trovata :(");
                        System.exit(0);
                    }else{

                        if(soluzioneOrdinate){
                            if(secondiPassati % 10 == 0){
                                startTime -= 1000;
                                System.out.println("Novo pezzo preso");
                                this.model = new ModelWrapper(soluzioniValide.get(fase++), calculator, new DeltaScoreCalculator());
                                solutionImprovator = new TabuSearchImprovator(model);
                            }
                            solutionImprovator.iterate();
                        }else{
                            soluzioneOrdinate = true;


                            soluzioniValide.sort((assignmentModel, t1) -> {
                                double score1 = calculator.getScore(assignmentModel, 0);
                                double score2 = calculator.getScore(t1, 0);
                                if(score1 == score2) return 0;
                                if(score1 < score2){
                                    return -1;
                                }else{
                                    return 1;
                                }
                            });

                            for(AssignmentModel model: soluzioniValide){
                                ModelWrapper modelWrapper = new ModelWrapper(model, calculator, new DeltaScoreCalculator());
                                System.out.println("Modello: " + modelWrapper.getActualScore());
                            }

                            this.model = new ModelWrapper(soluzioniValide.get(0), calculator, new DeltaScoreCalculator());
                            solutionImprovator = new TabuSearchImprovator(model);
                        }


                    }

                }




        } else {
            System.out.println("Genero la prima soluzione");
            firstSolutionGenerator.generateFirstSolution();
        }
    }

}
