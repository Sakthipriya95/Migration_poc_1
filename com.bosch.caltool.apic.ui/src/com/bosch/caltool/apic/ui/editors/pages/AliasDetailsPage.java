/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.apic.ui.editors.AliasDefEditorInput;
import com.bosch.caltool.apic.ui.editors.AliasDefinitionEditor;
import com.bosch.caltool.apic.ui.table.filters.AttrOutlineFilter;
import com.bosch.caltool.icdm.client.bo.apic.AliasDefEditorDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.AttrRootNode;
import com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseRootNode;
import com.bosch.caltool.icdm.client.bo.uc.UserFavUcRootNode;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.rcputils.griddata.GridDataUtil;


/**
 * ICDM-1135
 *
 * @author bru2cob
 */
public class AliasDetailsPage extends AbstractFormPage implements ISelectionListener {


  /**
   * Search editor page columns
   */
  private static final int SASHFORM_COL = 2;

  /**
   * Sash form size limit1
   */
  private static final int WGHT2 = 4;
  /**
   * Sash form size limit2
   */
  private static final int WGHT1 = 4;

  /**
   * Instance of formtoolkit
   */
  private FormToolkit formToolkit;
  /**
   * Instance of non scrollable form
   */
  private Form nonScrollableForm;
  /**
   * Instance of sash form
   */
  private SashForm sashForm;

  /**
   * Outline filter
   */
  private AttrOutlineFilter outlineFilter;


  /**
   * Instance of results section
   */
  private final AliasAttributeSection attrSection;

  /**
   * Instance of attributes tree section
   */
  private final AliasValueSection valSection;

  /**
   * Instance of attributes tree methods
   */
  private final PIDCSearchAttrTreeUtil attrTreeUtil;


  /**
   * @param editor editor instance
   * @param editorId editor id
   * @param title title0..
   */
  public AliasDetailsPage(final FormEditor editor, final String editorId, final String title) {
    super(editor, editorId, title);
    this.attrSection = new AliasAttributeSection(this);
    this.valSection = new AliasValueSection(this);
    this.attrTreeUtil = null;
  }

  /**
   * @return the sashForm
   */
  public SashForm getSashForm() {
    return this.sashForm;
  }


  /**
   * @return the valSection
   */
  public AliasValueSection getValSection() {
    return this.valSection;
  }

  /**
   * @return the attrSection
   */
  public AliasAttributeSection getAttrSection() {
    return this.attrSection;
  }

  /**
   * @return the attrTreeUtil
   */
  public PIDCSearchAttrTreeUtil getAttrTreeUtil() {
    return this.attrTreeUtil;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public AliasDefEditorInput getEditorInput() {
    return (AliasDefEditorInput) super.getEditorInput();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AliasDefinitionEditor getEditor() {
    return (AliasDefinitionEditor) super.getEditor();
  }


  @Override
  public void createPartControl(final Composite parent) {

    // create a main form
    this.nonScrollableForm = getEditor().getToolkit().createForm(parent);

    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.nonScrollableForm.getBody().setLayout(new GridLayout());
    this.nonScrollableForm.getBody().setLayoutData(gridData);
    this.nonScrollableForm.setText(getDataHandler().getAliasDef().getName());

    addHelpAction((ToolBarManager) this.nonScrollableForm.getToolBarManager());
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = SASHFORM_COL;

    // add a sashform with two columns
    this.sashForm = new SashForm(this.nonScrollableForm.getBody(), SWT.HORIZONTAL);
    this.sashForm.setLayout(gridLayout);
    this.sashForm.setLayoutData(gridData);

    final ManagedForm mform = new ManagedForm(parent);

    // create the form content
    createFormContent(mform);
  }

  /**
   * Create form content
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    this.formToolkit = managedForm.getToolkit();

    this.attrSection.createResultsSection();
    // create attrs section
    this.valSection.createAttrValSection();
    // create pidc search results section

    // set the width of two section
    this.sashForm.setWeights(new int[] { WGHT1, WGHT2 });
    // add listeners
    getSite().getPage().addSelectionListener(this);

    this.attrSection.getResultTable().getGrid().setSelection(0);
    // Call the change input
    changeTabInp();
    GridItem[] selection = this.attrSection.getResultTable().getGrid().getSelection();
    Attribute attr = (Attribute) selection[0].getData();
    this.attrSection.setSelectedAttr(attr);
    this.attrSection.toggleButtons(attr);
    initializeOutlineFilter();
  }


  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }


  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  public FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setFocus() {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart iworkbenchpart, final ISelection iselection) {
//    refresh alias details page based on the outline selection
    if ((getSite().getPage().getActiveEditor() == getEditor()) && (iworkbenchpart instanceof OutlineViewPart)) {
      outLineSelectionListener(iselection);
      AliasDetailsPage.this.attrSection.getResultTable().refresh();
    }

  }


  /**
   * Adds the outline selection listener to the attribute tree viewer
   *
   * @param selection selection
   */
  private void outLineSelectionListener(final ISelection selection) {
    if ((selection != null) && !selection.isEmpty() && (selection instanceof IStructuredSelection)) {
      Object first = ((IStructuredSelection) selection).getFirstElement();
      // Check if selection is SuperGroup
      checkAttrSelection(first);
      if ((first instanceof AttrRootNode) || (first instanceof UseCaseRootNode) ||
          (first instanceof UserFavUcRootNode)) {
        this.outlineFilter.setGroup(false);
        this.outlineFilter.setSuperGroup(false);
        this.outlineFilter.setCommon(true);
        this.outlineFilter.setFilterText("");
      }
//      if any usecase is selected in the outline window
      else if (first instanceof IUseCaseItemClientBO) {
        final IUseCaseItemClientBO ucItem = (IUseCaseItemClientBO) first;
        this.outlineFilter.setUseCaseItem(ucItem);
      }
//    if any private usecase is selected in the outline window
      else if (first instanceof FavUseCaseItemNode) {
        this.outlineFilter.setFavUseCaseItem((FavUseCaseItemNode) first);
      }
      this.valSection.getValueAliasTable().setInput(null);
    }


  }


  /**
   * @param first selected element
   */
  private void checkAttrSelection(final Object first) {
//    check selected attribute is attribute super group or attribute group
    if (first instanceof AttrSuperGroup) {
      this.outlineFilter.setGroup(false);
      this.outlineFilter.setSuperGroup(true);
      this.outlineFilter.setCommon(false);
      AttrSuperGroup attrSuperGroup = (AttrSuperGroup) first;
      this.outlineFilter.setFilterText(attrSuperGroup.getName());
    }
    else if (first instanceof AttrGroup) {
      this.outlineFilter.setGroup(true);
      this.outlineFilter.setSuperGroup(false);
      AttrGroup attrGroup = (AttrGroup) first;
      this.outlineFilter.setParentSuperGroup(getEditorInput().getOutlineDataHandler().getAttrRootNode()
          .getAttrGroupModel().getAllSuperGroupMap().get(attrGroup.getSuperGrpId()).getName());
      this.outlineFilter.setFilterText(attrGroup.getName());
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    this.attrSection.refresh();
    this.valSection.refresh();
  }


  /**
   * Initializes the outline filter
   */
  public void initializeOutlineFilter() {
    this.outlineFilter = new AttrOutlineFilter(getEditorInput().getOutlineDataHandler());
    this.attrSection.getResultTable().addFilter(this.outlineFilter);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return this.nonScrollableForm.getToolBarManager();
  }

  /**
   * change the right side table input.
   */
  public void changeTabInp() {
//    selected attribute
    GridItem[] selection = this.attrSection.getResultTable().getGrid().getSelection();
//    refresh the value alias section based on the selected attribute alias
    if (CommonUtils.isNotNull(selection) && (selection.length > 0)) {
      Attribute data = (Attribute) selection[0].getData();
      this.valSection.setSelectedAttr(data);
      this.valSection.refresh();
    }

  }

  /**
   * @return data handler
   */
  @Override
  public AliasDefEditorDataHandler getDataHandler() {
    return getEditorInput().getDataHandler();
  }

}
