/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwFunction;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParameter;


/**
 * @author bne4cob
 */
public class CDRResultFunction extends AbstractCdrObject implements Comparable<CDRResultFunction> {


  /**
   * Result params of Review function
   */
  private Map<Long, CDRResultParameter> paramsMap;

  /**
   * @param dataProvider the data provider
   * @param objID id
   */
  protected CDRResultFunction(final CDRDataProvider dataProvider, final Long objID) {
    super(dataProvider, objID);
    dataProvider.getDataCache().getCDRResultFunctionMap().put(objID, this);
  }

  /**
   * @return CDRFunction
   */
  public CDRFunction getCDRFunction() {
    String funcName = getEntityProvider().getDbCDRResFunction(getID()).getTFunction().getName();
    return getDataCache().getCDRFunction(funcName);
  }


  /**
   * @return CDRResult
   */
  public CDRResult getCDRResult() {
    TRvwFunction dbReviewFunc = getEntityProvider().getDbCDRResFunction(getID());
    long resultId = dbReviewFunc.getTRvwResult().getResultId();
    return getDataCache().getCDRResult(resultId);
  }

  /**
   * Gets the Function name
   *
   * @return func name
   */
  @Override
  public String getName() {
    return getEntityProvider().getDbCDRResFunction(getID()).getTFunction().getName();
  }

  /**
   * Returns the parameters of this review for the given function
   *
   * @return the parameters
   */
  protected Map<Long, CDRResultParameter> getParameterMap() {
    if (this.paramsMap == null) {
      this.paramsMap = new HashMap<Long, CDRResultParameter>();
      Set<TRvwParameter> tRvwParameters = getEntityProvider().getDbCDRResFunction(getID()).getTRvwParameters();
      if (tRvwParameters == null) {
        return this.paramsMap;
      }

      CDRResultParameter resParam;
      for (TRvwParameter dbRvwPrm : tRvwParameters) {
        resParam = getDataCache().getAllCDRResultParameters().get(dbRvwPrm.getRvwParamId());
        if (resParam == null) {
          resParam = new CDRResultParameter(getDataCache().getDataProvider(), dbRvwPrm.getRvwParamId()); // NOPMD
        }
        this.paramsMap.put(resParam.getID(), resParam);
      }
    }
    return this.paramsMap;
  }

  /**
   * Returns the parameters of this review for the given function
   *
   * @return the parameters
   */
  public SortedSet<CDRResultParameter> getParameters() {
    return new TreeSet<CDRResultParameter>(getParameterMap().values());
  }

  /**
   * @return the created user
   */
  @Override
  public final String getCreatedUser() {
    return getEntityProvider().getDbCDRResFunction(getID()).getCreatedUser();
  }

  /**
   * @return the created date
   */
  @Override
  public final Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbCDRResFunction(getID()).getCreatedDate());
  }

  /**
   * @return the modified date
   */
  @Override
  public final Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbCDRResFunction(getID()).getModifiedDate());
  }

  /**
   * @return the modified user
   */
  @Override
  public final String getModifiedUser() {
    return getEntityProvider().getDbCDRResFunction(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CDRResultFunction other) {
    return ApicUtil.compare(getName(), other.getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }

    CDRResultFunction cdrResultFunction = (CDRResultFunction) obj;
    return getID().equals(cdrResultFunction.getID());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return getID().intValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CDREntityType.CDR_RES_FUNCTION;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return getEntityProvider().getDbCDRResFunction(getID()).getTFunction().getLongname();
  }

  // ICDM-1720
  /**
   * @return the function version of the parameter present in a2l file
   */
  public String getFunctionVersion() {
    return getEntityProvider().getDbCDRResFunction(getID()).getTFuncVers();
  }

  /**
   * This method returns the function name and the function version, if function version not available, then this
   * returns only function name
   *
   * @return function name and version together
   */
  // ICDM-1333
  public String getNameWithFuncVersion() {
    String returnString = getName();
    String functionVersion = getFunctionVersion();
    if (CommonUtils.isNotEmptyString(functionVersion)) {
      returnString = CommonUtils.concatenate(returnString, " (", functionVersion, ")");
    }
    return returnString;
  }


}
