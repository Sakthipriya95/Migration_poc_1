/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
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

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.sorters.AttributesGridTabViewerSorter;
import com.bosch.caltool.cdr.ui.table.filters.AttributesFilters;
import com.bosch.caltool.icdm.client.bo.cdr.AttrValcombinationCalculator;
import com.bosch.caltool.icdm.client.bo.cdr.QuesDepnAttributeRow;
import com.bosch.caltool.icdm.client.bo.cdr.QuestionnaireUtil;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestDepenValCombination;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.ruleseditor.utils.Messages;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * This dialog adds the Attribute value combination in Question creation dialog
 *
 * @author dmo5cob
 */
public class AddAttributeValueCombDialog extends AbstractDialog {

  /**
   * Add Attribute Dependency
   */
  private static final String ATTR_DEP = "Attribute Value Combination";
  /**
   * Top composite instance
   */
  private Composite top;
  /**
   * composite instance
   */
  private Composite composite;

  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  /**
   * section instance
   */
  private Section section;

  /**
   * Form instance
   */
  private Form form;

  /**
   * name Col Length
   */
  private static final int NAME_COL_LEN = 200;

  /**
   * Desc col Length
   */
  private static final int DESC_COL_LEN = 300;

  /**
   * Attr tab Height
   */
  private static final int ATTR_TAB_HEIGHT = 150;
  private static final int VAL_EDIT_COL_WIDTH = 25;


  private Text attrFilText;
  private GridTableViewer attrTab;
  private Button saveBtn;


  /**
   * selected Attributes from the table viewer,.
   */

  private AttributesFilters attrFilter;
  private AttributesGridTabViewerSorter addAttrSorter;
  private final QuestionDialog questionDialog;
  private final Set<QuesDepnAttributeRow> selectedAttrs = new HashSet<>();
  /**
   * Stores attr and its selected values
   */
  private final ConcurrentMap<Attribute, SortedSet<AttributeValue>> attrVals = new ConcurrentHashMap<>();
  private final ConcurrentMap<Attribute, SortedSet<AttributeValue>> attrValsSetWhileUpdate = new ConcurrentHashMap<>();
  private final List<QuestDepenValCombination> listAttrValComb = new ArrayList<>();

  /**
   * @param parentShell parent shell
   * @param questionDialog instance
   * @param selectedAttrs
   * @param listAttrVal
   */
  public AddAttributeValueCombDialog(final Shell parentShell, final QuestionDialog questionDialog,
      final Set<QuesDepnAttributeRow> selectedAttrs, final List<QuestDepenValCombination> listAttrVal) {
    super(parentShell);
    this.questionDialog = questionDialog;
    if (null != selectedAttrs) {
      this.selectedAttrs.addAll(selectedAttrs);
    }
    if (null != listAttrVal) {
      this.listAttrValComb.addAll(listAttrVal);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle(ATTR_DEP);

    // Set the message
    setMessage(
        "Please select the attribute/value you want to use or enter a new attribute value combination in column New Attr/Val",
        IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(ATTR_DEP);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    newShell.setLayout(new GridLayout());
    newShell.setLayoutData(gridData);
    super.configureShell(newShell);
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
   * {@inheritDoc}
   */
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
   * create composite
   */
  private void createComposite() {

    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite.setLayoutData(gridData);
    createSection();
  }

  /**
   * create section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Select the Attributes");
    this.section.setLayout(new GridLayout());
    this.section.getDescriptionControl().setEnabled(false);
    createForm();
    this.section.setExpanded(true);
    this.section.setClient(this.form);

  }

  /**
   * create form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    final GridData gridData = GridDataUtil.getInstance().getTextGridData();
    createAttrTable(this.form);
    final GridLayout gridLayout = new GridLayout();
    this.form.getBody().setLayout(gridLayout);
    this.form.getBody().setLayoutData(gridData);
  }

  /**
   * @param form
   */
  private void createAttrTable(final Form form) {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessVerticalSpace = true;
    gridData.heightHint = ATTR_TAB_HEIGHT;
    this.addAttrSorter = new AttributesGridTabViewerSorter();
    this.attrFilText = this.formToolkit.createText(form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.attrTab =
        new GridTableViewer(form.getBody(), SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
    this.attrTab.setAutoPreferredHeight(true);
    this.attrTab.getGrid().setLayout(new GridLayout());
    this.attrTab.getGrid().setLayoutData(gridData);
    this.attrTab.getGrid().setLinesVisible(true);
    this.attrTab.getGrid().setHeaderVisible(true);
    this.attrTab.setComparator(this.addAttrSorter);
    createTabColumns();
    this.attrTab.setContentProvider(ArrayContentProvider.getInstance());

    setTabViewInput();
    addSelectionListener(this.attrTab);
    addDoubleClickListener(this.attrTab);
    createAttrFilter();
    addMouseDownListener();
  }


  /**
   * create the Attr Dep Filter for the Param Attribute Dep Table
   */
  private void createAttrFilter() {

    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.verticalAlignment = GridData.CENTER;
    this.attrFilText.setLayoutData(gridData);
    this.attrFilText.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.attrFilter = new AttributesFilters(); // Add TableViewer filter
    this.attrTab.addFilter(this.attrFilter);

    this.attrFilText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        String text = AddAttributeValueCombDialog.this.attrFilText.getText().trim();
        AddAttributeValueCombDialog.this.attrFilter.setFilterText(text);
        AddAttributeValueCombDialog.this.attrTab.refresh();
      }
    });

  }

  /**
   * ICDM 574-This method defines the activities to be performed when double clicked on the table
   *
   * @param functionListTableViewer2
   */
  private void addDoubleClickListener(final GridTableViewer tableviewer) {
    tableviewer.addDoubleClickListener(new IDoubleClickListener() {

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

  /**
   * @param attrTab
   */
  private void addSelectionListener(final GridTableViewer attrTab) {
    attrTab.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        // TO-DO
      }

    });
  }

  /**
   * set the Tab view Input
   */
  private void setTabViewInput() {
    this.attrTab.setInput(this.selectedAttrs);
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Add", true);
    this.saveBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }


  /**
   * create the Table Columns
   */
  private void createTabColumns() {
    final GridViewerColumn attrColumn = new GridViewerColumn(this.attrTab, SWT.NONE);
    attrColumn.getColumn().setText("Attribute Name");
    attrColumn.getColumn().setWidth(NAME_COL_LEN);
    attrColumn.getColumn().setResizeable(true);

    attrColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        QuesDepnAttributeRow attr = (QuesDepnAttributeRow) element;
        return attr.getAttribute().getName();
      }
    });

    attrColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrColumn.getColumn(), 0, this.addAttrSorter, this.attrTab));

    final GridViewerColumn attrValColumn = new GridViewerColumn(this.attrTab, SWT.NONE);
    attrValColumn.getColumn().setText("Value");
    attrValColumn.getColumn().setWidth(DESC_COL_LEN);
    attrValColumn.getColumn().setResizeable(true);

    attrValColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        QuesDepnAttributeRow qAttr = (QuesDepnAttributeRow) element;
        String returnStr;
        if (!getAttrVals().isEmpty()) {
          SortedSet<AttributeValue> selVals = getAttrVals().get(qAttr.getAttribute());
          if (selVals != null) {
            AddAttributeValueCombDialog.this.attrValsSetWhileUpdate.put(qAttr.getAttribute(), selVals);
            StringBuilder values = new StringBuilder();
            for (AttributeValue selVal : selVals) {
              values.append(selVal.getName()).append(",");
            }
            return values.substring(0, values.length() - 1);
          }
          returnStr = getNewSelectedValue(qAttr);

        }
        else {
          returnStr = getNewSelectedValue(qAttr);
        }
        return returnStr;
      }

      /**
       * @param qAttr
       */
      private String getNewSelectedValue(final QuesDepnAttributeRow qAttr) {
        SortedSet<AttributeValue> valuesSet = new TreeSet<>();
        if ((AddAttributeValueCombDialog.this.listAttrValComb != null) &&
            !AddAttributeValueCombDialog.this.listAttrValComb.isEmpty()) {
          for (QuestDepenValCombination combObj : AddAttributeValueCombDialog.this.listAttrValComb) {
            AttributeValue valueObj = combObj.getAttrValMap().get(qAttr.getAttribute());
            if (null != valueObj) {
              valuesSet.add(valueObj);
            }
          }
          if (!valuesSet.isEmpty()) {
            AddAttributeValueCombDialog.this.attrValsSetWhileUpdate.put(qAttr.getAttribute(), valuesSet);
            StringBuilder values = new StringBuilder();
            for (AttributeValue selVal : valuesSet) {
              values.append(selVal.getName()).append(",");
            }
            return values.substring(0, values.length() - 1);
          }
        }
        return "";
      }

    });

    attrValColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrValColumn.getColumn(), 1, this.addAttrSorter, this.attrTab));

    createAttrValSelColViewer();
  }

  /**
   * This method creates PIDC attribute value edit column
   */
  private void createAttrValSelColViewer() {
    final GridViewerColumn attrValEditColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.attrTab, VAL_EDIT_COL_WIDTH);
    attrValEditColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * Defines attribute value edit image
       */
      private Image editImage;

      /**
       * Get the value edit image {@inheritDoc}
       */
      @Override
      public Image getImage(final Object element) {

        if (this.editImage == null) {
          this.editImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.VALUE_EDIT_28X30);
          return this.editImage;
        }
        return this.editImage;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return "";
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    Question ques = null;
    if (this.questionDialog.isUpdate()) {
      ques = this.questionDialog.getSelectedQuestion();
    }
    if ((getAttrVals() == null) || getAttrVals().isEmpty()) {
      MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Information",
          "Kindly select a value for the attribute");
      return;
    }
    List<QuestDepenValCombination> quesValCombList = new ArrayList<>();
    List<QuestDepenValCombination> existingQuesValCombList = new ArrayList<>();
    existingQuesValCombList.addAll(getExistingValuesFromTable(ques));
    List<QuestDepenValCombination> exstngValCombList = new ArrayList<>();

    for (QuestDepenValCombination quesDepnValCombination : existingQuesValCombList) {
      try {
        exstngValCombList.add(quesDepnValCombination.clone());
      }
      catch (CloneNotSupportedException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }

    // in case of update of existing combination
    if (!this.listAttrValComb.isEmpty()) {
      for (QuestDepenValCombination quesDepnValCombination : this.listAttrValComb) {
        for (Attribute attr : quesDepnValCombination.getAttrValMap().keySet()) {
          SortedSet<AttributeValue> value = this.attrValsSetWhileUpdate.get(attr);
          getAttrVals().put(attr, value);
        }
      }
      AttrValcombinationCalculator combinationCalculator = new AttrValcombinationCalculator(getAttrVals());
      Map<Integer, Map<Attribute, AttributeValue>> constructAttrValCombi =
          combinationCalculator.constructAttrValCombi();
      for (Entry<Integer, Map<Attribute, AttributeValue>> combination : constructAttrValCombi.entrySet()) {
        for (QuestDepenValCombination quesDepnValCombination : this.listAttrValComb) {
          quesDepnValCombination.setAttrValMap(new ConcurrentHashMap<>(combination.getValue()));
          quesValCombList.add(quesDepnValCombination);
        }
      }
    } // in case of inserting new combination
    else {
      quesValCombList
          .addAll(QuestionnaireUtil.constructAttrValCombiForQues(getAttrVals(), ques, this.questionDialog.isUpdate()));
    }

    boolean validateExistingCombination = validateExistingCombination(exstngValCombList, quesValCombList);

    if (existingQuesValCombList.isEmpty() || (!existingQuesValCombList.isEmpty() && validateExistingCombination)) {
      if (!CommonUtils.isNotEmpty(this.listAttrValComb)) {
        if (!existingQuesValCombList.isEmpty()) {
          quesValCombList.addAll(existingQuesValCombList);
        }
        this.questionDialog.getAttrsValueTableViewer().setInput(quesValCombList);
      }

      if (!this.listAttrValComb.isEmpty()) {
        for (QuestDepenValCombination quesDepnValCombination : quesValCombList) {
          if (null != quesDepnValCombination.getCombinationId()) {
            this.questionDialog.getEditQuesValCombList().add(quesDepnValCombination);
          }
        }
      }

      this.questionDialog.getAttrsValueTableViewer().refresh();
      this.questionDialog.checkSaveBtnEnable();
      super.okPressed();
    }
    else {
      CDMLogger.getInstance().errorDialog("Combination already present !", Activator.PLUGIN_ID);
      this.questionDialog.getAttrsValueTableViewer().setInput(exstngValCombList);
      super.okPressed();
    }
  }

  /**
   * @param ques
   * @return
   */
  private List<QuestDepenValCombination> getExistingValuesFromTable(final Question ques) {
    GridItem[] itemsList = this.questionDialog.getAttrsValueTableViewer().getGrid().getItems();
    List<QuestDepenValCombination> existingQuesValCombList = new ArrayList<>();
    if (itemsList.length != 0) {
      for (GridItem gridItem : itemsList) {
        QuestDepenValCombination existingValComb = (QuestDepenValCombination) gridItem.getData();
        existingValComb.setQuestion(ques);
        existingQuesValCombList.add(existingValComb);
      }
    }
    return existingQuesValCombList;
  }

  /**
   * @param existingQuesValCombList
   * @param quesValCombList
   */
  private boolean validateExistingCombination(final List<QuestDepenValCombination> existingQuesValCombList,
      final List<QuestDepenValCombination> quesValCombList) {
    for (QuestDepenValCombination quesDepnValCombination : quesValCombList) {
      for (QuestDepenValCombination existingquesDepnValCombination : existingQuesValCombList) {
        Map<Attribute, AttributeValue> existingquesDepnValMap = existingquesDepnValCombination.getAttrValMap();
        Map<Attribute, AttributeValue> quesDepnValMap = quesDepnValCombination.getAttrValMap();
        if (existingquesDepnValMap.size() != quesDepnValMap.size()) {
          return true;
        }
        int sizeOfMap = quesDepnValMap.size();
        int counter = 0;
        for (Entry<Attribute, AttributeValue> attrVal : quesDepnValMap.entrySet()) {
          Attribute attrKey = attrVal.getKey();
          AttributeValue value = attrVal.getValue();
          if ((null != existingquesDepnValMap.get(attrKey)) && existingquesDepnValMap.get(attrKey).equals(value)) {
            counter++;
          }
        }
        if (sizeOfMap == counter) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.BORDER | SWT.TITLE | SWT.MIN | SWT.RESIZE | SWT.MAX);

  }

  /**
   * Add mouse down listener to the pidc attribute value edit column
   */
  private void addMouseDownListener() {
    this.attrTab.getGrid().addMouseListener(new MouseAdapter() {

      @Override
      public void mouseDown(final MouseEvent event) {
        final int columnIndex =
            GridTableViewerUtil.getInstance().getTabColIndex(event, AddAttributeValueCombDialog.this.attrTab);


        if (columnIndex == ApicUiConstants.COLUMN_INDEX_2) {
          final Point point = new Point(event.x, event.y);
          // Determine which row was selected
          final GridItem item = AddAttributeValueCombDialog.this.attrTab.getGrid().getItem(point);
          if ((item != null) && !item.isDisposed()) {
            // Determine which column was selected
            for (int i = 0, n = AddAttributeValueCombDialog.this.attrTab.getGrid().getColumnCount(); i < n; i++) {
              final Rectangle rect = item.getBounds(i);
              if (rect.contains(point)) {
                editTabItem(columnIndex, point);
                break;
              }
            }
          }
        }
      }
    });
  }

  /**
   * @param columnIndex deines gridviewer column index
   */
  private void editTabItem(final int columnIndex, final Point point) {
    final QuesDepnAttributeRow editableAttr = getSelectedPIDCAttr(point);
    boolean editFlag = false;
    if (CommonUtils.isNotEmpty(this.listAttrValComb)) {
      editFlag = true;
    }

    if (columnIndex == ApicUiConstants.COLUMN_INDEX_2) {
      final AttrValueEditDialog dialog =
          new AttrValueEditDialog(this.attrTab.getControl().getShell(), editableAttr.getAttribute(), this, editFlag);
      dialog.open();

      GridItem[] items = this.attrTab.getGrid().getItems();
      for (GridItem gridItem : items) {
        if (ApicConstants.DEFAULT_COMBO_SELECT.equals(gridItem.getText(1)) || gridItem.getText(1).isEmpty()) {
          AddAttributeValueCombDialog.this.saveBtn.setEnabled(false);
          break;
        }
        AddAttributeValueCombDialog.this.saveBtn.setEnabled(true);
      }

    }
    else {
      CDMLogger.getInstance().info(ApicUiConstants.EDIT_NOT_ALLOWED, Activator.PLUGIN_ID);
    }
  }

  private QuesDepnAttributeRow getSelectedPIDCAttr(final Point point) {
    QuesDepnAttributeRow pidcAttr = null;
    // Determine which row was selected
    this.attrTab.getGrid().selectCell(point);
    final IStructuredSelection selection = (IStructuredSelection) this.attrTab.getSelection();
    if ((selection != null) && (!selection.isEmpty())) {
      final Object element = selection.getFirstElement();
      if (element instanceof QuesDepnAttributeRow) {
        pidcAttr = (QuesDepnAttributeRow) element;

      }
    }
    return pidcAttr;
  }


  /**
   * @return the attrTab
   */
  public GridTableViewer getAttrTab() {
    return this.attrTab;
  }


  /**
   * @return the attrVals
   */
  public ConcurrentMap<Attribute, SortedSet<AttributeValue>> getAttrVals() {
    return this.attrVals;
  }


}
