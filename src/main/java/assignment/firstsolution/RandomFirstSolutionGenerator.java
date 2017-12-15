package main.java.assignment.firstsolution;

import main.java.assignment.model.ModelWrapper;
import main.java.assignment.util.TimedSolutionGenerator;

import java.util.Random;

public class RandomFirstSolutionGenerator implements IFirstSolutionGenerator{

    Random random = new Random(System.currentTimeMillis());
    int tentativi = 0;

    ModelWrapper model;

    public RandomFirstSolutionGenerator(ModelWrapper model){
        this.model = model;
    }

    @Override
    public void generateFirstSolution() {
        model.clearExamsIfDone();
        for(int i=0; i<model.getExamsNumber(); i++){
            tentativi = 0;
            int actualTimeSlot = random.nextInt(model.getTimeslotsNumber());
            while(!model.canIAssignExamHere(actualTimeSlot, i) && tentativi < model.getTimeslotsNumber()*2){
                actualTimeSlot = random.nextInt(model.getTimeslotsNumber());
                tentativi++;
            }

            if(tentativi == model.getTimeslotsNumber()*2) model.addConflict(actualTimeSlot, i);

            model.assignExams(actualTimeSlot, i, true);
        }
        model.setAsDone();
    }

}
