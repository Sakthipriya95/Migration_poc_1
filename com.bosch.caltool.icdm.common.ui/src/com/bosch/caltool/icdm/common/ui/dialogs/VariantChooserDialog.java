/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.dialogs;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.ProjectObjectWithAttributes;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * This class provides a dialog to select the variant value
 */
public class VariantChooserDialog extends AbstractDialog {

  /**
   * Shell title
   */
  private static final String SHELL_TITLE = "Select a variant";
  /**
   * Dialog title
   */
  private static final String DIALOG_TITLE = "Select a variant";
  /**
   * dialog meassage
   */
  private static final String MESSAGE_TXT = "Select a variant from the list";

  private static final String OKBTN_LBL = "Select";
  /**
   * Width hint for UI controls
   */
  private static final int WIDTH_HINT = 200;
  /**
   * Add new user button instance
   */
  protected Button okBtn;
  /**
   * GridTableViewer instance for add new user
   */
  protected GridTableViewer tabViewer;
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

  private PidcVariant selVariant;

  private final PidcVersion pidcVer;

  private final SortedSet<Attribute> attrsSet;
  private final Map<Long, PidcVersionAttribute> pidcVersAttrMap;
  private Map<Long, ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute>> variantObjMap;

  /**
   * @param shell the parent shell
   * @param pidcVer version
   * @param attrSet set of relevant attributes
   * @param pidcVersAttrMap pidc Vers Attr Map
   */
  public VariantChooserDialog(final Shell shell, final PidcVersion pidcVer, final SortedSet<Attribute> attrSet,
      final Map<Long, PidcVersionAttribute> pidcVersAttrMap) {
    super(shell);
    this.pidcVer = pidcVer;
    this.attrsSet = attrSet;
    this.pidcVersAttrMap = pidcVersAttrMap;


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
    setTitle(VariantChooserDialog.DIALOG_TITLE);
    // Set the message
    setMessage(VariantChooserDialog.MESSAGE_TXT, IMessageProvider.INFORMATION);

    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(VariantChooserDialog.SHELL_TITLE);
    super.configureShell(newShell);
    // ICDM-153
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
    // create composite
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
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), "List of variants");
    createForm();
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.getDescriptionControl().setEnabled(false);
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);

    // Create tableviewer
    createTabViewer();
    // Set ContentProvider and LabelProvider to addNewUserTableViewer
    setTabViewerProviders();
    // Set input to the addNewUserTableViewer
    setTabViewerInput();
    // Add selection listener to the addNewUserTableViewer
    addTableSelectionListener();
    // Adds double click selection listener to the addNewUserTableViewer
    addDoubleClickListener();
    // Invokde GridColumnViewer sorter
    this.form.getBody().setLayout(new GridLayout());


  }

  /**
   * ICDM 574-This method defines the activities to be performed when double clicked on the table
   *
   * @param functionListTableViewer2
   */
  private void addDoubleClickListener() {
    this.tabViewer.addDoubleClickListener(e -> Display.getDefault().asyncExec(VariantChooserDialog.this::okPressed));
  }

  /**
   * This method adds selection listener to the addNewUserTableViewer
   */
  private void addTableSelectionListener() {
    this.tabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection =
            (IStructuredSelection) VariantChooserDialog.this.tabViewer.getSelection();
        if (selection == null) {
          VariantChooserDialog.this.okBtn.setEnabled(false);
        }
        else {

          final Object element = selection.getFirstElement();
          if (element instanceof PidcVariant) {
            VariantChooserDialog.this.selVariant = (PidcVariant) element;
            VariantChooserDialog.this.okBtn.setEnabled(true);
          }
        }


      }
    });
  }

  /**
   * This method sets the input to the addNewUserTableViewer
   */
  private void setTabViewerInput() {
    SortedSet<PidcVariant> varSet = new TreeSet<>();
    try {
      Set<Long> attrIdSet = this.pidcVersAttrMap.values().stream().filter(PidcVersionAttribute::isAtChildLevel)
          .map(PidcVersionAttribute::getAttrId).collect(Collectors.toSet());
      this.variantObjMap = new PidcVariantServiceClient().getVariantsWithAttrs(this.pidcVer.getId(), attrIdSet);
      this.variantObjMap.values().stream().map(ProjectObjectWithAttributes::getProjectObject).forEach(varSet::add);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    this.tabViewer.setInput(varSet);
  }

  /**
   * This method sets ContentProvider & LabelProvider to the addNewUserTableViewer
   */
  private void setTabViewerProviders() {
    this.tabViewer.setContentProvider(ArrayContentProvider.getInstance());

  }

  /**
   * This method adds Columns to the addNewUserTableViewer
   */
  private void createGridViewerColumns() {
    createVariantNameColumn();
    createAttrsCol();
  }


  /**
   * Depending on number of variant attrs columns are created
   */
  private void createAttrsCol() {
    for (final Attribute attr : this.attrsSet) {
      PidcVersionAttribute versAttr = this.pidcVersAttrMap.get(attr.getId());
      // if the attr is variant column is created
      if ((versAttr != null) && versAttr.isAtChildLevel()) {
        final GridViewerColumn attrsColumn =
            GridViewerColumnUtil.getInstance().createGridViewerColumn(this.tabViewer, attr.getName(), WIDTH_HINT);

        attrsColumn.setLabelProvider(new ColumnLabelProvider() {

          @Override
          public String getText(final Object element) {
            return getVarAttrVal(attr, (PidcVariant) element);
          }
        });
      }
    }
  }

  /**
   * This method adds var name column
   */
  private void createVariantNameColumn() {
    final GridViewerColumn varNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.tabViewer, "Variant", WIDTH_HINT);

    varNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        return ((PidcVariant) element).getName();
      }

    });
  }


  /**
   * This method creates the addNewUserTableViewer
   *
   * @param gridData
   */
  private void createTabViewer() {

    this.tabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL,
        GridDataUtil.getInstance().getHeightHintGridData(WIDTH_HINT));

    // Create GridViewerColumns
    createGridViewerColumns();
  }


  /**
   * Creates the buttons for the button bar
   *
   * @param parent the parent composite
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.okBtn = createButton(parent, IDialogConstants.OK_ID, VariantChooserDialog.OKBTN_LBL, false);
    this.okBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
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
   * @param attr
   * @param attrsColumn
   * @return variant atribute value
   */
  private String getVarAttrVal(final Attribute attr, final PidcVariant var) {
    PidcVariantAttribute varAttr = this.variantObjMap.get(var.getId()).getProjectAttrMap().get(attr.getId());
    return varAttr == null ? "" : CommonUtils.checkNull(varAttr.getValue());
  }

  /**
   * @return the selVariant
   */
  public PidcVariant getSelVariant() {
    return this.selVariant;
  }

}