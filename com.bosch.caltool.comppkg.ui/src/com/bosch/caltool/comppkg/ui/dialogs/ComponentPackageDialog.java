/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.ui.dialogs;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.comppkg.ui.Activator;
import com.bosch.caltool.comppkg.ui.views.ComponentPackagesListViewPart;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.listeners.LinkTableSelectionListener;
import com.bosch.caltool.icdm.common.ui.sorter.LinkTableSorter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.views.data.LinkData;
import com.bosch.caltool.icdm.common.ui.views.providers.LinkTableLabelProvider;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.LinkServiceClient;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * ICDM-748
 *
 * @author bru2cob
 */
public class ComponentPackageDialog extends AbstractDialog {


  /**
   * width of description english column
   */
  private static final int DESC_ENG_COL_WIDTH = 120;
  /**
   * width of link column
   */
  private static final int LINK_COL_WIDTH = 100;
  /**
   * width of description german column
   */
  private static final int DESC_GER_COL_WIDTH = 74;
  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * Composite instance
   */
  private Composite top;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  /**
   * Section instance
   */
  private Section section;
  /**
   * Add new user button instance
   */
  private Button saveBtn;

  /**
   * Section for links
   */
  private Section sectionLink;
  /**
   * Form instance for links
   */
  private Form formLink;
  /**
   * Sorter instance
   */
  private LinkTableSorter linksTabSorter;

  /**
   * GridTableViewer for links
   */
  private GridTableViewer linksTabViewer;


  /**
   * Defines new link action in toolbar
   */
  private Action newLinkAction;
  /**
   * Defines edit link action in toolbar
   */
  private Action editLinkAction;

  /**
   * Defines delete link action in toolbar
   */
  private Action deleteLinkAction;

  /**
   * Form instance
   */
  private Form form;
  private final boolean addFlag;
  private String strTitle;
  private Text nameText;
  private Text descEngText;
  private Text descGerText;
  private ControlDecoration nameDecor;
  /**
   * Decorators instance
   */
  private final Decorators decorators = new Decorators();
  private ControlDecoration descDecor;
  private final ComponentPackagesListViewPart cpListView;

  /**
   * ICDM-1502 ok/cancel/close button pressed
   */
  protected boolean okCancelPress;

  /**
   * ICDM-1502 boolean to indicate links changed
   */
  protected boolean linksChanged;

  /**
   * @param parentShell shell
   * @param addFlag Add/Edit flag
   * @param componentPackagesListViewPart ViewPart
   */
  public ComponentPackageDialog(final Shell parentShell, final boolean addFlag,
      final ComponentPackagesListViewPart componentPackagesListViewPart) {
    super(parentShell);
    this.addFlag = addFlag;
    this.cpListView = componentPackagesListViewPart;
  }

  /**
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);

    setTitle(this.strTitle);
    setMessage("Enter the details", IMessageProvider.INFORMATION);

    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    if (this.addFlag) {
      this.strTitle = "Add a component package";
    }
    else {
      this.strTitle = "Edit a component package";
    }
    newShell.setText("Component Package");
    super.configureShell(newShell);
    super.setHelpAvailable(true);
  }

  /**
   * Creates the gray area
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());

    createComposite();
    return this.top;
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {
    this.composite = getFormToolkit().createComposite(this.top);

    createSection();
    if (!this.addFlag) {
      // ICDM-977
      createLinkSection();
    }
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.composite.setLayout(new GridLayout());
  }

  /**
   * ICDM-977 create link section
   */
  private void createLinkSection() {

    GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.minimumHeight = 150;
    gridData.minimumWidth = 300;

    this.sectionLink =
        SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), gridData, "Link Details");
    createLinkForm();
    this.sectionLink.setClient(this.formLink);

  }

  /**
   * create link form
   */
  private void createLinkForm() {
    this.linksTabSorter = new LinkTableSorter();

    this.formLink = getFormToolkit().createForm(this.sectionLink);
    this.formLink.getBody().setLayout(new GridLayout());

    GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.heightHint = SWT.DEFAULT; // iCDM-971
    this.linksTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.formLink.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, gridData);

    createToolBarAction();
    createTabColumns();

    // ICDM-1502
    this.linksTabViewer.setContentProvider(new IStructuredContentProvider() {

      @Override
      public void dispose() {
        // TODO Auto-generated method stub
      }

      @Override
      public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        if (!ComponentPackageDialog.this.okCancelPress) {
          ComponentPackageDialog.this.linksChanged = true;
          checkSaveBtnEnable();
        }
      }

      @Override
      public Object[] getElements(final Object inputElement) {
        if (inputElement instanceof SortedSet<?>) {
          return ((SortedSet) inputElement).toArray();
        }
        return null;
      }

    });

    this.linksTabViewer.setLabelProvider(new LinkTableLabelProvider());


    try {
      Map<Long, Link> cpLinkMap =
          new LinkServiceClient().getAllLinksByNode(this.cpListView.getSelectedCmpPkg().getId(), MODEL_TYPE.COMP_PKG);

      SortedSet<LinkData> linkDataSet =
          cpLinkMap.values().stream().map(LinkData::new).collect(Collectors.toCollection(TreeSet::new));
      this.linksTabViewer.setInput(linkDataSet);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    // Invoke TableViewer Column sorters
    invokeColumnSorter(this.linksTabSorter);

  }

  /**
   * Add sorter for the table columns
   */
  private void invokeColumnSorter(final AbstractViewerSorter sorter) {
    this.linksTabViewer.setComparator(sorter);
  }

  /**
   * creates the columns of access rights table viewer
   */
  private void createTabColumns() {
    createLinkColumn();

    createDescEngColumn();

    createDescGerColumn();

    this.linksTabViewer.getGrid().addSelectionListener(
        new LinkTableSelectionListener(this.linksTabViewer, this.editLinkAction, this.deleteLinkAction));
  }

  /**
   * create link column
   */
  private void createLinkColumn() {
    final GridViewerColumn linkColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.linksTabViewer, "Link", LINK_COL_WIDTH);

    linkColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(linkColumn.getColumn(),
            com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants.COLUMN_INDEX_0, this.linksTabSorter,
            this.linksTabViewer));
  }

  /**
   * creates desc Eng column
   */
  private void createDescEngColumn() {
    final GridViewerColumn descEngColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.linksTabViewer, "Description(Eng)", DESC_ENG_COL_WIDTH);

    descEngColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(descEngColumn.getColumn(),
            com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants.COLUMN_INDEX_1, this.linksTabSorter,
            this.linksTabViewer));
  }

  /**
   * creates desc Ger column
   */
  private void createDescGerColumn() {
    final GridViewerColumn descGerColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.linksTabViewer, "Description(Ger)", DESC_GER_COL_WIDTH);


    descGerColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(descGerColumn.getColumn(),
            com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants.COLUMN_INDEX_2, this.linksTabSorter,
            this.linksTabViewer));
  }

  /**
   * This method creates Section ToolBar actions
   */
  private void createToolBarAction() {

    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

    final ToolBar toolbar = toolBarManager.createControl(this.sectionLink);

    CommonActionSet comnActionSet = new CommonActionSet();

    this.newLinkAction = comnActionSet.addNewLinkAction(ComponentPackageDialog.this.linksTabViewer);
    toolBarManager.add(this.newLinkAction);
    this.editLinkAction = comnActionSet.addEditLinkAction(toolBarManager, ComponentPackageDialog.this.linksTabViewer);
    this.deleteLinkAction = comnActionSet.addDeleteLinkActionToSection(toolBarManager,
        ComponentPackageDialog.this.linksTabViewer, this.editLinkAction);


    toolBarManager.update(true);

    this.sectionLink.setTextClient(toolbar);
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
   * This method initializes section
   */
  private void createSection() {

    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), this.strTitle);

    createForm();
    this.section.setLayout(new GridLayout());
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.setClient(this.form);
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {

    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Save", true);
    this.saveBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    checkSaveBtnEnable();
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    GridLayout layout = new GridLayout();
    this.form.getBody().setLayout(layout);
    layout.numColumns = 2;
    getFormToolkit().createLabel(this.form.getBody(), "Name");
    this.nameText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.nameText.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    this.nameText.setFocus();

    this.nameDecor = new ControlDecoration(this.nameText, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.nameDecor, "This field is mandatory.");


    this.nameText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        checkSaveBtnEnable();
      }
    });


    getFormToolkit().createLabel(this.form.getBody(), "Description(English)");
    this.descEngText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.descEngText.setLayoutData(GridDataUtil.getInstance().getTextGridData());

    this.descDecor = new ControlDecoration(this.descEngText, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.descDecor, "This field is mandatory.");
    this.descEngText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        checkSaveBtnEnable();
      }
    });

    getFormToolkit().createLabel(this.form.getBody(), "Description(German)");
    this.descGerText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.descGerText.setLayoutData(GridDataUtil.getInstance().getTextGridData());

    this.descGerText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        checkSaveBtnEnable();
      }
    });

    if (!this.addFlag && (null != this.cpListView.getSelectedCmpPkg())) {
      this.nameText.setText(this.cpListView.getSelectedCmpPkg().getName());
      this.descEngText.setText(this.cpListView.getSelectedCmpPkg().getDescEng());
      if (null != this.cpListView.getSelectedCmpPkg().getDescGer()) {
        this.descGerText.setText(this.cpListView.getSelectedCmpPkg().getDescGer());
      }


    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {

    String nameCp = this.nameText.getText().trim();
    String descEng = this.descEngText.getText().trim();
    String descGer = this.descGerText.getText().trim();

    addToCommandStack(nameCp, descEng, descGer);

    this.okCancelPress = true;
    super.okPressed();
  }

  /**
   * This method creates a new record in TABV_ATTRIBUTES table
   *
   * @param nameEng
   * @param descEng
   * @param unit
   * @param valTypeItem
   * @param valTypeId
   * @param format
   * @param normFlagitem
   * @param specLinkFlag
   * @param partNumFlag
   * @param valMandatory
   */
  private void addToCommandStack(final String nameCp, final String descEng, final String nameGer) {
    try {
      if (this.addFlag) {
        CompPackage compPkg = new CompPackage();
        compPkg.setName(nameCp);
        compPkg.setDescEng(descEng);
        compPkg.setDescGer(nameGer);
        CompPackage compPkgCreated = this.cpListView.getDataHandler().createCompPkg(compPkg);
        refreshNode(compPkgCreated);
      }
      else {
        CompPackage selectedCmpPkg = this.cpListView.getSelectedCmpPkg();
        selectedCmpPkg.setName(nameCp);
        selectedCmpPkg.setDescEng(descEng);
        selectedCmpPkg.setDescGer(nameGer);
        this.cpListView.getDataHandler().updateCompPkg(selectedCmpPkg);

        updateLinks();
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }


  /**
   *
   */
  private void updateLinks() {
    CommonUiUtils.getInstance().createMultipleLinkService((SortedSet<LinkData>) this.linksTabViewer.getInput(),
        this.cpListView.getSelectedCmpPkg().getId(), MODEL_TYPE.COMP_PKG);
  }

  /**
   * This method validates text fields
   *
   * @return boolean
   */
  private boolean validateFields() {
    String nameCp = this.nameText.getText();
    String descEng = this.descEngText.getText();

    return (!CommonUtils.isEmptyString(nameCp) && !CommonUtils.isEmptyString(descEng));

  }

  private void refreshNode(final CompPackage addedComp) {
    Display.getDefault().syncExec(() -> selectNewCompPkg(addedComp));
  }

  /**
   * This method selects the newly created comp pkg in the viewer
   *
   * @param addedComp .
   */
  protected void selectNewCompPkg(final CompPackage addedComp) {
    this.cpListView.getTableViewer().setInput(this.cpListView.getDataHandler().getAllCompPackages());
    ComponentPackageDialog.this.cpListView.getTableViewer().refresh();

    this.cpListView.getTableViewer().setSelection(new StructuredSelection(addedComp), true);
  }

  /**
   * Validates save button enable or disable
   */
  private void checkSaveBtnEnable() {
    if (this.saveBtn != null) {
      // check only when save btn is not null
      this.saveBtn.setEnabled(validateFields());

      if (!this.addFlag) {
        // ICDM-1502 check if there is any changes in link section
        this.saveBtn.setEnabled(this.saveBtn.getEnabled() && this.linksChanged);

      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void cancelPressed() {
    this.okCancelPress = true;
    super.cancelPressed();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean close() {
    this.okCancelPress = true;
    return super.close();
  }
}
