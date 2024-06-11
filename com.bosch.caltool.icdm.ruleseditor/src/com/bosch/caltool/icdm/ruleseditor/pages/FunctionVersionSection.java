/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.pages;

import java.util.SortedSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.model.a2l.FunctionVersionUnique;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;
import com.bosch.rcputils.griddata.GridDataUtil;


/**
 * function selection section in rules editor
 *
 * @author bru2cob
 */
public class FunctionVersionSection {

  /**
   * function selection grp width
   */
  private static final int FUNC_GRP_WIDTH = 1100;
  /**
   * Number of cols in the function selection section
   */
  private static final int FUNCTION_SECTION_COLS = 5;
  /**
   * Func selection section
   */
  private Section sectionOne;
  /**
   * Variant option
   */
  public static final String VARIANT = "Variant";
  /**
   * Alternative option
   */
  public static final String ALTERNATIVE = "Alternative";
  /**
   * Function version
   */
  public static final String FUNC_VERSION = "Version";

  /**
   * Function selection combo
   */
  private Combo comboFuncVersion;


  /**
   * @return the comboFuncVersion
   */
  public Combo getComboFuncVersion() {
    return this.comboFuncVersion;
  }


  /**
   * @return the sectionOne
   */
  public Section getSectionOne() {
    return this.sectionOne;
  }

  /**
   * Form instance
   */
  private Form formOne;
  /**
   * listpage instance
   */
  private ListPage listPage;
  /**
   * Details page
   */
  private DetailsPage detailsPage;
  /**
   * Parameter rules page
   */
  private ParametersRulePage paramRulesPg;


  /**
   * @return the formOne
   */
  public Form getFormOne() {
    return this.formOne;
  }

  /**
   * alternative btn
   */
  // iCDM-845
  private Button chkAlternative;

  /**
   * @return the chkAlternative
   */
  public Button getChkAlternative() {
    return this.chkAlternative;
  }


  /**
   * @return the chkVariant
   */
  public Button getChkVariant() {
    return this.chkVariant;
  }

  /**
   * Rules editor input
   */
  private final ReviewParamEditorInput editorInput;

  /**
   * @return the chkFuncVersion
   */
  public Button getChkFuncVersion() {
    return this.chkFuncVersion;
  }

  /**
   * Button for chk varaint
   */
  private Button chkVariant;
  /**
   * Button for chk func version
   */
  private Button chkFuncVersion;
  /**
   * Instance of composite
   */
  private final Composite composite;
  /**
   * Instance of confidbased rules page
   */
  private ConfigBasedRulesPage configRulesPg;

  /**
   * @param formPage formpage instance
   * @param editorInput editor input
   * @param composite composite
   * @param listPage listpage instance
   */
  public FunctionVersionSection(final AbstractFormPage formPage, final ReviewParamEditorInput editorInput,
      final Composite composite) {
    this.composite = composite;
    this.editorInput = editorInput;
    setPage(formPage);
  }


  /**
   * @param formPage2
   */
  private void setPage(final AbstractFormPage formPage) {
    if (formPage instanceof ListPage) {
      this.listPage = (ListPage) formPage;
    }
    else if (formPage instanceof DetailsPage) {
      this.detailsPage = (DetailsPage) formPage;
    }
    else if (formPage instanceof ParametersRulePage) {
      this.paramRulesPg = (ParametersRulePage) formPage;
    }
    else if (formPage instanceof ConfigBasedRulesPage) {
      this.configRulesPg = (ConfigBasedRulesPage) formPage;
    }

  }


  /**
   * Create func selection section
   *
   * @param toolkit This method initializes sectionOne
   */
  public void createFuncSelcSec(final FormToolkit toolkit) {

    this.sectionOne = toolkit.createSection(this.composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionOne.setText("Function Version");
    this.sectionOne.setExpanded(true);
    this.sectionOne.getDescriptionControl().setEnabled(false);
    // create form
    createFormOne(toolkit);
    this.sectionOne.setLayoutData(getGridDataWithOutGrabExcessVSpace());
    // set client
    this.sectionOne.setClient(this.formOne);

  }

  /**
   * This method initializes formOne
   *
   * @param gridLayout
   */
  private void createFormOne(final FormToolkit formToolkit) {

    this.formOne = formToolkit.createForm(this.sectionOne);

    final Group selGroup = new Group(this.formOne.getBody(), SWT.NONE);
    final GridLayout layout = new GridLayout(FUNCTION_SECTION_COLS, false);
    // set layout
    selGroup.setLayout(layout);
    GridData gridData = GridDataUtil.getInstance().getWidthHintGridData(FUNC_GRP_WIDTH);
    // set layout data
    selGroup.setLayoutData(gridData);
    // create function version combo
    createFuncVerCombo(selGroup);

    new Label(selGroup, SWT.NONE).setText(" Show parameters of selected : ");

    /* Function version radio button */
    createFuncVersion(selGroup);

    /* Alternative radio button */
    createFuncAlternative(selGroup);

    /* Variant radio button */
    createFuncVariant(selGroup);
    this.formOne.getBody().setLayoutData(gridData);
    this.formOne.getBody().setLayout(layout);
  }

  /**
   * Create function variant
   *
   * @param selGroup group
   */
  private void createFuncVariant(final Group selGroup) {
    this.chkVariant = new Button(selGroup, SWT.RADIO);
    this.chkVariant.setToolTipText("Show all parameters of selected version's variant: (E.g) 1.2.* ");
    this.chkVariant.setText("Version's Variant");
    this.chkVariant.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {

          @Override
          public void run() {
            Button btn = (Button) selectionevent.getSource();
            if (btn.getSelection()) {
              // set table input ans status based on selection
              setTabInput(false);
              FunctionVersionSection.this.editorInput.setSelectedCombo(VARIANT);
            }
          }
        });
      }
    });
  }

  /**
   * Create function alternative option
   *
   * @param selGroup group
   */
  private void createFuncAlternative(final Group selGroup) {
    this.chkAlternative = new Button(selGroup, SWT.RADIO);
    this.chkAlternative.setToolTipText("Show all parameters of selected version's alternative: (E.g) 1.*.* ");
    this.chkAlternative.setText("Version's Alternative");
    this.chkAlternative.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {

          @Override
          public void run() {
            Button btn = (Button) selectionevent.getSource();
            if (btn.getSelection()) {
              // set table input ans status based on selection
              setTabInput(false);
              FunctionVersionSection.this.editorInput.setSelectedCombo(ALTERNATIVE);
            }
          }
        });
      }
    });
  }

  /**
   * Create function version
   *
   * @param selGroup group
   */
  private void createFuncVersion(final Group selGroup) {
    this.chkFuncVersion = new Button(selGroup, SWT.RADIO);
    this.chkFuncVersion.setToolTipText("Show parameters of the selected function version only");
    this.chkFuncVersion.setText(FUNC_VERSION);
    this.chkFuncVersion.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {

          @Override
          public void run() {
            Button btn = (Button) selectionevent.getSource();
            if (btn.getSelection()) {
              // set table input ans status based on selection
              setTabInput(false);
              FunctionVersionSection.this.editorInput.setSelectedCombo(FUNC_VERSION);
            }
          }
        });
      }
    });
  }

  /**
   * Create function version combo
   *
   * @param selGroup
   */
  private void createFuncVerCombo(final Group selGroup) {
    this.comboFuncVersion = new Combo(selGroup, SWT.READ_ONLY);
    this.comboFuncVersion.add(ApicConstants.OPTION_ALL);
    final GridData descData = new GridData(SWT.NONE, SWT.NONE, false, false);
    descData.widthHint = 210;
    this.comboFuncVersion.setLayoutData(descData);

    SortedSet<FunctionVersionUnique> sortedVersions = this.editorInput.getSortedVersions();

    // fetch only if needed
    if (sortedVersions == null) {
      sortedVersions = this.editorInput.getDataProvider().getSortedVersions(this.editorInput.getSelectedObject());
      this.editorInput.setSortedVersions(sortedVersions);
    }


    // Get all function version to be implemented
    for (FunctionVersionUnique version : sortedVersions) {
      this.comboFuncVersion.add(version.getFuncVersion());
    }
    if (FunctionVersionSection.this.editorInput.getCdrFuncVersion() == null) {
      this.comboFuncVersion.select(0);
    }
    else {
      this.comboFuncVersion
          .select(this.comboFuncVersion.indexOf(FunctionVersionSection.this.editorInput.getCdrFuncVersion()));

    }
    // Selection listener
    this.comboFuncVersion.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        if (FunctionVersionSection.this.chkAlternative.getSelection() ||
            FunctionVersionSection.this.chkVariant.getSelection()) {
          FunctionVersionSection.this.chkFuncVersion.setSelection(false);
        }
        else {
          FunctionVersionSection.this.chkFuncVersion.setSelection(true);
          FunctionVersionSection.this.editorInput.setSelectedCombo(FUNC_VERSION);
        }
        // set table input ans status based on selection
        setTabInput(true);
      }


    });
  }

  /**
   * @return
   */
  public GridData getGridDataWithOutGrabExcessVSpace() {
    final GridData gridDataFour = new GridData();
    gridDataFour.horizontalAlignment = GridData.FILL;
    gridDataFour.grabExcessHorizontalSpace = true;
    gridDataFour.verticalAlignment = GridData.FILL;
    return gridDataFour;
  }


  /**
   * Call set table input of the corresponding page
   *
   * @param funcVersionChange
   */
  private void setTabInput(final boolean funcVersionChange) {
    ReviewParamEditor editor = null;
    if (this.listPage != null) {
      FunctionVersionSection.this.listPage.setTabViewerInput();
      FunctionVersionSection.this.listPage.getParamTabSec().getParamTab().reconstructNatTable();
      FunctionVersionSection.this.listPage.getParamTabSec().getParamTab().setStatusBarMessage(false);
      editor = FunctionVersionSection.this.listPage.getEditor();

    }
    if (this.detailsPage != null) {
      FunctionVersionSection.this.detailsPage.setTabViewerInput(funcVersionChange);
      FunctionVersionSection.this.detailsPage
          .setStatusBarMessage(FunctionVersionSection.this.detailsPage.getFcTableViewer());
      editor = FunctionVersionSection.this.detailsPage.getEditor();
    }
    if (this.paramRulesPg != null) {
      FunctionVersionSection.this.paramRulesPg.setTabViewerInput(funcVersionChange);
      FunctionVersionSection.this.paramRulesPg
          .setStatusBarMessage(FunctionVersionSection.this.paramRulesPg.getFcTableViewer());
      editor = FunctionVersionSection.this.paramRulesPg.getEditor();
    }
    if (this.configRulesPg != null) {
      this.configRulesPg.setParamTabViewerInput();
      this.configRulesPg.setAttrTabViewerInput();
      FunctionVersionSection.this.configRulesPg
          .setStatusBarMessage(FunctionVersionSection.this.configRulesPg.getParamTableViewer());
      editor = FunctionVersionSection.this.configRulesPg.getEditor();
    }
    if (editor != null) {
      refreshAllPages(editor);
    }
  }


  /**
   * @param editor
   */
  private void refreshAllPages(final ReviewParamEditor editor) {


    ListPage editorListPage = editor.getListPage();
    if ((editorListPage != null) && (this.listPage == null)) {
      if (editorListPage.funcSelcSec != null) {
        editorListPage.funcSelcSec.getComboFuncVersion().indexOf(editor.getEditorInput().getCdrFuncVersion());
      }
      editorListPage.setActive(true);
      editorListPage.getParamTabSec().getParamTab().reconstructNatTable();
      editorListPage.getParamTabSec().getParamTab().setStatusBarMessage(false);
    }

    DetailsPage editorDetailsPage = editor.getDetailsPage();
    if ((editorDetailsPage != null) && (this.detailsPage == null)) {
      if (editorDetailsPage.funcSelcSec != null) {
        editorDetailsPage.funcSelcSec.getComboFuncVersion().indexOf(editor.getEditorInput().getCdrFuncVersion());
      }
      editorDetailsPage.setActive(true);
      editorDetailsPage.setStatusBarMessage(editorDetailsPage.getFcTableViewer());
    }
    ParametersRulePage editorParamRulesPage = editor.getParamRulesPage();
    if ((editorParamRulesPage != null) && (this.paramRulesPg == null)) {
      if (editorParamRulesPage.funcSelcSec != null) {
        editorParamRulesPage.funcSelcSec.getComboFuncVersion().indexOf(editor.getEditorInput().getCdrFuncVersion());
      }
      editorParamRulesPage.setActive(true);
      editorParamRulesPage.setStatusBarMessage(editorParamRulesPage.getFcTableViewer());
    }

    ConfigBasedRulesPage editorConfigPage = editor.getConfigPage();
    if ((editorConfigPage != null) && (this.configRulesPg == null)) {
      editorConfigPage.setParamTabViewerInput();
      editorConfigPage.setAttrTabViewerInput();
      editorConfigPage.setStatusBarMessage(editorConfigPage.getParamTableViewer());
    }

  }
}
