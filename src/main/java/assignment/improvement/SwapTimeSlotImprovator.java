package main.java.assignment.improvement;

import main.java.assignment.model.IModelWrapper;

import java.util.Random;

public class SwapTimeSlotImprovator implements ISolutionImprovator {

    private IModelWrapper model;
    private Random random = new Random(System.currentTimeMillis());

    public SwapTimeSlotImprovator(IModelWrapper model){
        this.model = model;
    }


    @Override
    public void iterate() {


        for(int i=0; i<model.getTimeslotsNumber(); i++){
            for(int j=model.getTimeslotsNumber()-1; j>=0; j--){
                if(i==j) continue;
                if(model.estimateTimeslotSwapValue(i,j) > 0) {
                    model.swapTimeSlot(i, j);
                    return;
                }
            }
        }


        for(int a=0; a<model.getExamsNumber() / 3; a++){
            int i = random.nextInt(model.getTimeslotsNumber());
            int j = random.nextInt(model.getTimeslotsNumber());

            model.swapTimeSlot(i,j);
        }




        /*
        double punteggioIniziale = model.getActualScore();
        double punteggioFinale;

        int i = -1;
        int j = -1;
        int passi = 0;
        double delta = 0;


        do{

            passi++;

            if(i != -1 && j != -1){
                model.swapTimeSlot(i,j);
            }

            i = random.nextInt(model.getTimeslotsNumber());
            j = random.nextInt(model.getTimeslotsNumber());

            model.swapTimeSlot(i,j);
            punteggioFinale = model.getActualScore();

        }while(i == j || punteggioFinale > punteggioIniziale && passi < 20);


        model.swapTimeSlot(i, j);

        double estimatedPoint = punteggioIniziale - model.estimateTimeslotSwapValue(i, j);
        double actualPoint = model.getActualScore();

        System.out.println("Punteggio previsto: " + estimatedPoint);
        System.out.println("Punteggio reale: " + actualPoint);

        System.out.println("Differenza tra reale e prevista: " + (estimatedPoint-actualPoint));


        */

        /*

        int i,j;
        do{
            i = random.nextInt(model.getTimeslotsNumber());
            j= random.nextInt(model.getTimeslotsNumber());
        }while(model.estimateTimeslotSwapValue(i,j) < 0);

        model.swapTimeSlot(i,j);

        */

    }



}
