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
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectObjectBO;
import com.bosch.caltool.icdm.client.bo.apic.ProjectAttributeUtil;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;

/**
 * Helper class used by Compare editor which contains information related to the dynamic columns in the compare editor
 *
 * @author jvi6cob
 */
public class ColumnDataMapper {


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

  /**
   * key - column index, value- pidcversion/variant/subvariant id
   */
  private final Map<Integer, Long> columnIndexProjectObjMap = new TreeMap<>();

  private final Map<Long, AbstractProjectObjectBO> projectHandlerMap;

  /**
   * @return the ipidcAttrs
   */
  public Set<IProjectAttribute> getIpidcAttrs() {
    return new HashSet<IProjectAttribute>(this.columnIndexPIDCAttrMap.values());
  }


  // TODO: Can be removed
  private final Map<IProjectAttribute, List<Integer>> pidcAttributeColumnsMappings = new HashMap<>();

  private final ProjectAttributeUtil compareEditorUtil = new ProjectAttributeUtil();

  /**
   * @param dataHandlerMap
   */
  public ColumnDataMapper(final Map<Long, AbstractProjectObjectBO> projectHandlerMap) {
    this.projectHandlerMap = projectHandlerMap;
  }


  /**
   * Method which stores the column Name,IPIDCAttribute against column index.Also it stores all the columnIndexes for a
   * IPIDCAttribute of PIDCVersion/Variant/Subvariant under comparison
   *
   * @param pidcAttribute IPIDCAttribute
   */
  public void addNewColumnIndex(final IProjectAttribute pidcAttribute) {
    if (this.columnIndexFlagMap.isEmpty()) {
      // Add column index
      this.columnIndexFlagMap.put(4, ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType());
      this.columnIndexFlagMap.put(5, ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType());
      this.columnIndexFlagMap.put(6, ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType());
      this.columnIndexFlagMap.put(7, ApicConstants.SUMMARY_LABEL);
      this.columnIndexFlagMap.put(8, ApicConstants.VALUE_TEXT);
      this.columnIndexFlagMap.put(9, "Part Number");
      this.columnIndexFlagMap.put(10, "Specification");
      this.columnIndexFlagMap.put(11, "Modified Date");
      this.columnIndexFlagMap.put(12, ApicConstants.CHARACTERISTIC);
      this.columnIndexFlagMap.put(13, ApicConstants.CHARVAL);
      this.columnIndexFlagMap.put(14, ApicConstants.COMMENT);

      // add pidc attribute mapping
      this.columnIndexPIDCAttrMap.put(4, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(5, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(6, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(7, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(8, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(9, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(10, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(11, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(12, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(13, pidcAttribute);
      this.columnIndexPIDCAttrMap.put(14, pidcAttribute);

      // add projectobject mapping
      this.columnIndexProjectObjMap.put(4, getObjectId(pidcAttribute));
      this.columnIndexProjectObjMap.put(5, getObjectId(pidcAttribute));
      this.columnIndexProjectObjMap.put(6, getObjectId(pidcAttribute));
      this.columnIndexProjectObjMap.put(7, getObjectId(pidcAttribute));
      this.columnIndexProjectObjMap.put(8, getObjectId(pidcAttribute));
      this.columnIndexProjectObjMap.put(9, getObjectId(pidcAttribute));
      this.columnIndexProjectObjMap.put(10, getObjectId(pidcAttribute));
      this.columnIndexProjectObjMap.put(11, getObjectId(pidcAttribute));
      this.columnIndexProjectObjMap.put(12, getObjectId(pidcAttribute));
      this.columnIndexProjectObjMap.put(13, getObjectId(pidcAttribute));
      this.columnIndexProjectObjMap.put(14, getObjectId(pidcAttribute));


      // Can be moved out of if else
      List<Integer> columnIndices = new ArrayList<>();
      columnIndices.add(4);
      columnIndices.add(5);
      columnIndices.add(6);
      columnIndices.add(7);
      columnIndices.add(8);
      columnIndices.add(9);
      columnIndices.add(10);
      columnIndices.add(11);
      columnIndices.add(12);
      columnIndices.add(13);
      columnIndices.add(14);
      this.pidcAttributeColumnsMappings.put(pidcAttribute, columnIndices);

    }
    else {

      List<Integer> keyList = new ArrayList<>(this.columnIndexFlagMap.keySet());
      Integer highestkey = keyList.get(keyList.size() - 1);

//      add column index flag mapping
      this.columnIndexFlagMap.put(highestkey + 1, ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType());
      this.columnIndexFlagMap.put(highestkey + 2, ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType());
      this.columnIndexFlagMap.put(highestkey + 3, ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType());
      this.columnIndexFlagMap.put(highestkey + 4, ApicConstants.SUMMARY_LABEL);
      this.columnIndexFlagMap.put(highestkey + 5, ApicConstants.VALUE_TEXT);
      this.columnIndexFlagMap.put(highestkey + 6, "Part Number");
      this.columnIndexFlagMap.put(highestkey + 7, "Specification");
      this.columnIndexFlagMap.put(highestkey + 8, "Modified Date");
      this.columnIndexFlagMap.put(highestkey + 9, ApicConstants.CHARACTERISTIC);
      this.columnIndexFlagMap.put(highestkey + 10, ApicConstants.CHARVAL);
      this.columnIndexFlagMap.put(highestkey + 11, ApicConstants.COMMENT);

      // add column index projectattr mapping
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

      // add column index pidc project object mapping
      this.columnIndexProjectObjMap.put(highestkey + 1, getObjectId(pidcAttribute));
      this.columnIndexProjectObjMap.put(highestkey + 2, getObjectId(pidcAttribute));
      this.columnIndexProjectObjMap.put(highestkey + 3, getObjectId(pidcAttribute));
      this.columnIndexProjectObjMap.put(highestkey + 4, getObjectId(pidcAttribute));
      this.columnIndexProjectObjMap.put(highestkey + 5, getObjectId(pidcAttribute));
      this.columnIndexProjectObjMap.put(highestkey + 6, getObjectId(pidcAttribute));
      this.columnIndexProjectObjMap.put(highestkey + 7, getObjectId(pidcAttribute));
      this.columnIndexProjectObjMap.put(highestkey + 8, getObjectId(pidcAttribute));
      this.columnIndexProjectObjMap.put(highestkey + 9, getObjectId(pidcAttribute));
      this.columnIndexProjectObjMap.put(highestkey + 10, getObjectId(pidcAttribute));
      this.columnIndexProjectObjMap.put(highestkey + 11, getObjectId(pidcAttribute));


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

      this.pidcAttributeColumnsMappings.put(pidcAttribute, columnIndices);
    }
  }

  /**
   * @param projAttr
   * @return project object id
   */
  private Long getObjectId(final IProjectAttribute projAttr) {
    if (projAttr instanceof PidcVersionAttribute) {
//      get pidc version id
      return ((PidcVersionAttribute) projAttr).getPidcVersId();
    }
    else if (projAttr instanceof PidcVariantAttribute) {
//      get pidc variant id
      return ((PidcVariantAttribute) projAttr).getVariantId();
    }
    else if (projAttr instanceof PidcSubVariantAttribute) {
//      get pidc sub-variant id
      return ((PidcSubVariantAttribute) projAttr).getSubVariantId();
    }
    return null;
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
   */
  public Object getColumnData(final int colIndex) {
    // get data for resp column
    IProjectAttribute ipidcAttribute = this.columnIndexPIDCAttrMap.get(colIndex);
    Object returnValue = null;
    if (ipidcAttribute != null) {
      String usedFlagStr = ipidcAttribute.getUsedFlag();

      String flagString = this.columnIndexFlagMap.get(colIndex);


      switch (flagString) {
        case ApicConstants.SUMMARY_LABEL:
          // summmary
          returnValue = ApicConstants.PROJ_ATTR_USED_FLAG.getType(ipidcAttribute.getUsedFlag());
          break;
        case ApicConstants.VALUE_TEXT:
          // Value
          returnValue = getPrjAttrDispName(ipidcAttribute);
          break;
        case ApicConstants.CHARACTERISTIC:
          // char

          returnValue = getCharacteristicName(ipidcAttribute);
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
        default:
          returnValue = defaultData(usedFlagStr, flagString);
      }


    }
    return returnValue;
  }


  /**
   * Method to get value name in english
   *
   * @param ipidcAttribute
   * @return characteristic value name
   */
  private Object getCharValName(final IProjectAttribute ipidcAttribute) {
    Object returnValue;
//   get pidc attributes characteristic (attribute class) to display
    if ((this.projectHandlerMap.get(this.compareEditorUtil.getID(ipidcAttribute)).getPidcDataHandler()
        .getAttributeValueMap().get(ipidcAttribute.getValueId()) != null) &&
        (this.projectHandlerMap.get(this.compareEditorUtil.getID(ipidcAttribute)).getPidcDataHandler()
            .getAttributeValueMap().get(ipidcAttribute.getValueId()).getCharacteristicValueId() != null)) {
      returnValue = this.projectHandlerMap.get(this.compareEditorUtil.getID(ipidcAttribute)).getPidcDataHandler()
          .getCharacteristicValueMap().get(this.projectHandlerMap.get(this.compareEditorUtil.getID(ipidcAttribute))
              .getPidcDataHandler().getAttributeValueMap().get(ipidcAttribute.getValueId()).getCharacteristicValueId())
          .getValNameEng();
    }
    else {
      returnValue = "";
    }
    return returnValue;
  }


  /**
   * Method to get the modified date of PIDC attribute
   *
   * @param ipidcAttribute
   * @return modified date
   */
  private Object getModifiedDateVal(final IProjectAttribute ipidcAttribute) {
    try {
//      get modified date in dd-MMM-yyyy
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
   * @return characteristic name
   */
  private Object getCharacteristicName(final IProjectAttribute ipidcAttribute) {
    Object returnValue = null;
//    get characteristic name
    if (ipidcAttribute.getAttrId() != null) {
      if (this.projectHandlerMap.get(this.compareEditorUtil.getID(ipidcAttribute)).getPidcDataHandler()
          .getAttributeMap().get(ipidcAttribute.getAttrId()).getCharacteristicId() != null) {
        returnValue = this.projectHandlerMap.get(this.compareEditorUtil.getID(ipidcAttribute)).getPidcDataHandler()
            .getCharacteristicMap().get(this.projectHandlerMap.get(this.compareEditorUtil.getID(ipidcAttribute))
                .getPidcDataHandler().getAttributeMap().get(ipidcAttribute.getAttrId()).getCharacteristicId())
            .getName();
      }
    }
    else {
      returnValue = "";
    }
    return returnValue;
  }


  /**
   * @param ipidcAttribute
   * @return the displayed value of this project attribute as string
   */
  private Object getPrjAttrDispName(final IProjectAttribute ipidcAttribute) {
//    get default attribute display name
    AbstractProjectAttributeBO handlerValue = this.compareEditorUtil.getProjectAttributeHandler(ipidcAttribute,
        this.projectHandlerMap.get(this.compareEditorUtil.getID(ipidcAttribute)));
    if (handlerValue != null) {
      return handlerValue.getDefaultValueDisplayName(true);
    }
    return null;
  }

  /**
   * @param usedFlagStr
   * @param flagString
   * @return defauult data
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


  /**
   * @return the projectHandlerMap
   */
  public Map<Long, AbstractProjectObjectBO> getProjectHandlerMap() {
    return this.projectHandlerMap;
  }


  /**
   * @return the columnIndexProjectObjMap
   */
  public Map<Integer, Long> getColumnIndexProjectObjMap() {
    return this.columnIndexProjectObjMap;
  }


}