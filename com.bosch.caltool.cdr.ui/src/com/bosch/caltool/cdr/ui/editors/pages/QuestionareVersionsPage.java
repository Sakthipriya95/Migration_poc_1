/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import java.util.SortedSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.jface.gridviewer.CheckEditingSupport;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.actions.CdrActionSet;
import com.bosch.caltool.cdr.ui.dialogs.QuestionnaireVersionEditDialog;
import com.bosch.caltool.cdr.ui.editors.ReviewQuestionaireEditor;
import com.bosch.caltool.cdr.ui.editors.ReviewQuestionaireEditorInput;
import com.bosch.caltool.cdr.ui.sorters.QuestionnaireVersionsSorter;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireDefBO;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireConstants.QUESTION_VERSION_STATUS;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;
import com.bosch.caltool.icdm.ws.rest.client.cdr.QnaireVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * First page of Questionare editor
 *
 * @author dmo5cob
 */
public class QuestionareVersionsPage extends AbstractFormPage {

  /**
   *
   */
  private static final int CHKBOX_COL_WIDTH = 35;
  /**
   * Label
   */
  private static final String VERSIONS_LABEL = "Versions";
  /**
   * Col width - Name
   */
  private static final int COL_WIDTH_NAME = 150;
  /**
   * Col width - Description
   */
  private static final int COL_WIDTH_DESC = 250;
  /**
   * Col width - Status
   */
  private static final int COL_WIDTH_STATUS = 70;
  /**
   * Col width - Active
   */
  private static final int COL_WIDTH_ACTIVE = 50;
  /**
   * Col width - Created Date
   */
  private static final int COL_WIDTH_CRE_DATE = 200;
  /**
   * Col width - Created User
   */
  private static final int COL_WIDTH_CRE_USR = 250;
  /**
   * editor instance
   */
  private final ReviewQuestionaireEditor editor;
  /**
   * Non scrollable form
   */
  private Form nonScrollableForm;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Section instance
   */
  private Section section;
  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * Form instance
   */
  private Form form;
  /**
   * GridTableViewer instance
   */
  private GridTableViewer versionsTabViewer;
  /**
   * User name col
   */
  private GridViewerColumn versionNameColumn;
  /**
   * User id col
   */
  private GridViewerColumn versionDescColumn;
  /**
   * Dept col
   */
  private GridViewerColumn statusColumn;
  /**
   * Grant access col
   */
  private GridViewerColumn activeColumn;
  /**
   * Add new version action
   */
  private Action addNewVersionAction;
  /**
   * Edit Version action
   */
  private Action editVersionAction;
  private GridViewerColumn createdDateColumn;
  private GridViewerColumn createdUserColumn;
  private QuestionnaireVersionsSorter versionsSorter;

  private final QnaireDefBO qnaireDefBo;

  private SortedSet<QuestionnaireVersion> allVersions;

  /**
   * @param editor ReviewQuestionaireEditor instance
   * @param qnaireDefBo QnaireDefBo
   */
  public QuestionareVersionsPage(final FormEditor editor, final QnaireDefBO qnaireDefBo) {
    super(editor, VERSIONS_LABEL, VERSIONS_LABEL);
    this.editor = (ReviewQuestionaireEditor) editor;
    this.qnaireDefBo = qnaireDefBo;
    this.allVersions = getTableInput();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {
    // Create an ordinary non scrollable form on which widgets are built
    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);
    // ICDM-208
    this.nonScrollableForm.setText("Questionnaire Versions");
    addHelpAction((ToolBarManager) this.nonScrollableForm.getToolBarManager());
    // instead of editor.getToolkit().createScrolledForm(parent); in superclass
    // formToolkit is obtained from managed form to create form within section
    ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);
  }


  /**
   *
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    this.formToolkit = managedForm.getToolkit();
    createComposite(this.formToolkit);
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
    this.section.setClient(this.form);
  }


  /**
   * This method initializes form
   */
  private void createForm(final FormToolkit toolkit) {
    this.form = toolkit.createForm(this.section);
    this.form.getBody().setLayout(new GridLayout());

    this.versionsTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, GridDataUtil.getInstance().getGridData());

    this.versionsSorter = new QuestionnaireVersionsSorter(this.qnaireDefBo);

    createVersionsTabColumns();

    this.versionsTabViewer.setContentProvider(ArrayContentProvider.getInstance());

    this.versionsTabViewer.setInput(this.allVersions);

    // add selection listener to the table
    this.versionsTabViewer.getGrid().addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        if ((getSelectedVersion() != null) && QuestionareVersionsPage.this.qnaireDefBo.isModifiable()) {
          QuestionareVersionsPage.this.editVersionAction.setEnabled(true);
        }
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent event) {
        // TODO Auto-generated method stub

      }
    });
    // Invoke TableViewer Column sorters
    invokeColumnSorter();

    this.versionsTabViewer.addDoubleClickListener(event -> {
      IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      Object selectedNode = selection.getFirstElement();

      if (selectedNode instanceof QuestionnaireVersion) {

        QuestionnaireVersion version = (QuestionnaireVersion) selectedNode;
        CdrActionSet actionSet = new CdrActionSet();

        actionSet.openQuestionnaireEditor(version);
      }
    });

    createToolBarAction();

  }

  /**
   * invoke column sorter for the versions table
   */
  private void invokeColumnSorter() {
    this.versionsTabViewer.setComparator(this.versionsSorter);
  }

  /**
   * This method creates Section ToolBar actions
   */
  private void createToolBarAction() {

    ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

    ToolBar toolbar = toolBarManager.createControl(this.section);

    addNewVersionAction(toolBarManager);

    addEditVerionActionToSection(toolBarManager);

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
        QuestionnaireVersionEditDialog versionEditDialog = new QuestionnaireVersionEditDialog(
            Display.getCurrent().getActiveShell(), QuestionareVersionsPage.this.formToolkit,
            QuestionareVersionsPage.this.qnaireDefBo, QuestionareVersionsPage.this);
        versionEditDialog.open();
      }
    };
    // Set the image for add version action
    this.addNewVersionAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));

    toolBarManager.add(this.addNewVersionAction);
    enableDisableAddVersBtn();
  }

  /**
   *
   */
  private void enableDisableAddVersBtn() {
    boolean validQuesExisting = false;
    if (CommonUtils.isNotEmpty(this.qnaireDefBo.getQnaireDefModel().getQuestionMap())) {
      for (Question ques : this.qnaireDefBo.getQnaireDefModel().getQuestionMap().values()) {
        if (!this.qnaireDefBo.isDeletedExt(ques.getId())) {
          validQuesExisting = true;
          break;
        }
      }
    }
    boolean isEnabled = this.qnaireDefBo.isModifiable() && validQuesExisting;
    this.addNewVersionAction.setEnabled(isEnabled);
  }

  /**
   * This method creates non defined filter action
   *
   * @param toolBarManager
   */
  private void addEditVerionActionToSection(final ToolBarManager toolBarManager) {
    // Create an action to delete the user
    this.editVersionAction = new Action("Edit", SWT.NONE) {

      @Override
      public void run() {
        QuestionnaireVersionEditDialog versionEditDialog =
            new QuestionnaireVersionEditDialog(Display.getCurrent().getActiveShell(),
                QuestionareVersionsPage.this.formToolkit, QuestionareVersionsPage.this.getSelectedVersion(),
                QuestionareVersionsPage.this.qnaireDefBo, QuestionareVersionsPage.this);
        versionEditDialog.open();
      }

    };

    // Set the image for delete the user
    this.editVersionAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));
    this.editVersionAction.setEnabled(false);
    toolBarManager.add(this.editVersionAction);
  }

  /**
   * @return QuestionnaireVersion
   */
  private QuestionnaireVersion getSelectedVersion() {
    QuestionnaireVersion version = null;
    IStructuredSelection selection = (IStructuredSelection) this.versionsTabViewer.getSelection();
    Object firstElement = selection.getFirstElement();
    if (firstElement instanceof QuestionnaireVersion) {
      version = (QuestionnaireVersion) firstElement;
    }
    return version;
  }

  /**
   *
   */
  private void createVersionsTabColumns() {
    createNameDescStatusCols();

    createActiveCol();

    this.createdDateColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(this.versionsTabViewer,
        "Created Date", COL_WIDTH_CRE_DATE);
    this.createdDateColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof QuestionnaireVersion) {
          QuestionnaireVersion version = (QuestionnaireVersion) element;
          return version.getCreatedDate();
        }
        return "";
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        if (element instanceof QuestionnaireVersion) {
          QuestionnaireVersion vers = (QuestionnaireVersion) element;
          if (CommonUtils.isEqual(vers,
              ((ReviewQuestionaireEditorInput) getEditorInput()).getSelQuestionareVersion())) {
            return Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
          }
        }
        return Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
      }
    });
    // Add column selection listener
    this.createdDateColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(this.createdDateColumn.getColumn(), 4, this.versionsSorter, this.versionsTabViewer));

    this.createdUserColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(this.versionsTabViewer,
        "Created User", COL_WIDTH_CRE_USR);
    this.createdUserColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof QuestionnaireVersion) {
          QuestionnaireVersion version = (QuestionnaireVersion) element;
          return version.getCreatedUser();
        }
        return "";
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        if (element instanceof QuestionnaireVersion) {
          QuestionnaireVersion vers = (QuestionnaireVersion) element;
          if (CommonUtils.isEqual(vers,
              ((ReviewQuestionaireEditorInput) getEditorInput()).getSelQuestionareVersion())) {
            return Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
          }
        }
        return Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
      }
    });
    // Add column selection listener
    this.createdUserColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(this.createdUserColumn.getColumn(), 5, this.versionsSorter, this.versionsTabViewer));
  }

  private void createNameDescStatusCols() {
    this.versionNameColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(this.versionsTabViewer,
        "Version Name", COL_WIDTH_NAME);
    this.versionNameColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof QuestionnaireVersion) {
          QuestionnaireVersion version = (QuestionnaireVersion) element;
          return QuestionareVersionsPage.this.qnaireDefBo.getVersionName(version);
        }

        return "";
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        if (element instanceof QuestionnaireVersion) {
          QuestionnaireVersion vers = (QuestionnaireVersion) element;
          if (CommonUtils.isEqual(vers,
              ((ReviewQuestionaireEditorInput) getEditorInput()).getSelQuestionareVersion())) {
            return Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
          }
        }
        return Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
      }
    });
    // Add column selection listener
    this.versionNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(this.versionNameColumn.getColumn(), 0, this.versionsSorter, this.versionsTabViewer));

    createVersDescCol();

    this.statusColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.versionsTabViewer, "Status", COL_WIDTH_STATUS);
    this.statusColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof QuestionnaireVersion) {
          QuestionnaireVersion version = (QuestionnaireVersion) element;
          return QUESTION_VERSION_STATUS.getType(version.getInworkFlag()).getUiType();
        }
        return "";
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        if (element instanceof QuestionnaireVersion) {
          QuestionnaireVersion vers = (QuestionnaireVersion) element;
          if (CommonUtils.isEqual(vers,
              ((ReviewQuestionaireEditorInput) getEditorInput()).getSelQuestionareVersion())) {
            return Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
          }
        }
        return Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
      }
    });
    // Add column selection listener
    this.statusColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(this.statusColumn.getColumn(), 2, this.versionsSorter, this.versionsTabViewer));
  }

  /**
   * create vers desc column
   */
  private void createVersDescCol() {
    this.versionDescColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(this.versionsTabViewer,
        "Version Description", COL_WIDTH_DESC);

    this.versionDescColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof QuestionnaireVersion) {
          QuestionnaireVersion version = (QuestionnaireVersion) element;
          return version.getDescription();
        }
        return "";
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        if (element instanceof QuestionnaireVersion) {
          QuestionnaireVersion vers = (QuestionnaireVersion) element;
          if (CommonUtils.isEqual(vers,
              ((ReviewQuestionaireEditorInput) getEditorInput()).getSelQuestionareVersion())) {
            return Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
          }
        }
        return Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
      }
    });
    // Add column selection listener
    this.versionDescColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(this.versionDescColumn.getColumn(), 1, this.versionsSorter, this.versionsTabViewer));
  }

  /**
   *
   */
  private void createActiveCol() {
    GridColumn grantGridCol = new GridColumn(this.versionsTabViewer.getGrid(), SWT.CHECK | SWT.CENTER);
    grantGridCol.setWidth(CHKBOX_COL_WIDTH);
    grantGridCol.setText(ApicConstants.USED_NO_DISPLAY);
    grantGridCol.setSummary(false);
    this.activeColumn = new GridViewerColumn(this.versionsTabViewer, grantGridCol);
    this.activeColumn.getColumn().setText("Active");
    this.activeColumn.getColumn().setWidth(COL_WIDTH_ACTIVE);
    this.activeColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        if (element instanceof QuestionnaireVersion) {
          QuestionnaireVersion version = (QuestionnaireVersion) element;
          final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
          if (version.getActiveFlag() != null) {
            gridItem.setChecked(cell.getVisualIndex(), version.getActiveFlag().equals(ApicConstants.CODE_YES));
          }
          // active version cannot be modified for working set

          gridItem.setCheckable(cell.getVisualIndex(), QuestionareVersionsPage.this.qnaireDefBo.isModifiable() &&
              !QuestionareVersionsPage.this.qnaireDefBo.isWorkingSet(version));
        }
      }
    });
    this.activeColumn.setEditingSupport(new CheckEditingSupport(this.activeColumn.getViewer()) {

      @Override
      public void setValue(final Object arg0, final Object arg1) {
        // get the active version
        QuestionnaireVersion activeVersion = null;
        for (QuestionnaireVersion version : QuestionareVersionsPage.this.allVersions) {
          if ((null != version.getActiveFlag()) && version.getActiveFlag().equals(ApicConstants.CODE_YES)) {
            activeVersion = version;
          }
        }

        boolean confirm;
        if ((boolean) arg1) {
          if (activeVersion == null) {
            // if active version is null , need not show the confirmation dialog
            confirm = true;
          }
          else {
            // when setting to active, show the confirm dialog that the active version's flag will be set to false
            confirm = MessageDialogUtils.getConfirmMessageDialog("Confirm changing active version",
                "The currently active version " + QuestionareVersionsPage.this.qnaireDefBo.getVersionName() +
                    " is set to not active because only one version can be active");
          }

        }
        else {
          // when setting to non active, show the confirm dialog that reviews cannot use questionnaire for review
          confirm = MessageDialogUtils.getConfirmMessageDialog("Confirm changing active flag",
              "There's no other active version at the moment. Setting all versions to non active won't allow the user to select this questionnaire for their reviews.");
        }
        if (confirm) {
          // ICDM-2027
          // creating edit command for questionnaire version
          updateQuestionnaireVersion((QuestionnaireVersion) arg0,
              (boolean) arg1 ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);
        }
        else {
          QuestionareVersionsPage.this.versionsTabViewer.update(arg0, null);
        }
      }


    });
    this.activeColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(this.activeColumn.getColumn(), 3, this.versionsSorter, this.versionsTabViewer));
  }


  private void updateQuestionnaireVersion(final QuestionnaireVersion oldVersion, final Object activeFlag) {
    QuestionnaireVersion versionToupdate = oldVersion;
    versionToupdate.setActiveFlag((String) activeFlag);
    try {
      new QnaireVersionServiceClient().update(versionToupdate);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }


  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return this.nonScrollableForm.getToolBarManager();
  }

  /**
   * @return
   */
  public SortedSet<QuestionnaireVersion> getTableInput() {
    this.allVersions = this.qnaireDefBo.getAllVersions();
    return this.allVersions;
  }

  /**
   *
   */
  public void refreshPage() {
    this.versionsTabViewer.setInput(getTableInput());
    // to enable or disable add button as per the changes in access rights of the user
    enableDisableAddVersBtn();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return this.editor.getEditorInput().getQuestionnaireEditorDataHandler();
  }

  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    if (this.versionsTabViewer != null) {
      refreshPage();
    }
  }
}
