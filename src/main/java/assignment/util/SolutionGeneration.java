package main.java.assignment.util;

import main.java.assignment.model.ModelWrapper;

abstract public class  SolutionGeneration {

    ModelWrapper model;

    public SolutionGeneration(ModelWrapper model){
        this.model = model;
    }

    abstract void iterate();



}
