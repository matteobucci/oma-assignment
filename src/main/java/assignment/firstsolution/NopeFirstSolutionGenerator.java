package main.java.assignment.firstsolution;
import java.util.ArrayList;
import java.util.List;
import main.java.assignment.model.ModelWrapper;
import main.java.assignment.util.TimedSolutionGenerator;

import java.util.Random;

public class NopeFirstSolutionGenerator implements IFirstSolutionGenerator {

    private final ModelWrapper model;

    public NopeFirstSolutionGenerator(ModelWrapper model){
        this.model = model;
    }

    Random random = new Random(System.currentTimeMillis());
    int tentativi = 0;

    private static final int MAX_ATTEMPT = 20;

    int bestActualIndex = -1;
    int bestActualValue = Integer.MAX_VALUE;

    private boolean canAddByTolerance(int tolerance, int conflicts){
        //Higher the tolerance higher the probability to return true.
        //Conflicts decrement success probability.
        //Yeah. This is RANDOM
        return random.nextInt(100) > (tolerance - conflicts*(tolerance/100));
    }

    @Override
    public void generateFirstSolution() {
        model.clearExamsIfDone();
        int exm_left = model.getExamsNumber();
        List<Integer> skippedExams = new ArrayList();
        List<Integer> slots = new ArrayList();
        for(int i=0; i<model.getExamsNumber(); i++){
            slots.add(i);
        }
        int tolerance = 20;
        int T = model.getTimeslotsNumber();
        //Step 1: Populate the solution with non-conflicting exams
        while(exm_left > 0){
            //Pick an exam
            int cur_exm = model.getExamsNumber() - exm_left;
            //For each timeslot
            for(int t=0; t < T; t++) {
                if (model.canIAssignExamHere(t, cur_exm)){
                    int conflicts = model.howManyConflictAnExamHave(t, cur_exm);
                    if(conflicts <= 0 || canAddByTolerance(tolerance, conflicts) )
                    {
                        slots.remove(Integer.valueOf(t));
                        model.assignExams(t, cur_exm, true);
                        break;
                    } else {
                        continue;
                    }
                }
            }
            exm_left--;
        }
        //Step 2: Insert skpped exams in the free slots left, randomly.
        while(skippedExams.size() > 0){
            int exm = skippedExams.get(0);
            int slotIndex = random.nextInt(slots.size()-1);
            int slot = slots.get(slotIndex);
            int choiceConflicts = model.howManyConflictAnExamHave(slot, exm);
            model.assignExams(slot, exm, true);
            slots.remove(slotIndex);
            skippedExams.remove(0);
            model.addConflict(slot, exm);

        }


        model.setAsDone();
    }

}