package main.java.assignment.euristic;

import main.java.assignment.firstsolution.Alberto2SolutionGenerator;
import main.java.assignment.firstsolution.RandomFirstSolutionGenerator;
import main.java.assignment.improvement.SwapTimeSlotImprovator;
import main.java.assignment.improvement.TabuSearchImprovatorExtreme;
import main.java.assignment.model.AssignmentModel;
import main.java.assignment.model.IModelWrapper;
import main.java.assignment.solution.RandomSolutionGenerator;
import main.java.assignment.solution.TabuSearchSolutionGeneratorExtreme;
import main.java.assignment.util.BestList;
import main.java.assignment.util.ExamPair;

import java.util.Comparator;

public class FinalEuristicMichiaUnAltra extends IEuristic{

    int passi = 0;

    //Variabili passo 0
    int MAX_SOLUZIONI_INIZIALI_TROVATE; //TODO: E' un parametro
    int soluzioniTrovate = 0;
    double punteggioMigliore = Double.MAX_VALUE;
    AssignmentModel migliorModel = null;

    int tempoIniziale = (int) System.currentTimeMillis();

    //Variabili passo 2
    double punteggioModelloPeggiore = Double.MAX_VALUE;
    private int DIMENSIONE_LISTA_MIGLIORI; //La dimensione della lista dei migliori
    BestList listaMigliori;

    //Variabili passo 3
    double lastPunteggio = Double.MAX_VALUE;

    //Passi oltre la quale la tabu search si considera in una zona stagnante
    int PASSI_NO_MIGLIORAMENTO_MASSIMI = 0;


    //Parametri
    int SECONDI_FASE_1;
    int SECONDI_FASE_2;

    //Numero fase
    int fase = 1;

    //Passi nei quali si tenta la generazione di una feasable prima di passare ad un nuovo esame
    private int PASSI_RIPROVA = 0;


    public FinalEuristicMichiaUnAltra(IModelWrapper model, int secondiTotali) {
        super(model);
        this.solutionGenerator = new RandomSolutionGenerator(model);
        this.firstSolutionGenerator = new Alberto2SolutionGenerator(model);
        this.solutionImprovator = new SwapTimeSlotImprovator(model);
        model.printOnlyCompleteSolutions(false);
        System.out.println("I secondi totali sono: " + secondiTotali);


        SECONDI_FASE_1 = secondiTotali / 10; //TODO -> Scrivere relativamente al tempo totale
        SECONDI_FASE_2 = secondiTotali / 5; //TODO -> Scrivere relativamente al tempo totale
        MAX_SOLUZIONI_INIZIALI_TROVATE = 50;
        PASSI_RIPROVA = model.getExamsNumber();
        PASSI_NO_MIGLIORAMENTO_MASSIMI = 30000/model.getExamsNumber();
        DIMENSIONE_LISTA_MIGLIORI = 30;

        System.out.println("SECONDI FASE 1 = " + SECONDI_FASE_1);
        System.out.println("SECONDI FASE 2 = " + SECONDI_FASE_2);
        System.out.println("PASSI RIPROVA = " + PASSI_RIPROVA);
        System.out.println("PASSI_MIGLIORAMENTO_MASSIMI = " + PASSI_NO_MIGLIORAMENTO_MASSIMI);



        listaMigliori = new BestList(DIMENSIONE_LISTA_MIGLIORI);
    }

    @Override
    public void iterate() {

        int secondiCorrenti = ((int) ( System.currentTimeMillis() - tempoIniziale) / 1000);

        if (model.isAssignmentComplete()) {
            if(model.isSolutionValid()){

                if(fase == 1){
                    if((soluzioniTrovate > MAX_SOLUZIONI_INIZIALI_TROVATE || secondiCorrenti > SECONDI_FASE_1)){
                       initFaseDue();
                       return;
                    }

                    soluzioniTrovate++;
                    System.out.println("Trovate " + soluzioniTrovate + " soluzioni inziali");
                    if(punteggioMigliore > model.getActualScore()){
                        punteggioMigliore = model.getActualScore();
                        migliorModel = model.getAssignmentModel().clone();
                        System.out.println("Trovato punteggio migliore fase 1: " + punteggioMigliore);
                    }

                    firstSolutionGenerator.generateFirstSolution();
                    passi = 0;
                }


                //Passo 2 -> Faccio lo swap della migliore trovata e prendo i V migliori entro M secondi
                if(fase == 2){

                    if(secondiCorrenti > SECONDI_FASE_2){
                       initFaseTre();
                       return;
                    }

                    model.randomSwapTimeSlot();
                    if(listaMigliori.shouldAdd(model.getActualScore())){
                        listaMigliori.add(new ExamPair(model.getActualScore(), model.getAssignmentModel().clone()));
                    }
                }

                if(fase == 3){

                    if(PASSI_NO_MIGLIORAMENTO_MASSIMI < passi){
                        if(listaMigliori.size() > 0){
                            model.changeModel(listaMigliori.remove(0).getModel());
                            lastPunteggio = Double.MAX_VALUE;
                            passi = 0;
                            System.out.println("Passo al prossimo modello da migliorare");
                        }
                    }

                    solutionImprovator.iterate();
                    passi++;

                    if(lastPunteggio > model.getActualScore()){
                        lastPunteggio = model.getActualScore();
                        passi = 0;
                        System.out.println("Nuovo punteggio minimo: " + lastPunteggio);
                    }

                }


                //Passo 3 -> Tabu search in ognuno di questi


            }else{
                solutionGenerator.iterate();
                passi++;

                if (passi % PASSI_RIPROVA == 0) {
                 //   System.out.println("Riprovo dall'inizio. Conflitti questa volta: " + model.getConflictNumber());
                    firstSolutionGenerator.generateFirstSolution();
                   // System.out.println("Conflitti di partenza: " + model.getConflictNumber());
                    passi = 0;
                }
            }
        } else {
            System.out.println("Genero la prima soluzione");
            firstSolutionGenerator.generateFirstSolution();
        }
    }

    private void initFaseTre() {
        fase = 3;
        //Init fase 3
        System.out.println("Passo alla fase 3");
        solutionImprovator = new TabuSearchImprovatorExtreme(model);
        listaMigliori.sort(new Comparator<ExamPair>() {
            @Override
            public int compare(ExamPair examPair, ExamPair t1) {
                if(examPair.getScore() == t1.getScore()) return 0;
                if(examPair.getScore() < t1.getScore()){
                    return -1;
                }else{
                    return 1;
                }
            }
        });
        for(ExamPair pair: listaMigliori){
            model.changeModel(pair.getModel());
            System.out.println("MODELLO: " + model.getActualScore());
        }
        passi = 0;
        model.changeModel(listaMigliori.remove(0).getModel());
        System.out.println("Passo al primo modello da migliorare");
    }

    private void initFaseDue() {
        fase = 2;
        //Preparazione alla fase 2
        model.changeModel(migliorModel);
        System.out.println("Arrivato alla fase 2 con un model dal punteggio di: " + model.getActualScore());
    }

}
