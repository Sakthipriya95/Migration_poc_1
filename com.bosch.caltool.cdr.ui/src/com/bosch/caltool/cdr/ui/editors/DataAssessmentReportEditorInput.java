/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.bosch.caltool.icdm.client.bo.cdr.DataAssmntReportDataHandler;

/**
 * @author ajk2cob
 */
public class DataAssessmentReportEditorInput implements IEditorInput {

  /**
   * PIDC name
   */
  private String pidcName;
  /**
   * A2LFile
   */
  private String a2lFileName;
  /**
   * Selected PIDC variant name
   */
  private String selctedPidcVariantName;
  /**
   * Is editor is opened with baseline data or generated from the A2L
   */
  private boolean isBaseline;
  /**
   * Data Assessment Baseline Id
   */
  private Long baselineId;
  /**
   * Editor name
   */
  private String editorName;
  /**
   * User Notification Message to show message about changes in 'WP finished' Status
   */
  private String userNotificationMsg;
  /**
   * Data Assessment Report Data Handler
   */
  private DataAssmntReportDataHandler dataAssmntReportDataHandler;

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
    return getConcatName();
  }

  /**
   * @return compute editor name
   */
  private String getConcatName() {
    if (this.editorName == null) {
      this.editorName = (isBaseline() ? "Baseline " : "") + "Data Assessment Report (PIDC: " + this.pidcName +
          ", \nA2L: " + getA2lFileName() + ", \nVariant: " + this.selctedPidcVariantName + ")";
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
    return getConcatName();
  }

  /**
   * @return the selctedPidcVariantName
   */
  public String getSelctedPidcVariantName() {
    return this.selctedPidcVariantName;
  }


  /**
   * @param selctedPidcVariantName the selctedPidcVariantName to set
   */
  public void setSelctedPidcVariantName(final String selctedPidcVariantName) {
    this.selctedPidcVariantName = selctedPidcVariantName;
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

  /**
   * @return A2l File name
   */
  public String getA2lFileName() {
    return this.a2lFileName;
  }

  /**
   * @param a2lFileName ,A2l File name
   */
  public void setA2lFileName(final String a2lFileName) {
    this.a2lFileName = a2lFileName;
  }

  /**
   * @return the pidcName
   */
  public String getPidcName() {
    return this.pidcName;
  }


  /**
   * @param pidcName the pidcName to set
   */
  public void setPidcName(final String pidcName) {
    this.pidcName = pidcName;
  }

  /**
   * @return the isBaseline
   */
  public boolean isBaseline() {
    return this.isBaseline;
  }

  /**
   * @param isBaseline the isBaseline to set
   */
  public void setBaseline(final boolean isBaseline) {
    this.isBaseline = isBaseline;
  }

  /**
   * @return the baselineId
   */
  public Long getBaselineId() {
    return this.baselineId;
  }

  /**
   * @param baselineId the baselineId to set
   */
  public void setBaselineId(final Long baselineId) {
    this.baselineId = baselineId;
  }

  /**
   * @return the dataAssmntReportDataHandler
   */
  public DataAssmntReportDataHandler getDataAssmntReportDataHandler() {
    return this.dataAssmntReportDataHandler;
  }

  /**
   * @param dataAssmntReportDataHandler the dataAssmntReportDataHandler to set
   */
  public void setDataAssmntReportDataHandler(final DataAssmntReportDataHandler dataAssmntReportDataHandler) {
    this.dataAssmntReportDataHandler = dataAssmntReportDataHandler;
  }

}
