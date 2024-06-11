/**
 *
 */
package com.bosch.caltool.apic.ui.views.providers;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectObjectBO;
import com.bosch.caltool.icdm.client.bo.apic.PIDCDetailsNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.apic.ProjectAttributeUtil;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;

/**
 * Label provider for PIDC Details Tree
 *
 * @author adn1cob
 */
public class PIDCDetailsViewLabelProvider extends StyledCellLabelProvider implements ILabelProvider {

  /**
   * Instance of abstract project object bo
   */
  private final AbstractProjectObjectBO projObjBO;

  /**
   * @param projObjBO Abstract Project Handler
   */
  public PIDCDetailsViewLabelProvider(final AbstractProjectObjectBO projObjBO) {
    this.projObjBO = projObjBO;
  }

  /**
   * This method prepares the text and image for the PID tree nodes
   */
  @Override
  public void update(final ViewerCell cell) {
    // Get the element
    final Object element = cell.getElement();
    // ICDM 395
    // set the nodes atyle based on the type
    if (element instanceof PIDCDetailsNode) {
      setPDetNodeStyle(cell, (PIDCDetailsNode) element);
    }
    /* Project ID card */
    else if (element instanceof PidcVersion) {
      setPidcStyle(cell, (PidcVersion) element);
    }
    /* Variant */
    else if (element instanceof PidcVariant) {
      setVariantStyle(cell, (PidcVariant) element);
    }
    /* Sub Variant */
    else if (element instanceof PidcSubVariant) {
      setSVarStyle(cell, (PidcSubVariant) element);
    }
    super.update(cell);
  }

  /**
   * Set the style text details to cell
   *
   * @param cell ViewerCell
   * @param style StyledString
   */
  private void setCellStyle(final ViewerCell cell, final StyledString style) {
    cell.setText(style.toString());
    cell.setStyleRanges(style.getStyleRanges());

  }

  /**
   * Set the node properties for Sub variant
   *
   * @param cell ViewerCell
   * @param svar PIDCSubVariant
   */
  private void setSVarStyle(final ViewerCell cell, final PidcSubVariant svar) {
    final StyledString styStr = new StyledString();
    styStr.append(svar.getName());

    PidcSubVariantBO subVarBo =
        new PidcSubVariantBO(this.projObjBO.getPidcDataHandler().getPidcVersionInfo().getPidcVersion(), svar,
            this.projObjBO.getPidcDataHandler());

    if (svar.isDeleted()) {
      // set text in red for deleted
      cell.setForeground(
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
    }
    else {
      // set text in black for un-deleted
      cell.setForeground(
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK));
    }

    Image image = getVariantImage(null, svar.isDeleted(), hasUndefinedOrUnclearAttrs(subVarBo), ImageKeys.SUBVAR_28X30);
    // set sub-variant image
    cell.setImage(image);
    setCellStyle(cell, styStr);
  }

  /**
   * Set the node properties for variant
   *
   * @param cell
   * @param element
   * @param pidText
   */
  private void setVariantStyle(final ViewerCell cell, final PidcVariant variant) {
    final StyledString styStr = new StyledString();
    styStr.append(variant.getName());
    Image image = null;
    PidcVariantBO varBo = new PidcVariantBO(this.projObjBO.getPidcDataHandler().getPidcVersionInfo().getPidcVersion(),
        variant, this.projObjBO.getPidcDataHandler());
    // set red text for deleted variants
    if (variant.isDeleted()) {

      cell.setForeground(
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
    }
    else {

      cell.setForeground(
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK));
    }

    boolean hasUnclearOrUndefinedAttrs = hasUndefinedOrUnclearAttrs(varBo);

    // set selected object for filtering during loading of pidc editor
    setObjectForFiltering(variant, hasUnclearOrUndefinedAttrs);
    image = getVariantImage(image, variant.isDeleted(), hasUnclearOrUndefinedAttrs, ImageKeys.VARIANT_28X30);
    // set variant image
    cell.setImage(image);
    setCellStyle(cell, styStr);
  }

  private boolean hasUndefinedOrUnclearAttrs(final AbstractProjectObjectBO projObjBo) {
    boolean hasUnclrOrUndfndAttrs = false;

    boolean hasInvalidAttrValues = projObjBo.hasInvalidAttrValues();
    if (projObjBo.getPidcDataHandler().isQuotAttrWarningReq()) {
      hasUnclrOrUndfndAttrs = hasInvalidAttrValues || hasUndefinedQuotationAttr(projObjBo);
    }
    else if (projObjBo.getPidcDataHandler().isMandAttrWarningReq()) {
      hasUnclrOrUndfndAttrs = hasInvalidAttrValues || !projObjBo.isAllMandatoryAttrDefined();
    }
    else if (projObjBo.getPidcDataHandler().isMandAndUcAttrWarningReq()) {
      hasUnclrOrUndfndAttrs =
          hasInvalidAttrValues || !projObjBo.isAllMandatoryAttrDefined() || hasUndefinedUsecaseAttr(projObjBo);
    }
    return hasUnclrOrUndfndAttrs;
  }


  private void setObjectForFiltering(final Object projObj, final boolean hasUnclearOrUndefinedAttrs) {

    PidcVersionBO versionBo = (PidcVersionBO) (this.projObjBO);
    if ((projObj instanceof PidcVariant) && !((PidcVariant) projObj).isDeleted()) {
      for (PidcSubVariant subVar : this.projObjBO.getPidcDataHandler().getSubVariantMap().values()) {
        if (!subVar.isDeleted()) {
          PidcSubVariantBO subVarBo =
              new PidcSubVariantBO(this.projObjBO.getPidcDataHandler().getPidcVersionInfo().getPidcVersion(), subVar,
                  this.projObjBO.getPidcDataHandler());

          if (hasUndefinedOrUnclearAttrs(subVarBo) && (versionBo.getSelectedObjForFiltering() == null)) {
            versionBo.setSelectedObjForFiltering(subVar);
            break;
          }
        }
      }

      if (hasUnclearOrUndefinedAttrs && (versionBo.getSelectedObjForFiltering() == null)) {
        versionBo.setSelectedObjForFiltering(projObj);
      }
    }
  }

  /**
   * Set the node properties for PIDCard
   *
   * @param cell ViewerCell
   * @param pidcVersion PIDCVersion
   */
  private void setPidcStyle(final ViewerCell cell, final PidcVersion pidcVersion) {
    final StyledString styStr = new StyledString();


    // check this
    styStr.append(this.projObjBO.getPidcDataHandler().getPidcVersionInfo().getPidc().getName());
    styStr.append(" (" + pidcVersion.getVersionName() + ") ", StyledString.COUNTER_STYLER);
    Image image;
    if (this.projObjBO.getPidcDataHandler().getPidcVersionInfo().getPidc().isDeleted()) {
      image = ImageManager.getDecoratedImage(ImageKeys.PIDC_16X16, ImageKeys.PIDC_DEL_8X8, IDecoration.BOTTOM_RIGHT);
      cell.setForeground(
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
    }
    else {
      image = ImageManager.getInstance().getRegisteredImage(ImageKeys.PIDC_16X16);
      cell.setForeground(
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK));
    }
    // set pidc image
    cell.setImage(image);
    setCellStyle(cell, styStr);
  }

  /**
   * Set the node properties for PIDCDetailsNode
   *
   * @param cell ViewerCell
   * @param pdetNode PIDCDetailsNode
   */
  private void setPDetNodeStyle(final ViewerCell cell, final PIDCDetailsNode pdetNode) {
    final StyledString styStr = new StyledString();
    Image image = null;

    // value Undefined node
    if ((pdetNode.getType() == PIDCDetailsNode.NODE_TYPE.VAL_UNDEF) ||
        (pdetNode.getType() == PIDCDetailsNode.NODE_TYPE.VAL_NOTUSED)) {
      styStr.append(pdetNode.getName());
      image = ImageManager.getInstance().getRegisteredImage(ImageKeys.PDT_VAL_UDEF_28X30);
      setCellTextColor(cell, pdetNode.isDeleted(this.projObjBO));
    }
    /* Variant */
    else if (pdetNode.isVariantNode()) {
      final PidcVariant pidcVar = pdetNode.getPidcVariant();
      PidcVariantBO varBo = new PidcVariantBO(this.projObjBO.getPidcDataHandler().getPidcVersionInfo().getPidcVersion(),
          pidcVar, this.projObjBO.getPidcDataHandler());

      styStr.append(pidcVar.getName());

      image = getVariantImage(image, pidcVar.isDeleted(), hasUndefinedOrUnclearAttrs(varBo), ImageKeys.VARIANT_28X30);

      setCellTextColor(cell, pidcVar.isDeleted());

    }
    // Value node
    else {
      styStr.append(pdetNode.getName());
      image = ImageManager.getInstance().getRegisteredImage(ImageKeys.PDT_VAL_NOD_28X30);
      setCellTextColor(cell, pdetNode.isDeleted(this.projObjBO));
    }

    cell.setImage(image);
    setCellStyle(cell, styStr);
  }

  /**
   * @param image
   * @param pidcVar
   * @return
   */
  private Image getVariantImage(final Image image, final boolean isdeleted, final boolean hasUnclearOrUndefinedAttrs,
      final ImageKeys baseImgKey) {

    Image ret = image;
    if (baseImgKey != null) {
      ImageKeys[] overlayKeys = { null, null, null, null };

      if (isdeleted) {
        overlayKeys[0] = ImageKeys.RED_MARK_8X8;
      }
      // for unclear or missing values for mandatory or quotation attrs, show a warning overlay icon
      if (hasUnclearOrUndefinedAttrs) {
        overlayKeys[3] = ImageKeys.WARNING_12X12;
      }
      if (!CommonUtils.isNullOrEmpty(overlayKeys)) {
        ret = ImageManager.getMultipleDecoratedImage(baseImgKey, overlayKeys);

      }
      else {
        ret = ImageManager.getInstance().getRegisteredImage(baseImgKey);
      }

    }
    return ret;
  }

  private boolean hasUndefinedUsecaseAttr(final AbstractProjectObjectBO projectBo) {

    for (IProjectAttribute projAttr : projectBo.getAttributes().values()) {
      AbstractProjectAttributeBO projAttrBo =
          new ProjectAttributeUtil().getProjectAttributeHandler(projAttr, projectBo);
      if (projectBo.getPidcDataHandler().getProjectUsecaseModel().getProjectUsecaseAttrIdSet()
          .contains(projAttr.getAttrId()) &&
          !projectBo.getPidcDataHandler().getAttributeMap().get(projAttr.getAttrId()).isDeleted() &&
          projAttrBo.isProjAttrVisibleAtAllLevels(projAttr) && !projAttrBo.isValueDefined(projAttr)) {
        return true;
      }
    }
    return false;
  }

  private boolean hasUndefinedQuotationAttr(final AbstractProjectObjectBO projectBo) {

    for (IProjectAttribute projAttr : projectBo.getAttributes().values()) {
      AbstractProjectAttributeBO projAttrBo =
          new ProjectAttributeUtil().getProjectAttributeHandler(projAttr, projectBo);
      if (projAttrBo.isQuotationRelevant() &&
          !projectBo.getPidcDataHandler().getAttributeMap().get(projAttr.getAttrId()).isDeleted() &&
          projAttrBo.isProjAttrVisibleAtAllLevels(projAttr) && !projAttrBo.isValueDefined(projAttr)) {
        return true;
      }
    }
    return false;
  }

  /**
   * This Overridden method is mandatory implementation of ILabelProvider
   */
  @Override
  public Image getImage(final Object element) {
    return null;
  }

  @Override
  public String getText(final Object element) {
    if (element instanceof PIDCDetailsNode) { // Level nodes
      // check this
      return ((PIDCDetailsNode) element).getName();
    }
    // Return text based on element type
    if (element instanceof PidcVersion) { // ProjectID Card
      return ((PidcVersion) element).getPidcId().toString();
    }
    else if (element instanceof PidcVariant) { // PIDC Variant
      return ((PidcVariant) element).getName();
    }
    else if (element instanceof PidcSubVariant) { // PIDC SubVariant
      return ((PidcSubVariant) element).getName();
    }

    return null;

  }


  @Override
  public String getToolTipText(final Object element) {
    // tooltip for PIDCDetailsnode
    if (element instanceof PIDCDetailsNode) {
      return ((PIDCDetailsNode) element).getToolTip(this.projObjBO);
    }
    // tooltip for other objects
    if (element instanceof PidcVersion) { // ProjectID Card

      return this.projObjBO.getToolTip();
    }
    else if (element instanceof PidcVariant) { // PIDC Variant
      PidcVariantBO handler =
          new PidcVariantBO(this.projObjBO.getPidcDataHandler().getPidcVersionInfo().getPidcVersion(),
              (PidcVariant) element, this.projObjBO.getPidcDataHandler());
      return handler.getToolTip();
    }
    else if (element instanceof PidcSubVariant) { // PIDC SubVariant
      PidcSubVariantBO handler =
          new PidcSubVariantBO(this.projObjBO.getPidcDataHandler().getPidcVersionInfo().getPidcVersion(),
              (PidcSubVariant) element, this.projObjBO.getPidcDataHandler());
      return handler.getToolTip();

    }
    return "";
  }

  /**
   * Sets the color of the cell text.
   *
   * @param cell cell
   * @param isItemDeleted is Item in the cell Deleted
   */
  private void setCellTextColor(final ViewerCell cell, final boolean isItemDeleted) {
    // deleted nodes are displayed in Red
    if (isItemDeleted) {
      cell.setForeground(
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
    }
    // other nodes are displayed in black
    else {
      cell.setForeground(
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK));
    }
  }
}
