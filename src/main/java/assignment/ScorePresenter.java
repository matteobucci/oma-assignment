package main.java.assignment;

import main.java.assignment.model.ModelWrapper;
import main.java.assignment.view.ModelStatsViewer;
import main.java.assignment.view.ModelViewer;

public class ScorePresenter implements ModelWrapper.ModelListener {

    private ModelWrapper wrapper;
    private ModelStatsViewer viewer;
    private IScoreCalculator calculator;

    public ScorePresenter(ModelStatsViewer viewer, ModelWrapper wrapper){
        this.wrapper = wrapper;
        this.viewer = viewer;
        init();
    }

    private void init(){
        calculator = new ScoreCalculator();
        wrapper.setListener(this);
        onModelChanged();
    }


    @Override
    public void onModelChanged() {
        viewer.printdScore(calculator.getScore(wrapper.getAssignmentModel()));
        viewer.printConflicts(wrapper.getNumberOfConflicts());
    }
    
}
