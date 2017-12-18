package main.java.assignment.euristic;

import main.java.assignment.firstsolution.IFirstSolutionGenerator;
import main.java.assignment.improvement.ISolutionImprovator;
import main.java.assignment.model.IModelWrapper;
import main.java.assignment.model.ModelWrapper;
import main.java.assignment.solution.ISolutionGenerator;

public abstract class IEuristic {

    protected IFirstSolutionGenerator firstSolutionGenerator;
    protected ISolutionGenerator solutionGenerator;
    protected ISolutionImprovator solutionImprovator;

    protected IModelWrapper model;

    public IEuristic(IModelWrapper model){
        this.model = model;
    }

    public abstract void iterate();


}
