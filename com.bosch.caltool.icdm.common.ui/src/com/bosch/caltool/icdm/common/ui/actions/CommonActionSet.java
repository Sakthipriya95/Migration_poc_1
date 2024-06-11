/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.actions;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.AbstractTableViewer;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.calcomp.externallink.creation.LinkCreator;
import com.bosch.calcomp.externallink.exception.ExternalLinkException;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectObjectBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.apic.PIDCDetailsNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode.PIDC_TREE_NODE_TYPE;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewDetail;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.ss.CalDataType;
import com.bosch.caltool.icdm.client.bo.ss.SeriesStatisticsInfo;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseFavNodesMgr;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseGroupClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.common.bo.apic.AttributeCommon;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.dialogs.AddLinkDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.CalDataViewerDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.EditLinkDialog;
import com.bosch.caltool.icdm.common.ui.providers.ScratchPadDataFetcher;
import com.bosch.caltool.icdm.common.ui.sorters.ScratchPadSorter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ICDMClipboard;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.common.ui.views.ScratchPadViewPart;
import com.bosch.caltool.icdm.common.ui.views.data.LinkData;
import com.bosch.caltool.icdm.common.ui.wizards.CalDataFileImpWizardDialog;
import com.bosch.caltool.icdm.common.ui.wizards.CalDataFileImportWizard;
import com.bosch.caltool.icdm.common.ui.wizards.CalDataFileImportWizardData;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.FCBCUsage;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.IParamRuleResponse;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.RuleLinks;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.model.comphex.CompHexWithCDFParam;
import com.bosch.caltool.icdm.model.dataassessment.DaCompareHexParam;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.caltool.icdm.model.uc.UsecaseEditorModel;
import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;
import com.bosch.caltool.icdm.ws.rest.client.a2l.ParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRReviewResultServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.uc.UsecaseFavoriteServiceClient;
import com.bosch.rcputils.browser.BrowserUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.wbutils.WorkbenchUtils;


/**
 * @author DMO5COB
 */
public class CommonActionSet {

  /**
   * Constant string to send link to usecase in pidc version
   */
  private static final String SEND_LINK_TO_USECASE_IN_PIDC_VERSION = "Send Link to Usecase in  PIDC Version";

  /**
   *
   */
  private static final String CLASS = "class";

  /**
   * Constant string for Add to ScratchPad
   */
  private static final String ADD_TO_SCRATCH_PAD = "Add to ScratchPad";

  /**
   * Collapse all action
   */
  public static final int COLLAPSE_ALL = 0;

  private Object obj;

  private Action moveAction;

  private static final String DELIMIT_STR = "-|\\,|\\n|\\t|\\;|\\s";

  /**
   * regex for yyyy.mm date values
   */
  private static final String ATTR_DATE_PATTERN_1 = "\\w{4}.\\w{2}";

  /**
   * regex for yyyy.mm.dd date values
   */
  private static final String ATTR_DATE_PATTERN_2 = "\\w{4}.\\w{2}.\\w{2}";

  /**
   * @param menuMgr instance
   * @param addedObj instance
   */
  public void setAddToScrachPadAction(final IMenuManager menuMgr, final Object addedObj) {

    final ScratchPadViewPart scratchViewPartNew = (ScratchPadViewPart) PlatformUI.getWorkbench()
        .getActiveWorkbenchWindow().getActivePage().findView(ScratchPadViewPart.PART_ID);

    setInstanceObject(addedObj);
    this.moveAction = new Action() {

      @Override
      public void run() {

        if ((null != addedObj) && (null != scratchViewPartNew)) {
          ScratchPadDataFetcher data = new ScratchPadDataFetcher();

          boolean checkAdd = checkIfAlreadyAdded(CommonActionSet.this.obj, scratchViewPartNew);

          setValuesToSCratchPadDataFetcherIfPIDCTreeNode(addedObj, data, checkAdd);
          setValuesToSCratchPadDataFetcherIfFunction(addedObj, data, checkAdd);
          // Icdm-633
          if (!checkAdd && (addedObj instanceof A2LParameter)) {
            A2LParameter a2lParameter = (A2LParameter) CommonActionSet.this.obj;
            if ((a2lParameter.getCalData() == null) || (a2lParameter.getCalData().getCalDataPhy() == null)) {
              return;
            }
            setSeriesStatsInfo(scratchViewPartNew, data, a2lParameter);
          }
          // Check for instance of A2lParamInfo not required since this is applicable only in case of adding to
          // scratchPad via drag and drop
          // During drag A2lParameter is converted to A2lParamInfo to satisfy drag and drop to "export to CDF" wizard
          // ICDM-226
          setValuesToSCratchPadDataFetcherIfSeriesStatsInfo(addedObj, scratchViewPartNew, data, checkAdd);
          setVariantandVerDetailstoScratchPadDataFetcher(addedObj, data, checkAdd);
          scratchViewPartNew.getNodeList().add(data);
          Collections.sort(scratchViewPartNew.getNodeList(), new ScratchPadSorter());
          scratchViewPartNew.getTableViewer().setInput(scratchViewPartNew.getNodeList());
          scratchViewPartNew.getTableViewer().setSelection(new StructuredSelection(data), true);
        }
      }


    };
    // Icdm-697 to disable move to scratch pad when more than one element is selected
    this.moveAction.setText(ADD_TO_SCRATCH_PAD);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.SCRATCH_PAD_16X16);
    this.moveAction.setImageDescriptor(imageDesc);
    // ICDM-226
    if (addedObj instanceof SeriesStatisticsInfo) {
      final boolean checkAdd = checkIfCalDataPhyValAlreadyAdded(this.obj, scratchViewPartNew);

      this.moveAction.setEnabled(!checkAdd);

    }
    else {
      final boolean checkAdd = checkIfAlreadyAdded(this.obj, scratchViewPartNew);

      this.moveAction.setEnabled(!checkAdd);

    }
    menuMgr.add(this.moveAction);

  }


  /**
   * @param menuMgr menuMgr of the Scratch pad.
   * @param statInfoList list of Objects series Stat info
   */
  public void openMutlipleFromSeriesStat(final IMenuManager menuMgr, final List<SeriesStatisticsInfo> statInfoList) {
    final ScratchPadViewPart scratchViewPartNew = (ScratchPadViewPart) PlatformUI.getWorkbench()
        .getActiveWorkbenchWindow().getActivePage().findView(ScratchPadViewPart.PART_ID);

    // Icdm-1381 Add more than value to Scratch pad from series stat
    Action moveMultipleAction = new Action() {

      @Override
      public void run() {

        for (SeriesStatisticsInfo addedObj : statInfoList) {
          ScratchPadDataFetcher data = new ScratchPadDataFetcher();
          setInstanceObject(addedObj);
          data.setSeriesStatsInfo((SeriesStatisticsInfo) CommonActionSet.this.obj);

          removeDuplicates(scratchViewPartNew, addedObj);

          scratchViewPartNew.getNodeList().add(data);

          Collections.sort(scratchViewPartNew.getNodeList(), new ScratchPadSorter());
          scratchViewPartNew.getTableViewer().setInput(scratchViewPartNew.getNodeList());
          scratchViewPartNew.getTableViewer().setSelection(new StructuredSelection(data), true);
        }

      }
    };
    // Icdm-697 to disable move to scratch pad when more than one element is selected
    moveMultipleAction.setText(ADD_TO_SCRATCH_PAD);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.SCRATCH_PAD_16X16);
    moveMultipleAction.setImageDescriptor(imageDesc);
    moveMultipleAction.setEnabled(!statInfoList.isEmpty());
    menuMgr.add(moveMultipleAction);

  }

  /**
   * @param menuMgr instance
   * @param structuredSelection instance
   * @param a2lName a2lName
   */
  public void setAddMultipleParamsToScratchPadAction(final IMenuManager menuMgr,
      final IStructuredSelection structuredSelection, final String a2lName) {

    final ScratchPadViewPart scratchViewPart = (ScratchPadViewPart) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
        .getActivePage().findView(ScratchPadViewPart.PART_ID);

    Action moveMultipleAction = new Action() {

      @Override
      public void run() {
        StringBuilder invalidParams = new StringBuilder();
        Map<String, ScratchPadDataFetcher> mapObj = new ConcurrentHashMap<>();
        if (null != scratchViewPart) {
          setInputToScratchPartTableViewer(structuredSelection, a2lName, scratchViewPart, invalidParams, mapObj);
        }
        checkIfValuePresent(invalidParams);
      }


    };

    // Icdm-697 to disable move to scratch pad when more than one element is selected
    moveMultipleAction.setText(ADD_TO_SCRATCH_PAD);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.SCRATCH_PAD_16X16);
    moveMultipleAction.setImageDescriptor(imageDesc);
    moveMultipleAction.setEnabled(!structuredSelection.isEmpty());
    menuMgr.add(moveMultipleAction);

  }

  /**
   * @param addedObj
   * @param data
   * @param checkAdd
   */
  private void setVariantandVerDetailstoScratchPadDataFetcher(final Object addedObj, final ScratchPadDataFetcher data,
      final boolean checkAdd) {
    if (!checkAdd && (addedObj instanceof PidcVersion)) {
      data.setPidcVersion((PidcVersion) CommonActionSet.this.obj);
    }
    else if (!checkAdd && (addedObj instanceof PidcVariant)) {
      data.setPidcVariant((PidcVariant) CommonActionSet.this.obj);
    }
    else if (!checkAdd && (addedObj instanceof PidcSubVariant)) {
      data.setPidcSubVariant((PidcSubVariant) CommonActionSet.this.obj);
    }
    else if (!checkAdd && (addedObj instanceof AttributeValue)) {
      data.setAttrVal((AttributeValue) CommonActionSet.this.obj);
    }
  }

  /**
   * @param addedObj
   * @param scratchViewPartNew
   * @param data
   * @param checkAdd
   */
  private void setValuesToSCratchPadDataFetcherIfSeriesStatsInfo(final Object addedObj,
      final ScratchPadViewPart scratchViewPartNew, final ScratchPadDataFetcher data, final boolean checkAdd) {
    if (!checkAdd && (addedObj instanceof SeriesStatisticsInfo)) {
      data.setSeriesStatsInfo((SeriesStatisticsInfo) CommonActionSet.this.obj);

      removeDuplicates(scratchViewPartNew, (SeriesStatisticsInfo) addedObj);

    }
  }

  /**
   * @param addedObj
   * @param data
   * @param checkAdd
   */
  private void setValuesToSCratchPadDataFetcherIfFunction(final Object addedObj, final ScratchPadDataFetcher data,
      final boolean checkAdd) {
    if (!checkAdd && (addedObj instanceof com.bosch.calmodel.a2ldata.module.calibration.group.Function)) {
      data.setFunction((com.bosch.calmodel.a2ldata.module.calibration.group.Function) CommonActionSet.this.obj);
    }
  }

  /**
   * @param addedObj
   * @param data
   * @param checkAdd
   */
  private void setValuesToSCratchPadDataFetcherIfPIDCTreeNode(final Object addedObj, final ScratchPadDataFetcher data,
      final boolean checkAdd) {
    if (!checkAdd && (addedObj instanceof PidcTreeNode)) {
      PidcTreeNode pidctreeNodeElement = ((PidcTreeNode) addedObj);

      PIDC_TREE_NODE_TYPE nodeType = pidctreeNodeElement.getNodeType();

      switch (nodeType) {

        case PIDC_A2L:
          data.setPidcA2l(pidctreeNodeElement.getPidcA2l());
          break;

        case ACTIVE_PIDC_VERSION:
        case OTHER_PIDC_VERSION:
          data.setPidcVersion(pidctreeNodeElement.getPidcVersion());
          break;

        default:

      }
    }
  }


  private boolean checkValues(final ScratchPadDataFetcher nodeCheck, final A2LParameter a2lParameter) {
    boolean flag = false;

    if ((nodeCheck.getSeriesStatsInfo().getCalData().getCalDataPhy() != null) &&
        nodeCheck.getSeriesStatsInfo().getCalData().getCalDataPhy().getName()
            .equals(a2lParameter.getCalData().getCalDataPhy().getName()) &&
        (nodeCheck.getSeriesStatsInfo().getCalData().getCalDataPhy()
            .equals(a2lParameter.getCalData().getCalDataPhy()))) {
      flag = true;
    }

    return flag;
  }

  /**
   * @param scratchViewPart
   * @param data
   * @param a2lParameter
   */
  private void setSeriesStatsInfo(final ScratchPadViewPart scratchViewPart, final ScratchPadDataFetcher data,
      final A2LParameter a2lParameter) {
    for (ScratchPadDataFetcher nodeCheck : scratchViewPart.getNodeList()) {
      if ((nodeCheck.getSeriesStatsInfo() != null) && (nodeCheck.getSeriesStatsInfo().getCalData() != null) &&
          checkValues(nodeCheck, a2lParameter)) {
        scratchViewPart.getNodeList().remove(nodeCheck);
        break;
      }
    }
    final CalData calDataObj = a2lParameter.getCalData();
    final SeriesStatisticsInfo seriesStatisticsInfo = new SeriesStatisticsInfo(calDataObj, CalDataType.VALUE);
    data.setSeriesStatsInfo(seriesStatisticsInfo);
  }

  /**
   * @param structuredSelection
   * @param a2lName
   * @param scratchViewPart
   * @param invalidParams
   * @param mapObj
   */
  private void setInputToScratchPartTableViewer(final IStructuredSelection structuredSelection, final String a2lName,
      final ScratchPadViewPart scratchViewPart, final StringBuilder invalidParams,
      final Map<String, ScratchPadDataFetcher> mapObj) {
    for (final ScratchPadDataFetcher nodeCheck : scratchViewPart.getNodeList()) {
      if ((nodeCheck.getSeriesStatsInfo() != null) && (nodeCheck.getSeriesStatsInfo().getCalData() != null) &&
          (nodeCheck.getSeriesStatsInfo().getCalData().getCalDataPhy() != null)) {
        mapObj.put(nodeCheck.getSeriesStatsInfo().getCalData().getCalDataPhy().getName(), nodeCheck);
      }
    }

    for (Object selectedElement : structuredSelection.toList()) {
      if (selectedElement instanceof A2LParameter) {
        ScratchPadDataFetcher data = new ScratchPadDataFetcher();
        A2LParameter a2lParameter = (A2LParameter) selectedElement;
        if ((a2lParameter.getCalData() == null) || (a2lParameter.getCalData().getCalDataPhy() == null)) {
          invalidParams.append(a2lParameter.getName() + ", ");
          continue;
        }
        ScratchPadDataFetcher scratchDP = mapObj.get(a2lParameter.getCalData().getCalDataPhy().getName());
        if ((scratchDP != null) && (scratchDP.getSeriesStatsInfo().getCalData().equals(a2lParameter.getCalData()))) {
          scratchViewPart.getNodeList().remove(scratchDP);
        }
        final CalData calDataObj = a2lParameter.getCalData();

        final SeriesStatisticsInfo seriesStatisticsInfo = new SeriesStatisticsInfo(calDataObj, CalDataType.DSTVALUE);
        seriesStatisticsInfo.setDataSetName(a2lName);
        data.setSeriesStatsInfo(seriesStatisticsInfo);
        scratchViewPart.getNodeList().add(data);

      }
    }
    scratchViewPart.getTableViewer().setInput(scratchViewPart.getNodeList());
  }

  /**
   * @param invalidParams
   */
  private void checkIfValuePresent(final StringBuilder invalidParams) {
    if (invalidParams.length() > 0) {
      String message = invalidParams.substring(0, invalidParams.length() - 2);
      MessageDialogUtils.getInfoMessageDialog("Info", message + " not added to ScratchPad since no value is present");
    }
  }

  /**
   * @return the Move to Scratch Pad Action // Icdm-697 to disable move to scratch pad when more than one element is
   *         selected
   */
  public Action getMoveToScratchAction() {
    return this.moveAction;
  }

  /**
   * @param addedObj the addedObj to set
   */
  public void setInstanceObject(final Object addedObj) {
    // ICDM-226 ICDM-254
    if ((addedObj instanceof PidcTreeNode) || (addedObj instanceof PidcVersion) || (addedObj instanceof PidcVariant) ||
        (addedObj instanceof PidcSubVariant) ||
        (addedObj instanceof com.bosch.calmodel.a2ldata.module.calibration.group.Function) ||
        (addedObj instanceof Characteristic) || (addedObj instanceof A2LParameter) ||
        (addedObj instanceof SeriesStatisticsInfo) || (addedObj instanceof AttributeValue)) {
      this.obj = addedObj;
    }
  }


  /**
   * @return Object
   */
  public Object getInstanceObject() {
    return this.obj;
  }

  /**
   * @param addedObj instance
   * @param scratchViewPartNew ScratchPadViewPart instance
   * @return true, if node is already added to ScratchPad
   */
  public boolean checkIfAlreadyAdded(final Object addedObj, final ScratchPadViewPart scratchViewPartNew) {

    boolean checkAdd = false;

    if (CommonUtils.isNotNull(scratchViewPartNew) && CommonUtils.isNotEmpty(scratchViewPartNew.getNodeList())) {
      for (final ScratchPadDataFetcher nodeCheck : scratchViewPartNew.getNodeList()) {
        // check if node is already to to scratchpad
        if (isFuncOrAttrValueNodeAlreadyAdded(addedObj, nodeCheck) ||
            isPidcLevelNodeAlreadyAdded(addedObj, nodeCheck) || isTreeNodeAdded(addedObj, nodeCheck)) {
          checkAdd = true;
          break;
        }

      }
    }
    return checkAdd;
  }


  /**
   * @param addedObj
   * @param nodeCheck
   * @return true, if Function or AttributeValue is already added to ScratchPad
   */
  private boolean isFuncOrAttrValueNodeAlreadyAdded(final Object addedObj, final ScratchPadDataFetcher nodeCheck) {
    boolean isAlreadyAdded = false;
    if (addedObj instanceof Function) {
      if ((nodeCheck.getFunction() != null) &&
          CommonUtils.isEqual(((Function) addedObj).getName(), nodeCheck.getFunction().getName())) {
        isAlreadyAdded = true;
      }
    }
    else if ((addedObj instanceof AttributeValue) && (nodeCheck.getAttrVal() != null) &&
        (CommonUtils.isEqual(((AttributeValue) addedObj).getId(), nodeCheck.getAttrVal().getId()))) {
      isAlreadyAdded = true;
    }
    return isAlreadyAdded;

  }


  /**
   * @param addedObj
   * @param nodeCheck
   * @return true, if PidcTreeNode or PidcDetailsNode of Variant type is already added to scratchpad
   */
  private boolean isTreeNodeAdded(final Object addedObj, final ScratchPadDataFetcher nodeCheck) {
    boolean isAlreadyAdded = false;
    if (addedObj instanceof PidcTreeNode) {
      if (checkPidcTreeNodeAlreadyAdded(addedObj, nodeCheck)) {
        isAlreadyAdded = true;
      }
    }
    else if ((addedObj instanceof PIDCDetailsNode)) {
      PIDCDetailsNode pidcDetNode = (PIDCDetailsNode) addedObj;
      if (pidcDetNode.isVariantNode() && (nodeCheck.getPidcVariant() != null) &&
          CommonUtils.isEqual(pidcDetNode.getPidcVariant().getId(), nodeCheck.getPidcVariant().getId())) {
        isAlreadyAdded = true;
      }
    }
    return isAlreadyAdded;
  }


  /**
   * @param addedObj
   * @param nodeCheck
   * @return true, if PidcVersion or PidcVariant or PidcSubVariant is added to scratch pad already
   */
  private boolean isPidcLevelNodeAlreadyAdded(final Object addedObj, final ScratchPadDataFetcher nodeCheck) {
    boolean isAlreadyAdded = false;
    if (addedObj instanceof PidcVersion) {
      if ((nodeCheck.getPidcVersion() != null) &&
          CommonUtils.isEqual(((PidcVersion) addedObj).getId(), nodeCheck.getPidcVersion().getId())) {
        isAlreadyAdded = true;
      }
    }
    else if (addedObj instanceof PidcVariant) {
      if ((nodeCheck.getPidcVariant() != null) &&
          CommonUtils.isEqual(((PidcVariant) addedObj).getId(), nodeCheck.getPidcVariant().getId())) {
        isAlreadyAdded = true;
      }
    }
    else if ((addedObj instanceof PidcSubVariant) && (nodeCheck.getPidcSubVariant() != null) &&
        CommonUtils.isEqual(((PidcSubVariant) addedObj).getId(), nodeCheck.getPidcSubVariant().getId())) {
      isAlreadyAdded = true;

    }
    return isAlreadyAdded;
  }


  /**
   * @param addedObj
   * @param nodeCheck
   * @return true, if PIDCTreeNide is already added to Scratch Pad
   */
  private boolean checkPidcTreeNodeAlreadyAdded(final Object addedObj, final ScratchPadDataFetcher nodeCheck) {

    PidcTreeNode pidctreeNodeElement = ((PidcTreeNode) addedObj);
    PIDC_TREE_NODE_TYPE nodeType = pidctreeNodeElement.getNodeType();
    boolean checkAdd = false;

    switch (nodeType) {

      case PIDC_A2L:
        if ((nodeCheck.getPidcA2l() != null) &&
            pidctreeNodeElement.getPidcA2l().getName().equals(nodeCheck.getPidcA2l().getName())) {
          checkAdd = true;
        }
        break;

      case ACTIVE_PIDC_VERSION:
      case OTHER_PIDC_VERSION:
        if ((nodeCheck.getPidcVersion() != null) &&
            CommonUtils.isEqual(pidctreeNodeElement.getPidcVersion().getId(), nodeCheck.getPidcVersion().getId())) {
          checkAdd = true;
        }
        break;

      default:
        break;
    }
    return checkAdd;
  }


  /**
   * This method checks for CalDataPhy value already added or not
   *
   * @param addedObj defines added object to scratcpad
   * @param scratchViewPartNew instance
   * @return boolean
   */
  // ICDM-226
  public boolean checkIfCalDataPhyValAlreadyAdded(final Object addedObj, final ScratchPadViewPart scratchViewPartNew) {

    boolean checkAdd = false;

    if (CommonUtils.isNotNull(scratchViewPartNew) && CommonUtils.isNotEmpty(scratchViewPartNew.getNodeList())) {
      for (final ScratchPadDataFetcher nodeCheck : scratchViewPartNew.getNodeList()) {
        // ICDM-226
        if ((addedObj instanceof SeriesStatisticsInfo) && (nodeCheck.getSeriesStatsInfo() != null) &&
            ((SeriesStatisticsInfo) addedObj).getCalData().getCalDataPhy()
                .equals(nodeCheck.getSeriesStatsInfo().getCalData().getCalDataPhy())) {
          checkAdd = true;
          break;
        }
      }
    }

    return checkAdd;
  }

  /**
   * @return ArrayList<IDoubleClickListener>
   */
  public List<IDoubleClickListener> getPidcTreeListeners() {

    IConfigurationElement[] configs = getConfigurationElements();

    ArrayList<IDoubleClickListener> retList = new ArrayList<>();
    for (IConfigurationElement config : configs) {
      try {
        IPIDCTreeViewActionSet extension = (IPIDCTreeViewActionSet) config.createExecutableExtension(CLASS);
        retList.add(extension.getDoubleClickListener());

      }
      catch (Exception exp) {
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
    }
    return retList;
  }

  /**
   * @param manager MenuManager
   * @param viewer TreeViewer
   * @param pidcTreeNode pidcTreeNode
   */
  public void getPidcTreeNodeRightClickActions(final IMenuManager manager, final TreeViewer viewer,
      final PidcTreeNode pidcTreeNode) {
    IConfigurationElement[] configs = getConfigurationElements();

    for (IConfigurationElement config : configs) {
      try {
        IPIDCTreeViewActionSet extension = (IPIDCTreeViewActionSet) config.createExecutableExtension(CLASS);
        extension.onRightClickOfPidcTreeNode(manager, viewer, pidcTreeNode);

      }
      catch (Exception exp) {
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }


  /**
   * @return
   */
  private IConfigurationElement[] getConfigurationElements() {
    return Platform.getExtensionRegistry()
        .getConfigurationElementsFor("com.bosch.caltool.icdm.common.ui.PIDCTreeViewExtnPt");
  }

  /**
   * @param selectedObject Selected Object
   * @param keyCode Key Code
   * @param stateMask State Mask
   * @param viewer Tree Viewer
   */
  public void getKeyListenerToViewer(final Object selectedObject, final int keyCode, final int stateMask,
      final TreeViewer viewer) {

    IConfigurationElement[] configs = getConfigurationElements();

    for (IConfigurationElement config : configs) {
      try {
        IPIDCTreeViewActionSet extension = (IPIDCTreeViewActionSet) config.createExecutableExtension(CLASS);
        extension.getKeyListenerToViewer(selectedObject, keyCode, stateMask, viewer);

      }
      catch (Exception exp) {
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }


  /**
   * @param pidCards pidCards
   */
  public void onRightClickOfAPRJnew(final Set<PidcVersion> pidCards) {
    IConfigurationElement[] configs = getConfigurationElements();

    for (IConfigurationElement config : configs) {
      try {
        IPIDCTreeViewActionSet extension = (IPIDCTreeViewActionSet) config.createExecutableExtension(CLASS);
        extension.onRightClickOfAPRJnew(pidCards);
      }
      catch (Exception exp) {
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * @param result CDR Review Result
   * @param paramName parameter to select by default
   * @param variantId Variant Id
   */
  public void openReviewResultEditor(final CDRReviewResult result, final String paramName, final Long variantId) {
    IConfigurationElement[] configs = getConfigurationElements();

    for (IConfigurationElement config : configs) {
      try {
        IPIDCTreeViewActionSet extension = (IPIDCTreeViewActionSet) config.createExecutableExtension(CLASS);
        extension.openReviewResult(result, paramName, variantId);

      }
      catch (CoreException exp) {
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * @param rvwVariant rvwVariant
   * @param paramName parameter to select by default
   */
  public void openReviewResultEditor(final RvwVariant rvwVariant, final String paramName) {
    IConfigurationElement[] configs = getConfigurationElements();

    for (IConfigurationElement config : configs) {
      try {
        IPIDCTreeViewActionSet extension = (IPIDCTreeViewActionSet) config.createExecutableExtension(CLASS);
        extension.openReviewResult(rvwVariant, paramName);

      }
      catch (CoreException exp) {
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * Opens review rules editor
   *
   * @param menuManagr menuManager
   * @param selectedObj selected object
   */
  public void openRulesEditor(final IMenuManager menuManagr, final Object selectedObj) {
    IConfigurationElement[] configs = getConfigurationElements();

    for (IConfigurationElement config : configs) {
      try {
        IPIDCTreeViewActionSet extension = (IPIDCTreeViewActionSet) config.createExecutableExtension(CLASS);
        extension.createRuleAction(menuManagr, selectedObj);

      }
      catch (CoreException exp) {
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }


  /**
   * Opens review result
   *
   * @param funcList List of Function
   * @param paramName param Name
   */
  public void openFunctionSelectionDialog(final List<Function> funcList, final String paramName) {
    IConfigurationElement[] configs = getConfigurationElements();

    for (IConfigurationElement config : configs) {
      try {
        IPIDCTreeViewActionSet extension = (IPIDCTreeViewActionSet) config.createExecutableExtension(CLASS);
        extension.openFunctionSelectionDialog(funcList, paramName);

      }
      catch (CoreException exp) {
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * Icdm-1349 New method for opening A2L file. Opens the A2l File.
   *
   * @param pidcA2lId pidcA2lFile Id
   */
  public void openA2lFile(final Long pidcA2lId) {
    IConfigurationElement[] configs = getConfigurationElements();

    for (IConfigurationElement config : configs) {
      try {
        IPIDCTreeViewActionSet extension = (IPIDCTreeViewActionSet) config.createExecutableExtension(CLASS);
        extension.openA2LFile(pidcA2lId);
      }
      catch (CoreException exp) {
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * ICDM-254
   *
   * @param manager manager
   * @param copyName copyName
   * @param viewer viewer
   * @param isCopyEnabled true, is copy action enabled
   */
  public void setCopyAction(final IMenuManager manager, final String copyName, final AbstractTableViewer viewer,
      final boolean isCopyEnabled) {

    final IStructuredSelection iStructSelection = (IStructuredSelection) viewer.getSelection();
    final List<AttributeValue> valList = new ArrayList<>();
    final Action copyAction = new Action() {

      @Override
      public void run() {
        copyAttrValAndParamName(iStructSelection, valList);
      }
    };
    setActionToMenuManager(manager, copyName, copyAction, ImageKeys.COPY_16X16, valList, true, isCopyEnabled);
  }

  /**
   * @param iStructSelection
   * @param valList
   */
  private void copyAttrValAndParamName(final IStructuredSelection iStructSelection,
      final List<AttributeValue> valList) {

    @SuppressWarnings("unchecked")
    List<Object> selList = iStructSelection.toList();
    ScratchPadDataFetcher scratchPadDataProvider;
    AttributeValue attrVal;
    for (Object object : selList) {
      // Copied from ScratchPad
      if (object instanceof ScratchPadDataFetcher) {
        scratchPadDataProvider = (ScratchPadDataFetcher) object;
        if (scratchPadDataProvider.getAttrVal() != null) {
          valList.add(scratchPadDataProvider.getAttrVal());
        }
        if (scratchPadDataProvider.getSeriesStatsInfo() != null) {
          // ICDM-1348
          CommonUiUtils
              .setTextContentsToClipboard(scratchPadDataProvider.getSeriesStatsInfo().getCalData().getShortName());
        }
      } // Copied from Value Table
      else if (object instanceof AttributeValue) {
        attrVal = (AttributeValue) object;
        valList.add(attrVal);
      }
    }
    ICDMClipboard.getInstance().setCopiedObject(valList);
  }

  /**
   * ICDM-254
   *
   * @param manager manager
   * @param selObject selObject
   * @param pasteName pasteName
   * @param viewer viewer
   */
  public void setPasteAction(final IMenuManager manager, final Object selObject, final String pasteName,
      final AbstractTableViewer viewer) {
    // add paste action
    final Action pastePIDCAction = new Action() {

      @Override
      public void run() {
        // Get copied object
        final Object copiedObject = ICDMClipboard.getInstance().getCopiedObject();
        // Attribute Value paste
        pasteAttrValue(selObject, viewer, copiedObject);
      }
    };
    setActionToMenuManager(manager, pasteName, pastePIDCAction, ImageKeys.PASTE_16X16, selObject, false, false);
  }

  /**
   * ICDM-254
   *
   * @param attrValues set of attr values
   * @param attrVal - AttributeValue to be pasted
   * @return true if the value is already present
   */
  public static boolean checkIfValueAlreadyAdded(final SortedSet<AttributeValue> attrValues,
      final AttributeValue attrVal) {
    boolean isAdded = false;
    for (AttributeValue value : attrValues) {
      if (value.getName().equalsIgnoreCase(attrVal.getName())) {
        isAdded = true;
        break;
      }
    }
    return isAdded;
  }

  /**
   * ICDM-254 Sets the attribute value information to the CmdModAttributeValue commad
   *
   * @param attrValue
   * @param attrValueGer
   * @param descGer
   * @param descEng
   * @param attrValToCreate
   * @param attr
   * @param attrVal
   */
  public static void setAttrValFields(final String attrValue, final String attrValueGer, final String descGer,
      final String descEng, final AttributeValue attrValToCreate, final Attribute attr, final AttributeValue attrVal) {

    String valType;
    String value = null;
    if ((attr != null) && (attr.getValueType() != null) && (attrValue != null)) {
      valType = attr.getValueType();
      if (AttributeValueType.ICDM_USER.toString().equals(valType)) {
        attrValToCreate.setUserId(attrVal.getUserId());
      }
      else {
        if (valType.equals(AttributeValueType.BOOLEAN.toString())) {
          setValueForBooleanType(attrValue, attrValToCreate);
        }
        else if ((valType.equals(AttributeValueType.NUMBER.toString())) && attrValue.contains(",")) {
          value = attrValue.replace(",", ".").trim();
        }
        else if (valType.equals(AttributeValueType.TEXT.toString())) {
          value = setAttrValueForTextTypeValue(attrValue, attr, attrVal);
        }
        else {
          value = attrValue;
        }

        setValueForTextHyperLinkDateNumberTypes(attrValue, attrValueGer, attrValToCreate, attr, valType, value);


        attrValToCreate.setDescriptionEng(descEng);
        attrValToCreate.setDescriptionGer(descGer);
      }
    }
  }


  private static void setValueForTextHyperLinkDateNumberTypes(final String attrValue, final String attrValueGer,
      final AttributeValue attrValToCreate, final Attribute attr, final String valType, final String value) {
    if ((valType.equals(AttributeValueType.TEXT.toString())) ||
        (valType.equals(AttributeValueType.HYPERLINK.toString()))) {
      setAttrValueForTextAndHyperLinkType(attrValueGer, attrValToCreate, value);
    }
    if (valType.equals(AttributeValueType.DATE.toString())) {
      setAttrValueForDateType(attrValue, attrValToCreate, attr);
    }
    if (valType.equals(AttributeValueType.NUMBER.toString())) {
      attrValToCreate.setNumValue(new BigDecimal(value));
    }
  }


  private static void setAttrValueForDateType(final String attrValue, final AttributeValue attrValToCreate,
      final Attribute attr) {
    try {
      attrValToCreate
          .setDateValue(AttributeCommon.convertAttrDateStringToDefaultDateFormat(attr.getFormat(), attrValue));
    }
    catch (ParseException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }


  /**
   * @param attrValueGer
   * @param attrValToCreate
   * @param value
   */
  private static void setAttrValueForTextAndHyperLinkType(final String attrValueGer,
      final AttributeValue attrValToCreate, final String value) {
    attrValToCreate.setTextValueEng(value);
    if ((attrValueGer != null) && !attrValueGer.trim().isEmpty()) {
      attrValToCreate.setTextValueGer(attrValueGer);
    }
    else {
      attrValToCreate.setTextValueGer("");
    }
  }


  /**
   * @param attrValue
   * @param attr
   * @param attrVal
   * @return
   */
  private static String setAttrValueForTextTypeValue(final String attrValue, final Attribute attr,
      final AttributeValue attrVal) {
    String value;
    SortedSet<AttributeValue> attrValues = new AttributeClientBO(attr).getAttrValues();
    if (attrValues.contains(attrVal) || checkIfValueAlreadyAdded(attrValues, attrVal)) {
      value = ApicConstants.COPY_OF + attrValue;
    }
    else {
      value = attrValue;
    }
    return value;
  }


  /**
   * @param attrValue
   * @param attrValToCreate
   */
  private static void setValueForBooleanType(final String attrValue, final AttributeValue attrValToCreate) {
    if (attrValue.equalsIgnoreCase(ApicConstants.BOOLEAN_TRUE_STRING) ||
        attrValue.equalsIgnoreCase(ApicConstants.BOOLEAN_TRUE_DB_STRING)) {
      attrValToCreate.setBoolvalue(ApicConstants.BOOLEAN_TRUE_DB_STRING);
    }
    else {
      attrValToCreate.setBoolvalue(ApicConstants.BOOLEAN_FALSE_DB_STRING);
    }
  }

  /**
   * ICDM-254
   *
   * @param selectedObj
   * @param tableViewer
   * @param copiedObject
   */
  private void pasteAttrValue(final Object selectedObj, final AbstractTableViewer tableViewer,
      final Object copiedObject) {

    if (((selectedObj instanceof ArrayList) && (((ArrayList<?>) selectedObj).size() == 1)) &&
        (copiedObject instanceof ArrayList)) {

      Attribute attr = (Attribute) ((ArrayList<?>) selectedObj).get(0);
      ArrayList<?> attrValList = (ArrayList<?>) copiedObject;

      for (Object object : attrValList) {
        if (object instanceof AttributeValue) {
          AttributeValue attrVal = setAttrValName(attr, object);
          // Checks whether the value type is compatible
          if (!attr.getValueType().equals(attrVal.getValueType())) {
            CDMLogger.getInstance().info("Value Type Mismatch !", Activator.PLUGIN_ID);
          }
          else {
            checkIfAttrValExists(tableViewer, attr, attrVal);
          }
        }
      }
    }
  }


  /**
   * @param tableViewer
   * @param attr
   * @param attrVal
   */
  private void checkIfAttrValExists(final AbstractTableViewer tableViewer, final Attribute attr,
      final AttributeValue attrVal) {
    SortedSet<AttributeValue> attrValues = new AttributeClientBO(attr).getAttrValues();

    if (!attrVal.getValueType().equals(AttributeValueType.TEXT.toString()) &&
        (attrValues.contains(attrVal) || checkIfValueAlreadyAdded(attrValues, attrVal))) {
      CDMLogger.getInstance().info("Value : " + attrVal.getName() + " already exists!!!", Activator.PLUGIN_ID);
    }
    // Paste the value (Insert operation)
    else {
      createAttrValue(tableViewer, attr, attrVal);
    }
  }


  /**
   * @param attr
   * @param object
   * @return
   */
  private AttributeValue setAttrValName(final Attribute attr, final Object object) {
    AttributeValue attrVal = (AttributeValue) object;
    // condition satisfies only if the date format and date value mismatches
    // happens when copy yyyy.mm date and paste to yyyy.mm.dd
    if (attrVal.getValueType().equals(AttributeValueType.DATE.toString()) &&
        !attr.getFormat().matches(ATTR_DATE_PATTERN_1) && attrVal.getName().matches(ATTR_DATE_PATTERN_1)) {
      attrVal.setName(attrVal.getName().concat(".01"));
    }
    else if (attrVal.getValueType().equals(AttributeValueType.DATE.toString()) &&
        !attr.getFormat().matches(ATTR_DATE_PATTERN_2) && attrVal.getName().matches(ATTR_DATE_PATTERN_2)) {
      attrVal.setName(attrVal.getName().substring(0, attrVal.getName().lastIndexOf('.')));
    }
    return attrVal;
  }


  private void createAttrValue(final AbstractTableViewer tableViewer, final Attribute attr,
      final AttributeValue attrVal) {
    AttributeValue attrValToCreate = new AttributeValue();
    attrValToCreate.setAttributeId(attr.getId());
    String value;
    if (attrVal.getValueType().equals(AttributeValueType.NUMBER.toString())) {
      value = String.valueOf(attrVal.getNumValue());
    }
    else if (attrVal.getValueType().equals(AttributeValueType.BOOLEAN.toString())) {
      value = attrVal.getBoolvalue();
    }
    else if (attrVal.getValueType().equals(AttributeValueType.ICDM_USER.toString())) {
      value = null;
    }
    else {
      value = attrVal.getName();
    }

    try {
      setAttrValFields(value, attrVal.getTextValueGer(), attrVal.getDescriptionGer(), attrVal.getDescriptionEng(),
          attrValToCreate, attr, attrVal);
      new AttributeValueServiceClient().create(attrValToCreate);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    tableViewer.setInput(new AttributeClientBO(attr).getAttrValues());
    tableViewer.refresh();
  }

  /**
   * ICDM-254 This method add the action to menu manager
   *
   * @param manager
   * @param copyName
   * @param action
   * @param imagePath
   * @param isCopyEnabled
   */
  private void setActionToMenuManager(final IMenuManager manager, final String copyName, final Action action,
      final ImageKeys imagePath, final Object selObject, final boolean isCopy, final boolean isCopyEnabled) {

    action.setText(copyName);

    // Get the image descriptor
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(imagePath);
    // Set the image for the action
    action.setImageDescriptor(imageDesc);

    action.setEnabled(((isCopy) && isCopyEnabled) || ICDMClipboard.getInstance().isPasteAllowed(selObject));

    manager.add(action);
  }


  /**
   * Add the common tool bar buttons related to the tree viewer in a view.
   *
   * @param treeViewer Tree viewer
   * @param tbMgr toolbar manager
   * @param expandLevel level to expand, in case of collapse all
   */
  public void addCommonTreeActions(final TreeViewer treeViewer, final IToolBarManager tbMgr, final int expandLevel) {
    Separator separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    tbMgr.add(separator);

    IAction collapseAllAction = new CollapseAllAction(treeViewer, expandLevel);
    tbMgr.add(collapseAllAction);

    separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    tbMgr.add(separator);

  }

  /**
   * to open PIDC from FC/BC usage
   *
   * @param menuMgr IMenuManager
   * @param addedObj Object
   */
  public void openPidcFromFCBC(final IMenuManager menuMgr, final Object addedObj) {
    final Action openPIDCFromFCBCAction = new Action() {

      @Override
      public void run() {

        if (addedObj instanceof FCBCUsage) {
          String selAPRJName = ((FCBCUsage) addedObj).getVcdmAprj();
          try {
            SortedSet<PidcVersion> pidCards = new PidcVersionServiceClient().getAprjPIDCs(selAPRJName);
            if (CommonUtils.isNotEmpty(pidCards)) {
              onRightClickOfAPRJnew(pidCards);
            }
            else {
              CDMLogger.getInstance().infoDialog("No Project-ID Cards are mapped to this APRJ", Activator.PLUGIN_ID);
            }

          }
          catch (ApicWebServiceException exp) {
            CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
          }
        }
      }
    };

    openPIDCFromFCBCAction.setText("Open PIDC");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.PIDC_16X16);
    openPIDCFromFCBCAction.setImageDescriptor(imageDesc);
    menuMgr.add(openPIDCFromFCBCAction);
  }

  /**
   * adds the context menu for links
   *
   * @param menuMgr IMenuManager
   * @param sortedSet SortedSet of links
   */
  public void addLinkAction(final IMenuManager menuMgr,
      final SortedSet<com.bosch.caltool.icdm.model.general.Link> sortedSet) {
    linkAction(menuMgr, sortedSet);
  }


  /**
   * @param menuMgr
   * @param sortedSet
   */
  private void linkAction(final IMenuManager menuMgr,
      final SortedSet<com.bosch.caltool.icdm.model.general.Link> sortedSet) {
    menuMgr.add(new Separator());
    if ((null != sortedSet) && !sortedSet.isEmpty()) {
      for (final Link link : sortedSet) {
        final Action linkAction = new Action() {

          @Override
          public void run() {
            openLinkFromString(link.getLinkUrl());
          }

        };

        linkAction.setText(link.getDescription());
        final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.HYPER_LINK_16X16);
        linkAction.setImageDescriptor(imageDesc);
        menuMgr.add(linkAction);
      }
    }
    menuMgr.add(new Separator());

  }

  /**
   * @param menuMgr
   * @param links
   */
  public void addValLinkAction(final MenuManager menuMgr, final SortedSet<Link> links) {
    linkAction(menuMgr, links);
  }

  /**
   * @param string String
   */
  public void openLinkFromString(final String string) {
    if ((null != string) && (string.trim().startsWith(CommonUIConstants.SHARED_PATH_PREFIX) ||
        string.trim().startsWith(CommonUIConstants.FILE_PATH_PREFIX))) {
      // for shared & file path
      openSharedPath(string);
    }
    else if (null != string) {
      // for browser links
      openExternalBrowser(string);
    }
  }

  /**
   * ICDM-1293 Import caldata files for component package
   *
   * @param manager menu mngr
   * @param importObj compPkg
   * @param paramColDataProvider ParamCollectionDataProvider
   * @param paramRulesOutput IParamRuleResponse
   */
  public void addImportCalDataAction(final IContributionManager manager, final ParamCollection importObj,
      final ParamCollectionDataProvider paramColDataProvider, final IParamRuleResponse<?, ?> paramRulesOutput) {
    final Action importAction = new Action() {

      @Override
      public void run() {
        if (paramColDataProvider.isModifiable(importObj)) {
          CalDataFileImportWizardData wizardData = new CalDataFileImportWizardData(importObj);
          wizardData.setParamColDataProvider(paramColDataProvider);
          wizardData.setParamRulesOutput(paramRulesOutput);
          CalDataFileImpWizardDialog importCalDataWizardDialog = new CalDataFileImpWizardDialog(
              Display.getDefault().getActiveShell(), new CalDataFileImportWizard(wizardData));
          importCalDataWizardDialog.create();
          importCalDataWizardDialog.open();

          refreshImportChanges();
        }
        else {
          CDMLogger.getInstance().infoDialog("User does not have sufficient access rights!", Activator.PLUGIN_ID);
        }
      }

      /**
      *
      */
      private void refreshImportChanges() {
        if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isEditorAreaVisible()) {

          IEditorReference[] editorReferences =
              PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
          for (IEditorReference iEditorReference : editorReferences) {
            IEditorPart editorPart = iEditorReference.getEditor(false);
            if (!(editorPart instanceof IImportRefresher)) {
              continue;
            }
            IImportRefresher editor = (IImportRefresher) editorPart;
            editor.doImportRefresh();
          }
        }
      }
    };

    importAction.setText("Import Calibration Data");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.DCM_UPLOAD_28X30);
    importAction.setImageDescriptor(imageDesc);
    manager.add(importAction);

  }

  /**
   * @param string String
   */
  public void openSharedPath(final String string) {
    try {
      if (CommonUtils.isValidLocalPathFormat(string)) {
        BrowserUtil.getInstance().openSharedPath(string);
      }
      else {
        MessageDialogUtils.getErrorMessageDialog("Invalid path:", "Not a valid path!");
      }
    }
    catch (Exception exp) {
      CDMLogger.getInstance().warnDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param string String
   */
  public void openExternalBrowser(final String string) {
    try {
      if (CommonUtils.isValidHyperlinkFormat(string)) {
        BrowserUtil.getInstance().openExternalBrowser(string);
      }
      else {
        MessageDialogUtils.getErrorMessageDialog("Invalid Url:", "Not a valid hyperlink!");
      }
    }
    catch (PartInitException | MalformedURLException exp) {
      CDMLogger.getInstance().warnDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param filePath . ICDM-893
   */
  public void openExcelFile(final String filePath) {
    try {

      Runtime.getRuntime().exec("cmd /c " + "\"" + filePath + "\"");
    }
    catch (IOException exp) {
      CDMLogger.getInstance().error(
          " Error occurred while opening the excel report file . Please check whether the file exists !", exp,
          Activator.PLUGIN_ID);
    }
  }

  /**
   * ICDM-931 Refreshes the tree viewers in the outline page
   *
   * @param deselectAll - true/false : whether or not to clear the selection on the tree while refreshing
   */
  public void refreshOutlinePages(final boolean deselectAll) {
    final OutlineViewPart viewPart = (OutlineViewPart) WorkbenchUtils.getView(OutlineViewPart.PART_ID);
    if (viewPart != null) {
      viewPart.refreshTreeViewer(deselectAll);

    }
  }

  /**
   * creates add new link icon in the toolbar and handles the action
   *
   * @param tabViewer GridTableViewer
   * @return Add Link Action
   */
  public Action addNewLinkAction(final GridTableViewer tabViewer) {
    // Create an action to add new link
    Action newLinkAction = new Action("Add Link", SWT.NONE) {

      @Override
      public void run() {
        final AddLinkDialog linkDialog = new AddLinkDialog(Display.getCurrent().getActiveShell(), tabViewer, true);
        linkDialog.open();
      }
    };
    // Set the image for add link action
    newLinkAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    newLinkAction.setEnabled(true);
    return newLinkAction;
  }

  /**
   * creates delete link icon in the toolbar and handles the action
   *
   * @param toolBarManager ToolBarManager
   * @param linksTabViewer GridTableViewer
   * @param editAction edit action
   * @return delete link action
   */
  public Action addDeleteLinkActionToSection(final ToolBarManager toolBarManager, final GridTableViewer linksTabViewer,
      final Action editAction) {
    // Create an action to delete the link
    Action deleteLinkAction = new Action("Delete Link", SWT.NONE) {

      @Override
      public void run() {

        IStructuredSelection selection = (IStructuredSelection) linksTabViewer.getSelection();

        // ICDM-1502
        LinkData linkData = (LinkData) selection.getFirstElement();

        SortedSet<LinkData> input = (SortedSet<LinkData>) linksTabViewer.getInput();

        linkData.setOprType(CommonUIConstants.CHAR_CONSTANT_FOR_DELETE);

        linksTabViewer.setInput(input);// to invoke input changed
        linksTabViewer.refresh();

        linksTabViewer.setSelection(null);

        // disable edit and delete buttons
        editAction.setEnabled(false);
        setEnabled(false);
      }

    };
    // Set the image for delete the user
    deleteLinkAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    deleteLinkAction.setEnabled(false);
    toolBarManager.add(deleteLinkAction);
    return deleteLinkAction;
  }

  /**
   * ICDM-452 creates edit link action in the toolbar and handles the action
   *
   * @param toolBarManager ToolBarManager
   * @param linksTabViewer GridTableViewer
   * @return Edit link action
   */
  public Action addEditLinkAction(final ToolBarManager toolBarManager, final GridTableViewer linksTabViewer) {
    // Create an action to add new link
    Action editLinkAction = new Action("Edit Link", SWT.NONE) {

      @Override
      public void run() {
        final IStructuredSelection selection = (IStructuredSelection) linksTabViewer.getSelection();
        final LinkData linkData = (LinkData) selection.getFirstElement();
        final EditLinkDialog linkDialog =
            new EditLinkDialog(Display.getCurrent().getActiveShell(), linkData, linksTabViewer, true);
        linkDialog.open();
      }
    };
    // Set the image for add link action
    editLinkAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));
    editLinkAction.setEnabled(false);
    toolBarManager.add(editLinkAction);
    return editLinkAction;
  }

  /**
   * ICDM-1031.1029
   *
   * @param manager IMenuManager
   * @param favUcNode FavUseCaseItemNode
   * @param viewer TreeViewer
   * @param pidcId PIDCard id
   * @param pidcVersId
   */
  public void addDelFavUcAction(final IMenuManager manager,
      final com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode favUcNode, final TreeViewer viewer,
      final Long pidcId, final Long pidcVersId) {
    final Action delAction = new Action() {

      @Override
      public void run() {

        boolean isOkToProceed = true;

        try {
          UsecaseFavorite ucFav = favUcNode.getFavUcItem().getUseCaseFav();
          // Message Popup should not appear for Private usecase
          if (isProjUsecase(ucFav)) {
            isOkToProceed = MessageDialogUtils.getConfirmMessageDialog("Update on CoC Work Packages",
                "On removing selected use case item '" + favUcNode.getName() +
                    "' from project use case, the relevant CoC Work Package(s) 'Used Flag' will be updated as 'No'. Would you like to proceed?");
          }
          if (isOkToProceed) {
            ucFav.setPidcVersId(pidcVersId);
            new UsecaseFavoriteServiceClient().delete(ucFav);
            viewer.refresh();
          }
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
        }
      }
    };

    delAction.setText("Delete Node");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16);
    delAction.setImageDescriptor(imageDesc);
    try {
      delAction.setEnabled((null == pidcId) || new CurrentUserBO().hasNodeOwnerAccess(pidcId));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
    }
    manager.add(delAction);

  }

  private boolean isProjUsecase(final UsecaseFavorite ucFav) {
    return CommonUtils.isNotNull(ucFav.getProjectId()) && CommonUtils.isNull(ucFav.getUserId());
  }


  /**
   * ICDM-1031 Action set to move to project favourite use cases
   *
   * @param manager IMenuManager
   * @param ucItem AbstractUseCaseItem
   * @param viewer TreeViewer
   * @param pidcVer PIDCard id
   * @param useCaseDataHandler
   * @param projUseCaseFlag
   */
  public void addMovToProjFav(final IMenuManager manager, final IUseCaseItemClientBO ucItem, final TreeViewer viewer,
      final PidcVersion pidcVer, final UseCaseDataHandler useCaseDataHandler, final boolean projUseCaseFlag) {
    final Action projUcAction = new Action() {

      @Override
      public void run() {

        try {
          UseCaseFavNodesMgr ucFavMgr = new UseCaseFavNodesMgr(pidcVer, useCaseDataHandler);
          useCaseDataHandler.setUcFavMgr(ucFavMgr);
          movToProjFav(ucItem, viewer, pidcVer, useCaseDataHandler);
        }
        catch (IcdmException exp) {
          CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
        }
      }


    };

    projUcAction.setText("Add as Project Use Case");// ICDM-1193
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.FAV_ADD_28X30);
    projUcAction.setImageDescriptor(imageDesc);
    try {
      projUcAction.setEnabled(projUseCaseFlag && new CurrentUserBO().hasNodeOwnerAccess(pidcVer.getPidcId()));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
    }
    manager.add(projUcAction);

  }

  /**
   * @param ucItem usecaseItem
   * @param viewer TreeViewer
   * @param pidcVer pidcVersion
   * @param ucHandler usecaseHandler
   * @throws IcdmException exception
   */
  public void movToProjFav(final IUseCaseItemClientBO ucItem, final TreeViewer viewer, final PidcVersion pidcVer,
      final UseCaseDataHandler ucHandler)
      throws IcdmException {
    if (ucHandler.isValidInsert(ucItem, true)) {
      boolean isOkToProceed = MessageDialogUtils.getConfirmMessageDialog("Update on CoC Work Packages",
          "On adding selected use case item '" + ucItem.getName() +
              "' as project use case, the relevant CoC Work Package(s) 'Used Flag' will be updated as 'Yes'. Would you like to proceed?");
      if (isOkToProceed) {
        UsecaseFavoriteServiceClient client = new UsecaseFavoriteServiceClient();

        UsecaseFavorite ucObj = new UsecaseFavorite();
        ucObj.setProjectId(pidcVer.getPidcId());
        ucObj.setPidcVersId(pidcVer.getId());

        if (ucItem instanceof UseCaseGroupClientBO) {
          UseCaseGroupClientBO grp = (UseCaseGroupClientBO) ucItem;
          ucObj.setGroupId(grp.getId());
        }
        else if (ucItem instanceof UsecaseClientBO) {
          UsecaseClientBO uc = (UsecaseClientBO) ucItem;
          ucObj.setUseCaseId(uc.getId());

        }
        else if (ucItem instanceof UseCaseSectionClientBO) {
          UseCaseSectionClientBO uc = (UseCaseSectionClientBO) ucItem;
          ucObj.setSectionId(uc.getId());


        }
        try {
          client.create(ucObj);
          viewer.refresh();
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
        }
      }
    }
  }

  /**
   * Icdm-1101 - new Action to Copy the Link Usecase in Pidc Version to ClipBoard
   *
   * @param manager manager
   * @param pidcVerId pidc version id
   * @param object UseCaseGroup/Usecase/UseCaseSection/UseCaseFavorite
   * @param ucItemId use case item id
   * @param isPrivateUCItem flag to check if it is a private UC item
   */
  public void copytoClipBoardUCLink(final IMenuManager manager, final Long pidcVerId, final Object object,
      final Long ucItemId, final boolean isPrivateUCItem) {

    Map<String, String> additionalDetails = new HashMap<>();
    additionalDetails.put(ApicConstants.PIDC_VERS_ID_STR, String.valueOf(pidcVerId));
    additionalDetails.put(ApicConstants.SELECTED_UC_ITEM_ID_STR, String.valueOf(ucItemId));
    // Copy Link Action
    final Action copyLink = new Action() {

      @Override
      public void run() {
        // ICDM-1649
        LinkCreator linkCreator = new LinkCreator(object, additionalDetails);
        try {
          linkCreator.copyToClipBoard();
          if (isPrivateUCItem) {
            CDMLogger.getInstance().infoDialog("Link '" + linkCreator.getUrl() +
                "' copied to clipboard.\n\nNOTE: This refers to the 'common' use case item, not the private use case node !!",
                Activator.PLUGIN_ID);
          }
        }
        catch (ExternalLinkException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }

    };
    copyLink.setText("Copy Link to Usecase in PIDC Version");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.PIDC_LINK_12X12);
    copyLink.setImageDescriptor(imageDesc);
    copyLink.setEnabled(true);
    manager.add(copyLink);
  }

  // ICDM-1232
  /**
   * Action to create new outlook mail with usecase Link(Link to usecase/sec/grp/private or project uc for pidc version)
   *
   * @param manager menu manager
   * @param modelObject UseCase item object
   * @param pidcVersId pidc version primary key
   * @param ucItemId usecase item id
   */
  public void sendUsecaseLinkInOutlook(final IMenuManager manager, final IModel modelObject, final Long pidcVersId,
      final Long ucItemId, final boolean isPrivateUCItem) {
    // ICDM-1649
    Map<String, String> additionalDetails = new HashMap<>();
    additionalDetails.put(ApicConstants.PIDC_VERS_ID_STR, String.valueOf(pidcVersId));
    additionalDetails.put(ApicConstants.SELECTED_UC_ITEM_ID_STR, String.valueOf(ucItemId));
    // Usecase Link in outlook Action
    final Action sendUsecaseLinkAction = new SendUsecaseObjectLinkAction(SEND_LINK_TO_USECASE_IN_PIDC_VERSION,
        modelObject, additionalDetails, isPrivateUCItem);
    manager.add(sendUsecaseLinkAction);
  }


  /**
   * ICDM-1029 Action set to move to private favourite usecases
   *
   * @param manager IMenuManager
   * @param ucItem AbstractUseCaseItem
   * @param viewer TreeViewer
   * @param ucHandler UseCaseDataHandler
   * @param privateUseCaseFlag
   */
  public void addMovToUserFav(final IMenuManager manager, final IUseCaseItemClientBO ucItem, final TreeViewer viewer,
      final UseCaseDataHandler ucHandler, final boolean privateUseCaseFlag) {
    final Action privateUcAction = new Action() {

      @Override
      public void run() {
        try {
          UseCaseFavNodesMgr ucFavMgr = new UseCaseFavNodesMgr(new CurrentUserBO().getUser(), ucHandler);
          ucHandler.setUcFavMgr(ucFavMgr);
          moveToUserFav(ucItem, viewer, ucHandler);
        }
        catch (IcdmException | ApicWebServiceException exp) {
          CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }

      }


    };

    privateUcAction.setText("Add as Private Use Case");// ICDM-1193
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.FAV_ADD_28X30);
    privateUcAction.setImageDescriptor(imageDesc);
    privateUcAction.setEnabled(privateUseCaseFlag);
    manager.add(privateUcAction);

  }

  /**
   * Open PIDC from review result
   *
   * @param menuMgr
   * @param addedObj
   */
  public void openPidcFromReviewResult(final IMenuManager menuMgr, final Object addedObj) {
    final Action openAction = new Action() {

      @Override
      public void run() {
        if (addedObj instanceof ReviewDetail) {
          ReviewDetail item = (ReviewDetail) addedObj;
          long pidId = item.getPidcId();
          Set<PidcVersion> pidCards = new TreeSet<>();
          PidcVersionServiceClient pidcversion = new PidcVersionServiceClient();
          PidcVersion pidcVer = null;
          try {
            pidcVer = pidcversion.getById(pidId);
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
          }
          if (CommonUtils.isNotNull(pidcVer)) {
            pidCards.add(pidcVer);
            onRightClickOfAPRJnew(pidCards);
          }
          else {
            CDMLogger.getInstance().infoDialog("This project id card is not available at the moment",
                Activator.PLUGIN_ID);
          }

        }
      }
    };

    openAction.setText("Open PIDC");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.PIDC_16X16);
    openAction.setImageDescriptor(imageDesc);
    menuMgr.add(openAction);
  }

  /**
   * @param ucItem
   * @param viewer
   * @param ucHandler
   * @throws IcdmException
   */
  public void moveToUserFav(final IUseCaseItemClientBO ucItem, final TreeViewer viewer,
      final UseCaseDataHandler ucHandler)
      throws IcdmException {
    if (ucHandler.isValidInsert(ucItem, true)) {
      UsecaseFavoriteServiceClient client = new UsecaseFavoriteServiceClient();
      UsecaseFavorite objUscFav = new UsecaseFavorite();
      try {
        objUscFav.setUserId(new CurrentUserBO().getUserID());
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
      if (ucItem instanceof UseCaseGroupClientBO) {
        UseCaseGroupClientBO grp = (UseCaseGroupClientBO) ucItem;
        objUscFav.setGroupId(grp.getId());
      }
      else if (ucItem instanceof UsecaseClientBO) {
        UsecaseClientBO uc = (UsecaseClientBO) ucItem;
        objUscFav.setUseCaseId(uc.getId());

      }
      else if (ucItem instanceof UseCaseSectionClientBO) {
        UseCaseSectionClientBO uc = (UseCaseSectionClientBO) ucItem;
        objUscFav.setSectionId(uc.getId());


      }
      try {
        client.create(objUscFav);
        viewer.refresh();
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp);
      }
    }
  }

  /**
   * iCDM-713 <br>
   * Open review result
   *
   * @param menuMgr
   * @param reviewDetail
   */
  public void openReviewResult(final MenuManager menuMgr, final ReviewDetail reviewDetail) {
    final Action openAction = new Action() {

      @Override
      public void run() {
        if (CommonUtils.isNotNull(reviewDetail.getReviewResultId())) {
          try {
            if (reviewDetail.getVariantId().equals(Long.valueOf(-1))) {
              openReviewResultEditor(new CDRReviewResultServiceClient().getById(reviewDetail.getReviewResultId()), null,
                  null);
            }
            else {
              RvwVariant rvwVariant = new RvwVariantServiceClient()
                  .getRvwVariantByResultNVarId(reviewDetail.getReviewResultId(), reviewDetail.getVariantId());
              openReviewResultEditor(rvwVariant, null);
            }
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().warnDialog("This review result is not available at the moment. " + e.getMessage(),
                e, Activator.PLUGIN_ID);
          }
        }
        else {
          CDMLogger.getInstance().warn("This review result is not available at the moment", Activator.PLUGIN_ID);
        }
      }
    };

    openAction.setText("Open Review Result");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.RVW_RES_CLOSED_16X16);
    openAction.setImageDescriptor(imageDesc);
    menuMgr.add(openAction);

  }


  /**
   * iCDM-713 <br>
   * Open review result
   *
   * @param menuMgr MenuManager
   * @param result Review Result
   * @param paramName Parameter Name
   * @param variantId Variant Id
   */
  public void openReviewResultAction(final IMenuManager menuMgr, final CDRReviewResult result, final String paramName,
      final Long variantId) {
    final Action openAction = new Action() {

      @Override
      public void run() {
        openReviewResultEditor(result, paramName, variantId);
      }
    };

    openAction.setText("Open Review Result");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.RVW_RES_CLOSED_16X16);
    openAction.setImageDescriptor(imageDesc);
    menuMgr.add(openAction);

  }

  private void removeDuplicates(final ScratchPadViewPart scratchViewPartNew, final SeriesStatisticsInfo addedObj) {
    for (int i = 0; i < scratchViewPartNew.getNodeList().size(); i++) {
      final ScratchPadDataFetcher nodeCheck = scratchViewPartNew.getNodeList().get(i);
      if ((nodeCheck.getSeriesStatsInfo() != null) && (nodeCheck.getSeriesStatsInfo().getCalData() != null) &&
          compareObjects(nodeCheck, addedObj)) {
        scratchViewPartNew.getNodeList().remove(nodeCheck);
        break;
      }
    }
  }

  private boolean compareObjects(final ScratchPadDataFetcher nodeCheck, final SeriesStatisticsInfo addedObj) {

    boolean flag = false;
    if ((nodeCheck.getSeriesStatsInfo().getCalData().getCalDataPhy() != null) &&
        nodeCheck.getSeriesStatsInfo().getCalData().getCalDataPhy().getName()
            .equals(addedObj.getCalData().getCalDataPhy().getName()) &&
        (nodeCheck.getSeriesStatsInfo().getCalData().getCalDataPhy().equals(addedObj.getCalData().getCalDataPhy()))) {
      flag = true;
    }
    return flag;
  }


  /**
   * @param menuMgr menuMgr
   * @param statInfoList calDataObjects
   */
  public void copyToClipBoard(final MenuManager menuMgr, final List<SeriesStatisticsInfo> statInfoList) {
    Action moveMultipleAction = new Action() {

      // Icdm-1381-Series Statistics View: Multiple Selection of parameters, additional context menue option
      @Override
      public void run() {
        String calDataName = "";
        for (SeriesStatisticsInfo data : statInfoList) {

          calDataName = CommonUtils.concatenate(calDataName, data.getCalData().getShortName(), "::",
              data.getCalDataPhyValType().getLabel(), "::", data.getCalData().getCalDataPhy().getSimpleDisplayValue());
          calDataName = CommonUtils.concatenate(calDataName, "::", "(", data.getDataSetName(), ")" + "\n");

        }

        CommonUiUtils.setTextContentsToClipboard(calDataName);

      }
    };
    // Icdm-697 to disable move to scratch pad when more than one element is selected
    moveMultipleAction.setText("Copy to ClipBoard");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.COPY_16X16);
    moveMultipleAction.setImageDescriptor(imageDesc);
    moveMultipleAction.setEnabled(!statInfoList.isEmpty());
    menuMgr.add(moveMultipleAction);

  }


  /**
   * @param manager manager
   * @param scratchPadViewPartNew scratchPadViewPart
   */
  public void copyFromClipBoard(final IMenuManager manager, final ScratchPadViewPart scratchPadViewPartNew) {
    Action moveMultipleAction = new Action() {

      @Override
      public void run() {
        addcopiedItemsToScratchPad(scratchPadViewPartNew);
      }
    };
    // Icdm-697 to disable move to scratch pad when more than one element is selected
    moveMultipleAction.setText("Paste");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.PASTE_16X16);
    moveMultipleAction.setImageDescriptor(imageDesc);
    manager.add(moveMultipleAction);

  }

  /**
   * @param set fetchFuncParams
   * @param scratchPadViewPartNew scratchPadViewPart
   */
  public void addItemsToScratchPad(final Set<Parameter> set, final ScratchPadViewPart scratchPadViewPartNew) {
    for (Parameter parameter : set) {
      ScratchPadDataFetcher dataFetcher = new ScratchPadDataFetcher();
      dataFetcher.setParameter(parameter);
      checkCalDataExists(parameter, scratchPadViewPartNew);
      scratchPadViewPartNew.getNodeList().add(dataFetcher);
    }
    Collections.sort(scratchPadViewPartNew.getNodeList(), new ScratchPadSorter());
    scratchPadViewPartNew.getTableViewer().setInput(scratchPadViewPartNew.getNodeList());
  }


  /**
   * Checks whether caldata dropped in scratchpad already exists,if so it will be removed
   *
   * @param selectedElement
   * @param seriesStatisticsType
   */
  private void checkCalDataExists(final Parameter parameter, final ScratchPadViewPart scratchPadViewPartNew) {
    for (int i = 0; i < scratchPadViewPartNew.getNodeList().size(); i++) {
      final ScratchPadDataFetcher nodeCheck = scratchPadViewPartNew.getNodeList().get(i);
      if ((nodeCheck.getParameter() != null) && (parameter.compareTo(nodeCheck.getParameter()) == 0)) {

        scratchPadViewPartNew.getNodeList().remove(nodeCheck);
        break;
      }

    }
  }

  /**
   * @param paramStrArr paramStrArr
   * @param fetchFuncParams fetchFuncParams
   * @return true if Invalid param is present.
   */
  protected boolean isInValidParamPresent(final List<String> paramStrArr, final Set<Parameter> fetchFuncParams) {
    Set<String> cdrFuncParamNames = new HashSet<>();
    StringBuilder notValidParams = new StringBuilder();
    for (Parameter parameter : fetchFuncParams) {
      cdrFuncParamNames.add(parameter.getName());
    }
    for (String paramStr : paramStrArr) {
      if (!cdrFuncParamNames.contains(paramStr)) {
        notValidParams.append(paramStr).append(",");
      }
    }
    if (notValidParams.length() > 0) {
      notValidParams.deleteCharAt(notValidParams.length() - 1);
      CDMLogger.getInstance().errorDialog(
          "The Following parameters are not available in the system and will not be added to the scratch pad -->" +
              notValidParams,
          Activator.PLUGIN_ID);
      return true;
    }
    return false;
  }


  /**
   * ICDM-1348
   *
   * @param menuMgr MenuManager
   * @param string String
   * @return Action
   */
  public Action copyParamNameToClipboardAction(final MenuManager menuMgr, final String string) {
    Action copyParamNameAction = new Action() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        CommonUiUtils.setTextContentsToClipboard(string);
      }
    };
    copyParamNameAction.setText("Copy Parameter Name to Clipboard");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.COPY_EDIT_16X16);
    copyParamNameAction.setImageDescriptor(imageDesc);
    menuMgr.add(copyParamNameAction);
    return copyParamNameAction;
  }


  /**
   * @param scratchPadViewPartNew
   */
  public void addcopiedItemsToScratchPad(final ScratchPadViewPart scratchPadViewPartNew) {
    String clipBoardStr = CommonUiUtils.getContentsFromClipBoard();
    if (CommonUtils.isEmptyString(clipBoardStr)) {
      CDMLogger.getInstance().errorDialog("No Parameter name in Clipboard", Activator.PLUGIN_ID);
      return;
    }
    List<String> paramStrArr = Arrays.asList(CommonUtils.splitWithDelimiter(clipBoardStr, DELIMIT_STR));
    Set<String> paramNameSet = new HashSet<>();
    for (String paramName : paramStrArr) {
      paramNameSet.add(paramName);
    }
    Map<String, Parameter> funcParams;
    try {
      funcParams = new ParameterServiceClient().getParamsByName(paramNameSet);
      Set<Parameter> paramSet = new HashSet<>(funcParams.values());
      isInValidParamPresent(paramStrArr, paramSet);

      addItemsToScratchPad(paramSet, scratchPadViewPartNew);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog("Error in Fetching Parameters", e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param menuMgr MenuManager
   * @param linkStr String
   * @return Action
   */
  public Action stringAsLinkAction(final MenuManager menuMgr, final String linkStr) {
    Action linkAction = new Action() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        openLinkFromString(linkStr);
      }
    };
    linkAction.setText(linkStr);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.HYPER_LINK_16X16);
    linkAction.setImageDescriptor(imageDesc);
    menuMgr.add(linkAction);
    return linkAction;
  }

  /**
   * Opens a2l editor from Review Rule editor
   *
   * @param manager
   * @param pidcA2l
   */
  // ICDM-2272
  public void openA2LEditor(final IMenuManager manager, final PidcA2l pidcA2l) {
    Action a2lEditorAction = new Action() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        openA2lFile(pidcA2l.getId());
      }
    };
    a2lEditorAction.setText("Open A2L File");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.A2LFILE_16X16);
    a2lEditorAction.setImageDescriptor(imageDesc);
    manager.add(a2lEditorAction);

  }

  // ICDM-1723
  /**
   * @param manager manager
   * @param caldata caldata
   * @param paramName paramName
   * @param fetchCheckVal true if checkvalue has to be fetched
   * @param characteristicsMap the map key : parameter name, the value : the characteristics
   */
  public void showTableGraph(final IMenuManager manager, final CalData caldata, final String paramName,
      final boolean fetchCheckVal, final Map<String, Characteristic> characteristicsMap) {
    final Action showGraph = new Action() {

      @Override
      public void run() {
        if (fetchCheckVal) {
          ConcurrentMap<String, CalData> calDataMap = new ConcurrentHashMap<>();
          CalData checkedValueObj = caldata;
          if (checkedValueObj != null) {
            calDataMap.put(CommonUIConstants.CHECK_VALUE, checkedValueObj);
          }
          // ICDM-2304
          CalDataViewerDialog calDataViewerDialog = new CalDataViewerDialog(Display.getCurrent().getActiveShell(),
              calDataMap, paramName, "Table/Graph Representation", false, false);
          // Task 234466 include T/G viewer V1.9.0
          calDataViewerDialog.setCharacteristicsMap(characteristicsMap);
          calDataViewerDialog.open();
        }
        else {
          CDMLogger.getInstance().infoDialog("Check values are not loaded!", Activator.PLUGIN_ID);
        }
      }
    };
    showGraph.setText("Show in Table/Graph viewer");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.TABLE_GRAPH_16X16);
    showGraph.setEnabled(true);
    showGraph.setImageDescriptor(imageDesc);
    manager.add(showGraph);
  }

  /**
   * @param pidcVersAttr PidcVersionAttribute
   * @return boolean value , true if pidcVersAttr is not null
   */
  public boolean isReviewAttributeSet(final PidcVersionAttribute pidcVersAttr) {
    return pidcVersAttr != null;
  }

  /**
   * This method opens the given specification link either<br>
   * a. In Desktop if given link is Shared path, <br>
   * b. In Desktop if given link is vCDM path, <br>
   * c. In Browser if given link is WebLink.
   *
   * @param specLink the given path location
   */
  // ICDM-2529
  public void openLink(final String specLink) {
    if (CommonUtils.isEmptyString(specLink)) {
      return;
    }
    String actualSpecLink = specLink.trim();

    // checks if the path is shared file path
    if (actualSpecLink.startsWith(CommonUIConstants.SHARED_PATH_PREFIX) ||
        actualSpecLink.startsWith(CommonUIConstants.FILE_PATH_PREFIX)) {
      openSharedPath(actualSpecLink);
    }
    // ICDM-843
    // checks if the path is vCDM file path
    else if ((actualSpecLink.toLowerCase(Locale.getDefault()))
        .startsWith(CommonUIConstants.VCDM_APRJ_PATH.toLowerCase(Locale.getDefault()))) {
      CDMCommonActionSet cdmCommonActionSet = new CDMCommonActionSet();
      String vcdmAprjId = actualSpecLink.toLowerCase(Locale.getDefault())
          .replace(CommonUIConstants.VCDM_APRJ_PATH.toLowerCase(Locale.getDefault()), "");
      cdmCommonActionSet.openVCDMClient(null, vcdmAprjId.trim());
    }
    else {
      // opens the path in internet explorer browser
      openExternalBrowser(actualSpecLink);
    }
  }

  /**
   * Method to display the error message if the questionnaire config attribute has already used in the review
   * questionnaire of this pidc version.
   *
   * @param pidcAttribute the pidc attribute
   * @param projObjBO AbstractProjectHandler
   * @return true if the error message for the modification of Division attribute is shown
   * @throws ApicWebServiceException exception
   */
  public static boolean isQnaireConfigModifyErrorMessageShown(final IProjectAttribute pidcAttribute,
      final AbstractProjectObjectBO projObjBO)
      throws ApicWebServiceException {
    String validationMsg = getValidationErrMsgForQnaireConfigAttrEdit(pidcAttribute, projObjBO);

    if (!validationMsg.isEmpty()) {
      MessageDialogUtils.getErrorMessageDialog("Edit Attribute : " + pidcAttribute.getName(),
          "The " + pidcAttribute.getName() + " attribute cannot be edited as \n" + validationMsg);
      return true;
    }

    return false;
  }

  private static String getValidationErrMsgForQnaireConfigAttrEdit(final IProjectAttribute pidcAttribute,
      final AbstractProjectObjectBO projObjBO)
      throws ApicWebServiceException {

    PidcVersionServiceClient pidcVersionServiceClient = new PidcVersionServiceClient();
    StringBuilder errorMsg = new StringBuilder(90);
    if (isQnaireConfigNotModifiable(pidcAttribute, projObjBO)) {
      errorMsg.append("- the project has Review Questionnaire(s)\n");
    }
    if (pidcVersionServiceClient.isCoCWPMappingAvailForPidcVersion(projObjBO.getPidcVersion().getId()).booleanValue()) {
      errorMsg.append("- the project has CoC Work Package(s)");
    }
    return errorMsg.toString();
  }

  /**
   * @param pidcAttribute pidc element attribute
   * @param projObjBO project object BO
   * @return whether qnaire config is not modifiable
   * @throws ApicWebServiceException throws Apic Web Service Exception
   */
  public static boolean isQnaireConfigNotModifiable(final IProjectAttribute pidcAttribute,
      final AbstractProjectObjectBO projObjBO)
      throws ApicWebServiceException {
    return (null != pidcAttribute.getValueId()) && new PidcVersionServiceClient()
        .isQnaireConfigAttrUsedInReview(projObjBO.getPidcVersion().getId()).booleanValue();
  }


  /**
   * add Help icon if its applicable
   *
   * @param toolBarManager ToolBarManager
   * @param suffixForHelpKey suffix of Help key
   */
  public void addHelpAction(final IToolBarManager toolBarManager, final String suffixForHelpKey) {
    Link helpLink = CommonUiUtils.INSTANCE.getHelpLink(suffixForHelpKey);
    if (helpLink != null) {
      HelpIconAction helpAction = new HelpIconAction(toolBarManager, helpLink);
      toolBarManager.add(helpAction);
      // add seperator
      toolBarManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
      toolBarManager.update(true);
    }
  }


  /**
   * Action to open Use Case from PIDC Outline view
   *
   * @param manager IMenuManager
   * @param selUseCase UsecaseClientBO
   */
  public void openUseCaseAction(final IMenuManager manager, final UsecaseClientBO selUseCase) {
    final Action openUCAction = new Action() {

      @Override
      public void run() {

        IConfigurationElement[] configs = getConfigurationElementsForUC();

        for (IConfigurationElement config : configs) {
          try {
            IUseCaseTreeViewOpenAction extension = (IUseCaseTreeViewOpenAction) config.createExecutableExtension(CLASS);
            UsecaseEditorModel useCaseEditorDataInput =
                new UseCaseServiceClient().getUseCaseEditorData(selUseCase.getId());
            extension.openUseCaseEditor(selUseCase, useCaseEditorDataInput);
          }

          catch (ApicWebServiceException | CoreException e) {
            CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
          }
        }

      }

    };

    openUCAction.setText("Open");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.UC_28X30);
    openUCAction.setImageDescriptor(imageDesc);
    openUCAction.setEnabled(true);
    manager.add(openUCAction);

  }

  /**
   * Copy RuleSet link to clipboard
   *
   * @param manager menu Manager
   * @param ruleSet RuleSet link to be copied as link
   */
  // iCDM-1249
  public void copyRuleSetLinktoClipBoard(final IMenuManager manager, final RuleSet ruleSet) {
    // Copy Link Action
    final Action copyLink = new Action() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {

        // ICDM-1649
        LinkCreator creator = new LinkCreator(ruleSet);
        try {
          creator.copyToClipBoard();
          creator.getDisplayText();
        }
        catch (ExternalLinkException exp) {
          CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }

      }

    };
    copyLink.setText("Copy Ruleset Link");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.RULE_SET_16X16);
    copyLink.setImageDescriptor(imageDesc);
    copyLink.setEnabled(true);
    manager.add(copyLink);
  }


  /**
   * Copy RuleSet link to clipboard
   *
   * @param manager menu Manager
   * @param ruleSet RuleSet link to be copied as link
   * @param cdrFuncParam as RuleSetParameter
   */
  // iCDM-1249
  public void copyRuleLinkOfParametertoClipBoard(final IMenuManager manager, final RuleSet ruleSet,
      final ReviewRule reviewRule, final IParameter cdrFuncParam) {
    // Copy Link Action
    final Action copyLink = new Action() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        Map<String, String> additionalDetails = new HashMap<>();
        additionalDetails.put(ApicConstants.RULE_ID, reviewRule.getRuleId().toString());
        additionalDetails.put(ApicConstants.RULESET_PARAM_ID, cdrFuncParam.getId().toString());

        // ICDM-1649
        LinkCreator creator = new LinkCreator(ruleSet, additionalDetails);
        try {
          creator.copyToClipBoard();
          creator.getDisplayText();
        }
        catch (ExternalLinkException exp) {
          CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }

      }

    };
    copyLink.setText("Copy Rule Link of Parameter");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.RULE_SET_16X16);
    copyLink.setImageDescriptor(imageDesc);
    copyLink.setEnabled(true);
    manager.add(copyLink);
  }


  /**
   * @return
   */
  private IConfigurationElement[] getConfigurationElementsForUC() {
    return Platform.getExtensionRegistry()
        .getConfigurationElementsFor("com.bosch.caltool.icdm.common.ui.UseCaseTreeViewExtnPt");
  }


  /**
   * Copy RuleSet link to clipboard
   *
   * @param manager menu Manager
   * @param ruleSet RuleSet link to be copied as link
   * @param cdrFuncParam as RuleSetParameter
   */
  // iCDM-1249
  /**
   * @param menuMgr
   * @param sortedSet
   */
  public void openRuleLinkAction(final IMenuManager menuMgr, final List<RuleLinks> ruleLinkList) {
    menuMgr.add(new Separator());
    if (CommonUtils.isNotEmpty(ruleLinkList)) {
      for (final RuleLinks RuleLink : ruleLinkList) {
        final Action openRuleLinkAction = new Action() {

          @Override
          public void run() {
            openLinkFromString(RuleLink.getLink());
          }
        };

        openRuleLinkAction.setText(RuleLink.getDescEng());
        final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.HYPER_LINK_16X16);
        openRuleLinkAction.setImageDescriptor(imageDesc);
        menuMgr.add(openRuleLinkAction);
      }
    }
    menuMgr.add(new Separator());
  }
  /**
   * Opens an editor for reviewing description column.
   *
   * @param cdrReportData The CDR report data.
   * @param firstElement The first element.
   * @param selPidcVar The selected PidcVariant.
   */
  public void openEditorForReviewDescCol(final CdrReportDataHandler cdrReportData, final Object firstElement,
      final PidcVariant selPidcVar) {

    // Extract parameter name based on the type of firstElement
    String paramName = firstElement instanceof DaCompareHexParam ? ((DaCompareHexParam) firstElement).getParamName()
        : ((CompHexWithCDFParam) firstElement).getParamName();

    // Check if CDR report data is not null
    if (CommonUtils.isNotNull(cdrReportData)) {
      // Retrieve review result for the parameter name
      CDRReviewResult reviewResult = cdrReportData.getReviewResult(paramName, 0);
      // Check if a review result is available
      if (CommonUtils.isNotNull(reviewResult)) {
        // Open the review result editor with the obtained review result and selected PidcVariant ID
        openReviewResultEditor(reviewResult, paramName, CommonUtils.isNotNull(selPidcVar) ? selPidcVar.getId() : null);
      }
    }
    // If CDR report data is null and firstElement is an instance of DaCompareHexParam
    else if (firstElement instanceof DaCompareHexParam) {
      DaCompareHexParam daCompareHexParam = (DaCompareHexParam) firstElement;
      try {
        // Retrieve review result by ID using a service client
        CDRReviewResult reviewResult = new CDRReviewResultServiceClient().getById(daCompareHexParam.getCdrResultId());
        // Check if a review result is available
        if (CommonUtils.isNotNull(reviewResult)) {
          // Open the review result editor with the obtained review result and primary variant ID
          openReviewResultEditor(reviewResult, paramName,
              CommonUtils.isNotNull(reviewResult.getPrimaryVariantId()) ? reviewResult.getPrimaryVariantId() : null);
        }
      }
      // Catch any ApicWebServiceException that might occur during service call
      catch (ApicWebServiceException e) {
        // Log and display error message in case of exception
        CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), Activator.PLUGIN_ID);
      }
    }
  }

}
