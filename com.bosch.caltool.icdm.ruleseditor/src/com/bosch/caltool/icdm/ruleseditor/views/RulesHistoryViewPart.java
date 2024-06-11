/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldataphy.CalDataPhy;
import com.bosch.calmodel.caldataphyutils.CalDataTableGraphComposite;
import com.bosch.calmodel.caldataphyutils.exception.CalDataTableGraphException;
import com.bosch.caltool.apic.ui.sorter.DependencyGridTabViewerSorter;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.ss.CalDataType;
import com.bosch.caltool.icdm.client.bo.ss.SeriesStatisticsInfo;
import com.bosch.caltool.icdm.common.bo.a2l.RuleMaturityLevel;
import com.bosch.caltool.icdm.common.bo.cdr.ReviewRuleUtil;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.dialogs.ComplexRuleDialog;
import com.bosch.caltool.icdm.common.ui.utils.CalDataUtil;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.AbstractViewPart;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleExt;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleParamCol;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.ruleseditor.sorters.RuleHistoryViewerSorter;
import com.bosch.caltool.icdm.ws.rest.client.cdr.ReviewRuleServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RuleSetRuleServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * ICDM-708 This class is for the view to display Rules History for a parameter
 *
 * @author mkl2cob
 */
public class RulesHistoryViewPart extends AbstractViewPart {

  /**
   * FormToolkit instance
   */
  private FormToolkit toolkit;
  /**
   * Text instance for lower limit value
   */
  private StyledText txtLowerLmtValue;
  /**
   * Text instance for UPPER limit value
   */
  private StyledText txtUpperLmtValue;
  /**
   * Defines text field widthhint
   */
  private static final int TEXT_FIELD_WIDTHHINT = 120;
  /**
   * Instance of form
   */
  private ScrolledForm scrolledForm;
  /**
   * Defines review data analyzer composite to split into number of columns
   */
  private static final int REVIEW_DATA_COLS = 4;

  private static final int TAB_VERTICAL_SPAN = 6;
  /**
   * Defines statistic values grid tableviewer heighthint
   */
  private static final int TAB_HEIGHTHINT = 100;
  /**
   * Defines statistic values grid tableviewer horizontal span layout data
   */
  private static final int TAB_HORIZONTAL_SPAN = 3;

  /** Table Graph Composite instance */
  private CalDataTableGraphComposite calDataTableGraphComposite;

  /**
   * Defines datasets gridviewer column width
   */
  private static final int DATASETS_VERSION_COLUMN_WIDTH = 70;

  /**
   * Defines datasets gridviewer column width
   */
  private static final int DATASETS_DATE_COLUMN_WIDTH = 150;

  /**
   * Defines datasets gridviewer column width
   */
  private static final int DATASETS_USER_COLUMN_WIDTH = 170;
  /**
   * Sorter for Rules History sorter
   */
  private RuleHistoryViewerSorter ruleHstrySorter;

  /**
   * Grid table viewer for rules versions
   */
  private GridTableViewer ruleVersionsGridTableViewer;

  /** Map which holds the listener(SWT.Activate) instance for the Text */
  private final Map<org.eclipse.swt.widgets.Scrollable, Map<Integer, Listener>> textListenerMap = new HashMap<>();

  /**
   * Text instance for unit value
   */
  private StyledText txtUnitValue;
  private StyledText txtHintValue;
  private StyledText txtRemarkUnicodeValue;
  private StyledText txtMethodValue;
  private StyledText txtRefValue;
  // Button instance for exact match to reference value
  private Button btnParamRefValMatch;
  private GridTableViewer attrDepTabViewer;
  private DependencyGridTabViewerSorter depSorter;
  // Extra code
  protected ReviewRule selectedRule;

  /**
   * Bitwise Rule text
   */
  private StyledText bitwiseRuleTxt;
  private ReviewRule selectedRuleForHist;
  private Button complexRuleBtn;
  private Label complexRuleLabel;
  private boolean isCompliance;

  private Text ruleOwnerTxt;
  private Text cocTxt;
  private StyledText intAdaptDescTxt;
  private StyledText dataDescTxt;
  private StyledText histDescTxt;

  @Override
  public void createPartControl(final Composite parent) {
    addHelpAction();
    // Create toolkit
    this.toolkit = new FormToolkit(parent.getDisplay());

    // Create scrolled form
    createScrolledForm(parent);

    // Create Rule version section
    final Section ruleVersionSection = createRuleVersionSection();

    // Create Rule version composite
    final Composite ruleVersionComp = this.toolkit.createComposite(ruleVersionSection);

    ruleVersionSection.setClient(ruleVersionComp);

    ruleVersionComp.setLayout(new GridLayout());

    // Create ui controls on Rule version section
    createRuleVersionControls(ruleVersionComp);

    // Create review info ui section
    final Section reviewDataSection = createReviewdataSection();

    // Create review info ui composite
    final Composite reviewdataComp = this.toolkit.createComposite(reviewDataSection);

    reviewDataSection.setClient(reviewdataComp);

    // Set layout to caldataComp
    final GridLayout layout = new GridLayout();
    layout.numColumns = REVIEW_DATA_COLS;
    reviewdataComp.setLayout(layout);

    // Create ui controls on review data section
    createReviewDataUIControls(reviewdataComp);

    // Create graph/table section
    final Section graphSection = createGraphSection();

    // Create graph/table composite
    Composite graphComp = this.toolkit.createComposite(graphSection);
    graphSection.setClient(graphComp);

    // Set layout to graph/table composite
    graphComp.setLayout(new GridLayout());
    // Create table graph composite structure
    this.calDataTableGraphComposite = new CalDataTableGraphComposite(graphComp, this.scrolledForm.getHorizontalBar(),
        this.scrolledForm.getVerticalBar(), CDMLogger.getInstance());


  }


  /**
   * @param reviewResultComp
   */
  private void createRuleVersionControls(final Composite reviewResultComp) {
    // Create review result GridTableViewer

    this.ruleVersionsGridTableViewer = GridTableViewerUtil.getInstance()
        .createGridTableViewer(reviewResultComp, GridDataUtil.getInstance().createHeightHintGridData(GridData.FILL,
            false, false, TAB_HORIZONTAL_SPAN, /*
                                                * Horizontal span
                                                */
            TAB_HEIGHTHINT, TAB_VERTICAL_SPAN /* Vertical span */));

    this.ruleVersionsGridTableViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.ruleVersionsGridTableViewer.setInput("");

    this.ruleHstrySorter = new RuleHistoryViewerSorter();
    // Invoke TableViewer Column sorters
    invokeColumnSorter();

    // Create GridTableViewer columns
    createGridViewerColumns();

    this.ruleVersionsGridTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

      @Override
      public void selectionChanged(final SelectionChangedEvent arg0) {
        IStructuredSelection selection = (IStructuredSelection) arg0.getSelection();
        if (selection.getFirstElement() instanceof ReviewRuleExt) {
          ReviewRuleExt cdrRule = (ReviewRuleExt) (selection.getFirstElement());
          fillUISection2(cdrRule);
          RulesHistoryViewPart.this.calDataTableGraphComposite.clearTableGraph();
          fillAttrDepForRule(cdrRule);
          // Extra Code
          RulesHistoryViewPart.this.selectedRule = cdrRule;
        }

      }
    });

  }

  /*
   * Add sorter for the table columns
   */
  private void invokeColumnSorter() {
    this.ruleVersionsGridTableViewer.setComparator(this.ruleHstrySorter);
  }


  /**
   * Columns for the table viewer are created
   *
   * @param ruleVersionGridTableViewer
   */
  private void createGridViewerColumns() {
    // Creates version gridviewer column
    createVersionColViewer();

    // Creates created user gridviewer column
    createUserColViewer();

    // Creates created date gridviewer column
    createDateColViewer();


  }

  /**
   * Column to display created user
   *
   * @param ruleHistoryGridTableViewer
   */
  private void createUserColViewer() {
    final GridViewerColumn userColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(
        this.ruleVersionsGridTableViewer, "Modified User", DATASETS_USER_COLUMN_WIDTH /* Column width */);
    userColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        ReviewRuleExt rule = (ReviewRuleExt) element;
        return rule.getRuleCreatedUserDispName();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getBackground(final Object element) {
        ReviewRule rule = (ReviewRule) element;
        if (rule.getRevId().equals(RulesHistoryViewPart.this.selectedRuleForHist.getRevId())) {
          return Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
        }
        return super.getBackground(element);
      }
    });
    // Add column selection listener
    userColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(userColumn.getColumn(), 2, this.ruleHstrySorter, this.ruleVersionsGridTableViewer));

  }

  /**
   * Column to display created date
   *
   * @param reviewResultGridTableViewer
   */
  private void createDateColViewer() {
    final GridViewerColumn dateColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(
        this.ruleVersionsGridTableViewer, "Modified Date", DATASETS_DATE_COLUMN_WIDTH /* Column width */);
    dateColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        ReviewRule rule = (ReviewRule) element;
        String createdDate = "";
        if (rule.getRuleCreatedDate() != null) {
          createdDate = rule.getRuleCreatedDate().toString();
        }
        return createdDate;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getBackground(final Object element) {
        ReviewRule rule = (ReviewRule) element;
        if (rule.getRevId().equals(RulesHistoryViewPart.this.selectedRuleForHist.getRevId())) {
          return Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
        }
        return super.getBackground(element);
      }
    });

    // Add column selection listener
    dateColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(dateColumn.getColumn(), 3, this.ruleHstrySorter, this.ruleVersionsGridTableViewer));
  }

  /**
   * Column to display version no
   *
   * @param reviewResultGridTableViewer
   */
  private void createVersionColViewer() {
    final GridViewerColumn versionColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(
        this.ruleVersionsGridTableViewer, "Version", DATASETS_VERSION_COLUMN_WIDTH /* Column width */);
    versionColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        ReviewRule rule = (ReviewRule) element;
        return rule.getRevId().toString();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getBackground(final Object element) {
        ReviewRule rule = (ReviewRule) element;
        if (rule.getRevId().equals(RulesHistoryViewPart.this.selectedRuleForHist.getRevId())) {
          return Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
        }
        return super.getBackground(element);
      }
    });
    // Add column selection listener
    versionColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(versionColumn.getColumn(), 1, this.ruleHstrySorter, this.ruleVersionsGridTableViewer));
  }

  /**
   * Create rule version section
   *
   * @return
   */
  private Section createRuleVersionSection() {
    // Creates review result Section
    return createSection(CommonUIConstants.RULE_VERSION_TITLE, false);
  }

  /**
   * This method creates Table/Graph UI form section
   *
   * @return Section
   */
  private Section createGraphSection() {
    // Creates Table/Graph Section
    return createSection(CommonUIConstants.TABLE_GRAPH, false);
  }

  /**
   * This method creates section
   *
   * @param sectionName defines section name
   * @param descControlEnable defines description control enable or not
   * @return Section instance
   */
  private Section createSection(final String sectionName, final boolean descControlEnable) {
    return SectionUtil.getInstance().createSection(this.scrolledForm.getBody(), this.toolkit,
        GridDataUtil.getInstance().getGridData(), sectionName, descControlEnable);
  }

  /**
   * Create review data ui controls
   *
   * @param reviewdataComp
   */
  private void createReviewDataUIControls(final Composite reviewdataComp) {

    createLowerLimitUIControls(reviewdataComp);

    createMethodUIControls(reviewdataComp);

    createUpperLimitUIControls(reviewdataComp);

    createUnitUIControls(reviewdataComp);

    createBitwiseRuleUIControl(reviewdataComp);

    createComplexRuleUIControl(reviewdataComp);

    createRefValUIControls(reviewdataComp);

    createRefValChkBox(reviewdataComp);

    createEmptySpaceControl(reviewdataComp);

    createHintUIControls(reviewdataComp);

    createRemarkUnicodeUIControls(reviewdataComp);

    // Icdm-1245- new Table for Attr Dependency.
    createAttrDepControl(reviewdataComp);

    createAdditionalInfoControl(reviewdataComp);

  }

  /**
   * @param reviewdataComp
   */
  private void createRemarkUnicodeUIControls(final Composite reviewdataComp) {
    createLblControl(reviewdataComp, CommonUIConstants.REMARK_UNICODE, "");
    // ICDM-759
    this.txtRemarkUnicodeValue = new StyledText(reviewdataComp,
        SWT.MULTI | SWT.BORDER | SWT.SCROLL_LINE | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
    createGridDataForStyledText(this.txtRemarkUnicodeValue);
  }


  /**
   * @param reviewdataComp
   */
  private void createAdditionalInfoControl(final Composite reviewdataComp) {
    createEmptySpaceControl(reviewdataComp);
    createEmptySpaceControl(reviewdataComp);
    createEmptySpaceControl(reviewdataComp);
    createEmptySpaceControl(reviewdataComp);
    createEmptySpaceControl(reviewdataComp);
    LabelUtil.getInstance().createLabel(reviewdataComp, "Rule Owner : ");
    this.ruleOwnerTxt = TextUtil.getInstance().createText(reviewdataComp, true, "");
    createGridData(this.ruleOwnerTxt);

    LabelUtil.getInstance().createLabel(reviewdataComp, "CoC : ");
    this.cocTxt = TextUtil.getInstance().createText(reviewdataComp, true, "");
    createGridData(this.cocTxt);

    LabelUtil.getInstance().createLabel(reviewdataComp, "Internal Adaptation Desc : ");
    this.intAdaptDescTxt = new StyledText(reviewdataComp,
        SWT.MULTI | SWT.BORDER | SWT.SCROLL_LINE | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
    createGridDataForStyledText(this.intAdaptDescTxt);

    LabelUtil.getInstance().createLabel(reviewdataComp, "Data Desc : ");
    this.dataDescTxt = new StyledText(reviewdataComp,
        SWT.MULTI | SWT.BORDER | SWT.SCROLL_LINE | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
    createGridDataForStyledText(this.dataDescTxt);

    LabelUtil.getInstance().createLabel(reviewdataComp, "Historie Desc : ");
    this.histDescTxt = new StyledText(reviewdataComp,
        SWT.MULTI | SWT.BORDER | SWT.SCROLL_LINE | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
    createGridDataForStyledText(this.histDescTxt);

  }


  /**
   * @param intAdaptDescTxt2
   */
  private void createGridDataForStyledText(final StyledText styledText) {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.horizontalSpan = 3;
    gridData.heightHint = 50;
    gridData.grabExcessHorizontalSpace = true;
    styledText.setLayoutData(gridData);
    styledText.setEditable(false);

  }


  /**
   *
   */
  private void createGridData(final Text txt) {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.horizontalSpan = 3;
    gridData.heightHint = 20;
    gridData.grabExcessHorizontalSpace = true;
    txt.setLayoutData(gridData);
    txt.setEditable(false);
  }


  /**
   * @param reviewdataComp
   */
  private void createComplexRuleUIControl(final Composite reviewdataComp) {
    this.complexRuleLabel = new Label(reviewdataComp, SWT.NONE);
    this.complexRuleLabel.setText("Formula:");
    this.complexRuleLabel.setVisible(false);
    this.complexRuleBtn = new Button(reviewdataComp, SWT.TOGGLE);
    this.complexRuleBtn.setVisible(false);
    this.complexRuleBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_TYPE_COMPLIANCE_16X16));
    this.complexRuleBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        if (null != RulesHistoryViewPart.this.selectedRule) {
          final ComplexRuleDialog remarksDialog = new ComplexRuleDialog(Display.getCurrent().getActiveShell(),
              RulesHistoryViewPart.this.selectedRule.getFormulaDesc(),
              RulesHistoryViewPart.this.selectedRule.getFormula());
          remarksDialog.open();
        }
      }
    });
  }


  /**
   * @param scComp
   */
  private void createBitwiseRuleUIControl(final Composite scComp) {


    createLblControl(scComp, "Bitwise Rule:", "");
    this.bitwiseRuleTxt = createStyledText(scComp);
    this.bitwiseRuleTxt.setEditable(false);


  }

  /**
   * New Table Viewer for Attr Dependeoncies
   *
   * @param reviewdataComp
   */
  private void createAttrDepControl(final Composite reviewdataComp) {


    createLblControl(reviewdataComp, "Attribute Dpendencies", "");
    // Create review result GridTableViewer

    this.attrDepTabViewer = GridTableViewerUtil.getInstance()
        .createGridTableViewer(reviewdataComp, GridDataUtil.getInstance().createHeightHintGridData(GridData.FILL, false,
            false, TAB_HORIZONTAL_SPAN, /*
                                         * Horizontal span
                                         */
            TAB_HEIGHTHINT, TAB_VERTICAL_SPAN /* Vertical span */));

    this.attrDepTabViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.attrDepTabViewer.setInput("");

    // Invoke TableViewer Column sorters
    invokeColumnSorter();
    this.depSorter = new DependencyGridTabViewerSorter();
    this.attrDepTabViewer.setSorter(this.depSorter);
    // Create GridTableViewer columns
    createAttTabCol();


  }

  /**
   * Columns for the table viewer are created
   */
  private void createAttTabCol() {
    // Creates version gridviewer column
    createAttrNameCol();

    // Creates created user gridviewer column
    createAttrValCol();


  }


  /**
   * Column to display Attr name
   */
  private void createAttrNameCol() {
    final GridViewerColumn attrNameColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.attrDepTabViewer, "Attribute Name", DATASETS_DATE_COLUMN_WIDTH /* Column width */);
    attrNameColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        AttributeValueModel rule = (AttributeValueModel) element;
        return rule.getAttr().getName();
      }
    });
    // Add column selection listener
    attrNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrNameColumn.getColumn(), 0, this.depSorter, this.attrDepTabViewer));
  }

  /**
   * Column to display Attr Value
   */
  private void createAttrValCol() {
    final GridViewerColumn attrValCol = GridViewerColumnUtil.getInstance().createGridViewerColumn(this.attrDepTabViewer,
        "Attribute Value", DATASETS_DATE_COLUMN_WIDTH /* Column width */);
    attrValCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {

        AttributeValueModel rule = (AttributeValueModel) element;
        return rule.getValue().getName();
      }
    });
    // Add column selection listener
    attrValCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrValCol.getColumn(), 1, this.depSorter, this.attrDepTabViewer));
  }

  /**
   * Create ref value checkbox
   *
   * @param comp
   */
  private void createRefValChkBox(final Composite comp) {

    this.btnParamRefValMatch = new Button(comp, SWT.CHECK);
    this.btnParamRefValMatch.setToolTipText(CDRConstants.EXACT_MATCH_TO_REFERENCE_VALUE);
    this.btnParamRefValMatch.setText(CDRConstants.EXACT_MATCH_TO_REFERENCE_VALUE);
    this.btnParamRefValMatch.setEnabled(false);
  }

  /**
   * Create ready for series
   *
   * @param reviewdataComp
   */
  private void createMethodUIControls(final Composite reviewdataComp) {
    createLblControl(reviewdataComp, "Ready for Series", "");
    this.txtMethodValue = createStyledText(reviewdataComp);
    this.txtMethodValue.setEditable(false);

  }

  /**
   * Create hint control
   *
   * @param reviewdataComp
   */
  private void createHintUIControls(final Composite reviewdataComp) {
    createLblControl(reviewdataComp, CommonUIConstants.REMARK_VALUE, "");
    // ICDM-759
    this.txtHintValue = new StyledText(reviewdataComp,
        SWT.MULTI | SWT.BORDER | SWT.SCROLL_LINE | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
    createGridDataForStyledText(this.txtHintValue);
  }

  /**
   * Create ref val ui control
   *
   * @param reviewdataComp
   */
  private void createRefValUIControls(final Composite reviewdataComp) {
    createLblControl(reviewdataComp, CommonUIConstants.REF_VALUE, "");
    this.txtRefValue = createStyledText(reviewdataComp);
    this.txtRefValue.setEditable(false);


    int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;
    final DragSource source = new DragSource(this.txtRefValue, operations);
    source.setTransfer(new Transfer[] { LocalSelectionTransfer.getTransfer() });
    source.addDragListener(new DragSourceListener() {

      @Override
      public void dragStart(final DragSourceEvent event) {
        String value = RulesHistoryViewPart.this.txtRefValue.getText();
        if ((value.isEmpty() || "n.a.".equalsIgnoreCase(value)) || value.endsWith("%")) {
          event.doit = false;
        }
        else {
          event.doit = true;
        }
      }

      @Override
      public void dragSetData(final DragSourceEvent event) {
        IStructuredSelection selection =
            (IStructuredSelection) RulesHistoryViewPart.this.ruleVersionsGridTableViewer.getSelection();
        ReviewRule cdrRule = (ReviewRule) (selection.getFirstElement());
        CalData calData = ReviewRuleUtil.getRefValue(cdrRule);
        if (null != calData) {
          String currentUser = null;
          try {
            currentUser = new CurrentUserBO().getUser().getName();
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
          }
          final CalData calDataObject =
              CalDataUtil.getCalDataHistoryDetails(currentUser, calData, "Reference Value", cdrRule, null);
          event.data = calDataObject;
          final SeriesStatisticsInfo calDataProvider = new SeriesStatisticsInfo(calDataObject, CalDataType.REF_VALUE);
          calDataProvider.setDataSetName(RulesHistoryViewPart.this.selectedRule.getDependenciesForDisplay());
          StructuredSelection structuredSelection = new StructuredSelection(calDataProvider);
          LocalSelectionTransfer.getTransfer().setSelection(structuredSelection);
        }
      }

      @Override
      public void dragFinished(final DragSourceEvent event) {
        // TODO:
      }
    });


  }

  /**
   * create unit ui
   *
   * @param reviewdataComp
   */
  private void createUnitUIControls(final Composite reviewdataComp) {
    createLblControl(reviewdataComp, CommonUIConstants.UNIT_VALUE, "");
    this.txtUnitValue = createStyledText(reviewdataComp);
    this.txtUnitValue.setEditable(false);

  }

  /**
   * create upper limit ui
   *
   * @param reviewdataComp
   */
  private void createUpperLimitUIControls(final Composite reviewdataComp) {
    createLblControl(reviewdataComp, CommonUIConstants.UPPER_LIMIT_VALUE, "");
    this.txtUpperLmtValue = createStyledText(reviewdataComp);
    this.txtUpperLmtValue.setEditable(false);

  }

  /**
   * create lowerlimit ui
   *
   * @param reviewdataComp
   */
  private void createLowerLimitUIControls(final Composite reviewdataComp) {
    createLblControl(reviewdataComp, CommonUIConstants.LOWER_LIMIT_VALUE, "");
    this.txtLowerLmtValue = createStyledText(reviewdataComp);
    this.txtLowerLmtValue.setEditable(false);

  }

  /**
   * @param reviewdataComp
   */
  private StyledText createStyledText(final Composite reviewdataComp) {
    final StyledText styledTxt = new StyledText(reviewdataComp, SWT.SINGLE | SWT.BORDER);
    styledTxt.setLayoutData(GridDataUtil.getInstance().getWidthHintGridData(TEXT_FIELD_WIDTHHINT));
    return styledTxt;
  }

  /**
   * This method create Label instance for review info values
   *
   * @param reviewdataComp
   * @param lblName
   */
  private void createLblControl(final Composite reviewdataComp, final String lblName, final String toolTip) {
    final Label createLabel = LabelUtil.getInstance().createLabel(this.toolkit, reviewdataComp, lblName);
    createLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    createLabel.setToolTipText(toolTip);
  }

  /**
   * This method creates empty space in UI
   *
   * @param caldataComp
   */
  private void createEmptySpaceControl(final Composite caldataComp) {
    getNewLabel(caldataComp, SWT.NONE);
  }

  /**
   * This method creates scrolled form
   *
   * @param parent
   */
  private void createScrolledForm(final Composite parent) {
    // Create scrolled form
    this.scrolledForm = this.toolkit.createScrolledForm(parent);
    // Set decorate form heading to form
    this.toolkit.decorateFormHeading(this.scrolledForm.getForm());
    // Set layout to scrolledForm
    final GridLayout gridLayout = new GridLayout();
    gridLayout.makeColumnsEqualWidth = false;
    gridLayout.numColumns = 3;
    this.scrolledForm.getBody().setLayout(gridLayout);
    // Set grid layout data to scrolledForm
    this.scrolledForm.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());
  }


  /**
   * fills the UI controls for first section
   *
   * @param ruleManager ruleManager
   * @param rule rule
   * @param showCompRuleHist isCompli history flag
   */
  public void fillRuleUISection(final ParamCollection paramCollection, final ReviewRule rule,
      final boolean showCompRuleHist) {
    List<ReviewRule> rules = new ArrayList<ReviewRule>();
    this.selectedRuleForHist = rule;
    rules.add(rule);
    this.calDataTableGraphComposite.clearTableGraph();
    this.complexRuleBtn.setVisible(false);
    this.complexRuleLabel.setVisible(false);
    this.isCompliance = showCompRuleHist;
    List<ReviewRuleExt> ruleHistory = null;
    ReviewRuleParamCol paramCol = new ReviewRuleParamCol();
    paramCol.setParamCollection(paramCollection);
    paramCol.setReviewRule(rule);
    ReviewRuleServiceClient serviceClient = new ReviewRuleServiceClient();
    RuleSetRuleServiceClient ruleSetRuleServiceClient = new RuleSetRuleServiceClient();

    try {
      if (showCompRuleHist) {
        // Rule Mgr is not required for COMPLI rule history : ALM-281562
        if (paramCollection instanceof Function) {
          ruleHistory = serviceClient.getCompliRuleHistory(paramCol);
        }
        else if (paramCollection instanceof RuleSet) {
          ruleHistory = ruleSetRuleServiceClient.getCompliRuleHistory(paramCol);
        }
      }
      else {
        if (paramCollection instanceof Function) {
          ruleHistory = serviceClient.getRuleHistory(paramCol);
        }
        else if (paramCollection instanceof RuleSet) {
          ruleHistory = ruleSetRuleServiceClient.getRuleHistory(paramCol);
        }
      }

    }
    catch (ApicWebServiceException excep) {
      CDMLogger.getInstance().errorDialog(excep.getMessage(), excep, Activator.PLUGIN_ID);
    }


    this.ruleVersionsGridTableViewer.setInput(ruleHistory);
    if (null != ruleHistory) {
      for (ReviewRule ruleObj : ruleHistory) {
        if (rule.getRevId().equals(ruleObj.getRevId())) {
          this.ruleVersionsGridTableViewer.setSelection(new StructuredSelection(ruleObj), true);

        }
      }
    }


    this.ruleVersionsGridTableViewer.refresh();

  }


  /**
   * Fill the Attr Dependencies for the Rule.
   *
   * @param rule
   */
  private void fillAttrDepForRule(final ReviewRule rule) {
    this.attrDepTabViewer.setInput(rule.getDependencyList());
    this.attrDepTabViewer.refresh();
  }


  /**
   * fills the UI controls for second section
   *
   * @param cdrRule
   */
  private void fillUISection2(final ReviewRuleExt cdrRule) {

    // set lower limit
    fillLowerLmtUI(cdrRule);
    // set ref method
    fillRefMtdUI(cdrRule);
    // set upper limit
    fillUpperLmtUI(cdrRule);
    // set bitwise limit
    fillBitwiseLmtUI(cdrRule);
    // if rule is complex , display the feils and set value
    if ((cdrRule.getFormulaDesc() != null) || (cdrRule.getFormula() != null)) {
      this.complexRuleLabel.setVisible(true);
      this.complexRuleBtn.setVisible(true);
    }
    else {
      this.complexRuleLabel.setVisible(false);
      this.complexRuleBtn.setVisible(false);
    }
    // set unit
    fillUnitValUI(cdrRule);
    // set ref value
    fillRefValUI(cdrRule);
    // set exact match flag
    fillExactMatchToRefVal(cdrRule);
    // set hint
    fillHintUI(cdrRule);
    // set unicode remarks
    fillRemarksUnicodeUI(cdrRule);
    // set rule owner info
    fillRuleOwnerInfoUI(cdrRule);
    // set CoC info
    fillCocInfoUI(cdrRule);
    // set Internal Adaptation Description
    fillIntAdaptDescInfoUI(cdrRule);
    // set Data Description
    fillDataDescInfoUI(cdrRule);
    // set Historie Description
    fillHistDescInfoUI(cdrRule);
  }


  /**
   * @param cdrRule
   */
  private void fillHistDescInfoUI(final ReviewRuleExt cdrRule) {
    if (!this.txtHintValue.isDisposed()) {
      if (cdrRule.getHistorieDescription() != null) {
        this.histDescTxt.setText(cdrRule.getHistorieDescription());
      }
      else {
        this.histDescTxt.setText("");
      }
    }
  }


  /**
   * @param cdrRule
   */
  private void fillDataDescInfoUI(final ReviewRuleExt cdrRule) {
    if (!this.txtHintValue.isDisposed()) {
      if (cdrRule.getDataDescription() != null) {
        this.dataDescTxt.setText(cdrRule.getDataDescription());
      }
      else {
        this.dataDescTxt.setText("");
      }
    }
  }


  /**
   * @param cdrRule
   */
  private void fillIntAdaptDescInfoUI(final ReviewRuleExt cdrRule) {
    if (!this.txtHintValue.isDisposed()) {
      if (cdrRule.getInternalAdaptationDescription() != null) {
        this.intAdaptDescTxt.setText(cdrRule.getInternalAdaptationDescription());
      }
      else {
        this.intAdaptDescTxt.setText("");
      }
    }
  }


  /**
   * @param cdrRule
   */
  private void fillCocInfoUI(final ReviewRuleExt cdrRule) {
    if (!this.txtHintValue.isDisposed()) {
      if (cdrRule.getCoc() != null) {
        this.cocTxt.setText(cdrRule.getCoc());
      }
      else {
        this.cocTxt.setText("");
      }
    }
  }


  /**
   * @param cdrRule
   */
  private void fillRuleOwnerInfoUI(final ReviewRuleExt cdrRule) {
    if (!this.txtHintValue.isDisposed()) {
      if (cdrRule.getRuleOwner() != null) {
        this.ruleOwnerTxt.setText(cdrRule.getRuleOwner());
      }
      else {
        this.ruleOwnerTxt.setText("");
      }
    }
  }


  /**
   * ICDM 734
   *
   * @param cdrRule CDRRule
   */
  private void fillExactMatchToRefVal(final ReviewRule cdrRule) {

    this.btnParamRefValMatch.setSelection(cdrRule.isDcm2ssd());

  }


  /**
   * @param cdrRule
   */
  private void fillHintUI(final ReviewRule cdrRule) {
    if (!this.txtHintValue.isDisposed()) {
      if (cdrRule.getHint() != null) {
        this.txtHintValue.setText(cdrRule.getHint());
      }
      else {
        this.txtHintValue.setText("");
      }
      removeListenerFromTextField(this.txtHintValue);
      addListener(this.txtHintValue, null);
    }
  }

  /**
   * @param cdrRule
   */
  private void fillRemarksUnicodeUI(final ReviewRule cdrRule) {
    if (!this.txtHintValue.isDisposed()) {
      this.txtRemarkUnicodeValue.setText(CommonUtils.checkNull(cdrRule.getUnicodeRemarks()));
      removeListenerFromTextField(this.txtRemarkUnicodeValue);
      addListener(this.txtRemarkUnicodeValue, null);
    }
  }

  /**
   * @param cdrRule
   */
  private void fillRefValUI(final ReviewRule cdrRule) {
    if (!this.txtRefValue.isDisposed()) {
      this.txtRefValue.setText(cdrRule.getRefValueDispString());
      // ICDM-1194
      RuleMaturityLevel maturityLevel = RuleMaturityLevel.getIcdmMaturityLvlEnumForSsdText(cdrRule.getMaturityLevel());
      this.txtRefValue
          .setBackground(com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils.getMaturityLevelColor(maturityLevel));
      this.txtRefValue.setToolTipText(maturityLevel.getICDMMaturityLevel());

      removeListenerFromTextField(this.txtRefValue);
      if (null != cdrRule.getRefValueCalData()) {
        addListener(this.txtRefValue, ReviewRuleUtil.getRefValue(cdrRule).getCalDataPhy());
      }
      else {
        addListener(this.txtRefValue, null);
      }
    }
  }

  /**
   * @param cdrRule
   */
  private void fillUnitValUI(final ReviewRule cdrRule) {
    if (!this.txtUnitValue.isDisposed()) {
      if (cdrRule.getUnit() != null) {
        this.txtUnitValue.setText(cdrRule.getUnit());
      }
      else {
        this.txtUnitValue.setText("");
      }
      removeListenerFromTextField(this.txtUnitValue);
      addListener(this.txtUnitValue, null);
    }
  }


  /**
   * @param cdrRule
   */
  private void fillUpperLmtUI(final ReviewRule cdrRule) {
    if (!this.txtUpperLmtValue.isDisposed()) {
      if (cdrRule.getUpperLimit() != null) {
        this.txtUpperLmtValue.setText(cdrRule.getUpperLimit().toString());
      }
      else {
        this.txtUpperLmtValue.setText("");
      }
      removeListenerFromTextField(this.txtUpperLmtValue);
      addListener(this.txtUpperLmtValue, null);
    }
  }


  /**
   * @param cdrRule
   */
  private void fillRefMtdUI(final ReviewRule cdrRule) {
    if (!this.txtMethodValue.isDisposed()) {
      if (cdrRule.getReviewMethod() != null) {
        if (ApicConstants.READY_FOR_SERIES.YES.dbType.equals(cdrRule.getReviewMethod())) {
          this.txtMethodValue.setText(ApicConstants.READY_FOR_SERIES.YES.uiType);
        }
        else if (ApicConstants.READY_FOR_SERIES.NO.dbType.equals(cdrRule.getReviewMethod())) {
          this.txtMethodValue.setText(ApicConstants.READY_FOR_SERIES.NO.uiType);
        }
      }
      else {
        this.txtMethodValue.setText("");
      }
      removeListenerFromTextField(this.txtMethodValue);
      addListener(this.txtMethodValue, null);
    }
  }


  /**
   * @param cdrRule
   */
  private void fillLowerLmtUI(final ReviewRule cdrRule) {
    if (!this.txtLowerLmtValue.isDisposed()) {
      if (cdrRule.getLowerLimit() != null) {
        this.txtLowerLmtValue.setText(cdrRule.getLowerLimit().toPlainString());
      }
      else {
        this.txtLowerLmtValue.setText("");
      }
      removeListenerFromTextField(this.txtLowerLmtValue);
      addListener(this.txtLowerLmtValue, null);
    }
  }


  /**
   * @param cdrRule
   */
  private void fillBitwiseLmtUI(final ReviewRule cdrRule) {
    if (!this.bitwiseRuleTxt.isDisposed()) {
      if (cdrRule.getBitWiseRule() == null) {
        this.bitwiseRuleTxt.setText("");
      }
      else {
        this.bitwiseRuleTxt.setText(cdrRule.getBitWiseRule());
      }
      removeListenerFromTextField(this.bitwiseRuleTxt);
      addListener(this.bitwiseRuleTxt, null);
    }
  }

  private void removeListenerFromTextField(final StyledText txtLowerLmtValue2) {
    final Map<Integer, Listener> listenerMap = this.textListenerMap.get(txtLowerLmtValue2);
    if (listenerMap != null) {
      for (Entry<Integer, Listener> listenerMapEntry : listenerMap.entrySet()) {
        txtLowerLmtValue2.removeListener(listenerMapEntry.getKey(), listenerMapEntry.getValue());
      }
    }
  }

  private void addListener(final StyledText text, final CalDataPhy calDataPhy) {


    // Listens the activation of the text fields
    final Listener activateListener = new Listener() {

      @Override
      public void handleEvent(final Event event) {

        if (null != calDataPhy) {
          String value = text.getText().trim();
          // Following is additional check, were the trigger for
          // displaying values is not applicable
          if (!((value.isEmpty() || "n.a.".equalsIgnoreCase(value)) || value.endsWith("%"))) {
            text.setSelection(0, value.length());
            text.showSelection();
          }
        }
      }
    };

    final Listener graphListener = new Listener() {

      @Override
      public void handleEvent(final Event event) {

        if (null != calDataPhy) {
          String value = text.getText().trim();
          // Following is additional check, were the trigger for
          // displaying values is not applicable
          if ((value.isEmpty() || "n.a.".equalsIgnoreCase(value)) || value.endsWith("%")) {
            RulesHistoryViewPart.this.calDataTableGraphComposite.clearTableGraph();
          }
          else {


            // Display the values of the CalDataPhy
            try {
              RulesHistoryViewPart.this.calDataTableGraphComposite.fillTableAndGraph(calDataPhy);
            }
            catch (CalDataTableGraphException exp) {
              CDMLogger.getInstance().error("Text values not supported in Graph", exp, Activator.PLUGIN_ID);
            }
          }
        }
        else {
          RulesHistoryViewPart.this.calDataTableGraphComposite.clearTableGraph();
        }

      }
    };
    text.addListener(SWT.CURSOR_APPSTARTING, activateListener);
    text.addListener(SWT.Activate, graphListener);


    Map<Integer, Listener> listenerMap = this.textListenerMap.get(text);
    if (listenerMap == null) {
      listenerMap = new HashMap<>();
      this.textListenerMap.put(text, listenerMap);
    }
    listenerMap.put(SWT.Activate, graphListener);
    listenerMap.put(SWT.CURSOR_APPSTARTING, activateListener);


    Listener[] deActivateListeners = text.getListeners(SWT.Deactivate);

    if (deActivateListeners.length == 0) {
      text.addListener(SWT.Deactivate, new Listener() {

        @Override
        public void handleEvent(final Event event) {
          text.setSelection(0);
          text.showSelection();
        }
      });
    }

  }


  /**
   * This method creates review information UI form section
   *
   * @return Section
   */
  private Section createReviewdataSection() {
    // Creates review info Section
    return createSection(CommonUIConstants.RULE_DATA_TITLE, false);
  }

  @Override
  public void setFocus() {
    // TODO Auto-generated method stub

  }

  /**
   * clears the UI data
   */
  public void resetUIControls() {

    this.calDataTableGraphComposite.clearTableGraph();
    if (!this.txtLowerLmtValue.isDisposed()) {
      this.txtLowerLmtValue.setText("");
    }
    if (!this.txtMethodValue.isDisposed()) {
      this.txtMethodValue.setText("");
    }
    if (!this.txtUpperLmtValue.isDisposed()) {
      this.txtUpperLmtValue.setText("");
    }
    if (!this.txtUnitValue.isDisposed()) {
      this.txtUnitValue.setText("");
    }
    if (!this.txtRefValue.isDisposed()) {
      this.txtRefValue.setText("");
      this.txtRefValue.setBackground(null);
    }
    if (!this.txtHintValue.isDisposed()) {
      this.txtHintValue.setText("");
    }
    if (!this.bitwiseRuleTxt.isDisposed()) {
      this.bitwiseRuleTxt.setText("");
    }
    this.attrDepTabViewer.setInput(null);

  }


  /**
   * @return the isCompliance
   */
  public boolean isCompliance() {
    return this.isCompliance;
  }


  /**
   * @param isCompliance the isCompliance to set
   */
  public void setCompliance(final boolean isCompliance) {
    this.isCompliance = isCompliance;
  }


  /**
   * @return the scrolledForm
   */
  public ScrolledForm getScrolledForm() {
    return this.scrolledForm;
  }


  /**
   * @param parent lable
   * @param style style
   * @return the label of given style
   */
  private Label getNewLabel(final Composite parent, final int style) {
    Label lable = new Label(parent, style);
    return lable;
  }


}
