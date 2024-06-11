/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.dialogs;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridEditor;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.table.filters.RuleSetParamAttrFilter;
import com.bosch.caltool.icdm.ruleseditor.utils.Messages;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author UKT1COB
 */
public class ExportRulesetAsCDFxDialog extends AbstractDialog {

  private static final int TBL_VIEWER_COL_WIDTH = 200;
  private Composite parentComposite;
  private Composite composite;
  private FormToolkit formToolKit;
  private Section section;
  private Form form;
  private GridTableViewer attrsTableViewer;
  Map<Long, Set<IParameterAttribute>> attrParamMap = new HashMap<>();
  private final ParameterDataProvider paramDataProvider;
  /**
   * Selected Attribute in Dialog
   */
  protected IParameterAttribute curParamAttr;
  /**
   * Export Button in Dialog
   */
  protected Button exportButton;
  /**
   * RuleSetParamAttrFilter
   */
  private RuleSetParamAttrFilter filter;
  /**
   * Cdfx file Export directory location
   */
  private String userSelDestFileDir;
  /**
   * exportable Cdfx file name
   */
  private String userSelDestFileName;
  /**
   * Set of Attributes
   */
  private Set<IParameterAttribute> attrSet;
  /**
   * List of Parameters in RuleSet
   */
  private final List<IParameter> listParamsInRuleset;
  /**
   * Attribute Value Model Set
   */
  private Set<AttributeValueModel> attrValueModSet;
  /**
   * set true if Attribute Set is Empty
   */
  private boolean isAttrSetEmpty;

  /**
   * @param parentShell parent shell
   * @param parameterDataProvider param data provider
   * @param listParamsInRuleset list of param in ruleset
   */
  public ExportRulesetAsCDFxDialog(final Shell parentShell, final ParameterDataProvider parameterDataProvider,
      final List<IParameter> listParamsInRuleset) {
    super(parentShell);
    this.paramDataProvider = parameterDataProvider;
    this.listParamsInRuleset = listParamsInRuleset;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {

    final Control contents = super.createContents(parent);

    // Set title
    setTitle(this.isAttrSetEmpty ? "Select CDFX File Export Location" : "Dependent Attributes");

    // Set the message
    if (!this.isAttrSetEmpty) {
      setMessage(
          "Consolidated list of dependent attributes for all parameters in the selected Ruleset. Select Values for each attribute.");
    }

    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {

    newShell.setText("Export Rule Set as CDFx");
    super.configureShell(newShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.parentComposite = (Composite) super.createDialogArea(parent);

    this.parentComposite.setLayout(new GridLayout());
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.parentComposite.setLayoutData(gridData);
    createComposite();
    return this.parentComposite;
  }

  /**
   * @param top2
   */
  private void createComposite() {

    this.composite = getFormToolkit().createComposite(this.parentComposite);
    this.composite.setLayout(new GridLayout());
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());

    createSection();
  }

  /**
   * @param composite
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), "");
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.setLayout(new GridLayout());
    createForm();
    this.section.getDescriptionControl().setEnabled(false);
    this.section.setClient(this.form);

  }

  /**
   * create form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    Composite formBody = this.form.getBody();
    formBody.setLayout(new GridLayout());
    formBody.setLayoutData(GridDataUtil.getInstance().getGridData());

    this.attrSet = getAttrValSet();
    this.isAttrSetEmpty = CommonUtils.isNullOrEmpty(this.attrSet);
    this.section
        .setText(this.isAttrSetEmpty ? "Select CDFx Export File Location" : "Select Dependent Attribute Values");
    if (!this.isAttrSetEmpty) {
      createAttrValTable(formBody);
    }

    // Destination File Path Selection
    createCDFxExportFileLocGrp(formBody);
  }

  /**
   * @param parentComp
   */
  private void createCDFxExportFileLocGrp(final Composite parentComp) {
    Group grp = new Group(parentComp, SWT.NONE);
    GridLayout grpGridLayout = new GridLayout();
    grpGridLayout.numColumns = 3;
    grp.setLayout(grpGridLayout);
    grp.setLayoutData(new GridData(GridData.FILL, GridData.FILL_HORIZONTAL, true, false));


    LabelUtil.getInstance().createLabel(grp, "Export Location : ");
    final Text fileText = TextUtil.getInstance().createText(grp, false, "");
    fileText.setEditable(false);
    fileText.setEnabled(true);
    fileText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

    String fileName = (getFileName().append(ApicUiConstants.CDFX_FILE_EXT)).toString();
    setUserSelDestFileDir(CommonUtils.getUserDirPath());
    setUserSelDestFileName(fileName);
    fileText.setText(CommonUtils.getUserDirPath() + File.separator + fileName);

    Button browseBtn = new Button(grp, SWT.NONE);
    // image for browse button
    browseBtn.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));

    browseBtn.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {
        FileDialog fileDialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
        fileDialog.setFilterPath(CommonUtils.getUserDirPath());
        fileDialog.setOverwrite(true);
        fileDialog.setFilterExtensions(ApicUiConstants.CDFX_FILE_FILTER_EXTN);
        fileDialog.setFileName(fileName);

        String selectedDestFilePath = fileDialog.open();
        if (CommonUtils.isNotEmptyString(selectedDestFilePath)) {
          File selectedDestFile = new File(selectedDestFilePath);
          setUserSelDestFileDir(selectedDestFile.getParent());
          setUserSelDestFileName(selectedDestFile.getName());
        }

        fileText.setText(ExportRulesetAsCDFxDialog.this.getUserSelDestFileDir() + File.separator +
            ExportRulesetAsCDFxDialog.this.getUserSelDestFileName());
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionevent) {
        // NA
      }
    });
  }


  /**
   * @return
   */
  private StringBuilder getFileName() {
    StringBuilder fileName = new StringBuilder();
    fileName.append("Ruleset_Cdfx_Export").append(CommonUIConstants.UNDERSCORE)
        .append(DateFormat.formatDateToString(new Date(), DateFormat.DATE_FORMAT_19));

    return fileName;
  }

  /**
   * @return
   */
  private Set<IParameterAttribute> getAttrValSet() {
    Set<IParameterAttribute> attributeSet = new TreeSet<>();
    for (IParameter param : this.listParamsInRuleset) {
      List<IParameterAttribute> paramAttrs = this.paramDataProvider.getParamAttrs(param);
      if (CommonUtils.isNotNull(paramAttrs)) {
        attributeSet.addAll(paramAttrs);
      }
    }

    return attributeSet;
  }

  /**
   * @return
   */
  private FormToolkit getFormToolkit() {
    if (CommonUtils.isNull(this.formToolKit)) {
      this.formToolKit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolKit;
  }

  /**
   * @param formBody
   */
  private void createFilterText(final Composite formBody) {
    Text filterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), formBody, getFilterTxtGridData(),
        Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));

    filterTxt.addModifyListener(event -> {
      // when there is a modification in the text filter, set the text to the filter
      final String text = filterTxt.getText().trim();
      this.filter.setFilterText(text);

      // refresh to filter the table based on table
      this.attrsTableViewer.refresh();

    });
    // set focus to type filter
    filterTxt.setFocus();
  }

  /**
   * This method returns filter text GridData object
   *
   * @return GridData
   */
  private GridData getFilterTxtGridData() {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.verticalAlignment = GridData.CENTER;
    return gridData;
  }


  /**
   * @param formBody
   */
  private void createAttrValTable(final Composite formBody) {

    this.filter = new RuleSetParamAttrFilter();
    createFilterText(formBody);

    this.attrsTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(formBody,
        SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FILL | SWT.BORDER, GridDataUtil.getInstance().getGridData());
    this.attrsTableViewer.setContentProvider(ArrayContentProvider.getInstance());

    // Create columns
    createColumns();

    this.attrsTableViewer.addFilter(this.filter);

    setAttrTabViewerInput();
  }


  /*
   * Defines the columns of the TableViewer
   */
  private void createColumns() {

    final GridViewerColumn attrNameColumn = new GridViewerColumn(this.attrsTableViewer, SWT.NONE);
    attrNameColumn.getColumn().setText("Attribute");
    attrNameColumn.getColumn().setWidth(TBL_VIEWER_COL_WIDTH);

    attrNameColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        IParameterAttribute paramAttr = (IParameterAttribute) element;
        Attribute attribute = ExportRulesetAsCDFxDialog.this.paramDataProvider.getAttribute(paramAttr);
        return attribute.getName();
      }
    });
    final GridViewerColumn valueColumn = new GridViewerColumn(this.attrsTableViewer, SWT.NONE);
    valueColumn.getColumn().setText("Value");
    valueColumn.getColumn().setWidth(TBL_VIEWER_COL_WIDTH);


    valueColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */

      @Override
      public String getText(final Object element) {
        return ApicConstants.DEFAULT_COMBO_SELECT;
      }
    });

    // Create an editor object to use for text editing
    final GridEditor gridEditor = new GridEditor(this.attrsTableViewer.getGrid());
    gridEditor.horizontalAlignment = SWT.LEFT;
    gridEditor.grabHorizontal = true;

    // mousedown listener
    this.attrsTableViewer.getGrid().addMouseListener(new MouseAdapter() {

      @Override
      public void mouseDown(final MouseEvent event) {

        disposeExistingEditor(gridEditor);

        // Locate the position of the mouse click
        Point point = new Point(event.x, event.y);

        // Determine which row was selected
        final GridItem item = ExportRulesetAsCDFxDialog.this.attrsTableViewer.getGrid().getItem(point);

        if (CommonUtils.isNotNull(item)) {
          // Identify which column was selected
          int column = getSelectedCol(point, item);

          if (column == CommonUIConstants.COLUMN_INDEX_1) {
            Object itemData = item.getData();
            if (itemData instanceof IParameterAttribute) {
              IParameterAttribute paraAttr = (IParameterAttribute) itemData;
              ExportRulesetAsCDFxDialog.this.curParamAttr = paraAttr;
              final CCombo comboObj = addDataToCombo(paraAttr);
              // Select the previously selected item
              comboObj.select(comboObj.indexOf(item.getText(column)));

              // Calculate the width for the editor and compute the column width to fit the dropdown .
              comboObj.setFocus();// Set the focus on the dropdown.
              gridEditor.setEditor(comboObj, item, column);

              // Add a listener to fix the selected item back to the cell
              final int colIndex = column;
              addSelectionListener(item, comboObj, colIndex);
            }
          }
        }
      }

      /**
       * @param paraAttr
       * @return
       */
      private CCombo addDataToCombo(final IParameterAttribute paraAttr) {
        // Create dropdown list and add data to it
        final CCombo comboObj = new CCombo(ExportRulesetAsCDFxDialog.this.attrsTableViewer.getGrid(), SWT.READ_ONLY);
        comboObj.add(ApicConstants.DEFAULT_COMBO_SELECT);
        SortedSet<AttributeValue> mappedAttrVal = new TreeSet<>();

        try {
          mappedAttrVal = ExportRulesetAsCDFxDialog.this.paramDataProvider.getMappedAttrVal(paraAttr);
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
        for (AttributeValue attrVal : mappedAttrVal) {
          comboObj.add(attrVal.getName());
        }
        return comboObj;
      }
    });
  }

  /**
   * @param gridEdtr
   */
  private void disposeExistingEditor(final GridEditor gridEdtr) {
    // Dispose any existing editor
    Control oldEditor = gridEdtr.getEditor();
    if (oldEditor != null) {
      oldEditor.dispose();
    }
  }

  /**
   * @param point
   * @param item
   * @return
   */
  private int getSelectedCol(final Point point, final GridItem item) {
    int column = -1;
    for (int index = 0, n =
        ExportRulesetAsCDFxDialog.this.attrsTableViewer.getGrid().getColumnCount(); index < n; index++) {
      Rectangle rectangle = item.getBounds(index);
      if (rectangle.contains(point)) {
        column = index;// the selected column
        break;
      }
    }
    return column;
  }

  /**
   * @param item
   * @param comboObj
   * @param colIndex
   */
  private void addSelectionListener(final GridItem item, final CCombo comboObj, final int colIndex) {
    comboObj.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {


        item.setText(colIndex, comboObj.getText());
        GridItem[] items = ExportRulesetAsCDFxDialog.this.attrsTableViewer.getGrid().getItems();
        for (GridItem gridItem : items) {
          ExportRulesetAsCDFxDialog.this.exportButton
              .setEnabled(CommonUtils.isNotEqual(ApicConstants.DEFAULT_COMBO_SELECT, gridItem.getText(1)));
        }
        // They selected an item; end the editing session
        comboObj.dispose();
      }
    });
  }


  /**
   * Sets the tableviewer input
   */
  public void setAttrTabViewerInput() {

    this.attrsTableViewer.getGrid().removeAll();
    this.attrsTableViewer.setInput(this.attrSet);
  }


  /**
   * @param attrValueModSet
   * @param gridItem
   * @param attribute
   */
  private void setUsedNotUsedAttrValModel(final Set<AttributeValueModel> attrValueModSet, final GridItem gridItem,
      final Attribute attribute) {
    AttributeValueModel attrValModel;

    if (gridItem.getText(1).equals(ApicConstants.NOT_USED)) {
      attrValModel = new AttributeValueModel();
      attrValModel.setAttr(attribute);
      attrValModel.setValue(this.paramDataProvider.createAttrValNotUsedObject(attribute));

      attrValueModSet.add(attrValModel);
    }
    else if (gridItem.getText(1).equals(ApicConstants.USED)) {
      attrValModel = new AttributeValueModel();
      attrValModel.setAttr(attribute);
      attrValModel.setValue(this.paramDataProvider.createAttrValUsedObject(attribute));

      attrValueModSet.add(attrValModel);
    }
  }


  @Override
  protected void createButtonsForButtonBar(final Composite parent) {

    this.exportButton = createButton(parent, IDialogConstants.OK_ID, "Export", false);
    this.exportButton.setEnabled(CommonUtils.isNullOrEmpty(this.attrSet));

    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    this.attrValueModSet = CommonUtils.isNull(this.attrsTableViewer) ? new HashSet<>() : getAttrValModels();
    super.okPressed();
  }


  /**
   * @return the Attr Val Model Set for Searching rules
   */
  private Set<AttributeValueModel> getAttrValModels() {
    GridItem[] attrGridItems = this.attrsTableViewer.getGrid().getItems();
    Set<AttributeValueModel> attrValModSet = new HashSet<>();
    for (GridItem gridItem : attrGridItems) {
      if (gridItem.getData() instanceof IParameterAttribute) {
        IParameterAttribute paramAttr = (IParameterAttribute) gridItem.getData();
        SortedSet<AttributeValue> attrValList = null;
        try {
          attrValList = this.paramDataProvider.getMappedAttrVal(paramAttr);
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
          return Collections.emptySet();
        }
        Attribute attribute = this.paramDataProvider.getAttribute(paramAttr);

        for (AttributeValue attrVal : attrValList) {
          if (attrVal.getName().equals(gridItem.getText(1))) {
            attrValModSet.add(this.paramDataProvider.createAttrValModel(attrVal, attribute));
          }
        }
        // If the attribute is not used ,then create a dummy object and set in the model
        setUsedNotUsedAttrValModel(attrValModSet, gridItem, attribute);
      }
    }
    return attrValModSet;
  }


  /**
   * @return the attrValueModSet
   */
  public Set<AttributeValueModel> getAttrValueModSet() {
    return this.attrValueModSet;
  }


  /**
   * @return the userSelDestFileDir
   */
  public String getUserSelDestFileDir() {
    return this.userSelDestFileDir;
  }


  /**
   * @param userSelDestFileDir the userSelDestFileDir to set
   */
  public void setUserSelDestFileDir(final String userSelDestFileDir) {
    this.userSelDestFileDir = userSelDestFileDir;
  }


  /**
   * @return the userSelDestFileName
   */
  public String getUserSelDestFileName() {
    return this.userSelDestFileName;
  }


  /**
   * @param userSelDestFileName the userSelDestFileName to set
   */
  public void setUserSelDestFileName(final String userSelDestFileName) {
    this.userSelDestFileName = userSelDestFileName;
  }


}
