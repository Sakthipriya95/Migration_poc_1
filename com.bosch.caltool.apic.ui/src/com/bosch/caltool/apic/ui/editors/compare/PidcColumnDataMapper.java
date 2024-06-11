package com.bosch.caltool.apic.ui.editors.compare;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;

/**
 * Helper class used by Compare editor which contains information related to the dynamic columns in the compare editor
 *
 * @author jvi6cob
 */
public class PidcColumnDataMapper {


  // TODO: Check whether both maps can be combined
  // Also check keeping different maps has any advantages
  // The below maps should exist in tandem with each other
  /**
   * Map with UI Col Index as key and value is ???,Yes or No.
   */
  private final Map<Integer, String> columnIndexFlagMap = new TreeMap<>();
  /**
   * Map with UI Col Index as key and value is IPIDCAttribute.
   */
  private final Map<Integer, IProjectAttribute> columnIndexPIDCAttrMap = new TreeMap<>();

  private AbstractProjectAttributeBO projectAttributeHandler;

  private final PidcDataHandler pidcDataHandler;


  /**
   * @return the pidcDataHandler
   */
  public PidcDataHandler getPidcDataHandler() {
    return this.pidcDataHandler;
  }


  /**
   * @param projectAttrHandler
   * @param pidcDataHandler
   */
  public PidcColumnDataMapper(final AbstractProjectAttributeBO projectAttrHandler,
      final PidcDataHandler pidcDataHandler) {
    super();
    this.pidcDataHandler = pidcDataHandler;
    initialize(projectAttrHandler);
  }


  private void initialize(final AbstractProjectAttributeBO projectAttrHandler) {
    if (projectAttrHandler instanceof PidcVersionAttributeBO) {
      this.projectAttributeHandler =
          new PidcVersionAttributeBO(((PidcVersionAttributeBO) projectAttrHandler).getPidcVersAttr(),
              (PidcVersionBO) projectAttrHandler.getProjectObjectBO());
    }
    else if (projectAttrHandler instanceof PidcVariantAttributeBO) {
      this.projectAttributeHandler =
          new PidcVariantAttributeBO(((PidcVariantAttributeBO) projectAttrHandler).getProjectVarAttr(),
              (PidcVariantBO) projectAttrHandler.getProjectObjectBO());
    }
    else if (projectAttrHandler instanceof PidcSubVariantAttributeBO) {
      this.projectAttributeHandler =
          new PidcSubVariantAttributeBO(((PidcSubVariantAttributeBO) projectAttrHandler).getProjectSubVarAttr(),
              (PidcSubVariantBO) projectAttrHandler.getProjectObjectBO());
    }


  }

  /**
   * @return the ipidcAttrs
   */
  public Set<IProjectAttribute> getIpidcAttrs() {
    return new HashSet<IProjectAttribute>(this.columnIndexPIDCAttrMap.values());
  }


  // TODO: Can be removed
  private final Map<IProjectAttribute, List<Integer>> pidcAttributeColumnsMappings = new HashMap<>();


  /**
   * Method which stores the column Name,IPIDCAttribute against column index.Also it stores all the columnIndexes for a
   * IPIDCAttribute of PIDCVersion/Variant/Subvariant under comparison
   *
   * @param pidcAttribute IPIDCAttribute
   */
  public void addNewColumnIndex(final IProjectAttribute pidcAttribute) {
    if (this.columnIndexFlagMap.isEmpty()) {
      // Add column index
      this.columnIndexFlagMap.put(3, ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType());
      this.columnIndexFlagMap.put(4, ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType());
      this.columnIndexFlagMap.put(5, ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType());
      this.columnIndexFlagMap.put(6, ApicConstants.VALUE_TEXT);
      this.columnIndexFlagMap.put(8, ApicConstants.LABEL_VCDM_FLAG);
      this.columnIndexFlagMap.put(9, ApicConstants.LABEL_FM_RELEVANT_FLAG);
      this.columnIndexFlagMap.put(10, ApicConstants.LABEL_PART_NUM);
      this.columnIndexFlagMap.put(11, ApicConstants.SPECIFICATION);
      this.columnIndexFlagMap.put(12, ApicConstants.COMMENT);
      this.columnIndexFlagMap.put(13, ApicConstants.MODIFIED_DATE);
      this.columnIndexFlagMap.put(14, ApicConstants.CHARACTERISTIC);
      this.columnIndexFlagMap.put(15, ApicConstants.CHARVAL);
      this.columnIndexFlagMap.put(16, ApicConstants.LABEL_ATTRIBUTE_CREATED_ON);
      this.columnIndexFlagMap.put(17, ApicConstants.LABEL_GROUP);
      this.columnIndexFlagMap.put(18, ApicConstants.LABEL_SUPER_GROUP);

      //
      this.columnIndexPIDCAttrMap.put(3, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(4, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(5, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(6, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(8, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(9, pidcAttribute);

      this.columnIndexPIDCAttrMap.put(10, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(11, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(12, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(13, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(14, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(15, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(16, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(17, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(18, pidcAttribute);

      // Can be moved out of if else
      List<Integer> columnIndices = new ArrayList<>();
      columnIndices.add(3);
      columnIndices.add(4);
      columnIndices.add(5);
      columnIndices.add(6);
      columnIndices.add(7);
      columnIndices.add(8);
      columnIndices.add(9);
      columnIndices.add(11);
      columnIndices.add(12);
      columnIndices.add(13);
      columnIndices.add(14);
      columnIndices.add(15);
      columnIndices.add(16);
      columnIndices.add(17);
      columnIndices.add(18);

      this.pidcAttributeColumnsMappings.put(pidcAttribute, columnIndices);

    }
    else {

      List<Integer> keyList = new ArrayList<>(this.columnIndexFlagMap.keySet());
      Integer highestkey = keyList.get(keyList.size() - 1);

      this.columnIndexFlagMap.put(highestkey + 1, ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType());
      this.columnIndexFlagMap.put(highestkey + 2, ApicConstants.PROJ_ATTR_USED_FLAG.NO.getUiType());
      this.columnIndexFlagMap.put(highestkey + 3, ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType());

      this.columnIndexFlagMap.put(highestkey + 4, ApicConstants.VALUE_TEXT);
      this.columnIndexFlagMap.put(highestkey + 5, ApicConstants.LABEL_PART_NUM);
      this.columnIndexFlagMap.put(highestkey + 6, ApicConstants.SPECIFICATION);
      this.columnIndexFlagMap.put(highestkey + 7, ApicConstants.COMMENT);
      this.columnIndexFlagMap.put(highestkey + 8, ApicConstants.MODIFIED_DATE);
      this.columnIndexFlagMap.put(highestkey + 9, ApicConstants.CHARACTERISTIC);
      this.columnIndexFlagMap.put(highestkey + 10, ApicConstants.CHARVAL);
      this.columnIndexFlagMap.put(highestkey + 11, ApicConstants.LABEL_ATTRIBUTE_CREATED_ON);
      this.columnIndexFlagMap.put(highestkey + 12, ApicConstants.LABEL_GROUP);
      this.columnIndexFlagMap.put(highestkey + 13, ApicConstants.LABEL_SUPER_GROUP);
      this.columnIndexFlagMap.put(highestkey + 14, ApicConstants.LABEL_VCDM_FLAG);
      this.columnIndexFlagMap.put(highestkey + 15, ApicConstants.LABEL_FM_RELEVANT_FLAG);


      this.columnIndexPIDCAttrMap.put(highestkey + 1, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(highestkey + 2, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(highestkey + 3, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(highestkey + 4, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(highestkey + 5, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(highestkey + 6, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(highestkey + 7, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(highestkey + 8, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(highestkey + 9, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(highestkey + 10, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(highestkey + 11, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(highestkey + 12, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(highestkey + 13, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(highestkey + 14, pidcAttribute);

      // Can be moved out of if else
      List<Integer> columnIndices = new ArrayList<>();
      columnIndices.add(this.columnIndexPIDCAttrMap.size() + 1);
      columnIndices.add(this.columnIndexPIDCAttrMap.size() + 2);
      columnIndices.add(this.columnIndexPIDCAttrMap.size() + 3);
      columnIndices.add(this.columnIndexPIDCAttrMap.size() + 4);
      columnIndices.add(this.columnIndexPIDCAttrMap.size() + 5);
      columnIndices.add(this.columnIndexPIDCAttrMap.size() + 6);
      columnIndices.add(this.columnIndexPIDCAttrMap.size() + 7);
      columnIndices.add(this.columnIndexPIDCAttrMap.size() + 8);
      columnIndices.add(this.columnIndexPIDCAttrMap.size() + 9);
      columnIndices.add(this.columnIndexPIDCAttrMap.size() + 10);
      columnIndices.add(this.columnIndexPIDCAttrMap.size() + 11);
      columnIndices.add(this.columnIndexPIDCAttrMap.size() + 12);
      columnIndices.add(this.columnIndexPIDCAttrMap.size() + 13);
      columnIndices.add(this.columnIndexPIDCAttrMap.size() + 14);

      this.pidcAttributeColumnsMappings.put(pidcAttribute, columnIndices);
    }
  }

  /**
   * Method for removing IPIDCAttribute related mappings TODO: Not used as of now, can be removed since adding/removal
   * of pidc for comparison results in NatTable reconstruction.
   *
   * @param pidcAttribute reconstructing nattable on column removal might be better
   */
  public void removeColumnIndex(final IProjectAttribute pidcAttribute) {
    List<Integer> columnIndicesToRemove = this.pidcAttributeColumnsMappings.get(pidcAttribute);
    for (Integer integer : columnIndicesToRemove) {
      this.columnIndexFlagMap.remove(integer);
      // update greater than columns in both columnIndexFlagMap and columnIndexPIDCAttrMap
      this.columnIndexPIDCAttrMap.remove(integer);
    }

  }

  /**
   * Method which returns the data corresponding cell in the compare editor. Using the column index it fetches the
   * corresponding details
   *
   * @param colIndex int
   * @return Object
   * @throws ParseException
   */
  public Object getColumnData(final int colIndex) {

    // get data for resp column
    IProjectAttribute ipidcAttribute = this.columnIndexPIDCAttrMap.get(colIndex);
    String usedFlagStr = null;
    if (ipidcAttribute != null) {
      usedFlagStr = ipidcAttribute.getUsedFlag();
    }
    String flagString = this.columnIndexFlagMap.get(colIndex);

    Object returnValue = null;

    if (ipidcAttribute != null) {
      switch (flagString) {

        case ApicConstants.VALUE_TEXT:
          // Value
          returnValue = this.projectAttributeHandler.getDefaultValueDisplayName(true);
          break;
        case ApicConstants.CHARACTERISTIC:
          // char
          returnValue = getCharacteristicValName(ipidcAttribute);
          break;
        case ApicConstants.PART_NUMBER:
          // Part number
          returnValue = ipidcAttribute.getPartNumber();
          break;
        case ApicConstants.SPECIFICATION:
          // spec
          returnValue = ipidcAttribute.getSpecLink();
          break;
        case ApicConstants.COMMENT:
          // comment
          returnValue = ipidcAttribute.getAdditionalInfoDesc();
          break;
        case ApicConstants.MODIFIED_DATE:
          // modified date
          returnValue = getModifiedDateVal(ipidcAttribute);
          break;
        case ApicConstants.CHARVAL:
          // charval
          returnValue = getCharValName(ipidcAttribute);
          break;
        case ApicConstants.LABEL_ATTRIBUTE_CREATED_ON:
          returnValue = getAttrCreatedOnLabel(ipidcAttribute);
          break;
        case ApicConstants.LABEL_GROUP:
          returnValue = this.pidcDataHandler.getAttributeGroupMap()
              .get(this.pidcDataHandler.getAttributeMap().get(ipidcAttribute.getAttrId()).getAttrGrpId()).getName();
          break;
        case ApicConstants.LABEL_SUPER_GROUP:
          returnValue = getSuperGrpLabel(ipidcAttribute);
          break;
        case ApicConstants.LABEL_VCDM_FLAG:

          returnValue = this.projectAttributeHandler.canTransferToVcdm() ? Boolean.TRUE : Boolean.FALSE;
          break;
        case ApicConstants.LABEL_FM_RELEVANT_FLAG:
          returnValue = this.projectAttributeHandler.isFocusMatrixApplicable() ? Boolean.TRUE : Boolean.FALSE;
          break;
        default:
          return defaultData(usedFlagStr, flagString);
      }
    }
    return returnValue;
  }


  /**
   * @param ipidcAttribute
   * @return
   */
  private Object getSuperGrpLabel(final IProjectAttribute ipidcAttribute) {
    Object returnValue;
    returnValue = this.pidcDataHandler.getAttributeSuperGroupMap()
        .get(this.pidcDataHandler.getAttributeGroupMap()
            .get(this.pidcDataHandler.getAttributeMap().get(ipidcAttribute.getAttrId()).getAttrGrpId()).getSuperGrpId())
        .getName();
    return returnValue;
  }


  /**
   * @param ipidcAttribute
   * @return
   */
  private Object getAttrCreatedOnLabel(final IProjectAttribute ipidcAttribute) {
    try {
      return (ipidcAttribute.getCreatedDate() == null) ? ""
          : ApicUtil.formatDate(DateFormat.DATE_FORMAT_09, ipidcAttribute.getCreatedDate());
    }
    catch (ParseException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return null;
  }


  /**
   * @param ipidcAttribute
   * @return
   */
  private Object getCharValName(final IProjectAttribute ipidcAttribute) {
    Object returnValue;
    if ((this.pidcDataHandler.getAttributeValueMap().get(ipidcAttribute.getValueId()) != null) && (this.pidcDataHandler
        .getAttributeValueMap().get(ipidcAttribute.getValueId()).getCharacteristicValueId() != null)) {
      returnValue = this.pidcDataHandler.getCharacteristicValueMap()
          .get(this.pidcDataHandler.getAttributeValueMap().get(ipidcAttribute.getValueId()).getCharacteristicValueId())
          .getValNameEng();
    }
    else {
      returnValue = "";
    }
    return returnValue;
  }


  /**
   * @param ipidcAttribute
   * @return
   */
  private Object getModifiedDateVal(final IProjectAttribute ipidcAttribute) {
    try {
      return (ipidcAttribute.getModifiedDate() == null) ? ""
          : ApicUtil.formatDate(DateFormat.DATE_FORMAT_09, ipidcAttribute.getModifiedDate());
    }
    catch (ParseException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return null;
  }


  /**
   * @param ipidcAttribute
   * @return
   */
  private Object getCharacteristicValName(final IProjectAttribute ipidcAttribute) {
    Object returnValue = null;
    if (this.projectAttributeHandler.getProjectAttr() != null) {
      if (this.pidcDataHandler.getAttributeMap().get(ipidcAttribute.getAttrId()).getCharacteristicId() != null) {
        returnValue = this.pidcDataHandler.getCharacteristicMap()
            .get(this.pidcDataHandler.getAttributeMap().get(ipidcAttribute.getAttrId()).getCharacteristicId())
            .getName();
      }
    }
    else {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * @param usedFlagStr
   * @param flagString
   * @return
   */
  private Object defaultData(final String usedFlagStr, final String flagString) {
    // get defauult data
    Object returnValue;
    if (flagString.equals(usedFlagStr)) {
      returnValue = Boolean.TRUE;
    }

    else {
      returnValue = Boolean.FALSE;
    }
    return returnValue;
  }

  /**
   * Gets the Column Index Flag Map
   *
   * @return the columnIndexFlagMap
   */
  public Map<Integer, String> getColumnIndexFlagMap() {
    return this.columnIndexFlagMap;
  }


  /**
   * Gets the Column Index IPIDCAttribute Map
   *
   * @return the columnIndexPIDCAttrMap
   */
  public Map<Integer, IProjectAttribute> getColumnIndexPIDCAttrMap() {
    return this.columnIndexPIDCAttrMap;
  }


}