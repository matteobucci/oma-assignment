package main.java.assignment.util;

import javafx.util.Pair;
import main.java.assignment.model.AssignmentModel;

public class ModelPair extends Pair<Double, AssignmentModel> {

    public ModelPair(Double key, AssignmentModel value) {
        super(key, value);
    }

    public double getScore(){
        return getKey();
    }

    public AssignmentModel getModel(){
        return getValue();
    }

}
