package main.java.assignment.euristic;

import main.java.assignment.firstsolution.Alberto2SolutionGenerator;
import main.java.assignment.firstsolution.RandomFirstSolutionGenerator;
import main.java.assignment.improvement.ISolutionImprovator;
import main.java.assignment.improvement.SwapSolutionImprovator;
import main.java.assignment.improvement.TabuSearchImprovator;
import main.java.assignment.model.IModelWrapper;
import main.java.assignment.solution.RandomSolutionGenerator;
import main.java.assignment.solution.TabuSearchSolutionGenerator;
import org.omg.CORBA.INTERNAL;

/*
E' un euristica che genera solo prime soluzioni di continuo almeno miglioriamo la generazione di queste
 */

public class FirstSolutionsEuristic extends IEuristic{

    private long startTime = System.currentTimeMillis();
    private long lastSolutionTime = System.currentTimeMillis();
    private int solutionsGenerated = 0;
    private int passiRichiesti = 0;
    private int passiTotali = 0;
    private double punteggioTotale = 0;
    private double punteggioMassimo = Double.MIN_VALUE;
    private double punteggioMinimo = Double.MAX_VALUE;
    private long tempoMinimo = Integer.MAX_VALUE;
    private long tempoMassimo = Integer.MIN_VALUE;


    public FirstSolutionsEuristic(IModelWrapper model) {
        super(model);
        this.solutionGenerator = new TabuSearchSolutionGenerator(model);
        this.firstSolutionGenerator = new RandomFirstSolutionGenerator(model);
        model.printOnlyCompleteSolutions(true);
    }

    @Override
    public void iterate() {
        if (model.isAssignmentComplete()) {
            if (model.isSolutionValid()) { //YEAH. Non ho conflitti
                showCompletementDetails(); //Stampo e aggiorno i dettagli sulle risoluzioni
                firstSolutionGenerator.generateFirstSolution(); //Ricomincio con una nuova
            } else {
                solutionGenerator.iterate(); //Miglioro lo standard attuale
            }
        } else {
            //Se non ho assegnato tutti gli esami
            firstSolutionGenerator.generateFirstSolution(); //Genero la prima soluzione
        }

        passiRichiesti++;
        passiTotali++;
    }

    private void showCompletementDetails() {
        double punteggio = model.getActualScore();
        solutionsGenerated++;
        punteggioTotale += punteggio;
        long timeUsed =  (System.currentTimeMillis() - lastSolutionTime);
        lastSolutionTime = System.currentTimeMillis();
        if(punteggio > punteggioMassimo){
            punteggioMassimo = punteggio;
            System.out.println("NUOVO PUNTEGGIO MASSIMO = " + punteggio);
        }
        if (punteggio < punteggioMinimo){
            punteggioMinimo = punteggio;
            System.out.println("NUOVO PUNTEGGIO MINIMO = " + punteggio);
        }
        if(timeUsed > tempoMassimo){
            tempoMassimo = timeUsed;
            System.out.println("NUOVO TEMPO MASSIMO = " + timeUsed);
        }
        if (timeUsed < tempoMinimo){
            tempoMinimo = timeUsed;
            System.out.println("NUOVO TEMPO MINIMO = " + timeUsed);
        }
        System.out.println("==================================================================");
        System.out.println("Soluzione generata! (numero " + solutionsGenerated + ")");
        System.out.println("Tempo di risoluzione = " + timeUsed);
        System.out.println("Passi Richiesti = " + passiRichiesti);
        System.out.println("Punteggio soluzione = " + punteggio);
        System.out.println("Punteggio minimo = " + punteggioMinimo);
        System.out.println("Punteggio massimo = " + punteggioMassimo);
        System.out.println("Tempo minimo = " + tempoMinimo);
        System.out.println("Tempo massimo = " + tempoMassimo);
        System.out.println("Passi medi richiesti = " + (passiTotali / solutionsGenerated));
        System.out.println("Tempo medio di risoluzione = " + (((System.currentTimeMillis() - startTime)/solutionsGenerated)));
        System.out.println("Punteggio medio soluzione = " + (punteggioTotale/solutionsGenerated));
        System.out.println("Prestazioni (passi/secondo = " + passiRichiesti / (((float)timeUsed / 1000)));
        System.out.println("==================================================================");


        passiRichiesti = 0;
        model.print();
    }


}
