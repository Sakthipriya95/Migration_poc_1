/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.utils;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.BeveledBorderDecorator;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.style.VerticalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISourceProvider;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.calutil.tools.A2LUtil;
import com.bosch.caltool.datamodel.core.IBasicObject;
import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.icdm.client.bo.apic.PIDCDetailsNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNodeHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixColorCode;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.common.bo.a2l.RuleMaturityLevel;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.dialogs.OpenLinkFromMessageDialog;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.views.PIDTreeViewPart;
import com.bosch.caltool.icdm.common.ui.views.data.LinkData;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.ICDMLoggerConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.ReviewResultData;
import com.bosch.caltool.icdm.model.cdr.ReviewVariantModel;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.LinkServiceClient;
import com.bosch.rcputils.IUtilityConstants;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * @author rvu1cob
 */
public enum CommonUiUtils {

                           /**
                            * Enum constant for singleton class implimentation.
                            */
                           INSTANCE;

  /**
   * separator of vCDM studio relative paths, stored as common param
   */
  private static final String VCDMSTUDIO_REL_PATH_SEP = ";";
  /**
   * Hint buffer's length
   */
  private static final int HINT_BUFFER_LENGTH = 130;
  private static final String COL_HEADER_LABEL = "COL_HEADER_LABEL";
  /**
   *
   */
  private static final int FONT_HEIGHT_NINE = 9;

  /**
   * constant for multiple decimal separaters not allowed message
   */
  private static final String MULTIPLE_DECIMAL_NOT_ALLOWED = "Multiple decimal seperaters not allowed!";
  /**
   * constant for mixed decimal separaters not allowed message
   */
  private static final String BOTH_COMMA_AND_DOT_FOUND = "Mixed decimal seperaters not allowed!";
  /**
   * constant for invalid format message
   */
  private static final String INVALID_FORMAT = "InValid Format !";

  /**
   * ID of intro page
   */
  private static final String INTRO_VIEW_PART_ID = "org.eclipse.ui.internal.introview";

  /**
   * Get Instance method.
   *
   * @return instance of this class
   */
  public static CommonUiUtils getInstance() {
    return INSTANCE;
  }

  /**
   * Set focus the given Eclipse View
   *
   * @param viewID Eclipse View's ID
   */
  public void setfocusToView(final String viewID) {
    Display.getCurrent().syncExec(() -> {
      final IViewPart viewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(viewID);
      if (viewPart != null) {
        viewPart.setFocus();
      }

    });
  }

  /**
   * Activates a view based on the View ID
   *
   * @param viewID String
   */
  public void showView(final String viewID) {
    if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
      try {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewID);
      }
      catch (PartInitException exp) {
        CDMLogger.getInstance().debug(exp.getMessage(), exp);
      }
    }
  }

  /**
   * This method returns the PIDCVariant object, utility when PIDCDetailsNode is enabled In PIDC Virtual Details
   * structure, PIDCVariant object is handled inside PIDCDetailsNode
   *
   * @param selObj
   * @return
   */
  public PidcVariant getPidcVariantObject(final Object selObj) {
    PidcVariant pidcVar = null;
    if (selObj instanceof PidcVariant) {
      pidcVar = (PidcVariant) selObj;
    }
    else if (selObj instanceof PIDCDetailsNode) {
      pidcVar = ((PIDCDetailsNode) selObj).getPidcVariant();
    }
    return pidcVar;
  }

  /**
   * Open a Information dialog with filepath/folder path link to open from it
   *
   * @param dispMsg as displau message
   * @param filePath as open file path
   */
  public void openInfoDialogWithLink(final String dispMsg, final String filePath) {
    OpenLinkFromMessageDialog linkFromMessageDialog =
        new OpenLinkFromMessageDialog(Display.getCurrent().getActiveShell(), dispMsg, filePath,
            ICDMLoggerConstants.DIALOG_TITLE_INFO, ILoggerAdapter.LEVEL_INFO);
    linkFromMessageDialog.open();
  }

  /**
   * Open a Warning dialog with filepath/folder path link to open from it
   *
   * @param dispMsg as displau message
   * @param filePath as open file path
   */
  public void openWarningDialogWithLink(final String dispMsg, final String filePath) {
    OpenLinkFromMessageDialog linkFromMessageDialog =
        new OpenLinkFromMessageDialog(Display.getCurrent().getActiveShell(), dispMsg, filePath,
            ICDMLoggerConstants.DIALOG_TITLE_WARN, ILoggerAdapter.LEVEL_WARN);
    linkFromMessageDialog.open();
  }

  /**
   * Open a Error dialog with filepath/folder path link to open from it
   *
   * @param dispMsg as displau message
   * @param filePath as open file path
   */
  public void openErrorDialogWithLink(final String dispMsg, final String filePath) {
    OpenLinkFromMessageDialog linkFromMessageDialog =
        new OpenLinkFromMessageDialog(Display.getCurrent().getActiveShell(), dispMsg, filePath,
            ICDMLoggerConstants.DIALOG_TITLE_ERROR, ILoggerAdapter.LEVEL_ERROR);
    linkFromMessageDialog.open();
  }

  /**
   * This method returns the PIDCVariant object, utility when PIDCDetailsNode is enabled In PIDC Virtual Details
   * structure, PIDCVariant object is handled inside PIDCDetailsNode
   *
   * @param selObj
   * @return
   */
  public PidcVariant getPidcVariantObj(final Object selObj) {
    PidcVariant pidcVar = null;
    if (selObj instanceof PidcVariant) {
      pidcVar = (PidcVariant) selObj;
    }
    else if (selObj instanceof PIDCDetailsNode) {
      pidcVar = ((PIDCDetailsNode) selObj).getPidcVariant();
    }
    return pidcVar;
  }

  /**
   * Icdm-865 This method provides the source instance for enabling export state
   *
   * @return source intance
   */
  public ISourceProvider getSourceProvider() {
    final IWorkbenchWindow wbWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    ISourceProviderService spService = null;
    ISourceProvider srcProvider = null;
    if (wbWindow == null) {
      for (IWorkbenchWindow wbw : PlatformUI.getWorkbench().getWorkbenchWindows()) {
        spService = wbw.getService(ISourceProviderService.class);
        if (spService != null) {
          break;
        }
      }
    }
    else {
      spService = wbWindow.getService(ISourceProviderService.class);
    }
    if (spService != null) {
      srcProvider = spService.getSourceProvider(CommandState.VAR_EXPORT_STATUS);
    }

    return srcProvider;
  }

  /**
   * Update excel export command's state based on the selected tree node
   *
   * @param selectedNode selected node
   */
  public void updateExcelExportServiceState(final PidcTreeNode selectedNode) {
    boolean exportStatus;
    if (selectedNode != null) {
      switch (selectedNode.getNodeType()) {
        case REV_RES_NODE:
          CDRReviewResult result = selectedNode.getReviewResult();
          exportStatus = CDRConstants.REVIEW_STATUS.OPEN != CDRConstants.REVIEW_STATUS.getType(result.getRvwStatus());
          break;
        case ACTIVE_PIDC_VERSION:
        case OTHER_PIDC_VERSION:
          exportStatus = !selectedNode.getPidcVersion().isDeleted();
          break;
        case QNAIRE_RESP_NODE:
          exportStatus = !selectedNode.getQnaireResp().isDeletedFlag();
          break;
        default:
          exportStatus = false;
          break;
      }

      CommandState expReportService = (CommandState) getSourceProvider();
      expReportService.setExportService(exportStatus);

    }
  }

  /**
   * ICDM-931 Gives the deocrated image for objects which has links
   *
   * @param baseImgKey base image key
   * @param overlayKey overlay image key
   * @return baseimage/decoratedImage
   */
  public Image getLinkOverLayedImage(final ImageKeys baseImgKey, final ImageKeys overlayKey) {
    return ImageManager.getDecoratedImage(baseImgKey, overlayKey, IDecoration.TOP_RIGHT);
  }

  /**
   * ICDM-931 Gives the deocrated image for objects which has links
   *
   * @param baseImgKey base image key
   * @param overlayKey overlay image key
   * @param element object
   * @param compPkgWithLinkSet nodes with links
   * @return baseimage/decoratedImage
   */
  public Image getLinkOverLayedImageCompPkg(final ImageKeys baseImgKey, final ImageKeys overlayKey,
      final CompPackage element, final Set<Long> compPkgWithLinkSet) {
    // ICDM-959
    boolean hasLink = compPkgWithLinkSet.contains(element.getId());
    if (!hasLink) {
      if (baseImgKey != null) {
        return ImageManager.getInstance().getRegisteredImage(baseImgKey);
      }
      return null;
    }
    // ICDM-1021
    return ImageManager.getDecoratedImage(baseImgKey, overlayKey, IDecoration.TOP_RIGHT);
  }

  /**
   * ICDM-931 Gives the deocrated image for objects which has links
   *
   * @param baseImgKey base image key
   * @param overlayKey overlay image key
   * @param element object
   * @param pidcVersionBO pidc version handler
   * @return baseimage/decoratedImage
   */
  public Image getLinkOverLayedImage(final ImageKeys baseImgKey, final ImageKeys overlayKey, final Object element,
      final PidcVersionBO pidcVersionBO) {
    Attribute attr = (Attribute) element;

    if (!pidcVersionBO.getPidcDataHandler().getLinks().contains(attr.getId())) {
      if (baseImgKey != null) {
        return ImageManager.getInstance().getRegisteredImage(baseImgKey);
      }
      return null;
    }

    return ImageManager.getDecoratedImage(baseImgKey, overlayKey, IDecoration.TOP_RIGHT);
  }


  /**
   * ICDM-1091 Gives the decorated image for objects which are virtual nodes in fav nodes with decorator for link
   *
   * @param baseImgKey base image key
   * @param element object for which link is present
   * @return baseimage/decoratedImage
   */
  public Image getVirtualLinkOverLayedImage(final ImageKeys baseImgKey, final Object element) {
    ImageKeys[] overlayKeys = { null, ImageKeys.LINK_DECORATOR_12X12, ImageKeys.VIRTUAL_DECORATOR_12X12, null };
    return ImageManager.getMultipleDecoratedImage(baseImgKey, overlayKeys);
  }

  /**
   * ICDM-1091 Gives the decorated image for objects which are fav nodes with decorator for link
   *
   * @param baseImgKey base image key
   * @param element object for which link is present
   * @return baseimage/decoratedImage
   */
  public Image getFavLinkOverLayedImage(final ImageKeys baseImgKey, final Object element) {
    ImageKeys[] overlayKeys = { null, ImageKeys.LINK_DECORATOR_12X12, null, ImageKeys.FAV_NODE_DECORATOR_12X12 };
    return ImageManager.getMultipleDecoratedImage(baseImgKey, overlayKeys);
  }


  /**
   * ICDM-1801 Gives the decorated images for objects which are locked review results
   *
   * @param baseImgKey base image key
   * @param element object for which link is present
   * @return baseimage/decoratedImage
   */
  private Image getReviewResultOverLayImg(final ImageKeys baseImgKey, final Object element) {
    ReviewResultData cdrResult;
    ReviewVariantModel rvwVar = null;
    if (element instanceof ReviewVariantModel) {
      rvwVar = (ReviewVariantModel) element;
      cdrResult = rvwVar.getReviewResultData();
    }
    else {
      cdrResult = (ReviewResultData) element;
    }
    if (CDRConstants.REVIEW_STATUS.getType(cdrResult.getCdrReviewResult().getRvwStatus()).toString()
        .equals(CDRConstants.REVIEW_STATUS.OPEN.toString()) && (baseImgKey != null)) {
      ImageKeys[] overlayKeys = getOverlayImageForMapVar(rvwVar);
      if (CDRConstants.REVIEW_LOCK_STATUS.getType(cdrResult.getCdrReviewResult().getLockStatus()).toString()
          .equals(CDRConstants.REVIEW_LOCK_STATUS.YES.toString())) {
        overlayKeys[2] = ImageKeys.LOCK_8X8;
        overlayKeys[3] = ImageKeys.RED_MARK_8X8;
      }
      if (!CommonUtils.isNullOrEmpty(overlayKeys)) {
        return ImageManager.getMultipleDecoratedImage(baseImgKey, overlayKeys);

      }
      return ImageManager.getDecoratedImage(baseImgKey, ImageKeys.RED_MARK_8X8, IDecoration.BOTTOM_RIGHT);
    }
    else if (CDRConstants.REVIEW_STATUS.getType(cdrResult.getCdrReviewResult().getRvwStatus()).toString()
        .equals(CDRConstants.REVIEW_STATUS.IN_PROGRESS.toString()) && (baseImgKey != null)) {
      ImageKeys[] overlayKeys = getOverlayImageForMapVar(rvwVar);
      if (CDRConstants.REVIEW_LOCK_STATUS.getType(cdrResult.getCdrReviewResult().getLockStatus()).toString()
          .equals(CDRConstants.REVIEW_LOCK_STATUS.YES.toString())) {
        overlayKeys[2] = ImageKeys.LOCK_8X8;
        overlayKeys[3] = ImageKeys.YELLOW_MARK_8X8;
      }
      if (!CommonUtils.isNullOrEmpty(overlayKeys)) {
        return ImageManager.getMultipleDecoratedImage(baseImgKey, overlayKeys);
      }

      return ImageManager.getDecoratedImage(baseImgKey, ImageKeys.YELLOW_MARK_8X8, IDecoration.BOTTOM_RIGHT);
    }
    else if (CDRConstants.REVIEW_STATUS.getType(cdrResult.getCdrReviewResult().getRvwStatus()).toString()
        .equals(CDRConstants.REVIEW_STATUS.CLOSED.toString()) && (baseImgKey != null)) {
      ImageKeys[] overlayKeys = getOverlayImageForMapVar(rvwVar);
      if (CDRConstants.REVIEW_LOCK_STATUS.getType(cdrResult.getCdrReviewResult().getLockStatus()).toString()
          .equals(CDRConstants.REVIEW_LOCK_STATUS.YES.toString())) {
        overlayKeys[2] = ImageKeys.LOCK_8X8;
        overlayKeys[3] = ImageKeys.GREEN_MARK_8X8;
      }
      if (!CommonUtils.isNullOrEmpty(overlayKeys)) {
        return ImageManager.getMultipleDecoratedImage(baseImgKey, overlayKeys);
      }
      return ImageManager.getDecoratedImage(baseImgKey, ImageKeys.GREEN_MARK_8X8, IDecoration.BOTTOM_RIGHT);
    }
    return ImageManager.getInstance().getRegisteredImage(baseImgKey);

  }


  /**
   * Images for CDR Review Results
   *
   * @param baseImgKey base image key
   * @param element object for which link is present
   * @return baseimage/decoratedImage
   */
  private Image getReviewResultOverLayImages(final ImageKeys baseImgKey, final PidcTreeNode pidcNode) {
    CDRReviewResult reviewResult = pidcNode.getReviewResult();
    RvwVariant rvwVar = pidcNode.getReviewVarResult();
    if (reviewResult.getRvwStatus().equals(CDRConstants.REVIEW_STATUS.OPEN.getDbType())) {
      if (baseImgKey != null) {
        ImageKeys[] overlayKeys = getOverlayForMapVar(rvwVar);
        if ((null != reviewResult.getLockStatus()) &&
            reviewResult.getLockStatus().equals(CDRConstants.REVIEW_LOCK_STATUS.YES.getDbType())) {
          overlayKeys[2] = ImageKeys.LOCK_8X8;
          overlayKeys[3] = ImageKeys.RED_MARK_8X8;
        }
        if (!CommonUtils.isNullOrEmpty(overlayKeys)) {
          return ImageManager.getMultipleDecoratedImage(baseImgKey, overlayKeys);

        }
        return ImageManager.getDecoratedImage(baseImgKey, ImageKeys.RED_MARK_8X8, IDecoration.BOTTOM_RIGHT);
      }
    }
    else if (reviewResult.getRvwStatus().equals(CDRConstants.REVIEW_STATUS.IN_PROGRESS.getDbType())) {
      if (baseImgKey != null) {
        ImageKeys[] overlayKeys = getOverlayForMapVar(rvwVar);
        if ((null != reviewResult.getLockStatus()) &&
            reviewResult.getLockStatus().equals(CDRConstants.REVIEW_LOCK_STATUS.YES.getDbType())) {
          overlayKeys[2] = ImageKeys.LOCK_8X8;
          overlayKeys[3] = ImageKeys.YELLOW_MARK_8X8;
        }
        if (!CommonUtils.isNullOrEmpty(overlayKeys)) {
          return ImageManager.getMultipleDecoratedImage(baseImgKey, overlayKeys);
        }

        return ImageManager.getDecoratedImage(baseImgKey, ImageKeys.YELLOW_MARK_8X8, IDecoration.BOTTOM_RIGHT);
      }
    }
    else if (reviewResult.getRvwStatus().equals(CDRConstants.REVIEW_STATUS.CLOSED.getDbType())) {
      if (baseImgKey != null) {
        ImageKeys[] overlayKeys = getOverlayForMapVar(rvwVar);
        if ((null != reviewResult.getLockStatus()) &&
            reviewResult.getLockStatus().equals(CDRConstants.REVIEW_LOCK_STATUS.YES.getDbType())) {
          overlayKeys[2] = ImageKeys.LOCK_8X8;
          overlayKeys[3] = ImageKeys.GREEN_MARK_8X8;
        }
        if (!CommonUtils.isNullOrEmpty(overlayKeys)) {
          return ImageManager.getMultipleDecoratedImage(baseImgKey, overlayKeys);
        }
        return ImageManager.getDecoratedImage(baseImgKey, ImageKeys.GREEN_MARK_8X8, IDecoration.BOTTOM_RIGHT);
      }
    }
    return ImageManager.getInstance().getRegisteredImage(baseImgKey);

  }

  /**
   * @param pidcNode
   * @param rvwResult
   * @return
   */
  public Image getImageForCDRResult(final PidcTreeNode pidcNode) {

    Image reviewResultImage = null;
    CDRReviewResult rvwResult = pidcNode.getReviewResult();
    if (null != rvwResult.getReviewType()) {
      // images for official review
      if (rvwResult.getReviewType().equals(CDRConstants.REVIEW_TYPE.OFFICIAL.getDbType())) {
        reviewResultImage = getReviewResultOverLayImages(ImageKeys.RVW_RES_MAIN_16X16, pidcNode);
      }
      // images for test review
      else if (rvwResult.getReviewType().equals(CDRConstants.REVIEW_TYPE.TEST.getDbType())) {
        reviewResultImage = getReviewResultOverLayImages(ImageKeys.RVW_RESULT_TEST_16X16, pidcNode);
      }
      // images for start review
      else if (rvwResult.getReviewType().equals(CDRConstants.REVIEW_TYPE.START.getDbType())) {
        reviewResultImage = getReviewResultOverLayImages(ImageKeys.RVW_RESULT_START_16X16, pidcNode);
      }
    }
    else {
      reviewResultImage = getReviewResultOverLayImages(ImageKeys.RVW_RESULT_START_16X16, pidcNode);
    }
    return reviewResultImage;
  }


  // End of code to be removed
  /**
   * @param rvwVar
   * @return
   */
  private ImageKeys[] getOverlayImageForMapVar(final ReviewVariantModel rvwVar) {
    ImageKeys[] overlayKeys = { null, null, null, null };
    if (CommonUtils.isNotNull(rvwVar) && CommonUtils.isNotNull(rvwVar.getRvwVariant()) &&
        rvwVar.getRvwVariant().isLinkedVariant()) {
      overlayKeys[0] = ImageKeys.LINK_VAR_MARK_8X8;
    }
    return overlayKeys;
  }

  /**
   * @param rvwVar
   * @return
   */
  private ImageKeys[] getOverlayForMapVar(final RvwVariant rvwVar) {
    ImageKeys[] overlayKeys = { null, null, null, null };
    if (CommonUtils.isNotNull(rvwVar) && rvwVar.isLinkedVariant()) {
      overlayKeys[0] = ImageKeys.LINK_VAR_MARK_8X8;
    }
    return overlayKeys;
  }

  /**
   * Icdm-874 Different images for test/official review
   *
   * @param element Object in the tree
   * @return Image instance
   */
  public Image getImageForCDRReslt(final Object element) {

    Image reviewResultImage = null;
    String reviewType;
    if (element instanceof ReviewVariantModel) {
      reviewType = CDRConstants.REVIEW_TYPE
          .getType(((ReviewVariantModel) element).getReviewResultData().getCdrReviewResult().getReviewType())
          .toString();
    }
    // iCDM-647
    // Diff images for official review
    else {
      reviewType = CDRConstants.REVIEW_TYPE.getType(((ReviewResultData) element).getCdrReviewResult().getReviewType())
          .toString();
    }
    if (null != reviewType) {
      if (reviewType.equalsIgnoreCase(CDRConstants.REVIEW_TYPE.OFFICIAL.toString())) {

        reviewResultImage = getReviewResultOverLayImg(ImageKeys.RVW_RES_MAIN_16X16, element);
      }
      // Icdm-874 Different images for test review
      else if (reviewType.equalsIgnoreCase(CDRConstants.REVIEW_TYPE.TEST.toString())) {

        reviewResultImage = getReviewResultOverLayImg(ImageKeys.RVW_RESULT_TEST_16X16, element);
      }
      // ICDM-1801
      else if (reviewType.equalsIgnoreCase(CDRConstants.REVIEW_TYPE.START.toString())) {
        reviewResultImage = getReviewResultOverLayImg(ImageKeys.RVW_RESULT_START_16X16, element);
      }
    }
    return reviewResultImage;
  }

  /**
   * @param pidcA2l Object in the tree
   * @return Image instance
   */
  public Image getImageForA2lFile(final PidcA2l pidcA2l) {
    Image nodeImg = ImageManager.getInstance().getRegisteredImage(ImageKeys.A2LFILE_16X16);

    ImageKeys[] overlayKeys = { null, null, null, null };
    Long numCompli = pidcA2l.getCompliParamCount();
    if (CommonUtils.isNotNull(numCompli) && (numCompli > 0)) {
      overlayKeys[2] = ImageKeys.COMPLI_MARK_8X8;
    }
    if (!CommonUtils.isNullOrEmpty(overlayKeys)) {
      nodeImg = ImageManager.getMultipleDecoratedImage(ImageKeys.A2LFILE_16X16, overlayKeys);
    }
    return nodeImg;

  }

  /**
   * @param element Object in the tree
   * @return Image instance
   */
  public Image getImageForA2lFileNew(final com.bosch.caltool.icdm.model.a2l.A2LFile element) {
    Image nodeImg = ImageManager.getInstance().getRegisteredImage(ImageKeys.A2LFILE_16X16);

    ImageKeys[] overlayKeys = { null, null, null, null };
    Long numCompli = element.getNumCompli();
    if (CommonUtils.isNotNull(numCompli) && (numCompli > 0)) {
      overlayKeys[2] = ImageKeys.COMPLI_MARK_8X8;
    }
    if (!CommonUtils.isNullOrEmpty(overlayKeys)) {
      nodeImg = ImageManager.getMultipleDecoratedImage(ImageKeys.A2LFILE_16X16, overlayKeys);
    }
    return nodeImg;

  }

  /**
   * Open CDFX file in vCDMStudio
   *
   * @param cdfxPath path of cdfx file
   * @return user response to confirmation dialog
   */
  // iCDM-912
  public static boolean openCdfxFile(final String cdfxPath) {

    // Confirm before opening CDMStudio
    final boolean confirm = MessageDialogUtils.getQuestionMessageDialog("Open vCDMStudio",
        "Do you want to open \"" + cdfxPath + "\" in vCDMStudio? " + "\n\n" + getAdditionalHint());

    if (confirm) {
      // Get the exe path from common params
      final String vcdmStudioPath = fetchVcdmStudioPath();
      if (CommonUtils.isEmptyString(vcdmStudioPath)) {
        CDMLogger.getInstance().errorDialog("Could not identify vCDMStudio path. Please contact iCDM Hotline.",
            Activator.PLUGIN_ID);
      }

      // Open vCDMStudio
      Display.getDefault().asyncExec(() -> openVcdmStudio(vcdmStudioPath, cdfxPath));
    }

    return confirm;

  }

  /*
   * Obtain the vCDM Studio path from the common parameter, after path validation
   */
  private static String fetchVcdmStudioPath() {

    String retPath = "";
    try {
      String vcdmExePaths = new CommonDataBO().getParameterValue(CommonParamKey.VCDMSTUDIO_EXE_REL_PATH);
      for (String flePath : vcdmExePaths.split(VCDMSTUDIO_REL_PATH_SEP)) {
        String fullFilePath = "C:\\Program Files (x86)" + File.separator + flePath;
        if (new File(fullFilePath).exists()) {
          retPath = fullFilePath;
          break;
        }
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return retPath;
  }

  /**
   * @return additional hint when getting pre-calibration data and when exporting CDFX file
   */
  public static String getAdditionalHint() {

    StringBuilder desc = new StringBuilder(HINT_BUFFER_LENGTH);
    if (new CommonDataBO().getLanguage().equals(Language.ENGLISH)) {
      desc.append("ATTENTION: The calibration engineer is at any time responsible for the data not the tool.");
      desc.append("\n");
      desc.append(
          "iCDM pre-calibration data must not be used as series data without prior verification (data review or test)!");
    }
    else {
      desc.append("ACHTUNG: Der Applikateur hat zu jeder Zeit die Verantwortung für die Daten, nicht das Tool.");
      desc.append("\n");
      desc.append(
          "iCDM Vorbedatungswerte dürfen ohne vorherige Verifizierung (Datenreview oder Test) nicht als Serienbedatung verwendet werden!");
    }
    return desc.toString();
  }

  /**
   * Open CDM Studio with progress monitor
   *
   * @param insPath path configured in window->preferences->cdm studio
   * @param filePath path of the cdfx file
   */
  private static void openVcdmStudio(final String insPath, final String filePath) {
    final ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
    try {
      dialog.run(true, true, monitor -> {
        monitor.beginTask("Opening CDM Studio, Please wait...", IProgressMonitor.UNKNOWN);
        Display.getDefault().asyncExec(() -> executeCDMStudio(insPath, filePath));
        monitor.done();
        if (monitor.isCanceled()) {
          CDMLogger.getInstance().info("Opening CDM Studio was cancelled", Activator.PLUGIN_ID);
        }

      });
    }
    catch (InvocationTargetException | InterruptedException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      // Restore interrupted state...
      Thread.currentThread().interrupt();

    }

  }

  /**
   * Execute the CDM Studio
   */
  private static void executeCDMStudio(final String insPath, final String filePath) {
    try {
      if (insPath == null) {
        throw new IOException("Invalid CDM Studio path " + insPath);
      }
      // Defect Fix 260931, for cdm studio 15.c, changed from canape32.EXE to CDMStudio32.exe
      String command = "\"" + insPath + "\" -ys " + "\"" + filePath + "\"";
      Runtime.getRuntime().exec(command);
    }
    catch (IOException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * Get the color of the given maturity level
   *
   * @param maturityLevel CDR rule maturity level
   * @return color
   */
  public static Color getMaturityLevelColor(final RuleMaturityLevel maturityLevel) {
    Color color;

    switch (maturityLevel) {
      case START:
        color = GUIHelper.getColor(255, 215, 0);// yellow
        break;

      case STANDARD:
        color = GUIHelper.getColor(30, 144, 255);// blue
        break;

      case FIXED:
        color = GUIHelper.getColor(50, 205, 50);// green
        break;

      default:
        color = GUIHelper.getColor(255, 255, 255);// white

    }

    return color;
  }

  /**
   * Get the color of the given Focus Matrix Color Code
   *
   * @param colorCodes Focus Matrix Color Code
   * @return color
   */
  public static Color getFocusMatrixColor(final FocusMatrixColorCode colorCodes) {
    Color color;

    switch (colorCodes) {
      case RED:
        color = GUIHelper.getColor(255, 0, 0);// red
        break;

      case GREEN:
        color = GUIHelper.getColor(0, 128, 0);// green
        break;

      case YELLOW:
        color = GUIHelper.getColor(255, 255, 0);// yellow
        break;

      case ORANGE:
        color = GUIHelper.getColor(255, 153, 0);// orange
        break;

      default:
        color = GUIHelper.getColor(255, 255, 255);// white

    }

    return color;
  }

  /**
   * The below method brings the Shell to the foreground. </br>
   * This method needs to be used with <b>CAUTION</b> since this code is a workaround and it accesses OS code
   * directly</br>
   * Ref: <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=192036" >https://bugs.eclipse.org/bugs/show_bug.cgi?id=
   * 192036</a>
   *
   * @param shell The Shell to be brought to the foreground
   */
  public static void forceActive(final Shell shell) {
    long hFrom = OS.GetForegroundWindow();

    if (hFrom <= 0) {
      OS.SetForegroundWindow(shell.handle);
      return;
    }

    if (shell.handle == hFrom) {
      return;
    }

    int pid = OS.GetWindowThreadProcessId(hFrom, null);
    int threadID = OS.GetWindowThreadProcessId(shell.handle, null);

    if (threadID == pid) {
      OS.SetForegroundWindow(shell.handle);
      return;
    }

    if (pid > 0) {
      if (!OS.AttachThreadInput(threadID, pid, true)) {
        return;
      }
      OS.SetForegroundWindow(shell.handle);
      OS.AttachThreadInput(threadID, pid, false);
    }

    OS.BringWindowToTop(shell.handle);
    OS.UpdateWindow(shell.handle);
    OS.SetActiveWindow(shell.handle);
  }

  /**
   * @param paramType parameter type
   * @return the Column image for Type
   */
  public Image getParamTypeImage(final String paramType) {

    Image img;

    switch (paramType) {
      case A2LUtil.MAP_TEXT:
        img = ImageManager.getInstance().getRegisteredImage(ImageKeys.MAP_16X16);
        break;
      case A2LUtil.CURVE_TEXT:
        img = ImageManager.getInstance().getRegisteredImage(ImageKeys.CURVE_16X16);
        break;
      case A2LUtil.VALUE_TEXT:
        img = ImageManager.getInstance().getRegisteredImage(ImageKeys.VALUE_16X16);
        break;
      case A2LUtil.ASCII_TEXT:
        img = ImageManager.getInstance().getRegisteredImage(ImageKeys.ASCII_16X16);
        break;
      case A2LUtil.VAL_BLK_TEXT:
        img = ImageManager.getInstance().getRegisteredImage(ImageKeys.VALBLK_16X16);
        break;
      case A2LUtil.AXIS_PTS_TEXT:
        img = ImageManager.getInstance().getRegisteredImage(ImageKeys.AXIS_16X16);
        break;
      default:
        img = null;
    }
    return img;
  }

  /**
   * @param viewer Viewer
   */
  public void addKeyListenerToCopyNameFromViewer(final Viewer viewer) {
    viewer.getControl().addKeyListener(new KeyListener() {

      @Override
      public void keyReleased(final KeyEvent keyevent) {
        // Not applicable
      }

      @Override
      public void keyPressed(final KeyEvent keyevent) {
        IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
        if (selection.getFirstElement() instanceof IBasicObject) {
          // checking if the selection is an instance of AbstractDataObject
          IBasicObject dataObj = (IBasicObject) selection.getFirstElement();
          if (CommonUtils.isNotNull(dataObj) && (keyevent.stateMask == CommonUIConstants.KEY_CTRL) &&
              (keyevent.keyCode == CommonUIConstants.KEY_COPY)) {
            CommonUiUtils.setTextContentsToClipboard(dataObj.getName());
          }
        }
      }

    });
  }

  /**
   * @param linkData LinkData
   * @param nodeId Long
   * @param nodeType String
   * @return Link Object
   */
  public Link setLinkObjForCreateLink(final LinkData linkData, final Long nodeId, final String nodeType) {
    Link linkObj = new Link();
    linkObj.setLinkUrl(linkData.getNewLink());
    linkObj.setNodeId(nodeId);
    linkObj.setNodeType(nodeType);
    linkObj.setDescriptionEng(linkData.getNewDescEng());
    linkObj.setDescriptionGer(linkData.getNewDescGer());

    // if node type is attribute value, set attribute value id
    if (nodeType.equals(ApicConstants.ATTRIB_VALUE)) {
      linkObj.setAttributeValueId(nodeId);
    }

    return linkObj;
  }

  /**
   * call webservice to add links
   *
   * @param linkListforAdd Link objects
   */
  private void createLinks(final List<Link> linkListforAdd) {
    try {
      Set<Link> newLinks = new LinkServiceClient().create(linkListforAdd);
      if (CommonUtils.isNotEmpty(newLinks)) {
        CDMLogger.getInstance().info("New Links created successfully");
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * call webservice to edit links
   *
   * @param linkListforEdit Link objects
   */
  private void editLinks(final List<Link> linkListforEdit) {
    try {
      Set<Link> editLinks = new LinkServiceClient().update(linkListforEdit);
      if (CommonUtils.isNotEmpty(editLinks)) {
        CDMLogger.getInstance().info("Links have been edited successfully");
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * call webservice to delete links
   *
   * @param linkListforDel Link objects
   */
  private void deleteLinks(final List<Link> linkListforDel) {
    try {
      new LinkServiceClient().delete(linkListforDel);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * Calls LinkServiceClient to create,update and delete link(ICDM-1502)
   *
   * @param input SortedSet<LinkData> input from the tab viewer
   * @param nodeId Long
   * @param nodeType String
   * @return
   * @throws ApicWebServiceException
   */
  public com.bosch.caltool.icdm.model.apic.attr.AttributeValue createMultipleLinkService(
      final SortedSet<LinkData> input, final Long nodeId, final IModelType nodeType) {
    List<Link> linkListforDel = new ArrayList<>();
    List<Link> linkListforAdd = new ArrayList<>();
    List<Link> linkListforEdit = new ArrayList<>();
    com.bosch.caltool.icdm.model.apic.attr.AttributeValue updatedAttrValue = null;
    if (CommonUtils.isNotEmpty(input)) {
      for (LinkData linkData : input) {
        Link linkObj = linkData.getLinkObj();
        if ((null != linkObj) && (CommonUIConstants.CHAR_CONSTANT_FOR_DELETE == linkData.getOprType())) {
          linkListforDel.add(linkObj);
        }
        else if ((null != linkObj) && (CommonUIConstants.CHAR_CONSTANT_FOR_EDIT == linkData.getOprType())) {
          linkObj.setLinkUrl(linkData.getNewLink());
          linkObj.setDescriptionEng(linkData.getNewDescEng());
          linkObj.setDescriptionGer(linkData.getNewDescGer());
          linkListforEdit.add(linkObj);
        }
        else if (CommonUIConstants.CHAR_CONSTANT_FOR_ADD == linkData.getOprType()) {
          linkListforAdd.add(setLinkObjForCreateLink(linkData, nodeId, nodeType.getTypeCode()));
        }
      }
      if (CommonUtils.isNotEmpty(linkListforAdd) && CommonUtils.isNotNull(linkListforAdd)) {
        createLinks(linkListforAdd);
      }
      if (CommonUtils.isNotEmpty(linkListforEdit) && CommonUtils.isNotNull(linkListforEdit)) {
        editLinks(linkListforEdit);
      }
      if (CommonUtils.isNotEmpty(linkListforDel) && CommonUtils.isNotNull(linkListforDel)) {
        deleteLinks(linkListforDel);
      }
      if (nodeType.getTypeCode().equals(ApicConstants.ATTRIB_VALUE)) {

        try {
          updatedAttrValue = new AttributeValueServiceClient().getById(nodeId);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }

      }

    }
    return updatedAttrValue;
  }


  /**
   * @return if current display is availble, get current display, else default display
   */
  public Display getDisplay() {
    return CommonUtils.checkNull(Display.getCurrent(), Display.getDefault());
  }

  /**
   * @param attrLevel level of attribute
   * @param attrValueEng value eng
   * @param attrValueGer value german
   * @return true if name is valid
   */
  public boolean validateVariantName(final int attrLevel, final String attrValueEng, final String attrValueGer) {
    if (attrLevel == ApicConstants.VARIANT_CODE_ATTR) {
      if (!CommonUtils.isvCDMCompliantName(attrValueEng.trim()) ||
          (!CommonUtils.isEmptyString(attrValueGer.trim()) && !CommonUtils.isvCDMCompliantName(attrValueGer.trim()))) {
        MessageDialogUtils.getErrorMessageDialog("Invalid Value Info:", getMessage("ATTR_EDITOR", "vCDM_VALID_NAME"));
        return false;
      }
    }
    return true;
  }

  /**
   * @param filePath file path
   * @return the filr name for the file path
   */
  public static String getFileName(final String filePath) {
    return filePath.substring(filePath.lastIndexOf('\\') + 1);
  }

  /**
   * @param configRegistry IConfigRegistry
   * @return IConfigRegistry instance with style set
   */
  public static IConfigRegistry getNatTableHeaderStyle(final IConfigRegistry configRegistry) {
    IStyle style = new Style();
    style.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WIDGET_BACKGROUND);
    style.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_WIDGET_FOREGROUND);
    style.setAttributeValue(CellStyleAttributes.GRADIENT_BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
    style.setAttributeValue(CellStyleAttributes.GRADIENT_FOREGROUND_COLOR, GUIHelper.getColor(136, 212, 215));
    style.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    style.setAttributeValue(CellStyleAttributes.VERTICAL_ALIGNMENT, VerticalAlignmentEnum.MIDDLE);
    style.setAttributeValue(CellStyleAttributes.BORDER_STYLE, null);
    style.setAttributeValue(CellStyleAttributes.FONT,
        GUIHelper.getFont(new FontData("Segoe UI", FONT_HEIGHT_NINE, SWT.NONE)));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, style, DisplayMode.NORMAL,
        COL_HEADER_LABEL);
    ICellPainter valueHeaderCellPainter = new BeveledBorderDecorator(new TextPainter());
    // configure the painter
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, valueHeaderCellPainter,
        DisplayMode.NORMAL, COL_HEADER_LABEL);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, valueHeaderCellPainter,
        DisplayMode.NORMAL, COL_HEADER_LABEL);
    return configRegistry;
  }

  /**
   * Opens the file in window desktop
   *
   * @param filePath the file to be opened
   */
  public static void openFile(final String filePath) {
    try {
      CDMLogger.getInstance().debug("Opening file \"{}\" with default application . . .", filePath);
      // ICDM-745
      File fileToOpen = new File(filePath);
      Desktop.getDesktop().open(fileToOpen);

      CDMLogger.getInstance().debug("File opened successfully");
    }
    catch (IOException exp) {
      CDMLogger.getInstance().error(
          "Error occurred while opening file \"" + FilenameUtils.getBaseName(filePath) + "\". " + exp.getMessage(), exp,
          Activator.PLUGIN_ID);
    }
  }

  /**
   * Icon for usecase which are not up to date
   *
   * @param baseImgKey base image key
   * @param overlayKey overlay image key
   * @param element object
   * @return baseimage/decoratedImage
   */
  public Image getOutdatedOverLayedImage(final ImageKeys baseImgKey, final ImageKeys overlayKey, final Object element) {
    if (element instanceof UsecaseClientBO) {
      UsecaseClientBO uc = (UsecaseClientBO) element;

      if (!uc.isUpToDate()) {
        if ((baseImgKey != null) && (overlayKey != null)) {
          return ImageManager.getDecoratedImage(baseImgKey, overlayKey, IDecoration.BOTTOM_LEFT);
        }
      }
    }
    return ImageManager.getInstance().getRegisteredImage(baseImgKey);
  }

  /**
   * @param baseImgKey base Img Key
   * @param element element
   * @return overlayed image
   */
  public Image getLinkOutdatedOverLayedImage(final ImageKeys baseImgKey, final Object element) {
    ImageKeys[] overlayKeys =
        { null, ImageKeys.LINK_DECORATOR_12X12, ImageKeys.VIRTUAL_DECORATOR_12X12, ImageKeys.OUTDATED, null };
    return ImageManager.getMultipleDecoratedImage(baseImgKey, overlayKeys);
  }

  /**
   * @param baseImgKey base Img Key
   * @param element element
   * @return overlayed image
   */
  public Image getVirtualLinkOutdatedOverLayedImage(final ImageKeys baseImgKey, final Object element) {
    ImageKeys[] overlayKeys =
        { null, ImageKeys.LINK_DECORATOR_12X12, ImageKeys.VIRTUAL_DECORATOR_12X12, ImageKeys.OUTDATED };
    return ImageManager.getMultipleDecoratedImage(baseImgKey, overlayKeys);
  }

  /**
   * If Pidc tree view part is available with tree node handler, return its instance If not, initialise a new tree node
   * handler object
   *
   * @return PidcTreeNodeHandler
   */
  public PidcTreeNodeHandler getPidcViewPartHandlerIfPresent() {
    PidcTreeNodeHandler treeHandler;
    final IViewPart viewPartObj =
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(PIDTreeViewPart.VIEW_ID);
    if ((null != viewPartObj) && (viewPartObj instanceof PIDTreeViewPart)) {
      final PIDTreeViewPart pidViewPart = (PIDTreeViewPart) viewPartObj;
      if (null != pidViewPart.getTreeHandler()) {
        treeHandler = pidViewPart.getTreeHandler();
      }
      else {
        treeHandler = new PidcTreeNodeHandler(true);
        pidViewPart.setTreeHandler(treeHandler);
      }
    }
    else {
      treeHandler = new PidcTreeNodeHandler(true);
    }
    return treeHandler;
  }

  /**
   * @return active page instance in active editor
   */
  public AbstractFormPage getActivePageInActiveEditor() {
    AbstractFormEditor activeEditor = getActiveEditor();
    if (activeEditor != null) {
      return (AbstractFormPage) activeEditor.getActivePageInstance();
    }
    return null;
  }

  /**
   * @return Active editor instance
   */
  public AbstractFormEditor getActiveEditor() {
    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    if (page != null) {
      return (AbstractFormEditor) page.getActiveEditor();
    }
    return null;
  }


  /**
   * iCDM-1249 Set contents to clipboard
   *
   * @param textData - text format of contents to be copied
   * @param html - html format of contents to be copied
   */
  public static void setContentsToClipboard(final String textData, final String html) {
    if ((textData == null) || (html == null)) {
      return;
    }
    HTMLTransfer htmlTransfer = HTMLTransfer.getInstance();
    Clipboard clipboard = new Clipboard(Display.getCurrent());
    TextTransfer textTransfer = TextTransfer.getInstance();
    Transfer[] transfers = new Transfer[] { htmlTransfer, textTransfer };
    Object[] data = new Object[] { html, textData };
    clipboard.setContents(data, transfers);
    clipboard.dispose();
  }

  /**
   * ICDM-1348 Set text contents to clipboard
   *
   * @param textData String
   */
  public static void setTextContentsToClipboard(final String textData) {
    if (textData == null) {
      return;
    }
    Clipboard clipboard = new Clipboard(Display.getCurrent());
    TextTransfer textTransfer = TextTransfer.getInstance();
    Transfer[] transfers = new Transfer[] { textTransfer };
    Object[] data = new Object[] { textData };
    clipboard.setContents(data, transfers);
    clipboard.dispose();
  }

  /**
   * get the contents from the clip board
   *
   * @return clipboard contents as string
   */
  public static String getContentsFromClipBoard() {
    String result = "";
    java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    // odd: the Object param of getContents is not currently used
    Transferable contents = clipboard.getContents(null);
    if ((contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
      try {
        result = (String) contents.getTransferData(DataFlavor.stringFlavor);
      }
      catch (UnsupportedFlavorException | IOException ex) {
        CDMLogger.getInstance().error(" Error while copying the contents from clipboard!", ex, Activator.PLUGIN_ID);
      }
    }
    return result;
  }

  /**
   * @return SortedSet<String>
   */
  public static SortedSet<String> getDateFormats() {
    // below formats can be used to meet new date format change in future
    // "mm/dd/yyyy", "dd/mm/yyyy", "mm/dd/ yy", "dd/mm/yy", "yyyy/dd/mm", "yyyy/mm/dd",
    // "yy/dd/mm", "mm-dd-yyyy", "dd-mm-yyyy", "yyyy-dd-mm", "yyyy-mm-dd", "dd-mmm-yyyy",
    // "mmm-dd-yyyy", "yyyy-mmm-dd", "yyyy-dd-mmm", "dd.mm.yyyy", "mm.dd.yyyy", "yyyy.mm"
    // ICDM-154
    SortedSet<String> dateFormats = new TreeSet<String>();
    // add the date formats to sorted set
    for (String string : ApicConstants.ATTR_DATE_FORMATS) {
      dateFormats.add(string);
    }
    return dateFormats;
  }

  /**
   * @param format defines attribute format
   * @return boolean
   */
  public static boolean validateNumberFormat(final String format) {
    boolean isValid;
    Pattern pattern;
    Matcher patternMatcher;
    String regex;
    if (" ".equals(format)) {
      // if there is no format
      isValid = true;
    }
    else {
      if ((format.length() > 2) && (format.contains(".") || format.contains(","))) {
        // if the format containts the . or ,
        regex = "[/:<>!~@*#$%^&()=?()\"|![a-zA-Z]\\[#$]";
        pattern = Pattern.compile(regex);
        patternMatcher = pattern.matcher(format.subSequence(0, format.length()));
        if (patternMatcher.find()) {
          isValid = false;
        }
        else {
          isValid = true;
        }
      }
      else {
        // if the format does not contain . or ,
        regex = "[/.,:<>!~@*#$%^&()=?()\"|![a-zA-Z]\\[#$]";
        pattern = Pattern.compile(regex);
        patternMatcher = pattern.matcher(format.subSequence(0, format.length()));
        if (patternMatcher.find()) {
          isValid = false;
        }
        else {
          isValid = true;
        }
      }
    }
    return isValid;
  }

  /**
   * @param input defines text filed input
   * @param decorators instance
   * @param conDec instance
   */
  // ICDM-112
  public static void validateNumFormatStyle(final String input, final Decorators decorators,
      final ControlDecoration conDec) {
    boolean isValid = validateFormat(input);
    if (isValid) {
      if (input.length() > 0) {
        // if it is valid format, do not show error decorator
        decorators.showErrDecoration(conDec, IUtilityConstants.EMPTY_STRING, false);
      }
      else {
        // if the text is empty, show required decoration
        decorators.showReqdDecoration(conDec, IUtilityConstants.MANDATORY_MSG);
      }
    }
    else {
      // if the text is not valid, show error decorator
      decorators.showErrDecoration(conDec, IUtilityConstants.MULTIPLE_DECIMAL_NOT_ALLOWED, true);
    }
  }

  /**
   * @param format defines attributes format
   * @return boolean
   */
  public static boolean validateFormat(final String format) {
    boolean isValid;
    int commaCount = 0;
    int dotCount = 0;
    int negativeCount = 0;
    int plusCount = 0;
    if (!" ".equals(format.trim())) {
      for (int i = 0; i < format.length(); i++) {
        char chVal = format.charAt(i);
        boolean isDigit = Character.isDigit(chVal);
        if (!isDigit) {
          if (chVal == ',') {
            // when there is , in the format
            commaCount++;
          }
          else if (chVal == '.') {
            // when there is . in the format
            dotCount++;
          }
          else if (chVal == '-') {
            // when there is - in the format
            negativeCount++;
          }
          else if (chVal == '+') {
            // when there is + in the format
            plusCount++;
          }
        }
      }
    }
    if ((commaCount > 1) || (dotCount > 1)) {
      CDMLogger.getInstance().info(MULTIPLE_DECIMAL_NOT_ALLOWED, Activator.PLUGIN_ID);
      isValid = false;
    }
    else if ((commaCount == 1) && (dotCount == 1)) {
      CDMLogger.getInstance().info(BOTH_COMMA_AND_DOT_FOUND, Activator.PLUGIN_ID);
      isValid = false;
    }
    else if ((negativeCount > 1) || (plusCount > 1) || ((negativeCount == 1) && (plusCount == 1))) {
      CDMLogger.getInstance().info(INVALID_FORMAT, Activator.PLUGIN_ID);
      isValid = false;
    }
    else {
      isValid = true;
    }
    return isValid;
  }

  /**
   * @param unit defines attribute unit
   * @return boolean
   */
  // ICDM-112
  public static boolean validateUnit(final String unit) {
    boolean isValid;
    // [/,:<>!~@#$%^&()+=?()\"|!\\[#$-]
    Pattern pattern = Pattern.compile("[,:<>!~@*#$%^&()+=?()\"|!\\[#$]");
    Matcher patternMatcher = pattern.matcher(unit.subSequence(0, unit.length()));
    if (patternMatcher.find()) {
      isValid = false;
    }
    else {
      isValid = true;
    }
    return isValid;
  }

  /**
   * Create TextPropertyDescriptor
   *
   * @param objId
   * @param displayName
   * @param category
   * @return
   */
  // ICDM-100
  public static PropertyDescriptor createTxtDesciptor(final Object objId, final String displayName,
      final String category) {
    PropertyDescriptor txtDescriptor = new PropertyDescriptor(objId, displayName);
    if ((category != null) && (category.length() > 0)) {
      txtDescriptor.setCategory(category);
    }

    return txtDescriptor;

  }


  public static IPropertyDescriptor[] createPropertyDescFields(final String[] propDescFields) {
    List<IPropertyDescriptor> propertyDescriptorsList = new ArrayList<IPropertyDescriptor>();

    // ICDM-54
    String categoryTitle = " ";
    String title = "Title";
    PropertyDescriptor descriptorTitle = createTxtDesciptor(title, title, categoryTitle);
    propertyDescriptorsList.add(descriptorTitle);

    String category = "Info";

    for (String propDesc : propDescFields) {
      PropertyDescriptor descriptor = createTxtDesciptor(propDesc, propDesc, category);
      propertyDescriptorsList.add(descriptor);
    }

    IPropertyDescriptor[] propDescriptors = new IPropertyDescriptor[propertyDescriptorsList.size()];
    propertyDescriptorsList.toArray(propDescriptors);

    return propDescriptors;
  }

  /**
   * Get the icon for based on the parameter type
   *
   * @param valueType value type (VALUE,CURVE,MAP...)
   * @return Image
   */
  public static ImageDescriptor getIconForParamType(final String valueType) {
    if (CommonUtils.isEqual(valueType, A2LUtil.VALUE_TEXT)) {
      return ImageManager.getImageDescriptor(ImageKeys.VALUE_16X16);
    }
    if (CommonUtils.isEqual(valueType, A2LUtil.CURVE_TEXT)) {
      return ImageManager.getImageDescriptor(ImageKeys.CURVE_16X16);
    }
    if (CommonUtils.isEqual(valueType, A2LUtil.ASCII_TEXT)) {
      return ImageManager.getImageDescriptor(ImageKeys.ASCII_16X16);
    }
    if (CommonUtils.isEqual(valueType, A2LUtil.AXIS_PTS_TEXT)) {
      return ImageManager.getImageDescriptor(ImageKeys.AXIS_16X16);
    }
    if (CommonUtils.isEqual(valueType, A2LUtil.MAP_TEXT)) {
      return ImageManager.getImageDescriptor(ImageKeys.MAP_16X16);
    }
    if (CommonUtils.isEqual(valueType, A2LUtil.VAL_BLK_TEXT)) {
      return ImageManager.getImageDescriptor(ImageKeys.VALBLK_16X16);
    }
    return null;
  }

  /**
   * @param grpName grpNmae
   * @param name name
   * @return the tool tip text for the Group name and name
   */
  public static String getMessage(final String grpName, final String name, final Object... varArgs) {
    try {
      return new CommonDataBO().getMessage(grpName, name, varArgs);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      return "";
    }
  }

  /**
   * @param suffixForHelpKey String
   * @return URL as String
   */
  public Link getHelpLink(final String suffixForHelpKey) {
    CommonDataBO commonDataBo = new CommonDataBO();
    try {
      String helpLinkKey = "HELP_" + suffixForHelpKey;
      CDMLogger.getInstance().debug("Fetching Help link for key : {}", helpLinkKey);
      Link helpLink = commonDataBo.getHelpLink(helpLinkKey);
      CDMLogger.getInstance().debug("  Help link : {}", helpLink);
      return helpLink;
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp);
    }
    return null;
  }

  /**
   * hide intro view part
   */
  public static void hideIntroViewPart() {
    // Get current page
    IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    // Find desired view :
    IViewPart introPart = activePage.findView(INTRO_VIEW_PART_ID);
    if (introPart != null) {
      activePage.hideView(introPart);
    }
  }

  /**
   * @param number
   * @return based on the language settings and return the percentage as String
   */
  public static String displayLangBasedPercentage(final String number) {
    String localNumber = number;
    // If the percentage is already in german format then the double.parser cannot parse that number to avoid this we
    // are replacing the "," with "." any how we are converting the percentage based on language selection
    if (localNumber.contains(",")) {
      localNumber = localNumber.replace(",", ".");
    }
    String percentNumber = "";
    String langName = Language.getLanguage(PlatformUI.getPreferenceStore().getString(ApicConstants.LANGUAGE)).getText();
    if (Language.GERMAN.getText().equalsIgnoreCase(langName)) {
      percentNumber = String.format(Locale.GERMAN, "%.2f", Double.parseDouble(localNumber));
    }
    else {
      percentNumber = String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(localNumber));
    }
    return percentNumber;
  }

}
