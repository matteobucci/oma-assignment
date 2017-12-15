package main.java.assignment.firstsolution;

import main.java.assignment.model.IModelWrapper;

import java.util.Random;

public class SimpleFirstSolutionGenerator implements IFirstSolutionGenerator{

    private final IModelWrapper model;

    public SimpleFirstSolutionGenerator(IModelWrapper model){
        this.model = model;
    }

    @Override
    public void generateFirstSolution() {
        model.clearExamsMatrix();
        for(int i=0; i<model.getExamsNumber(); i++){
            int actualTimeSlot = model.getTimeslotsNumber() -1;
            while(model.estimateNumberOfConflictOfExam(actualTimeSlot, i) != 0){
                actualTimeSlot--;
                if(actualTimeSlot < 0){
                    //Non riesco a comporre una soluzione valida
                    actualTimeSlot = new Random().nextInt(model.getTimeslotsNumber());
                    break;
                }
            }
            model.assignExams(actualTimeSlot, i, true);
        }
    }

}
