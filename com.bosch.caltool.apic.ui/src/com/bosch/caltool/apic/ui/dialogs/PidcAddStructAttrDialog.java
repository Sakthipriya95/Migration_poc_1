package com.bosch.caltool.apic.ui.dialogs;

import java.util.Collection;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.table.filters.PIDCAttrFilters;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.apic.ui.views.providers.PidcAddStructAttrTableLabelProvider;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDetStructure;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcDetStructureServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * This class provides a dialog to select & add virtual level attributes from variant attributes
 */
// ICDM-395
/**
 * @author dmo5cob
 */
public class PidcAddStructAttrDialog extends AbstractDialog {

  /**
   * Add new user button instance
   */
  private Button okBtn;
  /**
   * GridTableViewer instance for add new user
   */
  private GridTableViewer varAttrsTableViewer;
  /**
   * Composite instance
   */
  private Composite top;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * Section instance
   */
  private Section section;
  /**
   * Form instance
   */
  private Form form;

  // Title
  private static final String ADD_NEW_STRUCTURE_ATTR = "Add New Structure Attribute";

  // Dialog message
  private static final String THIS_IS_TO_ADD_NEW_ATTR = "This is to add Structure Attribute";


  private static final String ADD_LABEL = "Add";
  /**
   * PIDCard version instance
   */
  private final PidcVersion pidcVer;

  // selected PIDc attribute
  private IProjectAttribute selectedPidcAttr;

  private static final boolean addNodeActionEnabled = true;

  /**
   * Filter text instance
   */
  private Text filterTxt;

  private PIDCAttrFilters pidcAttrFilter;
  private final long level;
  private final Collection<IProjectAttribute> values;
  private final PidcVersionBO pidcVersionBO;

  /**
   * @param shell the parent shell
   * @param level attribute level
   * @param pidcVersionBO PidcVersionBO
   * @param values2 collection of proj attribute
   */
  public PidcAddStructAttrDialog(final Shell shell, final long level, final PidcVersionBO pidcVersionBO,
      final Collection<IProjectAttribute> values2) {
    super(shell);
    this.pidcVersionBO = pidcVersionBO;
    this.pidcVer = pidcVersionBO.getPidcVersion();
    this.level = level;
    this.values = values2;
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
    setTitle(PidcAddStructAttrDialog.ADD_NEW_STRUCTURE_ATTR);

    // Set the message
    setMessage(PidcAddStructAttrDialog.THIS_IS_TO_ADD_NEW_ATTR, IMessageProvider.INFORMATION);

    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(PidcAddStructAttrDialog.ADD_NEW_STRUCTURE_ATTR);
    super.configureShell(newShell);
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
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * This method initializes section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), "List of attributes");
    createForm();
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    // Create Filter text
    createFilterTxt();
    // Create new users grid tableviewer
    createVarAttrsGridTabViewer();

    // Set ContentProvider and LabelProvider to TableViewer
    setTabViewerProviders();

    // Set input to the TableViewer
    setTabViewerInput();

    // Add selection listener to the TableViewer
    addTableSelectionListener();

    // Add double click listener
    addDoubleClickListener();

    // Add filters to the TableViewer
    addFilters();


    this.form.getBody().setLayout(new GridLayout());

  }

  /**
   * adding double click listener to the table viewer
   */
  private void addDoubleClickListener() {

    // Add double click listener for table
    this.varAttrsTableViewer.addDoubleClickListener(doubleclickevent -> Display.getDefault().asyncExec(() -> {

      final IStructuredSelection selection =
          (IStructuredSelection) PidcAddStructAttrDialog.this.varAttrsTableViewer.getSelection();
      if (CommonUtils.isNotNull(selection) && CommonUtils.isNotEqual(selection.size(), 0)) {
        boolean enable = addVirtualLvlSelectedAttr(selection);
        if (enable) {
          okPressed();
        }
      }
    }));
  }

  /**
   * This method adds the filter instance to pidc/variant/sub-variant attribute value
   */
  private void addFilters() {
    this.pidcAttrFilter = new PIDCAttrFilters(this.pidcVersionBO.getPidcDataHandler());
    // Add PIDC Attribute TableViewer filter
    this.varAttrsTableViewer.addFilter(this.pidcAttrFilter);

  }

  /**
   * This method creates filter text
   */
  private void createFilterTxt() {
    // Filter text for table
    this.filterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.addModifyListener(event -> {
      final String text = PidcAddStructAttrDialog.this.filterTxt.getText().trim();
      PidcAddStructAttrDialog.this.pidcAttrFilter.setFilterText(text);
      PidcAddStructAttrDialog.this.varAttrsTableViewer.refresh();
    });
    // ICDM-183
    this.filterTxt.setFocus();

  }

  /**
   * This method adds selection listener to the addNewUserTableViewer
   */
  private void addTableSelectionListener() {
    // Add selection listener for table
    this.varAttrsTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        addVirtualLvlAttrFromVarAttr();
      }
    });
  }

  /**
   * Add virtual level attributes from variant attributes
   */
  private void addVirtualLvlAttrFromVarAttr() {
    final IStructuredSelection selection =
        (IStructuredSelection) PidcAddStructAttrDialog.this.varAttrsTableViewer.getSelection();
    if (CommonUtils.isNotNull(selection) && CommonUtils.isNotEqual(selection.size(), 0)) {
      PidcAddStructAttrDialog.this.okBtn.setEnabled(addVirtualLvlSelectedAttr(selection));
    }
    else {
      PidcAddStructAttrDialog.this.okBtn.setEnabled(false);
      PidcAddStructAttrDialog.this.selectedPidcAttr = null;
    }
  }


  /**
   * @param selection
   */
  private boolean addVirtualLvlSelectedAttr(final IStructuredSelection selection) {
    boolean enable = false;
    final Object element = selection.getFirstElement();
    if (element instanceof IProjectAttribute) {
      enable = true;
      PidcAddStructAttrDialog.this.selectedPidcAttr = (IProjectAttribute) element;
      for (PidcDetStructure pidDetStruct : PidcAddStructAttrDialog.this.pidcVersionBO.getVirtualLevelAttrs().values()) {
        if (pidDetStruct.getAttrId().longValue() == PidcAddStructAttrDialog.this.selectedPidcAttr.getAttrId()
            .longValue()) {
          enable = false;
        }
      }
    }
    return enable;
  }

  /**
   * This method sets the input to the TableViewer
   */
  private void setTabViewerInput() {

    this.varAttrsTableViewer.setInput(this.values);
  }

  /**
   * This method sets ContentProvider & LabelProvider to the addNewUserTableViewer
   */
  private void setTabViewerProviders() {
    this.varAttrsTableViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.varAttrsTableViewer.setLabelProvider(new PidcAddStructAttrTableLabelProvider(this.pidcVersionBO));
  }

  /**
   * This method adds Columns to the addNewUserTableViewer
   */
  private void createGridViewerColumns() {
    createAttrNameColumn();
    createDescColumn();
  }

  /**
   * This method adds user name column to the addNewUserTableViewer
   */
  private void createAttrNameColumn() {
    GridViewerColumnUtil.getInstance().createGridViewerColumn(this.varAttrsTableViewer, "Attribute Name", 200);
  }

  /**
   * This method adds user id column to the addNewUserTableViewer
   */
  private void createDescColumn() {
    GridViewerColumnUtil.getInstance().createGridViewerColumn(this.varAttrsTableViewer, "Description", 300);
  }

  /**
   * This method creates the addNewUserTableViewer
   *
   * @param gridData
   */
  private void createVarAttrsGridTabViewer() {
    // Create table viewer
    this.varAttrsTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL,
        GridDataUtil.getInstance().getHeightHintGridData(200));
    // Create GridViewerColumns
    createGridViewerColumns();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {

    if (this.selectedPidcAttr != null) {

      PidcDetStructureServiceClient pidcDetStructClient = new PidcDetStructureServiceClient();
      PidcDetStructure pidcDetStruct = new PidcDetStructure();
      pidcDetStruct.setAttrId(this.selectedPidcAttr.getAttrId());
      pidcDetStruct.setPidcVersId(this.pidcVer.getId());
      pidcDetStruct.setPidAttrLevel(this.level);
      try {
        pidcDetStructClient.create(pidcDetStruct);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, com.bosch.caltool.apic.ui.Activator.PLUGIN_ID);
      }
    }
    super.okPressed();

  }

  /**
   * {@inheritDoc}
   */
  // ICDM-153
  @Override
  protected boolean isResizable() {
    return true;
  }

  /**
   * Creates the buttons for the button bar
   *
   * @param parent the parent composite
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.okBtn = createButton(parent, IDialogConstants.OK_ID, PidcAddStructAttrDialog.ADD_LABEL, false);
    this.okBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
  }

  public boolean isAddNodeActionEnabled() {
    return PidcAddStructAttrDialog.addNodeActionEnabled;
  }
}