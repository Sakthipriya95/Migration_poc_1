package com.bosch.caltool.usecase.ui.dialogs;

import java.util.SortedSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.IStructuredContentProvider;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.listeners.LinkTableSelectionListener;
import com.bosch.caltool.icdm.common.ui.sorter.LinkTableSorter;
import com.bosch.caltool.icdm.common.ui.views.providers.LinkTableLabelProvider;
import com.bosch.rcputils.IUtilityConstants;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;
import com.bosch.rcputils.text.TextBoxContentDisplay;
import com.bosch.rcputils.ui.forms.SectionUtil;
import com.bosch.rcputils.ui.validators.Validator;

/**
 * This class provides a dialog to add/edit Attribute Super Group and Group ICDM-139
 */
public abstract class AbstractUseCaseDialog extends AbstractDialog {

  /**
   * Button instance for save
   */
  protected Button saveBtn;

  /**
   * Composite instance for the dialog
   */
  private Composite composite;

  protected String attrValue;
  protected String attrValueGer;
  protected String descGer;
  protected String descEng;
  /**
   * Decorators instance
   */
  private final Decorators decorators = new Decorators();

  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Section instance
   */
  private Section section;
  /**
   * Section for links
   */
  private Section sectionLink;

  /**
   * Form instance
   */
  private Form form;
  /**
   * Form instance for links
   */
  private Form formLink;
  private Composite top;

  /**
   * Defines new link action in toolbar
   */
  protected Action newLinkAction;

  /**
   * Defines edit link action in toolbar
   */
  protected Action editLinkAction;

  /**
   * Defines delete link action in toolbar
   */
  protected Action deleteLinkAction;

  /**
   * boolean that tells whether link section needs to be shown or not
   */
  private final boolean linkSectionNeeded;


  /**
   * Label and Text for English and German Name and Desc
   */

  protected Label engLabel;

  protected Text nameEngText;

  protected Label gerLabel;
  protected Text nameGermText;

  protected Label valueDescengLabel;
  protected Text descEngText;

  protected Label valueDescGerLabel;
  protected Text descGermText;
  protected Label emptyLabel;

  private ControlDecoration txtValEngDec;
  private ControlDecoration engDec;
  protected Label superGrpNameLbl;
  protected Text superGrpNameTxt;

  /**
   * Sorter instance
   */
  private LinkTableSorter linksTabSorter;

  /**
   * GridTableViewer for links
   */
  protected GridTableViewer linksTabViewer;

  /**
   * boolean to indicate ok or cancel is pressed
   */
  protected boolean okCancelPress;

  /**
   * boolean to indicate links are changed
   */
  protected boolean linksChanged;

  /**
   * ICDM-1558 check box for relevancy to focus matrix
   */
  protected Button chkBtnFocusMatrixRelev;

  /**
   * boolean to indicate relevancy to focus matrix
   */
  private final boolean relevantToFocusMatrix;

  /**
   * button to get user preference to open usecase editor on creation
   */
  protected Button chkBtnOpenEditorChkBox;

  /**
   * the maximum length for the descripton of english & german field
   */
  private static final int MAX_TEXT_BOX_SIZE = 4000;

  /**
   * The Parameterized Constructor
   *
   * @param parentShell instance
   * @param linkSectionNeeded boolean that tells whether link section needs to be shown or not
   * @param relevantToFocusMatrix boolean to indicate relevancy to focus matrix check box to be shown
   */
  // ICDM-108
  public AbstractUseCaseDialog(final Shell parentShell, final boolean linkSectionNeeded,
      final boolean relevantToFocusMatrix) {
    super(parentShell);
    this.linkSectionNeeded = linkSectionNeeded;
    this.relevantToFocusMatrix = relevantToFocusMatrix;
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

    // Set the title
    setTitle("");

    // Set the message
    setMessage("");

    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    super.configureShell(newShell);
    // ICDM-153
    super.setHelpAvailable(true);
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse .swt.widgets.Composite)
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Save", true);
    // ICDM-112
    this.saveBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets .Composite)
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    GridData gridData = GridDataUtil.getInstance().getGridData();
    if (this.linkSectionNeeded) {
      gridData.heightHint = 400;
      gridData.widthHint = 540;
    }
    this.top.setLayoutData(gridData);
    createComposite();
    return this.top;
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
   * This method initializes composite
   */
  private void createComposite() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    createSection1();
    if (this.linkSectionNeeded) {
      createSection2();
    }
    this.composite.setLayoutData(gridData);
  }

  /**
   * creates section with table for links
   */
  private void createSection2() {
    this.sectionLink = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Link Details");
    createForm2();
    this.sectionLink.setClient(this.formLink);

  }

  /**
   * creates form with table for links
   */
  private void createForm2() {
    this.linksTabSorter = new LinkTableSorter();

    this.formLink = getFormToolkit().createForm(this.sectionLink);
    this.formLink.getBody().setLayout(new GridLayout());

    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.linksTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.formLink.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, gridData);

    createToolBarAction();
    createTabColumns();

    this.linksTabViewer.setContentProvider(new IStructuredContentProvider() {

      @Override
      public void dispose() {
        // TODO Auto-generated method stub
      }

      @Override
      public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        if (!AbstractUseCaseDialog.this.okCancelPress) {
          AbstractUseCaseDialog.this.linksChanged = true;
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
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.linksTabViewer, "Link", 200);

    linkColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(linkColumn.getColumn(), 0, this.linksTabSorter, this.linksTabViewer));
  }

  /**
   * creates desc Eng column
   */
  private void createDescEngColumn() {
    final GridViewerColumn descEngColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.linksTabViewer, "Description(Eng)", 154);

    descEngColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(descEngColumn.getColumn(), 1, this.linksTabSorter, this.linksTabViewer));
  }

  /**
   * creates desc Ger column
   */
  private void createDescGerColumn() {
    final GridViewerColumn descGerColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.linksTabViewer, "Description(Ger)", 150);

    descGerColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(descGerColumn.getColumn(), 2, this.linksTabSorter, this.linksTabViewer));
  }

  /**
   * This method creates Section ToolBar actions
   */
  private void createToolBarAction() {

    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

    final ToolBar toolbar = toolBarManager.createControl(this.sectionLink);

    addNewLinkAction(toolBarManager);

    addEditLinkAction(toolBarManager);

    addDeleteLinkActionToSection(toolBarManager);


    toolBarManager.update(true);

    this.sectionLink.setTextClient(toolbar);
  }

  /**
   * creates edit link action in the toolbar and handles the action
   *
   * @param toolBarManager
   */
  protected abstract void addEditLinkAction(final ToolBarManager toolBarManager);

  /**
   * creates add new link icon in the toolbar and handles the action
   *
   * @param toolBarManager ToolBarManager
   */
  protected abstract void addNewLinkAction(final ToolBarManager toolBarManager);

  /**
   * creates delete link icon in the toolbar and handles the action
   *
   * @param toolBarManager ToolBarManager
   */
  protected abstract void addDeleteLinkActionToSection(final ToolBarManager toolBarManager);

  /**
   * This method initializes section
   */
  private void createSection1() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Enter the details");
    createForm();
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);
    final GridData txtGrid = GridDataUtil.getInstance().getTextGridData();
    final GridData txtGrid2 = getTextAreaGridData();


    this.engLabel = getFormToolkit().createLabel(this.form.getBody(), "Name(English)");
    this.nameEngText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.nameEngText.setLayoutData(txtGrid);
    // ICDM-183
    this.nameEngText.setFocus();
    this.txtValEngDec = new ControlDecoration(this.nameEngText, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.txtValEngDec, IUtilityConstants.MANDATORY_MSG);

    this.emptyLabel = getFormToolkit().createLabel(this.form.getBody(), "");
    this.gerLabel = getFormToolkit().createLabel(this.form.getBody(), "Name(German)");
    this.nameGermText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.nameGermText.setLayoutData(txtGrid);

    this.emptyLabel = getFormToolkit().createLabel(this.form.getBody(), "");
    this.valueDescengLabel = getFormToolkit().createLabel(this.form.getBody(), "Description (English): ");

    // iCDM-2007
    TextBoxContentDisplay boxContentDisplay = new TextBoxContentDisplay(this.form.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, MAX_TEXT_BOX_SIZE, txtGrid2);
    this.descEngText = boxContentDisplay.getText();
    this.engDec = new ControlDecoration(this.descEngText, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.engDec, IUtilityConstants.MANDATORY_MSG);

    this.emptyLabel = getFormToolkit().createLabel(this.form.getBody(), "");
    this.emptyLabel = getFormToolkit().createLabel(this.form.getBody(), "");
    this.emptyLabel = getFormToolkit().createLabel(this.form.getBody(), "");
    this.valueDescGerLabel = getFormToolkit().createLabel(this.form.getBody(), "Description (German): ");

    // iCDM-2007
    boxContentDisplay = new TextBoxContentDisplay(this.form.getBody(), SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL,
        MAX_TEXT_BOX_SIZE, txtGrid2);
    this.descGermText = boxContentDisplay.getText();
    this.emptyLabel = getFormToolkit().createLabel(this.form.getBody(), "");
    this.nameEngText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {

        Validator.getInstance().validateNDecorate(AbstractUseCaseDialog.this.engDec,
            AbstractUseCaseDialog.this.txtValEngDec, AbstractUseCaseDialog.this.descEngText,
            AbstractUseCaseDialog.this.nameEngText, true);

        checkSaveBtnEnable();
      }
    });
    this.descEngText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {

        Validator.getInstance().validateNDecorate(AbstractUseCaseDialog.this.engDec,
            AbstractUseCaseDialog.this.txtValEngDec, AbstractUseCaseDialog.this.descEngText,
            AbstractUseCaseDialog.this.nameEngText, true);

        checkSaveBtnEnable();
      }
    });
    // ICDM-112
    createEmptyLabel(2);
    LabelUtil.getInstance().createLabel(this.form.getBody(), "Open Editor on Creation");

    this.chkBtnOpenEditorChkBox = new Button(this.form.getBody(), SWT.CHECK);

    // ICDM-1558
    if (this.relevantToFocusMatrix) {
      createEmptyLabel(4);
      LabelUtil.getInstance().createLabel(this.form.getBody(), "Relevant for Focus matrix");

      this.chkBtnFocusMatrixRelev = new Button(this.form.getBody(), SWT.CHECK);
    }

  }

  /**
   * Method to create empty label for specified count
   */
  private void createEmptyLabel(final int emptyLabelCount) {
    for (int createdCount = 0; createdCount < emptyLabelCount; createdCount++) {
      LabelUtil.getInstance().createEmptyLabel(this.form.getBody());
    }
  }

  /**
     *
     */
  // ICDM-112
  protected void checkSaveBtnEnable() {
    this.saveBtn.setEnabled(validateFields());
  }

  /**
   * This method validates text & combo fields
   *
   * @return boolean
   */
  // ICDM-112
  private boolean validateFields() {
    String value;
    boolean returnVal = false;
    if (!this.nameEngText.isDisposed()) {
      value = this.nameEngText.getText().trim();
      final String valDescEng = this.descEngText.getText();

      if (!"".equals(value.trim()) && !"".equals(valDescEng.trim())) {
        returnVal = true;
      }

    }
    return returnVal;
  }

  /**
   * ICDM 451 returns the layout needed for text area
   *
   * @return GridData
   */
  private GridData getTextAreaGridData() {
    final GridData gridData2 = new GridData();
    gridData2.grabExcessHorizontalSpace = true;
    gridData2.horizontalAlignment = GridData.FILL;
    gridData2.verticalAlignment = GridData.FILL;
    gridData2.verticalSpan = 2;
    gridData2.heightHint = 40;
    gridData2.grabExcessVerticalSpace = true;
    return gridData2;
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
  protected void okPressed() {
    this.okCancelPress = true;
    super.okPressed();
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