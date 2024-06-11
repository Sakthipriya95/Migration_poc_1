/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.usecase.ui.views.listeners;


/**
 * A listener which updates the UseCaseDetailsPage
 * 
 * @author jvi6cob
 */
public interface UseCaseListener {

  /**
   * Refresh tree
   */
  void refreshTreeViewer(boolean deselectAll);

}
