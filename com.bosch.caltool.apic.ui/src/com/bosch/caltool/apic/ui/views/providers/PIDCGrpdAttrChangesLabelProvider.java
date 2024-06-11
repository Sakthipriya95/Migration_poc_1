/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue;
import com.bosch.caltool.icdm.model.apic.pidc.GroupdAttrPredefAttrModel;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Label Provider for PIDCGrpdAttrChangesDialog table viewer
 *
 * @author dmo5cob
 */
public class PIDCGrpdAttrChangesLabelProvider implements ITableLabelProvider, ITableColorProvider {


  /**
   * Constant
   */
  private static final String PIDC_LEVEL = "PIDC Level";
  /**
   * Constant
   */
  private static final String INVISIBLE = "Invisible";
  /**
   * Constant
   */
  private static final String VARIANT_LEVEL = "Variant Level";
  /**
   * Constant
   */
  private static final String SUB_VARIANT_LEVEL = "SubVariant Level";
  private int columnIdx;
  private final IProjectAttribute grpAttr;
  private final PidcVersion pidcVersion;
  private final List<GroupdAttrPredefAttrModel> grpAttrPredefAttrList;
  private final Map<IProjectAttribute, Long> grpAttrValMap;
  private final Map<PredefinedAttrValue, String> attrValPidcValMap;
  private final Map<PredefinedAttrValue, String> attrValPidcLvlMap;
  private final PidcDataHandler pidcDataHandler;

  /**
   * @param pidcAttr PIDC Attribute selected
   * @param pidcVersion PIDC version selected
   * @param grpAttrPredefAttrList Collection of GroupdAttrPredefAttrModel
   * @param grpAttrValMap
   * @param attrValPidcLvlMap
   * @param attrValPidcValMap
   * @param pidcDataHandler
   */
  public PIDCGrpdAttrChangesLabelProvider(final IProjectAttribute pidcAttr, final PidcVersion pidcVersion,
      final List<GroupdAttrPredefAttrModel> grpAttrPredefAttrList, final Map<IProjectAttribute, Long> grpAttrValMap,
      final Map<PredefinedAttrValue, String> attrValPidcValMap,
      final Map<PredefinedAttrValue, String> attrValPidcLvlMap, final PidcDataHandler pidcDataHandler) {
    this.grpAttr = pidcAttr;
    this.pidcVersion = pidcVersion;
    this.grpAttrPredefAttrList = grpAttrPredefAttrList;
    this.grpAttrValMap = grpAttrValMap;
    this.attrValPidcValMap = attrValPidcValMap;
    this.attrValPidcLvlMap = attrValPidcLvlMap;
    this.pidcDataHandler = pidcDataHandler;
  }


  /**
   * @param element instance
   * @return tooltip
   */
  public String getToolTipText(final Object element) {
    if (element instanceof PredefinedAttrValue) {
      // get the tool tip text
      return getDetailTxt(element, this.columnIdx);
    }
    return "";
  }


  @Override
  public boolean isLabelProperty(final Object element, final String property) {
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getColumnText(final Object element, final int columnIndex) {
    String result = "";
    if (element instanceof AttributeValue) {
      // get the table column text
      result = getGrpdAttrTxt(element, columnIndex, this.grpAttr);
    }
    if (element instanceof IProjectAttribute) {
      if (this.grpAttrValMap.keySet().contains(element)) {
        result = getGrpdAttrTxt(((IProjectAttribute) element).getValue(), columnIndex, (IProjectAttribute) element);
      }
      else {
        result = getDetailTxtMulti(element, columnIndex);
      }
    }
    if (element instanceof PredefinedAttrValue) {
      // get the table column text
      result = getDetailTxt(element, columnIndex);
    }

    return result;
  }

  // ICDM-2626 (method for opening a PIDC, show grp attr info icon )
  /**
   * @param element
   * @param columnIndex
   * @return
   */
  private String getDetailTxtMulti(final Object element, final int columnIndex) {
    String result;
    final IProjectAttribute ipidcPredefAttr = (IProjectAttribute) element;

    this.columnIdx = columnIndex;

    switch (columnIndex) {
      case CommonUIConstants.COLUMN_INDEX_0:
        result = CommonUtils.checkNull(ipidcPredefAttr.getName());
        break;
      case CommonUIConstants.COLUMN_INDEX_1:
        result = getValueMulti(ipidcPredefAttr);
        break;
      case CommonUIConstants.COLUMN_INDEX_2:
        result = getPredefValMulti(ipidcPredefAttr);
        break;
      case CommonUIConstants.COLUMN_INDEX_3:
        result = getPredefLevelMulti(ipidcPredefAttr);

        break;
      default:
        result = "";
        break;
    }
    return result;

  }

  // ICDM-2626 (method for opening a PIDC, show grp attr info icon )
  /**
   * @param ipidcPredefAttr
   * @return
   */
  private String getPredefValMulti(final IProjectAttribute ipidcPredefAttr) {
    String result = "";

    for (GroupdAttrPredefAttrModel model : this.grpAttrPredefAttrList) {

      for (Entry<IProjectAttribute, PredefinedAttrValue> modelElemnt : model.getPredefAttrValMap().entrySet()) {

        if ((modelElemnt.getKey() instanceof PidcVersionAttribute) &&
            (ipidcPredefAttr instanceof PidcVersionAttribute)) {
          PidcVersionAttribute predefAattr = (PidcVersionAttribute) ipidcPredefAttr;
          PidcVersionAttribute modeAttr = (PidcVersionAttribute) modelElemnt.getKey();
          if (predefAattr.equals(modeAttr)) {
            if (null != modelElemnt.getValue().getPredefinedValue()) {
              result = null == modelElemnt.getValue().getPredefinedValue() ? ""
                  : modelElemnt.getValue().getPredefinedValue();
            }
            else {
              result = "";
            }
          }
        }
        else if ((modelElemnt.getKey() instanceof PidcVariantAttribute) &&
            (ipidcPredefAttr instanceof PidcVariantAttribute)) {
          PidcVariantAttribute predefAattr = (PidcVariantAttribute) ipidcPredefAttr;
          PidcVariantAttribute modeAttr = (PidcVariantAttribute) modelElemnt.getKey();
          if (predefAattr.equals(modeAttr)) {
            if (null != modelElemnt.getValue().getPredefinedValue()) {
              result = null == modelElemnt.getValue().getPredefinedValue() ? ""
                  : modelElemnt.getValue().getPredefinedValue();
            }
            else {
              result = "";
            }
          }
        }
        else if ((modelElemnt.getKey() instanceof PidcSubVariantAttribute) &&
            (ipidcPredefAttr instanceof PidcSubVariantAttribute)) {
          PidcSubVariantAttribute predefAattr = (PidcSubVariantAttribute) ipidcPredefAttr;
          PidcSubVariantAttribute modeAttr = (PidcSubVariantAttribute) modelElemnt.getKey();
          if (predefAattr.equals(modeAttr)) {
            if (null != modelElemnt.getValue().getPredefinedValue()) {
              result = null == modelElemnt.getValue().getPredefinedValue() ? ""
                  : modelElemnt.getValue().getPredefinedValue();
            }
            else {
              result = "";
            }
          }
        }

      }
    }
    return result;
  }

  // ICDM-2626 (method for opening a PIDC, show grp attr info icon )
  /**
   * @param ipidcPredefAttr
   * @return
   */
  private String getPredefLevelMulti(final IProjectAttribute ipidcPredefAttr) {
    String result = "";
    IProjectAttribute groupedAttr = null;
    for (GroupdAttrPredefAttrModel model : this.grpAttrPredefAttrList) {

      for (Entry<IProjectAttribute, PredefinedAttrValue> modelElemnt : model.getPredefAttrValMap().entrySet()) {

        if (modelElemnt.getKey().equals(ipidcPredefAttr)) {
          groupedAttr = model.getGroupedAttribute();
          break;
        }
      }
    }
    if (null != groupedAttr) {
      if (this.pidcDataHandler.getPidcVersInvisibleAttrSet().contains(ipidcPredefAttr.getAttrId()) ||
          this.pidcDataHandler.getVariantInvisbleAttributeMap().values().contains(ipidcPredefAttr.getAttrId()) ||
          this.pidcDataHandler.getSubVariantInvisbleAttributeMap().values().contains(ipidcPredefAttr.getAttrId())) {
        result = INVISIBLE;
      }
      else {
        // Grp attr at PIDC level
        if (groupedAttr instanceof PidcVersionAttribute) {
          if (ipidcPredefAttr instanceof PidcVariantAttribute) {
            result = VARIANT_LEVEL + " : " + "* To be moved to PIDC Level";
          }
          else if (ipidcPredefAttr instanceof PidcSubVariantAttribute) {
            result = SUB_VARIANT_LEVEL + " : " + "* To be moved to PIDC Level";
          }
          else {
            result = PIDC_LEVEL;
          }

        } // Grp attr at variant level
        else if (groupedAttr instanceof PidcVariantAttribute) {
          if (ipidcPredefAttr instanceof PidcVersionAttribute) {
            result = PIDC_LEVEL + "->" + VARIANT_LEVEL;
          }
          else if (ipidcPredefAttr instanceof PidcSubVariantAttribute) {

            result = SUB_VARIANT_LEVEL + " : " + "* To be moved to Variant Level";
          }
          else if (ipidcPredefAttr instanceof PidcVariantAttribute) {
            result = VARIANT_LEVEL;
          }
        }

        // Grp attr at subvariant level
        else if (groupedAttr instanceof PidcSubVariantAttribute) {

          if (ipidcPredefAttr instanceof PidcVersionAttribute) {
            result = PIDC_LEVEL + "->" + SUB_VARIANT_LEVEL;
          }
          else if (ipidcPredefAttr instanceof PidcVariantAttribute) {

            result = VARIANT_LEVEL + " -> " + SUB_VARIANT_LEVEL;
          }
          else if (ipidcPredefAttr instanceof PidcSubVariantAttribute) {
            result = SUB_VARIANT_LEVEL;
          }
        }
      }
    }
    return result;

  }

  // ICDM-2626 (method for dialog when opening a PIDC, show grp attr info icon )
  /**
   * @param ipidcPredefAttr
   * @return
   */
  private String getValueMulti(final IProjectAttribute ipidcPredefAttr) {
    String result = "";

    if (ipidcPredefAttr instanceof PidcVersionAttribute) {
      result = null == ((PidcVersionAttribute) ipidcPredefAttr).getValue() ? ""
          : ((PidcVersionAttribute) ipidcPredefAttr).getValue();
    }
    else if (ipidcPredefAttr instanceof PidcVariantAttribute) {
      result = null == ((PidcVariantAttribute) ipidcPredefAttr).getValue() ? ""
          : ((PidcVariantAttribute) ipidcPredefAttr).getValue();
    }
    else if (ipidcPredefAttr instanceof PidcSubVariantAttribute) {
      result = null == ((PidcSubVariantAttribute) ipidcPredefAttr).getValue() ? ""
          : ((PidcSubVariantAttribute) ipidcPredefAttr).getValue();
    }
    return result;
  }


  /**
   * @param element
   * @param columnIndex
   * @param grpdAttr
   * @return
   */
  private String getGrpdAttrTxt(final Object element, final int columnIndex, final IProjectAttribute grpdAttr) {
    String result = "";

    this.columnIdx = columnIndex;

    switch (columnIndex) {
      // Attribute Name
      case CommonUIConstants.COLUMN_INDEX_0:
        result = CommonUtils.checkNull(grpdAttr.getName());
        break;
      // Value in PIDC
      case CommonUIConstants.COLUMN_INDEX_1:
        result = getValuePidc(grpdAttr);
        break;
      // New Value
      case CommonUIConstants.COLUMN_INDEX_2:
        if (element instanceof AttributeValue) {
          result = ((AttributeValue) element).getName();
        }
        break;
      // PIDC Level
      case CommonUIConstants.COLUMN_INDEX_3:
        result = getLevel(grpdAttr);

        break;
      default:
        result = "";
        break;
    }
    return result;

  }

  /**
   * @param grpdAttr
   * @param result
   * @return
   */
  private String getLevel(final IProjectAttribute grpdAttr) {

    String result = "";
    if (this.pidcDataHandler.getPidcVersInvisibleAttrSet().contains(grpdAttr.getAttrId()) ||
        this.pidcDataHandler.getVariantInvisbleAttributeMap().values().contains(grpdAttr.getAttrId()) ||
        this.pidcDataHandler.getSubVariantInvisbleAttributeMap().values().contains(grpdAttr.getAttrId())) {
      result = INVISIBLE;
    }
    else {
      if (grpdAttr instanceof PidcVersionAttribute) {
        result = PIDC_LEVEL;
      }
      else if (grpdAttr instanceof PidcVariantAttribute) {
        PidcVariantAttribute grpAttrVar = (PidcVariantAttribute) grpdAttr;
        StringBuilder variantLvl = new StringBuilder();
        variantLvl.append(VARIANT_LEVEL);
        variantLvl.append(" ( ");
        variantLvl.append(grpAttrVar.getVariantName());
        variantLvl.append(" )");
        result = variantLvl.toString();
      }
      else if (grpdAttr instanceof PidcSubVariantAttribute) {
        PidcSubVariantAttribute grpAttrSubVar = (PidcSubVariantAttribute) grpdAttr;
        StringBuilder subVariantLvl = new StringBuilder();
        subVariantLvl.append(SUB_VARIANT_LEVEL);
        subVariantLvl.append(" ( ");
        subVariantLvl.append(grpAttrSubVar.getVariantName());
        subVariantLvl.append(" : ");
        subVariantLvl.append(grpAttrSubVar.getSubVariantName());
        subVariantLvl.append(" )");
        result = subVariantLvl.toString();
      }
    }

    return result;
  }

  // ICDM-2625
  /**
   * @param attribute PIDC attribute
   * @return PIDC attribute value
   */
  private String getValuePidc(final IProjectAttribute pidcAttr) {
    PidcVersionBO versionHandler = new PidcVersionBO(this.pidcVersion, this.pidcDataHandler);
    if (pidcAttr instanceof PidcVersionAttribute) {

      return new PidcVersionAttributeBO((PidcVersionAttribute) pidcAttr, versionHandler)
          .getDefaultValueDisplayName(false);
    }
    else if (pidcAttr instanceof PidcVariantAttribute) {
      PidcVariantAttribute varAttr = (PidcVariantAttribute) pidcAttr;
      PidcVariantBO varHandler = new PidcVariantBO(this.pidcVersion,
          this.pidcDataHandler.getVariantMap().get(varAttr.getVariantId()), this.pidcDataHandler);
      return new PidcVariantAttributeBO(varAttr, varHandler).getDefaultValueDisplayName(false);
    }
    else if (pidcAttr instanceof PidcSubVariantAttribute) {
      PidcSubVariantAttribute subvarAttr = (PidcSubVariantAttribute) pidcAttr;
      PidcSubVariantBO subVarHandler = new PidcSubVariantBO(this.pidcVersion,
          this.pidcDataHandler.getSubVariantMap().get(subvarAttr.getSubVariantId()), this.pidcDataHandler);
      return new PidcSubVariantAttributeBO(subvarAttr, subVarHandler).getDefaultValueDisplayName(false);
    }

    return ApicConstants.EMPTY_STRING;
  }


  /**
   * @param element
   * @param columnIndex
   * @return
   */
  private String getDetailTxt(final Object element, final int columnIndex) {
    String result;
    final PredefinedAttrValue predfndAttrVal = (PredefinedAttrValue) element;
    this.columnIdx = columnIndex;
    switch (columnIndex) {
      case CommonUIConstants.COLUMN_INDEX_0:
        result = CommonUtils.checkNull(predfndAttrVal.getPredefinedAttrName());
        break;
      case CommonUIConstants.COLUMN_INDEX_1:
        result = this.attrValPidcValMap.get(predfndAttrVal);
        break;
      case CommonUIConstants.COLUMN_INDEX_2:
        result = casePredeAttrVal(predfndAttrVal);
        break;
      case CommonUIConstants.COLUMN_INDEX_3:
        result = this.attrValPidcLvlMap.get(predfndAttrVal);
        break;
      default:
        result = "";
        break;
    }
    return result;
  }


  /**
   * @param predfndAttrVal
   * @return
   */
  private String casePredeAttrVal(final PredefinedAttrValue predfndAttrVal) {
    String result;
    if (null != predfndAttrVal.getPredefinedValue()) {
      result = CommonUtils.checkNull(predfndAttrVal.getPredefinedValue());
    }
    else {
      result = "";
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addListener(final ILabelProviderListener iListner) {
    // Not Applicable

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // Not Applicable

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Image getColumnImage(final Object arg0, final int arg1) {

    if (arg1 == CommonUIConstants.COLUMN_INDEX_4) {

      if (arg0 instanceof AttributeValue) {
        if (checkWriteAccess()) {
          return ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_READONLY_16X16);
        }

        for (String predfnAttrLvl : this.attrValPidcLvlMap.values()) {
          if (predfnAttrLvl.contains("*")) {
            return ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_READONLY_16X16);
          }
        }
        return ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16);
      }
      else if (arg0 instanceof PidcVersionAttribute) {
        if (checkWriteAccess()) {
          return ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_READONLY_16X16);
        }
        PidcVersionAttribute pidcAttr = (PidcVersionAttribute) arg0;
        if (this.grpAttrValMap.keySet().contains(pidcAttr)) {
          if (this.grpAttrValMap.containsKey(pidcAttr)) {

            if (getGrpAttrChildLevelMulti(pidcAttr)) {

              return ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_READONLY_16X16);
            }
            return ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16);
          }
        }
      }
      else if (arg0 instanceof PidcVariantAttribute) {
        if (checkWriteAccess()) {
          return ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_READONLY_16X16);
        }
        PidcVariantAttribute pidcAttrVar = (PidcVariantAttribute) arg0;
        if (this.grpAttrValMap.keySet().contains(pidcAttrVar)) {


          if (getGrpAttrChildLevelMulti(pidcAttrVar)) {

            return ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_READONLY_16X16);
          }
          return ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16);
        }

      }
      else if (arg0 instanceof PidcSubVariantAttribute) {
        if (checkWriteAccess()) {
          return ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_READONLY_16X16);
        }
        PidcSubVariantAttribute pidcAttrSubVar = (PidcSubVariantAttribute) arg0;
        if (this.grpAttrValMap.keySet().contains(pidcAttrSubVar)) {
          if (this.grpAttrValMap.containsKey(pidcAttrSubVar)) {

            if (getGrpAttrChildLevelMulti(pidcAttrSubVar)) {

              return ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_READONLY_16X16);
            }
            return ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16);
          }
        }
      }
    }
    return null;
  }


  /**
   * returns if the user has write access on the pidcversion
   */
  private boolean checkWriteAccess() {

    NodeAccess access;
    try {
      access = new CurrentUserBO().getNodeAccessRight(this.pidcVersion.getPidcId());


      if ((null != access) && !access.isWrite()) {
        return true;
      }
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
    }
    return false;
  }

  /**
   * @param pidcAttrSubVar
   * @return
   */
  private Boolean getGrpAttrChildLevelMulti(final IProjectAttribute ipidcGrpAttr) {

    IProjectAttribute groupedAttr = null;
    Map<IProjectAttribute, PredefinedAttrValue> predefAttrMap = new ConcurrentHashMap<>();
    for (GroupdAttrPredefAttrModel model : this.grpAttrPredefAttrList) {

      if (model.getGroupedAttribute().equals(ipidcGrpAttr)) {
        groupedAttr = model.getGroupedAttribute();
        predefAttrMap = model.getPredefAttrValMap();
      }
    }
    if ((null != groupedAttr) && (null != predefAttrMap)) {
      for (IProjectAttribute ipidcPredefAttr : predefAttrMap.keySet()) {
        String result = getPredefLevelMulti(ipidcPredefAttr);
        if (result.contains("*")) {
          return true;
        }
      }
    }

    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void removeListener(final ILabelProviderListener ilabelproviderlistener) {
    // TODO Auto-generated method stub

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Color getForeground(final Object obj, final int i) {

    if ((obj instanceof PredefinedAttrValue) && (i == CommonUIConstants.COLUMN_INDEX_3)) {

      String result = this.attrValPidcLvlMap.get(obj);
      if (result.contains("*")) {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay()
            .getSystemColor(SWT.COLOR_RED);
      }
    }
    else if ((obj instanceof PidcVersionAttribute) && (i == CommonUIConstants.COLUMN_INDEX_3)) {
      PidcVersionAttribute pidcAttr = (PidcVersionAttribute) obj;
      if (!this.grpAttrValMap.containsKey(pidcAttr)) {
        String result = getPredefLevelMulti(pidcAttr);
        if (result.contains("*")) {

          return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay()
              .getSystemColor(SWT.COLOR_RED);
        }
      }
    }
    else if ((obj instanceof PidcVariantAttribute) && (i == CommonUIConstants.COLUMN_INDEX_3)) {
      PidcVariantAttribute pidcAttrVar = (PidcVariantAttribute) obj;
      if (!this.grpAttrValMap.containsKey(pidcAttrVar)) {
        String result = getPredefLevelMulti(pidcAttrVar);
        if (result.contains("*")) {

          return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay()
              .getSystemColor(SWT.COLOR_RED);
        }
      }
    }
    else if ((obj instanceof PidcSubVariantAttribute) && (i == CommonUIConstants.COLUMN_INDEX_3)) {
      PidcSubVariantAttribute pidcAttrSubVar = (PidcSubVariantAttribute) obj;
      if (!this.grpAttrValMap.containsKey(pidcAttrSubVar)) {
        String result = getPredefLevelMulti(pidcAttrSubVar);
        if (result.contains("*")) {

          return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay()
              .getSystemColor(SWT.COLOR_RED);
        }
      }
    }
    else if ((obj instanceof IProjectAttribute) && (i == CommonUIConstants.COLUMN_INDEX_3)) {
      String result = getPredefLevelMulti((IProjectAttribute) obj);
      if (result.contains("*")) {

        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay()
            .getSystemColor(SWT.COLOR_RED);
      }
    }
    return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Color getBackground(final Object obj, final int i) {
    // TODO Auto-generated method stub
    return null;
  }

}
