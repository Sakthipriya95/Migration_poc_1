/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.ui.providers.DSTSelectionContentProvider;
import com.bosch.caltool.icdm.common.ui.providers.DSTSelectionLabelProvider;
import com.bosch.caltool.icdm.model.vcdm.VCDMApplicationProject;
import com.bosch.caltool.icdm.model.vcdm.VCDMDSTRevision;
import com.bosch.caltool.icdm.model.vcdm.VCDMProductKey;
import com.bosch.caltool.icdm.model.vcdm.VCDMProgramkey;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * A Dialog for displaying the DSTs using a specific A2LFile
 *
 * @author jvi6cob
 */
public class DSTSelectionDialog extends AbstractDialog {

  /**
   * Composite instance for the dialog
   */
  private Composite composite;
  /**
   * save button
   */
  protected Button selectBtn;
  /**
   * cancel button
   */
  protected Button cancelBtn;
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
  /**
   * Composite instance
   */
  private Composite top;

  private final List<VCDMApplicationProject> vcdmAPRJS;

  private final String a2lFileName;


  private TreeViewer treeViewer;
  private VCDMDSTRevision vcdmdstRevision;
  private String selectedTreePath;


  /**
   * Constructor
   *
   * @param parametersNatFormPage ParametersNatFormPage
   * @param parentShell Shell
   * @param vcdmAPRJS VCDMApplicationProject
   * @param a2lFileName String
   */
  public DSTSelectionDialog(final Shell parentShell, final List<VCDMApplicationProject> vcdmAPRJS,
      final String a2lFileName) {
    super(parentShell);
    this.vcdmAPRJS = vcdmAPRJS;
    this.a2lFileName = a2lFileName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Select DST");
    super.configureShell(newShell);
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
    setTitle("VCDM DST(s)");
    // Set the message
    setMessage("DST(s) using " + this.a2lFileName, IMessageProvider.INFORMATION);
    return contents;
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    // Configure select and cancel buttons
    this.selectBtn = createButton(parent, IDialogConstants.OK_ID, "Select", true);
    this.selectBtn.setEnabled(false);
    this.cancelBtn = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    TreeSelection selection = (TreeSelection) this.treeViewer.getSelection();
    if (selection.getFirstElement() instanceof VCDMDSTRevision) {
      this.vcdmdstRevision = (VCDMDSTRevision) selection.getFirstElement();
      TreePath parentPath = selection.getPaths()[0].getParentPath();

      // Get the vcdm program key from last segment
      VCDMProgramkey programkey = (VCDMProgramkey) parentPath.getLastSegment();
      VCDMProductKey vcdmVariant = (VCDMProductKey) parentPath.getSegment(1);
      VCDMApplicationProject applicationProject = (VCDMApplicationProject) parentPath.getFirstSegment();
      this.selectedTreePath = applicationProject.getAprjName() + " : " + vcdmVariant.getVariantName() + " : " +
          programkey.getProgramKeyName() + " : " + this.vcdmdstRevision.getRevisionNo();

      super.okPressed();
    }

  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets .Composite)
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
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(gridData);
    this.section.getDescriptionControl().setEnabled(false);
  }

  /**
   * This method initializes section
   */
  private void createSection() {
    // Creation section
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "DST(s) using " + this.a2lFileName);
    createForm();
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);

    final GridData gridData = new GridData();
    // Apply grid data styles
    applyGridDataStyles(gridData);
    Tree tree = getFormToolkit().createTree(this.form.getBody(), SWT.BORDER);
    this.treeViewer = new TreeViewer(tree);
    // Get viewer and set styled layout for tree
    this.treeViewer.getTree().setLayoutData(gridData);
    // Set Content provider for the tree
    this.treeViewer.setContentProvider(new DSTSelectionContentProvider());
    // Set Label provider for the tree
    this.treeViewer.setLabelProvider(new DSTSelectionLabelProvider());
    this.treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

      @Override
      public void selectionChanged(final SelectionChangedEvent event) {
        DSTSelectionDialog.this.checkSaveBtnEnable();
      }
    });
    // Call to build tree using setInput()
    this.treeViewer.setInput(this.vcdmAPRJS);

    addDoubleClickListener();

  }

  /**
   * This method defines the activities to be performed when double clicked on the tree
   */
  private void addDoubleClickListener() {
    // Double click listener action
    this.treeViewer.addDoubleClickListener(new IDoubleClickListener() {

      @Override
      public void doubleClick(final DoubleClickEvent doubleclickevent) {
        Display.getDefault().asyncExec(new Runnable() {

          @Override
          public void run() {
            okPressed();
          }
        });
      }

    });
  }

  private void applyGridDataStyles(final GridData gridData) {
    // Apply the standard styles
    gridData.verticalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.widthHint = 200;
    gridData.heightHint = 300;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.RESIZE | SWT.TITLE);
    setBlockOnOpen(false);
  }

  /**
   * checks whether the save button can be enabled or not
   */
  private void checkSaveBtnEnable() {
    // Perform validations

    this.selectBtn.setEnabled(validateFields());

  }

  /**
   * @return
   */
  private boolean validateFields() {
    boolean isValid = false;
    IStructuredSelection selection = (IStructuredSelection) this.treeViewer.getSelection();
    Object selectedElement = selection.getFirstElement();
    if (selectedElement instanceof VCDMDSTRevision) {
      // If selected elemenet is DST revision
      isValid = true;
    }
    return isValid;
  }


  /**
   * @return the vcdmdstRevision
   */
  public VCDMDSTRevision getVcdmdstRevision() {
    return this.vcdmdstRevision;
  }


  /**
   * @return the selectedTreePath
   */
  public String getSelectedTreePath() {
    return this.selectedTreePath;
  }

}
