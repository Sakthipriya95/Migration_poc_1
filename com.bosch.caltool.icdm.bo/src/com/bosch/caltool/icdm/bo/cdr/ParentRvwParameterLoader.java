/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.GttObjectName;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParameter;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;


/**
 * ICDM-2204
 *
 * @author mkl2cob
 */
public class ParentRvwParameterLoader {

  /**
   * Set index value of project-id if variant available
   */
  private static final int SET_INDEXOF_PROJID_WITH_VAR = 2;
  /**
   * Set index value of variant-id
   */
  private static final int SET_INDEXOF_VARID_WITH_VAR = 1;
  /**
   * Set index value of project-id if variant not available
   */
  private static final int SET_INDEXOF_PROJID_WITHOUT_VAR = 1;
  /**
   * review rank value - 4
   */
  private static final int RVW_RANK_VALUE_FOUR = 4;
  /**
   * review rank value - 3
   */
  private static final int RVW_RANK_VALUE_THREE = 3;
  /**
   * review rank value - 2
   */
  private static final int RVW_RANK_VALUE_TWO = 2;
  /**
   * review rank value - 1
   */
  private static final int RVW_RANK_VALUE_ONE = 1;
  /**
   * index of param id
   */
  private static final int INDEX_OF_PARAM_ID = 0;
  /**
   * index of review rank
   */
  private static final int INDEX_OF_RVW_RANK = 2;
  /**
   * index of result param id
   */
  private static final int INDEX_OF_RES_PARAM_ID = 1;
  /**
   * index of rev label based on created date
   */
  private static final int INDEX_OF_REV_LABEL = 3;
  /**
   * ParentRvwParamLoaderInput
   */
  private ParentRvwParamLoaderInput loaderInput;

  /**
   * the map to be returned after the fetching
   */
  private ConcurrentMap<Long, CDRResultParameter> rvwResObjMap = new ConcurrentHashMap<>();
  private final ServiceData serviceData;

  /**
   * Constructor
   *
   * @param cdrDataProvider CDRDataProvider
   */
  public ParentRvwParameterLoader(final ServiceData serviceData) {
    this.serviceData = serviceData;
  }

  /**
   * @param loaderInput ParentRvwParamLoaderInput
   * @return map of param name:type and parent parameter
   */
  public Map<Long, CDRResultParameter> fetchParentParameters(final ParentRvwParamLoaderInput loaderInput) {
    this.loaderInput = loaderInput;
    this.rvwResObjMap.clear();
    getLogger().info("ParentRvwParameterLoader : parameter details loading started. Inputs : {}", loaderInput);
    fetchParamsFromDb();
    getLogger().info(
        "ParentRvwParameterLoader : parameter details loading completed. Number of parameters retrieved : {}",
        this.rvwResObjMap.size());
    return this.rvwResObjMap;
  }


  /**
   * fetch param details and result details from db
   */
  private void fetchParamsFromDb() {
    EntityManager entMgr = null;

    try {
      entMgr = ObjectStore.getInstance().getEntityManagerFactory().createEntityManager();

      entMgr.getTransaction().begin();
      // fill the temporary table
      fillTempTable(entMgr);
      // create the query
      TypedQuery<Object[]> reportQry = createPrjtDataQry(entMgr);
      // find the parent params using the consider flags and ranks
      findParentParams(reportQry);
    }
    catch (Exception exp) {
      getLogger().error(exp.getLocalizedMessage(), exp);
    }
    finally {
      if (entMgr != null) {
        entMgr.getTransaction().rollback();
        entMgr.close();
      }
    }

  }

  /**
   * find the parent params using the consider flags and ranks
   *
   * @param reportQry
   * @throws DataException
   */
  private void findParentParams(final TypedQuery<Object[]> reportQry) throws DataException {
    // create the comparison object map for each param id
    ConcurrentMap<Long, Object[]> comparisonObjMap = new ConcurrentHashMap<>();
    this.rvwResObjMap = new ConcurrentHashMap<>();
    // execute the query
    List<Object[]> resultList = reportQry.getResultList();
    boolean considerThisResult;
    // remove the objects that are not applicable
    for (Object[] resObj : resultList) {
      considerThisResult = false;
      BigDecimal reviewRank = (BigDecimal) resObj[INDEX_OF_RVW_RANK];

      int intValueOfRvwRank = reviewRank.intValue();
      if (this.loaderInput.isConsiderOfficialLockd()) {
        if (intValueOfRvwRank == RVW_RANK_VALUE_ONE) {
          // if it is official locked
          considerThisResult = true;
        }
      }
      else if (this.loaderInput.isConsiderOfficial() &&
          ((intValueOfRvwRank == RVW_RANK_VALUE_ONE) || (intValueOfRvwRank == RVW_RANK_VALUE_TWO))) {
        // if it is official review
        considerThisResult = true;
      }
      if (this.loaderInput.isConsiderStartLocked()) {
        if (intValueOfRvwRank == RVW_RANK_VALUE_THREE) {
          // if it is start locked
          considerThisResult = true;
        }
      }
      else if (this.loaderInput.isConsiderStart() &&
          ((intValueOfRvwRank == RVW_RANK_VALUE_THREE) || (intValueOfRvwRank == RVW_RANK_VALUE_FOUR))) {
        // if it is start review
        considerThisResult = true;
      }
      if (considerThisResult) {
        // get the param id
        Long paramId = ((BigDecimal) resObj[INDEX_OF_PARAM_ID]).longValue();
        Object[] objInMap = comparisonObjMap.get(paramId);
        if (CommonUtils.isNull(objInMap)) {
          // if there is no other result parameter that could be considered as parent
          // fill the map with this result parameter
          fillMapWithNewResultParam(comparisonObjMap, resObj, paramId);
        }
        else {
          // if there exists an object that could be considered as parent parameter

          int compareResult = compareResultParams(resObj, reviewRank, objInMap);

          if (compareResult < 0) {
            // if the rank of the current object is less than the object in map
            fillMapWithNewResultParam(comparisonObjMap, resObj, paramId);
          }

        }
      }
    }

  }

  /**
   * @param resObj
   * @param reviewRank
   * @param objInMap
   * @return
   */
  private int compareResultParams(final Object[] resObj, final BigDecimal reviewRank, final Object[] objInMap) {
    int compareResult;
    BigDecimal rankOfObjInMap = (BigDecimal) objInMap[INDEX_OF_RVW_RANK];
    compareResult = ApicUtil.compareBigDecimal(reviewRank, rankOfObjInMap);
    if (compareResult == 0) {
      // initialise the ranks to the rev label which is based on created date
      compareResult = ApicUtil.compareBigDecimal((BigDecimal) resObj[INDEX_OF_REV_LABEL],
          (BigDecimal) objInMap[INDEX_OF_REV_LABEL]);
    }
    return compareResult;
  }

  /**
   * @param comparisonObjMap
   * @param resObj
   * @param paramId
   * @throws DataException
   */
  private void fillMapWithNewResultParam(final ConcurrentMap<Long, Object[]> comparisonObjMap, final Object[] resObj,
      final Long paramId) throws DataException {
    // put it inside comparison object map
    comparisonObjMap.put(paramId, resObj);
    CDRResultParameterLoader paramLoader = new CDRResultParameterLoader(this.serviceData);

    // put it inside result object map
    this.rvwResObjMap.put(paramId, paramLoader
        .createDataObject(paramLoader.getEntityObject(((BigDecimal) resObj[INDEX_OF_RES_PARAM_ID]).longValue())));
  }

  /**
   * @param entMgr EntityManager
   */
  private void fillTempTable(final EntityManager entMgr) {
    cleanupTempTable(entMgr);

    GttObjectName tempParam;

    // Create entities for all the parameters
    for (Parameter param : this.loaderInput.getParamSet()) {
      tempParam = new GttObjectName();
      tempParam.setId(param.getId());

      entMgr.persist(tempParam);

    }

    entMgr.flush();
  }

  /**
   * @param entMgr
   */
  private void cleanupTempTable(final EntityManager entMgr) {

    getLogger().debug("Clearing temp table ...");

    // Delete the existing records in this temp table, if any
    final Query delQuery = entMgr.createQuery("delete from GttObjectName temp");
    delQuery.executeUpdate();

  }


  /**
   * @return logger logger
   */
  private ILoggerAdapter getLogger() {
    return ObjectStore.getInstance().getLogger();
  }

  /**
   * @param entMgr EntityManager
   * @return TypedQuery<Object[]>
   */
  private TypedQuery<Object[]> createPrjtDataQry(final EntityManager entMgr) {
    TypedQuery<Object[]> projectDataRvwQuery;
    if (this.loaderInput.getVariantId() == null) {
      // for delta review without variant
      projectDataRvwQuery = entMgr.createNamedQuery(TRvwParameter.NNQ_GET_PROJDATA_NO_VAR, Object[].class);
      projectDataRvwQuery.setParameter(SET_INDEXOF_PROJID_WITHOUT_VAR, this.loaderInput.getProjectId());
    }
    else {
      // for delta review with variant
      projectDataRvwQuery = entMgr.createNamedQuery(TRvwParameter.NNQ_GET_PROJDATA_WITH_VAR, Object[].class);
      projectDataRvwQuery.setParameter(SET_INDEXOF_VARID_WITH_VAR, this.loaderInput.getVariantId());
      projectDataRvwQuery.setParameter(SET_INDEXOF_PROJID_WITH_VAR, this.loaderInput.getProjectId());
    }
    return projectDataRvwQuery;
  }
}
