package main.java.assignment.improvement;

import main.java.assignment.model.IModelWrapper;

import java.util.Random;

public class SwapSolutionImprovator implements ISolutionImprovator {

    private IModelWrapper model;

    Random random = new Random(System.currentTimeMillis());

    public SwapSolutionImprovator(IModelWrapper model){
        this.model = model;
    }


    @Override
    public void iterate() {
        int selectedExam = random.nextInt(model.getExamsNumber());
        int actualTimeSlot = model.getExamTimeslot(selectedExam); //TODO: PER LE PRESTAZIONI POTREI TORNARE TUTTI E DUE GLI INDICI INSIEME
        int selectedExam2;
        int actualTimeslot2;
        boolean continueLoop = true;
        int attempt = 0;

        do{

            do{
                selectedExam2 = random.nextInt(model.getExamsNumber());
                actualTimeslot2 = model.getExamTimeslot(selectedExam2);

            }while(actualTimeslot2 == actualTimeSlot);

            if(model.estimateNumberOfConflictOfExam(actualTimeslot2, selectedExam) == 0
                    && model.estimateNumberOfConflictOfExam(actualTimeSlot, selectedExam2) == 0){
                if(model.getActualScore()*2 - (model.getScoreOfAMove(selectedExam, actualTimeSlot, actualTimeslot2) + model.getScoreOfAMove(selectedExam2, actualTimeslot2, actualTimeSlot)) > 0){
                    model.assignExams(actualTimeSlot, selectedExam, false);
                    model.assignExams(actualTimeslot2, selectedExam, true);
                    model.assignExams(actualTimeslot2, selectedExam2, false);
                    model.assignExams(actualTimeSlot, selectedExam2, true);

                    System.out.println("Nuovo punteggio: " + model.getActualScore());
                    continueLoop = false;
                }

                attempt++;



            }else{
                attempt++;
            }

            if(attempt > model.getExamsNumber()){
                continueLoop = false;
            }
        }while(continueLoop);


    }



}
