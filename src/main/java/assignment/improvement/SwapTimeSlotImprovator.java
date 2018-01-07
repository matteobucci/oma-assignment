package main.java.assignment.improvement;

import main.java.assignment.model.IModelWrapper;

import java.util.Random;

public class SwapTimeSlotImprovator implements ISolutionImprovator {

    private IModelWrapper model;

    public SwapTimeSlotImprovator(IModelWrapper model){
        this.model = model;
    }

    int i = 0;

    @Override
    public void iterate() {
        model.randomSwapTimeSlot(
                (i++)%model.getTimeslotsNumber()
        );
    }



}
