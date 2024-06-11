/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.listeners;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.bosch.caltool.icdm.ruleseditor.pages.RuleInfoSection;


/**
 * @author rgo7cob
 */

public class EditRuleRefMatchSelListener extends SelectionAdapter {


  /**
   * @param detailsPage detailsPage
   */
  public EditRuleRefMatchSelListener(final RuleInfoSection detailsPage) {
    super();
    this.ruleInfoSection = detailsPage;


  }

  /**
   * detaile page instance.
   */
  private final RuleInfoSection ruleInfoSection;

  /**
   * Selection listener call back mathod.
   */
  @Override
  public void widgetSelected(final SelectionEvent event) {
    // Call the lower and the upper limit values in the details page for selcetion in Ref Value match button.
    this.ruleInfoSection.setLimitsToNonEditable();

    this.ruleInfoSection.enableSave();

  }


}
