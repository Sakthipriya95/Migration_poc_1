/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.services;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

/**
 * Enables/disables various buttons programatically
 *
 * @author bne4cob
 */
public class CommandState extends AbstractSourceProvider {

  /**
   * Enabled value
   */
  private static final String ENABLED = "ENABLED";

  /**
   * Disabled value
   */
  private static final String DISABLED = "DISABLED";

  /**
   * Service variable for undo status
   */
  public static final String VAR_UNDO_STATUS = "com.bosch.caltool.icdm.product.undostatus";

  /**
   * Service variable for redo status
   */
  public static final String VAR_REDO_STATUS = "com.bosch.caltool.icdm.product.redostatus";


  /**
   * Service variable for redo status
   */
  public static final String VAR_EXPORT_STATUS = "com.bosch.caltool.icdm.product.reportexportstatus";

  /**
   * Status of undo button
   */
  private boolean undoEnabled;

  /**
   * Status of redo button
   */
  private boolean redoEnabled;
  /**
   * Status of Export excel button Icdm-630
   */
  private boolean exportEnabled;

  @Override
  public void dispose() {
    // Not applicable
  }

  @Override
  public final Map<String, String> getCurrentState() {
    final Map<String, String> map = new HashMap<>(3);

    String value = this.undoEnabled ? ENABLED : DISABLED;
    map.put(VAR_UNDO_STATUS, value);

    value = this.redoEnabled ? ENABLED : DISABLED;
    map.put(VAR_REDO_STATUS, value);

    value = this.exportEnabled ? ENABLED : DISABLED;
    map.put(VAR_EXPORT_STATUS, value);


    return map;
  }

  @Override
  public final String[] getProvidedSourceNames() {
    return new String[] { VAR_UNDO_STATUS, VAR_REDO_STATUS, VAR_EXPORT_STATUS };
  }

  /**
   * Set thte status of Undo button
   *
   * @param undoEnabled whether the button is enabled or not
   */
  public final void setUndoStatus(final boolean undoEnabled) {
    this.undoEnabled = undoEnabled;
    final String value = this.undoEnabled ? ENABLED : DISABLED;
    fireSourceChanged(ISources.WORKBENCH, VAR_UNDO_STATUS, value);
  }


  /**
   * Set thte status of Redo button
   *
   * @param redoEnabled whether the button is enabled or not
   */
  public final void setRedoStatus(final boolean redoEnabled) {
    this.redoEnabled = redoEnabled;
    final String value = this.redoEnabled ? ENABLED : DISABLED;
    fireSourceChanged(ISources.WORKBENCH, VAR_REDO_STATUS, value);
  }

  /**
   * @param exportEnabled exportEnabled
   */
  public void setExportService(final boolean exportEnabled) {
    this.exportEnabled = exportEnabled;
    final String value = this.exportEnabled ? ENABLED : DISABLED;
    fireSourceChanged(ISources.WORKBENCH, VAR_EXPORT_STATUS, value);

  }

}
