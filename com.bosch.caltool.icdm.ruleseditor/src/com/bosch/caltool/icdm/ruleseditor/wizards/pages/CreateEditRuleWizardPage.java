/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.wizards.pages;

import java.util.Map;
import java.util.SortedSet;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ruleseditor.pages.RuleInfoSection;
import com.bosch.caltool.icdm.ruleseditor.wizards.AddNewConfigWizard;
import com.bosch.caltool.icdm.ruleseditor.wizards.AddNewConfigWizardData;
import com.bosch.rcputils.griddata.GridDataUtil;


/**
 * ICDM-1081 This page is to create a new rule for attr-val combination
 *
 * @author mkl2cob
 */
public class CreateEditRuleWizardPage<D extends IParameterAttribute, P extends IParameter> extends WizardPage {

  /**
   * initial size of string builder
   */
  private static final int STR_BUILDER_INITAIL_SIZE = 40;

  /**
   * PAGE TITLE FOR NEW RULE CREATION
   */
  private static final String PAGE_TITLE_FOR_NEW = "New Rule";

  /**
   * PAGE DESCRITPION FOR NEW RULE CREATION
   */
  private static final String PAGE_DESCRIPTION_FOR_NEW = "Enter the rule information";

  /**
   * PAGE TITLE FOR EDIT RULE DIALOG
   */
  private static final String PAGE_TITLE_FOR_EDIT = "Edit Rule";
  /**
   * PAGE DESCRITPION FOR edit RULE
   */
  private static final String PAGE_DESCRIPTION_FOR_EDIT = "Modify the rule information";


  /**
   * rule info section
   */
  private RuleInfoSection ruleInfoSection;

  /**
   * is Rule update or create
   */
  private final boolean isUpdate;

  private final ParameterDataProvider<D, P> paramDataProvider;

  private final ParamCollectionDataProvider paramCollDataProvider;

  private final AddNewConfigWizard addNewConfigWizard;

  /**
   * @param pageName Name of the page
   * @param isUpdate whether this is update rule
   * @param cdrRule selected cdr rule for update
   * @param addNewConfigWizard AddNewConfigWizard
   */
  public CreateEditRuleWizardPage(final String pageName, final boolean isUpdate, final ReviewRule cdrRule,
      final AddNewConfigWizard addNewConfigWizard, final ParameterDataProvider<D, P> paramDataProvider,
      final ParamCollectionDataProvider paramCollDataProvider) {
    super(pageName);
    this.paramDataProvider = paramDataProvider;
    this.paramCollDataProvider = paramCollDataProvider;
    this.ruleInfoSection = new RuleInfoSection(addNewConfigWizard, this, isUpdate, cdrRule,
        addNewConfigWizard.isReadOnlyMode(), this.paramDataProvider, this.paramCollDataProvider);
    addNewConfigWizard.setRuleInfoSection(this.ruleInfoSection);// ICDM-1244
    this.addNewConfigWizard = addNewConfigWizard;
    this.isUpdate = isUpdate;
    setTitleDescription();
    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CDR_WIZARD_PG1_67X57));
    setPageComplete(false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite composite) {
    initializeDialogUnits(composite);
    final Composite workArea = this.ruleInfoSection.createComposite(composite);
    if (this.isUpdate && !((AddNewConfigWizard) getWizard()).getWizardData().isAttrValMapIncomplete()) {
      this.ruleInfoSection.setRuleDetails(null);
    }
    createGridLayout(workArea);
  }

  /**
   * @param workArea Composite
   */
  private void createGridLayout(final Composite workArea) {
    final GridLayout layout = new GridLayout();
    workArea.setLayout(layout);
    workArea.setLayoutData(GridDataUtil.getInstance().createGridData());
    setControl(workArea);
  }

  /**
   * set title based on the selection from previous page
   */
  public void setTitleDescription() {
    ReviewRule rule = this.ruleInfoSection.getSelectedCdrRule();
    String desc = "";
    // Set title and description for update
    if (this.isUpdate) {
      StringBuilder values = new StringBuilder(STR_BUILDER_INITAIL_SIZE);
      AddNewConfigWizardData<D, P> wizardData = this.addNewConfigWizard.getWizardData();
      if (wizardData.getCdrRulesList() != null) {
        for (ReviewRule cdrRule : wizardData.getCdrRulesList()) {
          values.append(this.paramDataProvider.getAttrValString(cdrRule)).append(",");
        }
        desc = values.substring(0, values.length() - 1);
      }
      setTitle(PAGE_TITLE_FOR_EDIT);
      setDescription(PAGE_DESCRIPTION_FOR_EDIT + " " + desc);
      this.ruleInfoSection.setSelectedCdrRule(rule);
    } // Set title and description for insert new rule
    else {
      StringBuilder values = new StringBuilder(STR_BUILDER_INITAIL_SIZE);
      values.append("\nRule dependency : ");
      if (CommonUtils.isNotNull(this.addNewConfigWizard)) {
        AddNewConfigWizardData<D, P> wizardData = this.addNewConfigWizard.getWizardData();
        Map<D, SortedSet<AttributeValue>> attrVals = wizardData.getAttrVals();
        for (D paramAttr : attrVals.keySet()) {
          appendRuleDependencies(values, paramAttr);
        }
        desc = values.substring(0, values.length() - 1);
      }
      setTitle(PAGE_TITLE_FOR_NEW);
      setDescription(PAGE_DESCRIPTION_FOR_NEW + desc);
    }
  }

  /**
   * @param values StringBuilder
   * @param paramAttr AbstractParameterAttribute
   */
  private void appendRuleDependencies(final StringBuilder values, final IParameterAttribute paramAttr) {
    // append the attr name
    values.append(paramAttr.getName()).append("  --> ");
    AddNewConfigWizardData<D, P> wizardData = ((AddNewConfigWizard) getWizard()).getWizardData();
    Map<D, SortedSet<AttributeValue>> attrVals = wizardData.getAttrVals();
    SortedSet<AttributeValue> selVals = attrVals.get(paramAttr);
    if (selVals != null) {
      for (AttributeValue selVal : selVals) {
        // append selected values
        values.append(selVal.getName()).append(",");
      }
      values.deleteCharAt(values.length() - 1);
    }
    values.append(";");
  }

  /**
   * on back pressed , set the finish button to false
   */
  public void backPressed() {
    setPageComplete(false);
    getWizard().getContainer().updateButtons();
  }


  /**
   * @return the ruleInfoSection
   */
  public RuleInfoSection getRuleInfoSection() {
    return this.ruleInfoSection;
  }


  /**
   * @param ruleInfoSection the ruleInfoSection to set
   */
  public void setRuleInfoSection(final RuleInfoSection ruleInfoSection) {
    this.ruleInfoSection = ruleInfoSection;
  }
}
