/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.common.ui.dialogs.PIDCVaraintSelDialog;
import com.bosch.caltool.icdm.common.util.Activator;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;
import com.bosch.caltool.icdm.model.ssdfeature.Feature;
import com.bosch.caltool.icdm.model.ssdfeature.FeatureValue;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;
import com.bosch.caltool.icdm.ruleseditor.pages.ConfigBasedRulesPage;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionAttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;


/**
 * ICDM-1290 This dialog alows the user to select a PIDC/Varaint for getting the attribute Values.
 *
 * @author rgo7cob
 */
public class AddPidcAttrValDialog extends PIDCVaraintSelDialog {

  /**
   * Review Comment Dialog
   */
  private static final String TITLE = "Add Values from PIDC";


  /**
   * Result Parameter for which comment is to be updated - null in case of multiple params
   */
  private final ConfigBasedRulesPage rulesPage;


  private PidcDataHandler handler;


  /**
   * Default Selection in Combo
   */
  private static final String DEFAULT_SEL_STR = "<Select>";

  /**
   * @param parentShell parent shell
   * @param configBasedRulesPage Result Parameter for which comment is to be updated - null in case of multiple params
   */
  public AddPidcAttrValDialog(final Shell parentShell, final ConfigBasedRulesPage configBasedRulesPage) {
    super(parentShell);
    this.rulesPage = configBasedRulesPage;

  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle(TITLE);

    // Set the message
    setMessage("Select PIDC/Variant to get the Attribute Values", IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Select PIDC/Variant for attribute Values");
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    newShell.setLayout(new GridLayout());
    newShell.setLayoutData(gridData);
    super.configureShell(newShell);
  }


  /**
   * add double Click Listener for the tree Viewer.
   */
  @Override
  protected void addDoubleClickListener() {
    this.viewer.addDoubleClickListener(new IDoubleClickListener() {

      @Override
      public void doubleClick(final DoubleClickEvent event) {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        Object selected = selection.getFirstElement();
        // Set the selected PIDC and Variant
        setPIDCAndVariant(selected);
        // call Ok pressed
        okPressed();
      }

    });

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {

    // Reset the items to <Select>
    resetAllGridItems();
    // Set the Values from PIDC.
    if (setValuesFromPidc()) {
      // Any error disable the Search button of Config based page.
      this.rulesPage.getSearchButton().setEnabled(false);
      return;
    }
    // Set the Values from Variant.
    List<IProjectAttribute> invalidPidcAttr = setValuesFromVariant();
    if (!invalidPidcAttr.isEmpty()) {
      // Construct the error for Attributes in Sub Variant level.
      String error = "The following attributes Values cannot be filled beacuse they are defined in Sub-Variant Level: ";
      for (IProjectAttribute ipidcAttribute : invalidPidcAttr) {
        error = CommonUtils.concatenate(error, ipidcAttribute.getName(), '\n');

      }
      // Diable the Search button.
      this.rulesPage.getSearchButton().setEnabled(false);
      CDMLogger.getInstance().errorDialog(error.substring(0, error.length() - 1), Activator.PLUGIN_ID);
    }

    // Check if the Search button in the Config based rule page can be enabled
    if (checkToEnableSearch()) {
      // Any error disable the Search button of Config based page.
      this.rulesPage.getSearchButton().setEnabled(false);
      return;
    }
    super.okPressed();
  }

  /**
   * reset the attr values before start of setting Values from PIDC
   */
  private void resetAllGridItems() {
    // get all grid items and put all value in the first column as <Select>
    GridItem[] items = this.rulesPage.getAttrsTableViewer().getGrid().getItems();
    for (GridItem gridItem : items) {
      gridItem.setText(1, DEFAULT_SEL_STR);
    }
    this.rulesPage.getRulesTableViewer().setInput("");
  }


  /**
   * Check all the Grid Items if any one does not have any Value then give warning.
   */
  private boolean checkToEnableSearch() {

    String warnMessage =
        "The following Attribute's Values are not filled since there is no mapping between the Features Values and values from PIDC. \n";
    boolean warningReq = false;
    GridItem[] items = this.rulesPage.getAttrsTableViewer().getGrid().getItems();
    for (GridItem gridItem : items) {
      if (gridItem.getText(1).equals(DEFAULT_SEL_STR) || gridItem.getText(1).isEmpty()) {
        this.rulesPage.getSearchButton().setEnabled(false);
        warnMessage = CommonUtils.concatenate(warnMessage, gridItem.getText(0), '\n');
        warningReq = true;
      }
      this.rulesPage.getSearchButton().setEnabled(true);
    }

    if (warningReq) {
      // Set the Warning meesage that all the Attributes are not filled.
      CDMLogger.getInstance().warnDialog(warnMessage.substring(0, warnMessage.length() - 1), Activator.PLUGIN_ID);
      return true;
    }
    return false;
  }


  private PidcVersionBO getPidcVersionBO() {
    return getPidcVersionBO(this.selPidcVer);
  }


  private PidcVersionBO getPidcVersionBO(final PidcVersion version) {

    PidcVersionBO versionHandler = new PidcVersionBO(version, this.handler);
    return versionHandler;
  }

  /**
   * set Values from PIDC.
   */
  private boolean setValuesFromPidc() {

    if (this.selPidcVer != null) {

      PidcVersionAttributeServiceClient versionAttrClient = new PidcVersionAttributeServiceClient();
      PidcVersionWithDetails pidcVersionAttrModel = null;
      Map<Long, PidcVariant> variantsMap = null;
      Map<Long, PidcVersionAttribute> allAttrMap = null;
      try {
        pidcVersionAttrModel = versionAttrClient.getPidcVersionAttrModel(this.selPidcVer.getId());
        variantsMap = pidcVersionAttrModel.getPidcVariantMap();
        allAttrMap = pidcVersionAttrModel.getPidcVersionAttributeMap();
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        return true;
      }

      // If pidc has Variants and the PIDC is selected then throw error message.
      if (!variantsMap.isEmpty() && CommonUtils.isNull(this.selVar)) {
        CDMLogger.getInstance().errorDialog("Variant is Defined for the PIDC. Please select Variant to get the Values",
            Activator.PLUGIN_ID);
        return true;
      }

      if (CommonUtils.isNull(this.selVar)) {
        GridItem[] items = this.rulesPage.getAttrsTableViewer().getGrid().getItems();
        for (IProjectAttribute ipidcAttribute : allAttrMap.values()) {
          setGridItemValues(items, ipidcAttribute);
        }
      }

    }
    return false;
  }


  /**
   * @param items Grid Items
   * @param ipidcAttribute ipidcAttribute Iterate through the Grid Items and set the Values
   */
  private void setGridItemValues(final GridItem[] items, final IProjectAttribute ipidcAttribute) {
    // Set the Combo box items from the PIDC/Varaint.
    for (GridItem gridItem : items) {
      if (gridItem.getText(0).equals(ipidcAttribute.getName())) {
        if ((ipidcAttribute.getValueId() != null) &&
            isValueDefined(ipidcAttribute.getAttrId(), ipidcAttribute.getValueId())) {
          gridItem.setText(1, ipidcAttribute.getValue());
        }
        else if (ipidcAttribute.getUsedFlag().equals(ApicConstants.CODE_NO)) {
          gridItem.setText(1, ApicConstants.NOT_USED);
        }
        else if (ipidcAttribute.getUsedFlag().equals(ApicConstants.CODE_YES)) {
          gridItem.setText(1, ApicConstants.USED);
        }

        else {
          gridItem.setText(1, DEFAULT_SEL_STR);
        }
      }
    }
  }

  /**
   * @param attribute
   * @param attributeValue
   * @return
   */
  private boolean isValueDefined(final Long attrId, final Long valueId) {

    try {
      ReviewParamEditorInput reviewParamInput = this.rulesPage.getEditor().getEditorInput();
      // Get the feature from the ApicDataProvider
      Map<Long, Feature> featuresWithAttrKey = reviewParamInput.getParamDataProvider().getAllFeatures();
      Feature feature = featuresWithAttrKey.get(attrId);
      if (feature != null) {
        // Get all the feature Values.
        Collection<FeatureValue> values = reviewParamInput.getParamDataProvider().getFeatureValues(feature.getId());
        for (FeatureValue featureValue : values) {
          // If the feature Value id and the Attr value id are same retuen true.
          if ((featureValue.getAttrValId() != null) && featureValue.getAttrValId().equals(valueId)) {
            return true;
          }
        }
      }
    }

    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return false;
  }


  /**
   * Set the Values from Variant.
   *
   * @return List of Invalid Pidc attr(Which are defined at the Sub-variant level)
   */
  private List<IProjectAttribute> setValuesFromVariant() {
    List<IProjectAttribute> invalidPidcAttr = new ArrayList<>();
    try {
      if (CommonUtils.isNotNull(this.selVar)) {

        PidcVersionAttributeServiceClient versionAttrClient = new PidcVersionAttributeServiceClient();
        PidcVersionWithDetails pidcVersionAttrModel =
            versionAttrClient.getPidcVersionAttrModel(this.selVar.getPidcVersionId());
        Map<Long, PidcVersionAttribute> allVersionAttrMap = pidcVersionAttrModel.getPidcVersionAttributeMap();
        Map<Long, PidcVariantAttribute> allVariantAttrMap =
            pidcVersionAttrModel.getPidcVariantAttributeMap().get(this.selVar.getId());

        GridItem[] items = this.rulesPage.getAttrsTableViewer().getGrid().getItems();
        // get the matching attributes in the PIDC/Varaint and the Param attr table Viewer.
        Set<String> matchingAttrs = getMatchingAttrs(items, allVariantAttrMap.values());

        for (IProjectAttribute ipidcAttribute : allVersionAttrMap.values()) {
          setGridItemValues(items, ipidcAttribute);
        }
        for (IProjectAttribute ipidcAttribute : allVariantAttrMap.values()) {
          if (ipidcAttribute.isAtChildLevel() && matchingAttrs.contains(ipidcAttribute.getName())) {
            // Add all the Values which are Sub Varaints in a list to show the error.
            invalidPidcAttr.add(ipidcAttribute);
            continue;
          }
          setGridItemValues(items, ipidcAttribute);
        }
        // Icdm-1290 get also the Values from PIDC.
      }
    }

    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return invalidPidcAttr;
  }

  /**
   * @param items
   * @param varAttrs
   * @return
   */
  private Set<String> getMatchingAttrs(final GridItem[] items, final Collection<PidcVariantAttribute> varAttrs) {
    Set<String> matchingAttrs = new HashSet<>();
    for (PidcVariantAttribute variantAttr : varAttrs) {
      for (GridItem gridIem : items) {
        // For all the grid items get the matching Attributes.
        if (gridIem.getText().equals(variantAttr.getName())) {
          matchingAttrs.add(variantAttr.getName());
        }
      }
    }
    return matchingAttrs;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.DIALOG_TRIM);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void setSelectionForTreeViewer() {
    // No implementation needed here

  }


}
