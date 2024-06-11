/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.table.filters.ARCReleasedParamFilter;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.cdr.ui.actions.ReviewResultNATActionSet;
import com.bosch.caltool.cdr.ui.editors.pages.ReviewResultParamListPage;
import com.bosch.caltool.cdr.ui.views.providers.ARCTableLabelProvider;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * @author msp5cob
 */
public class ARCReleaseDisplayDialog extends AbstractDialog {

  /**
   *
   */
  private static final String CONFIRM_ARC_RELEASE_MSG = "Confirm ARC Release for the following Review Parameters";
  /**
  *
  */
  private static final String TITLE_TXT = "ARC Release Confirmation";

  /**
   * Composite instance
   */
  private Composite top;
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
   * Form instance
   */
  private Form form;
  /**
   * GridTableViewer instance for add values
   */
  private GridTableViewer paramNameTabViewer;

  private final List<CDRResultParameter> arcReleaseParams;

  private ARCReleasedParamFilter filter;

  private ARCTableLabelProvider arcTableLabelProvider;

  private final ReviewResultParamListPage reviewResultNatPage;

  /**
   * The parameterized constructor
   *
   * @param parentShell instance
   * @param arcReleaseParams arcReleaseParams
   * @param reviewResultNatPage ReviewResultParamListPage
   */
  public ARCReleaseDisplayDialog(final Shell parentShell, final List<CDRResultParameter> arcReleaseParams,
      final ReviewResultParamListPage reviewResultNatPage) {
    super(parentShell);
    this.arcReleaseParams = arcReleaseParams;
    this.reviewResultNatPage = reviewResultNatPage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);

    // Set title
    setTitle(CONFIRM_ARC_RELEASE_MSG);
    // Set the message
    try {
      String information = new CommonDataBO().getMessage("REVIEW_RESULT", "ARC_CONFIRMATION_INFO");
      setMessage(information, IMessageProvider.INFORMATION);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Set shell name
    newShell.setText(TITLE_TXT);
    super.configureShell(newShell);

  }

  /**
   * Creates the gray area
   *
   * @param parent the parent composite
   * @return Control
   */
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
        "Total ARC Parameters : " + this.arcReleaseParams.size());
    // create form
    createForm();
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.getDescriptionControl().setEnabled(false);
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);

    // Create values grid tableviewer
    createValuesGridTabViewer();

    this.form.getBody().setLayout(new GridLayout());

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
   * This method creates the addNewUserTableViewer
   *
   * @param gridData
   */
  private void createValuesGridTabViewer() {
    this.filter = new ARCReleasedParamFilter();

    // create filter text
    createFilterTxt();

    this.paramNameTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FILL | SWT.BORDER, GridDataUtil.getInstance().getGridData());

    this.paramNameTabViewer.setContentProvider(ArrayContentProvider.getInstance());

    // Create columns
    this.arcTableLabelProvider = new ARCTableLabelProvider();
    final GridViewerColumn serialNoColumn = new GridViewerColumn(this.paramNameTabViewer, SWT.NONE);
    serialNoColumn.getColumn().setText("SI.No");
    serialNoColumn.getColumn().setWidth(50);
    serialNoColumn.setLabelProvider(this.arcTableLabelProvider);


    final GridViewerColumn paramName = new GridViewerColumn(this.paramNameTabViewer, SWT.NONE);
    paramName.getColumn().setText("Parameter Name");
    paramName.getColumn().setWidth(450);
    paramName.setLabelProvider(this.arcTableLabelProvider);

    this.paramNameTabViewer.addFilter(this.filter);

    this.paramNameTabViewer.setInput(this.arcReleaseParams);

  }

  /**
   * This method creates filter text
   */
  private void createFilterTxt() {
    Text filterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));

    filterTxt.addModifyListener(event -> {
      // when there is a modification in the text filter, set the text to the filter
      final String text = filterTxt.getText().trim();
      this.filter.setFilterText(text);
      // Reset the Row Counter for each filter search
      this.arcTableLabelProvider.resetRowCounter();

      // refresh to filter the table based on table
      this.paramNameTabViewer.refresh();

    });
    // set focus to type filter
    filterTxt.setFocus();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }


  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.YES_LABEL, true);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.NO_LABEL, true);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    new ReviewResultNATActionSet().multiParamUpdateForARC(this.arcReleaseParams, true, this.reviewResultNatPage);
    super.okPressed();
  }
}
