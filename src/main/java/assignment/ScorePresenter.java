package main.java.assignment;

import main.java.assignment.model.ModelWrapper;
import main.java.assignment.view.ModelStatsViewer;
import main.java.assignment.view.ModelViewer;

public class ScorePresenter implements ModelWrapper.ModelListener {

    private ModelWrapper wrapper;
    private ModelStatsViewer viewer;

    public ScorePresenter(ModelStatsViewer viewer, ModelWrapper wrapper){
        this.wrapper = wrapper;
        this.viewer = viewer;
        init();
    }

    private void init(){
        wrapper.setListener(this);
        onModelChanged();
    }


    @Override
    public void onModelChanged() {
        viewer.printdScore(wrapper.getActualScore());
        viewer.printConflicts(wrapper.getNumberOfConflicts());
    }
    
}
