/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.io.IOException;
import java.util.SortedSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.RULE_SOURCE;
import com.bosch.caltool.icdm.model.cdr.RvwParametersSecondary;
import com.bosch.caltool.icdm.model.cdr.RvwResultsSecondary;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author svj7cob
 */
// Task 231287
public class SecondaryResultConsDialog extends AbstractDialog {

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
   * the form instance
   */
  private Form form;

  /**
   * secondary result param set
   */
  private final SortedSet<RvwParametersSecondary> secondResParamSet;

  /**
   * the top instance
   */
  private Composite top;

  /**
   * the parameter name
   */
  private final String paramName;
  ReviewResultClientBO resultData;

  /**
   * @param parentShell parentShell
   * @param secondaryParamSet secondResParamSet
   * @param paramName paramName
   * @param resultData
   */
  public SecondaryResultConsDialog(final Shell parentShell, final SortedSet<RvwParametersSecondary> secondaryParamSet,
      final String paramName, final ReviewResultClientBO resultData) {
    super(parentShell);
    this.secondResParamSet = secondaryParamSet;
    this.paramName = paramName;
    this.resultData = resultData;
  }

  /**
   * This shell configures the Text and Size of the dialog
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Consolidated Secondary Review Result");
    super.configureShell(newShell);
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    createButton(parent, IDialogConstants.OK_ID, "OK", false);
  }

  /**
   * By default returns true
   */
  @Override
  protected boolean isResizable() {
    // if true, the user can resize the dialog
    return true;
  }

  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    // create composite on parent comp
    createComposite();
    return this.top;
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    // create section
    createSection();
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * This method initializes section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        "Secondary Review Results of Param : " + this.paramName);
    // create form
    createForm();
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.getDescriptionControl().setEnabled(false);
    this.section.setClient(this.form);

  }

  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(new GridLayout());

    // create grid table viewer
    createGridTabViewer();
  }

  private void createGridTabViewer() {
    GridTableViewer gridTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE,
        GridDataUtil.getInstance().getHeightHintGridData(200));
    // create table columns
    createRuleSetColumn(gridTableViewer);
    createSourceColumn(gridTableViewer);
    createLowerLimitColumn(gridTableViewer);
    createUpperLimitColumn(gridTableViewer);
    createBitWiseLimitColumn(gridTableViewer);
    createRefValueColumn(gridTableViewer);
    createResultColumn(gridTableViewer);

    gridTableViewer.setContentProvider(ArrayContentProvider.getInstance());
    gridTableViewer.setInput(this.secondResParamSet);
  }

  /**
   * @param gridTableViewer
   */
  private void createSourceColumn(final GridTableViewer gridTableViewer) {
    // create ref valuecolumn
    final GridViewerColumn resultColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(gridTableViewer, "Rule Set Name", 150);
    resultColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        final RvwParametersSecondary item = (RvwParametersSecondary) element;
        RvwResultsSecondary secondaryResult =
            SecondaryResultConsDialog.this.resultData.getSecondaryResult(item.getSecReviewId());
        if (RULE_SOURCE.RULE_SET.getDbVal().equals(secondaryResult.getSource())) {
          return secondaryResult.getRuleSetName();
        }
        return "";
      }
    });

  }

  /**
   * Creates bit set column
   *
   * @param gridTableViewer
   */
  private void createBitWiseLimitColumn(final GridTableViewer gridTableViewer) {
    // create bitwise limit column
    final GridViewerColumn bitWiseLimitColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(gridTableViewer, "BitWise Limit", 100);
    bitWiseLimitColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        final RvwParametersSecondary item = (RvwParametersSecondary) element;
        return CommonUtils.checkNull(item.getBitwiseLimit());
      }
    });
  }

  /**
   * Creates upper limit column
   *
   * @param gridTableViewer
   */
  private void createUpperLimitColumn(final GridTableViewer gridTableViewer) {
    // create upper limit column
    final GridViewerColumn upperLimitColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(gridTableViewer, "Upper Limit", 100);
    upperLimitColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        final RvwParametersSecondary item = (RvwParametersSecondary) element;
        return String.valueOf(CommonUtils.checkNull(item.getUpperLimit(), ""));
      }
    });
  }

  /**
   * Creates lower limit column
   *
   * @param gridTableViewer
   */
  private void createLowerLimitColumn(final GridTableViewer gridTableViewer) {
    // create lower limit column
    final GridViewerColumn lowerLimitColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(gridTableViewer, "Lower Limit", 100);
    lowerLimitColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        final RvwParametersSecondary item = (RvwParametersSecondary) element;
        return String.valueOf(CommonUtils.checkNull(item.getLowerLimit(), ""));
      }
    });
  }

  /**
   * Creates result column
   *
   * @param gridTableViewer
   */
  private void createResultColumn(final GridTableViewer gridTableViewer) {
    // create ref valuecolumn
    final GridViewerColumn resultColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(gridTableViewer, "Result", 100);
    resultColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        final RvwParametersSecondary item = (RvwParametersSecondary) element;
        return SecondaryResultConsDialog.this.resultData.getSecondaryCommonResult(item);
      }
    });
  }

  /**
   * Creates ref value column
   *
   * @param gridTableViewer
   */
  private void createRefValueColumn(final GridTableViewer gridTableViewer) {
    // create ref valuecolumn
    final GridViewerColumn refValueColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(gridTableViewer, "Ref Value", 100);
    refValueColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        final RvwParametersSecondary item = (RvwParametersSecondary) element;
        CalData refValueObj;
        try {
          refValueObj = CalDataUtil.getCalDataObj(item.getRefValue());
          if (refValueObj != null) {
            return refValueObj.getCalDataPhy().getSimpleDisplayValue();
          }
        }
        catch (ClassNotFoundException | IOException exp) {
          CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
        }

        return "";
      }
    });
  }

  /**
   * Creates rule set column
   *
   * @param gridTableViewer
   */
  private void createRuleSetColumn(final GridTableViewer gridTableViewer) {
    // create rule set column
    final GridViewerColumn ruleSetName =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(gridTableViewer, "Rule Source", 100);
    ruleSetName.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        final RvwParametersSecondary item = (RvwParametersSecondary) element;
        RvwResultsSecondary secondaryResult =
            SecondaryResultConsDialog.this.resultData.getSecondaryResult(item.getSecReviewId());
        return RULE_SOURCE.getSource(secondaryResult.getSource()).getUiVal();
      }
    });
  }

  /**
   * create / return existing form tool kit
   *
   * @return
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }


}
