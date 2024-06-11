/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;


/**
 * ICDM-1697
 *
 * @author mkl2cob
 */
public class CdrReportEditorInput implements IEditorInput {

  /**
   * CdrReportData instance
   */
  private CdrReportDataHandler reportData;
  /**
   * User Notification Message to show message about changes in 'WP finished' Status
   */
  private String userNotificationMsg;


  /**
   * {@inheritDoc}
   */
  @Override
  public Object getAdapter(final Class adapter) {
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
    return ImageManager.getImageDescriptor(ImageKeys.CDR_PROJECT_REPORT_16X16);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    String varName = "";
    if (this.reportData.getPidcVariant() != null) {
      // if the report is initiated with variant
      varName = CommonUtils.concatenate("_", this.reportData.getPidcVariant().getName());
    }
    // if the report is initiated without variant
    return CommonUtils.concatenate(this.reportData.getPidcVers().getName(), varName, "_",
        this.reportData.getPidcA2l().getSdomPverName(), "_", this.reportData.getPidcA2l().getSdomPverVarName());

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
    StringBuilder tooltip = new StringBuilder();
    tooltip.append(this.reportData.getPidcVers().getName());
    tooltip.append(" - ");
    if (this.reportData.getPidcVariant() != null) {
      tooltip.append(this.reportData.getPidcVariant().getName());
      tooltip.append(" - ");
    }
    tooltip.append(this.reportData.getPidcA2l().getName());
    return tooltip.toString();
  }


  /**
   * @return the a2lFile
   */
  public PidcA2l getPidcA2l() {
    return this.reportData.getPidcA2l();
  }

  /**
   * @return a2l file
   */
  public A2LFile getA2lFile() {
    return this.reportData.getA2lFile();
  }

  /**
   * @param reportData CdrReportData
   */
  public void setCDRRportData(final CdrReportDataHandler reportData) {
    this.reportData = reportData;

  }

  /**
   * @return CdrReportData
   */
  public CdrReportDataHandler getReportData() {
    return this.reportData;
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
