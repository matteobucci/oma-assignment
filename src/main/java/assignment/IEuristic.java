package main.java.assignment;

import main.java.assignment.firstsolution.IFirstSolutionGenerator;
import main.java.assignment.model.ModelWrapper;
import main.java.assignment.solution.ISolutionGenerator;

public abstract class IEuristic {

    protected IFirstSolutionGenerator firstSolutionGenerator;
    protected ISolutionGenerator solutionGenerator;

    public IEuristic(IFirstSolutionGenerator firstSolutionGenerator, ISolutionGenerator solutionGenerator){
        this.firstSolutionGenerator = firstSolutionGenerator;
        this.solutionGenerator = solutionGenerator;
    }

    public abstract void iterate();


}
