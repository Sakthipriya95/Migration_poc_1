/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.views.providers;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRResultFunction;
import com.bosch.caltool.icdm.model.cdr.RvwResultWPandRespModel;

/**
 * @author say8cob
 */
public class ReviewResultOutlineTreeLabelProvider extends StyledCellLabelProvider implements ILabelProvider {

  private final ReviewResultBO reviewResultBO;

  /**
   * @param editorInput
   */
  public ReviewResultOutlineTreeLabelProvider(final ReviewResultBO reviewResultBO) {
    this.reviewResultBO = reviewResultBO;
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
    Image nodeImage = null;
    if ((element instanceof String) && (((String) element).equals(ApicConstants.A2L_WORK_PKG) ||
        ((String) element).equals(ApicConstants.WP_RESPONSIBILITY) ||
        ((String) element).equals(ApicConstants.FC_CONST))) {
      cellText.append((String) element);
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.COMMON_28X30);
    }
    else if (element instanceof RvwResultWPandRespModel) {
      RvwResultWPandRespModel resultWPandRespModel = (RvwResultWPandRespModel) element;
      cellText.append(resultWPandRespModel.getA2lWorkPackage().getName());
      nodeImage = fetchWPNodeImage(resultWPandRespModel);
    }
    else if (element instanceof A2lResponsibility) {
      A2lResponsibility a2lResponsibility = (A2lResponsibility) element;
      cellText.append(a2lResponsibility.getName());
      nodeImage = fetchA2lRespNodeImage(a2lResponsibility);
    }
    else if ((element instanceof String) && this.reviewResultBO.getRespTypeAndRespMap().containsKey(element)) {
      cellText.append((String) element);
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.WP_DEFN_USER_RESP_TYPE);
    }
    else if (element instanceof CDRResultFunction) {
      CDRResultFunction function = (CDRResultFunction) element;
      String functionName = function.getFunctionVers() != null
          ? function.getName().concat(" (" + function.getFunctionVers() + ")") : function.getName();
      cellText.append(functionName);
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNCTION_28X30);
    }
    else {
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_28X30);
    }
    return nodeImage;
  }

  /**
   * @param nodeImage
   * @param a2lResponsibility
   * @return
   */
  private Image fetchA2lRespNodeImage(final A2lResponsibility a2lResponsibility) {
    Map<Long, String> wpStatusMap = this.reviewResultBO.getReviewResultClientBO().getResponse()
        .getRespWpFinishedStatusMap().get(a2lResponsibility.getId());

    if (wpStatusMap != null) {
      for (RvwResultWPandRespModel rvwResultWPandRespModel : this.reviewResultBO.getReviewResultClientBO().getResponse()
          .getA2lWpMap().get(a2lResponsibility.getName())) {
        String wpStatus = wpStatusMap.get(rvwResultWPandRespModel.getA2lWorkPackage().getId());
        if (CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType().equals(wpStatus)) {
          return ImageManager.getDecoratedImage(ImageKeys.WP_DEFN_USER_RESPONSIBLE, ImageKeys.ALL_8X8,
              IDecoration.BOTTOM_RIGHT);
        }
      }
    }

    return ImageManager.getInstance().getRegisteredImage(ImageKeys.WP_DEFN_USER_RESPONSIBLE);
  }

  /**
   * @param nodeImage
   * @param resultWPandRespModel
   * @return
   */
  private Image fetchWPNodeImage(final RvwResultWPandRespModel resultWPandRespModel) {
    if (CommonUtils.isNotNull(resultWPandRespModel.getA2lResponsibility())) {
      Map<Long, String> wpStatusMap = this.reviewResultBO.getReviewResultClientBO().getResponse()
          .getRespWpFinishedStatusMap().get(resultWPandRespModel.getA2lResponsibility().getId());
      if (wpStatusMap != null) {
        for (Entry<Long, String> wpStatus : wpStatusMap.entrySet()) {
          if (CommonUtils.isEqual(resultWPandRespModel.getA2lWorkPackage().getId(), wpStatus.getKey()) &&
              CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType().equals(wpStatus.getValue())) {
            return ImageManager.getDecoratedImage(ImageKeys.WP_28X30, ImageKeys.ALL_8X8, IDecoration.BOTTOM_RIGHT);
          }
        }
      }
    }
    return ImageManager.getInstance().getRegisteredImage(ImageKeys.WP_28X30);
  }

  private String getWpToolTip(final RvwResultWPandRespModel resultWPandRespModel) {
    StringBuilder wpTooltip = new StringBuilder();
    wpTooltip.append("Workpackage : ");
    wpTooltip.append(resultWPandRespModel.getA2lWorkPackage().getName());
    if (CommonUtils.isNotNull(resultWPandRespModel.getA2lResponsibility())) {
      Map<Long, String> wpStatusMap = this.reviewResultBO.getReviewResultClientBO().getResponse()
          .getRespWpFinishedStatusMap().get(resultWPandRespModel.getA2lResponsibility().getId());
      if (wpStatusMap != null) {
        for (Entry<Long, String> wpStatus : wpStatusMap.entrySet()) {
          if (CommonUtils.isEqual(resultWPandRespModel.getA2lWorkPackage().getId(), wpStatus.getKey()) &&
              CommonUtils.isEqual(CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType(), wpStatus.getValue())) {
            wpTooltip.append("\n");
            wpTooltip.append(resultWPandRespModel.getA2lWorkPackage().getName());
            wpTooltip.append(" - ");
            wpTooltip.append(resultWPandRespModel.getA2lResponsibility().getName());
            wpTooltip.append(" ");
            wpTooltip.append("is Finished");
            break;
          }
        }
      }
    }
    return wpTooltip.toString();
  }

  private String getRespToolTip(final A2lResponsibility a2lResponsibility) {
    StringBuilder wpTooltip = new StringBuilder();
    wpTooltip.append("Responsible : ");
    wpTooltip.append(a2lResponsibility.getName());
    Map<Long, String> wpStatusMap = this.reviewResultBO.getReviewResultClientBO().getResponse()
        .getRespWpFinishedStatusMap().get(a2lResponsibility.getId());

    if (wpStatusMap != null) {

      for (RvwResultWPandRespModel rvwResultWPandRespModel : this.reviewResultBO.getReviewResultClientBO().getResponse()
          .getA2lWpMap().get(a2lResponsibility.getName())) {

        String wpStatus = wpStatusMap.get(rvwResultWPandRespModel.getA2lWorkPackage().getId());

        if (wpStatus.equals(CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType())) {

          wpTooltip.append("\n");
          wpTooltip.append(rvwResultWPandRespModel.getA2lWorkPackage().getName());
          wpTooltip.append(" - ");
          wpTooltip.append(a2lResponsibility.getName());
          wpTooltip.append(" ");
          wpTooltip.append("is Finished");
        }
      }
    }

    return wpTooltip.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Image getImage(final Object arg0) {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getText(final Object element) {
    String text = null;
    // Return text based on element type
    if ((element instanceof String) && ((String) element).equals(ApicConstants.A2L_WORK_PKG)) {
      text = ApicConstants.A2L_WORK_PKG;
    }
    // Resp node
    else if ((element instanceof String) && ((String) element).equals(ApicConstants.WP_RESPONSIBILITY)) {
      text = ApicConstants.WP_RESPONSIBILITY;
    }
    else if ((element instanceof String) && ((String) element).equals(ApicConstants.FC_CONST)) {
      text = ApicConstants.FC_CONST;
    }
    // Work Package
    else if (element instanceof RvwResultWPandRespModel) {
      text = ((RvwResultWPandRespModel) element).getA2lWorkPackage().getName();
    }
    else if (element instanceof CDRResultFunction) {
      CDRResultFunction function = (CDRResultFunction) element;
      text = function.getFunctionVers() != null ? function.getName().concat(" (" + function.getFunctionVers() + ")")
          : function.getName();
    }
    // Resposible
    else if (element instanceof A2lResponsibility) {
      text = ((A2lResponsibility) element).getName();
    }
    // Responsibilty Type
    else if ((element instanceof String) && this.reviewResultBO.getRespTypeAndRespMap().containsKey(element)) {
      text = (String) element;
    }
    return text;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText(final Object element) {
    String text = "";
    if (element instanceof RvwResultWPandRespModel) {
      text = getWpToolTip((RvwResultWPandRespModel) element);
    } // Resposible
    else if (element instanceof A2lResponsibility) {
      text = getRespToolTip((A2lResponsibility) element);
    }
    else if (element instanceof CDRResultFunction) {
      CDRResultFunction function = (CDRResultFunction) element;
      text = function.getFunctionVers() != null ? function.getName().concat(" (" + function.getFunctionVers() + ")")
          : function.getName();
    }
    else if ((element instanceof String) && ((String) element).equals(ApicConstants.A2L_WORK_PKG)) {
      text = ApicConstants.A2L_WORK_PKG;
    }
    // Resp node
    else if ((element instanceof String) && ((String) element).equals(ApicConstants.WP_RESPONSIBILITY)) {
      text = ApicConstants.WP_RESPONSIBILITY;
    }
    else if ((element instanceof String) && ((String) element).equals(ApicConstants.FC_CONST)) {
      text = ApicConstants.FC_CONST;
    }
    // Responsibilty Type
    else if ((element instanceof String) && this.reviewResultBO.getRespTypeAndRespMap().containsKey(element)) {
      text = (String) element;
    }
    return text;
  }

}
