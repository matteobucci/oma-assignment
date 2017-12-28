package main.java.assignment.scorecalculator;

import main.java.assignment.model.IModelWrapper;

import java.util.Set;

public class DeltaScoreCalculator implements IDeltaScoreCalculator {

    private boolean debug = false;

    public DeltaScoreCalculator(){
        this(false);
    }

    public DeltaScoreCalculator(boolean debug) {
        this.debug = debug;
    }

    @Override
    public double getScore(IModelWrapper model, int examIndex, int fromTimeSlot, int toTimeSlot) {

        if(debug) {
            System.out.println("Calcolo punteggio spostando l'esame " + examIndex + " dal timeslot " + fromTimeSlot + " al timeslot " + toTimeSlot);
        }

        int[][] conflictMatrix = model.getAssignmentModel().getConflictMatrix();
        //TODO: CONTROLLARE CHE LA MATRICE DEI CONFLITTI ABBIA LA DIAGONALE A 0
        double actualScore = model.getActualScore();
        double actualPenality = 0; //Rappresenta l(s) -> Penalità che l'esame che vado a spostare mi da attualmente
        double nextPenality = 0; //Rappresenta l(v) -> Penalità che l'esame che vado a spostare mi darà

        //Calcolo l(s)
        int startIndex = fromTimeSlot -5;
        if(startIndex < 0) startIndex = 0;


        for(int i=startIndex; (i<fromTimeSlot+5 && i<model.getTimeslotsNumber()); i++){
            if(i == fromTimeSlot) continue;
            Set<Integer> set = model.getTimeslotExams(i);
            for(Integer actual : set){
                //Penalità tra l'esame che sposto e l'esame considerato nei timeslot successivi
                actualPenality += (conflictMatrix[examIndex][actual] * Math.pow(2, 5-Math.abs(i-fromTimeSlot)) / model.getStudentNumber());
            }
        }

        int toStartIndex = toTimeSlot -5;
        if(toStartIndex < 0) toStartIndex = 0;

        for(int i=toStartIndex; (i<toTimeSlot+5 && i<model.getTimeslotsNumber()); i++){
            if(i == fromTimeSlot) continue;
            Set<Integer> set = model.getTimeslotExams(i);
            for(Integer actual : set){
                //Calcolo identico al precedente
                nextPenality += (conflictMatrix[examIndex][actual] * Math.pow(2, 5-Math.abs(i-toTimeSlot)) / model.getStudentNumber());
            }
        }

        if(debug){
            System.out.println("Punteggio corrente: " + actualScore);
            System.out.println("Penalità corrente: " + actualPenality);
            System.out.println("Penalità futura: " + nextPenality);
            System.out.println("Nuovo punteggio: " + (actualScore - actualPenality + nextPenality));

        }

        //Computo la soluzione
        return actualScore - actualPenality + nextPenality;
    }


}
