/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.rcputils.nebula.gridviewer;

import java.util.Collection;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;


/**
 * @author bru2cob
 */
// ICDM-1070
public class CustomTreeViewer extends TreeViewer {

  /**
   * String buffer initial size
   */
  private static final int SIZE = 40;
  /**
   * Instance of status line manager
   */
  private IStatusLineManager statusLineManager;


  /**
   * @param tree
   */
  public CustomTreeViewer(Tree tree) {
    super(tree);
    // TODO Auto-generated constructor stub
  }


  /**
   * @param body comp
   * @param style style
   */
  public CustomTreeViewer(Composite body, int style) {
    super(body, style);
  }


  /**
   * /** {@inheritDoc} Updation of status after refresh is done
   */
  @Override
  public void refresh() {
    super.refresh();
    setStatusBarMessage();
  }

  /**
   * input for status line
   */
  protected void setStatusBarMessage() {
    Collection<?> items = null;
    int totalItemCount = 0;
    if ((null != getInput()) && !(getInput().toString().isEmpty())) {
      items = (Collection<?>) getInput();
    }
    if (items != null) {
      totalItemCount = items.size();
    }
    int filteredItemCount = getTree().getItemCount();
    updateStatusBar(totalItemCount, filteredItemCount);
  }

  /**
   * Updating status
   * 
   * @param totalItemCount total attributes in the table
   * @param filteredItemCount filtered attributes in the table
   */
  public void updateStatusBar(final int totalItemCount, final int filteredItemCount) {
    final StringBuilder buf = new StringBuilder(SIZE);
    buf.append("Displaying : ").append(filteredItemCount).append(" out of " + totalItemCount + " records ");
    if (totalItemCount == filteredItemCount) {
      this.statusLineManager.setErrorMessage(null);
      this.statusLineManager.setMessage(buf.toString());
    }
    else {
      this.statusLineManager.setErrorMessage(buf.toString());
    }
    this.statusLineManager.update(true);

  }

  /**
   * @return the statusLineManager
   */
  public IStatusLineManager getStatusLineManager() {
    return this.statusLineManager;
  }

  /**
   * @param statusLineManager the statusLineManager to set
   */
  public void setStatusLineManager(final IStatusLineManager statusLineManager) {
    this.statusLineManager = statusLineManager;
  }


}
