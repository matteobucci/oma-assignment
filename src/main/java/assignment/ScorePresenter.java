package main.java.assignment;

import main.java.assignment.model.IModelWrapper;
import main.java.assignment.view.ModelStatsViewer;

public class ScorePresenter implements IModelWrapper.ModelListener {

    private IModelWrapper wrapper;
    private ModelStatsViewer viewer;

    public ScorePresenter(ModelStatsViewer viewer, IModelWrapper wrapper){
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
        viewer.printConflicts(wrapper.getConflictNumber());
    }
    
}
