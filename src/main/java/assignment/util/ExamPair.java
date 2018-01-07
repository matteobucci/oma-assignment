package main.java.assignment.util;

import javafx.util.Pair;
import main.java.assignment.model.AssignmentModel;

public class ExamPair extends Pair<Double, AssignmentModel> {

    public ExamPair(Double key, AssignmentModel value) {
        super(key, value);
    }

    public double getScore(){
        return getKey();
    }

    public AssignmentModel getModel(){
        return getValue();
    }

}
