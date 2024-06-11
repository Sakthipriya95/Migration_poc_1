package com.bosch.caltool.cdr.ui.views.providers;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.cdr.ui.editors.ReviewResultEditor;
import com.bosch.caltool.cdr.ui.editors.pages.ReviewResultParamDetPage;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.model.cdr.CDRResultFunction;

/**
 * @author dmo5cob
 */
public class ReviewResultTreeContentProvider implements ITreeContentProvider {

  /**
   * Constant string for Functions
   */
  private static final String FUNCTIONS = "Functions";

  // page instance to know whether parameters should be added
  private final AbstractFormPage page;

  private static final Object[] EMPTY_ARRAY = {};
  private final ReviewResultClientBO resultData;

  /**
   * @param editor ReviewResultEditor
   * @param page AbstractFormPage
   */
  public ReviewResultTreeContentProvider(final ReviewResultEditor editor, final AbstractFormPage page) {
    super();
    this.page = page;
    this.resultData = editor.getEditorInput().getResultData();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    // not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object[] getElements(final Object inputElement) {
    if ((inputElement instanceof String) && "".equals(inputElement)) {
      return new Object[] { FUNCTIONS };
    }


    else if ((inputElement instanceof String) && ((String) inputElement).equals(FUNCTIONS)) {
      return this.resultData.getFunctions().toArray();
    }

    // Generate children for CDRFunction only for ParamDetailsPage
    else if ((inputElement instanceof CDRResultFunction) && (this.page instanceof ReviewResultParamDetPage)) {
      return this.resultData.getParameters((CDRResultFunction) inputElement).toArray();
    }

    return EMPTY_ARRAY;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object[] getChildren(final Object parentElement) {

    // Generate children for RootNode
    if ((parentElement instanceof String) && ((String) parentElement).equals(FUNCTIONS)) {
      return this.resultData.getFunctions().toArray();
    }
    // Generate children for CDRFunction only for ParamDetailsPage
    if ((parentElement instanceof CDRResultFunction) && (this.page instanceof ReviewResultParamDetPage)) {
      return this.resultData.getParameters((CDRResultFunction) parentElement).toArray();
    }

    return EMPTY_ARRAY;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getParent(final Object parent) {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasChildren(final Object element) {
    // CDRResultFunction has children only in ParamDetailsPage tree
    if ((element instanceof CDRResultFunction) && (this.page instanceof ReviewResultParamDetPage)) {
      return !this.resultData.getParameters((CDRResultFunction) element).isEmpty();
    }
    return element instanceof String;
  }


}
