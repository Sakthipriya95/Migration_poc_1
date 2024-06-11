/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.actions;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;
import com.bosch.caltool.icdm.ruleseditor.pages.RuleInfoSection;
import com.bosch.caltool.icdm.ruleseditor.wizards.AddNewConfigWizard;
import com.bosch.caltool.icdm.ruleseditor.wizards.AddNewConfigWizardDialog;


/**
 * @author rgo7cob
 */
public class EditRuleWithDepAction<D extends IParameterAttribute, P extends IParameter> extends AbstractRuleEditAction {


  private final ParameterDataProvider<D, P> parameterDataProvider;
  private final ParamCollectionDataProvider paramColDataProvider;
  private final List<ReviewRule> selectedRules;
  private final IParameter selectedParam;
  private final ParamCollection cdrFunction;
  private final AbstractFormPage page;
  private final boolean readOnlyMode;

  private static final int EDIT_RULE_DIA_WIDTH = 1000;
  private static final int EDIT_RULE_DIA_HEIGHT = 900;


  /**
   * @param parameterDataProvider parameterDataProvider
   * @param paramColDataProvider paramColDataProvider
   */
  public EditRuleWithDepAction(final ParameterDataProvider<D, P> parameterDataProvider,
      final ParamCollectionDataProvider paramColDataProvider, final List<ReviewRule> selectedRules,
      final IParameter selectedParam, final ParamCollection cdrFunction, final AbstractFormPage page,
      final boolean readOnlyMode) {
    this.parameterDataProvider = parameterDataProvider;
    this.paramColDataProvider = paramColDataProvider;
    this.selectedRules = selectedRules;
    this.selectedParam = selectedParam;
    this.cdrFunction = cdrFunction;
    this.page = page;
    this.readOnlyMode = readOnlyMode;

    setEnabled(true);
    // ICDM-1190
    if (readOnlyMode) {
      setText("Show rule details");
    }
    else {
      setText("Show/Edit rule details");
    }
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16);
    setImageDescriptor(imageDesc);
  }


  @Override
  public void run() {
    openEditRuleDialog(this.selectedRules, this.selectedParam, this.cdrFunction, this.page, this.readOnlyMode,
        this.paramColDataProvider);
  }


  /**
   * {@inheritDoc}
   *
   * @param parameterDataProvider2
   * @param paramColDataProvider
   */
  @SuppressWarnings("javadoc")
  private void openEditRuleDialog(final List<ReviewRule> selectedRules, final IParameter selectedParam,
      final ParamCollection cdrFunction, final AbstractFormPage page, final boolean readOnlyMode,
      final ParamCollectionDataProvider paramColDataProvider) {
    StringBuilder attrDefIncomplete = new StringBuilder("false");
    Set<IParameterAttribute> depnAttrSet = new HashSet<>();
    Set<AttributeValueModel> attrValSet = new HashSet<>();
    boolean editPossible = true;
    boolean dialogAlreadyOpen;
    AddNewConfigWizardDialog dialog;
    int sizeOfSelectedRules = selectedRules.size();
    if (sizeOfSelectedRules > 1) {
      // check if multiple edit is possible
      editPossible = multipleEdit(selectedRules, attrDefIncomplete, selectedParam, depnAttrSet, attrValSet);
    }
    else {
      singleEdit(selectedRules, attrDefIncomplete, selectedParam, depnAttrSet, attrValSet);
    }
    if (editPossible) {
      // ICDM-1244
      if (page.getEditor() instanceof ReviewParamEditor) {

        ReviewParamEditor editor = (ReviewParamEditor) page.getEditor();
        Set<RuleInfoSection> ruleInfoSet = editor.getRuleInfoSectionMap().get(selectedParam);

        dialogAlreadyOpen = checkWizardDialogAlreadyOpen(ruleInfoSet, selectedRules);

        if (!dialogAlreadyOpen) {
          // need not create wizard in case of error
          AddNewConfigWizard wizard = new AddNewConfigWizard(selectedParam, cdrFunction, page, true, selectedRules,
              false, Boolean.valueOf(attrDefIncomplete.toString()), depnAttrSet, attrValSet, readOnlyMode, false,
              editor.getEditorInput().getParamDataProvider(), paramColDataProvider);
          dialog =
              new AddNewConfigWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);

          dialog.create();
          dialog.open();

          addToWizardRuleInfoSectionMap(selectedParam, dialog, editor, ruleInfoSet);


        }
      }
    }
  }

  /**
   * @param selectedRules
   * @param attrDefIncomplete
   * @param selectedParam
   * @param depnAttrSet
   * @param attrValSet
   */
  private void singleEdit(final List<ReviewRule> selectedRules, final StringBuilder attrDefIncomplete,
      final IParameter selectedParam, final Set<IParameterAttribute> depnAttrSet,
      final Set<AttributeValueModel> attrValSet) {

    Set<AttributeValueModel> dependencyList = new HashSet<AttributeValueModel>();
    dependencyList.addAll(selectedRules.get(0).getDependencyList());

    // ICDM-1199
    if ((this.parameterDataProvider.getParamAttrs(selectedParam) != null) &&
        (dependencyList.size() < this.parameterDataProvider.getParamAttrs(selectedParam).size())) {
      attrDefIncomplete.setLength(0);
      attrDefIncomplete.append("true");
      Set<Attribute> definedAttrSet = new HashSet<Attribute>();
      for (AttributeValueModel attrVal : dependencyList) {
        // getting the set of attributes defined
        definedAttrSet.add(attrVal.getAttr());
      }
      // get all the dependecy attributes
      for (IParameterAttribute paramAttr : this.parameterDataProvider.getParamAttrs(selectedParam)) {
        depnAttrSet.add(paramAttr);
      }
      // remove the defined attributes
      depnAttrSet.removeAll(definedAttrSet);
      // add the attr val mapping to the wizard data
      attrValSet.addAll(dependencyList);
    }


  }

  /**
   * ICDM-1199 check if all the selected rules have same number of attributes to be defined
   *
   * @param selectedRules list of selected rules
   * @param definedAttrSet set of defined attributes
   * @param attrValueModel collection of attr value model
   */
  private boolean checkMultipleUpdatePossible(final List<ReviewRule> selectedRules, final Set<Attribute> definedAttrSet,
      final Collection<AttributeValueModel> attrValueModel) {
    Map<Attribute, AttributeValue> attrValueMap = new HashMap<Attribute, AttributeValue>();
    for (AttributeValueModel attrVal : attrValueModel) {
      // getting the set of attributes defined
      definedAttrSet.add(attrVal.getAttr());
      attrValueMap.put(attrVal.getAttr(), attrVal.getValue());
    }
    for (ReviewRule rule : selectedRules) {
      if (selectedRules.indexOf(rule) == 0) {
        continue;
      }
      if (!CommonUtils.isEqual(rule.getDependencyList().size(), definedAttrSet.size())) {
        CDMLogger.getInstance().errorDialog(
            "There is a different number of attribute values which have additionally to be defined for the selected rules!",
            Activator.PLUGIN_ID);
        return false;
      }
    }
    return true;
  }


  /**
   * @param selectedRules
   * @param attrDefIncomplete
   * @param selectedParam
   */
  private boolean multipleEdit(final List<ReviewRule> selectedRules, final StringBuilder attrDefIncomplete,
      final IParameter selectedParam, final Set<IParameterAttribute> depnAttrSet,
      final Set<AttributeValueModel> attrValSet) {
    Set<AttributeValueModel> dependencyList = new TreeSet<>();
    dependencyList.addAll(selectedRules.get(0).getDependencyList());

    // ICDM-1199

    Set<Attribute> definedAttrSet = new HashSet<Attribute>();
    boolean editPossible = checkMultipleUpdatePossible(selectedRules, definedAttrSet, dependencyList);
    if (editPossible) {
      if (dependencyList.size() < this.parameterDataProvider.getParamAttrs(selectedParam).size()) {
        attrDefIncomplete.setLength(0);
        attrDefIncomplete.append("true");
      }
      // get all the dependecy attributes
      for (IParameterAttribute paramAttr : this.parameterDataProvider.getParamAttrs(selectedParam)) {

        depnAttrSet.add(paramAttr);
      }
      // remove the defined attributes
      depnAttrSet.removeAll(definedAttrSet);
      // add the attr val mapping to the wizard data
      attrValSet.addAll(dependencyList);
      return true;
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  public boolean checkWizardDialogAlreadyOpen(final Set<RuleInfoSection> ruleInfoSet,
      final List<ReviewRule> selectedRules) {
    boolean dialogAlreadyOpen = false;
    AddNewConfigWizardDialog dialog = null;
    if ((ruleInfoSet != null) && !ruleInfoSet.isEmpty()) {
      // iterate through the set to get the ruleinfosection with the same selected rules
      for (RuleInfoSection ruleInfoSect : ruleInfoSet) {
        if (ruleInfoSect.getWizard() != null) {
          List<ReviewRule> cdrRulesList = ruleInfoSect.getWizard().getWizardData().getCdrRulesList();
          dialogAlreadyOpen = (selectedRules != null) && (cdrRulesList != null) &&
              selectedRules.containsAll(cdrRulesList) && (selectedRules.size() == cdrRulesList.size());
          if (dialogAlreadyOpen) {
            dialog = ruleInfoSect.getWizard().getWizardDialog();
            break;
          }
        }
      }
    }

    maximiseExistingDialog(dialogAlreadyOpen, dialog);
    return dialogAlreadyOpen;
  }

  /**
   * @param dialogAlreadyOpen boolean
   * @param dialog AddNewConfigWizardDialog
   */
  private void maximiseExistingDialog(final boolean dialogAlreadyOpen, final AddNewConfigWizardDialog dialog) {
    if (dialogAlreadyOpen) {
      // if the dialog is already open , show the existing dialog
      if ((dialog != null) && dialog.getShell().getMinimized()) {
        dialog.getShell().setMaximized(true);
        dialog.getShell().setSize(EDIT_RULE_DIA_WIDTH, EDIT_RULE_DIA_HEIGHT);
      }
    }
  }

}
