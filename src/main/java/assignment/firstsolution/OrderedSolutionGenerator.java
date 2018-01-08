package main.java.assignment.firstsolution;

import main.java.assignment.model.IModelWrapper;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class OrderedSolutionGenerator implements IFirstSolutionGenerator {

    private final IModelWrapper model;

    public OrderedSolutionGenerator(IModelWrapper model){
        this.model = model;
    }

    @Override
    public void generateFirstSolution() {
        model.clearExamsMatrix();
        int[] vettoreEsamiOrdinati = model.orderMatrix(); //////////////////////////////////////////////////
        int attempt = 0;
        Random random =  new Random(System.currentTimeMillis());


        //STAMPA DI DEBUG
        // for (int i =0 ; i < vettoreEsamiOrdinati.length ; i++)
        //      System.out.print(vettoreEsamiOrdinati[i] + " ");


        //TODO adesso inserisco i base al vettore ordinato
        Set<Integer> esamiAssegnati = new HashSet<>();
        int firstTimeSlot = random.nextInt(model.getTimeslotsNumber());

        for(int i=0; i<model.getExamsNumber(); i++){
            int actualTimeSlot = firstTimeSlot;
            attempt = 0;
            // while(model.estimateNumberOfConflictOfExam(actualTimeSlot, i) != 0){
            while(model.estimateNumberOfConflictOfExam(actualTimeSlot, vettoreEsamiOrdinati[i]) != 0){
                actualTimeSlot++;
                attempt++;
                actualTimeSlot = actualTimeSlot % model.getTimeslotsNumber();
                if(attempt >= model.getTimeslotsNumber()){
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