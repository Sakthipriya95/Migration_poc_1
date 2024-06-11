package com.bosch.caltool.usecase.ui.views;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.common.ui.sorter.ValuesGridTabViewerSorter;
import com.bosch.caltool.icdm.common.ui.table.filters.ValueFilters;
import com.bosch.caltool.icdm.common.ui.views.AbstractViewPart;
import com.bosch.caltool.usecase.ui.views.providers.UseCaseAttributeValuesTableLabelProvider;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * @author adn1cob
 */
public class AttributeValuesViewPart extends AbstractViewPart {

  /**
   * ID of this view part
   */
  public static final String PART_ID = "com.bosch.caltool.usecase.ui.views.AttributeValuesViewPart";

  private FormToolkit toolkit;
  private ScrolledForm scrolledForm;
  private Composite composite;
  private Composite compositeAttributeValues;
  private Section sectionAttributeValues;
  private Form formAttributeValues;
  /**
   * valueTabViewer table viewer
   */
  private GridTableViewer valueTabViewer;
  private GridViewerColumn valueColumn;
  private GridViewerColumn unitColumn;
  private GridViewerColumn descColumn;
  private AbstractViewerSorter valTabSorter;
  private AttributeClientBO selectedAttribute;
  private Text valueFilterTxt;
  private ValueFilters attrValueFilters;
  /**
   * Constant defining the TypeFilterText label
   */
  private static final String FILTER_TXT_LBL = "type filter text";


  @Override
  public void createPartControl(final Composite parent) {
    addHelpAction();
    this.toolkit = new FormToolkit(parent.getDisplay());
    createScrolledForm(parent);
    this.composite = this.scrolledForm.getForm().getBody();
    createCompositeAttributeValues(this.toolkit);
  }

  /**
   * @param toolkit2
   */
  private void createCompositeAttributeValues(final FormToolkit toolkit2) {

    final GridData gridData1 = GridDataUtil.getInstance().getGridData();
    this.compositeAttributeValues = toolkit2.createComposite(this.composite);
    this.compositeAttributeValues.setLayout(new GridLayout());
    createSectionAttributeValues(toolkit2);
    this.compositeAttributeValues.setLayoutData(gridData1);
    addAttrValuesFilters();
    this.valueTabViewer.setComparator(this.valTabSorter);
  }

  /**
   *
   */
  private void addAttrValuesFilters() {
    this.attrValueFilters = new ValueFilters();
    // Add Value TableViewer filter
    this.valueTabViewer.addFilter(this.attrValueFilters);

  }

  /**
   * @param toolkit2
   */
  private void createSectionAttributeValues(final FormToolkit toolkit2) {
    final GridData gridData5 = GridDataUtil.getInstance().getGridData();
    this.sectionAttributeValues =
        toolkit2.createSection(this.compositeAttributeValues, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionAttributeValues.setText("Attribute Values");


    createFormAttributeValues(toolkit2);
    this.sectionAttributeValues.setLayoutData(gridData5);
    this.sectionAttributeValues.setClient(this.formAttributeValues);

    this.sectionAttributeValues.getDescriptionControl().setEnabled(false);

  }

  /**
   * @param toolkit2
   */
  private void createFormAttributeValues(final FormToolkit toolkit2) {
    this.formAttributeValues = toolkit2.createForm(this.sectionAttributeValues);
    this.formAttributeValues.getBody().setLayout(new GridLayout());

    this.valueFilterTxt = toolkit2.createText(this.formAttributeValues.getBody(), null, SWT.SINGLE | SWT.BORDER);
    createAttrValueFilterTxt();
    final GridData gridData = getTableViewerGridData();
    this.valueTabViewer = new GridTableViewer(this.formAttributeValues.getBody(),
        SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
    this.valueTabViewer.getGrid().setLayoutData(gridData);
    this.valueTabViewer.getGrid().setLinesVisible(true);
    this.valueTabViewer.getGrid().setHeaderVisible(true);
    createValueTabColumns();

    this.valueTabViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.valueTabViewer.setLabelProvider(new UseCaseAttributeValuesTableLabelProvider(this));
    if ((this.selectedAttribute != null) && (this.selectedAttribute.getAttrValues() != null)) {
      this.valueTabViewer.setInput(this.selectedAttribute.getAttrValues());
    }
  }


  /**
   * This method creates filter text for Values
   */
  private void createAttrValueFilterTxt() {
    final GridData gridData = getFilterTxtGridData();
    this.valueFilterTxt.setLayoutData(gridData);
    this.valueFilterTxt.setMessage(FILTER_TXT_LBL);
    this.valueFilterTxt.addModifyListener(event -> {
      final String text = AttributeValuesViewPart.this.valueFilterTxt.getText().trim();
      AttributeValuesViewPart.this.attrValueFilters.setFilterText(text);
      AttributeValuesViewPart.this.valueTabViewer.refresh();
    });
  }

  /**
   * This method returns filter text GridData object
   *
   * @return GridData
   */
  private GridData getFilterTxtGridData() {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.verticalAlignment = GridData.CENTER;
    return gridData;
  }

  /**
   *
   */
  private void createValueTabColumns() {
    this.valTabSorter = new ValuesGridTabViewerSorter();
    this.valueColumn = new GridViewerColumn(this.valueTabViewer, SWT.LEFT);
    this.valueColumn.getColumn().setText("Value");
    this.valueColumn.getColumn().setWidth(150);
    this.valueColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(this.valueColumn.getColumn(), 0, this.valTabSorter, this.valueTabViewer));

    this.unitColumn = new GridViewerColumn(this.valueTabViewer, SWT.LEFT);
    this.unitColumn.getColumn().setText("Unit");
    this.unitColumn.getColumn().setWidth(80);

    this.unitColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(this.unitColumn.getColumn(), 1, this.valTabSorter, this.valueTabViewer));


    this.descColumn = new GridViewerColumn(this.valueTabViewer, SWT.LEFT);
    this.descColumn.getColumn().setText("Description");
    this.descColumn.getColumn().setWidth(200);

    this.descColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(this.descColumn.getColumn(), 2, this.valTabSorter, this.valueTabViewer));

  }

  private void createScrolledForm(final Composite parent) {
    // Create scrolled form
    this.scrolledForm = this.toolkit.createScrolledForm(parent);
    // Set decorate form heading to form
    this.toolkit.decorateFormHeading(this.scrolledForm.getForm());
    // Set layout to scrolledForm
    final GridLayout gridLayout = new GridLayout();
    this.scrolledForm.getBody().setLayout(gridLayout);
    // Set grid layout data to scrolledForm
    this.scrolledForm.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  private GridData getTableViewerGridData() {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;

    gridData.heightHint = 250;
    return gridData;
  }

  // No implementation
  @Override
  public void setFocus() {
    // No implementation
  }

  /**
   * @param attributeClientBO Attribute icdm-292
   */
  public void setSelectedAttr(final AttributeClientBO attributeClientBO) {
    this.selectedAttribute = attributeClientBO;
    if ((attributeClientBO != null) && (attributeClientBO.getAttrValues() != null)) {
      this.valueTabViewer.setInput(this.selectedAttribute.getAttrValues());
    }
    else {
      this.valueTabViewer.setInput(null);
    }
  }


  /**
   * @return the valueTabViewer
   */
  public GridTableViewer getValueTabViewer() {
    return this.valueTabViewer;
  }

}
