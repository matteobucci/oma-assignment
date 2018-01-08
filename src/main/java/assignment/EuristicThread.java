package main.java.assignment;

import main.java.assignment.euristic.IEuristic;
import main.java.assignment.model.AssignmentModel;

public class EuristicThread extends Thread{

    IEuristic euristic;

    public EuristicThread(IEuristic euristic){
        super(() -> {
            while(true) euristic.iterate();
        });
        this.euristic = euristic;
    }

    public AssignmentModel getBestSolution() {
        return euristic.getBestAssignmentModel();
    }


}
