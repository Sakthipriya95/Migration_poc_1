/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode.PIDC_TREE_NODE_TYPE;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNodeHandler;
import com.bosch.caltool.icdm.client.bo.apic.TreeViewFlagValueProvider;
import com.bosch.caltool.icdm.common.ui.providers.PIDTreeViewContentProvider;
import com.bosch.caltool.icdm.common.ui.providers.PIDTreeViewLabelProvider;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.SdomPVER;
import com.bosch.rcputils.griddata.GridDataUtil;


/**
 * @author rgo7cob Changes made from abstarct class to ordinary class The method setSelectionForTreeViewer has empty
 *         implementation here
 */
public class PIDCVaraintSelDialog extends AbstractDialog {

  /**
   * composite top
   */
  protected Composite top;
  /**
   * form tool kit
   */
  protected FormToolkit formToolkit;
  /**
   * composite instance
   */
  protected Composite composite;
  /**
   * tree viewer
   */
  protected TreeViewer viewer;
  /**
   * save button
   */
  protected Button saveBtn;
  /**
   * selected variant
   */
  protected PidcVariant selVar;
  /**
   * selected PIDC version
   */
  protected PidcVersion selPidcVer;
  /**
   * selected PIDC version
   */
  protected PidcA2l selPidcA2l;

  /**
   * selected SDOM Pver
   */
  protected SdomPVER sdomPver;
  /**
   * Instance of pidc tree node handler
   */
  private PidcTreeNodeHandler treeHandler;

  /**
   * Tree Expansion level
   */
  protected static final int TREE_EXPANSION_LEVEL = 2;


  /**
   * @param parentShell parentShell
   */
  public PIDCVaraintSelDialog(final Shell parentShell) {

    super(parentShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.DIALOG_TRIM);
  }


  /**
   * Applies styles to GridData
   *
   * @param gridData
   */
  protected Composite applyGridDataStyles(final Composite parentComposite, final GridData gridData) {
    // Apply the standard styles
    gridData.verticalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    // Create GridLayout
    GridLayout gridLayout = new GridLayout();
    Composite comp = new Composite(parentComposite, SWT.NONE);
    comp.setLayoutData(gridData);
    comp.setLayout(gridLayout);
    return comp;
  }

  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    final GridData gridData = GridDataUtil.getInstance().getGridData();
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
   * create composite
   */
  private void createComposite() {

    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite.setLayoutData(gridData);
    createContent();
  }

  /**
   * Create composite data
   */
  protected void createContent() {
    initializeDialogUnits(this.composite);
    Composite topComposite = new Composite(this.composite, SWT.NONE);
    topComposite.setLayout(new GridLayout());
    topComposite.setLayoutData(GridDataUtil.getInstance().getGridData());
    GridData gridData = new GridData();
    // Apply grid data styles
    Composite childComposite = applyGridDataStyles(topComposite, gridData);
    // Create filters
    PatternFilter filter = new PatternFilter();
    FilteredTree tree = new FilteredTree(childComposite, SWT.BORDER, filter, true);
    // Get viewer and set styled layout for tree
    this.viewer = tree.getViewer();
    this.viewer.getTree().setLayoutData(gridData);
    // set auto expand level
    this.viewer.setAutoExpandLevel(TREE_EXPANSION_LEVEL);


    this.treeHandler = CommonUiUtils.getInstance().getPidcViewPartHandlerIfPresent();
    // Set Content provider for the tree
    // iCDM-1982
    this.viewer.setContentProvider(new PIDTreeViewContentProvider(this.treeHandler, new TreeViewFlagValueProvider(false, false, true, false, false, false, false)));
    // Set Label provider for the tree
    this.viewer.setLabelProvider(new PIDTreeViewLabelProvider(this.treeHandler, this.viewer));

    // Call to build tree using setInput(), EMPTY string object indicates to
    // create root node in Content provider
    this.viewer.setInput("");
    this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {

      @Override
      public void selectionChanged(final SelectionChangedEvent event) {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        Object selected = selection.getFirstElement();
        setPIDCAndVariant(selected);
      }
    });
    // Add the doublle click listener.
    addDoubleClickListener();
    // add tree selection listener
    setSelectionForTreeViewer();
  }

  /**
   * set the Selection
   */
  protected void setSelectionForTreeViewer() {
    // No implementation here
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Select", true);
    // ICDM-112
    this.saveBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * @param selected Set the selected Pidc Version and Varaint
   */
  protected void setPIDCAndVariant(final Object selected) {
    // Selection for variant
    if (selected instanceof PidcTreeNode) {
      PidcTreeNode selectedTreeNode = (PidcTreeNode) selected;
      this.sdomPver = selectedTreeNode.getSdomPver();
      if (selectedTreeNode.getNodeType() == PIDC_TREE_NODE_TYPE.PIDC_VAR_NODE) {
        this.selPidcVer = selectedTreeNode.getPidcVersion();
        this.selVar = selectedTreeNode.getPidcVariant();
        this.saveBtn.setEnabled(true);
      }
      else if ((selectedTreeNode.getNodeType() == PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION) ||
          (selectedTreeNode.getNodeType() == PIDC_TREE_NODE_TYPE.OTHER_PIDC_VERSION)) {
        this.selVar = null;
        this.selPidcVer = selectedTreeNode.getPidcVersion();
        if (CommonUtils.isNotNull(this.saveBtn)) {
          this.saveBtn.setEnabled(true);
        }
      }
      else if (selectedTreeNode.getNodeType() == PIDC_TREE_NODE_TYPE.PIDC_A2L) {
        this.selVar = null;
        this.selPidcVer = selectedTreeNode.getPidcVersion();
        this.selPidcA2l = selectedTreeNode.getPidcA2l();
        if (CommonUtils.isNotNull(this.saveBtn)) {
          this.saveBtn.setEnabled(true);
        }
      }
      // Selection for Other elements.
      else {
        this.selVar = null;
        this.selPidcVer = null;
        this.selPidcA2l = null;
        if (CommonUtils.isNotNull(this.saveBtn)) {
          this.saveBtn.setEnabled(false);
        }
      }
    }
    else if (selected == null) {
      this.saveBtn.setEnabled(false);
    }

  }

  /**
   * Add the double click listener
   */
  protected void addDoubleClickListener() {
    this.viewer.addDoubleClickListener(new IDoubleClickListener() {

      @Override
      public void doubleClick(final DoubleClickEvent event) {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        Object selected = selection.getFirstElement();
        // Set the selected PIDC and Variant
        setPIDCAndVariant(selected);
        // call Ok pressed
        okPressed();
      }

    });
  }


  /**
   * @return the selPidcVer
   */
  public PidcVersion getSelPidcVer() {
    return this.selPidcVer;
  }


  /**
   * @return the treeHandler
   */
  public PidcTreeNodeHandler getTreeHandler() {
    return this.treeHandler;
  }

  /**
   * @return the selPidcA2l
   */
  public PidcA2l getSelPidcA2l() {
    return this.selPidcA2l;
  }

}
