/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.editors.QnaireRespEditorInput;
import com.bosch.caltool.cdr.ui.editors.QnaireResponseEditor;
import com.bosch.caltool.cdr.ui.sorters.QnaireRespVersionSorter;
import com.bosch.caltool.cdr.ui.table.filters.QnaireRespVersionsFilter;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author say8cob
 */
public class QnaireRespVersionPage extends AbstractFormPage implements ISelectionListener {

  /**
   * Col width - Name
   */
  private static final int COL_WIDTH_NAME = 100;
  /**
   * Col width - Rev No
   */
  private static final int COL_WIDTH_REV_NO = 70;
  /**
   * Col width - Description
   */
  private static final int COL_WIDTH_DESC = 250;
  /**
   * Col width - Status
   */
  private static final int COL_WIDTH_STATUS = 70;
  /**
   * Col width - Created Date
   */
  private static final int COL_WIDTH_CRE_DATE = 200;
  /**
   * Col width - Created User
   */
  private static final int COL_WIDTH_CRE_USR = 250;

  /**
   * Label
   */
  private static final String VERSIONS_LABEL = "Review Questionnaire Versions";

  private final QnaireResponseEditor qnaireRespEditor;

  private Form nonScrollableForm;

  private Section section;

  private Composite composite;

  private Form form;

  private GridTableViewer versionsTabViewer;

  private QnaireRespVersionSorter respVersionsSorter;

  private final QnaireRespEditorDataHandler dataHandler;
  private Action addNewVersionAction;

  private Text filterTxt;
  private QnaireRespVersionsFilter qnaireVersFilter;

  /**
   * @param editor
   * @param formID
   * @param title
   */
  public QnaireRespVersionPage(final FormEditor editor, final String formID, final String title) {
    super(editor, formID, title);
    this.qnaireRespEditor = (QnaireResponseEditor) editor;
    this.dataHandler = getEditorInput().getQnaireRespEditorDataHandler();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public QnaireRespEditorInput getEditorInput() {
    return this.qnaireRespEditor.getEditorInput();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {
    // Create an ordinary non scrollable form on which widgets are built
    this.nonScrollableForm = this.qnaireRespEditor.getToolkit().createForm(parent);
    // ICDM-208
    this.nonScrollableForm.setText(VERSIONS_LABEL);
    addHelpAction((ToolBarManager) this.nonScrollableForm.getToolBarManager());
    // instead of editor.getToolkit().createScrolledForm(parent); in superclass
    // formToolkit is obtained from managed form to create form within section
    ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }

  /**
  *
  */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    FormToolkit formToolkit = managedForm.getToolkit();
    createComposite(formToolkit);
    this.section.getDescriptionControl().setEnabled(false);
  }

  /**
   * This method initializes composite
   */
  private void createComposite(final FormToolkit toolkit) {
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = this.nonScrollableForm.getBody();
    this.composite.setLayout(new GridLayout());
    createSection(toolkit);
    this.composite.setLayoutData(gridData);
  }

  /**
   * This method initializes section
   */
  private void createSection(final FormToolkit toolkit) {
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.section = SectionUtil.getInstance().createSection(this.composite, toolkit, VERSIONS_LABEL);
    this.section.setLayoutData(gridData);
    createForm(toolkit);
    createToolBarAction();
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm(final FormToolkit toolkit) {
    this.form = toolkit.createForm(this.section);
    this.form.getBody().setLayout(new GridLayout());

    this.filterTxt = toolkit.createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    createFilterTxt();

    this.versionsTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, GridDataUtil.getInstance().getGridData());

    this.respVersionsSorter = new QnaireRespVersionSorter();

    createVersionsTabColumns();
    addFilters();
    this.versionsTabViewer.setContentProvider(ArrayContentProvider.getInstance());
    Collection<RvwQnaireRespVersion> qnaireRespVersions =
        this.dataHandler.getQnaireVersions(this.dataHandler.getQuesResponse().getId());
    // activate the tooltip support for the viewer
    ColumnViewerToolTipSupport.enableFor(this.versionsTabViewer, ToolTip.NO_RECREATE);
    this.versionsTabViewer.setInput(qnaireRespVersions);


    // Invoke TableViewer Column sorters
    this.versionsTabViewer.setComparator(this.respVersionsSorter);

    addDoubleClickListener();

  }

  /**
   *
   */
  private void addDoubleClickListener() {
    this.versionsTabViewer.addDoubleClickListener(event -> {
      Runnable runnable = () -> {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        Object selectedNode = selection.getFirstElement();
        openQuestionnaireEditor(selectedNode);
      };
      BusyIndicator.showWhile(Display.getDefault(), runnable);

    });
  }

  /**
   * @param selectedNode
   */
  private void openQuestionnaireEditor(final Object selectedNode) {
    if (selectedNode instanceof RvwQnaireRespVersion) {
      RvwQnaireRespVersion rvwQnaireRespVersion = (RvwQnaireRespVersion) selectedNode;
      CDMLogger.getInstance().debug("Opening Questionnaire Response editor : {}",
          rvwQnaireRespVersion.getQnaireRespId());
      try {
        QnaireRespEditorInput qNaireResponseEditorInput = new QnaireRespEditorInput(rvwQnaireRespVersion);
        if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isEditorAreaVisible()) {
          IEditorPart openEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
              .openEditor(qNaireResponseEditorInput, QnaireResponseEditor.EDITOR_ID);
          if (openEditor instanceof QnaireResponseEditor) {
            openEditor.setFocus();
            CDMLogger.getInstance().debug("Questionnaire Response opened in the editor");
          }
        }
      }
      catch (PartInitException excep) {
        CDMLogger.getInstance().error(excep.getLocalizedMessage(), excep, Activator.PLUGIN_ID);
      }

    }
  }


  /**
   * This method creates Section ToolBar actions
   */
  private void createToolBarAction() {

    ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

    ToolBar toolbar = toolBarManager.createControl(this.section);

    addNewVersionAction(toolBarManager);

    toolBarManager.update(true);

    this.section.setTextClient(toolbar);
  }

  /**
   * This method creates non defined filter action
   *
   * @param toolBarManager
   */
  // ICDM-70
  private void addNewVersionAction(final ToolBarManager toolBarManager) {
    // Create an action to add new user
    this.addNewVersionAction = new Action("Add Version", SWT.NONE) {

      @Override
      public void run() {
        QnaireRespVersionPage.this.qnaireRespEditor.getQuesSummaryPge().createQnaireRespVersion();
      }
    };
    try {
      // Set the image for add version action
      this.addNewVersionAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
      this.addNewVersionAction.setEnabled(
          this.qnaireRespEditor.getQuesSummaryPge().checkQnaireAccessRights() && this.dataHandler.isModifiable());
      toolBarManager.add(this.addNewVersionAction);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

  }

  /**
  *
  */
  private void createVersionsTabColumns() {
    createNameDescStatusCols();

    GridViewerColumn reviewedDateColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.versionsTabViewer, "Reviewed Date", COL_WIDTH_CRE_DATE);
    reviewedDateColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return getReviewedDate(element);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getToolTipText(final Object element) {
        return getReviewedDate(element);
      }

      /**
       * @param element
       * @return
       */
      private String getReviewedDate(final Object element) {
        if (element instanceof RvwQnaireRespVersion) {
          RvwQnaireRespVersion rvwQnaireRespVersion = (RvwQnaireRespVersion) element;
          if (null != rvwQnaireRespVersion.getReviewedDate()) {
            SimpleDateFormat df10 = new SimpleDateFormat(com.bosch.caltool.icdm.common.util.DateFormat.DATE_FORMAT_10,
                Locale.getDefault(Locale.Category.FORMAT));
            SimpleDateFormat df15 = new SimpleDateFormat(com.bosch.caltool.icdm.common.util.DateFormat.DATE_FORMAT_15,
                Locale.getDefault(Locale.Category.FORMAT));
            Date date;
            try {
              date = df15.parse(rvwQnaireRespVersion.getReviewedDate());
              return df10.format(date);
            }
            catch (ParseException e) {
              CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
            }
          }
        }
        return "";
      }

    });
    // Add column selection listener
    reviewedDateColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(reviewedDateColumn.getColumn(), 4, this.respVersionsSorter, this.versionsTabViewer));

    GridViewerColumn reviewedUserColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.versionsTabViewer, "Reviewed User", COL_WIDTH_CRE_USR);
    reviewedUserColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return getReviewedUser(element);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getToolTipText(final Object element) {
        return getReviewedUser(element);
      }

      /**
       * @param element
       * @return
       */
      private String getReviewedUser(final Object element) {
        if (element instanceof RvwQnaireRespVersion) {
          RvwQnaireRespVersion rvwQnaireRespVersion = (RvwQnaireRespVersion) element;
          return rvwQnaireRespVersion.getReviewedUser();
        }
        return "";
      }
    });
    // Add column selection listener
    reviewedUserColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(reviewedUserColumn.getColumn(), 5, this.respVersionsSorter, this.versionsTabViewer));

    GridViewerColumn createdDateColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.versionsTabViewer, "Created Date", COL_WIDTH_CRE_DATE);
    createdDateColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return getCreatedDate(element);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getToolTipText(final Object element) {
        return getCreatedDate(element);
      }

      /**
       * @param element
       * @return
       */
      private String getCreatedDate(final Object element) {
        if (element instanceof RvwQnaireRespVersion) {
          RvwQnaireRespVersion rvwQnaireRespVersion = (RvwQnaireRespVersion) element;
          return rvwQnaireRespVersion.getCreatedDate();
        }
        return "";
      }

    });
    // Add column selection listener
    createdDateColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(createdDateColumn.getColumn(), 6, this.respVersionsSorter, this.versionsTabViewer));

    GridViewerColumn createdUserColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.versionsTabViewer, "Created User", COL_WIDTH_CRE_USR);
    createdUserColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return getCreatedUser(element);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getToolTipText(final Object element) {
        return getCreatedUser(element);
      }

      /**
       * @param element
       * @return
       */
      private String getCreatedUser(final Object element) {
        if (element instanceof RvwQnaireRespVersion) {
          RvwQnaireRespVersion rvwQnaireRespVersion = (RvwQnaireRespVersion) element;
          return rvwQnaireRespVersion.getCreatedUser();
        }
        return "";
      }
    });
    // Add column selection listener
    createdUserColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(createdUserColumn.getColumn(), 7, this.respVersionsSorter, this.versionsTabViewer));
  }

  private void createNameDescStatusCols() {
    GridViewerColumn revNumColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.versionsTabViewer, "Revision", COL_WIDTH_REV_NO);
    revNumColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof RvwQnaireRespVersion) {
          RvwQnaireRespVersion rvwQnaireRespVersion = (RvwQnaireRespVersion) element;
          return rvwQnaireRespVersion.getRevNum().toString();
        }
        return "";
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getToolTipText(final Object element) {
        if (element instanceof RvwQnaireRespVersion) {
          RvwQnaireRespVersion rvwQnaireRespVersion = (RvwQnaireRespVersion) element;
          return "Revision Number : " + rvwQnaireRespVersion.getRevNum().toString();
        }
        return "";
      }

    });
    // Add column selection listener
    revNumColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(revNumColumn.getColumn(), 0, this.respVersionsSorter, this.versionsTabViewer));

    GridViewerColumn versionNameColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.versionsTabViewer, "Version Name", COL_WIDTH_NAME);
    versionNameColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return getVersionName(element);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getToolTipText(final Object element) {
        return getVersionName(element);
      }

      /**
       * @param element
       * @return
       */
      private String getVersionName(final Object element) {
        if (element instanceof RvwQnaireRespVersion) {
          RvwQnaireRespVersion rvwQnaireRespVersion = (RvwQnaireRespVersion) element;
          return rvwQnaireRespVersion.getVersionName();
        }
        return "";
      }

    });
    // Add column selection listener
    versionNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(versionNameColumn.getColumn(), 1, this.respVersionsSorter, this.versionsTabViewer));

    createVersDescCol();

    createStatusColumn();
  }

  /**
   *
   */
  private void createStatusColumn() {
    GridViewerColumn statusColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.versionsTabViewer, "Status", COL_WIDTH_STATUS);
    statusColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return "";
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Image getImage(final Object element) {
        Image statusImage = null;
        if (element instanceof RvwQnaireRespVersion) {
          RvwQnaireRespVersion rvwQnaireRespVersion = (RvwQnaireRespVersion) element;
          if (CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getDbType()
              .equals(rvwQnaireRespVersion.getQnaireRespVersStatus())) {
            statusImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.ALL_16X16);
          }
          else if (CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getDbType()
              .equals(rvwQnaireRespVersion.getQnaireRespVersStatus()) ||
              CDRConstants.QS_STATUS_TYPE.NOT_ALLOW_NEGATIVE.getDbType()
                  .equals(rvwQnaireRespVersion.getQnaireRespVersStatus())) {
            statusImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.EXCLAMATION_ICON_16X16);
          }
          else if (CDRConstants.QS_STATUS_TYPE.NOT_ANSWERED.getDbType()
              .equals(rvwQnaireRespVersion.getQnaireRespVersStatus()) ||
              CDRConstants.QS_STATUS_TYPE.NOT_ALLOWED_FINISHED_WP.getDbType()
                  .equals(rvwQnaireRespVersion.getQnaireRespVersStatus())) {
            statusImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.REMOVE_16X16);
          }
        }
        return statusImage;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getToolTipText(final Object element) {
        if (element instanceof RvwQnaireRespVersion) {
          RvwQnaireRespVersion rvwQnaireRespVersion = (RvwQnaireRespVersion) element;
          return CDRConstants.QS_STATUS_TYPE.getTypeByDbCode(rvwQnaireRespVersion.getQnaireRespVersStatus())
              .getUiType();
        }
        return "";
      }

    });
    // Add column selection listener
    statusColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(statusColumn.getColumn(), 3, this.respVersionsSorter, this.versionsTabViewer));
  }

  /**
   * create vers desc column
   */
  private void createVersDescCol() {
    GridViewerColumn versionDescColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.versionsTabViewer, "Remarks", COL_WIDTH_DESC);

    versionDescColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return getDescription(element);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getToolTipText(final Object element) {
        return getDescription(element);
      }

      /**
       * @param element
       * @return
       */
      private String getDescription(final Object element) {
        if (element instanceof RvwQnaireRespVersion) {
          RvwQnaireRespVersion rvwQnaireRespVersion = (RvwQnaireRespVersion) element;
          return rvwQnaireRespVersion.getDescription();
        }
        return "";
      }
    });
    // Add column selection listener
    versionDescColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(versionDescColumn.getColumn(), 2, this.respVersionsSorter, this.versionsTabViewer));
  }

  /**
   * Add filters for the table
   */
  private void addFilters() {
    this.qnaireVersFilter = new QnaireRespVersionsFilter();
    // Add TableViewer filter
    this.versionsTabViewer.addFilter(this.qnaireVersFilter);
  }

  /**
   * This method creates filter text
   */
  private void createFilterTxt() {
    // Filter text for table
    this.filterTxt.setLayoutData(getFilterTxtGridData());
    this.filterTxt.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.addModifyListener(event -> {
      final String text = this.filterTxt.getText().trim();
      this.qnaireVersFilter.setFilterText(text);
      this.versionsTabViewer.refresh();
    });
    // ICDM-183
    this.filterTxt.setFocus();
  }

  /**
   * This method returns filter text GridData object
   *
   * @return GridData
   */
  private GridData getFilterTxtGridData() {
    GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.verticalAlignment = GridData.CENTER;
    return gridData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart arg0, final ISelection arg1) {
    // No Implementation needed
  }


  /**
   * @return the addNewVersionAction
   */
  public Action getAddNewVersionAction() {
    return this.addNewVersionAction;
  }


  /**
   * @param addNewVersionAction the addNewVersionAction to set
   */
  public void setAddNewVersionAction(final Action addNewVersionAction) {
    this.addNewVersionAction = addNewVersionAction;
  }


  /**
   * @return the versionsTabViewer
   */
  public GridTableViewer getVersionsTabViewer() {
    return this.versionsTabViewer;
  }


  /**
   * @param versionsTabViewer the versionsTabViewer to set
   */
  public void setVersionsTabViewer(final GridTableViewer versionsTabViewer) {
    this.versionsTabViewer = versionsTabViewer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return this.dataHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    if (null != this.qnaireRespEditor.getQnaireRespVersionPage().getVersionsTabViewer()) {
      this.qnaireRespEditor.getQnaireRespVersionPage().getVersionsTabViewer()
          .setInput(this.dataHandler.getQnaireVersions(this.dataHandler.getQuesResponse().getId()));
      this.qnaireRespEditor.getQnaireRespVersionPage().getVersionsTabViewer().refresh();
    }
    if (null != this.addNewVersionAction) {
      try {
        this.addNewVersionAction.setEnabled(
            this.qnaireRespEditor.getQuesSummaryPge().checkQnaireAccessRights() && this.dataHandler.isModifiable());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().debug(e.getMessage(), e);
      }
    }
  }
}
