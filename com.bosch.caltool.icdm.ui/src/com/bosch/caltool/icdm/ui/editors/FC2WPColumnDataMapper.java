package com.bosch.caltool.icdm.ui.editors;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.bosch.caltool.icdm.client.bo.fc2wp.FC2WPMappingResult;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMapping;
import com.bosch.caltool.icdm.ui.util.IUIConstants;

/**
 * Helper class used by Compare editor which contains information related to the dynamic columns in the compare editor
 *
 * @author
 */
public class FC2WPColumnDataMapper {


  /**
   * Map with UI Col Index as key and value column header name.
   */
  private final Map<Integer, String> columnIndexFlagMap = new TreeMap<>();
  /**
   * Map with UI Col Index as key and value is FC2WPMapping.
   */
  private final Map<Integer, FC2WPMapping> columnIndexFC2WPMap = new TreeMap<>();
  /**
   * Map with UI Col Index as key and value is FC2WPMappingResult.
   */
  private final Map<Integer, FC2WPMappingResult> columnIndexFC2WPMapResult = new TreeMap<>();


  /**
   * @return the columnIndexFC2WPMapResult
   */
  public Map<Integer, FC2WPMappingResult> getColumnIndexFC2WPMapResult() {
    return this.columnIndexFC2WPMapResult;
  }

  /**
   * @return the ipidcAttrs
   */
  public Set<FC2WPMapping> getFC2WP() {
    return new HashSet<>(this.columnIndexFC2WPMap.values());
  }

  private final Map<FC2WPMapping, List<Integer>> fc2wpColumnsMappings = new HashMap<>();


  /**
   * Method which stores the column Name,FC2WPMapping against column index.Also it stores all the columnIndexes for a
   * FC2WPMappingResult under comparison
   *
   * @param fc2wpMapping fc2wpMapping
   */
  public void addNewColumnIndex(final FC2WPMapping fc2wpMapping, final FC2WPMappingResult fc2wpMappingResult) {
    if (this.columnIndexFlagMap.isEmpty()) {


      addFirstVerObj(fc2wpMapping, fc2wpMappingResult);

    }
    else {

      List<Integer> keyList = new ArrayList<>(this.columnIndexFlagMap.keySet());
      Integer highestkey = keyList.get(keyList.size() - 1);


      this.columnIndexFlagMap.put(highestkey + 1, IUIConstants.WORK_PACKAGE);
      this.columnIndexFlagMap.put(highestkey + 2, IUIConstants.RESOURCE);
      this.columnIndexFlagMap.put(highestkey + 3, IUIConstants.WP_ID_MCR);
      this.columnIndexFlagMap.put(highestkey + 4, IUIConstants.BC);
      this.columnIndexFlagMap.put(highestkey + 5, IUIConstants.PT_TYPE);
      this.columnIndexFlagMap.put(highestkey + 6, IUIConstants.FIRST_CONTACT);
      this.columnIndexFlagMap.put(highestkey + 7, IUIConstants.SECOND_CONTACT);
      this.columnIndexFlagMap.put(highestkey + 8, IUIConstants.IS_AGREED);
      this.columnIndexFlagMap.put(highestkey + 9, IUIConstants.AGREED_ON);
      this.columnIndexFlagMap.put(highestkey + 10, IUIConstants.RESP_FOR_AGREEMENT);
      this.columnIndexFlagMap.put(highestkey + 11, IUIConstants.COMMENT);
      this.columnIndexFlagMap.put(highestkey + 12, IUIConstants.FC2WP_INFO);
      this.columnIndexFlagMap.put(highestkey + 13, IUIConstants.IS_IN_ICDM);
      this.columnIndexFlagMap.put(highestkey + 14, IUIConstants.IS_FC_IN_SDOM);
      this.columnIndexFlagMap.put(highestkey + 15, IUIConstants.FC_WITH_PARAMS);
      this.columnIndexFlagMap.put(highestkey + 16, IUIConstants.DELETED);
      this.columnIndexFlagMap.put(highestkey + 17, IUIConstants.CREATED_DATE);
      this.columnIndexFlagMap.put(highestkey + 18, IUIConstants.MODIFIED_DATE);


      //
      this.columnIndexFC2WPMap.put(highestkey + 1, fc2wpMapping);
      this.columnIndexFC2WPMap.put(highestkey + 2, fc2wpMapping);
      this.columnIndexFC2WPMap.put(highestkey + 3, fc2wpMapping);
      this.columnIndexFC2WPMap.put(highestkey + 4, fc2wpMapping);
      this.columnIndexFC2WPMap.put(highestkey + 5, fc2wpMapping);
      this.columnIndexFC2WPMap.put(highestkey + 6, fc2wpMapping);
      this.columnIndexFC2WPMap.put(highestkey + 7, fc2wpMapping);
      this.columnIndexFC2WPMap.put(highestkey + 8, fc2wpMapping);
      this.columnIndexFC2WPMap.put(highestkey + 9, fc2wpMapping);
      this.columnIndexFC2WPMap.put(highestkey + 10, fc2wpMapping);
      this.columnIndexFC2WPMap.put(highestkey + 11, fc2wpMapping);
      this.columnIndexFC2WPMap.put(highestkey + 12, fc2wpMapping);
      this.columnIndexFC2WPMap.put(highestkey + 13, fc2wpMapping);
      this.columnIndexFC2WPMap.put(highestkey + 14, fc2wpMapping);
      this.columnIndexFC2WPMap.put(highestkey + 15, fc2wpMapping);
      this.columnIndexFC2WPMap.put(highestkey + 16, fc2wpMapping);
      this.columnIndexFC2WPMap.put(highestkey + 17, fc2wpMapping);
      this.columnIndexFC2WPMap.put(highestkey + 18, fc2wpMapping);

      this.columnIndexFC2WPMapResult.put(highestkey + 1, fc2wpMappingResult);
      this.columnIndexFC2WPMapResult.put(highestkey + 2, fc2wpMappingResult);
      this.columnIndexFC2WPMapResult.put(highestkey + 3, fc2wpMappingResult);
      this.columnIndexFC2WPMapResult.put(highestkey + 4, fc2wpMappingResult);
      this.columnIndexFC2WPMapResult.put(highestkey + 5, fc2wpMappingResult);
      this.columnIndexFC2WPMapResult.put(highestkey + 6, fc2wpMappingResult);
      this.columnIndexFC2WPMapResult.put(highestkey + 7, fc2wpMappingResult);
      this.columnIndexFC2WPMapResult.put(highestkey + 8, fc2wpMappingResult);
      this.columnIndexFC2WPMapResult.put(highestkey + 9, fc2wpMappingResult);
      this.columnIndexFC2WPMapResult.put(highestkey + 10, fc2wpMappingResult);
      this.columnIndexFC2WPMapResult.put(highestkey + 11, fc2wpMappingResult);
      this.columnIndexFC2WPMapResult.put(highestkey + 12, fc2wpMappingResult);
      this.columnIndexFC2WPMapResult.put(highestkey + 13, fc2wpMappingResult);
      this.columnIndexFC2WPMapResult.put(highestkey + 14, fc2wpMappingResult);
      this.columnIndexFC2WPMapResult.put(highestkey + 15, fc2wpMappingResult);
      this.columnIndexFC2WPMapResult.put(highestkey + 16, fc2wpMappingResult);
      this.columnIndexFC2WPMapResult.put(highestkey + 17, fc2wpMappingResult);
      this.columnIndexFC2WPMapResult.put(highestkey + 18, fc2wpMappingResult);


      List<Integer> columnIndices = new ArrayList<>();
      columnIndices.add(this.columnIndexFC2WPMap.size() + 1);
      columnIndices.add(this.columnIndexFC2WPMap.size() + 2);
      columnIndices.add(this.columnIndexFC2WPMap.size() + 3);
      columnIndices.add(this.columnIndexFC2WPMap.size() + 4);
      columnIndices.add(this.columnIndexFC2WPMap.size() + 5);
      columnIndices.add(this.columnIndexFC2WPMap.size() + 6);
      columnIndices.add(this.columnIndexFC2WPMap.size() + 7);
      columnIndices.add(this.columnIndexFC2WPMap.size() + 8);
      columnIndices.add(this.columnIndexFC2WPMap.size() + 9);
      columnIndices.add(this.columnIndexFC2WPMap.size() + 10);
      columnIndices.add(this.columnIndexFC2WPMap.size() + 11);
      columnIndices.add(this.columnIndexFC2WPMap.size() + 12);
      columnIndices.add(this.columnIndexFC2WPMap.size() + 13);
      columnIndices.add(this.columnIndexFC2WPMap.size() + 14);
      columnIndices.add(this.columnIndexFC2WPMap.size() + 15);
      columnIndices.add(this.columnIndexFC2WPMap.size() + 16);
      columnIndices.add(this.columnIndexFC2WPMap.size() + 17);
      columnIndices.add(this.columnIndexFC2WPMap.size() + 18);

      this.fc2wpColumnsMappings.put(fc2wpMapping, columnIndices);
    }
  }

  /**
   * @param fc2wpMapping
   * @param fc2wpMappingResult
   */
  private void addFirstVerObj(final FC2WPMapping fc2wpMapping, final FC2WPMappingResult fc2wpMappingResult) {
    this.columnIndexFlagMap.put(2, IUIConstants.WORK_PACKAGE);
    this.columnIndexFlagMap.put(3, IUIConstants.RESOURCE);
    this.columnIndexFlagMap.put(4, IUIConstants.WP_ID_MCR);
    this.columnIndexFlagMap.put(5, IUIConstants.BC);
    this.columnIndexFlagMap.put(6, IUIConstants.PT_TYPE);
    this.columnIndexFlagMap.put(7, IUIConstants.FIRST_CONTACT);
    this.columnIndexFlagMap.put(8, IUIConstants.SECOND_CONTACT);
    this.columnIndexFlagMap.put(9, IUIConstants.IS_AGREED);
    this.columnIndexFlagMap.put(10, IUIConstants.AGREED_ON);
    this.columnIndexFlagMap.put(11, IUIConstants.RESP_FOR_AGREEMENT);
    this.columnIndexFlagMap.put(12, IUIConstants.COMMENT);
    this.columnIndexFlagMap.put(13, IUIConstants.FC2WP_INFO);
    this.columnIndexFlagMap.put(14, IUIConstants.IS_IN_ICDM);
    this.columnIndexFlagMap.put(15, IUIConstants.IS_FC_IN_SDOM);
    this.columnIndexFlagMap.put(16, IUIConstants.FC_WITH_PARAMS);
    this.columnIndexFlagMap.put(17, IUIConstants.DELETED);
    this.columnIndexFlagMap.put(18, IUIConstants.CREATED_DATE);
    this.columnIndexFlagMap.put(19, IUIConstants.MODIFIED_DATE);

    //
    this.columnIndexFC2WPMap.put(2, fc2wpMapping);
    this.columnIndexFC2WPMap.put(3, fc2wpMapping);
    this.columnIndexFC2WPMap.put(4, fc2wpMapping);
    this.columnIndexFC2WPMap.put(5, fc2wpMapping);
    this.columnIndexFC2WPMap.put(6, fc2wpMapping);
    this.columnIndexFC2WPMap.put(7, fc2wpMapping);
    this.columnIndexFC2WPMap.put(8, fc2wpMapping);
    this.columnIndexFC2WPMap.put(9, fc2wpMapping);
    this.columnIndexFC2WPMap.put(10, fc2wpMapping);
    this.columnIndexFC2WPMap.put(11, fc2wpMapping);
    this.columnIndexFC2WPMap.put(12, fc2wpMapping);
    this.columnIndexFC2WPMap.put(13, fc2wpMapping);
    this.columnIndexFC2WPMap.put(14, fc2wpMapping);
    this.columnIndexFC2WPMap.put(15, fc2wpMapping);
    this.columnIndexFC2WPMap.put(16, fc2wpMapping);
    this.columnIndexFC2WPMap.put(17, fc2wpMapping);
    this.columnIndexFC2WPMap.put(18, fc2wpMapping);
    this.columnIndexFC2WPMap.put(19, fc2wpMapping);

    this.columnIndexFC2WPMapResult.put(2, fc2wpMappingResult);
    this.columnIndexFC2WPMapResult.put(3, fc2wpMappingResult);
    this.columnIndexFC2WPMapResult.put(4, fc2wpMappingResult);
    this.columnIndexFC2WPMapResult.put(5, fc2wpMappingResult);
    this.columnIndexFC2WPMapResult.put(6, fc2wpMappingResult);
    this.columnIndexFC2WPMapResult.put(7, fc2wpMappingResult);
    this.columnIndexFC2WPMapResult.put(8, fc2wpMappingResult);
    this.columnIndexFC2WPMapResult.put(9, fc2wpMappingResult);
    this.columnIndexFC2WPMapResult.put(10, fc2wpMappingResult);
    this.columnIndexFC2WPMapResult.put(11, fc2wpMappingResult);
    this.columnIndexFC2WPMapResult.put(12, fc2wpMappingResult);
    this.columnIndexFC2WPMapResult.put(13, fc2wpMappingResult);
    this.columnIndexFC2WPMapResult.put(14, fc2wpMappingResult);
    this.columnIndexFC2WPMapResult.put(15, fc2wpMappingResult);
    this.columnIndexFC2WPMapResult.put(16, fc2wpMappingResult);
    this.columnIndexFC2WPMapResult.put(17, fc2wpMappingResult);
    this.columnIndexFC2WPMapResult.put(18, fc2wpMappingResult);
    this.columnIndexFC2WPMapResult.put(19, fc2wpMappingResult);


    List<Integer> columnIndices = new ArrayList<>();
    columnIndices.add(2);
    columnIndices.add(3);
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
    columnIndices.add(15);
    columnIndices.add(16);
    columnIndices.add(17);
    columnIndices.add(18);
    columnIndices.add(19);

    this.fc2wpColumnsMappings.put(fc2wpMapping, columnIndices);
  }

  /**
   * Method for removing FC2WPMapping related mappings
   *
   * @param pidcAttribute reconstructing nattable on column removal might be better
   */
  public void removeColumnIndex(final FC2WPMapping fc2wpMapping) {
    List<Integer> columnIndicesToRemove = this.fc2wpColumnsMappings.get(fc2wpMapping);
    for (Integer integer : columnIndicesToRemove) {
      this.columnIndexFlagMap.remove(integer);
      // update greater than columns in both columnIndexFlagMap and columnIndexPIDCAttrMap
      this.columnIndexFC2WPMap.remove(integer);

      this.columnIndexFC2WPMapResult.remove(integer);
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
    FC2WPMapping fc2wpMapping = this.columnIndexFC2WPMap.get(colIndex);
    Object returnObj = null;
    if (fc2wpMapping != null) {
      final String key = fc2wpMapping.getFunctionName();
      String flagString = this.columnIndexFlagMap.get(colIndex);
      FC2WPMappingResult fc2wpMappingResult = this.columnIndexFC2WPMapResult.get(colIndex);
      switch (flagString) {
        case IUIConstants.WORK_PACKAGE:
          // Workpackage
          returnObj = fc2wpMappingResult.getWorkpackage(key);
          break;
        case IUIConstants.RESOURCE:
          // Resource
          returnObj = fc2wpMappingResult.getResource(key);
          break;
        case IUIConstants.WP_ID_MCR:
          // WP-ID (MCR)
          returnObj = fc2wpMappingResult.getWpIdMCR(key);
          break;
        case IUIConstants.BC:
          // BC
          returnObj = fc2wpMappingResult.getBC(key);
          break;
        case IUIConstants.PT_TYPE:
          // PT-Type
          returnObj = fc2wpMappingResult.getPTtypeUIString(key);
          break;
        case IUIConstants.FIRST_CONTACT:
          // Contact1
          returnObj = fc2wpMappingResult.getFirstContactEffective(key);
          break;
        case IUIConstants.SECOND_CONTACT:
          // Contact2
          returnObj = fc2wpMappingResult.getSecondContactEffective(key);
          break;
        case IUIConstants.IS_AGREED:
          // Agreed with Coc ( Center of Competence)
          returnObj = fc2wpMappingResult.getIsCoCAgreedUIString(key);
          break;
        case IUIConstants.AGREED_ON:
          // Agreed on date
          returnObj = setDateFormat(fc2wpMapping.getAgreeWithCocDate());
          break;
        case IUIConstants.RESP_FOR_AGREEMENT:
          // Responsible for Coc agreement
          returnObj = fc2wpMappingResult.getAgreeWithCocRespUserDisplay(key);
          break;
        case IUIConstants.COMMENT:
          // Comments
          returnObj = fc2wpMapping.getComments();
          break;
        case IUIConstants.FC2WP_INFO:
          // FC2WP Information
          returnObj = fc2wpMapping.getFc2wpInfo();
          break;
        case IUIConstants.IS_IN_ICDM:
          // Is function part of ICDM A2L
          returnObj = fc2wpMappingResult.getIsInICDMA2LUIString(key);
          break;
        case IUIConstants.IS_FC_IN_SDOM:
          // Is function in SDOM
          returnObj = fc2wpMappingResult.getIsFcInSdomUIString(key);
          break;
        case IUIConstants.FC_WITH_PARAMS:
          returnObj = fc2wpMappingResult.isFcWithParams(key);
          break;
        case IUIConstants.DELETED:
          // Is Deleted
          returnObj = fc2wpMappingResult.getIsDeletedUIString(key);
          break;
        case IUIConstants.CREATED_DATE:
          // Created Date
          returnObj = fc2wpMappingResult.setDateFormat(fc2wpMapping.getCreatedDate());
          break;
        case IUIConstants.MODIFIED_DATE:
          // Modifies Date
          if (null != fc2wpMapping.getModifiedDate()) {
            returnObj = fc2wpMappingResult.setDateFormat(fc2wpMapping.getModifiedDate());
          }
          break;
        default:
          // do nothing
      }
    }
    return returnObj;
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
  public Map<Integer, FC2WPMapping> getColumnIndexFC2WPMap() {
    return this.columnIndexFC2WPMap;
  }

  /**
   * @param agreeWithCocDate
   * @return
   */
  private String setDateFormat(final Date date) {
    String formattedDate = "";
    if (date != null) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      formattedDate = ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_12, cal);
    }
    return formattedDate;
  }
}