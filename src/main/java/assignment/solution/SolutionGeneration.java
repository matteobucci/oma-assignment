package main.java.assignment.solution;

import main.java.assignment.model.IModelWrapper;

abstract public class  SolutionGeneration implements ISolutionGenerator{

    IModelWrapper model;

    public SolutionGeneration(IModelWrapper model){
        this.model = model;
    }


}
