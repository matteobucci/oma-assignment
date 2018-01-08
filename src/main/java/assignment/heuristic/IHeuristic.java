package main.java.assignment.heuristic;

import main.java.assignment.firstsolution.IFirstSolutionGenerator;
import main.java.assignment.improvement.ISolutionImprovator;
import main.java.assignment.model.AssignmentModel;
import main.java.assignment.model.IModelWrapper;
import main.java.assignment.solution.ISolutionGenerator;

public abstract class IHeuristic {

    protected IFirstSolutionGenerator firstSolutionGenerator;
    protected ISolutionGenerator solutionGenerator;
    protected ISolutionImprovator solutionImprovator;

    protected IModelWrapper model;

    public IHeuristic(IModelWrapper model){
        this.model = model;
    }

    public abstract void iterate();

    public AssignmentModel getBestAssignmentModel(){
        return model.getCalculator().getBestUntilNow();
    }


}
