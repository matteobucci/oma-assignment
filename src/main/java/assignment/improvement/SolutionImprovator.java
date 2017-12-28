package main.java.assignment.improvement;

import main.java.assignment.model.IModelWrapper;
import main.java.assignment.scorecalculator.IDeltaScoreCalculator;

import java.util.Random;

public class SolutionImprovator implements ISolutionImprovator {

    private IModelWrapper model;

    Random random = new Random(System.currentTimeMillis());

    public  SolutionImprovator(IModelWrapper model){
        this.model = model;
    }


    @Override
    public void iterate() {
        int maxAttempt = 0;
        int selectedExam = random.nextInt(model.getExamsNumber());
        int actualTimeSlot = model.getExamTimeslot(selectedExam); //TODO: PER LE PRESTAZIONI POTREI TORNARE TUTTI E DUE GLI INDICI INSIEME
        int nextTimeSlot = random.nextInt(model.getTimeslotsNumber());

        while((model.estimateNumberOfConflictOfExam(nextTimeSlot, selectedExam) != 0 || (nextTimeSlot == actualTimeSlot)) && maxAttempt < model.getTimeslotsNumber()){
            //Nel caso ci siano conflitti e lo spostamento non sia accettabile
            nextTimeSlot = (nextTimeSlot + 1) % model.getTimeslotsNumber();
            maxAttempt++;
        }

        if(maxAttempt == model.getTimeslotsNumber()){
           // System.out.println("Ci rinuncio, non ci sono mosse possibili per questo esame senza sputtanare tutto");
            return;
        }


        if(model.getScoreOfAMove(selectedExam, actualTimeSlot, nextTimeSlot) < model.getActualScore()*1.0001){
            model.assignExams(actualTimeSlot, selectedExam, false);
            model.assignExams(nextTimeSlot, selectedExam, true);
            System.out.println("Effettuo una mossa di move");
        }



    }



}
