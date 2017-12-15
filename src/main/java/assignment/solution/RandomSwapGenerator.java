package main.java.assignment.solution;

import javafx.util.Pair;
import main.java.assignment.model.ModelWrapper;

import java.util.Random;

public class RandomSwapGenerator extends SolutionGeneration {

    public RandomSwapGenerator(ModelWrapper model) {
        super(model);
    }



    @Override
    public void iterate() {
        Pair<Integer, Integer> conf = model.getConflict();
        int startConf = model.howManyConflictAnExamHave(conf.getKey(), conf.getValue());
        for (int i = 0; i < model.getTimeslotsNumber(); i++) {
            int newConf = model.howManyConflictAnExamHave(i, conf.getValue());
            if (startConf > newConf) {
                model.assignExams(conf.getKey(), conf.getValue(), false);
                model.assignExams(i, conf.getValue(), true);
                if (newConf == 0) {
                    System.out.println("Risolto conflitto per " + conf.getValue());
                } else {
                    System.out.println("Prima "+ conf.getValue()+" conflittava con: " + startConf + " esami ora con " + newConf);
                    model.addConflict(i, conf.getValue());
                }
                break;
            }
            if(i == model.getTimeslotsNumber()-1){
                System.out.println("Non sono riuscito a migliorare la situazione per" + conf.getValue());
                int newSlot = new Random().nextInt(model.getTimeslotsNumber());
                model.assignExams(newSlot, conf.getValue(), true);
                model.assignExams(conf.getKey(), conf.getValue(), false);
                model.addConflict(newSlot, conf.getValue());
            }
        }
    }
}
