/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.actions;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Status line contribution for the current user information.
 *
 * @author bne4cob
 */
public class CurrentUserStatusLineContribution extends ContributionItem {

  /**
   * Contribution's ID
   */
  public static final String OBJ_ID = "com.bosch.caltool.icdm.product.actions.CurrentUserStatusLineContribution";

  /**
   * Tooltip start length in string builder
   */
  private static final int SB_TOOLTIP_START_LEN = 60;

  /**
   * Width
   */
  private static final int LBL_WIDTH_CHRS = 40;

  /**
   * Time at which the application was started
   */
  private static final String APP_START_TIME = ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_07);


  /**
   * Constructor
   */
  public CurrentUserStatusLineContribution() {
    super(OBJ_ID);
  }

  /**
   * Adds the user details to the status line bar.
   * <p>
   * Details displayed are <br>
   * a) User Full Name <br>
   * b) NT ID <br>
   * c) Department<br>
   * d)Logged in time
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void fill(final Composite parent) {
    CLabel label = new CLabel(parent, SWT.SHADOW_NONE);

    GC graphics = new GC(parent);
    graphics.setFont(parent.getFont());
    FontMetrics fontMtric = graphics.getFontMetrics();

    CurrentUserBO currentUser = new CurrentUserBO();

    StatusLineLayoutData statusLineLayoutData = new StatusLineLayoutData();

    statusLineLayoutData.widthHint = fontMtric.getAverageCharWidth() * LBL_WIDTH_CHRS;
    statusLineLayoutData.heightHint = fontMtric.getHeight();
    label.setLayoutData(statusLineLayoutData);

    try {
      label.setText("Log in : " + currentUser.getFullName());
      StringBuilder tooltip = new StringBuilder(SB_TOOLTIP_START_LEN);
      tooltip.append("Name\t: ").append(currentUser.getFullName()).append("\nUser ID\t: ")
          .append(currentUser.getUserName()).append("\nDepartment : ").append(currentUser.getDepartment())
          .append("\nLogin time   : ").append(APP_START_TIME);
      label.setToolTipText(tooltip.toString());

      label.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.LOG_IN_16X16));

      graphics.dispose();

    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, com.bosch.caltool.icdm.product.Activator.PLUGIN_ID);
    }


  }

}
