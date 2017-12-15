package main.java.assignment;

import main.java.assignment.model.IModelWrapper;
import main.java.assignment.view.ModelViewer;

public class ModelPresenter implements IModelWrapper.ModelListener {

    private IModelWrapper wrapper;
    private ModelViewer viewer;

    public ModelPresenter(ModelViewer viewer, IModelWrapper wrapper){
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
        viewer.printModel(wrapper);
    }

}
