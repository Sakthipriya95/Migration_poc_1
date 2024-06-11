/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.actions.AddValidityValAction;
import com.bosch.caltool.apic.ui.dialogs.AddNewPredefinedAttrDialog;
import com.bosch.caltool.apic.ui.dialogs.EditValueDialog;
import com.bosch.caltool.apic.ui.listeners.PredefinedAttrTableViewerListeners;
import com.bosch.caltool.apic.ui.sorter.PredefinedAttrValTabSorter;
import com.bosch.caltool.apic.ui.table.filters.PredefinedAttrValTabFilter;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.apic.ui.views.providers.PredefinedAttrColLblProvider;
import com.bosch.caltool.apic.ui.views.providers.PredefinedValEditColLblProvider;
import com.bosch.caltool.icdm.client.bo.apic.ApicDataBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO;
import com.bosch.caltool.icdm.client.bo.apic.PredefinedAttrValuesValidityModel;
import com.bosch.caltool.icdm.common.ui.combo.BasicObjectCombo;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * ICDM-2593 Dialog to add i) validity attribute & validity attribute values ii) Predefined attributes & predefined
 * attribute values
 *
 * @author dja7cob
 */
public class PredefinedAttributesPage extends AbstractFormPage {

  /**
   * assigning width for the column
   */
  private static final int COL_WIDTH = 350;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * Validity attribute selected in the combo
   */
  private Attribute selectedValidityAttr;
  /**
   * Non scrollable form
   */
  private Form nonScrollableForm;
  /**
   * Section instance
   */
  private Section section;
  /**
   * Form instance
   */
  private Form form;
  /**
   * Combo instance
   */
  private BasicObjectCombo<Attribute> comboValidityAttributes;
  /**
   * Text instance to display the validity attribute values
   */
  private Text validityAttrValuesText;
  /**
   * Button to open a dialog to select the validity attribute values
   */
  private Button addLvlAttrValButton;
  /**
   * Tableviewer instance to display the predefined attributes and values
   */
  private GridTableViewer predefinedAttrValTableViewer;
  /**
   * DepAttrValTabFilter GridTableViewer filter instance
   */
  private PredefinedAttrValTabFilter predefinedAttrValTabFilter;
  /**
   * Defines AbstractViewerSorter - Predefined Attribite Value GridTableViewer sorter
   */
  private AbstractViewerSorter predefinedAttrValSorter;
  /**
   * Filter text instance
   */
  private Text filterTxt;
  /**
   * Action to add the predefined attributes
   */
  private Action addPredfndAttrAction;
  /**
   * Action to delete the predefined attributes
   */
  private Action delPredfndAttrAction;

  /**
   * Set of Validity attribute values selected from the AddValidityAttrValDialog
   */
  private SortedSet<AttributeValue> selValidityAttrValues = new TreeSet<>();
  /**
   * List of predefined attributes selected for the attribute value to be edited
   */
  private final SortedSet<Attribute> predfndAttrList = new TreeSet<>();
  /**
   * List of predefined attributes selected for deletion
   */
  private final SortedSet<Attribute> selPredefAttrListforDel = new TreeSet<>();
  /**
   * Map with selected predefined attribute and value
   */
  private final Map<Attribute, AttributeValue> selPredefinedAttrValMap = new HashMap<>();
  /**
   * EditValueDialog instance
   */
  private final EditValueDialog editValDialog;

  private final Map<Long, Attribute> attrsMap;
  private final AttributeValueClientBO selectedAttrValClientBO;
  private PredefinedAttrValuesValidityModel existingValidity;
  private Set<PredefinedAttrValue> existingPreDefinedAttrValueSet;

  /**
   * @param editValueDialog Edit value Dialog
   * @param attributeValue selected attribute values
   * @param attrsMap map of all attributes
   */
  public PredefinedAttributesPage(final EditValueDialog editValueDialog, final AttributeValue attributeValue,
      final Map<Long, Attribute> attrsMap) {
    super("Predefined Attribute Values", "");

    this.selectedAttrValClientBO = new AttributeValueClientBO(attributeValue);
    this.editValDialog = editValueDialog;
    this.attrsMap = attrsMap;
    setExistingValues();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Class<?> getMainUIClass() {
    return EditValueDialog.class;
  }

  /**
   * Set existing values from DB
   */
  private void setExistingValues() {
    // If the attribute value selected for editing has validity attributes already in database, display it in the combo
    // comboValidityAttributes
    this.existingValidity = this.selectedAttrValClientBO.getValidity(getAttrsMap());
    if ((null != this.existingValidity) && (null != this.existingValidity.getValidityAttribute())) {
      this.selectedValidityAttr = this.existingValidity.getValidityAttribute();
      if (null != this.existingValidity.getValidityValues()) {
        // If the attribute value selected for editing has validity attribute values already in database, display it in
        // validityAttrValuesText Textfield
        this.selValidityAttrValues = this.existingValidity.getValidityValues();
      }
    }
    // If predefined attributes and values are already present for the selected grouped attribute's value, set them as
    // input in UI
    this.existingPreDefinedAttrValueSet = this.selectedAttrValClientBO.getPreDefinedAttrValueSet();
    if (null != this.existingPreDefinedAttrValueSet) {
      for (com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue existingPredefAttrVal : this.existingPreDefinedAttrValueSet) {
        this.predfndAttrList.add(this.attrsMap.get(existingPredefAttrVal.getPredefinedAttrId()));

        this.selPredefinedAttrValMap.put(this.attrsMap.get(existingPredefAttrVal.getPredefinedAttrId()),
            new AttributeClientBO(this.attrsMap.get(existingPredefAttrVal.getPredefinedAttrId())).getAttrValuesMap()
                .get(existingPredefAttrVal.getPredefinedValueId()));
      }
    }
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
  public void createPartControl(final Composite parent) {

    // Create an ordinary non scrollable form on which widgets are built
    this.nonScrollableForm = new Form(parent, 0);
    this.nonScrollableForm
        .setText("Set Validity, Predefined Attributes and Values : " + this.selectedAttrValClientBO.getName());
    addHelpAction((ToolBarManager) this.nonScrollableForm.getToolBarManager());
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
   * {@inheritDoc}
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    this.formToolkit = managedForm.getToolkit();
    // create composite
    createComposite(this.formToolkit);
    this.section.getDescriptionControl().setEnabled(false);
  }

  /**
   * This method initializes composite
   */
  private void createComposite(final FormToolkit toolkit) {
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    this.composite = this.nonScrollableForm.getBody();
    this.composite.setLayout(new GridLayout());
    // Create section to display the validity attributes and values
    createValiditySection(toolkit);
    // Create section to display the predefined attributes and values
    createPredefinedAttrSection(toolkit);
    this.composite.setLayoutData(gridData);
  }

  /**
   * This method initializes section
   */
  private void createValiditySection(final FormToolkit toolkit) {
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = false;
    this.section =
        SectionUtil.getInstance().createSection(this.composite, toolkit, "Validity of Grouped Attribute's Values");
    this.section.setLayoutData(gridData);
    // Create form for the validity section
    // Form to display the validity of grouped attribute's value
    createValidityForm(toolkit);
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createValidityForm(final FormToolkit toolkit) {
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    this.form = toolkit.createForm(this.section);
    this.form.getBody().setLayout(gridLayout);
    toolkit.createLabel(this.form.getBody(), "Attribute");
    toolkit.createLabel(this.form.getBody(), "Value(s)");
    toolkit.createLabel(this.form.getBody(), "");
    // Create combo to show the list of all validity attributes
    createComboAttributes();
    // Create text field to display the list of validity attribute values selected
    createAttrValuesText();
    // Action to open the dialog to select the validity attribute values
    addValidityAttrValueAction();
  }

  /**
   *
   */
  private void addValidityAttrValueAction() {
    this.addLvlAttrValButton = new Button(this.form.getBody(), SWT.BUTTON1);
    this.addLvlAttrValButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.VALUE_EDIT_28X30));
    this.addLvlAttrValButton.setLayoutData(new GridData());
    this.addLvlAttrValButton.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        // If a validity attribute is selcted, enable the buttont o add the validity values
        // Else, disable the add validity value buton
        PredefinedAttributesPage.this.addLvlAttrValButton
            .setEnabled(null != PredefinedAttributesPage.this.selectedValidityAttr);
        // Action to add the validity attribute values
        IAction addValidityAttrValAction =
            new AddValidityValAction(PredefinedAttributesPage.this, PredefinedAttributesPage.this.selectedValidityAttr,
                PredefinedAttributesPage.this.selValidityAttrValues, PredefinedAttributesPage.this.editValDialog);
        addValidityAttrValAction.run();
      }
    });
    PredefinedAttributesPage.this.addLvlAttrValButton
        .setEnabled(null != PredefinedAttributesPage.this.selectedValidityAttr);
  }

  /**
   * Create combo for selecting validity attribute
   */
  private void createComboAttributes() {
    // Combo for the validity attribute
    this.comboValidityAttributes =
        new BasicObjectCombo<>(this.form.getBody(), SWT.READ_ONLY, ApicConstants.DEFAULT_COMBO_SELECT);
    this.comboValidityAttributes.setLayoutData(getTextFieldGridData());
    // Set input for the validity attribute combo
    setComboAttributesList();
  }

  /**
   *
   */
  private void setComboAttributesList() {
    // Set the level attributes (level>0) as input to the validity attribute combo
    List<Attribute> listAttrs = new ArrayList<>();
    for (Attribute attribute : new ApicDataBO().getAllLvlAttrByLevel().values()) {
      if (attribute.getLevel() > 0) {
        listAttrs.add(attribute);
      }
    }
    this.comboValidityAttributes.setElements(listAttrs);
    if (null != this.selectedValidityAttr) {
      this.comboValidityAttributes.select(this.comboValidityAttributes.getIndex(this.selectedValidityAttr));
    }
    else {
      this.comboValidityAttributes.select(0);
    }

    this.comboValidityAttributes.addSelectionListener(new SelectionAdapter() {

      /**
       * Populate Groups combobox. Enable/disable icon to add leav attribute values
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent evnt) {
        // If a validity attribute is selcted in the combo, set it as the selected validity sttribute
        if (null != PredefinedAttributesPage.this.comboValidityAttributes.getSelectedItem()) {
          PredefinedAttributesPage.this.selectedValidityAttr =
              PredefinedAttributesPage.this.comboValidityAttributes.getSelectedItem();
          // enable the butoon to add the validity attribute value
          PredefinedAttributesPage.this.addLvlAttrValButton.setEnabled(true);

          // If a validity attribute is already available in DB for the selcted grouped attribute's value,compare with
          // the newly selected validity attribute
          PredefinedAttrValuesValidityModel validity = PredefinedAttributesPage.this.existingValidity;
          if (null == validity) {
            validity = PredefinedAttributesPage.this.selectedAttrValClientBO.getValidity(getAttrsMap());
          }

          if ((null != validity) && (null != validity.getValidityAttribute())) {

            // If both the validity attributes are not the same, clear the text field which displays the validity
            // attribute values if any text is avilable already
            if (!validity.getValidityAttribute().equals(PredefinedAttributesPage.this.selectedValidityAttr)) {

              if (null != validity.getValidityAttributeValues()) {
                // If the attribute value selected for editing has validity attribute values already in database,
                // display it in validityAttrValuesText Textfield
                PredefinedAttributesPage.this.selValidityAttrValues.clear();
              }
              PredefinedAttributesPage.this.validityAttrValuesText
                  .setText(buildStringFromList(PredefinedAttributesPage.this.selValidityAttrValues));
            }
            else {
              // If both the validity attributes are same,add the newly selected validity attribute values to the text
              // field
              SortedSet<AttributeValue> attrValSet = new TreeSet<>();
              attrValSet.addAll(validity.getValidityAttributeValues().values());
              PredefinedAttributesPage.this.validityAttrValuesText.setText(buildStringFromList(attrValSet));
              PredefinedAttributesPage.this.selValidityAttrValues.clear();
              PredefinedAttributesPage.this.selValidityAttrValues.addAll(attrValSet);
            }
          }
          // Validations for the save button
          // If a validity attribute is available, check for the validity attribute value
          // if present, enable the save button
          // Else, if a only validity attribute is available without a validity attribute value, disable the save
          // button
          PredefinedAttributesPage.this.editValDialog.getSaveBtn()
              .setEnabled((null != PredefinedAttributesPage.this.validityAttrValuesText.getText()) &&
                  !PredefinedAttributesPage.this.validityAttrValuesText.getText().isEmpty());
        }
        else {
          // If no validity attribute is selected at all, the save button can be enabled
          PredefinedAttributesPage.this.addLvlAttrValButton.setEnabled(false);
          PredefinedAttributesPage.this.selValidityAttrValues.clear();
          PredefinedAttributesPage.this.validityAttrValuesText
              .setText(buildStringFromList(PredefinedAttributesPage.this.selValidityAttrValues));
          PredefinedAttributesPage.this.editValDialog.getSaveBtn().setEnabled(true);
        }
        PredefinedAttributesPage.this.validityAttrValuesText.redraw();
      }
    });
  }

  /**
   *
   */
  private void createAttrValuesText() {
    // Text field for the validity attribute values
    this.validityAttrValuesText = new Text(this.form.getBody(), SWT.BORDER);
    this.validityAttrValuesText.setLayoutData(getTextFieldGridData());
    this.validityAttrValuesText.setFocus();
    this.validityAttrValuesText.setEditable(false);
    this.validityAttrValuesText.setText(buildStringFromList(this.selValidityAttrValues));
  }

  /**
   *
   */
  private String buildStringFromList(final SortedSet<AttributeValue> attrValues) {
    StringBuilder lvlValuesStr = new StringBuilder();
    if (CommonUtils.isNotEmpty(attrValues)) {
      for (AttributeValue lvlVal : attrValues) {
        lvlValuesStr.append(lvlVal.getName());
        lvlValuesStr.append(", ");
      }
    }
    return lvlValuesStr.toString();
  }

  /**
   * @return
   */
  private GridData getTextFieldGridData() {
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = false;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.verticalAlignment = GridData.BEGINNING;
    gridData.verticalSpan = 1;
    gridData.heightHint = 25;
    gridData.minimumWidth = 140;
    return gridData;
  }

  /**
   * @param toolkit
   */
  private void createPredefinedAttrSection(final FormToolkit toolkit) {
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    this.section =
        SectionUtil.getInstance().createSection(this.composite, toolkit, "Set Predefined Attributes and their Values");
    this.section.setLayoutData(gridData);
    // Craete form to display the predefined attributes and values
    createPredefinedAttrForm(toolkit);
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createPredefinedAttrForm(final FormToolkit toolkit) {
    this.form = toolkit.createForm(this.section);
    this.form.getBody().setLayout(new GridLayout());
    // Create toolbar action for the predefined attribute section
    createToolBarAction();
    // Create Filter text for the predefined attribute table viewer
    createFilterTxt();
    // Create predefined attribute value table
    createPredefinedAttrValTable();
    // Listerner for the predefined attribute value table - to add the attribute value
    PredefinedAttrTableViewerListeners listnersUtil =
        new PredefinedAttrTableViewerListeners(this, this.selPredefinedAttrValMap, this.editValDialog);
    // add mouse down listener - for value edit column
    listnersUtil.addMouseDownListener();
    // Add filters to the TableViewer
    addFilters();
  }

  /**
   * This method creates Section ToolBar actions
   */
  private void createToolBarAction() {

    ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
    ToolBar toolbar = toolBarManager.createControl(this.section);

    // Action to Add new prefined attribute
    addNewPredefAttrAction(toolBarManager);
    // Action to delete one/multiple prefined attribute
    deletePredefAttrAction(toolBarManager);
    toolBarManager.update(true);
    this.section.setTextClient(toolbar);
  }

  /**
   * @param toolBarManager
   */
  private void deletePredefAttrAction(final ToolBarManager toolBarManager) {


    // Create an action to add new user
    this.delPredfndAttrAction = new Action("Delete Predefined Attribute(s)", SWT.NONE) {

      @Override
      public void run() {
        // Validations for the delete button in the toolbar
        validateDelPredefAttrBtn();
        if ((null != PredefinedAttributesPage.this.predfndAttrList) &&
            (null != PredefinedAttributesPage.this.selPredefAttrListforDel) &&
            !PredefinedAttributesPage.this.selPredefAttrListforDel.isEmpty()) {
          PredefinedAttributesPage.this.predfndAttrList
              .removeAll(PredefinedAttributesPage.this.selPredefAttrListforDel);
        }
        for (Attribute attrSel : PredefinedAttributesPage.this.selPredefAttrListforDel) {
          if (null != PredefinedAttributesPage.this.selPredefinedAttrValMap) {
            PredefinedAttributesPage.this.selPredefinedAttrValMap.remove(attrSel);
          }
        }
        PredefinedAttributesPage.this.predefinedAttrValTableViewer.refresh();
        PredefinedAttributesPage.this.editValDialog.getSaveBtn().setEnabled(true);
      }
    };
    // Set the image for add user action
    this.delPredfndAttrAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    toolBarManager.add(this.delPredfndAttrAction);
    this.delPredfndAttrAction.setEnabled(false);
  }

  /**
   * This method creates non defined filter action
   *
   * @param toolBarManager
   */
  private void addNewPredefAttrAction(final ToolBarManager toolBarManager) {


    // Create an action to add new user
    this.addPredfndAttrAction = new Action("Add Predefined Attribute(s)", SWT.NONE) {

      @Override
      public void run() {
        final AddNewPredefinedAttrDialog addAttrDialog =
            new AddNewPredefinedAttrDialog(Display.getDefault().getActiveShell(), PredefinedAttributesPage.this,
                PredefinedAttributesPage.this.editValDialog,
                new ArrayList<Attribute>(PredefinedAttributesPage.this.attrsMap.values()));
        addAttrDialog.open();
      }
    };
    // Set the image for add user action
    this.addPredfndAttrAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    toolBarManager.add(this.addPredfndAttrAction);
  }

  /**
   *
   */
  private void addFilters() {
    this.predefinedAttrValTabFilter = new PredefinedAttrValTabFilter();
    // Add PIDC Attribute TableViewer filter
    this.predefinedAttrValTableViewer.addFilter(this.predefinedAttrValTabFilter);
  }

  /**
   *
   */
  private void createFilterTxt() {
    this.filterTxt = TextUtil.getInstance().createFilterText(this.formToolkit, this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));

    this.filterTxt.addModifyListener(event -> {
      final String text = PredefinedAttributesPage.this.filterTxt.getText().trim();
      PredefinedAttributesPage.this.predefinedAttrValTabFilter.setFilterText(text);
      PredefinedAttributesPage.this.predefinedAttrValTableViewer.refresh();
    });

    this.filterTxt.setFocus();
  }

  /**
   *
   */
  private void createPredefinedAttrValTable() {

    this.predefinedAttrValTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL,
        GridDataUtil.getInstance().getHeightHintGridData(350));
    this.predefinedAttrValTableViewer.setContentProvider(new ArrayContentProvider());
    this.predefinedAttrValTableViewer.getGrid().setLayoutData(GridDataUtil.getInstance().getGridData());
    this.predefinedAttrValTableViewer.getGrid().setLinesVisible(true);
    this.predefinedAttrValTableViewer.getGrid().setHeaderVisible(true);
    // Create sorter for the table
    this.predefinedAttrValSorter = new PredefinedAttrValTabSorter(this.selPredefinedAttrValMap);
    this.predefinedAttrValTableViewer.setComparator(this.predefinedAttrValSorter);
    // Create GridViewerColumns
    createAttrValViewerColumns();
    addTableSelectionListener();
    this.predefinedAttrValTableViewer.setInput(this.predfndAttrList);
  }

  /**
   * This method add selection listener to valTableViewer
   */
  private void addTableSelectionListener() {
    this.predefinedAttrValTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
      *
      */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        PredefinedAttributesPage.this.selPredefAttrListforDel.clear();
        final List<Attribute> selAttrList = getSelAttrFromTabViewer();
        PredefinedAttributesPage.this.selPredefAttrListforDel.addAll(selAttrList);
        validateDelPredefAttrBtn();
      }
    });
  }

  private void validateDelPredefAttrBtn() {
    this.delPredfndAttrAction.setEnabled(!this.selPredefAttrListforDel.isEmpty());
  }

  /**
   * @return Attribute
   */
  protected List<Attribute> getSelAttrFromTabViewer() {
    List<Attribute> selAttrList = new ArrayList<>();
    final IStructuredSelection selection = (IStructuredSelection) this.predefinedAttrValTableViewer.getSelection();
    if ((selection != null) && (selection.size() != 0)) {
      final List<IStructuredSelection> elementList = selection.toList();
      if (elementList.get(0) instanceof Attribute) {
        selAttrList.addAll((Collection<? extends Attribute>) elementList);
      }
    }
    return selAttrList;
  }

  /**
   *
   */
  private void createAttrValViewerColumns() {
    // Create column to display the predefined attribute
    createPredefinedAttrCol();
    // Create column to display the predefined value
    createPredefindValCol();
    // Create column to display the icon to select the predefined value
    createPredefindValSelCol();
  }

  /**
   * Column to display the icon to select the predefine value
   */
  private void createPredefindValSelCol() {
    final GridViewerColumn depValSelColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.predefinedAttrValTableViewer, "", 30);
    // Add column selection listener
    depValSelColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(depValSelColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_2, this.predefinedAttrValSorter, this.predefinedAttrValTableViewer));
    // Label provider for the predefined attribute value edit column
    depValSelColumn.setLabelProvider(new PredefinedValEditColLblProvider());
  }

  /**
   * column to display the predefined value
   */
  private void createPredefindValCol() {
    final GridViewerColumn depValColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.predefinedAttrValTableViewer, "Value", 250);
    // Add column selection listener
    depValColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(depValColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_1, this.predefinedAttrValSorter, this.predefinedAttrValTableViewer));
    // Label provider for the predefined value column
    depValColumn.setLabelProvider(new PredefinedValColLblProvider(this.selPredefinedAttrValMap));
  }

  /**
   * column to display the predefined attribute
   */
  private void createPredefinedAttrCol() {
    final GridViewerColumn depAttrColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.predefinedAttrValTableViewer, "Attribute", COL_WIDTH);
    // Add column selection listener
    depAttrColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(depAttrColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_0, this.predefinedAttrValSorter, this.predefinedAttrValTableViewer));
    // Label provider for the predefined attribute column
    depAttrColumn.setLabelProvider(new PredefinedAttrColLblProvider());
  }

  /**
   * @return the attrValuesText
   */
  public Text getAttrValuesText() {
    return this.validityAttrValuesText;
  }

  /**
   * @return the predfndAttrList
   */
  public SortedSet<Attribute> getPredfndAttrList() {
    return this.predfndAttrList;
  }

  /**
   * @return the predefinedAttrValTableViewer
   */
  public GridTableViewer getPredefinedAttrValTableViewer() {
    return this.predefinedAttrValTableViewer;
  }

  /**
   * @return the addLvlAttrValButton
   */
  public Button getAddLvlAttrValButton() {
    return this.addLvlAttrValButton;
  }

  /**
   * @return the selectedLevelAttr
   */
  public Attribute getSelectedLevelAttr() {
    return this.selectedValidityAttr;
  }

  /**
   * @param selectedLevelAttr the selectedLevelAttr to set
   */
  public void setSelectedLevelAttr(final Attribute selectedLevelAttr) {
    this.selectedValidityAttr = selectedLevelAttr;
  }

  /**
   * @return the selLevAttrValues
   */
  public SortedSet<AttributeValue> getSelLevAttrValues() {
    return this.selValidityAttrValues;
  }

  /**
   * @return the addPredfndAttrAction
   */
  public Action getAddPredfndAttrAction() {
    return this.addPredfndAttrAction;
  }

  /**
   * @return the selPredefinedAttrValMap
   */
  public Map<Attribute, AttributeValue> getSelPredefinedAttrValMap() {
    return this.selPredefinedAttrValMap;
  }


  /**
   * @return the attrsMap
   */
  public Map<Long, Attribute> getAttrsMap() {
    return this.attrsMap;
  }


  /**
   * @return the existingValidity
   */
  public PredefinedAttrValuesValidityModel getExistingValidity() {
    return this.existingValidity;
  }


  /**
   * @return the existingPreDefinedAttrValueSet
   */
  public Set<PredefinedAttrValue> getExistingPreDefinedAttrValueSet() {
    return this.existingPreDefinedAttrValueSet;
  }

}
