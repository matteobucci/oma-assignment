package main.java.assignment.view;

import main.java.assignment.model.IModelWrapper;

public class TextViewer implements ModelViewer {

    @Override
    public void printModel(IModelWrapper model) {
        for(int i=0; i<model.getTimeslotsNumber(); i++){
            System.out.println("Timeslot " + i + ":");
            for(Integer actual: model.getTimeslotExams(i)){
                System.out.print(actual);
                System.out.print("|");
            }
            System.out.println("--------------------------------------------------------");
        }
    }

}
