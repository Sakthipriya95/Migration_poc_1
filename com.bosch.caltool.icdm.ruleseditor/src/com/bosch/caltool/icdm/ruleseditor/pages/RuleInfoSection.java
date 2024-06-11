/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calcomp.calutil.tools.A2LUtil;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldata.history.HistoryEntry;
import com.bosch.calmodel.caldatacomparison.CalDataAttributes;
import com.bosch.calmodel.caldatacomparison.CalDataComparison;
import com.bosch.calmodel.caldataphyutils.CalDataTableGraphComposite;
import com.bosch.calmodel.caldataphyutils.exception.CalDataTableGraphException;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.RuleDefinition;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.bo.a2l.RuleMaturityLevel;
import com.bosch.caltool.icdm.common.bo.cdr.ReviewRuleUtil;
import com.bosch.caltool.icdm.common.ui.dialogs.AddLinkDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.EditLinkDialog;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.data.LinkData;
import com.bosch.caltool.icdm.common.ui.views.providers.ComboViewerContentPropsalProvider;
import com.bosch.caltool.icdm.common.ui.views.providers.LinkTableLabelProvider;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.Unit;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleExt;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleParamCol;
import com.bosch.caltool.icdm.model.cdr.RuleLinks;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.dialogs.BitWiseLimitConfigDialog;
import com.bosch.caltool.icdm.ruleseditor.dialogs.ComplexRuleEditDialog;
import com.bosch.caltool.icdm.ruleseditor.dialogs.EditRuleDialog;
import com.bosch.caltool.icdm.ruleseditor.listeners.EditRuleHintModListener;
import com.bosch.caltool.icdm.ruleseditor.listeners.EditRuleRefMatchSelListener;
import com.bosch.caltool.icdm.ruleseditor.listeners.RefValueDragListener;
import com.bosch.caltool.icdm.ruleseditor.listeners.RuleInfoImportFileBtnSelListener;
import com.bosch.caltool.icdm.ruleseditor.listeners.RuleInfoRefValueDropListener;
import com.bosch.caltool.icdm.ruleseditor.listeners.RuleInfoTableGraphDropListener;
import com.bosch.caltool.icdm.ruleseditor.wizards.AddNewConfigWizard;
import com.bosch.caltool.icdm.ruleseditor.wizards.AddNewConfigWizardData;
import com.bosch.caltool.icdm.ruleseditor.wizards.pages.CreateEditRuleWizardPage;
import com.bosch.caltool.icdm.ws.rest.client.apic.UnitServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.ReviewRuleServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RuleSetRuleServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.UserServiceClient;
import com.bosch.rcputils.IUtilityConstants;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;
import com.bosch.rcputils.ui.validators.Validator;


/**
 * this class is to reuse sections
 *
 * @author mkl2cob
 */
public final class RuleInfoSection<D extends IParameterAttribute, P extends IParameter> {

  /**
   *
   */
  private static final int HINT_LENGTH = 50;
  /**
   * CONSTANT FOR LIST SIZE ONE
   */
  private static final int LIST_SIZE_ONE = 1;
  /**
   * INDEX OF USE CURRENT VALUE STRING IN COMBO
   */
  private static final int USE_CURR_VAL_INDEX = 4;
  /**
   * CONSTANT FOR NUMBER OF COLUMNS IN GROUP THREE
   */
  private static final int NO_OF_COLS_GROUP_THREE = 6;
  /**
   * count for 2 columns
   */
  private static final int COLUMN_COUNT_2 = 2;
  /**
   * message for declaring that source and destination parameters are different
   */
  public static final String SRC_DEST_PAR_TYP_DIFF = "Source and destination parameter types are different!";
  /**
   * number of columns for unit group
   */
  private static final int NO_OF_COLS_FOR_UNIT = 2;
  /**
   * for blue color in graph
   */
  private static final int GRAPH_COLOR_BLUE = 0;
  /**
   * string constant for not applicable
   */
  private static final String NOT_APPLICABLE = "n.a.";
  /**
   * Text constant for VALUE
   */
  public static final String VALUE_TEXT = "VALUE";
  /**
   * Result Parameter for which comment is to be updated - null in case of multiple params
   */
  private final IParameter selectedParam;

  /**
   * CDRFunction instance to check access rights
   */
  private final ParamCollection cdrFunction;
  /**
   * Composite instance
   */
  private SashForm verticalSashForm;

  /**
   * true for wizard , false for edit rule dialog
   */
  private final boolean isWizard;

  /**
   * to know whether it is insert new rule or update rule
   */
  private boolean update;

  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  /** Table Graph Composite instance */
  private CalDataTableGraphComposite calDataTableGraphComposite;

  /**
   * CalDataComparison object used to hold calData objects used for comparison
   */
  private final CalDataComparison calDataComparison = new CalDataComparison();

  /**
   * Graph color holder for Table/Graph comparison object
   */
  private int graphColor = 1;
  /**
   * composite
   */
  private Composite ruleInfoAndTblGraphComp;
  /**
   * scroll composite
   */
  private ScrolledComposite ruleInfoAndTblGraphScrollComp;

  /**
   * selected unit
   */
  private String selectedUnit;
  /**
   * selected maturity level
   */
  private String selectedMaturityLevel;
  /**
   * combo for maturity level
   */
  private Combo maturityCombo;

  /**
   * form instance
   */
  private Form formReviewData;

  /**
   * sash form
   */
  private SashForm rvwSashForm;

  /**
   * reference value text
   */
  private Text refValueTxt;

  /**
   * manual button
   */
  private Button readySeriesButton;


  /**
   * use current rvw mtd
   */
  private Button useCurrRvwMtd;

  private Button complexRuleBtn;

  /**
   * lower limit text
   */
  private Text lowerLimitTxt;
  /**
   * Bitwise Rule text
   */
  private Text bitwiseRuleTxt;
  /**
   * old bitwise rule text
   */
  private String oldBitWiseLimit;

  private String advancedFormula;

  private String oldAdvancedFormula;

  private ParamCollectionDataProvider paramCollDataProvider;
  /**
   * SortedSet<LinkData>
   */
  private SortedSet<LinkData> ruleLinksSet = new TreeSet<>();


  /**
   * @return the oldAdvancedFormula
   */
  public String getOldAdvancedFormula() {
    return this.oldAdvancedFormula;
  }


  /**
   * @param oldAdvancedFormula the oldAdvancedFormula to set
   */
  public void setOldAdvancedFormula(final String oldAdvancedFormula) {
    this.oldAdvancedFormula = oldAdvancedFormula;
  }


  /**
   * @return the oldBitWiseLimit
   */
  public String getOldBitWiseLimit() {
    return this.oldBitWiseLimit;
  }


  /**
   * @return the useCurrRvwMtd
   */
  public Button getUseCurrRvwMtd() {
    return this.useCurrRvwMtd;
  }


  /**
   * @param oldBitWiseLimit the oldBitWiseLimit to set
   */
  public void setOldBitWiseLimit(final String oldBitWiseLimit) {
    this.oldBitWiseLimit = oldBitWiseLimit;
  }

  /**
   * @return the bitwiseRuleTxt
   */
  public Text getBitwiseRuleTxt() {
    return this.bitwiseRuleTxt;
  }

  /**
   * upper limit text
   */
  private Text upperLimitTxt;
  /**
   * old lower limit value
   */
  private String oldLowerLimit;

  /**
   * @return the oldLowerLimit
   */
  public String getOldLowerLimit() {
    return this.oldLowerLimit;
  }


  /**
   * @return the oldUpperLimit
   */
  public String getOldUpperLimit() {
    return this.oldUpperLimit;
  }

  /**
   * old upper limit value
   */
  private String oldUpperLimit;
  /**
   * lower limit decorator
   */
  private ControlDecoration lowerLimitDecor;

  /**
   * selected cdr rule
   */
  private ReviewRule selectedCdrRule;

  /** Map which holds the listener(SWT.Activate) instance for the Text */
  private final Map<Scrollable, Map<Integer, Listener>> textListenerMap = new HashMap<>();

  /**
   * text field wdth
   */
  private static final int TEXT_FIELD_WIDTHHINT_1 = 140;

  /**
   * text area width
   */
  private static final int TEXT_AREA_WIDTHHINT = 280;

  /**
   * text area height
   */
  private static final int TEXT_AREA_HEIGHTHINT = 150;

  /**
   * maturity level combo width
   */
  private static final int MAT_COMBO_WIDTH = 125;

  /**
   * maturity value combo height
   */
  private static final int MAT_COMBO_HEIGHT = 150;

  /**
   * Rule Info section sash form weight
   */
  private static final int RULE_INFO_SASH_WEIGHT = 2;

  /**
   * Table Graph sash form weight
   */
  private static final int TBL_GRAPH_SASH_WEIGHT = 1;
  /**
   * Links sash form weight
   */
  private static final int LINKS_SASH_WEIGHT = 1;

  /**
   * Top and bottom section o vertical sash form column length
   */
  private static final int SEC_COL_LEN = 2;

  /**
   * ready for series ui span
   */
  private static final int REM_UI_SPAN = 3;

  /**
   * length of deactivator listeners
   */
  private static final int DEACTIVATE_LEN = 0;

  /**
   * button for clearing ref value
   */
  private Button btnClearRefVal;
  /**
   * button for exact match for ref value
   */
  private Button btnParamRefValMatch;

  /**
   * radio button for not changing exact match flag
   */
  private Button btnUseCurrExactMatch;

  /**
   * radio button for changing exact match flag
   */
  private Button btnChooseExactMatch;

  /**
   * reference value caldata obj
   */
  private CalData refValCalDataObj;
  /**
   * hint text area
   */
  private Text hintTxtArea;
  /**
   * btn to dcm file upload
   */
  private Button btnDCMfile;

  /**
   * upper limit decorator
   */
  private ControlDecoration upperLimitDecor;

  /**
   * edit rule dialog
   */
  private EditRuleDialog editRuleDialog;

  /**
   * AddNewConfigWizard instance
   */
  private AddNewConfigWizard wizard;

  /**
   * wizard page instance
   */
  private CreateEditRuleWizardPage wizardPage;

  /**
   * true if the selected rules have same maturity level
   */
  private boolean sameMaturityLevel = true;

  /**
   * true if all selected rules have same hint
   */
  private boolean sameHint = true;

  /**
   * text field for unit
   */
  private Text unitText;
  /**
   * CalDataAttributes variable which hold the CalData object for the original Ref value if any This reference is
   * updated when a new caldata is dropped into RefValue field in edit dialog. This updation is done so that graph color
   * is maintained in the Table/Graph composite
   */
  private CalDataAttributes refCalAttr;

  /**
   * boolean to know if the reference value is typed
   */
  private boolean isRefValTyped = true;
  /**
   * Section to edit parameter properties
   */
  private CDRParameterEditSection paramEditSection;
  private final boolean readOnlyMode;


  private BitWiseLimitConfigDialog bitWiseConfigDialog;
  /**
   * constant for left mouse click
   */
  static final int LEFT_MOUSE_CLICK = 1;

  private CalData checkValueObj;
  private String pidcVersName;
  private String resultName;
  private SortedSet<AttributeValueModel> attrValModel;
  private final ParameterDataProvider<D, P> paramDataProvider;
  private Text unicodeRmrkTxtArea;
  /**
   * List of available units
   */
  private SortedSet<String> units = new TreeSet<>();
  private ScrolledComposite paramPropScrollComp;
  private Composite paramPropComp;
  /**
   * Link Table
   */
  private GridTableViewer linkTable;
  /**
   * edit link button
   */
  private Button btnEditLink;
  /**
   * delete link button
   */
  private Button btnDeleteLink;
  private Button btnAddLink;


  /**
   * Constructor for create/edit rule, for parameters without dependencies
   *
   * @param selectedParam CDRFuncParameter
   * @param cdrFunction CDRFunction
   * @param dialog edit rule dialog
   * @param checkValueObj check value
   * @param readOnlyMode if true, section is in read only mode
   * @param pidcVersName PIDC version name(for display)
   * @param resultName Review result name (for display)
   * @param attrValModel attribute value model
   */
  public RuleInfoSection(final IParameter selectedParam, final ParamCollection cdrFunction, final EditRuleDialog dialog,
      final CalData checkValueObj, final boolean readOnlyMode, final String pidcVersName, final String resultName,
      final SortedSet<AttributeValueModel> attrValModel) {

    this.selectedParam = selectedParam;
    this.cdrFunction = cdrFunction;
    this.editRuleDialog = dialog;
    this.isWizard = false;

    this.checkValueObj = checkValueObj;
    this.pidcVersName = pidcVersName;
    this.resultName = resultName;
    this.attrValModel = attrValModel;
    this.paramDataProvider = dialog.getParamDataProvider();
    this.paramCollDataProvider = dialog.getParamColDataProvider();
    if ((attrValModel == null) && (this.paramDataProvider.getReviewRule(selectedParam) != null)) {
      this.update = true;
      this.selectedCdrRule = this.paramDataProvider.getReviewRule(selectedParam);
    }
    this.readOnlyMode = readOnlyMode;
  }


  // ICDM-1162
  /**
   * Constructor to edit the default rule. Rule to be edited is passed directly
   *
   * @param selectedParam CDRFuncParameter
   * @param cdrRule rule to be edited
   * @param cdrFunction CDRFunction
   * @param dialog edit rule dialog
   * @param readOnlyMode if true, section is in read only mode
   */
  public RuleInfoSection(final IParameter selectedParam, final ReviewRule cdrRule, final ParamCollection cdrFunction,
      final EditRuleDialog dialog, final boolean readOnlyMode) {
    this.selectedParam = selectedParam;
    this.cdrFunction = cdrFunction;
    this.editRuleDialog = dialog;
    this.isWizard = false;
    this.update = false;
    if (cdrRule != null) {
      this.update = true;
    }
    this.selectedCdrRule = cdrRule;
    this.readOnlyMode = readOnlyMode;
    this.paramDataProvider = dialog.getParamDataProvider();
    this.paramCollDataProvider = dialog.getParamColDataProvider();
  }

  /**
   * Constructor for wizard 3rd page
   *
   * @param wizard AddNewConfigWizard
   * @param createEditRuleWizardPage CreateEditRuleWizardPage
   * @param isUpdate true if this is a rule update
   * @param cdrRule current rule if this is update, else null
   * @param readOnlyMode if true, section is in read only mode
   * @param paramDataProvider
   * @param paramColDataProvider
   */
  public RuleInfoSection(final AddNewConfigWizard wizard, final CreateEditRuleWizardPage createEditRuleWizardPage,
      final boolean isUpdate, final ReviewRule cdrRule, final boolean readOnlyMode,
      final ParameterDataProvider<D, P> paramDataProvider, final ParamCollectionDataProvider paramColDataProvider) {

    this.wizard = wizard;
    AddNewConfigWizardData wizardData = wizard.getWizardData();
    this.paramCollDataProvider = paramColDataProvider;
    this.selectedParam = wizardData.getCdrParameter();
    this.cdrFunction = wizardData.getCdrFunction();
    this.wizardPage = createEditRuleWizardPage;
    this.isWizard = true;
    this.update = isUpdate;
    this.sameMaturityLevel = wizardData.isSameMaturityLevel();
    this.selectedCdrRule = cdrRule;
    this.sameHint = wizardData.isSameHint();
    this.paramDataProvider = paramDataProvider;
    this.readOnlyMode = readOnlyMode;
  }


  /**
   * @param selectedParam
   * @param cdrRule
   * @param cdrFunction
   * @param editRuleDialog2
   * @param checkValueObj2
   * @param readOnlyMode if true, section is in read only mode
   * @param pidcVersName2
   * @param resultName
   */
  public RuleInfoSection(final IParameter selectedParam, final ReviewRule cdrRule, final ParamCollection cdrFunction,
      final EditRuleDialog editRuleDialog2, final CalData checkValueObj2, final boolean readOnlyMode,
      final String pidcVersName2, final String resultName) {

    this.selectedParam = selectedParam;
    this.cdrFunction = cdrFunction;
    this.editRuleDialog = editRuleDialog2;
    this.isWizard = false;
    this.checkValueObj = checkValueObj2;
    this.pidcVersName = pidcVersName2;
    this.resultName = resultName;
    this.update = true;
    this.selectedCdrRule = cdrRule;
    this.readOnlyMode = readOnlyMode;
    this.paramDataProvider = editRuleDialog2.getParamDataProvider();
    this.paramCollDataProvider = editRuleDialog2.getParamColDataProvider();

  }


  /**
   * @return the paramEditSection
   */
  public CDRParameterEditSection getParamEditSection() {
    return this.paramEditSection;
  }

  /**
   * @return the bitWiseConfigDialog
   */
  public BitWiseLimitConfigDialog getBitWiseConfigDialog() {
    return this.bitWiseConfigDialog;
  }


  /**
   * @return the attrValModel
   */
  public SortedSet<AttributeValueModel> getAttrValModel() {
    return this.attrValModel;
  }


  /**
   * create composite
   *
   * @param top parent composite
   * @return Composite
   */
  public Composite createComposite(final Composite top) {

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    final GridLayout gridLayout = new GridLayout();
    // ICDM-2665
    this.verticalSashForm = new SashForm(top, SWT.VERTICAL);
    this.verticalSashForm.setLayout(gridLayout);
    this.verticalSashForm.setLayoutData(gridData);

    // Create Top section - Param properties and links
    createParamPropScrollComp();
    // Create Bottom sction - Rule Info and Table Graph
    createRuleInfoAndTblGraphScrollComp();
    // Split the weigntage between top and bottom section o vertical sash form
    this.verticalSashForm.setWeights(new int[] { 1, 2 });

    return this.verticalSashForm;
  }

  /**
   *
   */
  private void createParamPropScrollComp() {

    this.paramPropScrollComp = new ScrolledComposite(this.verticalSashForm, SWT.V_SCROLL);
    this.paramPropComp = new Composite(this.paramPropScrollComp, SWT.BORDER);

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = SEC_COL_LEN;
    this.paramPropComp.setLayout(gridLayout);
    this.paramPropComp.setLayoutData(GridDataUtil.getInstance().getGridData());

    // Create Param Prop Section
    createParameterEditSection();

    this.paramPropScrollComp.setContent(this.paramPropComp);
    this.paramPropScrollComp.setExpandHorizontal(true);
    this.paramPropScrollComp.setExpandVertical(true);
    this.paramPropScrollComp.setDragDetect(true);
    this.paramPropScrollComp.addControlListener(new ControlAdapter() {

      @Override
      public void controlResized(final ControlEvent event) {
        // ICDM-895
        Rectangle rect = RuleInfoSection.this.paramPropScrollComp.getClientArea();
        RuleInfoSection.this.paramPropScrollComp
            .setMinSize(RuleInfoSection.this.paramPropComp.computeSize(rect.width, SWT.DEFAULT));
      }
    });
  }

  /**
  *
  */
  private void createParameterEditSection() {
    this.paramEditSection = new CDRParameterEditSection(this.paramPropComp, getFormToolkit(), this);
    this.paramEditSection.createsectionParamProperties();
  }


  /*
   * This method creates scrolled composite for Rule Info, tbl Graph and Links sectionsr
   */
  private void createRuleInfoAndTblGraphScrollComp() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.ruleInfoAndTblGraphScrollComp = new ScrolledComposite(this.verticalSashForm, SWT.H_SCROLL | SWT.V_SCROLL);
    this.ruleInfoAndTblGraphScrollComp.setLayout(new GridLayout());
    this.ruleInfoAndTblGraphScrollComp.setLayoutData(gridData);
    this.ruleInfoAndTblGraphComp = new Composite(this.ruleInfoAndTblGraphScrollComp, SWT.NONE);
    this.ruleInfoAndTblGraphComp.setLayout(new GridLayout());
    this.ruleInfoAndTblGraphComp.setLayoutData(gridData);

    createSectionReviewData();

    this.ruleInfoAndTblGraphScrollComp.setContent(this.ruleInfoAndTblGraphComp);
    this.ruleInfoAndTblGraphScrollComp.setExpandHorizontal(true);
    this.ruleInfoAndTblGraphScrollComp.setExpandVertical(true);
    this.ruleInfoAndTblGraphScrollComp.setDragDetect(true);
    this.ruleInfoAndTblGraphScrollComp.addControlListener(new ControlAdapter() {

      @Override
      public void controlResized(final ControlEvent event) {
        // ICDM-895
        Rectangle rect = RuleInfoSection.this.ruleInfoAndTblGraphScrollComp.getClientArea();
        RuleInfoSection.this.ruleInfoAndTblGraphScrollComp
            .setMinSize(RuleInfoSection.this.ruleInfoAndTblGraphComp.computeSize(rect.width, SWT.DEFAULT));
      }
    });
  }

  /**
   * This method creates section for Bottom section - Rule Info and Table Graph
   */
  private void createSectionReviewData() {

    Section sectionReviewData = this.formToolkit.createSection(this.ruleInfoAndTblGraphComp,
        Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    sectionReviewData.setLayoutData(GridDataUtil.getInstance().getGridData());
    final GridLayout layout = new GridLayout();
    layout.numColumns = SEC_COL_LEN;
    sectionReviewData.setLayout(layout);
    sectionReviewData.setText("Rule Information");

    // ICDM-1822
    StringBuilder description = new StringBuilder();
    if (null != this.selectedCdrRule) {
      if (this.isWizard) {
        addWizardLastModifiedUserToDesc(description);
      }
      else {
        addLastModifiedUserToDesc(description, this.selectedCdrRule, false);
      }
    }
    sectionReviewData.setDescription(description.toString());
    sectionReviewData.setExpanded(true);

    // Create Form
    this.formReviewData = this.formToolkit.createForm(sectionReviewData);
    this.formReviewData.getBody().setLayout(layout);
    this.formReviewData.setLayoutData(GridDataUtil.getInstance().getGridData());

    // IcDm-660
    createReviewSashForm();

    // Group for Rule Info
    createRuleInfoGroup();

    // Group for Table Graph
    createTableGraphGroup();

    // Create Group for Links
    createLinksScrollComp();

    sectionReviewData.setClient(this.formReviewData);
    sectionReviewData.getDescriptionControl().setEnabled(false);
    // Create Weigts 1:1 // IcDm-660
    this.rvwSashForm.setWeights(new int[] { RULE_INFO_SASH_WEIGHT, TBL_GRAPH_SASH_WEIGHT, LINKS_SASH_WEIGHT });
  }

  /**
  *
  */
  private void createLinksScrollComp() {

    // Create Scrolled Link Composite
    ScrolledComposite linkScrollComp = new ScrolledComposite(this.rvwSashForm, SWT.H_SCROLL | SWT.V_SCROLL);
    linkScrollComp.setLayoutData(GridDataUtil.getInstance().getGridData());
    linkScrollComp.setLayout(new GridLayout());

    final Composite linksComp = new Composite(linkScrollComp, SWT.NONE);
    linksComp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    linksComp.setLayoutData(GridDataUtil.getInstance().getGridData());
    linksComp.setLayout(new GridLayout());

    // Create Links Group
    createLinksGroup(linksComp);

    linkScrollComp.setContent(linksComp);
    linkScrollComp.setExpandHorizontal(true);
    linkScrollComp.setExpandVertical(true);
    linkScrollComp.setDragDetect(true);
    linkScrollComp.addControlListener(new ControlAdapter() {

      @Override
      public void controlResized(final ControlEvent event) {
        linkScrollComp.setMinSize(linksComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
      }
    });
    linksComp.setLayout(new GridLayout());
    linksComp.setLayoutData(GridDataUtil.getInstance().getGridData());
  }


  /**
   * @param linksComp
   */
  private void createLinksGroup(final Composite linksComp) {
    final Group linksGrp = new Group(linksComp, SWT.NONE);
    linksGrp.setLayoutData(GridDataUtil.getInstance().getGridData());
    linksGrp.setLayout(new GridLayout());
    linksGrp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

    final Section linksSection =
        this.formToolkit.createSection(linksGrp, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    linksSection.setText("Link Details");
    linksSection.setLayoutData(GridDataUtil.getInstance().getGridData());
    linksSection.setLayout(new GridLayout());

    // Create Link Composite
    final Composite linkComp = this.formToolkit.createComposite(linksSection);
    linkComp.setLayoutData(GridDataUtil.getInstance().getGridData());
    linkComp.setLayout(new GridLayout());
    linksSection.setClient(linkComp);

    // Create link Action Buttons
    createLinkBtns(linkComp);

    // Create Link Table
    createLinkTable(linkComp);
  }

  /**
   * @param linkComp
   */
  private void createLinkBtns(final Composite linkComp) {

    Composite btnComp = new Composite(linkComp, SWT.NONE);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    btnComp.setLayout(gridLayout);
    btnComp.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false));

    // create Add Link Btn
    createAddLinkBtn(btnComp);

    // create Edit Link Btn
    createEditLinkBtn(btnComp);

    // create Delete Link Btn
    createDelLinkBtn(btnComp);
  }


  /**
   * @param linkComp
   */
  private void createDelLinkBtn(final Composite linkComp) {
    this.btnDeleteLink = new Button(linkComp, SWT.PUSH);
    this.btnDeleteLink.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.DELETE_16X16));
    this.btnDeleteLink.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        IStructuredSelection selection = (IStructuredSelection) RuleInfoSection.this.linkTable.getSelection();
        LinkData linkData = (LinkData) selection.getFirstElement();
        RuleInfoSection.this.ruleLinksSet = (SortedSet<LinkData>) RuleInfoSection.this.linkTable.getInput();

        linkData.setOprType(CommonUIConstants.CHAR_CONSTANT_FOR_DELETE);

        RuleInfoSection.this.linkTable.setInput(RuleInfoSection.this.ruleLinksSet);// to invoke input
        // changed
        RuleInfoSection.this.linkTable.refresh();
        RuleInfoSection.this.linkTable.setSelection(null);
        RuleInfoSection.this.btnEditLink.setEnabled(false);
        RuleInfoSection.this.btnDeleteLink.setEnabled(false);
        enableSave();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent event) {
        // No Implementation Required
      }
    });
    this.btnEditLink.setEnabled(false);
    this.btnDeleteLink.setEnabled(false);
  }


  /**
   * @param linkComp
   */
  private void createEditLinkBtn(final Composite linkComp) {
    this.btnEditLink = new Button(linkComp, SWT.PUSH);
    this.btnEditLink.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.EDIT_16X16));
    this.btnEditLink.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection = (IStructuredSelection) RuleInfoSection.this.linkTable.getSelection();
        final LinkData linkData = (LinkData) selection.getFirstElement();
        final EditLinkDialog linkDialog =
            new EditLinkDialog(Display.getCurrent().getActiveShell(), linkData, RuleInfoSection.this.linkTable, true);
        linkDialog.open();
        enableSave();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent event) {
        // No Implementation Required
      }
    });
  }


  /**
   * @param linkComp
   */
  private void createAddLinkBtn(final Composite linkComp) {
    this.btnAddLink = new Button(linkComp, SWT.PUSH);
    this.btnAddLink.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.ADD_16X16));
    this.btnAddLink.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final AddLinkDialog linkDialog =
            new AddLinkDialog(Display.getCurrent().getActiveShell(), RuleInfoSection.this.linkTable, true);
        linkDialog.open();
        enableSave();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent event) {
        // NA
      }
    });
  }


  /**
   * @param linkComp
   */
  private void createLinkTable(final Composite linkComp) {
    this.linkTable = GridTableViewerUtil.getInstance().createGridTableViewer(linkComp,
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, new GridData());
    createTabColumns(this.linkTable);
    this.linkTable.getGrid().setLayoutData(GridDataUtil.getInstance().getGridData());

    this.linkTable.setContentProvider(new IStructuredContentProvider() {

      @Override
      public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        RuleInfoSection.this.ruleLinksSet = (SortedSet<LinkData>) newInput;
      }

      @Override
      public void dispose() {
        // NA
      }

      public Object[] getElements(final Object inputElement) {
        if (inputElement instanceof SortedSet<?>) {
          return ((SortedSet) inputElement).toArray();
        }
        return new Object[0];
      }

    });

    this.linkTable.setLabelProvider(new LinkTableLabelProvider());

    // Set Input to Link Table
    setLinkTableInput();

    // add selection listener to link table
    this.linkTable.getGrid().addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection = (IStructuredSelection) RuleInfoSection.this.linkTable.getSelection();
        if ((selection != null) && (selection.getFirstElement() != null)) {
          RuleInfoSection.this.btnEditLink.setEnabled(true);
          RuleInfoSection.this.btnDeleteLink.setEnabled(true);
        }
        else {
          RuleInfoSection.this.btnEditLink.setEnabled(false);
          RuleInfoSection.this.btnDeleteLink.setEnabled(false);
        }
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent event) {
        // No Implementation Required
      }
    });
  }


  /**
   *
   */
  private void setLinkTableInput() {

    // While creating new Rule, selectedCdrrule will be null
    if (CommonUtils.isNotNull(this.selectedCdrRule) &&
        (this.paramDataProvider.getParamRulesOutput().getReviewRuleMap().containsKey(this.selectedParam.getName()))) {

      List<RuleLinks> ruleLinksList =
          this.paramDataProvider.getParamRulesOutput().getReviewRuleMap().get(this.selectedParam.getName()).stream()
              .filter(rvwRule -> CommonUtils.isEqual(this.selectedCdrRule.getRuleId(), rvwRule.getRuleId()))
              .collect(Collectors.toList()).get(0).getRuleLinkWrapperData().getListOfExistingLinksForSelRule();

      for (RuleLinks ruleLink : ruleLinksList) {
        this.ruleLinksSet.add(new LinkData(ruleLink));
      }
    }

    this.linkTable.setInput(this.ruleLinksSet);
  }


  /**
   * creates the columns of link table viewer
   *
   * @param linkTbl GridTableViewer
   */
  private void createTabColumns(final GridTableViewer linkTbl) {

    GridViewerColumnUtil.getInstance().createGridViewerColumn(linkTbl, "Link", 100);
    GridViewerColumnUtil.getInstance().createGridViewerColumn(linkTbl, "Description(Eng)", 100);
    GridViewerColumnUtil.getInstance().createGridViewerColumn(linkTbl, "Description(Ger)", 100);
  }


  /**
   * Set last modified user to section, if the component for wizard.
   *
   * @param description
   */
  private void addWizardLastModifiedUserToDesc(final StringBuilder description) {
    // For 'Copy to new configuration' option, in wizard, rules list is null
    if (this.wizard.getWizardData().getCdrRulesList() != null) {
      if ((this.wizard.getWizardData().getCdrRulesList().size() > 1) &&
          !this.wizard.getWizardData().isSameModifiedUser()) {
        // If multiple users have modified the enclosed rules, show modified user as 'multiple users'
        addLastModifiedUserToDesc(description, null, true);
      }
      else {
        addLastModifiedUserToDesc(description, this.selectedCdrRule, false);
      }
    }
  }

  /**
   * Add the last modified user to given description
   *
   * @param sectDesc SB for description
   * @param rule rule
   * @param multipleUsr is multiple users
   */
  private void addLastModifiedUserToDesc(final StringBuilder sectDesc, final ReviewRule rule,
      final boolean multipleUsr) {
    if (multipleUsr) {
      sectDesc.append("Last modified by : <MULTIPLE USERS>");
    }
    else {
      List<ReviewRuleExt> ruleHistoryList = null;

      try {

        ReviewRuleParamCol paramCol = new ReviewRuleParamCol();
        paramCol.setParamCollection(this.cdrFunction);
        paramCol.setReviewRule(rule);
        ReviewRuleServiceClient serviceClient = new ReviewRuleServiceClient();
        RuleSetRuleServiceClient ruleSetRuleServiceClient = new RuleSetRuleServiceClient();
        if (this.cdrFunction instanceof Function) {
          ruleHistoryList = serviceClient.getRuleHistory(paramCol);
        }
        else if (this.cdrFunction instanceof RuleSet) {
          ruleHistoryList = ruleSetRuleServiceClient.getRuleHistory(paramCol);
        }

        // If the rule is copied from another rule as a 'new' rule, history will not be available.
        if ((ruleHistoryList != null) && !ruleHistoryList.isEmpty()) {
          String name = ruleHistoryList.get(0).getRuleCreatedUser();


          User userName = new UserServiceClient().getApicUserByUsername(name);


          if (null != userName) {
            StringBuilder userFullName = new StringBuilder();
            userFullName.append(userName.getLastName()).append(", ").append(userName.getFirstName()).append(" (")
                .append(userName.getDepartment()).append(")");
            name = userFullName.toString();
          }
          sectDesc.append("Last modified by : ").append(name);
        }

      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * // IcDm-660 This method is for the Sash Form for the Review Section and the Table Graph Area
   */
  private void createReviewSashForm() {
    this.rvwSashForm = new SashForm(this.formReviewData.getBody(), SWT.HORIZONTAL);
    this.rvwSashForm.setLayout(new GridLayout());
    this.rvwSashForm.setLayoutData(GridDataUtil.getInstance().getGridData());

  }


  /**
   * This method initializes groupThree
   *
   * @param formRvwData
   * @param layout2
   */
  private void createRuleInfoGroup() {

    final Group groupOne = new Group(this.rvwSashForm, SWT.NONE);
    // ICDM-895
    GridData gridDt = new GridData();
    groupOne.setLayoutData(gridDt);

    groupOne.setLayout(new GridLayout());
    final ScrolledComposite sc1 = new ScrolledComposite(groupOne, SWT.H_SCROLL | SWT.V_SCROLL);
    final Composite scComp = new Composite(sc1, SWT.NONE);
    scComp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    sc1.setContent(scComp);
    sc1.setExpandHorizontal(true);
    sc1.setExpandVertical(true);
    sc1.setLayout(new GridLayout());
    sc1.setLayoutData(GridDataUtil.getInstance().getGridData());
    sc1.setDragDetect(true);
    sc1.addControlListener(new ControlAdapter() {

      @Override
      public void controlResized(final ControlEvent event) {
        sc1.setMinSize(scComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
      }
    });
    final GridLayout layout = new GridLayout();
    layout.numColumns = NO_OF_COLS_GROUP_THREE;
    scComp.setLayout(layout);
    scComp.setLayoutData(GridDataUtil.getInstance().getGridData());

    // Create ui controls
    createUIControls(scComp);
  }

  /**
   * @param scComp
   */
  private void createMaturityControl(final Composite scComp) {
    createLabelControl(scComp, "Maturity Level");
    this.maturityCombo = new Combo(scComp, SWT.READ_ONLY);
    this.maturityCombo.setLayout(new GridLayout());
    this.maturityCombo.setLayoutData(new GridData(MAT_COMBO_WIDTH, MAT_COMBO_HEIGHT));
    this.maturityCombo.add(RuleMaturityLevel.NONE.getICDMMaturityLevel());
    this.maturityCombo.add(RuleMaturityLevel.START.getICDMMaturityLevel());
    this.maturityCombo.add(RuleMaturityLevel.STANDARD.getICDMMaturityLevel());
    this.maturityCombo.add(RuleMaturityLevel.FIXED.getICDMMaturityLevel());
    if (this.isWizard && !this.sameMaturityLevel) {
      this.maturityCombo.add(CommonUIConstants.DISP_TEXT_USE_CUR_VAL);
    }
    this.maturityCombo.select(0);
    setSelectedMaturityLevel(this.maturityCombo.getItem(this.maturityCombo.getSelectionIndex()));


    // Selection listener
    this.maturityCombo.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent selectionEvnt) {
        if (getSelectedParam() != null) {
          // Getting the selection index.
          final int index = RuleInfoSection.this.maturityCombo.getSelectionIndex();
          String maturityLevel = RuleInfoSection.this.maturityCombo.getItem(index);
          setSelectedMaturityLevel(maturityLevel);

          enableSave();

        }
      }
    });
  }

  /**
   * This method initializes group1
   *
   * @param layout
   */
  private void createTableGraphGroup() {

    final Group groupTwo = new Group(this.rvwSashForm, SWT.FILL);
    groupTwo.setLayout(new GridLayout());
    groupTwo.setLayoutData(GridDataUtil.getInstance().getGridData());

    // Create graph/table section
    final Section graphSection = createGraphSection(groupTwo);

    // Create graph/table composite
    final Composite graphComp = this.formToolkit.createComposite(graphSection);
    graphSection.setClient(graphComp);
    // Set layout to graph/table composite
    graphComp.setLayout(new GridLayout());
    this.calDataTableGraphComposite =
        new CalDataTableGraphComposite(graphComp, this.ruleInfoAndTblGraphScrollComp.getHorizontalBar(),
            this.ruleInfoAndTblGraphScrollComp.getVerticalBar(), CDMLogger.getInstance());

    addDropToGraphComposite(graphComp);
  }

  /**
   * @param graphComp
   */
  private void addDropToGraphComposite(final Composite graphComp) {
    final Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    final DropTarget target =
        new DropTarget(graphComp, DND.DROP_DEFAULT | DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK);
    target.setTransfer(transferTypes);
    target.addDropListener(new RuleInfoTableGraphDropListener(this));
  }


  /**
   * @param scComp
   */
  private void createUIControls(final Composite scComp) {
    createLowerLimitUIControls(scComp);
    createReviewMethodUIControls(scComp);
    createUpperLimitUIControls(scComp);
    // Icdm-513
    createUnitUIControls(scComp);
    createBitwiseRuleUIControl(scComp);
    createComplexRuleUIControl(scComp);

    createRefValUIControls(scComp);
    createMaturityControl(scComp);
    // In case of multiple rule edit, 3 empty labels are sufficient
    if (!this.isWizard || this.wizard.getWizardData().isSameExactMatchFlag()) {
      fillerLabels(scComp, 4);
    }
    else {
      fillerLabels(scComp, 3);
    }

    createRemarkUIControls(scComp);
    createUnicodeRmrkControls(scComp);

  }


  /**
   * @param scComp
   */
  private void createUnicodeRmrkControls(final Composite scComp) {

    this.unicodeRmrkTxtArea = new Text(scComp, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
    final GridData createGridData =
        GridDataUtil.getInstance().createGridData(TEXT_AREA_WIDTHHINT, TEXT_AREA_HEIGHTHINT);
    createGridData.verticalAlignment = GridData.FILL;
    createGridData.horizontalSpan = REM_UI_SPAN;
    this.unicodeRmrkTxtArea.setLayoutData(createGridData);
    this.unicodeRmrkTxtArea.addModifyListener(new EditRuleHintModListener(this));
  }


  /**
   * @param scComp
   */
  private void createBitwiseRuleUIControl(final Composite scComp) {

    this.bitWiseConfigDialog =
        new BitWiseLimitConfigDialog(RuleInfoSection.this.editRuleDialog, RuleInfoSection.this.wizardPage);
    if ((this.selectedCdrRule == null) || !((this.selectedCdrRule.getBitWiseRule() != null) &&
        "COMPLEX RULE!".equals(this.selectedCdrRule.getBitWiseRule()))) {

      this.bitWiseConfigDialog.setInput();
    }
    createLabelControl(scComp, "Bitwise Rule:");
    this.bitwiseRuleTxt = createStyledTextField(scComp, TEXT_FIELD_WIDTHHINT_1, true);
    this.bitwiseRuleTxt.setEditable(false);
    this.bitwiseRuleTxt.addModifyListener(event -> enableSave());
    addMouseListener();
  }

  /**
   * @param scComp
   */
  private void createComplexRuleUIControl(final Composite scComp) {
    fillerLabels(scComp, 2);
    createLabelControl(scComp, "Complex Rule:");
    if (this.selectedCdrRule != null) {
      this.advancedFormula = this.selectedCdrRule.getFormulaDesc();
    }
    this.complexRuleBtn = new Button(scComp, SWT.PUSH);
    this.complexRuleBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.TICK_16X16));
    this.complexRuleBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        final ComplexRuleEditDialog remarksDialog =
            new ComplexRuleEditDialog(Display.getCurrent().getActiveShell(), RuleInfoSection.this);
        remarksDialog.open();
      }
    });
  }


  /**
   * Adds mouse listener to bitwise UI text feild
   */
  private void addMouseListener() {
    this.bitwiseRuleTxt.addMouseListener(new MouseListener() {

      @Override
      public void mouseUp(final MouseEvent arg0) {
        // Not applicable
      }

      @Override
      public void mouseDown(final MouseEvent mouseEvent) {
        if ((RuleInfoSection.this.bitWiseConfigDialog != null) && checkbitWiseConfigDialogShell()) {
          return;
        }
        if (mouseEvent.button == LEFT_MOUSE_CLICK) {
          RuleInfoSection.this.bitWiseConfigDialog.open();

        }
      }


      @Override
      public void mouseDoubleClick(final MouseEvent arg0) {
        // Not applicable
      }
    });
  }

  private boolean checkbitWiseConfigDialogShell() {
    return (RuleInfoSection.this.bitWiseConfigDialog.getShell() != null) &&
        !RuleInfoSection.this.bitWiseConfigDialog.getShell().isDisposed() &&
        !RuleInfoSection.this.bitWiseConfigDialog.close();
  }

  /**
   * @param scComp
   */
  private void createReviewMethodUIControls(final Composite scComp) {
    fillerLabels(scComp, 2);
    String labelName;
    try {
      labelName = new CommonDataBO().getMessage("CDR_RULE", "READY_FOR_SERIES");
      createLabelControl(scComp, labelName);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }


    final Composite buttGroup = new Composite(scComp, SWT.NONE);
    final GridLayout layout = new GridLayout();
    buttGroup.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    this.readySeriesButton = new Button(buttGroup, SWT.CHECK);
    this.readySeriesButton.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    final boolean initialSel = this.readySeriesButton.getSelection();

    if (this.isWizard && !this.wizard.getWizardData().isSameReadyForSeries()) {
      // if the reveiw methods are different , create a new radio button
      fillerLabels(buttGroup, 4);
      this.useCurrRvwMtd = new Button(buttGroup, SWT.CHECK);
      this.useCurrRvwMtd.setText(CommonUIConstants.DISP_TEXT_USE_CUR_VAL);
      this.useCurrRvwMtd.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
      layout.numColumns = CommonUIConstants.COLUMN_INDEX_7;
      buttGroup.setLayout(layout);
      this.useCurrRvwMtd.addSelectionListener(new SelectionAdapter() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void widgetSelected(final SelectionEvent event) {
          if ((RuleInfoSection.this.readySeriesButton.getSelection() != initialSel) &&
              RuleInfoSection.this.useCurrRvwMtd.getSelection()) {
            RuleInfoSection.this.readySeriesButton.setSelection(initialSel);
          }

          enableSave();
        }
      });
    }
    else {
      layout.numColumns = 2;
      buttGroup.setLayout(layout);
    }

    this.readySeriesButton.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        if (RuleInfoSection.this.useCurrRvwMtd != null) {
          RuleInfoSection.this.useCurrRvwMtd.setSelection(false);
        }
        enableSave();
      }

    });


  }


  /**
   * @param parent lable
   * @param style style
   * @return the label of given style
   */
  private Label getNewLabel(final Composite parent, final int style) {
    return new Label(parent, style);
  }

  /**
   * Enables save/finish button after verifying the input fields. Validates the fields required. For update mode, change
   * in value is also verified
   */
  public void enableSave() {
    // Save is enabled if validation is success, and any of the field is modified
    boolean enableSave = isFieldValuesValid() && (isRuleUpdated() || this.paramEditSection.isParamPropertiesUpdated());// ICDM-1244
    if (!this.isWizard && this.readOnlyMode && CommonUtils.isNotNull(getSaveBtn())) {
      getSaveBtn().setEnabled(false);
    }
    else {
      if (this.isWizard) {
        this.wizardPage.setPageComplete(enableSave);
        this.wizard.getContainer().updateButtons();
      }
      else if (CommonUtils.isNotNull(getSaveBtn())) {
        getSaveBtn().setEnabled(enableSave);
      }
    }


  }

  /**
   * Checks for number formatting, mandatory entry etc. in the fields
   *
   * @return true if all fields have valid values.
   */
  private boolean isFieldValuesValid() {

    boolean validationPassed = true;

    // Validate input fields
    // For validation using Validator, the validator should be invoked, even if text is empty

    // Lower limit number formatting, using validator.
    boolean validatorResult =
        Validator.getInstance().validateNDecorate(this.lowerLimitDecor, this.lowerLimitTxt, null, false, true, ",");
    // ICDM-1515 format is not empty string to accept ',' as decimal separator
    if (CommonUtils.isEqual(this.lowerLimitTxt.getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      Decorators decorators = new Decorators();
      decorators.showErrDecoration(this.lowerLimitDecor, IUtilityConstants.EMPTY_STRING, false);
    }
    if (!CommonUtils.isEmptyString(this.lowerLimitTxt.getText()) &&
        !CommonUtils.isEqual(this.lowerLimitTxt.getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      validationPassed &= validatorResult;
    }

    // Upper limit number formatting, using validator.
    validatorResult =
        Validator.getInstance().validateNDecorate(this.upperLimitDecor, this.upperLimitTxt, null, false, true, ",");
    // ICDM-1515 format is not empty string to accept ',' as decimal separator
    if (CommonUtils.isEqual(this.upperLimitTxt.getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      Decorators decorators = new Decorators();
      decorators.showErrDecoration(this.upperLimitDecor, IUtilityConstants.EMPTY_STRING, false);
    }
    if (!CommonUtils.isEmptyString(this.upperLimitTxt.getText()) &&
        !CommonUtils.isEqual(this.upperLimitTxt.getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      validationPassed &= validatorResult;
    }


    // If exact match is set, ref value should not be empty and lower/upper should be empty
    if (this.btnParamRefValMatch.getSelection()) {
      validationPassed &= validateForExactMatchSelection();

    }

    return validationPassed;

  }

  /**
   * @return true if the ref value is not be empty and lower/upper limits are empty
   */
  private boolean validateForExactMatchSelection() {
    return (!CommonUtils.isEmptyString(this.refValueTxt.getText()) ||
        CommonUtils.isEqual(this.refValueTxt.getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) &&
        CommonUtils.isEmptyString(this.lowerLimitTxt.getText()) &&
        CommonUtils.isEmptyString(this.upperLimitTxt.getText()) &&
        CommonUtils.isEmptyString(this.bitwiseRuleTxt.getText());
  }

  /**
   * Checks whether the rule information in the fields is updated. For create mode, this is always true. In update mode,
   * this is true if atleast one of the value is updated. The comparision is done between the UI fields and the rule
   * object being updated.
   *
   * @return true if rule is updated
   */
  private boolean isRuleUpdated() {

    boolean lowLimitModified = true;
    boolean upperLimitModified = true;
    boolean bitWiseRuleModified = true;
    boolean refValueUpdated = true;
    boolean exactMatchUpdated;
    boolean reviewTypeUpdated = true;
    boolean unitUpdated = true;
    boolean maturityUpdated = true;
    boolean remarksUpdated = true;
    boolean unicodeRemarksUpdated = true;
    boolean complexRulesModified = true;
    boolean ruleLinksUpdated = true;

    // Verify whether there are changes between rule being modified, applicable ony for update mode
    if (isUpdate()) {

      // Verify LowerLimit
      lowLimitModified = verifyLowerLimitModifiedForUpdate(lowLimitModified);

      // Verify Upper Limit
      upperLimitModified = verifyUpperLimitModifiedForUpdate(upperLimitModified);

      bitWiseRuleModified = verifyBitwiseModifiedForUpdate(bitWiseRuleModified);

      // Verify Complex Rule
      complexRulesModified = verifyComplexRulesModifiedForUpdate(complexRulesModified);

      refValueUpdated = verifyRefValueModifiedForUpdate(refValueUpdated);


      // Verify Exact Match flag
      exactMatchUpdated = verifyExactMatchModifiedForUpdate();

      // Verify Review Type
      reviewTypeUpdated = verifyReviewTypeModifiedForUpdate(reviewTypeUpdated);

      // Verify Unit
      int index;

      String refUnit = CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
      if (!this.isWizard || (this.wizard.getWizardData().isSameUnit())) {
        refUnit = getSelectedCdrRule().getUnit();

      }
      // ICDM-1922
      unitUpdated ^= CommonUtils.isEqual(this.unitText.getText(), CommonUtils.checkNull(refUnit));// ICDM-1052

      // Verify Maturity Level changed
      index = this.maturityCombo.getSelectionIndex();
      String uiMaturity = this.maturityCombo.getItem(index);
      String refMaturity = CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
      if (this.sameMaturityLevel) {
        refMaturity = RuleMaturityLevel.getIcdmMaturityLevelText(getSelectedCdrRule().getMaturityLevel());
      }
      maturityUpdated ^= CommonUtils.isEqual(uiMaturity, refMaturity);

      // Verify remark changed
      remarksUpdated = verifyRemarksModifiedForUpdate(remarksUpdated);

      unicodeRemarksUpdated ^= CommonUtils.isEqual(getUnicodeRmrkTxtArea().getText(),
          CommonUtils.checkNull(getSelectedCdrRule().getUnicodeRemarks()));

      // Verify Links Updated
      ruleLinksUpdated = verifyLinksModifiedForUpdate();
    }
    // ICDM-1922 for create rule the below feilds are checked to see whether anyone of the field is updated
    else {
      lowLimitModified ^= verifyLowLimitVerified();
      upperLimitModified ^= verifyUpperLimitModified();
      bitWiseRuleModified ^= verifyBitWiseRuleModified();
      complexRulesModified ^= verifyComplexRuleModified();
      refValueUpdated ^= verifyRefValueUpdated();
      exactMatchUpdated = this.btnParamRefValMatch.getSelection();
      unitUpdated ^= (!CommonUtils.isEqual(this.unitText.getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL) &&
          CommonUtils.isEqual(this.unitText.getText(), ""));
      String uiMaturity = this.maturityCombo.getItem(this.maturityCombo.getSelectionIndex());
      maturityUpdated ^= (!CommonUtils.isEqual(uiMaturity, CommonUIConstants.DISP_TEXT_USE_CUR_VAL) &&
          CommonUtils.isEqual(uiMaturity, "none (0%)"));
      remarksUpdated ^= (!CommonUtils.isEqual(getHintTxtArea().getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL) &&
          CommonUtils.isEqual(getHintTxtArea().getText(), ""));

      unicodeRemarksUpdated ^=
          (!CommonUtils.isEqual(getUnicodeRmrkTxtArea().getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL) &&
              CommonUtils.isEqual(getUnicodeRmrkTxtArea().getText(), ""));

      ruleLinksUpdated ^= CommonUtils.isNullOrEmpty((SortedSet<LinkData>) this.linkTable.getInput());
    }
    return lowLimitModified || upperLimitModified || bitWiseRuleModified || refValueUpdated || exactMatchUpdated ||
        reviewTypeUpdated || unitUpdated || maturityUpdated || remarksUpdated || unicodeRemarksUpdated ||
        complexRulesModified || ruleLinksUpdated;
  }


  /**
   * @param ruleLinksUpdated
   * @return
   */
  private boolean verifyLinksModifiedForUpdate() {

    Set<LinkData> ruleLinksInTbl = (SortedSet<LinkData>) this.linkTable.getInput();

    return CommonUtils.isNotEmpty(ruleLinksInTbl) &&
        (isNewLinksAdded(ruleLinksInTbl) || isRuleUpdateOrDeleted(ruleLinksInTbl));
  }


  /**
   * @param ruleLinksInTbl
   * @return
   */
  private boolean isRuleUpdateOrDeleted(final Set<LinkData> ruleLinksInTbl) {
    return (isExistingLinkDeleted(ruleLinksInTbl) || isLinksGotUpdated(ruleLinksInTbl));
  }


  /**
   * @param ruleLinksInTbl
   * @return
   */
  private boolean isLinksGotUpdated(final Set<LinkData> ruleLinksInTbl) {

    return CommonUtils.isNotEmpty(ruleLinksInTbl.stream()
        .filter(linkData -> CommonUtils.isEqual(linkData.getOprType(), CommonUIConstants.CHAR_CONSTANT_FOR_EDIT))
        .collect(Collectors.toList()));
  }

  /**
   * @param ruleLinksInTbl
   * @param existingRuleLink
   * @return
   */
  private boolean isNewLinksAdded(final Set<LinkData> ruleLinksInTbl) {
    return CommonUtils.isNotEmpty(ruleLinksInTbl.stream()
        .filter(linkData -> CommonUtils.isNull(linkData.getRuleLinkObj()) &&
            CommonUtils.isEqual(linkData.getOprType(), CommonUIConstants.CHAR_CONSTANT_FOR_ADD))
        .collect(Collectors.toList()));
  }


  /**
   * @param ruleLinksInTbl
   * @return
   */
  private boolean isExistingLinkDeleted(final Set<LinkData> ruleLinksInTbl) {
    return CommonUtils.isNotEmpty(ruleLinksInTbl.stream()
        .filter(linkData -> CommonUtils.isNotNull(linkData.getRuleLinkObj()) &&
            CommonUtils.isEqual(linkData.getOprType(), CommonUIConstants.CHAR_CONSTANT_FOR_DELETE))
        .collect(Collectors.toList()));
  }


  /**
   * @return
   */
  private boolean verifyExactMatchModifiedForUpdate() {
    boolean exactMatchUpdated;
    if (!this.isWizard || (this.wizard.getWizardData().isSameExactMatchFlag())) {
      exactMatchUpdated = this.btnParamRefValMatch.getSelection() != getSelectedCdrRule().isDcm2ssd();
    }
    else {
      // ICDM-1182
      exactMatchUpdated = this.btnChooseExactMatch.getSelection();
    }
    return exactMatchUpdated;
  }


  /**
   * @param remarksUpdated
   * @return
   */
  private boolean verifyRemarksModifiedForUpdate(final boolean remarksUpdated) {
    boolean remarksUpdatedLocal = remarksUpdated;
    String refRemark = CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
    if (this.sameHint) {
      refRemark = CommonUtils.checkNull(getSelectedCdrRule().getHint());
    }
    remarksUpdatedLocal ^= CommonUtils.isEqual(getHintTxtArea().getText(), refRemark);
    return remarksUpdatedLocal;
  }


  /**
   * @param reviewTypeUpdated
   * @return
   */
  private boolean verifyReviewTypeModifiedForUpdate(final boolean reviewTypeUpdated) {
    boolean reviewTypeUpdatedLocal = reviewTypeUpdated;
    if (!this.isWizard || (this.wizard.getWizardData().isSameReadyForSeries())) {
      ApicConstants.READY_FOR_SERIES iCDMReviewType;
      if (this.readySeriesButton.getSelection()) {
        iCDMReviewType = ApicConstants.READY_FOR_SERIES.YES;
      }
      else {
        iCDMReviewType = ApicConstants.READY_FOR_SERIES.NO;
      }
      reviewTypeUpdatedLocal ^= CommonUtils.isEqual(iCDMReviewType.getDbType(),
          CommonUtils.checkNull(getSelectedCdrRule().getReviewMethod()));
    }
    else {
      // ICDM-1182
      reviewTypeUpdatedLocal ^= this.useCurrRvwMtd.getSelection();
    }
    return reviewTypeUpdatedLocal;
  }


  /**
   * @param refValueUpdated
   * @return
   */
  private boolean verifyRefValueModifiedForUpdate(final boolean refValueUpdated) {
    boolean refValueUpdatedLocal = refValueUpdated;
    if (!this.isWizard || (this.wizard.getWizardData().isSameRefVal())) {
      // do this checking only if reference values are same or if this is single update
      // Verify Ref value
      if (isValueTypeParam()) {
        refValueUpdatedLocal ^=
            CommonUtils.isEqual(getSelectedCdrRule().getRefValueDispString(), this.refValueTxt.getText());
      }
      else {
        // Use CalDataPhy equals method for complex data type
        refValueUpdatedLocal ^= CommonUtils.isEqualCalDataPhy(this.refValCalDataObj,
            CalDataUtil.getCDPObj(getSelectedCdrRule().getRefValueCalData()));
      }
    }
    else {
      // this checking is for different reference values
      refValueUpdatedLocal ^= CommonUtils.isEqual(CommonUIConstants.DISP_TEXT_USE_CUR_VAL, this.refValueTxt.getText());
    }
    return refValueUpdatedLocal;
  }


  /**
   * @param complexRulesModified
   * @return
   */
  private boolean verifyComplexRulesModifiedForUpdate(final boolean complexRulesModified) {
    boolean complexRulesModifiedLocal = complexRulesModified;
    String advFormula = CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
    if (!this.isWizard) {
      advFormula = getSelectedCdrRule().getFormulaDesc() == null ? "" : getSelectedCdrRule().getFormulaDesc();
    }
    if (this.advancedFormula == null) {
      complexRulesModifiedLocal = false;
    }
    else {
      complexRulesModifiedLocal ^= CommonUtils.isEqual(this.advancedFormula, advFormula);
    }
    return complexRulesModifiedLocal;
  }


  /**
   * @param bitWiseRuleModified
   * @return
   */
  private boolean verifyBitwiseModifiedForUpdate(final boolean bitWiseRuleModified) {
    boolean bitWiseRuleModifiedLocal = bitWiseRuleModified;
    // Verify BitWise Limit
    String bitWise = CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
    if (!this.isWizard || (this.wizard.getWizardData().isSameBitWise())) {
      // ICDM-1922
      bitWise = getSelectedCdrRule().getBitWiseRule() == null ? "" : getSelectedCdrRule().getBitWiseRule();
    }

    bitWiseRuleModifiedLocal ^= CommonUtils.isEqual(this.bitwiseRuleTxt.getText(), bitWise);
    return bitWiseRuleModifiedLocal;
  }


  /**
   * @param upperLimitModified
   * @return
   */
  private boolean verifyUpperLimitModifiedForUpdate(final boolean upperLimitModified) {
    boolean upperLimitModifiedLocal = upperLimitModified;
    String uppLim = CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
    if (!this.isWizard || (this.wizard.getWizardData().isSameUpperLmt())) {
      uppLim = getSelectedCdrRule().getUpperLimit() == null ? "" : getSelectedCdrRule().getUpperLimit().toString();
    }
    upperLimitModifiedLocal ^= CommonUtils.isEqual(this.upperLimitTxt.getText(), uppLim);
    return upperLimitModifiedLocal;
  }


  /**
   * @param lowLimitModified
   * @return
   */
  private boolean verifyLowerLimitModifiedForUpdate(final boolean lowLimitModified) {
    boolean lowLimitModifiedLocal = lowLimitModified;
    String lowLim = CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
    if (!this.isWizard || (this.wizard.getWizardData().isSameLowerLmt())) {
      lowLim = getSelectedCdrRule().getLowerLimit() == null ? "" : getSelectedCdrRule().getLowerLimit().toString();
    }
    lowLimitModifiedLocal ^= CommonUtils.isEqual(this.lowerLimitTxt.getText(), lowLim);
    return lowLimitModifiedLocal;
  }


  /**
   * @return
   */
  private boolean verifyRefValueUpdated() {
    return !CommonUtils.isEqual(this.refValueTxt.getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL) &&
        CommonUtils.isEqual(this.refValueTxt.getText(), "");
  }


  /**
   * @return
   */
  private boolean verifyBitWiseRuleModified() {
    return !CommonUtils.isEqual(this.bitwiseRuleTxt.getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL) &&
        CommonUtils.isEqual(this.bitwiseRuleTxt.getText(), "");
  }

  /**
   * @return
   */
  private boolean verifyComplexRuleModified() {
    return !CommonUtils.isEqual(this.advancedFormula, CommonUIConstants.DISP_TEXT_USE_CUR_VAL) &&
        CommonUtils.isEqual(this.advancedFormula, "");
  }


  /**
   * @return
   */
  private boolean verifyUpperLimitModified() {
    return !CommonUtils.isEqual(this.upperLimitTxt.getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL) &&
        CommonUtils.isEqual(this.upperLimitTxt.getText(), "");
  }


  /**
   * @return
   */
  private boolean verifyLowLimitVerified() {
    return !CommonUtils.isEqual(this.lowerLimitTxt.getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL) &&
        CommonUtils.isEqual(this.lowerLimitTxt.getText(), "");
  }

  /**
   * @return true if the parameter is of value type
   */
  private boolean isValueTypeParam() {
    return CommonUtils.isEqual(getSelectedParam().getType(), ParameterType.VALUE.getText());
  }

  /**
   * This method creates different parameter UI controls
   *
   * @param caldataComp
   */
  private void createLowerLimitUIControls(final Composite comp) {
    createLabelControl(comp, "Lower Limit");
    this.lowerLimitTxt = createStyledTextField(comp, TEXT_FIELD_WIDTHHINT_1, true);
    this.lowerLimitTxt.addModifyListener(event -> enableSave());
    this.lowerLimitDecor = new ControlDecoration(this.lowerLimitTxt, SWT.LEFT | SWT.TOP);
  }

  /**
   * This method creates different parameter UI controls
   *
   * @param caldataComp
   */
  private void createUpperLimitUIControls(final Composite comp) {
    createLabelControl(comp, "Upper Limit");
    this.upperLimitTxt = createStyledTextField(comp, TEXT_FIELD_WIDTHHINT_1, true);
    this.upperLimitTxt.addModifyListener(event -> enableSave());
    this.upperLimitDecor = new ControlDecoration(this.upperLimitTxt, SWT.LEFT | SWT.TOP);
  }


  /**
   * @param scComp //Icdm-513
   */
  private void createUnitUIControls(final Composite scComp) {
    fillerLabels(scComp, 2);
    createLabelControl(scComp, "Unit");
    Composite compUnit = new Composite(scComp, SWT.NONE);
    compUnit.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = NO_OF_COLS_FOR_UNIT;
    compUnit.setLayout(gridLayout);
    compUnit.setLayoutData(new GridData());
    this.unitText = createStyledTextField(compUnit, TEXT_FIELD_WIDTHHINT_1, true);
    this.unitText.addModifyListener(event -> {
      this.selectedUnit = this.unitText.getText().trim();
      enableSave();
    });
    setContentPropsalForUnitField();
  }

  /**
   * Set Content Proposal for Unit Text Field
   */
  private void setContentPropsalForUnitField() {
    Set<Unit> unitSet = null;
    // Fetch the units available in icdm
    try {
      unitSet = new UnitServiceClient().getAll();
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), Activator.PLUGIN_ID);
    }
    if (unitSet != null) {
      this.units.addAll(unitSet.stream().map(Unit::getUnitName).collect(Collectors.toSet()));
    }
    IContentProposalProvider provider =
        new ComboViewerContentPropsalProvider(this.units.toArray(new String[this.units.size()]));
    ContentProposalAdapter adapter =
        new ContentProposalAdapter(this.unitText, new TextContentAdapter(), provider, null, null);
    adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
    adapter.setProposalPopupFocus();
    adapter.setPopupSize(new Point(200, 200));
    adapter.addContentProposalListener(arg0 -> {
      this.unitText.setText(arg0.getContent());
      adapter.getControlContentAdapter().setCursorPosition(this.unitText, SWT.END);
    });
  }


  /**
   * @param scComp
   */
  private void createRemarkUIControls(final Composite scComp) {


    // Icdm-621
    createLabelControl(scComp, "Remarks :");
    fillerLabels(scComp, 3);
    createLabelControl(scComp, "Remarks (Unicode) :");
    // ICDM-759
    this.hintTxtArea = new Text(scComp, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
    final GridData createGridData =
        GridDataUtil.getInstance().createGridData(TEXT_AREA_WIDTHHINT, TEXT_AREA_HEIGHTHINT);
    createGridData.verticalAlignment = GridData.FILL;
    createGridData.horizontalSpan = REM_UI_SPAN;
    this.hintTxtArea.setLayoutData(createGridData);
    this.hintTxtArea.addModifyListener(new EditRuleHintModListener(this));
  }

  /**
   * ICDM-1182 add focus listener to text fields
   *
   * @param nameTxt2 StyledText
   */
  private void addFocusListenerForRemarks(final Text nameTxt2) {
    nameTxt2.addFocusListener(new FocusListener() {

      @Override
      public void focusLost(final FocusEvent arg0) {
        if (RuleInfoSection.this.isWizard && CommonUtils.isEmptyString(nameTxt2.getText()) &&
            checkSetCurrValText(nameTxt2)) {
          nameTxt2.setText(CommonUIConstants.DISP_TEXT_USE_CUR_VAL);

        }
      }

      @Override
      public void focusGained(final FocusEvent arg0) {
        if (RuleInfoSection.this.isWizard && nameTxt2.getText().equals(CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
          nameTxt2.setText("");
        }

      }
    });
  }

  private boolean checkSetCurrValText(final Text nameTxt2) {
    return (CommonUtils.isEqual(RuleInfoSection.this.hintTxtArea, nameTxt2) && !RuleInfoSection.this.sameHint) ||
        canSetCurrValTextForLowLmt(nameTxt2) || canSetCurrValTextForUpLmt(nameTxt2) ||
        canSetCurrValTextForRefLmt(nameTxt2);
  }

  /**
   * @param nameTxt StyledText
   * @return true if we can set "<Use Current Value>" for reference value text
   */
  private boolean canSetCurrValTextForRefLmt(final Text nameTxt) {
    return CommonUtils.isEqual(RuleInfoSection.this.refValueTxt, nameTxt) &&
        !RuleInfoSection.this.wizard.getWizardData().isSameRefVal();
  }

  /**
   * @param nameTxt StyledText
   * @return true if we can set "<Use Current Value>" for upper limit text
   */
  private boolean canSetCurrValTextForUpLmt(final Text nameTxt) {
    return CommonUtils.isEqual(RuleInfoSection.this.upperLimitTxt, nameTxt) &&
        !RuleInfoSection.this.wizard.getWizardData().isSameUpperLmt() &&
        !RuleInfoSection.this.btnParamRefValMatch.getSelection();
  }

  /**
   * @param nameTxt StyledText
   * @return true if we can set "<Use Current Value>" for lower limit text
   */
  private boolean canSetCurrValTextForLowLmt(final Text nameTxt) {
    return CommonUtils.isEqual(RuleInfoSection.this.lowerLimitTxt, nameTxt) &&
        !RuleInfoSection.this.wizard.getWizardData().isSameLowerLmt() &&
        !RuleInfoSection.this.btnParamRefValMatch.getSelection();
  }


  /**
   * @param comp
   */
  private void createRefValUIControls(final Composite comp) {
    createLabelControl(comp, "Reference Value");
    this.refValueTxt = createStyledTextField(comp, TEXT_FIELD_WIDTHHINT_1, true);
    this.refValueTxt.setFocus();
    this.refValueTxt.setEditable(false);
    if (this.selectedParam.getType().equals(ParameterType.VALUE.getText())) {
      this.refValueTxt.setEditable(true);
    }
    this.refValueTxt.addModifyListener(event -> {

      if (RuleInfoSection.this.isRefValTyped && (RuleInfoSection.this.selectedParam != null) &&
          RuleInfoSection.this.selectedParam.getType().equals(ParameterType.VALUE.getText())) {
        // convert the value to caldata object , so that graph can be shown
        // this is applicable only in case the value is typed
        RuleInfoSection.this.refValCalDataObj = getRefValueOnModification();
        if ((RuleInfoSection.this.refCalAttr != null) && (RuleInfoSection.this.refValCalDataObj != null) &&
            !RuleInfoSection.this.refCalAttr.getCalData().equals(RuleInfoSection.this.refValCalDataObj)) {
          RuleInfoSection.this.refCalAttr.setCalData(RuleInfoSection.this.refValCalDataObj);
          displayTableGraph(RuleInfoSection.this.refValueTxt, RuleInfoSection.this.refValCalDataObj, true);
        }
      }
      onRefValTextModification();

    });

    Composite compRefVal = new Composite(comp, SWT.NONE);
    compRefVal.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = NO_OF_COLS_FOR_UNIT;
    compRefVal.setLayout(gridLayout);
    compRefVal.setLayoutData(new GridData());
    createFileImportButton(compRefVal);
    createClearButton(compRefVal);
    fillerLabels(comp, 1);
    createLabelControl(comp, CDRConstants.EXACT_MATCH_TO_REFERENCE_VALUE);
    createRefValChkBox(comp);
  }

  /**
   * This method create Label instance for statisctical values
   *
   * @param caldataComp
   * @param lblName
   */
  private void createLabelControl(final Composite composite1, final String lblName) {
    LabelUtil.getInstance().createLabel(this.formToolkit, composite1, lblName);
  }

  /**
   * @param comp
   * @param widthHint
   * @param isEditable
   * @return
   */
  private Text createStyledTextField(final Composite comp, final int widthHint, final boolean isEditable) {
    Text styledTxt = new Text(comp, SWT.SINGLE | SWT.BORDER);
    styledTxt.setLayoutData(GridDataUtil.getInstance().getWidthHintGridData(widthHint));
    styledTxt.setEditable(isEditable);
    return styledTxt;
  }

  /**
   * @param comp
   */
  private void createFileImportButton(final Composite comp) {
    // This button is to select a DCM file in reference value field
    this.btnDCMfile = new Button(comp, SWT.PUSH);
    this.btnDCMfile.setToolTipText("Import from file");
    this.btnDCMfile.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.DCM_UPLOAD_28X30));
    this.btnDCMfile.addSelectionListener(new RuleInfoImportFileBtnSelListener(this));
    final Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    dropToRefValue(transferTypes);
    RefValueDragListener paramActionSet = new RefValueDragListener(this.paramDataProvider, this.paramCollDataProvider);
    paramActionSet.dragRefValue(transferTypes, this.refValueTxt, "Reference Value", this);

  }

  /**
   * @param transferTypes
   * @return
   */
  private Transfer[] dropToRefValue(final Transfer[] transferTypes) {

    final DropTarget target =
        new DropTarget(this.refValueTxt, DND.DROP_DEFAULT | DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK);
    target.setTransfer(transferTypes);
    target.addDropListener(new RuleInfoRefValueDropListener(this));
    return transferTypes;
  }


  /**
   *
   */
  public void onRefValTextModification() {

    if ("".equals(this.refValueTxt.getText())) {
      this.btnParamRefValMatch.setEnabled(false);
      this.btnParamRefValMatch.setSelection(false);
      setLimitsToNonEditable();
      this.btnClearRefVal.setEnabled(false);
    }
    else {
      this.btnParamRefValMatch.setEnabled(true);
      this.btnClearRefVal.setEnabled(true);
    }

    enableSave();

    this.refValueTxt.setFocus();
  }


  /**
   * ICDM-1253
   *
   * @return CalData from reference value
   */
  private CalData getRefValueOnModification() {
    try {
      Double.parseDouble(this.refValueTxt.getText());
      return CalDataUtil.dcmToCalData(CalDataUtil.createDCMStringForNumber(this.selectedParam.getName(),
          this.selectedUnit, this.refValueTxt.getText()), this.selectedParam.getName(), CDMLogger.getInstance());
    }
    catch (NumberFormatException formatException) {
      return CalDataUtil.dcmToCalData(CalDataUtil.createDCMStringForText(this.selectedParam.getName(),
          this.selectedUnit, this.refValueTxt.getText()), this.selectedParam.getName(), CDMLogger.getInstance());
    }
  }

  /**
   * @param comp
   */
  private void createClearButton(final Composite comp) {
    // This button is to clear the refvalue
    this.btnClearRefVal = new Button(comp, SWT.PUSH);
    this.btnClearRefVal.setToolTipText("Clear Reference Value");
    this.btnClearRefVal.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.DELETE_16X16));
    this.btnClearRefVal.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        RuleInfoSection.this.refValueTxt.setText("");
        onRefValTextModification();
        setRefValCalDataObj(null);
        setLimitsToNonEditable();
        enableSave();

      }
    });
  }

  /**
   * @param comp
   */
  private void createRefValChkBox(final Composite comp) {
    if (!this.isWizard || this.wizard.getWizardData().isSameExactMatchFlag()) {
      final Composite buttGroup = new Composite(comp, SWT.NONE);
      final GridLayout layout = new GridLayout();
      buttGroup.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
      buttGroup.setLayout(layout);
      createExactMatchChkBox(buttGroup);
    }
    else {
      // ICDM-1182 if the exact match to reference value flags are different
      Group exactMatchGrp = new Group(comp, SWT.NONE);
      GridLayout layout = new GridLayout();
      layout.numColumns = COLUMN_COUNT_2;
      GridData gridData = new GridData();
      gridData.verticalSpan = COLUMN_COUNT_2;
      exactMatchGrp.setLayout(layout);
      exactMatchGrp.setLayoutData(gridData);
      exactMatchGrp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

      createExactMatchChkBox(exactMatchGrp);
      this.btnParamRefValMatch.setEnabled(false);
      LabelUtil.getInstance().createEmptyLabel(exactMatchGrp);
      this.btnUseCurrExactMatch = new Button(exactMatchGrp, SWT.RADIO);
      this.btnUseCurrExactMatch.setText(CommonUIConstants.DISP_TEXT_USE_CUR_VAL);
      this.btnUseCurrExactMatch.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
      this.btnUseCurrExactMatch.addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent arg0) {
          RuleInfoSection.this.btnParamRefValMatch.setEnabled(false);
          enableSave();
          setLimitsToNonEditable();
        }


      });
      this.btnUseCurrExactMatch.setSelection(true);
      this.btnChooseExactMatch = new Button(exactMatchGrp, SWT.RADIO);
      this.btnChooseExactMatch.setText("Change");
      this.btnChooseExactMatch.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
      this.btnChooseExactMatch.addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent arg0) {
          RuleInfoSection.this.btnParamRefValMatch.setEnabled(true);
          enableSave();
          setLimitsToNonEditable();
        }


      });
    }

  }

  /**
   * @param comp Composite
   */
  private void createExactMatchChkBox(final Composite comp) {
    this.btnParamRefValMatch = new Button(comp, SWT.CHECK);
    this.btnParamRefValMatch.setToolTipText(CDRConstants.EXACT_MATCH_TO_REFERENCE_VALUE);
    this.btnParamRefValMatch.addSelectionListener(new EditRuleRefMatchSelListener(this));
  }

  /**
   * @param grp
   * @param limit
   */
  private void fillerLabels(final Composite grp, final int limit) {
    for (int i = 0; i < limit; i++) {
      getNewLabel(grp, SWT.NONE);
    }
  }

  /**
   * Icdm-1054 - table Graph This method creates Table/Graph UI form section
   *
   * @param group
   * @return Section
   */
  private Section createGraphSection(final Group group) {
    // Creates Table/Graph Section
    return SectionUtil.getInstance().createSection(group, this.formToolkit, GridDataUtil.getInstance().getGridData(),
        "Table/Graph", false);
  }

  /**
   * @return the btnParamRefValMatch
   */
  public Button getBtnParamRefValMatch() {
    return this.btnParamRefValMatch;
  }

  /**
   * @return the selectedParam
   */
  public IParameter getSelectedParam() {
    return this.selectedParam;
  }

  /**
   * When bit wise checkbox is modified, enable and disable bitwise or complex rules accordingly
   */
  public void toggleComplexRulesForBitwiseParam() {
    this.oldBitWiseLimit = "";
    String currentAdvFormula = this.advancedFormula;
    this.advancedFormula = this.oldAdvancedFormula;
    this.oldAdvancedFormula = currentAdvFormula;
    String currentBitWiseRule = this.bitwiseRuleTxt == null ? "" : this.bitwiseRuleTxt.getText();
    if (this.bitwiseRuleTxt != null) {
      this.bitwiseRuleTxt.setText(this.oldBitWiseLimit);
    }
    this.oldBitWiseLimit = currentBitWiseRule;
    this.bitWiseConfigDialog.resetInput();
  }

  /**
   * Made the Method as public
   */
  public void setLimitsToNonEditable() {
    if (checkParameterType()) {
      if (checkExactMatch()) {
        setOldLowerLimit();
        RuleInfoSection.this.lowerLimitTxt.setText("");
        RuleInfoSection.this.lowerLimitTxt.setEditable(false);
        setOldUpperLimit();
        RuleInfoSection.this.upperLimitTxt.setText("");
        RuleInfoSection.this.upperLimitTxt.setEditable(false);
        if (!RuleInfoSection.this.bitwiseRuleTxt.getText().isEmpty()) {
          this.oldBitWiseLimit = RuleInfoSection.this.bitwiseRuleTxt.getText().trim();
        }
        RuleInfoSection.this.bitwiseRuleTxt.setEnabled(false);
        RuleInfoSection.this.bitwiseRuleTxt.setText("");
        this.oldAdvancedFormula = this.advancedFormula;
        this.advancedFormula = "";
        this.complexRuleBtn.setEnabled(false);

      }
      else if (!RuleInfoSection.this.paramEditSection.getBitwiseRuleChkBox().getSelection()
      /* !this.selectedParam.isRefValueMatchFlag() && */) {
        setTextRuleInfoFromOld();

      }
      else if (RuleInfoSection.this.paramEditSection.getBitwiseRuleChkBox().getSelection()) {
        setOldLowerLimit();
        RuleInfoSection.this.lowerLimitTxt.setText("");
        RuleInfoSection.this.lowerLimitTxt.setEditable(false);
        setOldUpperLimit();
        RuleInfoSection.this.upperLimitTxt.setText("");
        RuleInfoSection.this.upperLimitTxt.setEditable(false);
        RuleInfoSection.this.bitwiseRuleTxt.setEnabled(true);
        if (this.oldAdvancedFormula != null) {
          this.advancedFormula = this.oldAdvancedFormula;
        }
        RuleInfoSection.this.complexRuleBtn.setEnabled(true);
        if (null != this.oldBitWiseLimit) {
          RuleInfoSection.this.bitwiseRuleTxt.setText(this.oldBitWiseLimit);
        }
      }
    }
  }


  /**
   * @return
   */
  private boolean checkExactMatch() {
    return RuleInfoSection.this.btnParamRefValMatch.getSelection() ||
        ((RuleInfoSection.this.btnUseCurrExactMatch != null) &&
            RuleInfoSection.this.btnUseCurrExactMatch.getSelection());
  }


  /**
   * @return
   */
  private boolean checkParameterType() {
    return (this.selectedParam.getType().equals(ParameterType.VALUE.getText()) ||
        this.selectedParam.getType().equals(ParameterType.MAP.getText()) ||
        this.selectedParam.getType().equals(ParameterType.CURVE.getText()) ||
        this.selectedParam.getType().equals(ParameterType.AXIS_PTS.getText())) ||
        this.selectedParam.getType().equals(ParameterType.VAL_BLK.getText());
  }


  /**
   * setOldUpperLimit
   */
  private void setOldUpperLimit() {
    if ((!RuleInfoSection.this.paramEditSection.getBitwiseRuleChkBox().getSelection() ||
        !RuleInfoSection.this.btnParamRefValMatch.getSelection()) &&
        !RuleInfoSection.this.upperLimitTxt.getText().isEmpty()) {
      this.oldUpperLimit = RuleInfoSection.this.upperLimitTxt.getText().trim();
    }
  }


  /**
   * setOldLowerLimit
   */
  private void setOldLowerLimit() {
    if ((!RuleInfoSection.this.paramEditSection.getBitwiseRuleChkBox().getSelection() ||
        !RuleInfoSection.this.btnParamRefValMatch.getSelection()) &&
        !RuleInfoSection.this.lowerLimitTxt.getText().isEmpty()) {
      this.oldLowerLimit = RuleInfoSection.this.lowerLimitTxt.getText().trim();
    }
  }


  /**
   *
   */
  private void setTextRuleInfoFromOld() {
    RuleInfoSection.this.lowerLimitTxt.setEditable(true);
    if (null != this.oldLowerLimit) {
      RuleInfoSection.this.lowerLimitTxt.setText(this.oldLowerLimit);
    }
    RuleInfoSection.this.upperLimitTxt.setEditable(true);
    if (null != this.oldUpperLimit) {
      RuleInfoSection.this.upperLimitTxt.setText(this.oldUpperLimit);
    }
    if (!RuleInfoSection.this.bitwiseRuleTxt.getText().isEmpty()) {
      this.oldBitWiseLimit = RuleInfoSection.this.bitwiseRuleTxt.getText().trim();
    }
    if (!((this.selectedCdrRule != null) && "COMPLEX RULE!".equals(this.selectedCdrRule.getBitWiseRule()))) {
      RuleInfoSection.this.bitwiseRuleTxt.setText("");
    }
    else {
      RuleInfoSection.this.bitwiseRuleTxt.setText(this.selectedCdrRule.getBitWiseRule());
    }
    this.complexRuleBtn.setEnabled(true);
    if (this.oldAdvancedFormula != null) {
      this.advancedFormula = this.oldAdvancedFormula;
    }
    RuleInfoSection.this.bitwiseRuleTxt.setEnabled(false);
  }

  /**
   * @param refValCalDataObj the refValCalDataObj to set
   */
  public void setRefValCalDataObj(final CalData refValCalDataObj) {
    this.refValCalDataObj = refValCalDataObj;
  }

  /**
   * @return selectedCdrRule
   */
  public ReviewRule getSelectedCdrRule() {
    return this.selectedCdrRule;
  }


  /**
   * @return StyledText
   */
  public Text getHintTxtArea() {
    return this.hintTxtArea;
  }

  /**
   * @return StyledText
   */
  public Text getUnicodeRmrkTxtArea() {
    return this.unicodeRmrkTxtArea;
  }

  /**
   * @return the refValCalDataObj
   */
  public CalData getRefValCalDataObj() {
    return this.refValCalDataObj;
  }

  /**
   * @param selectedUnit selectedUnit
   */
  public void setSelectedUnit(final String selectedUnit) {
    this.selectedUnit = selectedUnit;

  }

  /**
   * @return StyledText
   */
  public Text getRefValueTxt() {
    return this.refValueTxt;
  }

  /**
   * @param cdrFuncParam CDRFuncParameter
   */
  public void setRuleDetails(final IParameter cdrFuncParam) {
    // ICDM-1162
    enableDisableReviewFields();
    boolean limitsFieldEditable = this.paramCollDataProvider.isModifiable(this.cdrFunction) &&
        (((this.selectedCdrRule != null) && !this.selectedCdrRule.isDcm2ssd()) || (this.selectedCdrRule == null)) &&
        (this.selectedParam.getType().equals(ParameterType.VALUE.getText()) ||
            this.selectedParam.getType().equals(ParameterType.MAP.getText()) ||
            this.selectedParam.getType().equals(ParameterType.VAL_BLK.getText()) ||
            this.selectedParam.getType().equals(ParameterType.CURVE.getText()) ||
            this.selectedParam.getType().equals(ParameterType.AXIS_PTS.getText()));
    this.lowerLimitTxt.setEditable(limitsFieldEditable);
    this.upperLimitTxt.setEditable(limitsFieldEditable);
    this.bitwiseRuleTxt.setEnabled(limitsFieldEditable && this.paramDataProvider.isBitWise(this.selectedParam));
    this.btnDCMfile.setEnabled(this.paramCollDataProvider.isModifiable(this.cdrFunction));

    setLowerLimitField();

    setUpperLimitField();

    setBitWiseLimitField();

    setUnitField();

    setRefValField(cdrFuncParam);

    setExactMatchClearRefVal();

    setReadyForSeriesField();

    setHintField();

    setUnicodeRmrkField();

    setMaturityLevelField();

    setLimitsToNonEditable();

    enableSave();

    // if edit rule dialog is opened for ruleSet , param properties cannot be edited
    enableParamProp();
    // ICDM-1190
    if (this.readOnlyMode) {
      disableAllFields();
    }

  }

  /**
   *
   */
  private void setBitWiseLimitField() {
    if (this.isWizard && !this.wizard.getWizardData().isSameBitWise()) {
      // if bitwise limits are different, set a markup value
      this.bitwiseRuleTxt.setText(CommonUIConstants.DISP_TEXT_USE_CUR_VAL);
    }
    else {
      this.bitwiseRuleTxt.setText(this.selectedCdrRule == null ? CommonUIConstants.EMPTY_STRING
          : CommonUtils.checkNull(this.selectedCdrRule.getBitWiseRule()));
    }
    this.bitWiseConfigDialog.setOldBitWiseValue(RuleInfoSection.this.bitwiseRuleTxt.getText());
    removeListenerFromTextField(this.bitwiseRuleTxt);


  }


  /**
   * Disable the parameter props for rule set
   */
  public void enableParamProp() {
    if (!this.paramCollDataProvider.isParamMappingModifiable(this.cdrFunction)) {
      this.paramEditSection.getCodeWordChkBox().setEnabled(false);
      this.paramEditSection.getComboClass().setEnabled(false);
      this.paramEditSection.getHintTxtArea().setEnabled(false);
      this.paramEditSection.getBitwiseRuleChkBox().setEnabled(false);
      this.paramEditSection.getBlackListChkBox().setEnabled(false);
    }
  }

  /**
   * Disable all the fields when the dialog is opened in the read only mode
   */
  private void disableAllFields() {
    this.lowerLimitTxt.setEditable(false);
    this.upperLimitTxt.setEditable(false);
    this.bitwiseRuleTxt.setEnabled(false);
    this.refValueTxt.setEditable(false);
    this.readySeriesButton.setEnabled(false);
    this.unitText.setEnabled(false);
    this.maturityCombo.setEnabled(false);
    this.btnClearRefVal.setEnabled(false);
    this.btnParamRefValMatch.setEnabled(false);
    if (null != this.btnUseCurrExactMatch) {
      this.btnUseCurrExactMatch.setEnabled(false);
    }
    if (null != this.btnChooseExactMatch) {
      this.btnChooseExactMatch.setEnabled(false);
    }
    this.hintTxtArea.setEditable(false);
    this.unicodeRmrkTxtArea.setEditable(false);
    this.btnDCMfile.setEnabled(false);
    // disable fields in param properties section
    this.paramEditSection.disableAllFields();
    disableLinkActionButtons(false);
  }


  /**
   * @param isToEnable
   */
  private void disableLinkActionButtons(final boolean isToEnable) {
    this.btnDeleteLink.setEnabled(isToEnable);
    this.btnEditLink.setEnabled(isToEnable);
    this.btnAddLink.setEnabled(isToEnable);
  }

  /**
   * enable disable exact match field & clear ref value field, set Exact match field
   */
  private void setExactMatchClearRefVal() {
    this.btnParamRefValMatch.setEnabled(true);
    this.btnClearRefVal.setEnabled(true);
    if ("".equals(this.refValueTxt.getText())) {
      this.btnParamRefValMatch.setEnabled(false);
      this.btnClearRefVal.setEnabled(false);
    }
    else if ((this.btnUseCurrExactMatch != null) && this.btnUseCurrExactMatch.getSelection()) {
      this.btnParamRefValMatch.setEnabled(false);
    }
    // ICDM-1280
    else if (createCheckValRule() && !isUpdate()) {
      this.btnParamRefValMatch.setSelection(true);
    }
    if ((this.selectedCdrRule != null) && (this.selectedCdrRule.isDcm2ssd()) &&
        (!this.isWizard || this.wizard.getWizardData().isSameExactMatchFlag())) {
      this.btnParamRefValMatch.setSelection(true);
    }
  }

  /**
   * set maturity level field
   */
  private void setMaturityLevelField() {
    if (this.isWizard && !this.sameMaturityLevel) {
      this.maturityCombo.select(USE_CURR_VAL_INDEX);
    }
    // selecting item in maturity combo box for existing rule
    else if (CommonUtils.isNotNull(this.selectedCdrRule) &&
        CommonUtils.isNotNull(this.selectedCdrRule.getMaturityLevel())) {
      this.maturityCombo.select(
          RuleMaturityLevel.getIcdmMaturityLvlEnumForSsdText(this.selectedCdrRule.getMaturityLevel()).ordinal());
    }
    // ICDM-1280
    else if (createCheckValRule()) {
      this.maturityCombo.select(
          RuleMaturityLevel.getIcdmMaturityLvlEnumForSsdText(RuleMaturityLevel.START.getSSDMaturityLevel()).ordinal());
    }
    setSelectedMaturityLevel(this.maturityCombo.getItem(this.maturityCombo.getSelectionIndex()));
  }

  /**
   * set hint field
   */
  private void setHintField() {
    if (this.isWizard && (this.wizard.getWizardData().getCdrRulesList() != null) &&
        (this.wizard.getWizardData().getCdrRulesList().size() > LIST_SIZE_ONE) && !this.sameHint) {

      // if hints are different , set a markup value
      this.hintTxtArea.setText(CommonUIConstants.DISP_TEXT_USE_CUR_VAL);
    }
    // ICDM-1280
    else if (createCheckValRule()) {
      StringBuilder hint = new StringBuilder(HINT_LENGTH);
      hint.append("Rule based on review result :").append("\n").append("Review Name : ").append(this.resultName)
          .append("\n").append("Project Name : ").append(this.pidcVersName);
      this.hintTxtArea.setText(hint.toString());
    }
    else {
      this.hintTxtArea.setText(this.selectedCdrRule == null ? CommonUIConstants.EMPTY_STRING
          : CommonUtils.checkNull(this.selectedCdrRule.getHint()));
    }

    removeListenerFromTextField(this.hintTxtArea);


  }

  private void setUnicodeRmrkField() {
    this.unicodeRmrkTxtArea.setText(this.selectedCdrRule == null ? CommonUIConstants.EMPTY_STRING
        : CommonUtils.checkNull(this.selectedCdrRule.getUnicodeRemarks()));
  }

  /**
   * set ready for series fields
   */
  private void setReadyForSeriesField() {
    // ICDM-1182
    if (this.isWizard && !this.wizard.getWizardData().isSameReadyForSeries()) {
      this.useCurrRvwMtd.setSelection(true);
    }
    else {
      this.readySeriesButton.setSelection(false);
      if ((this.selectedCdrRule != null) && (this.selectedCdrRule.getReviewMethod() != null) &&
          this.selectedCdrRule.getReviewMethod().equals(ApicConstants.READY_FOR_SERIES.YES.getDbType())) {
        this.readySeriesButton.setSelection(true);
      }
    }
  }

  /**
   * sets the ref val field
   *
   * @param cdrFuncParam CDRFuncParameter
   */
  private void setRefValField(final IParameter cdrFuncParam) {
    if (this.isWizard && !this.wizard.getWizardData().isSameRefVal()) {
      // ICDM-1182
      // if reference values are different, set a markup value
      this.refValueTxt.setText(CommonUIConstants.DISP_TEXT_USE_CUR_VAL);
    }
    else {
      // set the flag
      this.isRefValTyped = true;
      // ICDM-1280
      if (createCheckValRule()) {
        this.refValueTxt.setText(null == getRuleCheckVal().getCalDataPhy() ? CommonUIConstants.EMPTY_STRING
            : getRuleCheckVal().getCalDataPhy().getSimpleDisplayValue());
        String name = this.checkValueObj.getCalDataPhy().getName();

        // iCDM-2071
        this.refValCalDataObj = CalDataUtil.createCopy(this.checkValueObj, CDMLogger.getInstance());

        // icdm-2339
        if (ApicUtil.isVariantCoded(name)) {
          // if the parameter is variant coded , set the base parameter name in CalData object
          String newParamName = ApicUtil.getBaseParamName(name);
          this.refValCalDataObj.setShortName(newParamName);
          this.refValCalDataObj.getCalDataPhy().setName(newParamName);
        }
        removeListenerFromTextField(this.refValueTxt);
        addListener(this.refValueTxt, getRuleCheckVal(), true);
      }
      else {
        this.refValueTxt.setText(this.selectedCdrRule == null ? CommonUIConstants.EMPTY_STRING
            : CommonUtils.checkNull(this.selectedCdrRule.getRefValueDispString()));
        // iCDM-2071
        this.refValCalDataObj = CalDataUtil.createCopy(getRefValue(cdrFuncParam), CDMLogger.getInstance());
        removeListenerFromTextField(this.refValueTxt);

        addListener(this.refValueTxt, this.refValCalDataObj, true);
      }
    }
  }

  /**
   * set unit field
   */
  private void setUnitField() {
    if (this.isWizard && !this.wizard.getWizardData().isSameUnit()) {
      // ICDM-1182
      this.selectedUnit = CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
    }
    // ICDM-1280
    else if (createCheckValRule() && !isUpdate()) {
      this.selectedUnit = CalDataUtil.getUnit(getRuleCheckVal());
    }
    else {
      // Icdm-513
      this.selectedUnit = this.selectedCdrRule == null ? CommonUIConstants.EMPTY_STRING
          : CommonUtils.checkNull(this.selectedCdrRule.getUnit());
    }
    this.unitText.setText(this.selectedUnit);
  }

  /**
   * set upper limit field
   */
  private void setUpperLimitField() {
    if (this.isWizard && !this.wizard.getWizardData().isSameUpperLmt()) {
      // ICDM-1182
      // if upper limits are different, set a markup value
      this.upperLimitTxt.setText(CommonUIConstants.DISP_TEXT_USE_CUR_VAL);
    }
    else {
      this.upperLimitTxt
          .setText(this.selectedCdrRule == null ? CommonUIConstants.EMPTY_STRING : checkNullForUpperLimit());
    }
    removeListenerFromTextField(this.upperLimitTxt);

  }

  /**
   * @return
   */
  private String checkNullForUpperLimit() {
    return this.selectedCdrRule.getUpperLimit() == null ? CommonUIConstants.EMPTY_STRING
        : this.selectedCdrRule.getUpperLimit().toString();
  }

  /**
   * set lower limit field
   */
  private void setLowerLimitField() {
    if (this.isWizard && !this.wizard.getWizardData().isSameLowerLmt()) {
      // ICDM-1182
      // if lower limits are different , set a markup value
      this.lowerLimitTxt.setText(CommonUIConstants.DISP_TEXT_USE_CUR_VAL);
    }
    else {
      this.lowerLimitTxt.setText(this.selectedCdrRule == null ? CommonUIConstants.EMPTY_STRING : checkNullLowerLimit());
    }
    removeListenerFromTextField(this.lowerLimitTxt);

  }


  /**
   * @return
   */
  private String checkNullLowerLimit() {
    return this.selectedCdrRule.getLowerLimit() == null ? CommonUIConstants.EMPTY_STRING
        : this.selectedCdrRule.getLowerLimit().toString();
  }


  /**
   * returns reference value caldata
   *
   * @param cdrFuncParam CDRFuncParameter
   */
  private CalData getRefValue(final IParameter cdrFuncParam) {
    // ICDM-1162
    if ((cdrFuncParam != null) && (this.selectedCdrRule == null)) {
      return ReviewRuleUtil.getRefValue(this.paramDataProvider.getReviewRule(cdrFuncParam));
    }
    if ((this.selectedCdrRule != null) && (this.selectedCdrRule.getRefValueCalData() != null)) {
      return CalDataUtil.getCDPObj(this.selectedCdrRule.getRefValueCalData());
    }

    if ((this.selectedCdrRule != null) && (this.selectedCdrRule.getRefValue() != null)) {
      // Prepare DCM string for this decimal string and convert to CalData
      String paramName = this.selectedCdrRule.getParameterName();
      String dcmStr = CalDataUtil.createDCMStringForNumber(paramName, this.selectedCdrRule.getUnit(),
          this.selectedCdrRule.getRefValue().toString());
      return CalDataUtil.dcmToCalDataExt(dcmStr, paramName, CDMLogger.getInstance());
    }
    return null;
  }

  /**
   * @param nameTxt2
   * @param calData
   * @param showTableGraphCompositeAlways
   */
  public void addListener(final Text nameTxt2, final CalData calData, final boolean showTableGraphCompositeAlways) {

    // Listens the activation of the text fields
    Listener activateListener = event -> {
      if (null != calData) {
        String value = nameTxt2.getText().trim();
        // Following is additional check, were the trigger for
        // displaying values is not applicable
        if (!(("".equals(value) || NOT_APPLICABLE.equalsIgnoreCase(value)) || value.endsWith("%"))) {
          nameTxt2.setSelection(0, value.length());
          nameTxt2.showSelection();
        }
      }
    };

    final Listener graphListener = event -> {
      CalData calDataForDisplayTableGraph = null;
      if (calData != null) {
        calDataForDisplayTableGraph = calData;
      }
      if (nameTxt2.equals(RuleInfoSection.this.refValueTxt) && (RuleInfoSection.this.refValCalDataObj != null)) {
        calDataForDisplayTableGraph = RuleInfoSection.this.refValCalDataObj;
      }
      displayTableGraph(nameTxt2, calDataForDisplayTableGraph, showTableGraphCompositeAlways);
    };

    nameTxt2.addListener(SWT.CURSOR_APPSTARTING, activateListener);
    nameTxt2.addListener(SWT.Activate, graphListener);

    Map<Integer, Listener> listenerMap = this.textListenerMap.computeIfAbsent(nameTxt2, k -> new HashMap<>());
    listenerMap.put(SWT.Activate, graphListener);
    listenerMap.put(SWT.CURSOR_APPSTARTING, activateListener);

    final Listener[] deActivateListeners = nameTxt2.getListeners(SWT.Deactivate);

    if (deActivateListeners.length == DEACTIVATE_LEN) {
      nameTxt2.addListener(SWT.Deactivate, event -> {
        // revert selection
        nameTxt2.setSelection(0);
        nameTxt2.showSelection();
      });
    }

    addFocusListenerForRemarks(nameTxt2);
  }


  /**
   * @param numberStr as input
   * @return true for string
   * @throws Exception for invalid integer
   */
  public boolean checkRefValue(final String numberStr) {
    try {
      double parseDouble = Double.parseDouble(numberStr);
      CDMLogger.getInstance().info(parseDouble + " is a valid number");
    }
    catch (Exception e) {
      String number = numberStr.replaceAll("[,|.]", "");
      try {
        double parseDouble = Double.parseDouble(number);
        CDMLogger.getInstance().info(parseDouble + " is a valid number");
      }
      catch (Exception e1) {
        CDMLogger.getInstance().debug(e1.getMessage(), e1);
        return true;
      }
      throw e;
    }
    return false;
  }

  /**
   * @param nameTxt2
   * @param calData CalData to show in T/G Viewer
   * @param showCompositeAlways flag which indicates whether calDataComparison object needs to be shown all the time.
   *          This flag is used when there is no ref value existing for a rule but when user drags a value type to the
   *          table graph composite to have a look at the value.As of now this is applicable only to RefValue field in
   *          rule edit dialog
   */
  public void displayTableGraph(final Text nameTxt2, final CalData calData, final boolean showCompositeAlways) {
    if (CommonUtils.isNotNull(calData)) {
      String value = nameTxt2.getText().trim();
      // Following is additional check, were the trigger for
      // displaying values is not applicable
      if (("".equals(value) || NOT_APPLICABLE.equalsIgnoreCase(value)) || value.endsWith("%")) {
        RuleInfoSection.this.getCalDataTableGraphComposite().clearTableGraph();
      }
      else {
        // Display the values of the CalDataPhy
        try {
          displayCalDataPhy(calData);
        }
        catch (CalDataTableGraphException ex) {
          CDMLogger.getInstance().error("Text values not supported in Graph", ex, Activator.PLUGIN_ID);
        }
      }
    }
    else if (showCompositeAlways && (!getCalDataComparison().getCalDataAttributes().isEmpty())) {
      try {
        getCalDataTableGraphComposite().fillTableAndGraph(getCalDataComparison());
      }
      catch (CalDataTableGraphException e) {
        CDMLogger.getInstance().error("Text values not supported in Graph", e, Activator.PLUGIN_ID);
      }
    }
    else {
      RuleInfoSection.this.getCalDataTableGraphComposite().clearTableGraph();
    }
  }


  /**
   * @param calData
   * @throws CalDataTableGraphException
   */
  private void displayCalDataPhy(final CalData calData) throws CalDataTableGraphException {
    RuleInfoSection.this.getCalDataTableGraphComposite().addSelectionChangedListener(event -> enableSave());
    if (this.refCalAttr == null) {
      RuleInfoSection.this.getCalDataTableGraphComposite().clearTableGraph();
      this.refCalAttr = new CalDataAttributes(calData, GRAPH_COLOR_BLUE);
      this.refCalAttr.setShowDifferenceIndicator(true);
      String labelPrefix = "Value";
      this.refCalAttr.setLabelPrefix(" (" + labelPrefix + ") ");
      // ICDM-1128
      if (!A2LUtil.VALUE_TEXT.equals(calData.getCalDataPhy().getType())) {
        this.refCalAttr.setEditAllowed(true);
      }
      getCalDataComparison().addCalDataAttr(this.refCalAttr);
    }
    getCalDataTableGraphComposite().fillTableAndGraph(getCalDataComparison());
  }

  /**
   * @param refValueTxtField RefValue text field
   */
  public void removeListenerFromTextField(final Text refValueTxtField) {
    final Map<Integer, Listener> listenerMap = this.textListenerMap.get(refValueTxtField);
    if (listenerMap != null) {
      for (Entry<Integer, Listener> listenerMapEntry : listenerMap.entrySet()) {
        refValueTxtField.removeListener(listenerMapEntry.getKey(), listenerMapEntry.getValue());
      }
    }
  }

  /**
   * @param cdrFuncParam
   */
  private void enableDisableReviewFields() {
    boolean isModifiable = this.paramCollDataProvider.isModifiable(this.cdrFunction);
    this.readySeriesButton.setEnabled(isModifiable);
    this.hintTxtArea.setEditable(isModifiable);
    this.unicodeRmrkTxtArea.setEditable(isModifiable);
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
   * @return save button
   */
  public Button getSaveBtn() {
    return this.editRuleDialog.getSaveBtn();
  }


  /**
   * @return StyledText
   */
  public Text getLowerLimitTxt() {
    return this.lowerLimitTxt;
  }

  /**
   * @return StyledText
   */
  public Text getUpperLimitTxt() {
    return this.upperLimitTxt;
  }


  /**
   * @return readySeriesButton button
   */
  public Button getReadySeries() {
    return this.readySeriesButton;
  }

  /**
   * @return the cdrFunction
   */
  public ParamCollection getCdrFunction() {
    return this.cdrFunction;
  }

  /**
   * Icdm-513
   *
   * @return the selectedUnit
   */
  public String getSelectedUnit() {
    return this.selectedUnit;
  }


  /**
   * @param selectedCdrRule selectedCdrRule
   */
  public void setSelectedCdrRule(final ReviewRule selectedCdrRule) {
    this.selectedCdrRule = selectedCdrRule;
  }

  /**
   * @param ruleDefinition RuleDefinition
   */
  public void setRuleDefDetails(final RuleDefinition ruleDefinition) {

    this.selectedCdrRule = new ReviewRule();
    this.selectedCdrRule.setParameterName(ruleDefinition.getParamName());
    this.selectedCdrRule.setLowerLimit(ruleDefinition.getLowerLimit());
    this.selectedCdrRule.setUpperLimit(ruleDefinition.getUpperLimit());
    this.selectedCdrRule.setValueType(ruleDefinition.getValueType());
    this.selectedCdrRule.setReviewMethod(ruleDefinition.getReviewMethod());
    this.selectedCdrRule.setUnit(ruleDefinition.getUnit());
    this.selectedCdrRule.setMaturityLevel(ruleDefinition.getMaturityLevel());
    this.selectedCdrRule.setDcm2ssd(ruleDefinition.isExactMatch());
    this.selectedCdrRule.setBitWiseRule(ruleDefinition.getBitWise());
    this.selectedCdrRule.setRefValue(ruleDefinition.getRefValue());
    this.selectedCdrRule.setRefValueDispString(ruleDefinition.getRefValueDisplayString());
    this.selectedCdrRule.setFormulaDesc(ruleDefinition.getFormulaDesc());

    this.selectedCdrRule.setRefValCalData(
        CalDataUtil.convertCalDataToZippedByteArr(ruleDefinition.getRefValueCalData(), CDMLogger.getInstance()));
    setRuleDetails(null);
    enableSave();
  }


  /**
   * method to clear all fields
   */
  public void clearAllFields() {
    this.lowerLimitTxt.setText("");
    this.upperLimitTxt.setText("");
    this.refValueTxt.setText("");
    // ICDM-1922
    // by default manual is set
    this.readySeriesButton.setSelection(false);
    this.maturityCombo.select(0);
    this.unitText.setText("");
    this.btnClearRefVal.setEnabled(false);
    this.btnParamRefValMatch.setSelection(false);
    this.btnParamRefValMatch.setEnabled(false);
    setLimitsToNonEditable();
    RuleInfoSection.this.getCalDataTableGraphComposite().clearTableGraph();
  }

  /**
   * @return the update
   */
  public boolean isUpdate() {
    return this.update;
  }


  /**
   * @return the selectedMaturityLevel
   */
  public String getSelectedMaturityLevel() {
    return this.selectedMaturityLevel;
  }


  /**
   * @param selectedMaturityLevel the selectedMaturityLevel to set
   */
  public void setSelectedMaturityLevel(final String selectedMaturityLevel) {
    this.selectedMaturityLevel = selectedMaturityLevel;
  }

  /**
   * @return the sameMaturityLevel
   */
  public boolean isSameMaturityLevel() {
    return this.sameMaturityLevel;
  }

  /**
   * Sets unit, maturity level and remarks when a ref value is added
   */
  public void setValues() {

    RuleInfoSection.this.unitText.setText(RuleInfoSection.this.refValCalDataObj.getCalDataPhy().getUnit());// ICDM-1052
    setSelectedUnit(RuleInfoSection.this.refValCalDataObj.getCalDataPhy().getUnit());
    // ICDM-1125
    if ((RuleInfoSection.this.refValCalDataObj.getCalDataHistory() != null)) {
      List<HistoryEntry> caldataHistoryList =
          RuleInfoSection.this.refValCalDataObj.getCalDataHistory().getHistoryEntryList();
      if (CommonUtils.isNotEmpty(caldataHistoryList) &&
          (caldataHistoryList.get(caldataHistoryList.size() - 1).getRemark() != null)) {
        String value = caldataHistoryList.get(caldataHistoryList.size() - 1).getRemark().getValue().trim();

        // Set remarks and unicode remarks from imported file
        importRemarks(value, RuleInfoSection.this.hintTxtArea);
        importRemarks(value, RuleInfoSection.this.unicodeRmrkTxtArea);
      }
    }
    if (RuleInfoSection.this.maturityCombo.indexOf(RuleMaturityLevel
        .getIcdmMaturityLvlFromImportFileTxt(CalDataUtil.getStatus(RuleInfoSection.this.refValCalDataObj))) != -1) {
      RuleInfoSection.this.maturityCombo.select(RuleInfoSection.this.maturityCombo.indexOf(RuleMaturityLevel
          .getIcdmMaturityLvlFromImportFileTxt(CalDataUtil.getStatus(RuleInfoSection.this.refValCalDataObj))));
      setSelectedMaturityLevel(RuleMaturityLevel
          .getIcdmMaturityLvlFromImportFileTxt(CalDataUtil.getStatus(RuleInfoSection.this.refValCalDataObj)));
    }

    // the Selected Cdr rule can be null if it is called from Series stat or scratch pad
    if (this.selectedCdrRule != null) {
      this.selectedCdrRule.setRefValCalData(
          CalDataUtil.convertCalDataToZippedByteArr(RuleInfoSection.this.refValCalDataObj, CDMLogger.getInstance()));
    }
  }


  /**
   * @param remarksImported
   * @param remarksTxt
   */
  private void importRemarks(final String remarksImported, final Text remarksTxt) {
    StringBuilder strNewRemarks = new StringBuilder();

    if (!CommonUtils.isEmptyString(remarksTxt.getText())) {
      strNewRemarks.append(remarksTxt.getText()).append("\n\n");
    }
    String strOldRemarks = remarksTxt.getText().trim();
    if (strOldRemarks.isEmpty() ||
        (!strOldRemarks.contains(remarksImported) && !remarksImported.contains(strOldRemarks))) {
      strNewRemarks.append(remarksImported);
    }
    remarksTxt.setText(strNewRemarks.toString());
  }

  /**
   * @return the btnUseCurrExactMatch
   */
  public Button getBtnUseCurrExactMatch() {
    return this.btnUseCurrExactMatch;
  }

  /**
   * @return the btnChooseExactMatch
   */
  public Button getBtnChooseExactMatch() {
    return this.btnChooseExactMatch;
  }


  /**
   * @param isRefValTyped the isRefValTyped to set
   */
  public void setRefValTyped(final boolean isRefValTyped) {
    this.isRefValTyped = isRefValTyped;
  }


  /**
   * @return the btnClearRefVal
   */
  public Button getBtnClearRefVal() {
    return this.btnClearRefVal;
  }


  /**
   * @return the refCalAttr
   */
  public CalDataAttributes getRefCalAttr() {
    return this.refCalAttr;
  }

  /**
   * @return the calDataTableGraphComposite
   */
  public CalDataTableGraphComposite getCalDataTableGraphComposite() {
    return this.calDataTableGraphComposite;
  }

  /**
   * @return the graphColor
   */
  public int getGraphColor() {
    return this.graphColor;
  }

  /**
   * @param graphColor
   */
  public void setGraphColor(final int graphColor) {
    this.graphColor = graphColor;

  }

  /**
   * @return the calDataComparison
   */
  public CalDataComparison getCalDataComparison() {
    return this.calDataComparison;
  }

  /**
   * @return the unitText
   */
  public Text getUnitText() {
    return this.unitText;
  }

  /**
   * @return the editRuleDialog
   */
  public EditRuleDialog getEditRuleDialog() {
    return this.editRuleDialog;
  }


  /**
   * @return the wizard
   */
  public AddNewConfigWizard getWizard() {
    return this.wizard;
  }

  /**
   * @param update update flag
   */
  public void setUpdate(final boolean update) {
    this.update = update;

  }


  /**
   * @return the wizardPage
   */
  public CreateEditRuleWizardPage getWizardPage() {
    return this.wizardPage;
  }


  /**
   * @return the paramCollDataProvider
   */
  public ParamCollectionDataProvider getParamCollDataProvider() {
    return this.paramCollDataProvider;
  }


  /**
   * @param paramCollDataProvider the paramCollDataProvider to set
   */
  public void setParamCollDataProvider(final ParamCollectionDataProvider paramCollDataProvider) {
    this.paramCollDataProvider = paramCollDataProvider;
  }


  /**
   * @return the paramDataProvider
   */
  public ParameterDataProvider<D, P> getParamDataProvider() {
    return this.paramDataProvider;
  }


  /**
   * @return the units
   */
  public SortedSet<String> getUnits() {
    return this.units;
  }


  /**
   * @param units the units to set
   */
  public void setUnits(final SortedSet<String> units) {
    this.units = units;
  }


  /**
   * @return true if rule has to be created from result parameter
   */
  private boolean createCheckValRule() {
    return this.checkValueObj != null;
  }

  /**
   * @return the checkvalue of the result parameter
   */
  private CalData getRuleCheckVal() {
    return this.checkValueObj;
  }


  /**
   * @return the advancedFormula
   */
  public String getAdvancedFormula() {
    return this.advancedFormula;
  }


  /**
   * @param advancedFormula the advancedFormula to set
   */
  public void setAdvancedFormula(final String advancedFormula) {
    this.advancedFormula = advancedFormula;
  }


  /**
   * @return the ruleLinksSet
   */
  public SortedSet<LinkData> getRuleLinksSet() {
    return this.ruleLinksSet;
  }


  /**
   * @param ruleLinksSet the ruleLinksSet to set
   */
  public void setRuleLinksSet(final SortedSet<LinkData> ruleLinksSet) {
    this.ruleLinksSet = ruleLinksSet;
  }

}
