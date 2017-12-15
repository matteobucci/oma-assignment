package main.java.assignment.solution;

import javafx.util.Pair;
import main.java.assignment.model.IModelWrapper;

import java.util.Random;

public class RandomSolutionGenerator extends SolutionGeneration {

    IModelWrapper model;

    public RandomSolutionGenerator(IModelWrapper model){
        super(model);
        this.model = model;
    }

    @Override
    public void iterate() {
        if(model.getConflictNumber() != 0){
            int actualExam = model.getRandomConflictedExam();
            int oldConf = model.getNumberOfConflictOfExam(actualExam);
            int oldTimeSlot =  model.getExamTimeslot(actualExam);



            for (int i = 0; i < model.getTimeslotsNumber(); i++) {
                int newConf = model.estimateNumberOfConflictOfExam(i, actualExam);
                if(newConf < oldConf){
                    System.out.println("L'esame " + actualExam + " aveva " + oldConf + " conflitti ora ne ha " + newConf);
                    System.out.println("Sposto l'esame dal timeslot " + oldTimeSlot + " al timeslot " + i);
                    model.assignExams(oldTimeSlot, actualExam, false);
                    model.assignExams(i, actualExam, true);
                    return;
                }
            }

        }
    }

}
