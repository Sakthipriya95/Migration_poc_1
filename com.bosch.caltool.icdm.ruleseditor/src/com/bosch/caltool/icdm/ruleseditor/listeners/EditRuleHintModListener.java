/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.listeners;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

import com.bosch.caltool.icdm.ruleseditor.pages.RuleInfoSection;


/**
 * @author rgo7cob Listener for Enabling/Disabling the Save Button when the Hint area contents is changed
 */
public class EditRuleHintModListener implements ModifyListener {

  /**
   * Details page instance.
   */
  private final RuleInfoSection ruleInfoSection;

  /**
   * @param detailsPage detailsPage
   */
  public EditRuleHintModListener(final RuleInfoSection detailsPage) {
    this.ruleInfoSection = detailsPage;
  }

  /**
   * method invoked when the Hint area text is modified.
   */
  @Override
  public void modifyText(final ModifyEvent event) {
    this.ruleInfoSection.enableSave();
  }

}
