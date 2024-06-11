/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.pages;

import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.icdm.common.ui.providers.SelectionProviderMediator;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;
import com.bosch.caltool.icdm.ruleseditor.sorters.CDRFuncParamTabViewerSorter;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.text.TextUtil;


/**
 * Parameters table section in list page of Rules editor
 *
 * @author bru2cob
 */
public class ParamTableSection {

  /**
   * Instance of section to hold table
   */
  private Section sectionTwo;

  /**
   * @return the sectionTwo
   */
  public Section getSectionTwo() {
    return this.sectionTwo;
  }

  /**
   * Form instance
   */
  private Form formTwo;

  /**
   * @return the formTwo
   */
  public Form getFormTwo() {
    return this.formTwo;
  }

  /**
   * Table sorter instance
   */
  private CDRFuncParamTabViewerSorter fcTabSorter;
  /**
   * Listpage instance
   */
  private final ListPage listPage;

  /**
   * @return the listPage
   */
  public ListPage getListPage() {
    return this.listPage;
  }


  /**
   * @return the fcTabSorter
   */
  public CDRFuncParamTabViewerSorter getFcTabSorter() {
    return this.fcTabSorter;
  }

  /**
   * Filter text instance
   */
  private Text filterTxt;

  /**
   * param table instance
   */
  private ParamNatTable paramTab;


  /**
   * @return the summaryTreeViewer
   */
  public CustomNATTable getNatTable() {
    return this.paramTab.getNatTable();
  }

  /**
   * Returns filter text
   *
   * @return the filterTxt
   */
  public Text getFilterTxt() {
    return this.filterTxt;
  }

  /**
   * @param listPage listpage instance
   */
  public ParamTableSection(final ListPage listPage) {
    this.listPage = listPage;
  }

  /**
   * @param toolkit This method initializes sectionTwo
   */
  void createParamTabSec(final FormToolkit toolkit) {
    // create the section to hold the parm table
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.sectionTwo =
        toolkit.createSection(this.listPage.getComposite(), Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionTwo.setText("Parameters");
    this.sectionTwo.setExpanded(true);
    this.sectionTwo.getDescriptionControl().setEnabled(false);
    // create form in the section
    createFormTwo(toolkit);
    // set section layout data
    this.sectionTwo.setLayoutData(gridData);
    // set form as section client
    this.sectionTwo.setClient(this.formTwo);
  }

  /**
   * @param toolkit This method initializes form
   */
  private void createFormTwo(final FormToolkit toolkit) {

    this.formTwo = toolkit.createForm(this.sectionTwo);
    this.formTwo.getBody().setLayout(new GridLayout());


    this.fcTabSorter =
        new CDRFuncParamTabViewerSorter(this.listPage.getEditor().getEditorInput().getParamDataProvider());
    // ICDM-1185
    // Create Filter text
    createFilterTxt(this.formTwo.getBody());
    // create table viewer
    this.paramTab = new ParamNatTable(this);
    this.paramTab.createTableViewer();
    this.paramTab.createToolBarAction();
    // Add filters to the TableViewer
    this.listPage.addFilters();


    final ReviewParamEditor reviewParamEditor = this.listPage.getEditor();
    final SelectionProviderMediator selProviderMed = reviewParamEditor.getSelectionProviderMediator();
    this.listPage.getSite().setSelectionProvider(selProviderMed);

  }


  // ICDM-1185
  /**
   * This method creates filter text
   *
   * @param tableComp
   */
  private void createFilterTxt(final Composite tableComp) {
    this.filterTxt = TextUtil.getInstance().createFilterText(this.listPage.getEditor().getToolkit(), tableComp,
        GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.setMessage("type filter text");
    this.filterTxt.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        String text = ParamTableSection.this.filterTxt.getText().trim();
        ParamTableSection.this.getParamTab().getAllColumnFilterMatcher().setFilterText(text, true);
        ParamTableSection.this.getParamTab().getCustomFilterGridLayer().getComboGlazedListsFilterStrategy()
            .applyFilterInAllColumns(text);

        ParamTableSection.this.getParamTab().getCustomFilterGridLayer().getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                ParamTableSection.this.getParamTab().getCustomFilterGridLayer().getSortableColumnHeaderLayer()));

        ParamTableSection.this.getParamTab()
            .setStatusBarMessage(ParamTableSection.this.getParamTab().getGroupByHeaderLayer(), false);
      }
    });
  }


  /**
   * @return the paramTab
   */
  public ParamNatTable getParamTab() {
    return this.paramTab;
  }

}
