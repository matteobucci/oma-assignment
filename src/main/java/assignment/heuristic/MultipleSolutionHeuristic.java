package main.java.assignment.heuristic;

import main.java.assignment.firstsolution.OrderedSolutionGenerator;
import main.java.assignment.firstsolution.RandomFirstSolutionGenerator;
import main.java.assignment.improvement.*;
import main.java.assignment.model.AssignmentModel;
import main.java.assignment.model.IModelWrapper;
import main.java.assignment.solution.RandomSolutionGenerator;
import main.java.assignment.solution.TabuSearchSolutionGenerator;
import main.java.assignment.util.BestModelList;
import main.java.assignment.util.ModelPair;

import java.util.Comparator;

public class MultipleSolutionHeuristic extends IHeuristic {

    int passi = 0;
    int secondiTotali;

    //Variabili passo 1
    int MAX_SOLUZIONI_INIZIALI_TROVATE;
    BestModelList listaInizialiMigliori;
    AssignmentModel migliorModel = null;

    int tempoIniziale = (int) System.currentTimeMillis();

    //Variabili passo 2
    private int DIMENSIONE_LISTA_MIGLIORI; //La dimensione della lista dei migliori
    private BestModelList listaMigliori;

    //Variabili passo 3
    double lastPunteggio = Double.MAX_VALUE;

    //Passi oltre la quale gli algoritmi search si considera in una zona stagnante
    int PASSI_NO_MIGLIORAMENTO_MASSIMI = 0;

    //Passi nei quali si tenta la generazione di una feasable prima di passare ad una nuova soluzione
    private int PASSI_RIPROVA = 0;


    //Parametri
    int SECONDI_FASE_1;
    int SECONDI_FASE_2;

    //Numero fase
    int fase = 1;




    public MultipleSolutionHeuristic(IModelWrapper model, int secondiTotali) {
        super(model);
        if(model.getExamsNumber() < 185){
            System.out.println("Provo a generare una soluzione random");
            this.solutionGenerator = new TabuSearchSolutionGenerator(model);
            this.firstSolutionGenerator = new RandomFirstSolutionGenerator(model);
            PASSI_RIPROVA = model.getExamsNumber() * 20;// * 100;
        }else{
            System.out.println("Provo ad ordinare gli esami per numero di conflitti");
            this.solutionGenerator = new RandomSolutionGenerator(model);
            this.firstSolutionGenerator = new OrderedSolutionGenerator(model);
            PASSI_RIPROVA = model.getExamsNumber();// * 100;
        }


        this.solutionImprovator = new SwapTimeSlotImprovator(model);
        model.printOnlyCompleteSolutions(false);
        System.out.println("I secondi totali sono: " + secondiTotali);
        this.secondiTotali = secondiTotali;


        SECONDI_FASE_1 = (int) (secondiTotali * 0.1);
        SECONDI_FASE_2 = (int) (secondiTotali * 0.3);
        MAX_SOLUZIONI_INIZIALI_TROVATE = 200;

        PASSI_NO_MIGLIORAMENTO_MASSIMI = 100000/model.getExamsNumber();
        DIMENSIONE_LISTA_MIGLIORI = 30;

        System.out.println("SECONDI FASE 1 = " + SECONDI_FASE_1);
        System.out.println("SECONDI FASE 2 = " + SECONDI_FASE_2);
        System.out.println("PASSI RIPROVA = " + PASSI_RIPROVA);
        System.out.println("PASSI_MIGLIORAMENTO_MASSIMI = " + PASSI_NO_MIGLIORAMENTO_MASSIMI);


        listaMigliori = new BestModelList(DIMENSIONE_LISTA_MIGLIORI);
        listaInizialiMigliori = new BestModelList(MAX_SOLUZIONI_INIZIALI_TROVATE);
    }

    @Override
    public void iterate() {

        int secondiCorrenti = ((int) ( System.currentTimeMillis() - tempoIniziale) / 1000);

        if (model.isAssignmentComplete()) {
            if(model.isSolutionValid()){

                if(fase == 1){
                    if((secondiCorrenti > SECONDI_FASE_1)){
                       initFaseDue();
                       return;
                    }

                    listaInizialiMigliori.add(new ModelPair(model.getActualScore(), model.getAssignmentModel().clone()));
                    firstSolutionGenerator.generateFirstSolution();

                    if(solutionGenerator instanceof TabuSearchSolutionGenerator){
                        solutionGenerator = new TabuSearchSolutionGenerator(model); //Resetto il tabu generator
                    }

                    passi = 0;
                }


                //Passo 2 -> Faccio lo swap della migliore trovata e prendo i V migliori entro M secondi
                if(fase == 2){

                   if(secondiCorrenti > SECONDI_FASE_2){
                       if(listaMigliori.isEmpty()){
                           listaMigliori.add(new ModelPair(lastPunteggio, migliorModel));
                       }
                       initFaseTre();
                       return;
                    }

                    if(PASSI_NO_MIGLIORAMENTO_MASSIMI < passi){
                       if(listaInizialiMigliori.size() > 0){

                           listaMigliori.add(new ModelPair(lastPunteggio, migliorModel));
                           model.changeModel(listaInizialiMigliori.remove(0).getModel());
                           lastPunteggio = Double.MAX_VALUE;
                           passi = 0;
                       }else{
                           initFaseTre();
                           return;
                       }
                    }

                    solutionImprovator.iterate();
                    passi++;

                    if(lastPunteggio > model.getActualScore()){
                        lastPunteggio = model.getActualScore();
                        passi = 0;
                        migliorModel = model.getAssignmentModel().clone();
                    }


                }

                if(fase == 3){

                    if(PASSI_NO_MIGLIORAMENTO_MASSIMI < passi){
                        if(listaMigliori.size() > 0){
                            listaMigliori.add(new ModelPair(lastPunteggio, migliorModel));
                            model.changeModel(listaMigliori.remove(0).getModel());
                            solutionImprovator = new TabuSearchImprovator(model);
                            lastPunteggio = Double.MAX_VALUE;
                            passi = 0;
                        }
                    }

                    solutionImprovator.iterate();
                    passi++;

                    if(lastPunteggio > model.getActualScore()){
                        lastPunteggio = model.getActualScore();
                        passi = 0;
                        migliorModel = model.getAssignmentModel().clone();
                    }

                }




            }else{
                solutionGenerator.iterate();
                passi++;

                if (passi % PASSI_RIPROVA == 0) {

                    if( secondiCorrenti > (SECONDI_FASE_1/2) && listaInizialiMigliori.size() < 5){
                        System.out.println("Ho trovato poche soluzioni. Cambio algoritmo");
                        firstSolutionGenerator = new OrderedSolutionGenerator(model);
                        solutionGenerator = new RandomSolutionGenerator(model);
                        PASSI_RIPROVA = model.getExamsNumber();
                    }

                    firstSolutionGenerator.generateFirstSolution();
                    solutionGenerator = new TabuSearchSolutionGenerator(model);
                    passi = 0;


                }

            }
        } else {
            firstSolutionGenerator.generateFirstSolution();
        }
    }

    private void initFaseTre() {
        fase = 3;
        //Init fase 3
        System.out.println("Passo alla fase 3");
        solutionImprovator = new TabuSearchImprovator(model);
        listaMigliori.sort(new Comparator<ModelPair>() {
            @Override
            public int compare(ModelPair modelPair, ModelPair t1) {
                if(modelPair.getScore() == t1.getScore()) return 0;
                if(modelPair.getScore() < t1.getScore()){
                    return -1;
                }else{
                    return 1;
                }
            }
        });
        for(ModelPair pair: listaMigliori){
            model.changeModel(pair.getModel());
            System.out.println("MODELLO: " + model.getActualScore());
        }
        passi = 0;
        model.changeModel(listaMigliori.remove(0).getModel());
        System.out.println("Passo al primo modello da migliorare");
    }

    private void initFaseDue() {
        fase = 2;
        listaInizialiMigliori.sort(new Comparator<ModelPair>() {
            @Override
            public int compare(ModelPair modelPair, ModelPair t1) {
                if(modelPair.getScore() == t1.getScore()) return 0;
                if(modelPair.getScore() < t1.getScore()){
                    return -1;
                }else{
                    return 1;
                }
            }
        });
        //Preparazione alla fase 2
        for(int i=0; i<listaInizialiMigliori.size(); i++){
            System.out.println("FASE 1 MODELLI: " + listaInizialiMigliori.get(i).getScore());
        }
        passi = 0;
        model.changeModel(listaInizialiMigliori.remove(0).getModel());
        System.out.println("Arrivato alla fase 2 con un model dal punteggio di: " + model.getActualScore());
    }

}
