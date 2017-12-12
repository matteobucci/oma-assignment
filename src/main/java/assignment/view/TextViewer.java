package main.java.assignment.view;

import main.java.assignment.model.ModelWrapper;

public class TextViewer implements ModelViewer {

    @Override
    public void printModel(ModelWrapper model) {
        for(int i=0; i<model.getTimeslotsNumber(); i++){
            System.out.println("Timeslot " + i + ":");
            for(Integer actual: model.getExamAssignedToTimeSlot(i)){
                System.out.print(actual);
                System.out.print("|");
            }
            System.out.println("--------------------------------------------------------");
        }
    }

}
