package main.java.assignment.firstsolution;

import main.java.assignment.model.IModelWrapper;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class AlbertoSolutionGenerator implements IFirstSolutionGenerator {

    private final IModelWrapper model;

    public AlbertoSolutionGenerator(IModelWrapper model){
        this.model = model;
    }

    @Override
    public void generateFirstSolution() {
        model.clearExamsMatrix();
        int[] vettoreEsamiOrdinati = model.orderMatrix(); //////////////////////////////////////////////////


        //STAMPA DI DEBUG
        // for (int i =0 ; i < vettoreEsamiOrdinati.length ; i++)
        //      System.out.print(vettoreEsamiOrdinati[i] + " ");


        //TODO adesso inserisco i base al vettore ordinato
        Set<Integer> esamiAssegnati = new HashSet<>();

        for(int i=0; i<model.getExamsNumber(); i++){
            int actualTimeSlot = model.getTimeslotsNumber() -1;
            // while(model.estimateNumberOfConflictOfExam(actualTimeSlot, i) != 0){
            while(model.estimateNumberOfConflictOfExam(actualTimeSlot, vettoreEsamiOrdinati[i]) != 0){
                actualTimeSlot--;
                if(actualTimeSlot < 0){
                    //Non riesco a comporre una soluzione valida
                    actualTimeSlot = new Random().nextInt(model.getTimeslotsNumber());
                    break;

                }
            }
            model.assignExams(actualTimeSlot, vettoreEsamiOrdinati[i], true);
            esamiAssegnati.add(vettoreEsamiOrdinati[i]);
            //model.assignExams(actualTimeSlot, i, true); ////////////////////////////////////////////////

        }
    }

}