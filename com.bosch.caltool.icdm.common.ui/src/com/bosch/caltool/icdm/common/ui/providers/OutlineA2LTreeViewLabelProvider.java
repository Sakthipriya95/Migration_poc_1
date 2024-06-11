/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.providers;

import java.util.Map;
import java.util.SortedSet;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.calibration.group.Group;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFileInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.PidcA2LBO;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponentFunctions;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.A2LGroup;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackageGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpObj;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;


/**
 * @author apj4cob
 */
public class OutlineA2LTreeViewLabelProvider extends StyledCellLabelProvider implements ILabelProvider {

  private final A2LFileInfoBO a2lFileInfoBO;

  private final A2LWPInfoBO a2lwpInfoBO;
  /**
   * a2l file name
   */
  private final PidcA2LBO pidcA2lBo;
  /**
   * cdrReportDataHandler CdrReportDataHandler
   */
  private final CdrReportDataHandler cdrReportDataHandler;

  /**
   * Constructor
   *
   * @param a2lwpInfoBO A2LWPInfoBO
   * @param pidcA2lBo PidcA2LBO
   * @param cdrReportDataHandler CdrReportDataHandler
   */
  public OutlineA2LTreeViewLabelProvider(final A2LWPInfoBO a2lwpInfoBO, final PidcA2LBO pidcA2lBo,
      final CdrReportDataHandler cdrReportDataHandler) {
    super();
    this.a2lwpInfoBO = a2lwpInfoBO;
    this.a2lFileInfoBO = a2lwpInfoBO.getA2lFileInfoBo();
    this.pidcA2lBo = pidcA2lBo;
    this.cdrReportDataHandler = cdrReportDataHandler;
  }

  /**
   * This method prepares the text and image for the Outline tree nodes icdm-272
   */
  @Override
  public void update(final ViewerCell cell) {
    // Get the element
    final Object element = cell.getElement();
    // Create a styled string
    final StyledString cellText = new StyledString();
    Image nodeImage;
    nodeImage = getImage(element, cellText); // set text
    cell.setText(cellText.toString());
    // set the image
    cell.setImage(nodeImage);
    super.update(cell);
  }

  /**
   * @param element
   * @param cellText
   * @param nodeImage
   * @return Image
   */
  private Image getImage(final Object element, final StyledString cellText) { // NOPMD by dmo5cob on 7/24/14 11:31 AM

    if (element instanceof String) {
      // icdm-272
      return updateStringData(element, cellText);
    }
    Image nodeImage;
    if (element instanceof A2LFile) {
      cellText.append(this.pidcA2lBo.getA2LFileName());
      nodeImage = CommonUiUtils.getInstance().getImageForA2lFileNew(this.pidcA2lBo.getA2lFile());
    }
    else {
      nodeImage = getImageForObj(element, cellText);
    }


    return nodeImage;
  }

  /**
   * @param element Object
   * @param cellText StyledString
   * @return Image
   */
  public Image getImageForObj(final Object element, final StyledString cellText) {
    Image nodeImage = null;
    if (element instanceof Function) {
      cellText.append(((Function) element).getName());
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNCTION_28X30);
    }
    // Icdm-469 Icdm-586
    else if (element instanceof A2LParameter) {
      cellText.append(((A2LParameter) element).getName().trim());
      final A2LParameter character = (A2LParameter) element;
      nodeImage = setImageForParam(character);
    }
    else if (element instanceof A2LBaseComponents) {
      cellText.append(((A2LBaseComponents) element).getBcName());
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.BC_16X16);
    }
    else if (element instanceof A2LBaseComponentFunctions) {
      cellText.append(((A2LBaseComponentFunctions) element).getName());
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNCTION_28X30);
    }
    // Responsible Node for CDR Report/CompareHEx Report
    else if ((element instanceof A2lResponsibility) && (this.cdrReportDataHandler != null)) {
      A2lResponsibility a2lResp = (A2lResponsibility) element;
      cellText.append(a2lResp.getName());
      nodeImage = getA2lRespNodeImageForCDRReport(a2lResp);
    }
    // Responsible Node
    else if (element instanceof A2lResponsibility) {
      cellText.append(((A2lResponsibility) element).getName());
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.WP_DEFN_USER_RESPONSIBLE);
    }
    // Workpackage Node under Responsibility Node for CDR Report/CompareHex Report
    else if ((element instanceof A2lWpResponsibility) && (this.cdrReportDataHandler != null)) {
      A2lWpResponsibility a2lWpResponsibility = (A2lWpResponsibility) element;
      cellText.append(a2lWpResponsibility.getName());
      nodeImage = getA2lWpNodeImageForCDRReport(a2lWpResponsibility);
    }
    // Workpackage Node under Responsibility Node
    else if (element instanceof A2lWpResponsibility) {
      cellText.append(((A2lWpResponsibility) element).getName());
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.WP_28X30);
    }
    else if (element instanceof A2lWorkPackageGroup) {
      cellText.append(((A2lWorkPackageGroup) element).getGroupName());
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.WP_GROUP_28X30);
    }
    // ICDM-209 and ICDM-210
    else if (element instanceof A2lWpObj) {
      cellText.append(((A2lWpObj) element).getWpName());
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.WP_28X30);
    }
    else if (element instanceof A2LGroup) {
      cellText.append(((A2LGroup) element).getGroupName());
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.WP_GROUP_28X30);
    }
    // icdm-272
    else if (element instanceof Group) {
      cellText.append(((Group) element).getName());
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.WP_28X30);
    }
    // icdm-866
    else if (element instanceof CompPackage) {
      cellText.append(((CompPackage) element).getName());
      // ICDM-977
      nodeImage = CommonUiUtils.getInstance().getLinkOverLayedImageCompPkg(ImageKeys.CMP_PKG_16X16,
          ImageKeys.LINK_DECORATOR_12X12, (CompPackage) element, this.a2lFileInfoBO.getCpNodeWithLinks());
    }

    else if (element instanceof RuleSet) {
      cellText.append(((RuleSet) element).getName());
      // ICDM-977
      nodeImage =
          CommonUiUtils.getInstance().getLinkOverLayedImage(ImageKeys.RULE_SET_16X16, ImageKeys.LINK_DECORATOR_12X12);
    }
    return nodeImage;
  }

  /**
   * @param a2lWpObj
   * @return
   */
  private Image getA2lWpNodeImageForCDRReport(final A2lWpResponsibility wpResponsibility) {
    // fetching a2lwpresponsibility from Active A2lWPDefModel
    String respName = wpResponsibility.getMappedWpRespName();
    SortedSet<A2lWpResponsibility> a2lWpResponsibilities =
        this.cdrReportDataHandler.getA2lWPResponsibleMap().get(respName);

    A2lResponsibility a2lResponsibility = this.cdrReportDataHandler.getResponsibilityForRespName(respName);

    if (CommonUtils.isNotNull(a2lWpResponsibilities) && (a2lResponsibility != null)) {
      for (A2lWpResponsibility a2lWpResponsibility : a2lWpResponsibilities) {
        String wprespStatus = getWPRespFinishedStatus(a2lWpResponsibility.getA2lWpId(), a2lResponsibility.getId());
        if (wprespStatus.equals(CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType()) &&
            a2lWpResponsibility.getName().equals(wpResponsibility.getName())) {
          return ImageManager.getDecoratedImage(ImageKeys.WP_28X30, ImageKeys.ALL_8X8, IDecoration.BOTTOM_RIGHT);
        }
      }
    }

    return ImageManager.getInstance().getRegisteredImage(ImageKeys.WP_28X30);
  }


  /**
   * @param nodeImage
   * @param a2lResp
   * @return
   */
  private Image getA2lRespNodeImageForCDRReport(final A2lResponsibility a2lResp) {

    SortedSet<A2lWpResponsibility> a2lWpResponsibilities =
        this.cdrReportDataHandler.getA2lWPResponsibleMap().get(a2lResp.getName());

    // fetching a2lwpresponsibility from Active A2lWPDefModel
    if (CommonUtils.isNotNull(a2lWpResponsibilities)) {
      for (A2lWpResponsibility a2lWpResponsibility : a2lWpResponsibilities) {
        String wprespStatus = getWPRespFinishedStatus(a2lWpResponsibility.getA2lWpId(), a2lResp.getId());
        if (wprespStatus.equals(CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType())) {
          return ImageManager.getDecoratedImage(ImageKeys.WP_DEFN_USER_RESPONSIBLE, ImageKeys.ALL_8X8,
              IDecoration.BOTTOM_RIGHT);
        }
      }
    }

    return ImageManager.getInstance().getRegisteredImage(ImageKeys.WP_DEFN_USER_RESPONSIBLE);
  }

  private String getA2lResponsibilityTooltip(final A2lResponsibility a2lResp) {

    StringBuilder a2lRespToolTip = new StringBuilder();
    a2lRespToolTip.append("Responsible : ");
    a2lRespToolTip.append(a2lResp.getName());

    if (CommonUtils.isNotNull(this.cdrReportDataHandler)) {
      SortedSet<A2lWpResponsibility> a2lWpResponsibilities =
          this.cdrReportDataHandler.getA2lWPResponsibleMap().get(a2lResp.getName());

      if (CommonUtils.isNotNull(a2lWpResponsibilities)) {
        for (A2lWpResponsibility a2lWpResponsibility : a2lWpResponsibilities) {
          String wprespStatus = getWPRespFinishedStatus(a2lWpResponsibility.getA2lWpId(), a2lResp.getId());
          if (CommonUtils.isEqual(wprespStatus, CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType())) {
            a2lRespToolTip.append("\n");
            a2lRespToolTip.append(a2lWpResponsibility.getName());
            a2lRespToolTip.append(" - ");
            a2lRespToolTip.append(a2lResp.getName());
            a2lRespToolTip.append(" ");
            a2lRespToolTip.append("is Finished");
          }
        }
      }
    }

    return a2lRespToolTip.toString();
  }


  private String getA2lWpTooltip(final A2lWpResponsibility a2lWpResp) {

    StringBuilder a2lWpToolTip = new StringBuilder();
    a2lWpToolTip.append("Workpackage : ");
    a2lWpToolTip.append(a2lWpResp.getName());

    if (CommonUtils.isNotNull(this.cdrReportDataHandler)) {
      String respName = a2lWpResp.getMappedWpRespName();
      SortedSet<A2lWpResponsibility> a2lWpResponsibilities =
          this.cdrReportDataHandler.getA2lWPResponsibleMap().get(respName);

      A2lResponsibility a2lResponsibility = this.cdrReportDataHandler.getResponsibilityForRespName(respName);

      if (CommonUtils.isNotNull(a2lWpResponsibilities) && (a2lResponsibility != null)) {
        for (A2lWpResponsibility a2lWpResponsibility : a2lWpResponsibilities) {
          String wprespStatus = getWPRespFinishedStatus(a2lWpResponsibility.getA2lWpId(), a2lResponsibility.getId());
          if (CommonUtils.isEqual(wprespStatus, CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType()) &&
              a2lWpResponsibility.getName().equals(a2lWpResp.getName())) {
            a2lWpToolTip.append("\n");
            a2lWpToolTip.append(a2lWpResponsibility.getName());
            a2lWpToolTip.append(" - ");
            a2lWpToolTip.append(a2lWpResponsibility.getMappedWpRespName());
            a2lWpToolTip.append(" ");
            a2lWpToolTip.append("is Finished");
            break;
          }
        }
      }
    }

    return a2lWpToolTip.toString();
  }

  /**
   * @param a2lWpResponsibility
   * @return
   */
  private String getWPRespFinishedStatus(final Long wpId, final Long respId) {
    Map<Long, Map<Long, String>> wpFinishedStatusMap = this.cdrReportDataHandler.getWpFinishedStatusMap();
    String wpRespStatus = CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType();

    if (CommonUtils.isNotEmpty(wpFinishedStatusMap) && wpFinishedStatusMap.containsKey(wpId)) {
      Map<Long, String> respIdAndStatusMap = wpFinishedStatusMap.get(wpId);
      if (respIdAndStatusMap.containsKey(respId)) {
        wpRespStatus = respIdAndStatusMap.get(respId);
      }
    }

    return wpRespStatus;
  }

  /**
   * //Icdm-469 Icdm-586
   *
   * @param nodeImage
   * @param character
   * @return
   */
  private Image setImageForParam(final A2LParameter character) {
    Image img = null;
    if ("VALUE".equals(character.getType())) {
      img = ImageManager.getInstance().getRegisteredImage(ImageKeys.VALUE_16X16);
    }
    else if ("CURVE".equals(character.getType())) {
      img = ImageManager.getInstance().getRegisteredImage(ImageKeys.CURVE_16X16);
    }
    else if ("ASCII".equals(character.getType())) {
      img = ImageManager.getInstance().getRegisteredImage(ImageKeys.ASCII_16X16);
    }
    else if ("AXIS_PTS".equals(character.getType())) {
      img = ImageManager.getInstance().getRegisteredImage(ImageKeys.AXIS_16X16);
    }
    else if ("MAP".equals(character.getType())) {
      img = ImageManager.getInstance().getRegisteredImage(ImageKeys.MAP_16X16);
    }
    else if ("VAL_BLK".equals(character.getType())) {
      img = ImageManager.getInstance().getRegisteredImage(ImageKeys.VALBLK_16X16);
    }
    return img;
  }

  /**
   * icdm-272
   *
   * @param element
   * @param cellText
   * @return
   */
  private Image updateStringData(final Object element, final StyledString cellText) {
    Image nodeImage;
    cellText.append((String) element);
    final String rootStr = (String) element;

    if (rootStr.equals(this.pidcA2lBo.getA2LFileName())) {
      nodeImage = CommonUiUtils.getInstance().getImageForA2lFileNew(this.pidcA2lBo.getA2lFile());
    }
    else {
      if (CommonUtils.isNotEmpty(this.a2lwpInfoBO.getRespTypeAndRespMap().get(element)) ||
          CommonUtils.isNotEmpty(this.a2lwpInfoBO.getRespTypeAndRespMapForWpParam().get(element))) {
        nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.WP_DEFN_USER_RESP_TYPE);
      }
      else if (rootStr.equals(ApicConstants.BC_CONST) || rootStr.equals(ApicConstants.FC_CONST) ||
          rootStr.equals(ApicConstants.COMP) || rootStr.equals(ApicConstants.RULE_SET) ||
          rootStr.equals(ApicConstants.UNASSIGNED_PARAM) || rootStr.equals(ApicConstants.WP_RESPONSIBILITY) ||
          rootStr.equals(ApicConstants.A2L_WORK_PKG)) {
        nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.COMMON_28X30);
      }
      else if (rootStr.equals(this.a2lFileInfoBO.getWpRootGroupName())) {
        nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.WP_ROOT_28X30);
      }
      else if ((element instanceof String) &&
          this.a2lwpInfoBO.getA2lWpDefnModel().getA2lWpRespNodeMergedMap().containsKey(element)) {
        nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.WP_28X30);
      }
      else if (CommonUtils.isNotEqual(this.a2lFileInfoBO.getMappingSourceID(),
          this.a2lFileInfoBO.getA2lWpMapping().getGroupMappingId())) {
        nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNCTION_28X30);
      }
      else {
        nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_28X30);
      }
    }
    return nodeImage;
  }

  @Override
  public Image getImage(final Object element) {
    return null;
  }

  @Override
  public String getText(final Object element) {
    String text = null;
    // Return text based on element type
    if (element instanceof String) {
      text = element.toString();
    }
    // Responsible Node
    else if (element instanceof A2lResponsibility) {
      text = ((A2lResponsibility) element).getName();
    }
    else if (element instanceof A2lWpResponsibility) {
      text = ((A2lWpResponsibility) element).getName();
    }
    else if (element instanceof Function) {
      text = ((Function) element).getName();
    }
    else if (element instanceof A2LBaseComponents) {
      text = ((A2LBaseComponents) element).getBcName();
    }
    else if (element instanceof A2LBaseComponentFunctions) {
      text = ((A2LBaseComponentFunctions) element).getName();
    }
    else if (element instanceof A2lWorkPackageGroup) {
      text = ((A2lWorkPackageGroup) element).getGroupName();
    }
    // ICDM-209 and ICDM-210
    else if (element instanceof A2lWpObj) {
      text = ((A2lWpObj) element).getWpName();
    }
    else if (element instanceof A2LGroup) {
      text = ((A2LGroup) element).getGroupName();
    }
    else if (element instanceof Group) {
      text = ((Group) element).getName();
    }
    else if (element instanceof Characteristic) {
      text = ((Characteristic) element).getName();
    } // icdm-866
    else if (element instanceof CompPackage) {
      text = ((CompPackage) element).getName();
    }
    else if (element instanceof RuleSet) {
      text = ((RuleSet) element).getName();
    }
    return text;
  }

  /**
   * {@inheritDoc}
   */
  // ICDM 542
  @Override
  public String getToolTipText(final Object element) {
    String tooltip = null;
    if (element instanceof String) {
      tooltip = getDescForStringElement((String) element);
    }
    // Responsible Node
    else if (element instanceof A2lResponsibility) {
      A2lResponsibility a2lResponsibility = (A2lResponsibility) element;
      tooltip = getA2lResponsibilityTooltip(a2lResponsibility);
    }
    else if (element instanceof A2lWpResponsibility) {
      A2lWpResponsibility a2lWpResponsibility = (A2lWpResponsibility) element;
      tooltip = getA2lWpTooltip(a2lWpResponsibility);
    }
    else if (element instanceof Function) {
      tooltip = ((Function) element).getLongIdentifier();
    }
    else if (element instanceof A2LBaseComponents) {
      tooltip = ((A2LBaseComponents) element).getLongName();
    }
    else if (element instanceof A2LBaseComponentFunctions) {
      tooltip = ((A2LBaseComponentFunctions) element).getLongidentifier();
    }
    else if (element instanceof A2lWorkPackageGroup) {
      tooltip = ((A2lWorkPackageGroup) element).getGroupName();
    }
    else if (element instanceof A2lWpObj) {
      tooltip = ((A2lWpObj) element).getWpName();
    }
    else if (element instanceof A2LGroup) {
      tooltip = ((A2LGroup) element).getGroupLongName();
    }
    else if (element instanceof Group) {
      tooltip = ((Group) element).getLongIdentifier();
    }
    else if (element instanceof Characteristic) {
      tooltip = ((Characteristic) element).getLongIdentifier();
    } // icdm-866
    else if (element instanceof CompPackage) {
      tooltip = ((CompPackage) element).getDescription();
    }
    else if (element instanceof RuleSet) {
      tooltip = ((RuleSet) element).getDescription();
    }
    return tooltip;

  }

  /**
   * @param element
   */
  private String getDescForStringElement(final String element) {
    String desc = null;
    if (element.equals(ApicConstants.BC_CONST)) {
      desc = ApicConstants.BC_DESC;
    }
    else if (element.equals(ApicConstants.FC_CONST)) {
      desc = ApicConstants.FC_DESC;
    }
    else if (element.equals(ApicConstants.COMP)) {
      desc = ApicConstants.COMP_DESC;
    }
    else if (element.equals(ApicConstants.RULE_SET)) {
      desc = ApicConstants.RULE_SET;
    }
    else if (element.equals(ApicConstants.A2L_WORK_PKG)) {
      desc = ApicConstants.A2L_WORK_PKG;
    }
    else if (element.equals(ApicConstants.WP_RESPONSIBILITY)) {
      desc = ApicConstants.WP_RESPONSIBILITY;
    }
    else if (element.equals(ApicConstants.UNASSIGNED_PARAM)) {
      desc = ApicConstants.UNASSIGNED_PARAM_DESC;
    }
    else {
      desc = element;
    }
    return desc;
  }

}
