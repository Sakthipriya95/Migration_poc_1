/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.UserPreference;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;

/**
 * @author ekir1kor
 */
public class ReviewResultOpenDecimalPreferencesWindowAction extends Action {

  /**
   * constructor
   */
  public ReviewResultOpenDecimalPreferencesWindowAction() {
    setProperties();
  }

  private void setProperties() {
    setTooltipForOpenDecimalPreferencesWindowAction();
    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DECIMAL_16X16));
  }

  /**
   * set tooltip for the decimal pref icon
   */
  public void setTooltipForOpenDecimalPreferencesWindowAction() {
    setText("Set Decimal Preference\nCurrent Decimal Pref: " + getCurrentDecimalPref());
  }

  /**
   * @return decimal preference
   */
  private String getCurrentDecimalPref() {
    try {
      String decimalPref = CDRConstants.DECIMAL_PREF_NO_LIMIT;
      CurrentUserBO currentUser = new CurrentUserBO();
      UserPreference userPref = new UserPreference();
      if (CommonUtils.isNotNull(currentUser)) {
        userPref = currentUser.getUserPreference(CDRConstants.DECIMAL_PREF_LIMIT_KEY);
      }
      if (CommonUtils.isNotNull(userPref)) {
        decimalPref = userPref.getUserPrefVal();
      }
      return CommonUtils.isNotNull(decimalPref) ? decimalPref : CDRConstants.DECIMAL_PREF_NO_LIMIT;
    }
    catch (Exception e) {
      CDMLogger.getInstance().error("Error in fetching preference limit for decimal: " + e);
      return CDRConstants.DECIMAL_PREF_NO_LIMIT;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    PreferenceDialog preferenceDialog =
        new PreferenceDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            PlatformUI.getWorkbench().getPreferenceManager());
    preferenceDialog.setSelectedNode("com.bosch.caltool.icdm.product.preferences.RvwResultDecimalPreferencePage");
    preferenceDialog.open();
  }
}