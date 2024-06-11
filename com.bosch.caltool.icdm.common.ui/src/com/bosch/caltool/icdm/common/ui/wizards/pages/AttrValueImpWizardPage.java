/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.wizards.pages;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.bosch.calmodel.a2ldata.Activator;
import com.bosch.caltool.icdm.client.bo.caldataimport.CalDataImporterHandler;
import com.bosch.caltool.icdm.client.bo.caldataimport.CombinationCalculator;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.common.bo.a2l.RuleMaturityLevel;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.AttrValImpTableSection;
import com.bosch.caltool.icdm.common.ui.wizards.CalDataFileImportWizard;
import com.bosch.caltool.icdm.common.ui.wizards.CalDataFileImportWizardData;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.IParamRuleResponse;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.READY_FOR_SERIES;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportComparisonModel;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportData;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;


/**
 * Second page of caldata import wizard
 *
 * @author bru2cob
 */
public class AttrValueImpWizardPage<D extends IParameterAttribute, P extends IParameter> extends WizardPage {

  /**
   * Progress complete state
   */
  private static final int PROG_COMPLETE = 100;
  /**
   * Progress stage 2
   */
  private static final int PROG_2 = 40;

  int key = 1;

  /**
   * @return the attrsTableViewer
   */
  public GridTableViewer getAttrsTableViewer() {
    return this.tableSec.getAttrsTableViewer();
  }


  /**
   * instance of wizard
   */
  private CalDataFileImportWizard wizard;

  /**
   * instance of Table
   */
  private AttrValImpTableSection tableSec;


  /**
   * @return the tableSec
   */
  public AttrValImpTableSection getTableSec() {
    return this.tableSec;
  }


  /**
   * Page title
   */

  private static final String PAGE_TITLE = "Attribute Values to be used";
  /**
   * Page description
   */
  private static final String PAGE_DESCRIPTION = "Please set the attribute value(s) you want to use";

  /**
   * @param pageName Name of the page
   */
  public AttrValueImpWizardPage(final String pageName) {
    super(pageName);
    // set page title
    setTitle(PAGE_TITLE);
    // set page description
    setDescription(PAGE_DESCRIPTION);
    // set page image
    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CDR_WIZARD_PG1_67X57));
    // intially disable the next button
    setPageComplete(false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite composite) {

    initializeDialogUnits(composite);
    final Composite workArea = new Composite(composite, SWT.NONE);

    this.wizard = (CalDataFileImportWizard) getWizard();
    // create layout for composite
    createGridLayout(workArea);
    this.tableSec = new AttrValImpTableSection(this);
    // create attr-val table section
    this.tableSec.createSectionAttr(workArea);
  }

  /**
   * @param workArea Composite
   */
  private void createGridLayout(final Composite workArea) {
    final GridLayout layout = new GridLayout();
    // set layout
    workArea.setLayout(layout);
    // set layout data
    workArea.setLayoutData(GridDataUtil.getInstance().createGridData());
    // set control to the composite
    setControl(workArea);
  }


  /**
   * Method to invoke pages when next button is pressed
   */
  public void nextPressed() {
    try {
      getContainer().run(true, true, new IRunnableWithProgress() {

        @Override
        public void run(final IProgressMonitor monitor) {
          monitor.beginTask("Importing Calibration Data", PROG_COMPLETE);
          loadAndImportData(monitor);
          monitor.done();
        }
      });
    }
    catch (InvocationTargetException | InterruptedException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    getShell().setMaximized(true);
  }

  /**
   * @param monitor IProgressMonitor
   */
  public void loadAndImportData(final IProgressMonitor monitor) {

    constructAttrValCombi();
    if (null != this.wizard.getCalImportData()) {
      importData(monitor);
    }

    if (AttrValueImpWizardPage.this.wizard.getWizardData().isExceptionOccured()) {
      // disable the next button if an exception has occured
      setPageComplete(false);
    }
  }


  /**
   * Imports caldata files
   *
   * @param monitor progress monitor
   */
  private void importData(final IProgressMonitor monitor) {
    CalDataFileImportWizardData wizardData = AttrValueImpWizardPage.this.wizard.getWizardData();
    CalDataImporterHandler calDataImportHandler = this.wizard.getCalDataImportHandler();

    try {
      monitor.setTaskName("Checking for existing rules..");
      monitor.worked(PROG_2);
      ParamCollection importObject = this.wizard.getWizardData().getImportObject();
      ParamCollectionDataProvider paramColDataProvider = this.wizard.getWizardData().getParamColDataProvider();

      CalDataImportData calImportData = calDataImportHandler.getCalDataCompareList(this.wizard.getCalImportData(),
          importObject.getId().toString(), paramColDataProvider.getObjectTypeName(importObject));

      // to skip the parameters that are not assigned to function, func version
      Set<String> paramNotInFunc = new HashSet<>();
      for (String paramName : calImportData.getCalDataCompMap().keySet()) {
        if (!calImportData.getParamNameTypeMap().containsKey(paramName)) {
          paramNotInFunc.add(paramName);
        }
      }
      paramNotInFunc.forEach(paramName -> {
        calImportData.getCalDataCompMap().remove(paramName);
      });
      this.wizard.setCalImportData(calImportData);

      Collection<List<CalDataImportComparisonModel>> compModelListValues = calImportData.getCalDataCompMap().values();

      SortedSet<CalDataImportComparisonModel> modifyingData = new TreeSet<>();

      for (List<CalDataImportComparisonModel> calDataImportComparisonModel : compModelListValues) {
        modifyingData.addAll(calDataImportComparisonModel);
      }

      for (CalDataImportComparisonModel compObj : modifyingData) {
        initialiseValues(compObj, wizardData.getDefaultBaseComment(), wizardData.getDefaultMaturityLvl(),
            wizardData.getReadyForSeries(), wizardData.isDefaultExactMatchFlag());
      }
      // set the parameter properties to wizard data
      wizardData.setNewParamPropsMap(calImportData.getParamDetMap());
      AttrValueImpWizardPage.this.wizard.setCalImportData(calImportData);
      // initialise Hint text for parameters
      wizardData.initialiseHintAndClass(calImportData);
      // set input to compare page for creating/ updating rules
      AttrValueImpWizardPage.this.wizard.getCompRuleWizardPage().setTabInput(modifyingData);
    }
    catch (ApicWebServiceException ex) {
      wizardData.setExceptionOccured(true);
      CDMLogger.getInstance().errorDialog(ex.getLocalizedMessage(), ex, Activator.PLUGIN_ID);
    }

  }

  /**
   * @param compObj
   * @param defaultBaseComment
   * @param defaultMaturityLvl
   * @param readyForSeries
   * @param defaultExactMatchFlag
   */
  private void initialiseValues(final CalDataImportComparisonModel compObj, final String defaultBaseComment,
      final String defaultMaturityLvl, final READY_FOR_SERIES readyForSeries, final boolean defaultExactMatchFlag) {
    String oldHint = "";
    ReviewRule oldRule = compObj.getOldRule();

    ReviewRule newRule = compObj.getNewRule();
    if (null != oldRule) {
      if (null != oldRule.getHint()) {
        // set the hint value
        oldHint = oldRule.getHint();
      }
      // set the use text box values based on values from old rule
      if (null != oldRule.getLowerLimit()) {
        compObj.setUseNewLowLmt(false);
      }
      if (null != oldRule.getUpperLimit()) {
        compObj.setUseNewLowLmt(false);
      }
      if (!CommonUtils.isEmptyString(oldRule.getRefValueDispString())) {
        compObj.setUseNewRefVal(false);
      }
      if (null != oldRule.getUnit()) {
        compObj.setUseNewUnit(false);
      }
      if (null != oldRule.getMaturityLevel()) {
        compObj.setUseNewMaturityLvl(false);
      }
      if (null != oldRule.getReviewMethod()) {
        compObj.setUseNewRvwMtd(false);
      }
    }

    // initialise Hint
    initialiseHint(defaultBaseComment, oldHint, newRule, compObj);


    if (newRule != null) {
      // initialise default values
      // maturity level
      if (null == newRule.getMaturityLevel()) {
        newRule.setMaturityLevel(RuleMaturityLevel.getSsdMaturityLevelText(defaultMaturityLvl));
      }
      // review method
      if (CommonUtils.isEqual(READY_FOR_SERIES.NOT_DEFINED.dbType, newRule.getReviewMethod())) {
        newRule.setReviewMethod(readyForSeries.getDbType());
      }
      // exact match
      setExactMatch(defaultExactMatchFlag, oldRule, newRule, compObj);
      // validation for min and max values
      validateMinMaxValues(oldRule, newRule, compObj);
    }


  }

  /**
   *
   */
  private void validateMinMaxValues(final ReviewRule oldRule, final ReviewRule newRule,
      final CalDataImportComparisonModel compObj) {
    BigDecimal lowerLimit = null;
    BigDecimal upperLimit = null;
    // find lower limit
    if (compObj.isUseNewLowLmt()) {
      lowerLimit = newRule.getLowerLimit();
    }
    else if (null != oldRule) {
      lowerLimit = oldRule.getLowerLimit();
    }

    // find upper limit
    if (compObj.isUseNewUpLmt()) {
      upperLimit = newRule.getUpperLimit();
    }
    else if (null != oldRule) {
      upperLimit = oldRule.getUpperLimit();
    }

    if (ApicUtil.compareBigDecimal(lowerLimit, upperLimit) > 0) {
      // do not use the new values, in case the lower limit is greater than upper limit
      compObj.setUseNewLowLmt(false);
      compObj.setUseNewUpLmt(false);

    }

  }

  /**
   * @param defaultExactMatchFlag
   * @param newRule
   * @param oldRule
   * @param compObj
   */
  private void setExactMatch(final boolean exactMatch, final ReviewRule oldRule, final ReviewRule newRule,
      final CalDataImportComparisonModel compObj) {
    if (oldRule == null) {
      newRule.setDcm2ssd(exactMatch);// setting the default value in the new rule
    }
    else {
      newRule.setDcm2ssd(oldRule.isDcm2ssd());// set value from old rule
    }
    // find the reference value
    String refVal = "";
    if (compObj.isUseNewRefVal()) {
      refVal = newRule.getRefValueDispString();
    }
    else if (oldRule != null) {
      refVal = oldRule.getRefValueDispString();
    }
    compObj.setExactMatchEditable(!CommonUtils.isEmptyString(refVal));

    if (CommonUtils.isEmptyString(refVal)) {
      // if there is no ref value , then set the exact match to false
      newRule.setDcm2ssd(false);
    }

  }

  /**
   * @param defaultComment String
   * @param oldHint String
   * @param newRule
   * @param compObj
   */
  private void initialiseHint(final String defaultComment, final String oldHint, final ReviewRule newRule,
      final CalDataImportComparisonModel compObj) {
    StringBuilder hint = new StringBuilder(ApicConstants.HINT_STRING_SIZE);
    String newHint = "";

    // append the new comment if it not existing already
    if ((newRule != null) && !CommonUtils.isEmptyString(newRule.getHint())) {
      newHint = newRule.getHint();
    }
    boolean appendNewHint = !CommonUtils.isEmptyString(newHint) && !CommonUtils.checkNull(oldHint).contains(newHint);
    boolean appendDefaultComment =
        !CommonUtils.isEmptyString(defaultComment) && !CommonUtils.checkNull(oldHint).contains(defaultComment);
    if (!CommonUtils.isEmptyString(oldHint)) {
      if (!oldHint.startsWith("iCDM:") && (appendNewHint || appendDefaultComment)) {
        hint.append("iCDM:\n");
      }
      hint.append(oldHint);
    }
    // if both old and new comments are present
    compObj.setInfoDecorNeeded(!CommonUtils.isEmptyString(oldHint) && (appendNewHint || appendDefaultComment));
    if (compObj.isInfoDecorNeeded()) {
      hint.append("\n").append("Import - ");// insert line break if old hint is available
    }
    if (appendDefaultComment) {
      // Add Import only if default comment or new hint is present
      hint.append(defaultComment);
    }
    if (appendNewHint && appendDefaultComment) {
      hint.append("\n");
    }
    if (appendNewHint) {
      hint.append(newHint);
    }

    if (newRule != null) {
      newRule.setHint(hint.toString());
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IWizardPage getPreviousPage() {
    if (this.wizard.getWizardData().isExceptionOccured()) {
      return null;
    }
    return super.getPreviousPage();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IWizardPage getNextPage() {
    if (this.wizard.getWizardData().isExceptionOccured()) {
      return this.wizard.getPage("Import Calibration Data 2");
    }
    return super.getNextPage();
  }

  /**
   * @return the wizardData
   */
  public CalDataFileImportWizardData getWizardData() {
    return this.wizard.getWizardData();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IWizardContainer getContainer() {
    // TODO Auto-generated method stub
    return super.getContainer();
  }

  // TODO: Use Guava library from Java
  private Map<String, Map<Integer, Set<AttributeValueModel>>> constructAttrValCombi() {
    // TODO: Make it specific for each parameter in ruleset i.e Map<Parameter,Map<Integer,Map<Attribute,AttributeValue>>
    // Map<Parameter,Map<Integer,Set<FeatureValueModel>>>
    Map<Attribute, SortedSet<AttributeValue>> userSelectedAttrVals = this.wizard.getWizardData().getAttrVals();


    ParamCollectionDataProvider paramColDataProvider = this.wizard.getWizardData().getParamColDataProvider();
    IParamRuleResponse paramRuleResponse = this.wizard.getWizardData().getParamRuleResponse();


    Map<String, P> paramMap = new HashMap<String, P>();
    Map<String, List<D>> attrMap = new HashMap<String, List<D>>();
    Map<Long, Attribute> depAttributes = new HashMap<Long, Attribute>();

    Map<String, Map<Attribute, SortedSet<AttributeValue>>> validCombinationsForMappedObj = new HashMap<>();
    CalDataImportData calImportData = this.wizard.getCalImportData();


    if (paramRuleResponse != null) {
      attrMap.putAll(paramRuleResponse.getAttrMap());
      paramMap.putAll(paramRuleResponse.getParamMap());
      depAttributes.putAll(paramRuleResponse.getAttrObjMap());
      if (this.wizard.getWizardData().getImportObject() instanceof Function) {
        paramRuleResponse = addParamDepenForFunc(userSelectedAttrVals, paramRuleResponse, paramMap, attrMap,
            depAttributes, validCombinationsForMappedObj, calImportData);
      }
      else {
        addValidAttrValCombinationForRuleSet(userSelectedAttrVals, paramColDataProvider, paramMap, attrMap,
            depAttributes, validCombinationsForMappedObj, calImportData);
      }
    }


    Map<String, Map<Integer, Set<AttributeValueModel>>> validAttrValModel = new HashMap<>();
    for (Entry<String, Map<Attribute, SortedSet<AttributeValue>>> validCombEntry : validCombinationsForMappedObj
        .entrySet()) {
      if (validCombEntry.getValue() == null) {
        Map<Integer, Set<AttributeValueModel>> emptyAttrValModel = new HashMap<>();
        validAttrValModel.put(validCombEntry.getKey(), emptyAttrValModel);
      }
      else {
        CombinationCalculator combinationCalculator = new CombinationCalculator(validCombEntry.getValue());
        Map<Integer, Map<Long, AttributeValue>> constructAttrValCombi = combinationCalculator.constructAttrValCombi();
        Map<Integer, Set<AttributeValueModel>> combiAttrValModel = constructCombiAttrValModel(constructAttrValCombi);
        validAttrValModel.put(validCombEntry.getKey(), combiAttrValModel);
      }
    }
    if (null != calImportData) {
      calImportData.setMappedObjCombiAttrValModelMap(validAttrValModel);
    }
    return validAttrValModel;
  }

  /**
   * @param userSelectedAttrVals
   * @param paramColDataProvider
   * @param paramMap
   * @param attrMap
   * @param depAttributes
   * @param validCombinationsForMappedObj
   * @param calImportData
   */
  private void addValidAttrValCombinationForRuleSet(Map<Attribute, SortedSet<AttributeValue>> userSelectedAttrVals,
      ParamCollectionDataProvider paramColDataProvider, Map<String, P> paramMap, Map<String, List<D>> attrMap,
      Map<Long, Attribute> depAttributes,
      Map<String, Map<Attribute, SortedSet<AttributeValue>>> validCombinationsForMappedObj,
      CalDataImportData calImportData) {
    for (Entry<String, P> attributeMappedObject : paramMap.entrySet()) {

      boolean flag = paramColDataProvider.isInputParamChecked(this.wizard.getWizardData().getImportObject()) &&
          (null != calImportData) &&
          (calImportData.getInputDataMap().keySet().contains(attributeMappedObject.getKey()));
      if (flag || !paramColDataProvider.isInputParamChecked(this.wizard.getWizardData().getImportObject())) {

        List<D> attrList = attrMap.get(attributeMappedObject.getKey());
        if ((attrList != null) && !attrList.isEmpty()) {

          for (D paramattr : attrList) {
            Attribute attribute = depAttributes.get(paramattr.getAttrId());
            Map<Attribute, SortedSet<AttributeValue>> chosenAttrValCombination =
                validCombinationsForMappedObj.get(attributeMappedObject.getKey());
            if (chosenAttrValCombination == null) {
              chosenAttrValCombination = new HashMap<>();
            }
            chosenAttrValCombination.put(attribute, userSelectedAttrVals.get(attribute));
            validCombinationsForMappedObj.put(attributeMappedObject.getKey(), chosenAttrValCombination);
          }
        }
        else {
          validCombinationsForMappedObj.put(attributeMappedObject.getKey(), null);
        }
      }
    }
  }

  /**
   * @param userSelectedAttrVals
   * @param paramRuleResponse
   * @param paramMap
   * @param attrMap
   * @param depAttributes
   * @param validCombinationsForMappedObj
   * @param calImportData
   * @return
   */
  private IParamRuleResponse addParamDepenForFunc(final Map<Attribute, SortedSet<AttributeValue>> userSelectedAttrVals,
      IParamRuleResponse paramRuleResponse, final Map<String, P> paramMap, final Map<String, List<D>> attrMap,
      final Map<Long, Attribute> depAttributes,
      final Map<String, Map<Attribute, SortedSet<AttributeValue>>> validCombinationsForMappedObj,
      final CalDataImportData calImportData) {
    for (Function func : this.wizard.getCalImportData().getParamFuncObjMap().values()) {
      paramRuleResponse = this.wizard.getCalImportData().getFuncParamRespMap().get(func.getName());
      paramMap.putAll(paramRuleResponse.getParamMap());
      attrMap.putAll(paramRuleResponse.getAttrMap());
      depAttributes.putAll(paramRuleResponse.getAttrObjMap());
    }
    for (String paramName : calImportData.getInputDataMap().keySet()) {
      List<D> attrList = attrMap.get(paramName);
      if ((attrList != null) && !attrList.isEmpty()) {

        for (D paramattr : attrList) {
          Attribute attribute = depAttributes.get(paramattr.getAttrId());
          Map<Attribute, SortedSet<AttributeValue>> chosenAttrValCombination =
              validCombinationsForMappedObj.get(paramName);
          if (chosenAttrValCombination == null) {
            chosenAttrValCombination = new HashMap<>();
          }
          chosenAttrValCombination.put(attribute, userSelectedAttrVals.get(attribute));
          validCombinationsForMappedObj.put(paramName, chosenAttrValCombination);
        }
      }
      else {
        validCombinationsForMappedObj.put(paramName, null);
      }

    }
    return paramRuleResponse;
  }

  /**
   * @return the Attr Val Model Set for Searching rules
   */
  private Map<Integer, Set<AttributeValueModel>> constructCombiAttrValModel(
      final Map<Integer, Map<Long, AttributeValue>> combiAttrAttrValMap) {
    AttributeValueModel attrValModel;
    int modelKey = 1;
    Map<Integer, Set<AttributeValueModel>> attrValModels = new HashMap<>();
    for (Map<Long, AttributeValue> mapValue : combiAttrAttrValMap.values()) {
      Set<AttributeValueModel> attrValueModSet = new HashSet<>();
      for (Map.Entry<Long, AttributeValue> attrVal : mapValue.entrySet()) {

        AttributeValue val = attrVal.getValue();


        CalDataImporterHandler calDataImportHandler = this.wizard.getCalDataImportHandler();
        Attribute attribute = null;
        try {
          attribute = calDataImportHandler.getAttribute(val);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
          return null;
        }

        if (ApicConstants.NOT_USED.equals(val.getName())) {
          attrValModel = createAttributeValueModel(attribute, createAttrValNotUsedObject(attribute.getId()));
        }
        else if (ApicConstants.USED.equals(val.getName())) {
          attrValModel = createAttributeValueModel(attribute, createAttrValUsedObject(attribute.getId()));
        }
        else if(CDRConstants.DONT_CARE_ATTR_VALUE_TEXT.equals(val.getName())) {
          attrValModel = createAttributeValueModel(attribute, val);
        }
        
        
        else {
          attrValModel = new AttributeValueModel();
          attrValModel.setAttr(attribute);
          attrValModel.setValue(val);
        }
        attrValueModSet.add(attrValModel);
      }
      attrValModels.put(modelKey, attrValueModSet);
      modelKey++;
    }
    return attrValModels;
  }
  
  private AttributeValueModel createAttributeValueModel(Attribute attribute, AttributeValue attrValue) {
    AttributeValueModel attrValModel = new AttributeValueModel();
    attrValModel.setAttr(attribute);
    attrValModel.setValue(attrValue);
    return attrValModel;
  }
  

  /**
   * @param attrId attribute id
   * @return Attribute values - used
   */
  private AttributeValue createAttrValUsedObject(final Long attrId) {
    AttributeValue attributeValue = new AttributeValue();
    attributeValue.setAttributeId(attrId);
    attributeValue.setName(ApicConstants.USED);
    attributeValue.setId(ApicConstants.ATTR_VAL_USED_VALUE_ID);
    attributeValue.setDescription(ApicConstants.USED);
    return attributeValue;
  }

  /**
   * @param attrId attribute id
   * @return Attribute values - Not used
   */
  private AttributeValue createAttrValNotUsedObject(final Long attrId) {
    AttributeValue attributeValue = new AttributeValue();
    attributeValue.setAttributeId(attrId);
    attributeValue.setName(ApicConstants.NOT_USED);
    attributeValue.setId(ApicConstants.ATTR_VAL_NOT_USED_VALUE_ID);
    attributeValue.setDescription(ApicConstants.NOT_USED);
    return attributeValue;
  }

}
