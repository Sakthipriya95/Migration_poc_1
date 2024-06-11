/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.precal;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.GttObjectName;
import com.bosch.caltool.icdm.database.entity.apic.GttParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParameter;
import com.bosch.caltool.icdm.model.cdr.PreCalibrationDataInput;
import com.bosch.caltool.icdm.model.cdr.PreCalibrationDataOutput;
import com.bosch.caltool.icdm.model.cdr.PreCalibrationDataResponse;

/**
 * Fetch the data of Pre-Caldata from Database
 *
 * @author svj7cob
 */
// Task 243510
public class PreCalibrationDataLoader extends AbstractSimpleBusinessObject {

  /**
   * Key : parameter-id, Value : List of checked value in byte array
   */
  private Map<Long, List<byte[]>> paramChkValMap;
  /**
   * the response for pre cal data
   */
  private final PreCalibrationDataResponse response = new PreCalibrationDataResponse();

  private EntityManager entMgrToUse;

  /**
   * Constructor
   *
   * @param serviceData serviceData
   */
  public PreCalibrationDataLoader(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * Load the review result pre-calibration data from the database
   *
   * @param pidcVersIdWithNoVarSet pidcVersIdsHaving NoVariantSet
   * @param pidcVersIdWithVarsSet pidcVersIdsHaving VariantSet
   * @param variantIdSet variantIdSet
   * @param input preCalDataReviewResultInput
   * @return the pidc calibration response
   * @throws IcdmException Icdm Exception
   */
  // Task 238583
  public PreCalibrationDataResponse loadReviewResultPreCalData(final Set<Long> pidcVersIdWithNoVarSet,
      final Set<Long> pidcVersIdWithVarsSet, final Set<Long> variantIdSet, final PreCalibrationDataInput input)
      throws IcdmException {

    try {
      this.entMgrToUse = getNewEntMgr();
      this.entMgrToUse.getTransaction().begin();

      doLoadData(pidcVersIdWithNoVarSet, pidcVersIdWithVarsSet, variantIdSet, input);

      this.entMgrToUse.getTransaction().commit();
    }
    finally {
      closeEm(this.entMgrToUse);
    }

    return this.response;
  }

  private void doLoadData(final Set<Long> pidcVersIdWithNoVarSet, final Set<Long> pidcVersIdWithVarsSet,
      final Set<Long> variantIdSet, final PreCalibrationDataInput input)
      throws DataException {

    // fill temporary table with param-ids
    fillIntoGttParam(input.getParameterIdSet());

    if (CommonUtils.isNotEmpty(pidcVersIdWithNoVarSet)) {
      // fetch pre-cal data with pidc version ids as input with no variants
      fetchCheckValueFromDb(pidcVersIdWithNoVarSet, getDataByVersWithNoVarQry());
    }


    if (CommonUtils.isNotEmpty(pidcVersIdWithVarsSet)) {
      // fetch pre-cal data with pidc version ids as input with variants
      fetchCheckValueFromDb(pidcVersIdWithVarsSet, getDataByVersWithVarsQry());
    }

    if (CommonUtils.isNotEmpty(variantIdSet)) {
      // fetch pre-cal data with pidc variant ids as input
      fetchCheckValueFromDb(variantIdSet, getDataByVarsQry());
    }

    // manipulate the most frequent value
    calculateMostFreqValue();
  }

  /**
   * @param typedQuery resultListByVersId
   * @param entityManager temp entity manager
   */
  private void fetchCheckValueFromDb(final Set<Long> pidcObjIds, final TypedQuery<Object[]> typedQuery) {
    // fill temporary table with pidc vers-ids
    fillIntoGttObject(pidcObjIds);

    Long paramId;
    byte[] checkedValue;

    for (Object[] resObj : typedQuery.getResultList()) {
      paramId = Long.parseLong(String.valueOf(resObj[1]));
      checkedValue = (byte[]) (resObj[2]);
      addCheckValueObjectToMap(paramId, checkedValue);
    }

    deleteGttObject();
  }

  /**
   * Fills the temporary table
   *
   * @param entMgrToUse entMgr
   * @param paramIDs paramIDs
   * @param entityManager
   */
  private void fillIntoGttParam(final Set<Long> paramIDs) {
    for (Long paramID : paramIDs) {
      GttParameter rec = new GttParameter();
      rec.setId(paramID);
      rec.setParamName("D");
      this.entMgrToUse.persist(rec);
    }

    this.entMgrToUse.flush();
  }

  /**
   * Fills the temporary table
   *
   * @param entityManager
   * @param entMgrToUse entMgr
   * @param paramIDs paramIDs
   */
  private void fillIntoGttObject(final Set<Long> pidcObjectIds) {
    for (Long pidcObjectId : pidcObjectIds) {
      GttObjectName rec = new GttObjectName();
      rec.setId(pidcObjectId);
      rec.setObjName("D");
      this.entMgrToUse.persist(rec);
    }

    this.entMgrToUse.flush();
  }

  /**
   * Deletes the temporary table Gtt Object Name
   *
   * @param entityManager temp entity manager
   */
  private void deleteGttObject() {
    this.entMgrToUse.createNamedQuery(GttObjectName.NS_DELETE_GTT_OBJ_NAMES).executeUpdate();
  }

  /**
   * Get the pre cal data with pidc version as input with no variants
   *
   * @param entityManager temp entity manager
   * @param pidcVersionIds pidcVersionIds
   * @return typed query
   */
  private TypedQuery<Object[]> getDataByVersWithNoVarQry() {
    return this.entMgrToUse.createNamedQuery(TRvwParameter.NQ_GET_PRECALDATA_WITH_NO_VAR, Object[].class);
  }

  /**
   * Get the pre cal data with pidc version as input with variants
   *
   * @param entityManager temp entity manager
   * @param pidcVersionIds pidcVersionIds
   * @return typed query
   */
  private TypedQuery<Object[]> getDataByVersWithVarsQry() {
    return this.entMgrToUse.createNamedQuery(TRvwParameter.NQ_GET_PRECALDATA_WITH_VARS, Object[].class);
  }

  /**
   * Get the pre cal data with pidc version as input
   *
   * @param entityManager temp entity manager
   * @param variantIdsSet variantIdsSet
   * @return typed query
   */
  private TypedQuery<Object[]> getDataByVarsQry() {
    return this.entMgrToUse.createNamedQuery(TRvwParameter.NQ_GET_PRECALDATA_WITH_PIDC_VAR_IDS, Object[].class);
  }

  /**
   * manipulates most frequent value
   *
   * @throws DataException Exception in ws
   */
  private void calculateMostFreqValue() throws DataException {

    Map<Long, PreCalibrationDataOutput> paramPreCalDataDetails = new HashMap<>();

    // Iterating all the value list
    if (null != this.paramChkValMap) {
      for (Entry<Long, List<byte[]>> chkValEntry : this.paramChkValMap.entrySet()) {
        doCalculateMostFreqValue(paramPreCalDataDetails, chkValEntry);
      }
    }

    this.response.setParamPreCalDataDetails(paramPreCalDataDetails);
  }

  private void doCalculateMostFreqValue(final Map<Long, PreCalibrationDataOutput> paramPreCalDataDataMap,
      final Entry<Long, List<byte[]>> chkValEntry)
      throws DataException {

    List<byte[]> checkValueList = chkValEntry.getValue();

    // Key = check sum, value = count of check values of the parameter
    Map<Long, Integer> chkValChecksumCountMap = new HashMap<>();

    // Current most frequent check value
    byte[] mfCheckValue = null;

    // the type of caldata
    String type = null;

    int mfcvCount = 0;

    int totalValueCount = 0;
    for (byte[] checkValByte : checkValueList) {
      CalData calData = toCalData(checkValByte);
      if (null != calData.getCalDataPhy()) {
        long checksum = calData.getCalDataPhy().getChecksum();
        type = calData.getCalDataPhy().getType();
        Integer count = chkValChecksumCountMap.get(checksum);
        count = count == null ? 1 : (count + 1);
        chkValChecksumCountMap.put(checksum, count);
        totalValueCount++;
        if (count > mfcvCount) {
          // most frequent value count
          mfcvCount = count;

          // most frequent value in byte
          mfCheckValue = checkValByte;

        }
      }
    }

    // most freq value percent
    BigDecimal mfvPercent = CommonUtils.calculatePercentage(mfcvCount, totalValueCount, 2, RoundingMode.HALF_UP);

    Integer otherValuesCount = 0;
    Integer totalSize = chkValChecksumCountMap.size();

    if (chkValChecksumCountMap.size() > 1) {
      // other values count
      otherValuesCount = totalSize - 1;
    }

    // storing the statistics information for one param-id
    PreCalibrationDataOutput calibrationDatOutput = new PreCalibrationDataOutput();
    calibrationDatOutput.setMostFreqValue(mfCheckValue);
    calibrationDatOutput.setOtherCheckValuesCount(otherValuesCount);
    calibrationDatOutput.setPercentOfMostFreqCheckValue(mfvPercent);
    calibrationDatOutput.setType(type);

    Long paramId = chkValEntry.getKey();
    paramPreCalDataDataMap.put(paramId, calibrationDatOutput);
  }

  private CalData toCalData(final byte[] checkValByte) throws DataException {
    try {
      return CalDataUtil.getCalDataObj(checkValByte);
    }
    catch (ClassNotFoundException | IOException exp) {
      throw new DataException("Invalid CalData file", exp);
    }
  }

  /**
   * Adds the check value
   *
   * @param cdrResultParameter cdrResultParameter
   */
  private void addCheckValueObjectToMap(final Long paramId, final byte[] checkedValueByte) {
    if (null == this.paramChkValMap) {
      this.paramChkValMap = new HashMap<>();
    }

    List<byte[]> list = this.paramChkValMap.computeIfAbsent(paramId, k -> new ArrayList<>());
    list.add(checkedValueByte);
  }

  /**
   * @param em
   */
  private void closeEm(final EntityManager em) {
    if ((em != null) && em.isOpen()) {
      em.close();
    }
  }

}
