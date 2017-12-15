package main.java.assignment.solution;

import main.java.assignment.model.ModelWrapper;

abstract public class  SolutionGeneration implements ISolutionGenerator{

    ModelWrapper model;

    public SolutionGeneration(ModelWrapper model){
        this.model = model;
    }


}
