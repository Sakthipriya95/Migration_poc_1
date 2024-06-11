/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.bosch.caltool.icdm.client.bo.comphex.CompHexWithCDFxDataHandler;

/**
 * @author mkl2cob
 */
public class CompHexWithCDFxEditorInput implements IEditorInput {

  /**
   * the data after comparsion is held in this method
   */
  private CompHexWithCDFxDataHandler compHexDataHdlr;
  /**
   * editor name
   */
  private String editorName;
  /**
   * User Notification Message to show message about changes in 'WP finished' Status
   */
  private String userNotificationMsg;


  /**
   * {@inheritDoc}
   */
  @Override
  public Object getAdapter(final Class arg0) {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean exists() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ImageDescriptor getImageDescriptor() {
    // NA
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getConcatdName();
  }

  /**
   * @return compute editor name
   */
  private String getConcatdName() {
    if (this.editorName == null) {
      String varName = (this.compHexDataHdlr.getSelctedVar()) == null ? "<NO-VARIANT>"
          : this.compHexDataHdlr.getSelctedVar().getName();
      this.editorName = "Comparison - " + this.compHexDataHdlr.getHexFileName() + "  and \nReview Results of " +
          this.compHexDataHdlr.getA2lFile().getFilename() + "\nVariant - " + varName;
    }
    return this.editorName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IPersistableElement getPersistable() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText() {
    return getConcatdName();
  }

  /**
   * @return the compHexDataHdlr
   */
  public CompHexWithCDFxDataHandler getCompHexDataHdlr() {
    return this.compHexDataHdlr;
  }

  /**
   * @param compHexDataHdlr the compHexDataHdlr to set
   */
  public void setCompHexDataHdlr(final CompHexWithCDFxDataHandler compHexDataHdlr) {
    this.compHexDataHdlr = compHexDataHdlr;
  }


  /**
   * @return the userNotificationMsg
   */
  public String getUserNotificationMsg() {
    return this.userNotificationMsg;
  }


  /**
   * @param userNotificationMsg the userNotificationMsg to set
   */
  public void setUserNotificationMsg(final String userNotificationMsg) {
    this.userNotificationMsg = userNotificationMsg;
  }
}
