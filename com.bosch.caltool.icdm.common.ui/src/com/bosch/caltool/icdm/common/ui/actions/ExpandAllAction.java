/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;


/**
 * @author bru2cob
 */
public class ExpandAllAction extends Action {

  /**
   * collapse all constant
   */
  private static final String EXPAND_ALL = "Expand All";

  /**
   * Tree viewer
   */
  private final TreeViewer tViewer;

  /**
   * Level to which the tree has to be collapsed
   */
  private final int level;

  /**
   * @param tViewer Tree viewer
   * @param level Level to which the tree has to be collapsed
   */
  public ExpandAllAction(final TreeViewer tViewer, final int level) {
    super(EXPAND_ALL, ImageManager.getImageDescriptor(ImageKeys.TREE_EXPAND_16X16));
    setToolTipText(EXPAND_ALL);
    setDescription(EXPAND_ALL);

    this.tViewer = tViewer;
    this.level = level;

  }

  /**
   * Collapse the tree to the given level in the constructor
   */
  @Override
  public final void run() {
    try {
      this.tViewer.getControl().setRedraw(false);
      this.tViewer.expandAll();
      this.tViewer.expandToLevel(this.level);
    }
    finally {
      this.tViewer.getControl().setRedraw(true);
    }
  }

}
