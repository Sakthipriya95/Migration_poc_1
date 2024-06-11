/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.wizards.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueDontCare;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.ConfigBasedRuleInput;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleParamCol;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.ruleseditor.dialogs.RuleAttrValueEditDialog;
import com.bosch.caltool.icdm.ruleseditor.wizards.AddNewConfigWizard;
import com.bosch.caltool.icdm.ruleseditor.wizards.AddNewConfigWizardData;
import com.bosch.caltool.icdm.ws.rest.client.cdr.ReviewRuleServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RuleSetRuleServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;


/**
 * ICDM-1081 This page is to choose an existing attr val combnation or to create a new one
 *
 * @author mkl2cob
 */
public class SelectAttrValWizardPage<D extends IParameterAttribute, P extends IParameter> extends WizardPage {

  /**
   *
   */
  private static final int VAL_EDIT_COL_WIDTH = 25;
  /**
   * section
   */
  private Section sectionThree;
  /**
   * form
   */
  private Form formThree;

  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;


  /**
   * Table for attributes & dependencies
   */
  private GridTableViewer attrsTableViewer;

  int key = 1;

  /**
   * @return the attrsTableViewer
   */
  public GridTableViewer getAttrsTableViewer() {
    return this.attrsTableViewer;
  }

  /**
   * instance of wizard data
   */
  private AddNewConfigWizardData<D, P> wizardData;
  ConcurrentMap<Integer, Map<Long, AttributeValue>> attrVals = new ConcurrentHashMap<>();
  Queue<IParameterAttribute> queue = new LinkedList<>();
  private Queue<IParameterAttribute> paramAttrQueue;


  /**
   * @return the wizardData
   */
  public AddNewConfigWizardData getWizardData() {
    return this.wizardData;
  }

  private static final String PAGE_TITLE = "Attribute Value Combination";

  private static final String PAGE_DESCRIPTION =
      "Please select the attribute/value you want to use or enter a new attribute value combination in column New Attr/Val";

  /**
   * @param pageName Name of the page
   */
  public SelectAttrValWizardPage(final String pageName) {
    super(pageName);
    setTitle(PAGE_TITLE);
    setDescription(PAGE_DESCRIPTION);
    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CDR_WIZARD_PG1_67X57));
    setPageComplete(false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite composite) {
    this.wizardData = ((AddNewConfigWizard) getWizard()).getWizardData();
    initializeDialogUnits(composite);
    final Composite workArea = new Composite(composite, SWT.NONE);
    // create layout for composite
    createGridLayout(workArea);
    createSectionAttr(workArea);
  }

  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * @param workArea Composite
   */
  private void createSectionAttr(final Composite workArea) {
    this.sectionThree = getFormToolkit().createSection(workArea, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionThree.setText("Dependent Attributes");
    this.sectionThree.setExpanded(true);
    this.sectionThree.getDescriptionControl().setEnabled(true);
    this.sectionThree.setDescription(
        "Displaying dependent attributes for selected parameters. Select values from dropdown to search");

    createFormThree();
    this.sectionThree.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.sectionThree.setClient(this.formThree);

  }

  /**
   * create form
   */
  private void createFormThree() {

    final GridData gridDataFour = new GridData();
    gridDataFour.horizontalAlignment = GridData.FILL;
    gridDataFour.grabExcessHorizontalSpace = true;
    gridDataFour.verticalAlignment = GridData.FILL;
    gridDataFour.grabExcessVerticalSpace = true;

    this.formThree = getFormToolkit().createForm(this.sectionThree);

    GridLayout layout = new GridLayout();
    this.formThree.getBody().setLayout(layout);

    createAttrsTable(gridDataFour);

  }

  /**
   * @param gridDataFour
   */
  private void createAttrsTable(final GridData gridDataFour) {
    this.attrsTableViewer =
        new GridTableViewer(this.formThree.getBody(), SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);


    this.attrsTableViewer.getGrid().setLayoutData(gridDataFour);

    this.attrsTableViewer.getGrid().setLinesVisible(true);
    this.attrsTableViewer.getGrid().setHeaderVisible(true);

    this.attrsTableViewer.setContentProvider(ArrayContentProvider.getInstance());

    createColumns();
    addMouseDownListener();


    AddNewConfigWizard<D, P> wizard = (AddNewConfigWizard) getWizard();
    ParameterDataProvider<D, P> paramDataProvider = wizard.getParamDataProvider();

    List<D> dependencyAttrset = paramDataProvider.getParamAttrs(wizard.getWizardData().getCdrParameter());

    this.attrsTableViewer.setInput(dependencyAttrset);


  }

  /**
   * Add mouse down listener to the pidc attribute value edit column
   */
  private void addMouseDownListener() {
    this.attrsTableViewer.getGrid().addMouseListener(new MouseAdapter() {

      @Override
      public void mouseDown(final MouseEvent event) {
        final int columnIndex =
            GridTableViewerUtil.getInstance().getTabColIndex(event, SelectAttrValWizardPage.this.attrsTableViewer);


        if (columnIndex == ApicUiConstants.COLUMN_INDEX_2) {
          final Point point = new Point(event.x, event.y);
          // Determine which row was selected
          final GridItem item = SelectAttrValWizardPage.this.attrsTableViewer.getGrid().getItem(point);
          if ((item != null) && !item.isDisposed()) {
            // Determine which column was selected
            for (int i = 0, n = SelectAttrValWizardPage.this.attrsTableViewer.getGrid().getColumnCount(); i < n; i++) {
              final Rectangle rect = item.getBounds(i);
              if (rect.contains(point)) {
                editTabItem(columnIndex, point);
                break;
              }
            }
          }
        }
      }
    });
  }

  /**
   * @param columnIndex deines gridviewer column index
   */
  private void editTabItem(final int columnIndex, final Point point) {
    final D editableAttr = getSelectedPIDCAttr(point);
    editableAttr.getName();
    if (columnIndex == ApicUiConstants.COLUMN_INDEX_2) {
      final RuleAttrValueEditDialog dialog = new RuleAttrValueEditDialog(this.attrsTableViewer.getControl().getShell(),
          SelectAttrValWizardPage.this, editableAttr);
      dialog.open();
      SelectAttrValWizardPage.this.setPageComplete(true);
      GridItem[] items = SelectAttrValWizardPage.this.attrsTableViewer.getGrid().getItems();
      for (GridItem gridItem : items) {
        if (ApicConstants.DEFAULT_COMBO_SELECT.equals(gridItem.getText(1)) || gridItem.getText(1).isEmpty()) {
          // ICDM-1346
          SelectAttrValWizardPage.this.setPageComplete(false);
          getWizard().getContainer().updateButtons();
        }
      }

    }
    else {
      CDMLogger.getInstance().info(ApicUiConstants.EDIT_NOT_ALLOWED, Activator.PLUGIN_ID);
    }
  }


  private D getSelectedPIDCAttr(final Point point) {
    D pidcAttr = null;
    // Determine which row was selected
    SelectAttrValWizardPage.this.attrsTableViewer.getGrid().selectCell(point);
    final IStructuredSelection selection =
        (IStructuredSelection) SelectAttrValWizardPage.this.attrsTableViewer.getSelection();
    if ((selection != null) && (!selection.isEmpty())) {
      final Object element = selection.getFirstElement();
      if (element instanceof IParameterAttribute) {
        pidcAttr = (D) element;

      }
    }
    return pidcAttr;
  }

  /**
   * Defines the columns of the TableViewer
   */
  private void createColumns() {


    createAttrCol();


    createValCol();
    createAttrValSelColViewer();


  }

  /**
   *
   */
  private void createAttrCol() {
    final GridViewerColumn attrNameColumn = new GridViewerColumn(this.attrsTableViewer, SWT.NONE);
    attrNameColumn.getColumn().setText("Attribute");
    attrNameColumn.getColumn().setWidth(200);

    attrNameColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        D param = (D) element;
        return param.getName();
      }
    });
  }

  /**
   *
   */
  private void createValCol() {
    final GridViewerColumn valueColumn = new GridViewerColumn(this.attrsTableViewer, SWT.NONE);
    valueColumn.getColumn().setText("Value");
    valueColumn.getColumn().setWidth(200);

    valueColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */

      @Override
      public String getText(final Object element) {
        D paramAttr = (D) element;
        String returnStr = ApicConstants.DEFAULT_COMBO_SELECT;
        Set<AttributeValueModel> attrValueModSet = SelectAttrValWizardPage.this.wizardData.getAttrValueModSet();
        if (SelectAttrValWizardPage.this.wizardData.isAttrValMapIncomplete()) {
          if (SelectAttrValWizardPage.this.wizardData.getCdrRulesList().size() > 1) {
            // for multiple edit
            if (!SelectAttrValWizardPage.this.wizardData.getAttrributesIncomplete().contains(paramAttr)) {
              returnStr = CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
            }
          }
          else {
            returnStr = textForSingleEdit(paramAttr, returnStr, attrValueModSet);
          }
        }
        if (!SelectAttrValWizardPage.this.wizardData.getAttrVals().isEmpty()) {
          SortedSet<AttributeValue> selVals = SelectAttrValWizardPage.this.wizardData.getAttrVals().get(paramAttr);
          if (selVals != null) {
            StringBuilder values = new StringBuilder();
            for (AttributeValue selVal : selVals) {
              values.append(selVal.getName()).append(",");
            }
            return values.substring(0, values.length() - 1);
          }
        }
        return returnStr;
      }

      /**
       * @param paramAttr ParameterAttribute
       * @param returnStr return string
       * @param attrValueModSet set of attribute value model
       * @return
       */
      private String textForSingleEdit(final D paramAttr, final String returnStr,
          final Set<AttributeValueModel> attrValueModSet) {
        String textForSingleEdit = returnStr;
        // for single edit display the value text
        for (AttributeValueModel attrValModel : attrValueModSet) {
          if (CommonUtils.isEqual(attrValModel.getAttr().getId(), paramAttr.getAttrId())) {
            textForSingleEdit = attrValModel.getValue().getName();
          }
        }

        return textForSingleEdit;
      }
    });
  }

  /**
   * This method creates PIDC attribute value edit column
   */
  private void createAttrValSelColViewer() {
    final GridViewerColumn attrValEditColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(SelectAttrValWizardPage.this.attrsTableViewer, VAL_EDIT_COL_WIDTH);
    attrValEditColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * Defines attribute value edit image
       */
      private Image editImage;

      /**
       * Get the value edit image {@inheritDoc}
       */
      @Override
      public Image getImage(final Object element) {
        D paramAttr = (D) element;
        if (!SelectAttrValWizardPage.this.wizardData.getAttrributesIncomplete().contains(paramAttr)) {
          return null;
        }
        if (this.editImage == null) {
          this.editImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.VALUE_EDIT_28X30);
          return this.editImage;
        }
        return this.editImage;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return "";
      }
    });
  }

  /**
   * @return the Attr Val Model Set for Searching rules
   */
  private ConcurrentMap<Integer, Set<AttributeValueModel>> setAttrValModels() {
    AttributeValueModel attrValModel = null;
    int modelKey = 1;
    ConcurrentMap<Integer, Set<AttributeValueModel>> attrValModels = new ConcurrentHashMap<>();
    for (Map<Long, AttributeValue> mapValue : this.attrVals.values()) {
      Set<AttributeValueModel> attrValueModSet = new HashSet<>();
      for (Map.Entry<Long, AttributeValue> attrVal : mapValue.entrySet()) {

        try {
          Long attrId = attrVal.getKey();
          AttributeValue val = attrVal.getValue();
          AddNewConfigWizard<D, P> wizard = (AddNewConfigWizard) getWizard();
          ParameterDataProvider<D, P> paramDataProvider = wizard.getParamDataProvider();
          attrValModel = paramDataProvider.createAttrValModel(attrId, val);
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().errorDialog("Exception when creating Attr Value model", exp, Activator.PLUGIN_ID);
        }

        attrValueModSet.add(attrValModel);
      }
      attrValModels.put(modelKey, attrValueModSet);
      modelKey++;
    }
    return attrValModels;
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
   * set the attr val model during next pressed
   *
   * @throws IcdmException
   */
  public void nextPressed() {

    if (!vaidateDontCareAttrValue()) {
      setPageComplete(false);
    }

    this.attrVals.clear();
    // ICDM-1346
    constructAttrValCombi();

    AddNewConfigWizard addNewConfigWizard = (AddNewConfigWizard) getWizard();
    addNewConfigWizard.getWizardData().setAttrValModels(setAttrValModels());

    // ICDM-1176
    if (addNewConfigWizard.getNextPage(this) instanceof CreateEditRuleWizardPage) {
      CreateEditRuleWizardPage createEditRulePage = (CreateEditRuleWizardPage) addNewConfigWizard.getNextPage(this);
      createEditRulePage.getRuleInfoSection().setSelectedCdrRule(addNewConfigWizard.getWizardData().getCdrRule());
      createEditRulePage.getRuleInfoSection().setRuleDetails(null);
      createEditRulePage.setTitleDescription();

    }
    copyRulefromAnotherParamChk(addNewConfigWizard);
  }


  private boolean vaidateDontCareAttrValue() {

    int numOfDepAttrWithDontCare = 0;
    for (Entry<D, SortedSet<AttributeValue>> paramAttrValEntry : this.wizardData.getAttrVals().entrySet()) {
      for (AttributeValue attributeValue : paramAttrValEntry.getValue()) {
        if (attributeValue instanceof AttributeValueDontCare) {
          numOfDepAttrWithDontCare++;
        }
      }
    }

    if (CommonUtils.isNotEmpty(this.wizardData.getAttrVals()) &&
        CommonUtils.isEqual(this.wizardData.getAttrVals().size(), numOfDepAttrWithDontCare)) {
      MessageDialogUtils.getErrorMessageDialog("Error",
          "Invalid attribute value combination. Atleast one dependent attribute must have value other than " +
              CDRConstants.DONT_CARE_ATTR_VALUE_TEXT + ".");
      return false;
    }

    return true;

  }

  /**
   * If the param has to be copied then make a check
   *
   * @param addNewConfigWizard
   */
  private void copyRulefromAnotherParamChk(final AddNewConfigWizard addNewConfigWizard) {
    if (addNewConfigWizard.isCopyRuleFromOtherParam()) {
      String paramName = this.wizardData.getCdrParameter().getName();
      List<String> labelNames = new ArrayList<>();
      labelNames.add(paramName);
      String errorMess = "";
      for (Set<AttributeValueModel> attrValModel : this.wizardData.getAttrValModels().values()) {
        try {
          Map<String, List<ReviewRule>> ruleMap = null;
          SortedSet<AttributeValueModel> dependencySet = new TreeSet<>();
          dependencySet.addAll(attrValModel);
          List<AttributeValueModel> dependencyList = new ArrayList<>();
          dependencyList.addAll(attrValModel);
          if (this.wizardData.getCdrFunction() instanceof Function) {
            ConfigBasedRuleInput<Function> configBasedRuleInput = new ConfigBasedRuleInput<>();
            ReviewRuleParamCol<Function> reviewRuleParamCol = new ReviewRuleParamCol<>();
            reviewRuleParamCol.setParamCollection((Function) this.wizardData.getCdrFunction());
            configBasedRuleInput.setLabelNames(labelNames);
            configBasedRuleInput.setAttrValueModSet(dependencySet);
            configBasedRuleInput.setParamCol(reviewRuleParamCol);
            ReviewRuleServiceClient reviewRuleServiceClient = new ReviewRuleServiceClient();
            ruleMap = reviewRuleServiceClient.readRulesForDependency(configBasedRuleInput);
          }
          else if (this.wizardData.getCdrFunction() instanceof RuleSet) {
            ConfigBasedRuleInput<RuleSet> configBasedRuleInput = new ConfigBasedRuleInput<>();
            ReviewRuleParamCol<RuleSet> reviewRuleParamCol = new ReviewRuleParamCol<>();
            reviewRuleParamCol.setParamCollection((RuleSet) this.wizardData.getCdrFunction());
            configBasedRuleInput.setLabelNames(labelNames);
            configBasedRuleInput.setAttrValueModSet(dependencySet);
            configBasedRuleInput.setParamCol(reviewRuleParamCol);
            RuleSetRuleServiceClient ruleSetRuleServiceClient = new RuleSetRuleServiceClient();
            ruleMap = ruleSetRuleServiceClient.readRulesForDependency(configBasedRuleInput);
          }
          if (isRuleExists(ruleMap, dependencyList, paramName)) {
            if (errorMess.isEmpty()) {
              errorMess = "Rule already exists for the attribute Value combination \n";
            }
            else {
              errorMess = CommonUtils.concatenate(errorMess, "\n");
            }
            for (AttributeValueModel featureValueModel : dependencyList) {
              errorMess = CommonUtils.concatenate(errorMess, " ", featureValueModel.getAttr().getName(), " : ",
                  featureValueModel.getValue().getName(), ",");
            }

          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().errorDialog("Error while reading rule. " + exp.getMessage(), exp,
              Activator.PLUGIN_ID);
          setPageComplete(false);
        }
      }
      if (!errorMess.isEmpty()) {
        errorMess = errorMess.substring(0, errorMess.lastIndexOf(','));
        errorMess =
            CommonUtils.concatenate(errorMess, "\n Only insert of new rule is allowed.Select any other combination.");
        CDMLogger.getInstance().errorDialog(errorMess, Activator.PLUGIN_ID);
        SelectAttrValWizardPage.this.setPageComplete(false);
        getWizard().getContainer().updateButtons();
      }
    }
  }

  /**
   * @param ruleMap
   * @param dependencyList
   * @param paramName
   */
  private boolean isRuleExists(final Map<String, List<ReviewRule>> ruleMap,
      final List<AttributeValueModel> dependencyList, final String paramName) {

    List<ReviewRule> ruleList = ruleMap.get(paramName);
    if (CommonUtils.isNotNull(ruleList)) {
      for (ReviewRule rule : ruleList) {
        if ((rule.getDependencyList() != null) && (rule.getDependencyList().size() == dependencyList.size())) {
          for (AttributeValueModel feaValModel : rule.getDependencyList()) {
            for (AttributeValueModel depModel : dependencyList) {
              if ((feaValModel.getAttr().getId().equals(depModel.getAttr().getId())) &&
                  (feaValModel.getValue().getId().equals(depModel.getValue().getId()))) {
                return true;
              }
            }
          }

        }

      }
    }
    return false;

  }

  private void constructAttrValCombi() {
    this.paramAttrQueue = new LinkedList<>();
    this.paramAttrQueue.addAll(this.wizardData.getAttrVals().keySet());
    constructAllCom(this.paramAttrQueue.poll(), null);
  }

  /**
   * @param paramAttrQueue
   */
  private void constructAllCom(final IParameterAttribute paramAttr, final AttributeValue... attrValPasd) {
    IParameterAttribute poppedParam = this.paramAttrQueue.poll();
    for (AttributeValue attrVal : this.wizardData.getAttrVals().get(paramAttr)) {
      if (null == poppedParam) {
        // base case since there is no call to constructAllCon after this line
        ConcurrentMap<Long, AttributeValue> attrValMap = new ConcurrentHashMap<>();
        if (attrValPasd == null) {
          attrValMap.put(attrVal.getAttributeId(), attrVal);
          this.attrVals.put(this.key, attrValMap);
          this.key++;
        }
        else {
          if (attrValPasd.length != 0) {
            AttributeValue[] attributeValuesToBePassed = Arrays.copyOf(attrValPasd, attrValPasd.length + 1);
            attributeValuesToBePassed[attributeValuesToBePassed.length - 1] = attrVal;
            for (AttributeValue attributeVal : attributeValuesToBePassed) {
              attrValMap.put(attributeVal.getAttributeId(), attributeVal);
            }
            this.attrVals.put(this.key, attrValMap);
            this.key++;
          }

        }
      }
      else {
        if (null == attrValPasd) {
          constructAllCom(poppedParam, attrVal);
        }
        else {
          if (attrValPasd.length == 0) {
            // first recursive call
            constructAllCom(poppedParam, new AttributeValue[] { attrVal });
          }
          else {
            AttributeValue[] attributeValuesToBePassed = Arrays.copyOf(attrValPasd, attrValPasd.length + 1);
            attributeValuesToBePassed[attributeValuesToBePassed.length - 1] = attrVal;
            constructAllCom(poppedParam, attributeValuesToBePassed);
          }

        }

      }
    }
    if (poppedParam != null) {
      this.paramAttrQueue.add(poppedParam);
    }
  }


}
