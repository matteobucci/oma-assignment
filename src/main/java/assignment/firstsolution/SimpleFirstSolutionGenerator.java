package main.java.assignment.firstsolution;

import main.java.assignment.model.ModelWrapper;

import java.util.Random;

public class SimpleFirstSolutionGenerator implements IFirstSolutionGenerator{

    private final ModelWrapper model;

    public SimpleFirstSolutionGenerator(ModelWrapper model){
        this.model = model;
    }

    @Override
    public void generateFirstSolution() {
        model.clearExamsIfDone();
        for(int i=0; i<model.getExamsNumber(); i++){
            int actualTimeSlot = model.getTimeslotsNumber() -1;
            while(!model.canIAssignWithoutAnyConflict(actualTimeSlot, i)){
                actualTimeSlot--;
                if(actualTimeSlot < 0){
                    //Non riesco a comporre una soluzione valida
                    actualTimeSlot = new Random().nextInt(model.getTimeslotsNumber());
                    model.addConflict(actualTimeSlot, i);
                    break;
                }
            }
            model.assignExams(actualTimeSlot, i, true);
        }
        model.setAsDone();
    }

}
