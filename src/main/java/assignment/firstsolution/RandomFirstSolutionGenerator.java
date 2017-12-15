package main.java.assignment.firstsolution;

import main.java.assignment.model.IModelWrapper;

import java.util.Random;

public class RandomFirstSolutionGenerator implements IFirstSolutionGenerator{

    Random random = new Random(System.currentTimeMillis());
    int tentativi = 0;

    IModelWrapper model;

    public RandomFirstSolutionGenerator(IModelWrapper model){
        this.model = model;
    }

    @Override
    public void generateFirstSolution() {
        model.clearExamsMatrix();
        for(int i=0; i<model.getExamsNumber(); i++){
            tentativi = 0;
            int actualTimeSlot = random.nextInt(model.getTimeslotsNumber());
            while(model.estimateNumberOfConflictOfExam(actualTimeSlot, i) != 0 && tentativi < model.getTimeslotsNumber()*2){
                actualTimeSlot = random.nextInt(model.getTimeslotsNumber());
                tentativi++;
            }

            model.assignExams(actualTimeSlot, i, true);
        }
    }

}
