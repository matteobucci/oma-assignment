package main.java.assignment;

import main.java.assignment.heuristic.IHeuristic;
import main.java.assignment.model.AssignmentModel;

public class EuristicThread extends Thread{

    IHeuristic euristic;

    public EuristicThread(IHeuristic euristic){
        super(() -> {
            while(true) euristic.iterate();
        });
        this.euristic = euristic;
    }

    public AssignmentModel getBestSolution() {
        return euristic.getBestAssignmentModel();
    }


}
