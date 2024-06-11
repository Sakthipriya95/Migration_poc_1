/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.action;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.ParserLogger;
import com.bosch.caltool.icdm.model.cda.CustomerFilter;
import com.bosch.caltool.icdm.model.cda.FunctionFilter;
import com.bosch.caltool.icdm.model.cda.ParameterFilterLabel;
import com.bosch.caltool.icdm.model.cda.PlatformFilter;
import com.bosch.caltool.icdm.model.cda.SystemConstantFilter;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.wizards.pages.CaldataAnalyzerCustFilterWizardPage;
import com.bosch.caltool.icdm.ui.wizards.pages.CaldataAnalyzerECUPlatWizardPage;
import com.bosch.caltool.icdm.ui.wizards.pages.CaldataAnalyzerFunctionFilterWizardPage;
import com.bosch.caltool.icdm.ui.wizards.pages.CaldataAnalyzerParamFilterWizardPage;
import com.bosch.caltool.icdm.ui.wizards.pages.CaldataAnalyzerSysConFilterWizardPage;
import com.bosch.caltool.labfunparser.exception.ParserException;
import com.bosch.caltool.labfunparser.textparser.InputFileParser;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * @author pdh2cob
 */

public class CaldataAnalyzerFilterActionSet {


  private Action pasteAction;

  private Action deleteAction;

  private Action importLabFileAction;

  private WizardPage page;

  /**
   * @param page
   */
  public CaldataAnalyzerFilterActionSet(final WizardPage page) {
    setInstanceObject(page);
  }


  private void setInstanceObject(final WizardPage page) {
    if (page instanceof CaldataAnalyzerParamFilterWizardPage) {
      this.page = page;
    }
    if (page instanceof CaldataAnalyzerFunctionFilterWizardPage) {
      this.page = page;
    }
    if (page instanceof CaldataAnalyzerCustFilterWizardPage) {
      this.page = page;
    }
    if (page instanceof CaldataAnalyzerECUPlatWizardPage) {
      this.page = page;
    }
    if (page instanceof CaldataAnalyzerSysConFilterWizardPage) {
      this.page = page;
    }
    if (page instanceof CaldataAnalyzerECUPlatWizardPage) {
      this.page = page;
    }
  }

  /**
   * @param manager
   * @param tableViewer
   * @param labels
   */
  public void setPasteAction(final IMenuManager manager) {


    this.pasteAction = new Action() {

      @Override
      public void run() {
        if (CaldataAnalyzerFilterActionSet.this.page instanceof CaldataAnalyzerParamFilterWizardPage) {
          List<ParameterFilterLabel> labels =
              ((CaldataAnalyzerParamFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getLabels();

          labels.addAll((Collection<? extends ParameterFilterLabel>) getDataFromClipBoard());
          ((CaldataAnalyzerParamFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getParamTabViewer()
              .setInput(labels);
          ((CaldataAnalyzerParamFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getParamTabViewer()
              .refresh();
        }

        if (CaldataAnalyzerFilterActionSet.this.page instanceof CaldataAnalyzerFunctionFilterWizardPage) {
          List<FunctionFilter> functions =
              ((CaldataAnalyzerFunctionFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getFunctions();

          functions.addAll((Collection<? extends FunctionFilter>) getDataFromClipBoard());
          ((CaldataAnalyzerFunctionFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getFunctionTabViewer()
              .setInput(functions);
          ((CaldataAnalyzerFunctionFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getFunctionTabViewer()
              .refresh();
        }
        if (CaldataAnalyzerFilterActionSet.this.page instanceof CaldataAnalyzerSysConFilterWizardPage) {
          List<SystemConstantFilter> systemConstants =
              ((CaldataAnalyzerSysConFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getSystemConstants();
          systemConstants.addAll((Collection<? extends SystemConstantFilter>) getDataFromClipBoard());
          ((CaldataAnalyzerSysConFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page)
              .getSystemConstantTabViewer().setInput(systemConstants);
          ((CaldataAnalyzerSysConFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page)
              .getSystemConstantTabViewer().refresh();
        }
        if (CaldataAnalyzerFilterActionSet.this.page instanceof CaldataAnalyzerCustFilterWizardPage) {
          List<CustomerFilter> customerList =
              ((CaldataAnalyzerCustFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getCustomerFilterModel()
                  .getCustomerFilterList();
          customerList.addAll((Collection<? extends CustomerFilter>) getDataFromClipBoard());
          ((CaldataAnalyzerCustFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getGridTabViewer()
              .setInput(customerList);
          ((CaldataAnalyzerCustFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getGridTabViewer().refresh();
        }
        if (CaldataAnalyzerFilterActionSet.this.page instanceof CaldataAnalyzerECUPlatWizardPage) {
          List<PlatformFilter> platformList =
              ((CaldataAnalyzerECUPlatWizardPage) CaldataAnalyzerFilterActionSet.this.page).getPlatformFilterModel()
                  .getPlatformFilterList();
          platformList.addAll((Collection<? extends PlatformFilter>) getDataFromClipBoard());
          ((CaldataAnalyzerECUPlatWizardPage) CaldataAnalyzerFilterActionSet.this.page).getGridTabViewer()
              .setInput(platformList);
          ((CaldataAnalyzerECUPlatWizardPage) CaldataAnalyzerFilterActionSet.this.page).getGridTabViewer().refresh();
        }
      }

    };

    this.pasteAction.setText("Paste");
    manager.add(this.pasteAction);

    final ImageDescriptor pasteLabel = ImageManager.getImageDescriptor(ImageKeys.PASTE_16X16);
    this.pasteAction.setImageDescriptor(pasteLabel);
    addToToolbarMgr(this.pasteAction);

  }


  private void addToToolbarMgr(final Action action) {
    if (this.page instanceof CaldataAnalyzerParamFilterWizardPage) {
      ((CaldataAnalyzerParamFilterWizardPage) this.page).getToolBarManager().add(action);
    }
    if (this.page instanceof CaldataAnalyzerFunctionFilterWizardPage) {
      ((CaldataAnalyzerFunctionFilterWizardPage) this.page).getToolBarManager().add(action);
    }
    if (this.page instanceof CaldataAnalyzerSysConFilterWizardPage) {
      ((CaldataAnalyzerSysConFilterWizardPage) this.page).getToolBarManager().add(action);
    }

    if (this.page instanceof CaldataAnalyzerCustFilterWizardPage) {
      ((CaldataAnalyzerCustFilterWizardPage) this.page).getToolBarManager().add(action);
    }
    if (this.page instanceof CaldataAnalyzerECUPlatWizardPage) {
      ((CaldataAnalyzerECUPlatWizardPage) this.page).getToolBarManager().add(action);
    }
  }


  /**
   * @param manager
   * @param tableViewer
   * @param labels
   * @param selectedLabelList
   * @param deletedLabel
   */
  public void setDeleteAction(final IMenuManager manager) {

    this.deleteAction = new Action() {

      @Override
      public void run() {
        deleteFilterItems();
      }

    };

    this.deleteAction.setText("Delete");
    this.deleteAction.setEnabled(true);
    manager.add(this.deleteAction);

    final ImageDescriptor deleteLabel = ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16);
    this.deleteAction.setImageDescriptor(deleteLabel);
    addToToolbarMgr(this.deleteAction);

  }


  /**
   * Action to import lab file
   */
  public void setImportLabAction() {
    // add import lab file action
    this.importLabFileAction = new Action() {

      @Override
      public void run() {
        importLABFile();
        ((CaldataAnalyzerParamFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getParamTabViewer()
            .setInput(((CaldataAnalyzerParamFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getLabels());
        ((CaldataAnalyzerParamFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getParamTabViewer().refresh();
      }
    };
    this.importLabFileAction.setText("Load LAB File");
    final ImageDescriptor importLAB = ImageManager.getImageDescriptor(ImageKeys.UPLOAD_LAB_16X16);
    this.importLabFileAction.setImageDescriptor(importLAB);
    ((CaldataAnalyzerParamFilterWizardPage) this.page).getToolBarManager().add(this.importLabFileAction);
  }

  /**
   * @return set of strings from clipboard
   */
  public List<?> getDataFromClipBoard() {

    List list = new ArrayList();
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Clipboard clipboard = toolkit.getSystemClipboard();
    try {
      String content = (String) clipboard.getData(DataFlavor.stringFlavor);
      if ((content != null) && !content.isEmpty() && !isAlreadyPresent(content)) {
        list = addStringToList(content);
      }
    }
    catch (UnsupportedFlavorException | IOException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, com.bosch.caltool.icdm.ui.Activator.PLUGIN_ID);
    }
    return list;
  }

  private boolean isAlreadyPresent(final String content) {
    boolean isPresent = false;
    String[] contentList = content.split("\\n");

    for (String string : contentList) {

      if (this.page instanceof CaldataAnalyzerParamFilterWizardPage) {
        for (ParameterFilterLabel label : ((CaldataAnalyzerParamFilterWizardPage) this.page).getLabels()) {
          if (string.trim().equals(label.getLabel())) {
            isPresent = true;
          }
        }
      }

      if (this.page instanceof CaldataAnalyzerFunctionFilterWizardPage) {
        for (FunctionFilter function : ((CaldataAnalyzerFunctionFilterWizardPage) this.page).getFunctions()) {
          if (string.trim().equals(function.getFunctionName())) {
            isPresent = true;
          }
        }
      }


      if (this.page instanceof CaldataAnalyzerCustFilterWizardPage) {
        for (CustomerFilter customer : ((CaldataAnalyzerCustFilterWizardPage) this.page).getCustomerFilterModel()
            .getCustomerFilterList()) {
          if (string.trim().equals(customer.getCustomerName())) {
            isPresent = true;
          }
        }
      }

      if (this.page instanceof CaldataAnalyzerECUPlatWizardPage) {
        for (PlatformFilter platform : ((CaldataAnalyzerECUPlatWizardPage) this.page).getPlatformFilterModel()
            .getPlatformFilterList()) {
          if (string.trim().equals(platform.getEcuPlatformName())) {
            isPresent = true;
          }
        }
      }

    }
    return isPresent;
  }

  private List addStringToList(final String content) {

    List list = new ArrayList();

    String[] contentList = content.split("\\n");
    for (String string : contentList) {
      if (this.page instanceof CaldataAnalyzerParamFilterWizardPage) {
        ParameterFilterLabel label = new ParameterFilterLabel();
        label.setLabel(string.trim());
        label.setMustExist(false);
        list.add(label);
      }
      if (this.page instanceof CaldataAnalyzerFunctionFilterWizardPage) {
        FunctionFilter function = new FunctionFilter();
        function.setFunctionName(string.trim());
        function.setFunctionVersion("*");
        list.add(function);
      }
      if (this.page instanceof CaldataAnalyzerSysConFilterWizardPage) {
        SystemConstantFilter sysCon = new SystemConstantFilter();
        sysCon.setSystemConstantName(string.trim());
        sysCon.setSystemConstantValue("");
        sysCon.setInverseFlag(false);
        list.add(sysCon);
      }

      if (this.page instanceof CaldataAnalyzerCustFilterWizardPage) {
        CustomerFilter customFilter = new CustomerFilter();
        customFilter.setCustomerName(string.trim());
        list.add(customFilter);
      }

      if (this.page instanceof CaldataAnalyzerECUPlatWizardPage) {
        PlatformFilter platformFilter = new PlatformFilter();
        platformFilter.setEcuPlatformName(string.trim());
        list.add(platformFilter);
      }

    }
    return list;
  }

  /**
   * ICDM-841 import lab file
   */
  protected void importLABFile() {
    final FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN | SWT.MULTI);
    fileDialog.setText("Import LAB file");
    fileDialog.setFilterExtensions(new String[] { "*.lab" });
    fileDialog.setFilterNames(new String[] { "LAB File(*.lab)" });
    final String selectedFile = fileDialog.open();
    if (selectedFile != null) {
      Runnable busyRunnable = new Runnable() {

        @Override
        public void run() {
          try {
            InputFileParser fileParser;
            String filterPath = fileDialog.getFilterPath();
            String[] filesNames = fileDialog.getFileNames();
            File selFile;
            for (String fileName : filesNames) {
              selFile = new File(filterPath, fileName);
              String filePath = selFile.getAbsolutePath();
              fileParser = new InputFileParser(ParserLogger.getInstance(), filePath);
              fileParser.parse();
              List<String> fileLabels = fileParser.getLabels();
              List<ParameterFilterLabel> labelList = new ArrayList<>();
              if (!fileLabels.isEmpty()) {
                for (String string : fileLabels) {
                  ParameterFilterLabel label = new ParameterFilterLabel();
                  label.setLabel(string);
                  label.setMustExist(false);
                  if (!((CaldataAnalyzerParamFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getLabels()
                      .contains(label)) {
                    labelList.add(label);
                  }
                }
                ((CaldataAnalyzerParamFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getLabels()
                    .addAll(labelList);
              }
              else {
                MessageDialogUtils.getInfoMessageDialog("", "The File " + fileName + " is invalid !");
              }
            }
          }
          catch (ParserException e) {
            CDMLogger.getInstance().error("Error while importing Lab File. " + e.getMessage(), e, Activator.PLUGIN_ID);
          }
        }
      };
      BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(), busyRunnable);
    }

  }


  /**
   * @return the deleteAction
   */
  public Action getDeleteAction() {
    return this.deleteAction;
  }


  /**
   * @return the pasteAction
   */
  public Action getPasteAction() {
    return this.pasteAction;
  }


  /**
   *
   */
  public void deleteFilterItems() {
    if (CaldataAnalyzerFilterActionSet.this.page instanceof CaldataAnalyzerParamFilterWizardPage) {
      List<ParameterFilterLabel> labels =
          ((CaldataAnalyzerParamFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getLabels();
      List<ParameterFilterLabel> selectedLabelList =
          ((CaldataAnalyzerParamFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getParamTabViewer()
              .getStructuredSelection().toList();

      for (ParameterFilterLabel label : selectedLabelList) {
        labels.remove(label);
      }
      ((CaldataAnalyzerParamFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getParamTabViewer()
          .setInput(labels);
      ((CaldataAnalyzerParamFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getParamTabViewer().refresh();
    }

    if (CaldataAnalyzerFilterActionSet.this.page instanceof CaldataAnalyzerFunctionFilterWizardPage) {
      List<FunctionFilter> functions =
          ((CaldataAnalyzerFunctionFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getFunctions();
      List<FunctionFilter> selectedFunctionsList =
          ((CaldataAnalyzerFunctionFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getFunctionTabViewer()
              .getStructuredSelection().toList();
      for (FunctionFilter function : selectedFunctionsList) {
        functions.remove(function);
      }
      ((CaldataAnalyzerFunctionFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getFunctionTabViewer()
          .setInput(functions);
      ((CaldataAnalyzerFunctionFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getFunctionTabViewer()
          .refresh();
    }

    if (CaldataAnalyzerFilterActionSet.this.page instanceof CaldataAnalyzerSysConFilterWizardPage) {
      List<SystemConstantFilter> syscons =
          ((CaldataAnalyzerSysConFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getSystemConstants();
      List<SystemConstantFilter> selectedsysConList =
          ((CaldataAnalyzerSysConFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page)
              .getSystemConstantTabViewer().getStructuredSelection().toList();
      for (SystemConstantFilter syscon : selectedsysConList) {
        syscons.remove(syscon);
      }
      ((CaldataAnalyzerSysConFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getSystemConstantTabViewer()
          .setInput(syscons);
      ((CaldataAnalyzerSysConFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getSystemConstantTabViewer()
          .refresh();
    }

    if (CaldataAnalyzerFilterActionSet.this.page instanceof CaldataAnalyzerCustFilterWizardPage) {
      List<CustomerFilter> customerList =
          ((CaldataAnalyzerCustFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getCustomerFilterModel()
              .getCustomerFilterList();
      List<CustomerFilter> selectedCustomerList =
          ((CaldataAnalyzerCustFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getGridTabViewer()
              .getStructuredSelection().toList();
      for (CustomerFilter customer : selectedCustomerList) {
        customerList.remove(customer);
      }
      ((CaldataAnalyzerCustFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getGridTabViewer()
          .setInput(customerList);
      ((CaldataAnalyzerCustFilterWizardPage) CaldataAnalyzerFilterActionSet.this.page).getGridTabViewer().refresh();
    }

    if (CaldataAnalyzerFilterActionSet.this.page instanceof CaldataAnalyzerECUPlatWizardPage) {
      List<PlatformFilter> platformList = ((CaldataAnalyzerECUPlatWizardPage) CaldataAnalyzerFilterActionSet.this.page)
          .getPlatformFilterModel().getPlatformFilterList();
      List<PlatformFilter> selectedPlatformList =
          ((CaldataAnalyzerECUPlatWizardPage) CaldataAnalyzerFilterActionSet.this.page).getGridTabViewer()
              .getStructuredSelection().toList();
      for (PlatformFilter customer : selectedPlatformList) {
        platformList.remove(customer);
      }
      ((CaldataAnalyzerECUPlatWizardPage) CaldataAnalyzerFilterActionSet.this.page).getGridTabViewer()
          .setInput(platformList);
      ((CaldataAnalyzerECUPlatWizardPage) CaldataAnalyzerFilterActionSet.this.page).getGridTabViewer().refresh();
    }
  }
}
