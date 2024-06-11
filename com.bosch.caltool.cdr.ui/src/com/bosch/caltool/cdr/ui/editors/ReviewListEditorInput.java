/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewListEditorDataHandler;
import com.bosch.caltool.icdm.common.util.CommonUtils;


/**
 * @author mkl2cob
 */
public class ReviewListEditorInput implements IEditorInput {


  /**
   * constant for string "Review Results of "
   */
  private static final String REVIEW_RESULTS_OF_STR = "Review Results of ";

  private IStructuredSelection selectedResults;

  private final ReviewListEditorDataHandler dataHandler;

  /**
   * Editor input creation
   *
   * @param pidcTreeNode pidc tree node
   */
  public ReviewListEditorInput(final PidcTreeNode pidcTreeNode) {
    this.dataHandler = new ReviewListEditorDataHandler(pidcTreeNode);
  }

  /**
   * @return the dataHandler
   */
  public ReviewListEditorDataHandler getDataHandler() {
    return this.dataHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getAdapter(final Class adapter) {
    // Not applicable
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean exists() {
    // Not applicable
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ImageDescriptor getImageDescriptor() {
    // Not applicable
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return REVIEW_RESULTS_OF_STR + getsEffectiveName();
  }

  /**
   * gets the Name of Review Result Editor
   *
   * @return effective name
   */
  // ICDM-2177
  public String getsEffectiveName() {
    return getDataHandler().getEffectiveName();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public IPersistableElement getPersistable() {
    // Not applicable
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText() {
    return REVIEW_RESULTS_OF_STR + getsEffectiveName();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.dataHandler == null) ? 0 : this.dataHandler.hashCode());
    result = (prime * result) + ((this.selectedResults == null) ? 0 : this.selectedResults.hashCode());
    return result;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ReviewListEditorInput other = (ReviewListEditorInput) obj;

    return !CommonUtils.isNotEqual(getsEffectiveName(), other.getsEffectiveName());
  }


  /**
   * @return the selectedResults
   */
  public IStructuredSelection getSelectedResults() {
    return this.selectedResults;
  }

  /**
   * @param selectedResults the selectedResults to set
   */
  public void setSelectedResults(final IStructuredSelection selectedResults) {
    this.selectedResults = selectedResults;
  }


}
