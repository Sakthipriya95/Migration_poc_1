/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang.WordUtils;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.jface.gridviewer.CheckEditingSupport;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.actions.PIDCActionSet;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.actions.CdrActionSet;
import com.bosch.caltool.cdr.ui.dialogs.ReviewDescEditDialog;
import com.bosch.caltool.cdr.ui.dialogs.ReviewInfoCommentDialog;
import com.bosch.caltool.cdr.ui.dialogs.SimplifiedGeneralQnaireDialog;
import com.bosch.caltool.cdr.ui.editors.ReviewResultEditor;
import com.bosch.caltool.cdr.ui.sorters.RvwAttrValTabSorter;
import com.bosch.caltool.cdr.ui.sorters.RvwParticipantSorter;
import com.bosch.caltool.cdr.ui.table.filters.RvwAttrValFilter;
import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultBO;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.dialogs.UserSelectionDialog;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_USER_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRResultFunction;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.ReviewInfoWpDefDetails;
import com.bosch.caltool.icdm.model.cdr.RvwAttrValue;
import com.bosch.caltool.icdm.model.cdr.RvwFile;
import com.bosch.caltool.icdm.model.cdr.RvwParticipant;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewActionSet;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;
import com.bosch.caltool.icdm.ruleseditor.utils.Messages;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRReviewResultServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwFileServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwParticipantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.nebula.gridviewer.CustomGridTableViewer;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextBoxContentDisplay;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * Third page of ReviewResult editor
 *
 * @author mkl2cob
 */
public class ReviewResultInfoPage extends AbstractFormPage {


  /**
   *
   */
  private static final int CONFIRM_MSG_LENGTH = 140;

  /**
   * Constant string for edit auditor
   */
  private static final String EDIT_AUDITOR = "Edit Auditor";

  /**
   * Constant string for download file
   */
  private static final String DOWNLOAD_FILE = "Download File";

  /**
   * CONSTANT for message
   */
  private static final String REVIEW_TYPE_CANNOT_BE_MODIFIED = "Review type cannot be modified.";

  /**
   * General section columns
   */
  private static final int GENERAL_SEC_COLS = 3;

  /**
   * Constant for the row count
   */

  private static final int LIST_ROW_COUNT = 5;


  /**
   * Constant for File review Control
   */
  private static final int LIST_REV_CON = 3;
  /**
   * instance of formtoolkit
   */
  private FormToolkit formToolkit;
  /**
   * Composite for review information
   */
  private Composite compositeTwo;
  /**
   * Section to hold review information form
   */
  private Section reviewInfoSection;
  /**
   * Form to hold review information
   */
  private ScrolledForm reviewInfoForm;
  /**
   * Group which holds the review information
   */
  private final CDRReviewResult cdrResult;

  private ScrolledForm scrollableForm;

  private Button deleteUserButton;
  private Button deleteButton;
  private Button downloadButton;
  private List<RvwFile> selectedIcdmFile = new ArrayList<>();
  private org.eclipse.swt.widgets.List filesList;

  private final Map<String, RvwFile> attachmentsMap = new HashMap<>();

  private org.eclipse.swt.widgets.List filesReviewedList;


  private final Map<String, RvwFile> fileReviewMap = new HashMap<>();

  // Download button for the Input files
  private Button downloadInpButton;

  // Array List for the Input Files
  private List<RvwFile> selectedInputFile;

  /**
   * ICDM 635 List to display internal files
   */
  private org.eclipse.swt.widgets.List internalFilesList;

  /**
   * Download or export button for the Internal files
   */
  private Button exportFileButton;
  /**
   * Hash map for internal files
   */
  private final Map<String, RvwFile> intfileMap = new HashMap<>();
  /**
   * Reference to store selected internal file
   */
  protected List<RvwFile> selectedIntFiles;

  private Section fileInfoSection;

  private Form fileInfoForm;

  private Section userInfoSection;

  private Form userInfoForm;

  private Text rvwStatusTxt;

  private Text commentTxt;

  private org.eclipse.swt.widgets.List inputFilesList;

  private List<RvwFile> selectedLabFiles;

  private final Map<String, RvwFile> inpFileMap = new HashMap<>();

  private Button exportLabFileButton;

  private Text auditorTxt;

  private Text variantName;

  private final ReviewResultEditor editor;

  /**
   * List of selected participants
   */
  protected RvwParticipant[] selectedParticipants;


  private RvwParticipantSorter rvwParticipantSorter;

  private final Image downloadImg;
  private Text descTxt;

  private Section attrValSection;

  private Form attrValForm;

  private GridTableViewer attrValTabViewer;

  private GridTableViewer otherParticipantTabViewer;

  private Text filterTxt;

  private RvwAttrValFilter attrValFilter;

  private RvwAttrValTabSorter attrValTabSorter;

  private Button rvwCheck;

  private Button editBtn;

  private Button editAuditorButton;

  private Button addButton;

  private Button addFileButton;

  private Button testRevRadio;

  private Button offRevRadio;

  /**
   * Column size of Attr Val Table Column
   */
  private static final int ATTR_VAL_COL_SIZE = 200;

  /**
   * Font name Constant
   */
  private static final String SEOGE_FONT_NAME = "Segoe UI";

  private Text calEngNameTxt;

  /**
   * ICDM-1746
   */

  private Button startRevRadio;

  ReviewResultClientBO resultData;

  private final ReviewResultBO resultBo;

  private Text pidcNameTxt;

  private Button editCommentBtn;

  private Button noOBDLabelRadio;

  private Button onlyOBDLabelRadio;

  private Button bothOBDAndNonOBDLabelRadio;

  private Label simpGnrlQnaireControlLabel;

  private Button simpGnrlQnaireControlButton;

  private Label simpGnrlQnaireFillerLabel;

  private SimplifiedGeneralQnaireDialog simpGnrlQnaireDialog;

  /**
   * Return code for Ok pressed
   */
  private static final int CODE_FOR_OK = 0;

  /**
   * @param editor ReviewResultEditor
   */
  public ReviewResultInfoPage(final ReviewResultEditor editor) {
    super(editor, "Functions", Messages.getString("ReviewInfoPage.label")); //$NON-NLS-1$ //$NON-NLS-2$
    this.editor = editor;
    this.downloadImg = ImageManager.getInstance().getRegisteredImage(ImageKeys.DOWNLOAD_16X16);
    this.resultData = this.editor.getEditorInput().getResultData();
    this.resultBo = this.resultData.getResultBo();
    this.cdrResult = this.resultBo.getCDRResult();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    Map<IModelType, Map<Long, ChangeData<?>>> consChangeData = dce.getConsChangeData();
    if (isWidgetNotDisposed()) {
      for (Entry<IModelType, Map<Long, ChangeData<?>>> entry : consChangeData.entrySet()) {
        Map<Long, ChangeData<?>> map = entry.getValue();
        if (entry.getKey() == MODEL_TYPE.CDR_RES_FILE) {
          refreshCdrFile();
        }
        else if (entry.getKey() == MODEL_TYPE.CDR_PARTICIPANT) {
          refreshCdrParticipant(map);
        }
        else if (entry.getKey() == MODEL_TYPE.CDR_RESULT) {
          refreshCdrResult();
        }
        else if ((entry.getKey() == MODEL_TYPE.PIDC_VERSION) && (this.pidcNameTxt != null)) {

          this.pidcNameTxt.setText(ReviewResultInfoPage.this.resultBo.getPidcVersion().getName());
        }
        else if ((entry.getKey() == MODEL_TYPE.VARIANT)) {
          this.variantName.setText(getVariantDetails());
          this.variantName.setToolTipText(WordUtils.wrap(this.variantName.getText(), 100, "\n", true));
        }
      }
      if (getPartControl() != null) {
        updateEditableFieldStatus();
        setSelForLock();
      }
      if (null != this.attrValTabViewer) {
        this.attrValTabViewer.refresh();
      }
    }
  }


  /**
   *
   */
  private void refreshCdrFile() {
    final SortedSet<RvwFile> attachments = ReviewResultInfoPage.this.resultBo.getAdditionalFiles();
    fillFileListInput(this.filesList, attachments);
    if (getPartControl() != null) {
      this.deleteButton.setEnabled(false);
      this.downloadButton.setEnabled(false);
    }
  }


  /**
   *
   */
  private void refreshCdrResult() {
    if (getPartControl() != null) {
      setOldReviewType();
      setRadioButtonVal();
      hideUnhideSimpGnrlQnaireControl(this.reviewInfoForm.getBody());
      setSimplifiedGeneralQnaireDialogContent(ReviewResultInfoPage.this.cdrResult);
      ReviewResultInfoPage.this.descTxt.setText(ReviewResultInfoPage.this.cdrResult.getDescription());
      ReviewResultInfoPage.this.editor.setEditorPartName(ReviewResultInfoPage.this.cdrResult.getName());
      ReviewResultInfoPage.this.commentTxt.setText(ReviewResultInfoPage.this.cdrResult.getComments() == null ? ""
          : ReviewResultInfoPage.this.cdrResult.getComments());
      this.editor.getReviewResultParamListPage().reconstructNatTable();
    }
  }


  /**
   * @param map
   */
  private void refreshCdrParticipant(final Map<Long, ChangeData<?>> map) {
    if (ReviewResultInfoPage.this.resultBo.getAuditor() != null) {
      ReviewResultInfoPage.this.auditorTxt.setText(ReviewResultInfoPage.this.resultBo.getAuditor().getName());
    }
    RvwParticipant participant = null;
    for (ChangeData<?> changeData : map.values()) {
      if ((changeData.getChangeType() == CHANGE_OPERATION.CREATE) ||
          (changeData.getChangeType() == CHANGE_OPERATION.UPDATE)) {
        participant = (RvwParticipant) changeData.getNewData();
      }
      else if (changeData.getChangeType() == CHANGE_OPERATION.DELETE) {
        participant = (RvwParticipant) changeData.getOldData();
      }
      break;
    }
    if ((participant != null) && REVIEW_USER_TYPE.ADDL_PARTICIPANT.getDbType().equals(participant.getActivityType())) {
      Collection<RvwParticipant> values = ReviewResultInfoPage.this.resultData.getResultBo().getOtherParticipants();
      this.otherParticipantTabViewer.setInput(values);
      this.otherParticipantTabViewer.refresh();
    }
  }

  /**
   * @return
   */
  private boolean isWidgetNotDisposed() {
    return (null != this.compositeTwo) && !this.compositeTwo.isDisposed();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {
    final FormToolkit toolkit = new FormToolkit(parent.getDisplay());

    this.scrollableForm = toolkit.createScrolledForm(parent);
    this.scrollableForm.setAlwaysShowScrollBars(true);
    final GridLayout layout = new GridLayout();
    this.scrollableForm.getBody().setLayout(layout);
    final GridData data = new GridData(
        GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_BEGINNING);
    this.scrollableForm.getBody().setLayoutData(data);
    this.scrollableForm.setText("Review Information");
    addHelpAction((ToolBarManager) this.scrollableForm.getToolBarManager());
    final ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);

    if (this.scrollableForm.getParent() != null) {
      this.scrollableForm.getParent().layout(true, true);
    }
  }

  /**
   * @param formToolkit2
   * @param managedForm
   * @param parent
   * @param gridData
   * @throws ApicWebServiceException
   */
  private void createReviewComposite() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.compositeTwo = this.scrollableForm.getBody();
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    this.compositeTwo.setLayout(gridLayout);
    this.compositeTwo.setLayoutData(gridData);
    createGenSection();
    createFilesSection();
    createAttrValSection();
    createPartSection();
  }


  /**
   *
   */
  private void createAttrValSection() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.attrValSection = SectionUtil.getInstance().createSection(this.compositeTwo, this.formToolkit, "");
    this.attrValSection.setLayoutData(gridData);
    this.attrValSection.setLayout(new GridLayout());
    createAttrValForm(this.formToolkit);
    this.attrValSection.setClient(this.attrValForm);
    this.attrValSection.getDescriptionControl().setEnabled(false);
    this.attrValSection.setText("Attribute and Values");

  }

  /**
   * @param toolkit
   */
  private void createAttrValForm(final FormToolkit toolkit) {
    this.attrValForm = toolkit.createForm(this.attrValSection);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessHorizontalSpace = true;
    final GridLayout gridLayout1 = new GridLayout();
    this.attrValForm.getBody().setLayout(gridLayout1);
    this.attrValForm.getBody().setLayoutData(gridData);
    this.attrValForm.getBody().setFont(new Font(this.attrValForm.getBody().getDisplay(), SEOGE_FONT_NAME, 8, SWT.BOLD));
    createAttrValContent();

  }

  /**
   * create the Attr Val Table Viewer
   */
  private void createAttrValContent() {
    createFilterTxt();

    this.attrValTabViewer = GridTableViewerUtil.getInstance().createCustomGridTableViewer(this.attrValForm.getBody(),
        SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI,
        new int[] { CommonUIConstants.COLUMN_INDEX_2 });
    this.attrValTabViewer.getControl().addListener(SWT.MouseVerticalWheel, this::respondToMouseWheelEvent);
    // activate the tooltip support for the viewer
    ColumnViewerToolTipSupport.enableFor(this.attrValTabViewer, ToolTip.NO_RECREATE);

    initializeEditorStatusLineManager(this.attrValTabViewer);
    createSorter(this.attrValTabViewer);
    createColumns();

    this.attrValFilter = new RvwAttrValFilter();
    this.attrValTabViewer.addFilter(this.attrValFilter);
    this.attrValTabViewer.setContentProvider(ArrayContentProvider.getInstance());
    // ICDM 555
    this.attrValTabViewer.setInput(this.resultBo.getRvwAttrValSet());

  }


  /**
   * @param attrValTabViewer attrValTabViewer
   */
  private void createSorter(final GridTableViewer attrValTabViewer) {

    this.attrValTabSorter = new RvwAttrValTabSorter(this.resultData);
    attrValTabViewer.setComparator(this.attrValTabSorter);

  }

  /**
   * Create the Filter text
   */
  private void createFilterTxt() {
    this.filterTxt = this.formToolkit.createText(this.attrValForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    final GridData gridData = getFilterTxtGridData();
    this.filterTxt.setLayoutData(gridData);
    this.filterTxt.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));

    this.filterTxt.addModifyListener((final ModifyEvent event) -> {
      final String text = ReviewResultInfoPage.this.filterTxt.getText().trim();
      ReviewResultInfoPage.this.attrValFilter.setFilterText(text);
      ReviewResultInfoPage.this.attrValTabViewer.refresh();

    });
  }

  /**
   * create the Attr Val Columns
   */
  private void createColumns() {
    final GridViewerColumn attrName = GridViewerColumnUtil.getInstance().createGridViewerColumn(this.attrValTabViewer,
        "Attribute Name", ATTR_VAL_COL_SIZE);

    attrName.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof RvwAttrValue) {
          return ((RvwAttrValue) element).getName();
        }
        return "";
      }
    });
    attrName.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrName.getColumn(), 0, this.attrValTabSorter, this.attrValTabViewer));

    final GridViewerColumn valName = GridViewerColumnUtil.getInstance().createGridViewerColumn(this.attrValTabViewer,
        "Attribute Value", ATTR_VAL_COL_SIZE);

    valName.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof RvwAttrValue) {
          return ((RvwAttrValue) element).getValueDisplay();
        }
        return "";
      }
    });

    valName.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(valName.getColumn(),
        1, this.attrValTabSorter, this.attrValTabViewer));

  }


  /**
   * Icdm -673 creating new Sections
   *
   * @throws ApicWebServiceException
   */
  private void createPartSection() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.userInfoSection = SectionUtil.getInstance().createSection(this.compositeTwo, this.formToolkit, "");
    this.userInfoSection.setLayoutData(gridData);
    this.userInfoSection.setLayout(new GridLayout());
    createUserInfoForm(this.formToolkit);
    this.userInfoSection.setClient(this.userInfoForm);
    this.userInfoSection.getDescriptionControl().setEnabled(false);
    this.userInfoSection.setText("Participants");

  }


  /**
   * This method creates section to hold review info
   */
  private void createGenSection() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.reviewInfoSection = SectionUtil.getInstance().createSection(this.compositeTwo, this.formToolkit, "");
    this.reviewInfoSection.setLayoutData(gridData);
    this.reviewInfoSection.setLayout(new GridLayout());
    createGenInfoForm(this.formToolkit);
    this.reviewInfoSection.setClient(this.reviewInfoForm);
    this.reviewInfoSection.getDescriptionControl().setEnabled(false);
    this.reviewInfoSection.setText("General");
  }

  /**
   *
   */
  private void createFilesSection() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.fileInfoSection = SectionUtil.getInstance().createSection(this.compositeTwo, this.formToolkit, "");
    this.fileInfoSection.setLayoutData(gridData);
    gridData.verticalSpan = 1;
    this.fileInfoSection.setLayout(new GridLayout());
    createFileInfoForm(this.formToolkit);
    this.fileInfoSection.setClient(this.fileInfoForm);
    this.fileInfoSection.getDescriptionControl().setEnabled(false);
    this.fileInfoSection.setText("Files");
  }

  /**
   * @param formToolkit2
   */
  private void createFileInfoForm(final FormToolkit toolkit) {
    this.fileInfoForm = toolkit.createForm(this.fileInfoSection);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    final GridLayout gridLayout1 = new GridLayout();
    gridLayout1.numColumns = 3;
    this.fileInfoForm.getBody().setLayout(gridLayout1);
    this.fileInfoForm.getBody().setLayoutData(gridData);
    this.fileInfoForm.getBody()
        .setFont(new Font(this.fileInfoForm.getBody().getDisplay(), SEOGE_FONT_NAME, 8, SWT.BOLD));
    createFileInfoContent();

  }

  @Override
  public Control getPartControl() {
    return this.scrollableForm;
  }

  /**
   * This method creates the form content
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    this.formToolkit = managedForm.getToolkit();
    createReviewComposite();
  }


  /**
   * @param toolkit Icdm-673
   * @throws ApicWebServiceException
   */
  private void createUserInfoForm(final FormToolkit toolkit) {
    this.userInfoForm = toolkit.createForm(this.userInfoSection);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessHorizontalSpace = true;
    final GridLayout gridLayout1 = new GridLayout();
    gridLayout1.numColumns = 3;
    this.userInfoForm.getBody().setLayout(gridLayout1);
    this.userInfoForm.getBody().setLayoutData(gridData);
    this.userInfoForm.getBody()
        .setFont(new Font(this.userInfoForm.getBody().getDisplay(), SEOGE_FONT_NAME, 8, SWT.BOLD));
    createUserContent();

  }


  /**
   * This method creates form to hold review info
   *
   * @param toolkit Icdm-673
   */
  private void createGenInfoForm(final FormToolkit toolkit) {
    this.reviewInfoForm = toolkit.createScrolledForm(this.reviewInfoSection);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessHorizontalSpace = true;
    final GridLayout gridLayout1 = new GridLayout();
    gridLayout1.numColumns = GENERAL_SEC_COLS;
    this.reviewInfoForm.getBody().setLayout(gridLayout1);
    this.reviewInfoForm.getBody().setLayoutData(gridData);
    this.reviewInfoForm.getBody()
        .setFont(new Font(this.reviewInfoForm.getBody().getDisplay(), SEOGE_FONT_NAME, 8, SWT.BOLD));
    createRightContent();
  }


  /**
   * this method creates review info form content Icdm-673
   */
  private void createRightContent() {

    Composite rvwScrolledForm = this.reviewInfoForm.getBody();

    createProjectUIControl(rvwScrolledForm);

    createVariantUIControl(rvwScrolledForm);

    createWPUIControl(rvwScrolledForm);

    createFunReviewControl(rvwScrolledForm);

    createParentReviewSection(rvwScrolledForm);

    createRuleSetCtrl(rvwScrolledForm);

    createA2lUIControl(rvwScrolledForm);
    // 489480
    createWpDefControl(rvwScrolledForm);


    // display OBD labels only if it is not empty
    if (CommonUtils.isNotNull(ReviewResultInfoPage.this.resultBo.getObdFlag()) &&
        CommonUtils.isNotEmptyString(ReviewResultInfoPage.this.resultBo.getObdFlag().toString())) {
      createOBDLabelsControl(rvwScrolledForm);
    }

    createSimplifiedGeneralQnaireControl(rvwScrolledForm);

    createDescControl(rvwScrolledForm);

    createRvwStatusControl(rvwScrolledForm);

    createRvwLockedControl(rvwScrolledForm);

    createReviewTypeControl(rvwScrolledForm);

    createCommentField(rvwScrolledForm);
  }


  /**
   * ICDM 610
   *
   * @param comp composite
   */
  private void createRuleSetCtrl(final Composite comp) {
    createLabelControl(comp, "Rule Set:");
    final Text nameTxt = createTextFileld(comp);
    nameTxt.setEnabled(true);
    if (null != this.resultBo.getRuleSet()) {
      nameTxt.setText(this.resultBo.getRuleSet().getName());
    }
    // Icdm-1349- Open Parent result From Review Infor page.
    Button openResBut = new Button(comp, SWT.PUSH);
    openResBut.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.RULE_SET_16X16));
    openResBut.setToolTipText("Open Result Set");
    if (this.resultBo.getRuleSet() == null) {
      openResBut.setEnabled(false);
    }
    openResBut.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        ReviewActionSet paramActionSet = new ReviewActionSet();
        try {
          paramActionSet.openRulesEditor(new ReviewParamEditorInput(ReviewResultInfoPage.this.resultBo.getRuleSet()),
              null);
        }
        catch (PartInitException excep) {
          CDMLogger.getInstance().error(excep.getLocalizedMessage(), excep, Activator.PLUGIN_ID);
        }
      }
    });
  }

  /**
   * Icdm-1355- Show the A2l File name in Review Information
   *
   * @param comp comp
   */
  private void createA2lUIControl(final Composite comp) {

    createLabelControl(comp, "A2L File:");
    final Text nameTxt = createTextFileld(comp);
    nameTxt.setEnabled(true);
    if (null != this.resultBo.getA2lFileName()) {
      nameTxt.setText(this.resultBo.getA2lFileName());
    }

    // Icdm-1349- Open A2l From Review Infor page.
    Button openA2lAction = new Button(comp, SWT.PUSH);
    openA2lAction.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.A2LFILE_16X16));
    openA2lAction.setToolTipText("Open A2L File");

    openA2lAction.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        CommonActionSet actionSet = new CommonActionSet();
        actionSet.openA2lFile(ReviewResultInfoPage.this.resultBo.getPidcA2l().getId());
      }
    });
  }

  /**
   * 489480 - Show WP definition version and variant group name in review result editor
   *
   * @param comp comp
   */
  private void createWpDefControl(final Composite comp) {
    createLabelControl(comp, "Work Package definition");
    Text wpDefnVersTxt;
    wpDefnVersTxt = createTextFileld(comp);
    wpDefnVersTxt.setEnabled(true);
    ReviewInfoWpDefDetails rvwInfoWpDefDetails =
        this.editor.getEditorInput().getResultData().getResponse().getRvwInfoWpDefDetails();
    String wpdefndetails = rvwInfoWpDefDetails.getWpDefVersName();
    if (null != rvwInfoWpDefDetails.getVariantGrpname()) {
      wpdefndetails = wpdefndetails + " >> " + rvwInfoWpDefDetails.getVariantGrpname();
    }
    wpDefnVersTxt.setText(null != wpdefndetails ? wpdefndetails : "");
    fillerLabels(comp, 1);
  }

  /**
   * Icdm-875 new method for Review Type control
   *
   * @param body
   */
  private void createReviewTypeControl(final Composite form) {

    final GridData lblgridData = GridDataUtil.getInstance().getGridData();
    LabelUtil.getInstance().createLabel(this.formToolkit, form, "Review Type:").setLayoutData(lblgridData);
    lblgridData.verticalAlignment = SWT.CENTER;

    final Composite reviewTypeComp = new Composite(form, SWT.NONE);
    final GridData gridData = GridDataUtil.getInstance().getGridData();

    gridData.verticalAlignment = SWT.CENTER;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = false;

    final GridLayout layout = new GridLayout();
    layout.numColumns = 3;

    reviewTypeComp.setLayoutData(gridData);
    reviewTypeComp.setLayout(layout);

    final GridData radioGridData = GridDataUtil.getInstance().getGridData();
    radioGridData.grabExcessHorizontalSpace = true;

    createStartRadioControl(reviewTypeComp, radioGridData);
    createOfficialRadioControl(reviewTypeComp, radioGridData);
    createTestRadioControl(reviewTypeComp, radioGridData);

    // Set which button has to be selected
    setRadioButtonVal();
    fillerLabels(form, 1);
  }


  private void createTestRadioControl(final Composite reviewTypeComp, final GridData radioGridData) {
    this.testRevRadio = new Button(reviewTypeComp, SWT.RADIO);
    this.testRevRadio.setLayoutData(radioGridData);
    this.testRevRadio.setText("Test");
    try {
      this.testRevRadio
          .setToolTipText(new CommonDataBO().getMessage(CDRConstants.REVIEW_TYPE_GROUP_NAME, "TOOLTIP_TEST_RVW"));
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
    }
    this.testRevRadio.addSelectionListener(new SelectionAdapter() {


      @Override
      public void widgetSelected(final SelectionEvent event) {
        selListenerForTestRevBtn();
      }
    });
  }

  /**
   *
   */
  private void selListenerForTestRevBtn() {
    if (ReviewResultInfoPage.this.testRevRadio.getSelection()) {

      try {
        boolean usedInCdfxDelivery = ReviewResultInfoPage.this.resultBo.isUsedInCDFXDelivery();
        if (ReviewResultInfoPage.this.resultBo.isModifiable() && !ReviewResultInfoPage.this.resultBo.isResultLocked() &&
            !usedInCdfxDelivery) {
          if ((ReviewResultInfoPage.this.resultBo.getReviewType() != REVIEW_TYPE.TEST) &&
              ReviewResultInfoPage.this.canChangeType()) {
            callUpdateCmd(CDRConstants.REVIEW_TYPE.TEST);
          }
        }
        else if (usedInCdfxDelivery) {
          CDMLogger.getInstance().infoDialog(new CommonDataBO().getMessage("REVIEW_RESULT", "USED_IN_CDFX_DELIVERY"),
              Activator.PLUGIN_ID);
          setOldReviewType();
        }
        else {
          CDMLogger.getInstance().infoDialog(REVIEW_TYPE_CANNOT_BE_MODIFIED, Activator.PLUGIN_ID);
        }
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
  }


  private void createOfficialRadioControl(final Composite reviewTypeComp, final GridData radioGridData) {
    this.offRevRadio = new Button(reviewTypeComp, SWT.RADIO);
    this.offRevRadio.setLayoutData(radioGridData);
    this.offRevRadio.setText("Official");
    try {
      this.offRevRadio
          .setToolTipText(new CommonDataBO().getMessage(CDRConstants.REVIEW_TYPE_GROUP_NAME, "TOOLTIP_OFFICIAL_RVW"));
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
    }
    this.offRevRadio.addSelectionListener(new SelectionAdapter() {


      @Override
      public void widgetSelected(final SelectionEvent event) {
        if (ReviewResultInfoPage.this.offRevRadio.getSelection() &&
            (ReviewResultInfoPage.this.resultBo.getReviewType() != REVIEW_TYPE.OFFICIAL)) {
          CDMLogger.getInstance().infoDialog(REVIEW_TYPE_CANNOT_BE_MODIFIED, Activator.PLUGIN_ID);
          setOldReviewType();
        }
      }

    });
  }


  private void createStartRadioControl(final Composite reviewTypeComp, final GridData radioGridData) {
    this.startRevRadio = new Button(reviewTypeComp, SWT.RADIO);
    this.startRevRadio.setLayoutData(radioGridData);
    this.startRevRadio.setText("Start");
    try {
      this.startRevRadio
          .setToolTipText(new CommonDataBO().getMessage(CDRConstants.REVIEW_TYPE_GROUP_NAME, "TOOLTIP_START_RVW"));
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    this.startRevRadio.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        if (ReviewResultInfoPage.this.startRevRadio.getSelection() &&
            (ReviewResultInfoPage.this.resultBo.getReviewType() != REVIEW_TYPE.START)) {
          CDMLogger.getInstance().infoDialog(REVIEW_TYPE_CANNOT_BE_MODIFIED, Activator.PLUGIN_ID);
          setOldReviewType();
        }
      }
    });
  }

  /**
   *
   */
  protected void setOldReviewType() {

    REVIEW_TYPE reviewType = ReviewResultInfoPage.this.resultBo.getReviewType();
    this.startRevRadio.setSelection(false);
    this.offRevRadio.setSelection(false);
    this.testRevRadio.setSelection(false);
    if (reviewType == REVIEW_TYPE.START) {
      this.startRevRadio.setSelection(true);
    }
    else if (reviewType == REVIEW_TYPE.OFFICIAL) {
      this.offRevRadio.setSelection(true);
    }
    else {
      this.testRevRadio.setSelection(true);
    }

  }

  /**
   * @return boolean
   */
  protected boolean canChangeType() {
    StringBuilder dialogMessage = new StringBuilder(CONFIRM_MSG_LENGTH);
    dialogMessage.append("Attention:\n").append("This change can not be reverted!\n")
        .append("It's not possible to switch from Testreview to Start or Official.\n")
        .append("Would you like to continue?");
    boolean confirmMessageDialog =
        MessageDialogUtils.getConfirmMessageDialogWithYesNo("Change review type", dialogMessage.toString());
    if (!confirmMessageDialog) {
      setOldReviewType();
    }
    return confirmMessageDialog;

  }

  // ICDM-1800
  /**
   * @return boolean
   */
  protected boolean checkOffRevValid() {
    if (CommonUtils.isEqualIgnoreCase(this.auditorTxt.getText(), this.calEngNameTxt.getText())) {
      CDMLogger.getInstance().errorDialog(
          "For official reviews, the 'Calibration Engineer' and 'Auditor' should be different users",
          Activator.PLUGIN_ID);
      setRadioButtonVal();
      return false;
    }
    return true;
  }

  /**
   * @param testRevRadio
   * @param offRevRadio
   */
  private void setRadioButtonVal() {
    this.testRevRadio.setSelection(false);
    this.startRevRadio.setSelection(false);
    this.offRevRadio.setSelection(false);
    if (ReviewResultInfoPage.this.resultBo.getReviewType() == CDRConstants.REVIEW_TYPE.TEST) {
      this.testRevRadio.setSelection(true);
    }
    else if (ReviewResultInfoPage.this.resultBo.getReviewType() == CDRConstants.REVIEW_TYPE.START) {// ICDM-1801
      this.startRevRadio.setSelection(true);
    }
    else {
      this.offRevRadio.setSelection(true);
    }

    // Check for the Current User and Enable or diable the Radio buttons
    if (ReviewResultInfoPage.this.resultBo.isModifiable() && !ReviewResultInfoPage.this.resultBo.isResultLocked()) {
      this.testRevRadio.setEnabled(true);
      this.offRevRadio.setEnabled(true);
      this.startRevRadio.setEnabled(true);
    }
    else {
      this.testRevRadio.setEnabled(false);
      this.offRevRadio.setEnabled(false);
      this.startRevRadio.setEnabled(false);
    }
  }

  /**
   * create OBD control and corresponding label
   *
   * @param comp - the parent compsoite
   */
  private void createOBDLabelsControl(final Composite comp) {
    // creating the label
    final GridData lblgridData = GridDataUtil.getInstance().getGridData();
    LabelUtil.getInstance().createLabel(this.formToolkit, comp, "OBD Option:").setLayoutData(lblgridData);
    lblgridData.verticalAlignment = SWT.TOP;

    // creating the obd composite to store the radio buttons
    final Composite oBDLabelsComposite = new Composite(comp, SWT.NONE);
    oBDLabelsComposite.setLayoutData(new GridData(GridData.FILL, SWT.TOP, true, false));
    oBDLabelsComposite.setLayout(new GridLayout());
    oBDLabelsComposite.setEnabled(false);

    final GridData radioGridData = GridDataUtil.getInstance().getGridData();

    // create all the OBD labels
    createNoOBDLabelRadioControl(oBDLabelsComposite, radioGridData);
    createOnlyOBDLabelRadioControl(oBDLabelsComposite, radioGridData);
    createBothOBDAndNonOBDLabelRadioControl(oBDLabelsComposite, radioGridData);

    // Set which button has to be selected while loading
    setRadioButtonValForOBDLabels();
    // the layout of the general info body is of 3 columns and since this entire OBD fields requires only 2 columns we
    // define the last 1 as empty so that the next element view is not affected
    fillerLabels(comp, 1);
  }

  /**
   * create No OBD Label radio button
   *
   * @param oBDLabelsComposite - the parent compsoite, radioGridData - grid data styling for the radio button
   */
  private void createNoOBDLabelRadioControl(final Composite oBDLabelsComposite, final GridData radioGridData) {
    this.noOBDLabelRadio = new Button(oBDLabelsComposite, SWT.RADIO);
    this.noOBDLabelRadio.setLayoutData(radioGridData);
    this.noOBDLabelRadio.setText("No OBD Labels");
    this.noOBDLabelRadio.setEnabled(false);
  }

  /**
   * create Only OBD Label radio button
   *
   * @param oBDLabelsComposite - the parent compsoite, radioGridData - grid data styling for the radio button
   */
  private void createOnlyOBDLabelRadioControl(final Composite oBDLabelsComposite, final GridData radioGridData) {
    this.onlyOBDLabelRadio = new Button(oBDLabelsComposite, SWT.RADIO);
    this.onlyOBDLabelRadio.setLayoutData(radioGridData);
    this.onlyOBDLabelRadio.setText("Only OBD Labels");
    this.onlyOBDLabelRadio.setEnabled(false);
  }

  /**
   * create Both OBD and Non-OBD Labels radio button
   *
   * @param oBDLabelsComposite - the parent compsoite, radioGridData - grid data styling for the radio button
   */
  private void createBothOBDAndNonOBDLabelRadioControl(final Composite oBDLabelsComposite,
      final GridData radioGridData) {
    this.bothOBDAndNonOBDLabelRadio = new Button(oBDLabelsComposite, SWT.RADIO);
    this.bothOBDAndNonOBDLabelRadio.setLayoutData(radioGridData);
    this.bothOBDAndNonOBDLabelRadio.setText("Both OBD and Non-OBD Labels");
    this.bothOBDAndNonOBDLabelRadio.setEnabled(false);
  }

  /**
   * sets the value for the OBD radio btns
   */
  private void setRadioButtonValForOBDLabels() {
    if (CommonUtils.isEqual(ReviewResultInfoPage.this.resultBo.getObdFlag(), CDRConstants.OBD_OPTION.NO_OBD_LABELS)) {
      setOBDRadioSelection(false, true, false);
    }
    else if (CommonUtils.isEqual(ReviewResultInfoPage.this.resultBo.getObdFlag(),
        CDRConstants.OBD_OPTION.ONLY_OBD_LABELS)) {
      setOBDRadioSelection(true, false, false);
    }
    else if (CommonUtils.isEqual(ReviewResultInfoPage.this.resultBo.getObdFlag(),
        CDRConstants.OBD_OPTION.BOTH_OBD_AND_NON_OBD_LABELS)) {
      setOBDRadioSelection(false, false, true);
    }
  }

  /**
   * set OBD radio btn values
   *
   * @param enbOnlyOBDRadioBtn
   * @param enbNoOBDRadioBtn
   * @param enbBothOBDAndNonOBDRadioBtn
   */
  public void setOBDRadioSelection(final boolean enbOnlyOBDRadioBtn, final boolean enbNoOBDRadioBtn,
      final boolean enbBothOBDAndNonOBDRadioBtn) {
    this.onlyOBDLabelRadio.setSelection(enbOnlyOBDRadioBtn);
    this.noOBDLabelRadio.setSelection(enbNoOBDRadioBtn);
    this.bothOBDAndNonOBDLabelRadio.setSelection(enbBothOBDAndNonOBDRadioBtn);
  }

  /**
   * create Simplified General Questionnnaire control
   *
   * @param composite - the parent composite
   */
  private void createSimplifiedGeneralQnaireControl(final Composite composite) {
    // creating the label
    GridData gridData = new GridData();
    gridData.verticalAlignment = SWT.TOP;
    this.simpGnrlQnaireControlLabel =
        LabelUtil.getInstance().createLabel(this.formToolkit, composite, "Simplified General Questionnnaire:");
    this.simpGnrlQnaireControlLabel.setLayoutData(gridData);

    // creating the button to open the dialog
    this.simpGnrlQnaireControlButton = new Button(composite, SWT.NONE);
    this.simpGnrlQnaireControlButton.setLayoutData(new GridData());
    this.simpGnrlQnaireControlButton.setToolTipText("Open Simplified General Questionnnaire Dialog");
    this.simpGnrlQnaireControlButton
        .setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.QUESTIONARE_ICON_16X16));

    this.simpGnrlQnaireControlButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        ReviewResultInfoPage.this.simpGnrlQnaireDialog = new SimplifiedGeneralQnaireDialog(
            Display.getCurrent().getActiveShell(), ReviewResultInfoPage.this.cdrResult, true);
        ReviewResultInfoPage.this.simpGnrlQnaireDialog.open();
      }
    });

    // creating the filler label to complete the row
    this.simpGnrlQnaireFillerLabel = new Label(composite, SWT.NONE);
    this.simpGnrlQnaireFillerLabel.setLayoutData(new GridData());

    // hides/ unhides the composites
    hideUnhideSimpGnrlQnaireControl(composite);
  }

  /**
   * hide/unhide Simplified General Questionnnaire control composites
   *
   * @param composite - the parent composite
   */
  private void hideUnhideSimpGnrlQnaireControl(final Composite composite) {
    try {
      boolean shouldBeVisible =
          CommonUtils.isEqual(ReviewResultInfoPage.this.resultBo.getReviewType(), REVIEW_TYPE.OFFICIAL) &&
              this.resultData.isSimpQuesEnab() && CommonUtils.isNotNull(this.cdrResult.getSimpQuesRespValue());
      // if the Simplified General Questionnnaire control should be hidden then close the dialog if it is opened
      if (!shouldBeVisible && CommonUtils.isNotNull(this.simpGnrlQnaireDialog)) {
        this.simpGnrlQnaireDialog.close();
      }

      if (CommonUtils.isNotNull(this.simpGnrlQnaireControlLabel)) {
        GridData gridData = (GridData) this.simpGnrlQnaireControlLabel.getLayoutData();
        gridData.exclude = !shouldBeVisible;
        this.simpGnrlQnaireControlLabel.setVisible(shouldBeVisible);
      }

      if (CommonUtils.isNotNull(this.simpGnrlQnaireControlButton)) {
        GridData gridData = (GridData) this.simpGnrlQnaireControlButton.getLayoutData();
        gridData.exclude = !shouldBeVisible;
        this.simpGnrlQnaireControlButton.setVisible(shouldBeVisible);
      }

      if (CommonUtils.isNotNull(this.simpGnrlQnaireFillerLabel)) {
        GridData gridData = (GridData) this.simpGnrlQnaireFillerLabel.getLayoutData();
        gridData.exclude = !shouldBeVisible;
        this.simpGnrlQnaireFillerLabel.setVisible(shouldBeVisible);
      }
      composite.layout();
      composite.getParent().layout();
    }
    catch (SWTException error) {
      CDMLogger.getInstance().error("Error refreshing Simplified Questionnaire", error, Activator.PLUGIN_ID);
    }
  }

  /**
   * after CNS refresh if dialog contents are changed then the changes will be reflected in the dialog
   *
   * @param cdrResult - the newly refreshed CDRReviewResult
   */

  public void setSimplifiedGeneralQnaireDialogContent(final CDRReviewResult cdrResult) {
    try {
      // check if scrollform is created, and the simp qnaire dialog is also created to refresh it
      if (CommonUtils.isNotNull(getPartControl()) && CommonUtils.isNotNull(this.simpGnrlQnaireDialog)) {
        Button confirmRadioBtn = this.simpGnrlQnaireDialog.getConfirmRadioBtn();
        Button notConfmRadioBtn = this.simpGnrlQnaireDialog.getNotConfmRadioBtn();
        boolean simpQuesRespFlag =
            CommonUtils.isEqual(this.cdrResult.getSimpQuesRespValue(), CommonUtilConstants.CODE_YES);
        // check if the buttons are not disposed and then assign the values
        if (CommonUtils.isNotNull(simpQuesRespFlag) && !confirmRadioBtn.isDisposed() &&
            !notConfmRadioBtn.isDisposed()) {
          confirmRadioBtn.setSelection(simpQuesRespFlag);
          notConfmRadioBtn.setSelection(!simpQuesRespFlag);
        }

        String simpQuesRemarks = this.cdrResult.getSimpQuesRemarks();
        simpQuesRemarks = CommonUtils.isNotEmptyString(simpQuesRemarks) ? simpQuesRemarks : "";
        TextBoxContentDisplay textBoxContentDisplay = this.simpGnrlQnaireDialog.getTextBoxContentDisplay();
        // check if the textbox is not disposed and set the remarks
        if (!textBoxContentDisplay.isDisposed()) {
          textBoxContentDisplay.getText().setText(CommonUtils.checkNull(simpQuesRemarks));
        }
      }
    }
    catch (SWTException error) {
      CDMLogger.getInstance().error("Error refreshing Simplified Questionnaire Dialog", error, Activator.PLUGIN_ID);
    }

  }

  /**
   * @param comp comp
   */
  private void createRvwStatusControl(final Composite comp) {
    createLabelControl(comp, "Review Status:");
    this.rvwStatusTxt = createTextFileld(comp);
    this.rvwStatusTxt.setEnabled(true);
    this.rvwStatusTxt.setText(ReviewResultInfoPage.this.resultBo.getStatusUIType());
    fillerLabels(comp, 1);
  }

  /**
   * @param comp comp
   */
  private void createRvwLockedControl(final Composite comp) {
    createLabelControl(comp, "Review Locked:");
    this.rvwCheck = new Button(comp, SWT.CHECK);


    this.rvwCheck.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {

        String lockStatus = null;
        if (ReviewResultInfoPage.this.resultBo.isResultLocked()) {
          // Result is locked now. Trying to unlock
          if (ReviewResultInfoPage.this.resultBo.canUnLockResult()) {
            lockStatus = ApicConstants.CODE_NO;
          }
          else {
            CDMLogger.getInstance().errorDialog(
                "You are not allowed to unlock the review. Only a user with owner access rights to the PIDC can unlock a review. Please ask one of the PIDC owners to unlock your review",
                Activator.PLUGIN_ID);
            return;
          }
        }
        else {
          // Result is unlocked now. Trying to lock
          if (ReviewResultInfoPage.this.resultBo.canLockResult()) {
            lockStatus = ApicConstants.CODE_YES;
          }
          else {
            CDMLogger.getInstance().errorDialog("User does not have access to lock review", Activator.PLUGIN_ID);
            return;
          }
        }
        try {
          CDRReviewResultServiceClient resultClient = new CDRReviewResultServiceClient();
          CDRReviewResult cdrReviewResult = resultClient.getById(ReviewResultInfoPage.this.cdrResult.getId());
          cdrReviewResult.setLockStatus(lockStatus);
          resultClient.update(cdrReviewResult);
        }
        catch (ApicWebServiceException error) {
          CDMLogger.getInstance().error(error.getMessage(), error, Activator.PLUGIN_ID);
        }

        super.widgetSelected(selectionevent);
      }


    });
    setSelForLock();
    fillerLabels(comp, 1);
  }

  /**
   * set the selection for Lock
   */
  private void setSelForLock() {
    this.rvwCheck.setEnabled(
        ReviewResultInfoPage.this.resultBo.canLockResult() || ReviewResultInfoPage.this.resultBo.canUnLockResult());
    this.rvwCheck.setSelection(ReviewResultInfoPage.this.resultBo.isResultLocked());
  }

  private void createFileInfoContent() {
    createFileReviewControl(this.fileInfoForm.getBody());
    fillerLabels(this.fileInfoForm.getBody(), 3);
    createFileAttachmentSection(this.fileInfoForm.getBody());

    // ICDM 635
    fillerLabels(this.fileInfoForm.getBody(), 2);
    createInternalFilesSection(this.fileInfoForm.getBody());
    fillerLabels(this.fileInfoForm.getBody(), 4);
    createInputFilesSection(this.fileInfoForm.getBody());

  }

  private void createCommentField(final Composite comp) {

    createLabelControl(comp, "Comments:");
    this.commentTxt = new Text(comp, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
    this.commentTxt.addListener(SWT.MouseVerticalWheel, this::respondToMouseWheelEvent);

    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.widthHint = 200;
    gridData.heightHint = 80;
    this.commentTxt.setLayoutData(gridData);

    this.commentTxt.setEditable(false);
    if (ReviewResultInfoPage.this.resultBo.getCDRResult().getComments() != null) {
      this.commentTxt.setText(ReviewResultInfoPage.this.resultBo.getCDRResult().getComments());
    }
    else {
      this.commentTxt.setText("");
    }

    this.commentTxt.addKeyListener(new KeyListener() {

      @Override
      public void keyReleased(final KeyEvent keyEvent) {
        if (!(((keyEvent.stateMask & SWT.CTRL) == SWT.CTRL) && (keyEvent.keyCode == 262144))) {
          MessageDialogUtils.getWarningMessageDialog("Edit comment", "Please use the edit button to modify the text");
        }
      }

      @Override
      public void keyPressed(final KeyEvent keyEvent) {
        // Implementation in KeyReleased() method is sufficient
      }
    });
    this.editCommentBtn = new Button(comp, SWT.NONE);
    this.editCommentBtn.setToolTipText("Edit Comments");
    this.editCommentBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.EDIT_16X16));

    isEditApplicable(this.editCommentBtn);
    this.editCommentBtn.addSelectionListener(new SelectionAdapter() {

      ReviewInfoCommentDialog commentDialog =
          new ReviewInfoCommentDialog(Display.getCurrent().getActiveShell(), ReviewResultInfoPage.this.resultData);

      @Override
      public void widgetSelected(final SelectionEvent event) {
        int isOkPressed = this.commentDialog.open();
        if (isOkPressed == CODE_FOR_OK) {

          final String comments = this.commentDialog.getReviewComments();
          CDRReviewResultServiceClient client = new CDRReviewResultServiceClient();
          try {
            CDRReviewResult cloneResult = ReviewResultInfoPage.this.cdrResult.clone();
            CommonUtils.shallowCopy(cloneResult, ReviewResultInfoPage.this.cdrResult);
            cloneResult.setComments(comments);
            client.update(cloneResult);
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
          }
        }
      }

    });

  }

  /**
   * Icdm-738 display the Lab/fun file in Review info page
   *
   * @param body
   */
  private void createInputFilesSection(final Composite comp) {
    createLabelControl(comp, "Input files:");
    this.inputFilesList = new org.eclipse.swt.widgets.List(comp, SWT.V_SCROLL | SWT.BORDER | SWT.MULTI);
    this.inputFilesList.addListener(SWT.MouseVerticalWheel, this::respondToMouseWheelEvent);
    final GridData gridData = new GridData();
    gridData.verticalSpan = 3;
    gridData.verticalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.heightHint = LIST_ROW_COUNT * this.internalFilesList.getItemHeight();
    this.inputFilesList.setLayoutData(gridData);
    final SortedSet<RvwFile> attachments = new TreeSet<>();
    attachments.addAll(ReviewResultInfoPage.this.resultBo.getLabFunFiles());
    attachments.addAll(ReviewResultInfoPage.this.resultBo.getMonicaFiles());
    fillInpFileMap(this.inputFilesList, attachments);
    final GridData gridData1 = new GridData();
    addLabExportButton(comp, gridData1);

    this.inputFilesList.addListener(SWT.Selection, event -> {
      if (ReviewResultInfoPage.this.resultData.getResultBo().canDownloadFiles()) {
        ReviewResultInfoPage.this.exportLabFileButton.setEnabled(true);
      }
      final String[] fileSelection = ReviewResultInfoPage.this.inputFilesList.getSelection();
      setSelectedLabFunFiles(fileSelection);

    });

    new ListViewer(this.inputFilesList)
        .addDoubleClickListener(doubleclickevent -> Display.getDefault().asyncExec(() -> {
          String[] inputFileList = ReviewResultInfoPage.this.inputFilesList.getSelection();
          RvwFile rvwFile = ReviewResultInfoPage.this.inpFileMap.get(inputFileList[0]);
          if (rvwFile != null) {
            ReviewResultInfoPage.this.editor.openRvwFile(rvwFile);
          }
        }));
  }

  /**
   * @param comp
   * @param gridData1
   */
  private void addLabExportButton(final Composite comp, final GridData gridData3) {


    this.exportLabFileButton = new Button(comp, SWT.PUSH);
    this.exportLabFileButton.setImage(this.downloadImg);
    this.exportLabFileButton.setToolTipText(DOWNLOAD_FILE);
    this.exportLabFileButton.setEnabled(false);
    this.exportLabFileButton.setLayoutData(gridData3);

    this.exportLabFileButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        downloadButtonAction(ReviewResultInfoPage.this.selectedLabFiles);
      }
    });

  }

  /**
   * @param inputFilesList2
   * @param attachments
   */
  private void fillInpFileMap(final org.eclipse.swt.widgets.List fileList, final SortedSet<RvwFile> attachments) {
    fileList.removeAll();
    this.inpFileMap.clear();
    for (RvwFile icdmFile : attachments) {
      String fileName = icdmFile.getName();
      if (fileName.contains(CDRConstants.FILENAME_SEPERATOR)) {
        fileName = fileName.substring(fileName.lastIndexOf(CDRConstants.FILENAME_SEPERATOR) + 1, fileName.length());
      }
      fileList.add(fileName);
      this.inpFileMap.put(fileName, icdmFile);
    }

  }

  /**
   * @param fileSelection
   */
  protected void setSelectedLabFunFiles(final String[] fileSelection) {
    ReviewResultInfoPage.this.selectedLabFiles = new ArrayList<>();
    if (fileSelection != null) {
      for (String selection : fileSelection) {
        ReviewResultInfoPage.this.selectedLabFiles.add(ReviewResultInfoPage.this.inpFileMap.get(selection));
      }

    }

  }

  /**
   * Icdm-673 New Sections
   *
   * @throws ApicWebServiceException
   */
  private void createUserContent() {

    createCreatedUserUIControl();// ICDM-1746
    fillerLabels(this.userInfoForm.getBody(), 1);
    createCalEngUIControl(this.userInfoForm.getBody());
    fillerLabels(this.userInfoForm.getBody(), 1);
    createAuditorUIControl(this.userInfoForm.getBody());
    createOtherPartyControl(this.userInfoForm.getBody());
  }

  /**
   * ICDM-1746 create label and text field
   */
  private void createCreatedUserUIControl() {
    createLabelControl(this.userInfoForm.getBody(), "Review Creator:");
    Text createdUsrTxt;
    createdUsrTxt = createTextFileld(this.userInfoForm.getBody());
    createdUsrTxt.setEnabled(true);
    if (this.cdrResult.getCreatedUser() != null) {
      String createdUserName = this.resultBo.getCreatedUser().getDescription();
      createdUsrTxt.setText(createdUserName);
    }


  }

  /**
   * ICDM 635 Section to display internal files
   *
   * @param reviewInfoGroup
   */
  private void createInternalFilesSection(final Composite comp) {
    createLabelControl(comp, "Internal files:");
    this.internalFilesList = new org.eclipse.swt.widgets.List(comp, SWT.V_SCROLL | SWT.BORDER | SWT.MULTI);
    this.internalFilesList.addListener(SWT.MouseVerticalWheel, this::respondToMouseWheelEvent);
    final GridData gridData = new GridData();
    gridData.verticalSpan = 3;
    gridData.verticalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.heightHint = LIST_ROW_COUNT * this.internalFilesList.getItemHeight();
    this.internalFilesList.setLayoutData(gridData);
    final SortedSet<RvwFile> attachments = new TreeSet<>();
    if (CommonUtils.isNotEmpty(ReviewResultInfoPage.this.resultBo.getRuleFile())) { // ICDM-844
      for (RvwFile icdmFile : ReviewResultInfoPage.this.resultBo.getRuleFile()) {
        attachments.add(icdmFile);
      }
    }
    attachments.addAll(ReviewResultInfoPage.this.resultBo.getOutputFiles());
    fillIntFileListInput(this.internalFilesList, attachments);
    final GridData gridData1 = new GridData();
    addExportButton(comp, gridData1);

    this.internalFilesList.addListener(SWT.Selection, event -> {
      if (ReviewResultInfoPage.this.resultData.getResultBo().canDownloadFiles()) {
        ReviewResultInfoPage.this.exportFileButton.setEnabled(true);
      }

      final String[] fileSelection = ReviewResultInfoPage.this.internalFilesList.getSelection();
      setSelectedInternalFiles(fileSelection);

    });

    new ListViewer(this.internalFilesList)
        .addDoubleClickListener(doubleclickevent -> Display.getDefault().asyncExec(() -> {
          String[] internalFiles = ReviewResultInfoPage.this.internalFilesList.getSelection();
          RvwFile rvwFile = ReviewResultInfoPage.this.intfileMap.get(internalFiles[0]);
          if (rvwFile != null) {
            ReviewResultInfoPage.this.editor.openRvwFile(rvwFile);
          }
        }));
  }

  /**
   * ICDM 635 Filling the list of internal files
   *
   * @param fileList List
   * @param attachments SortedSet of Icdm files
   */
  private void fillIntFileListInput(final org.eclipse.swt.widgets.List fileList, final SortedSet<RvwFile> attachments) {
    fileList.removeAll();
    this.intfileMap.clear();
    for (RvwFile icdmFile : attachments) {
      fileList.add(icdmFile.getName());
      this.intfileMap.put(icdmFile.getName(), icdmFile);
    }
  }

  /**
   * ICDM 635 Adding export button
   *
   * @param comp Composite
   * @param gridData3 GridData
   */
  private void addExportButton(final Composite comp, final GridData gridData3) {
    this.exportFileButton = new Button(comp, SWT.PUSH);
    this.exportFileButton.setImage(this.downloadImg);
    this.exportFileButton.setToolTipText(DOWNLOAD_FILE);
    this.exportFileButton.setEnabled(false);
    this.exportFileButton.setLayoutData(gridData3);

    this.exportFileButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        downloadButtonAction(ReviewResultInfoPage.this.selectedIntFiles);
      }
    });
  }

  /**
   * ICDM 635 Setting the selected files in the list
   *
   * @param fileSelection Array of selected file names
   */
  protected void setSelectedInternalFiles(final String[] fileSelection) {
    ReviewResultInfoPage.this.selectedIntFiles = new ArrayList<>();
    if (fileSelection != null) {
      for (String selection : fileSelection) {
        ReviewResultInfoPage.this.selectedIntFiles.add(ReviewResultInfoPage.this.intfileMap.get(selection));

      }

    }
  }

  /**
   * @param reviewInfoGroup2
   */
  private void createFileAttachmentSection(final Composite comp) {

    createLabelControl(comp, "Files Attached:");
    createTableForFileSelection(comp);
  }

  /**
   * @param comp Icdm-612 Files Attached Section
   */
  private void createTableForFileSelection(final Composite comp) {

    this.filesList = new org.eclipse.swt.widgets.List(comp, SWT.V_SCROLL | SWT.BORDER | SWT.MULTI);
    this.filesList.addListener(SWT.MouseVerticalWheel, this::respondToMouseWheelEvent);
    final GridData gridData = new GridData();
    gridData.verticalSpan = 4;
    gridData.verticalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.heightHint = (LIST_ROW_COUNT - 3) * this.filesList.getItemHeight();
    this.filesList.setLayoutData(gridData);
    final SortedSet<RvwFile> attachments = ReviewResultInfoPage.this.resultBo.getAdditionalFiles();
    fillFileListInput(this.filesList, attachments);

    createAddDeleteButtons(comp);

    this.filesList.addListener(SWT.Selection, event -> {
      if (ReviewResultInfoPage.this.resultBo.canDownloadFiles()) {
        ReviewResultInfoPage.this.deleteButton.setEnabled(!ReviewResultInfoPage.this.resultBo.isResultLocked());
        ReviewResultInfoPage.this.downloadButton.setEnabled(true);
      }
      else {
        ReviewResultInfoPage.this.deleteButton.setEnabled(false);
      }
      final String[] fileSelection = ReviewResultInfoPage.this.filesList.getSelection();
      setSelectedIcdmFiles(fileSelection);
    });

    new ListViewer(this.filesList).addDoubleClickListener(doubleclickevent -> Display.getDefault().asyncExec(() -> {
      String[] fileLists = ReviewResultInfoPage.this.filesList.getSelection();
      RvwFile rvwFile = ReviewResultInfoPage.this.attachmentsMap.get(fileLists[0]);
      if (rvwFile != null) {
        ReviewResultInfoPage.this.editor.openRvwFile(rvwFile);
      }
    }));
  }

  /**
   * @param comp
   */
  private void createAddDeleteButtons(final Composite comp) {
    this.addFileButton = new Button(comp, SWT.PUSH);
    this.addFileButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.ADD_16X16));
    this.addFileButton.setToolTipText("Add Files");

    this.addFileButton.setEnabled(ReviewResultInfoPage.this.resultBo.isModifiable());

    fillerLabels(comp, 1);
    this.addFileButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        addButtonAction();
      }
    });

    this.deleteButton = new Button(comp, SWT.PUSH);
    this.deleteButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.DELETE_16X16));
    this.deleteButton.setToolTipText("Delete Files");
    this.deleteButton.setEnabled(false);

    this.deleteButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        deleteButtonAction();
      }
    });
    fillerLabels(comp, 1);


    addDownLoadButton(comp);

  }

  /**
   * @param comp Composite
   * @param gridData3 GridData
   */
  private void addDownLoadButton(final Composite comp) {
    this.downloadButton = new Button(comp, SWT.PUSH);
    this.downloadButton.setImage(this.downloadImg);
    this.downloadButton.setToolTipText(DOWNLOAD_FILE);
    this.downloadButton.setEnabled(false);

    this.downloadButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        downloadButtonAction(ReviewResultInfoPage.this.selectedIcdmFile);
      }
    });
  }

  /**
   *
   */
  protected void deleteButtonAction() {

    final List<RvwFile> icdmFileList = this.selectedIcdmFile;
    Set<RvwFile> filesSet = new HashSet<>(icdmFileList);
    RvwFileServiceClient fileClient = new RvwFileServiceClient();
    try {
      fileClient.delete(filesSet);
    }
    catch (ApicWebServiceException exception) {
      CDMLogger.getInstance().error(exception.getLocalizedMessage(), exception, Activator.PLUGIN_ID);
    }

  }

  /**
   * download the zip file
   *
   * @param icdmFileList file List
   */
  protected void downloadButtonAction(final List<RvwFile> icdmFileList) {
    final DirectoryDialog dirDialog = new DirectoryDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
    dirDialog.setText("Save Attached Files");

    final String dirSelected = dirDialog.open();
    RvwFileServiceClient client = new RvwFileServiceClient();
    if (dirSelected != null) {
      for (RvwFile icdmFile : icdmFileList) {
        String fileName = icdmFile.getName();
        if (fileName.contains(CDRConstants.FILENAME_SEPERATOR)) {
          fileName = fileName.substring(fileName.lastIndexOf(CDRConstants.FILENAME_SEPERATOR) + 1, fileName.length());
        }
        try {
          client.downloadEmrFile(icdmFile.getId(), fileName, dirSelected);
        }
        catch (ApicWebServiceException exception) {
          CDMLogger.getInstance().error(exception.getLocalizedMessage(), exception, Activator.PLUGIN_ID);
        }
      }
    }
  }


  /**
   *
   */
  protected void addButtonAction() {

    final FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE | SWT.MULTI);
    fileDialog.setText("Add Review files");
    fileDialog.setFilterNames(new String[] { "*.zip" });
    final String path = fileDialog.open();
    final String[] fileNames = fileDialog.getFileNames();
    for (String fileName : fileNames) {
      try {
        final String appendedPath = getAbsoluteFilePath(path, fileName);
        if (isNotDuplicateFile(fileName)) {
          final File file = new File(appendedPath); // NOPMD by rgo7cob on 7/10/14 4:54 PM
          RvwFile rvwFile = new RvwFile();
          rvwFile.setResultId(this.cdrResult.getId());
          rvwFile.setFileType(CDRConstants.REVIEW_FILE_TYPE.RVW_ADDL_FILE.getDbType());
          rvwFile.setFilePath(file.getAbsolutePath());
          rvwFile.setName(file.getName());
          rvwFile.setNodeType(MODEL_TYPE.CDR_RESULT.getTypeCode());
          RvwFileServiceClient fileClient = new RvwFileServiceClient();
          fileClient.create(rvwFile);

        }
        else {
          MessageDialogUtils.getErrorMessageDialog("File Already Attached",
              "The File is allready Attached to the Result.Please select another file or save the file with a different name");
        }
      }
      catch (ApicWebServiceException exception) {
        CDMLogger.getInstance().error(exception.getLocalizedMessage(), exception, Activator.PLUGIN_ID);
      }
    }


  }


  /**
   * @param fileList List
   * @param attachments SortedSet of Icdm files
   */
  private void fillFileListInput(final org.eclipse.swt.widgets.List fileList, final SortedSet<RvwFile> attachments) {
    if (null != fileList) {
      fileList.removeAll();
      this.attachmentsMap.clear();
      for (RvwFile icdmFile : attachments) {
        StringBuilder fileName = new StringBuilder();
        fileName.append(icdmFile.getName());
        fileName.append("-");
        fileName.append(icdmFile.getCreatedDate());
        fileList.add(fileName.toString());
        this.attachmentsMap.put(fileName.toString(), icdmFile);
      }
    }
  }

  /**
   * Check for the Duplicate File
   *
   * @param fileName
   */
  private boolean isNotDuplicateFile(final String fileName) {
    for (RvwFile file : this.attachmentsMap.values()) {
      if (ApicUtil.compare(fileName, file.getName()) == 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * @param fullFilePath
   * @param fileNam
   * @return
   */
  private String getAbsoluteFilePath(final String fullFilePath, final String fileNam) {
    final int idx = fullFilePath.lastIndexOf('\\');
    final StringBuilder appendedPath = new StringBuilder(idx >= 0 ? fullFilePath.substring(0, idx) : fullFilePath);
    appendedPath.append('\\').append(fileNam);
    return appendedPath.toString();
  }


  /**
   * @param comp composite
   */
  private void createVariantUIControl(final Composite comp) {
    createLabelControl(comp, "Variant:");
    this.variantName = createTextFileld(comp);
    this.variantName.setEnabled(true);

    String finalVariantNames = getVariantDetails();
    this.variantName.setText(finalVariantNames);

    // ICDM-2594
    String tooltip = WordUtils.wrap(this.variantName.getText(), 100, "\n", true);
    this.variantName.setToolTipText(tooltip);

    fillerLabels(comp, 1);
  }

  /**
   * @return
   */
  // ICDM-2391
  private String getVariantDetails() {
    if (null == this.resultBo.getVariant()) {
      return "";
    }
    Collection<RvwVariant> values = ReviewResultInfoPage.this.resultBo.getReviewVarMap().values();
    Iterator<RvwVariant> iterator = values.iterator();
    StringJoiner joiner = new StringJoiner(", ");
    String originalVariantName = ReviewResultInfoPage.this.resultBo.getVariant().getName();
    StringBuilder finalVariantNames = new StringBuilder(originalVariantName);
    while (iterator.hasNext()) {
      RvwVariant cdrReviewVariant = iterator.next();
      String linkedVariantName = cdrReviewVariant.getVariantName();
      if (!originalVariantName.equals(linkedVariantName)) {
        joiner.add(linkedVariantName);
      }
    }
    if (joiner.length() > 0) {
      finalVariantNames.append("; Attached to other variants - ").append(joiner.toString());
    }
    return finalVariantNames.toString();
  }

  /**
   * @param comp composite
   * @throws ApicWebServiceException
   */
  private void createCalEngUIControl(final Composite comp) {
    createLabelControl(comp, "Calibration Engineer:");
    // ICDM-1800
    this.calEngNameTxt = createTextFileld(comp);
    this.calEngNameTxt.setEnabled(true);
    if (ReviewResultInfoPage.this.resultBo.getCalibrationEngineer() != null) {
      this.calEngNameTxt.setText(ReviewResultInfoPage.this.resultBo.getCalibrationEngineer().getName());
    }

  }

  /**
   * @param comp composite
   */
  private void createAuditorUIControl(final Composite comp) {
    createLabelControl(comp, "Auditor:");
    this.auditorTxt = createTextFileld(comp);
    this.auditorTxt.setEnabled(true);

    if (ReviewResultInfoPage.this.resultBo.getAuditor() != null) {
      this.auditorTxt.setText(ReviewResultInfoPage.this.resultBo.getAuditor().getName());
    }

    // ICDM-809
    createEditButton(comp);
  }

  /**
   * ICDM-809
   *
   * @param comp Composite
   */
  private void createEditButton(final Composite comp) {
    this.editAuditorButton = new Button(comp, SWT.PUSH);
    this.editAuditorButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.EDIT_16X16));
    this.editAuditorButton.setToolTipText(EDIT_AUDITOR);
    // Story 221802
    try {
      this.editAuditorButton.setEnabled(isEditAuditorAllowed());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    this.editAuditorButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        try {
          editButtonAction();
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }
      }
    });

  }


  /**
   * @return
   * @throws ApicWebServiceException
   */
  private boolean isEditAuditorAllowed() throws ApicWebServiceException {
    return this.cdrResult.getCreatedUser().equals(new CurrentUserBO().getUserName()) &&
        !ReviewResultInfoPage.this.resultBo.isResultLocked() &&
        (ReviewResultInfoPage.this.resultBo.getWorkPackageName() != null) && !CDRConstants.CDR_SOURCE_TYPE.MONICA_FILE
            .getUIType().equalsIgnoreCase(ReviewResultInfoPage.this.resultBo.getWorkPackageName());
  }

  private void editButtonAction() throws ApicWebServiceException {

    final UserSelectionDialog userDialog = new UserSelectionDialog(Display.getCurrent().getActiveShell(), "Edit User",
        EDIT_AUDITOR, EDIT_AUDITOR, "Set", false, true);
    userDialog.setSelectedMultipleUser(null);
    userDialog.open();
    final User selectedUser = userDialog.getSelectedUser();
    final RvwParticipant auditor = ReviewResultInfoPage.this.resultBo.getAuditor();
    if (selectedUser != null) {
      if (ReviewResultInfoPage.this.offRevRadio.getSelection() && CommonUtils
          .isEqualIgnoreCase(selectedUser.getDescription(), ReviewResultInfoPage.this.calEngNameTxt.getText())) {
        CDMLogger.getInstance().errorDialog(
            "For official reviews, the 'Calibration Engineer' and 'Auditor' should be different users",
            Activator.PLUGIN_ID);
      }
      else {
        RvwParticipantServiceClient client = new RvwParticipantServiceClient();
        RvwParticipant cloneObj = new RvwParticipant();
        CommonUtils.shallowCopy(cloneObj, auditor);
        cloneObj.setUserId(selectedUser.getId());
        client.update(cloneObj);
      }
    }
  }

  /**
   * Icdm-673
   *
   * @param comp composite
   * @throws ApicWebServiceException
   */
  private void createOtherPartyControl(final Composite comp) {
    createLabelControl(comp, "Other Participants:");
    createOtherPartContent();

    // Icdm-621
    final GridData gridData = new GridData();
    gridData.verticalSpan = 4;
    gridData.verticalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.heightHint = 250;
    this.otherParticipantTabViewer.getControl().setLayoutData(gridData);

    // ICDM-809
    createAddDeleteUserButtons(comp);
    addListener();
    addKeyListner();
  }

  private void createOtherPartContent() {
    this.otherParticipantTabViewer = GridTableViewerUtil.getInstance().createCustomGridTableViewer(
        this.userInfoForm.getBody(), SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI,
        new int[] { CommonUIConstants.COLUMN_INDEX_2 });
    this.otherParticipantTabViewer.getControl().addListener(SWT.MouseVerticalWheel, this::respondToMouseWheelEvent);
    this.rvwParticipantSorter = new RvwParticipantSorter();
    initializeEditorStatusLineManager(this.otherParticipantTabViewer);
    createOtherParticipantsTabCols();
    this.otherParticipantTabViewer.setComparator(this.rvwParticipantSorter);
    this.otherParticipantTabViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.otherParticipantTabViewer.setInput(this.resultBo.getOtherParticipants());
  }

  private void createOtherParticipantsTabCols() {
    createOtherParticipantColumn();
    createWriteAccesscolumn();
  }

  /**
  *
  */
  private void createOtherParticipantColumn() {

    final GridViewerColumn participantName = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.otherParticipantTabViewer, "Participant Name", ATTR_VAL_COL_SIZE);

    participantName.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof RvwParticipant) {
          return ((RvwParticipant) element).getName();
        }
        return "";
      }
    });
    participantName.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        participantName.getColumn(), 0, this.rvwParticipantSorter, this.otherParticipantTabViewer));

  }

  private void createWriteAccesscolumn() {
    final GridViewerColumn writeAccess = new GridViewerColumn(this.otherParticipantTabViewer, SWT.CHECK);
    writeAccess.getColumn().setText("Write");
    writeAccess.getColumn().setWidth(70);
    writeAccess.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        Object element = cell.getElement();
        if (element instanceof RvwParticipant) {
          setGridItem(cell, element);
        }
      }
    });

    writeAccess.setEditingSupport(new CheckEditingSupport(writeAccess.getViewer()) {

      @Override
      public void setValue(final Object arg0, final Object arg1) {
        if (ReviewResultInfoPage.this.resultBo.canModifyOtherParticipants()) {
          RvwParticipant parti = (RvwParticipant) arg0;
          if ((parti.isEditFlag() && !(boolean) arg1) || (!parti.isEditFlag() && (boolean) arg1)) {
            parti.setEditFlag((boolean) arg1);
          }
          try {
            new RvwParticipantServiceClient().update(parti);
          }
          catch (ApicWebServiceException exp) {
            CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
          }
        }
      }
    });
    writeAccess.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(writeAccess.getColumn(), 1, this.rvwParticipantSorter, this.otherParticipantTabViewer));

  }

  /**
   * @param cell
   * @param element
   */
  private void setGridItem(final ViewerCell cell, final Object element) {
    final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
    if (((RvwParticipant) element).isEditFlag()) {
      gridItem.setChecked(cell.getVisualIndex(), true);
      gridItem.setCheckable(cell.getVisualIndex(), ReviewResultInfoPage.this.resultBo.canModifyOtherParticipants());
    }
    else {
      gridItem.setChecked(cell.getVisualIndex(), false);
      gridItem.setCheckable(cell.getVisualIndex(), ReviewResultInfoPage.this.resultBo.canModifyOtherParticipants());
    }
  }

  /**
   * add listener to participants list
   */
  private void addListener() {
    this.otherParticipantTabViewer.addSelectionChangedListener(arg -> {
      Collection<RvwParticipant> rvwParticipantList =
          ReviewResultInfoPage.this.resultData.getResultBo().getOtherParticipants();
      ReviewResultInfoPage.this.deleteUserButton.setEnabled((!ReviewResultInfoPage.this.resultBo.isResultLocked() &&
          ReviewResultInfoPage.this.resultBo.canModifyOtherParticipants() && !rvwParticipantList.isEmpty()));

      Object[] selectedItems = ReviewResultInfoPage.this.otherParticipantTabViewer.getStructuredSelection().toArray();
      ReviewResultInfoPage.this.selectedParticipants = new RvwParticipant[selectedItems.length];
      for (int i = 0; i < selectedItems.length; i++) {
        RvwParticipant parti = (RvwParticipant) selectedItems[i];
        ReviewResultInfoPage.this.selectedParticipants[i] = parti;
      }
    });
  }


  /**
   * ICDM-809
   *
   * @param comp Composite
   * @throws ApicWebServiceException
   */
  private void createAddDeleteUserButtons(final Composite comp) {
    addUserAction(comp);
    deleteUserAction(comp);
  }

  /**
   * @param comp Composite
   */
  private void deleteUserAction(final Composite comp) {
    this.deleteUserButton = new Button(comp, SWT.PUSH);
    this.deleteUserButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.DELETE_16X16));
    this.deleteUserButton.setToolTipText("Delete Users");
    this.deleteUserButton.setEnabled(false);
    this.deleteUserButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        deleteUserButtonAction();
      }
    });
  }

  /**
   * action for deleting users as other participants
   */

  private void deleteUserButtonAction() {
    Set<RvwParticipant> rvwParticipants = new HashSet<>();
    if ((ReviewResultInfoPage.this.selectedParticipants != null) &&
        (ReviewResultInfoPage.this.selectedParticipants.length != 0) &&
        !ReviewResultInfoPage.this.resultBo.isResultLocked()) {
      rvwParticipants =
          Arrays.asList(ReviewResultInfoPage.this.selectedParticipants).stream().collect(Collectors.toSet());
    }
    RvwParticipantServiceClient client = new RvwParticipantServiceClient();
    try {
      client.delete(rvwParticipants);
    }
    catch (ApicWebServiceException exception) {
      CDMLogger.getInstance().error(exception.getLocalizedMessage(), exception, Activator.PLUGIN_ID);
    }

    ReviewResultInfoPage.this.deleteUserButton.setEnabled(false);
  }

  /**
   * @param comp Composite
   * @throws ApicWebServiceException
   */
  private void addUserAction(final Composite comp) {
    this.addButton = new Button(comp, SWT.PUSH);
    this.addButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.ADD_16X16));
    this.addButton.setToolTipText("Add Users");

    this.addButton.setEnabled((!ReviewResultInfoPage.this.resultBo.isResultLocked() &&
        ReviewResultInfoPage.this.resultBo.canModifyOtherParticipants()));


    fillerLabels(comp, 1);
    this.addButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        addUserButtonAction();
      }
    });
  }

  /**
   * @return
   */

  /**
   * action for adding new users as other participants
   */
  private void addUserButtonAction() {

    final UserSelectionDialog userDialog = new UserSelectionDialog(Display.getCurrent().getActiveShell(), "Add User",
        "Add user", "Add user", "Add", true, false);
    userDialog.setSelectedMultipleUser(null);
    userDialog.open();
    List<User> selectedUsers = userDialog.getSelectedMultipleUser();
    if ((selectedUsers != null) && !selectedUsers.isEmpty()) {
      final Iterator<User> users = selectedUsers.iterator();
      List<RvwParticipant> toCreateList = new ArrayList<>();
      while (users.hasNext()) {
        final User selectedUser = users.next();
        if (!checkAlreadyPresent(selectedUser)) {
          RvwParticipant participant = new RvwParticipant();
          participant.setActivityType(REVIEW_USER_TYPE.ADDL_PARTICIPANT.getDbType());
          participant.setResultId(ReviewResultInfoPage.this.cdrResult.getId());
          participant.setUserId(selectedUser.getId());
          participant.setName(selectedUser.getDescription());
          toCreateList.add(participant);
        }
      }
      RvwParticipantServiceClient client = new RvwParticipantServiceClient();
      try {
        client.create(toCreateList);
      }
      catch (ApicWebServiceException error) {
        CDMLogger.getInstance().error(error.getMessage(), error, Activator.PLUGIN_ID);
      }

    }
  }

  /**
   * @param selectedUser
   * @return
   */
  private boolean checkAlreadyPresent(final User selectedUser) {
    for (RvwParticipant participant : this.resultBo.getOtherParticipants()) {
      if (Long.compare(participant.getUserId(), selectedUser.getId()) == 0) {
        return true;
      }
    }
    return false;

  }

  private void createProjectUIControl(final Composite comp) {

    createLabelControl(comp, "Project:");
    this.pidcNameTxt = createTextFileld(comp);
    this.pidcNameTxt.setEnabled(true);
    if (ReviewResultInfoPage.this.resultBo.getPidcVersion() != null) {
      this.pidcNameTxt.setText(ReviewResultInfoPage.this.resultBo.getPidcVersion().getName());
    }
    // Icdm-1349- Open PIDC From Review Infor page.
    Button openPidAction = new Button(comp, SWT.PUSH);
    openPidAction.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.PIDC_16X16));
    openPidAction.setToolTipText("Open PIDC");

    openPidAction.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final PIDCActionSet actionSet = new PIDCActionSet();
        actionSet.openPIDCEditor(ReviewResultInfoPage.this.resultBo.getPidcVersion());
      }
    });
  }

  private void respondToMouseWheelEvent(final Event event) {
    Point origin = this.scrollableForm.getOrigin();
    origin.y -= event.count;
    this.scrollableForm.setOrigin(origin);
  }


  /**
   * @param comp composite
   */
  private void createFunReviewControl(final Composite comp) {
    createLabelControl(comp, "Functions Reviewed:");
    // Icdm-621


    final org.eclipse.swt.widgets.List funRvwList =
        new org.eclipse.swt.widgets.List(comp, SWT.V_SCROLL | SWT.BORDER | SWT.MULTI);
    funRvwList.addListener(SWT.MouseVerticalWheel, this::respondToMouseWheelEvent);

    final GridData gridData = new GridData();
    gridData.verticalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.heightHint = (LIST_ROW_COUNT) * funRvwList.getItemHeight();
    funRvwList.setLayoutData(gridData);
    if (ReviewResultInfoPage.this.resultData.getFunctions() != null) {
      final SortedSet<CDRResultFunction> funcs = ReviewResultInfoPage.this.resultData.getFunctions();
      // ICDM-1333
      for (CDRResultFunction function : funcs) {
        funRvwList.add(ReviewResultInfoPage.this.resultData.getNameWithFuncVersion(function));
      }
    }
    fillerLabels(comp, 1);
  }

  /**
   * Icdm-612 File Reviewed section download button
   *
   * @param comp composite
   */
  private void createFileReviewControl(final Composite comp) {
    createLabelControl(comp, "Files Reviewed:");

    this.filesReviewedList = new org.eclipse.swt.widgets.List(comp, SWT.V_SCROLL | SWT.BORDER | SWT.MULTI);
    this.filesReviewedList.addListener(SWT.MouseVerticalWheel, this::respondToMouseWheelEvent);
    final GridData gridData = new GridData();
    gridData.verticalSpan = 3;
    gridData.verticalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.heightHint = (LIST_REV_CON) * this.filesReviewedList.getItemHeight();
    this.filesReviewedList.setLayoutData(gridData);

    final SortedSet<RvwFile> inputFiles = ReviewResultInfoPage.this.resultBo.getInputFiles();
    fillFileReviewedInput(inputFiles);


    createDownloadButton(comp);

    this.filesReviewedList.addListener(SWT.Selection, event -> {
      // Icdm-923 restrictions to export CDR files allow only if Apic Write or cal eng or auditor

      if (ReviewResultInfoPage.this.resultBo.canDownloadFiles()) {

        ReviewResultInfoPage.this.downloadInpButton.setEnabled(true);
      }

      final String[] fileSelection = ReviewResultInfoPage.this.filesReviewedList.getSelection();
      setSelectedInputFiles(fileSelection);
    });

    new ListViewer(this.filesReviewedList)
        .addDoubleClickListener(doubleclickevent -> Display.getDefault().asyncExec(() -> {

          String[] rvwFileList = ReviewResultInfoPage.this.filesReviewedList.getSelection();
          RvwFile rvwFile = ReviewResultInfoPage.this.fileReviewMap.get(rvwFileList[0]);
          if (rvwFile != null) {
            ReviewResultInfoPage.this.editor.openRvwFile(rvwFile);
          }
        }));
  }


  /**
   * @param fileSelection
   */
  protected void setSelectedInputFiles(final String[] fileSelection) {
    ReviewResultInfoPage.this.selectedInputFile = new ArrayList<>();
    if (fileSelection != null) {
      for (String selection : fileSelection) {
        ReviewResultInfoPage.this.selectedInputFile.add(ReviewResultInfoPage.this.fileReviewMap.get(selection));

      }

    }
  }

  /**
   * @param comp
   */
  private void createDownloadButton(final Composite comp) {
    this.downloadInpButton = new Button(comp, SWT.PUSH);
    this.downloadInpButton.setImage(this.downloadImg);
    this.downloadInpButton.setToolTipText("Download Files");
    this.downloadInpButton.setEnabled(false);
    fillerLabels(comp, 1);
    this.downloadInpButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        downloadButtonAction(ReviewResultInfoPage.this.selectedInputFile);
      }
    });

  }

  /**
   * @param inputFiles
   */
  private void fillFileReviewedInput(final SortedSet<RvwFile> inputFiles) {
    this.filesReviewedList.removeAll();
    this.fileReviewMap.clear();
    for (RvwFile icdmFile : inputFiles) {
      this.filesReviewedList.add(icdmFile.getName());
      this.fileReviewMap.put(icdmFile.getName(), icdmFile);
    }
  }

  /**
   * @param comp composite
   */
  private void createWPUIControl(final Composite comp) {
    createLabelControl(comp, "WorkPackage:");
    final Text nameTxt = createTextFileld(comp);
    nameTxt.setEnabled(true);
    if (ReviewResultInfoPage.this.resultBo.getWorkPackageName() != null) {
      nameTxt.setText(ReviewResultInfoPage.this.resultBo.getWorkPackageName());
    }
    fillerLabels(comp, 1);
  }

  /**
   * ICDM 610
   *
   * @param comp composite
   */
  private void createParentReviewSection(final Composite comp) {
    createLabelControl(comp, "Parent Review:");
    final Text nameTxt = createTextFileld(comp);
    nameTxt.setEnabled(true);
    nameTxt.setText(ReviewResultInfoPage.this.resultBo.getBaseReviewInfo());
    // Icdm-1349- Open Parent result From Review Infor page.
    Button openResBut = new Button(comp, SWT.PUSH);
    openResBut.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.RVW_RES_CLOSED_16X16));
    openResBut.setToolTipText("Open parent Result");
    if (ReviewResultInfoPage.this.resultBo.getParentReview() == null) {
      openResBut.setEnabled(false);
    }
    openResBut.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        CDRReviewResult parentReview = ReviewResultInfoPage.this.resultBo.getParentReview();
        if (parentReview != null) {
          RvwVariant rvwVariant = null;
          try {
            if (CommonUtils.isNotNull(parentReview.getPrimaryVariantId())) {
              rvwVariant = new RvwVariantServiceClient().getRvwVariantByResultNVarId(parentReview.getId(),
                  parentReview.getPrimaryVariantId());
            }
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().error(e.getMessage(), Activator.PLUGIN_ID);
          }
          // open review result
          new CdrActionSet().openReviewResultEditor(rvwVariant, null, parentReview, null);
        }
      }
    });
  }

  /**
   * ICDM 658 Section to display review description
   *
   * @param reviewInfoGroup
   */
  private void createDescControl(final Composite comp) {
    createLabelControl(comp, "Review Description:");
    this.descTxt = createTextFileld(comp);
    this.descTxt.setEnabled(true);
    if (null != ReviewResultInfoPage.this.resultBo.getCDRResult().getDescription()) {
      this.descTxt.setText(ReviewResultInfoPage.this.resultBo.getCDRResult().getDescription());
    }
    // ICDM-974
    this.editBtn = new Button(comp, SWT.PUSH);
    this.editBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.EDIT_16X16));
    this.editBtn.setToolTipText("Edit Description");
    isEditApplicable(this.editBtn);
    this.editBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        ReviewDescEditDialog editDesc = new ReviewDescEditDialog(Display.getCurrent().getActiveShell(),
            ReviewResultInfoPage.this.resultBo.getCDRResult().getDescription());
        editDesc.open();
        if ((editDesc.getReviewDescription() != null) && !editDesc.getReviewDescription().isEmpty()) {
          try {
            CDRReviewResult cloneResult = ReviewResultInfoPage.this.cdrResult.clone();
            CommonUtils.shallowCopy(cloneResult, ReviewResultInfoPage.this.cdrResult);
            cloneResult.setReviewType(ReviewResultInfoPage.this.resultBo.getReviewType().getDbType());
            cloneResult.setDescription(editDesc.getReviewDescription());
            CDRReviewResultServiceClient resultClient = new CDRReviewResultServiceClient();
            resultClient.update(cloneResult);
          }
          catch (ApicWebServiceException error) {
            CDMLogger.getInstance().error(error.getMessage(), error, Activator.PLUGIN_ID);
          }
        }
      }
    });
  }

  /**
   * This method enables the edit button based on user access rights. Opens the edit dialog when the button is cliked to
   * edit the description
   *
   * @param editBtn edit deccription button
   */
  private void isEditApplicable(final Button editBtn) {
    CurrentUserBO currentUser = new CurrentUserBO();
    try {
      boolean editFlag = !ReviewResultInfoPage.this.resultBo.isResultLocked() &&
          (currentUser.hasNodeWriteAccess(ReviewResultInfoPage.this.resultBo.getPidcVersion().getPidcId()) ||
              ReviewResultInfoPage.this.resultBo.isModifiable());
      editBtn.setEnabled(editFlag);
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
    }

  }

  /**
   * This method creates text field
   *
   * @param caldataComp
   * @return Text
   */
  private Text createTextFileld(final Composite comp) {

    final Text text = TextUtil.getInstance().createEditableText(this.formToolkit, comp, false, "");
    final GridData widthHintGridData = new GridData();
    widthHintGridData.horizontalAlignment = GridData.FILL;
    widthHintGridData.grabExcessHorizontalSpace = true;
    widthHintGridData.widthHint = 200;
    text.setLayoutData(widthHintGridData);
    return text;
  }

  /**
   * This method create Label instance for statisctical values
   *
   * @param caldataComp
   * @param lblName
   */
  private void createLabelControl(final Composite composite, final String lblName) {
    final GridData gridData = new GridData();
    gridData.verticalAlignment = SWT.TOP;
    LabelUtil.getInstance().createLabel(this.formToolkit, composite, lblName).setLayoutData(gridData);
  }

  /**
   * @param grp
   * @param limit
   */
  private void fillerLabels(final Composite grp, final int limit) {

    for (int i = 0; i < limit; i++) {
      new Label(grp, SWT.NONE); // NOPMD by rgo7cob on 7/10/14 4:56 PM
    }

  }

  // Icdm-612
  @Override
  public void initializeEditorStatusLineManager(final Viewer viewer) {
    if (viewer instanceof CustomGridTableViewer) {
      final CustomGridTableViewer customGridTabViewer = (CustomGridTableViewer) viewer;
      final IStatusLineManager statLineManagerEditPart = getEditorSite().getActionBars().getStatusLineManager();
      customGridTabViewer.setStatusLineManager(statLineManagerEditPart);
    }

  }

  /**
   * Icdm-612 selection of the Icdm file in the table viewer
   *
   * @param fileSelection
   */
  private void setSelectedIcdmFiles(final String[] fileSelection) {

    ReviewResultInfoPage.this.selectedIcdmFile = new ArrayList<>();
    if (fileSelection != null) {
      for (String selection : fileSelection) {
        ReviewResultInfoPage.this.selectedIcdmFile.add(ReviewResultInfoPage.this.attachmentsMap.get(selection));

      }

    }
  }

  // To Refresh the Review Result Status on page swap
  @Override
  public void setActive(final boolean active) {
    this.rvwStatusTxt.setText(ReviewResultInfoPage.this.resultBo.getStatusUIType());
  }

  /**
   * enable or disable editable fields in UI,
   */
  private void updateEditableFieldStatus() {

    boolean enabled =
        ReviewResultInfoPage.this.resultBo.isModifiable() && !ReviewResultInfoPage.this.resultBo.isResultLocked();
//    For Description Field  Edit Button
    isEditApplicable(this.editBtn);
//    For Comments Field  Edit Button
    isEditApplicable(this.editCommentBtn);
    try {
      this.editAuditorButton.setEnabled(isEditAuditorAllowed());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    this.addButton.setEnabled(enabled);
    this.addFileButton.setEnabled(ReviewResultInfoPage.this.resultBo.isModifiable());
    this.deleteButton.setEnabled(false);
    this.downloadButton.setEnabled(false);
    this.offRevRadio.setEnabled(enabled);
    this.testRevRadio.setEnabled(enabled);

  }

  /**
   * add key listener to participants list
   */
  private void addKeyListner() {
    this.otherParticipantTabViewer.getGrid().addKeyListener(new KeyAdapter() {

      @Override
      public void keyPressed(final KeyEvent event) {

        final char character = event.character;
        if (character == SWT.DEL) {
          deleteUserButtonAction();
        }
      }
    });
  }

  /**
   *
   */
  private void callUpdateCmd(final CDRConstants.REVIEW_TYPE reviewType) {

    try {
      CDRReviewResult cloneResult = ReviewResultInfoPage.this.cdrResult.clone();
      CommonUtils.shallowCopy(cloneResult, ReviewResultInfoPage.this.cdrResult);
      cloneResult.setReviewType(reviewType.getDbType());
      cloneResult.setDescription(this.cdrResult.getDescription());

      new CDRReviewResultServiceClient().update(cloneResult);
      CDMLogger.getInstance().info("Review Type is modified successfully", Activator.PLUGIN_ID);
    }
    catch (ApicWebServiceException error) {
      CDMLogger.getInstance().error(error.getMessage(), error, Activator.PLUGIN_ID);
    }


  }


  /**
   * This method returns filter text GridData object
   *
   * @return GridData
   */
  private GridData getFilterTxtGridData() {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.verticalAlignment = GridData.CENTER;
    return gridData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return this.scrollableForm.getToolBarManager();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReviewResultBO getDataHandler() {
    return this.editor.getEditorInput().getDataHandler();
  }
}
