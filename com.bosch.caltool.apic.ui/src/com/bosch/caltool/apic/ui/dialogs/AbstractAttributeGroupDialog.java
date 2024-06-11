package com.bosch.caltool.apic.ui.dialogs;

import java.util.SortedSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;
import com.bosch.rcputils.ui.forms.SectionUtil;
import com.bosch.rcputils.ui.validators.Validator;

/**
 * This class provides a dialog to add/edit Attribute Super Group and Group ICDM-139
 */
public abstract class AbstractAttributeGroupDialog extends AbstractDialog {

  /**
   * constant for link table height
   */
  private static final int LINK_TBL_HEIGHT = 80;
  /**
   * width of desc column in link table
   */
  private static final int DESC_COL_WIDTH = 150;
  /**
   * width of link colummn in link table
   */
  private static final int LINK_COL_WIDTH = 180;
  /**
   * Button instance for save
   */
  private Button saveBtn;
  /**
   * Composite instance for the dialog
   */
  private Composite composite;
  /**
   * Value
   */
  protected String attrValue;
  /**
   * German Value
   */
  protected String attrValueGer;
  /**
   * German Desc
   */
  protected String descGer;
  /**
   * Desc
   */
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
   * Form instance
   */
  private Form form;
  private Composite top;
  /**
   * Section for links
   */
  private Section sectionLink;

  /**
   * Label and Text for English and German Name and Desc
   */

  protected Label engLabel;

  /**
   * English name
   */
  protected Text nameEngText;

  /**
   * German label
   */
  protected Label gerLabel;
  /**
   * German name textfield
   */
  protected Text nameGermText;

  /**
   * German value label
   */
  protected Label valueDescengLabel;
  /**
   * Desc english text field
   */
  protected Text descEngText;

  /**
   * Desc German label
   */
  protected Label valueDescGerLabel;
  /**
   * Desc german textfield
   */
  protected Text descGermText;
  /**
   * empty filler label
   */
  protected Label emptyLabel;

  /**
   * Decorator for english description text field
   */
  private ControlDecoration txtValEngDec;
  private ControlDecoration engDec;

  /**
   * super grp name lable
   */
  protected Label superGrpNameLbl;
  /**
   * super grp name text
   */
  protected Text superGrpNameTxt;

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
   * Form instance for links
   */
  private Form formLink;
  /**
   * Sorter instance
   */
  private LinkTableSorter linksTabSorter;
  /**
   * Defines new link action in toolbar
   */
  protected Action newLinkAction;


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
   * The Parameterized Constructor
   *
   * @param parentShell instance
   * @param linkSectionNeeded .
   */
  // ICDM-108
  public AbstractAttributeGroupDialog(final Shell parentShell, final boolean linkSectionNeeded) {
    super(parentShell);
    this.linkSectionNeeded = linkSectionNeeded;
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
    // ICDM-929
    GridData gridData = GridDataUtil.getInstance().getGridData();
    if (this.linkSectionNeeded) {
      gridData.heightHint = 400;
      gridData.widthHint = 540;
    }
    this.top.setLayoutData(gridData);
    this.top.setLayout(new GridLayout());
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
    // ICDM-929
    if (this.linkSectionNeeded) {
      createSection2();
    }
    this.composite.setLayoutData(gridData);
  }

  /**
   * ICDM-929 creates section with table for links
   */
  private void createSection2() {
    this.sectionLink = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Link Details");
    createForm2();
    this.sectionLink.setClient(this.formLink);

  }

  /**
   * ICDM-929 creates form with table for links
   */
  private void createForm2() {
    this.linksTabSorter = new LinkTableSorter();

    this.formLink = getFormToolkit().createForm(this.sectionLink);
    this.formLink.getBody().setLayout(new GridLayout());

    GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.heightHint = LINK_TBL_HEIGHT;
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
        if (!AbstractAttributeGroupDialog.this.okCancelPress) {
          AbstractAttributeGroupDialog.this.linksChanged = true;
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

    this.linksTabViewer.getGrid().addSelectionListener(
        new LinkTableSelectionListener(this.linksTabViewer, this.editLinkAction, this.deleteLinkAction));

    // Invoke TableViewer Column sorters
    invokeColumnSorter(this.linksTabSorter);
  }


  /**
   * ICDM-929 This method creates Section ToolBar actions
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
   * ICDM-929 creates edit link action in the toolbar and handles the action
   *
   * @param toolBarManager ToolBarManager
   */
  protected abstract void addEditLinkAction(final ToolBarManager toolBarManager);

  /**
   * ICDM-929 creates add new link icon in the toolbar and handles the action
   *
   * @param toolBarManager ToolBarManager
   */
  protected abstract void addNewLinkAction(final ToolBarManager toolBarManager);

  /**
   * ICDM-929 creates delete link icon in the toolbar and handles the action
   *
   * @param toolBarManager ToolBarManager
   */
  protected abstract void addDeleteLinkActionToSection(final ToolBarManager toolBarManager);

  /**
   * ICDM-929 Add sorter for the table columns
   */
  private void invokeColumnSorter(final AbstractViewerSorter sorter) {
    this.linksTabViewer.setComparator(sorter);
  }

  /**
   * ICDM-929 creates the columns of access rights table viewer
   */
  private void createTabColumns() {
    createLinkColumn();

    createDescEngColumn();

    createDescGerColumn();

    this.linksTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection =
            (IStructuredSelection) AbstractAttributeGroupDialog.this.linksTabViewer.getSelection();
        if (selection != null) {
          AbstractAttributeGroupDialog.this.deleteLinkAction.setEnabled(true);
          AbstractAttributeGroupDialog.this.editLinkAction.setEnabled(true);
        }
      }
    });
  }

  /**
   * ICDM-929 create link column
   */
  private void createLinkColumn() {
    final GridViewerColumn linkColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.linksTabViewer, "Link", LINK_COL_WIDTH);

    linkColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(linkColumn.getColumn(), 0, this.linksTabSorter, this.linksTabViewer));
  }

  /**
   * ICDM-929 creates desc Eng column
   */
  private void createDescEngColumn() {
    final GridViewerColumn descEngColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.linksTabViewer, "Description(Eng)", DESC_COL_WIDTH);

    descEngColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(descEngColumn.getColumn(), 1, this.linksTabSorter, this.linksTabViewer));
  }

  /**
   * ICDM-929 creates desc Ger column
   */
  private void createDescGerColumn() {
    final GridViewerColumn descGerColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.linksTabViewer, "Description(Ger)", 150);

    descGerColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(descGerColumn.getColumn(), 2, this.linksTabSorter, this.linksTabViewer));
  }

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
    final GridData txtGrid = getTextFieldGridData();
    showSuperGroup(txtGrid, this.form, getFormToolkit());

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
    GridData gridDataTextArea = getTextAreaGridData();
    this.valueDescengLabel = getFormToolkit().createLabel(this.form.getBody(), "Description (English): ");

    this.descEngText =
        getFormToolkit().createText(this.form.getBody(), null, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
    this.descEngText.setLayoutData(gridDataTextArea);
    this.engDec = new ControlDecoration(this.descEngText, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.engDec, IUtilityConstants.MANDATORY_MSG);

    this.emptyLabel = getFormToolkit().createLabel(this.form.getBody(), "");
    this.emptyLabel = getFormToolkit().createLabel(this.form.getBody(), "");
    this.emptyLabel = getFormToolkit().createLabel(this.form.getBody(), "");
    this.valueDescGerLabel = getFormToolkit().createLabel(this.form.getBody(), "Description (German): ");

    this.descGermText =
        getFormToolkit().createText(this.form.getBody(), null, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
    this.descGermText.setLayoutData(gridDataTextArea);
    this.emptyLabel = getFormToolkit().createLabel(this.form.getBody(), "");
    this.nameEngText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {

        Validator.getInstance().validateNDecorate(AbstractAttributeGroupDialog.this.engDec,
            AbstractAttributeGroupDialog.this.txtValEngDec, AbstractAttributeGroupDialog.this.descEngText,
            AbstractAttributeGroupDialog.this.nameEngText, true);

        checkSaveBtnEnable();
      }
    });
    this.descEngText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {

        Validator.getInstance().validateNDecorate(AbstractAttributeGroupDialog.this.engDec,
            AbstractAttributeGroupDialog.this.txtValEngDec, AbstractAttributeGroupDialog.this.descEngText,
            AbstractAttributeGroupDialog.this.nameEngText, true);

        checkSaveBtnEnable();
      }
    });
    // ICDM-112
  }

  /**
   * ICDM 451 returns the layout needed for text area
   *
   * @return GridData
   */
  private GridData getTextAreaGridData() {
    GridData gridData2 = new GridData();
    gridData2.grabExcessHorizontalSpace = true;
    gridData2.horizontalAlignment = GridData.FILL;
    gridData2.verticalAlignment = GridData.FILL;
    gridData2.verticalSpan = 2;
    gridData2.heightHint = 40;
    gridData2.grabExcessVerticalSpace = true;
    return gridData2;
  }

  /**
   * This method can be implemented to show the super group UI controls
   *
   * @param txtGrid instance
   * @param formObj instance
   * @param formToolkit2 instance
   */
  public abstract void showSuperGroup(GridData txtGrid, Form formObj, FormToolkit formToolkit2);

  /**
     *
     */
  // ICDM-112
  private void checkSaveBtnEnable() {
    this.saveBtn.setEnabled(validateFields());
  }

  /**
   * This method validates text & combo fields
   *
   * @return boolean
   */
  // ICDM-112
  private boolean validateFields() {

    if (!this.nameEngText.isDisposed()) {
      String value = this.nameEngText.getText().trim();
      final String valDescEng = this.descEngText.getText();

      if (!"".equals(value.trim()) && !"".equals(valDescEng.trim())) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return
   */
  private GridData getTextFieldGridData() {
    final GridData gridData2 = new GridData();
    gridData2.grabExcessHorizontalSpace = true;
    gridData2.horizontalAlignment = GridData.FILL;
    gridData2.verticalAlignment = GridData.CENTER;
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


}