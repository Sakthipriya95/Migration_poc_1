/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.editor;

import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.NodeAccessPageDataHandler;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.FunctionVersionUnique;
import com.bosch.caltool.icdm.model.a2l.IParamRuleResponse;
import com.bosch.caltool.icdm.model.cdr.ConfigBasedParam;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.utils.RuleEditorConstants;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Input class for ReviewParamEditor
 *
 * @author mkl2cob
 */
public class ReviewParamEditorInput<D extends IParameterAttribute, P extends IParameter> implements IEditorInput {


  /**
   * Main object. could be CDRFunction,RuleSet.. objects
   */
  private final ParamCollection cdrObject;
  /**
   * Constant for Group Name
   */
  private static final String GROUP_NAME = "RULE_EDITOR";
  /**
   * Constant for Name
   */
  private static final String NAME = "ACCESS_RIGHT_HINT";

  /**
   * @return the cdrObject
   */
  public ParamCollection getCdrObject() {
    return this.cdrObject;
  }


  // The version is stored at editor level to synchronise the function version combos in the ListPage and DetailsPage
  /**
   * cdr Func Version
   */
  private String cdrFuncVersion;
  /**
   * Parameter. could be CDRFuncParameter or RuleSetParamater
   */
  private IParameter cdrFuncParam;
  /**
   * current Config based param
   */
  private ConfigBasedParam configParam;
  /**
   * Config param map. Map with abstract param obj as key
   */
  private Map<IParameter, ConfigBasedParam> configParamMap = new ConcurrentHashMap<>();
  /**
   * Selected combo text
   */
  private String selComboTxt;

  private IRulesEditorCustomization customEditorObj;


  private boolean editoropened;
  /**
   * ICDM-2265 A2LEditorDataProvider instance
   */
  private A2LEditorDataProvider a2lFileEditorDP;

  /**
   * A2l file
   */
  private A2LFile a2lFile;


  ParamCollectionDataProvider dataProvider = new ParamCollectionDataProvider();

  private ParameterDataProvider paramDataProvider;
  private final NodeAccessPageDataHandler nodeAccessBO;
  private Map<String, ?> paramMap;
  private SortedSet<FunctionVersionUnique> sortedVersions;

  private IParamRuleResponse<D, P> paramRulesOutput;

  private Long ruleId;

  /**
   * @return the dataProvider
   */
  public ParamCollectionDataProvider getDataProvider() {
    return this.dataProvider;
  }


  /**
   * @return the editoropened
   */
  public boolean isEditoropened() {
    return this.editoropened;
  }


  /**
   * @return the customEditorObj
   */
  public IRulesEditorCustomization getCustomEditorObj() {
    return this.customEditorObj;
  }

  /**
   * Constructor
   *
   * @param cdrObject CDRFunction/RuleSet object instance
   */
  public ReviewParamEditorInput(final ParamCollection cdrObject) {
    this.cdrObject = cdrObject;
    this.nodeAccessBO = new NodeAccessPageDataHandler(this.cdrObject);
    this.nodeAccessBO.setReadColApplicable(true);
    this.nodeAccessBO.setTitle(getDescription());
  }

  /**
   * Constructor
   *
   * @param cdrObject CDRFunction/RuleSet object instance
   */
  public ReviewParamEditorInput(final ParamCollection cdrObject, final IRulesEditorCustomization customEditorObj) {
    this.cdrObject = cdrObject;
    this.customEditorObj = customEditorObj;
    this.nodeAccessBO = new NodeAccessPageDataHandler(this.cdrObject);
    this.nodeAccessBO.setReadColApplicable(true);
    this.nodeAccessBO.setTitle(getDescription());

  }

  /**
   * @return the configParamMap
   */
  public Map<IParameter, ConfigBasedParam> getConfigParamMap() {
    return this.configParamMap;
  }

  /**
   * @return the configParam
   */
  public ConfigBasedParam getConfigParam() {
    return this.configParam;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Object getAdapter(final Class adapter) {
    // Not applicable
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean exists() {
    // Not applicable
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ImageDescriptor getImageDescriptor() {
    // Not applicable
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public IPersistableElement getPersistable() {
    // Not applicable
    return null;
  }

  /**
   * {@inheritDoc} returns the name of the function
   */
  @Override
  public String getToolTipText() {

    return this.dataProvider.getToolTip(this.cdrObject);
  }

  /**
   * @return AbstractParameterCollection
   */
  public ParamCollection getSelectedObject() {
    return this.cdrObject;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return this.cdrObject.getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return this.cdrObject.getId().intValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ReviewParamEditorInput other = (ReviewParamEditorInput) obj;

    if (other.cdrObject == null) {
      return false;
    }

    return this.cdrObject.getId().longValue() == other.cdrObject.getId().longValue();
  }


  /**
   * @return selected function version
   */
  public String getCdrFuncVersion() {
    return this.cdrFuncVersion;
  }


  /**
   * set the function version selected in the UI
   *
   * @param cdrFuncVersion selected function version
   */
  public void setCdrFuncVersion(final String cdrFuncVersion) {
    this.cdrFuncVersion = cdrFuncVersion;
  }


  /**
   * @return AbstractParameter
   */
  public IParameter getCdrFuncParam() {
    return this.cdrFuncParam;
  }


  /**
   * @param cdrFuncParam selected parameter
   */
  public void setCdrFuncParam(final IParameter cdrFuncParam) {
    this.cdrFuncParam = cdrFuncParam;
  }

  /**
   * @param selectedParam selected configParam
   */
  public void setConfigParam(final ConfigBasedParam selectedParam) {
    this.configParam = selectedParam;

  }

  /**
   * @param configParamMap configParamMap
   */
  public void setConfigParamMap(final Map<IParameter, ConfigBasedParam> configParamMap) {
    this.configParamMap = configParamMap;

  }

  // Icdm-1089
  /**
   * @param selComboTxt - Combo Selection stored as Text
   */
  public void setSelectedCombo(final String selComboTxt) {
    this.selComboTxt = selComboTxt;
  }

  /**
   * @return the selComboTxt
   */
  public String getSelComboTxt() {
    return this.selComboTxt;
  }

  /**
   * Get the editor ID
   *
   * @return editorID
   */
  public String getEditorID() {
    String editorId = null;
    if (getSelectedObject() instanceof RuleSet) {
      editorId = "com.bosch.caltool.cdr.ui.editors.RuleSetEditor";
    }
    else if (getSelectedObject() instanceof Function) {
      editorId = "com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor";
    }

    else if (getSelectedObject() instanceof CompPackage) {
      editorId = "com.bosch.caltool.cdr.ui.editors.CPEditor";
    }
    return editorId;
  }


  /**
   * @param editoropened editoropened
   */
  public void setEditorAlreadyOpened(final boolean editoropened) {
    this.editoropened = editoropened;
  }


  /**
   * @return A2LFile
   */
  public A2LFile getA2lFile() {
    return this.a2lFile;
  }


  /**
   * @param a2lFileEditorDP A2LEditorDataProvider
   */
  public void setA2LDataProvider(final A2LEditorDataProvider a2lFileEditorDP) {
    this.a2lFileEditorDP = a2lFileEditorDP;
  }

  /**
   * @return A2LEditorDataProvider
   */
  public A2LEditorDataProvider getA2lDataProvider() {
    return this.a2lFileEditorDP;
  }


  /**
   * @param selectedElement A2LFile
   */
  public void setA2lFile(final A2LFile selectedElement, final long pidcA2lId) {
    this.a2lFile = selectedElement;
    // ICDM-2273 store the a2l file id in preference store
    if (null == selectedElement) {
      // set the a2l file as null in preference store
      PlatformUI.getPreferenceStore().setToDefault(RuleEditorConstants.RULE_OUTLINE_A2L + this.cdrObject.getId());
    }
    else {
      PlatformUI.getPreferenceStore().setValue(RuleEditorConstants.RULE_OUTLINE_A2L + this.cdrObject.getId(),
          Long.toString(pidcA2lId) + RuleEditorConstants.DELIMITER_MULTIPLE_ID + this.a2lFile.getId().toString() +
              RuleEditorConstants.DELIMITER_MULTIPLE_ID + this.a2lFile.getFilename());
    }
  }


  /**
   * @param paramDataProvider paramDataProvider
   */
  public void setParamDataProvider(final ParameterDataProvider paramDataProvider) {
    this.paramDataProvider = paramDataProvider;
  }


  /**
   * @return the paramDataProvider
   */
  public ParameterDataProvider getParamDataProvider() {
    return this.paramDataProvider;
  }

  /**
   * @return nodeAccessBO
   */
  public NodeAccessPageDataHandler getNodeAccessBO() {
    return this.nodeAccessBO;
  }

  /**
   * Get object Description
   *
   * @return description
   */
  public String getDescription() {
    try {
      return new CommonDataBO().getMessage(GROUP_NAME, NAME,
          this.dataProvider.getObjectTypeName(getSelectedObject()) + "s");
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return null;
  }


  /**
   * @param paramMap paramMap
   */
  public void setParamMap(final Map<String, ?> paramMap) {
    this.paramMap = paramMap;

  }


  /**
   * @return the paramMap
   */
  public Map<String, ?> getParamMap() {
    return this.paramMap;
  }


  /**
   * @param sortedVersions sortedVersions
   */
  public void setSortedVersions(final SortedSet<FunctionVersionUnique> sortedVersions) {
    this.sortedVersions = sortedVersions;
  }


  /**
   * @return the sortedVersions
   */
  public SortedSet<FunctionVersionUnique> getSortedVersions() {
    return this.sortedVersions;
  }


  /**
   * @param paramRulesOutput
   */
  public void setParamRulesOutput(final IParamRuleResponse<D, P> paramRulesOutput) {
    this.paramRulesOutput = paramRulesOutput;

  }


  /**
   * @return the paramRulesOutput
   */
  public IParamRuleResponse<D, P> getParamRulesOutput() {
    return this.paramRulesOutput;
  }


  /**
   * @return the ruleId
   */
  public Long getRuleId() {
    return this.ruleId;
  }


  /**
   * @param ruleId the ruleId to set
   */
  public void setRuleId(final Long ruleId) {
    this.ruleId = ruleId;
  }


}
