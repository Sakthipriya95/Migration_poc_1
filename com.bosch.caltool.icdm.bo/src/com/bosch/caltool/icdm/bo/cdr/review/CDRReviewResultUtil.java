/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.review;

import java.util.Arrays;

import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.general.CommonParamKey;


/**
 * @author ukt1cob
 */
public final class CDRReviewResultUtil {

  private CDRReviewResultUtil() {
    //
  }

  /**
   * @param serviceData service Data
   * @param pidcVersId pidcVersId
   * @return true if OBD option is enabled for the given cdr review result
   * @throws IcdmException exception
   */
  public static final boolean isOBDOptionEnabled(final ServiceData serviceData, final Long pidcVersId)
      throws IcdmException {

    return isPidcDivIdMappedToGivComParam(serviceData, pidcVersId, CommonParamKey.DIVISIONS_WITH_OBD_OPTION);
  }

  /**
   * @param serviceData service Data
   * @param pidcVersId pidcVersId
   * @return true if OBD option is enabled for the given cdr review result
   * @throws IcdmException exception
   */
  public static final boolean isSimpQuesEnabled(final ServiceData serviceData, final Long pidcVersId)
      throws IcdmException {

    return isPidcDivIdMappedToGivComParam(serviceData, pidcVersId, CommonParamKey.DIV_WITH_SIMPL_GEN_QNAIRE);
  }

  /**
   * @param serviceData service Data
   * @param pidcVersId pidcVersId
   * @return true if OBD option is enabled for the given cdr review result
   * @throws IcdmException exception
   */
  public static final boolean isRvwQnaireRespAllowed(final ServiceData serviceData, final Long pidcVersId)
      throws IcdmException {

    return isPidcDivIdMappedToGivComParam(serviceData, pidcVersId, CommonParamKey.DIVISIONS_WITH_QNAIRES);
  }


  /**
   * @param serviceData
   * @param pidcVersId
   * @param commonParamKey
   * @throws IcdmException
   * @throws DataException
   */
  private static final boolean isPidcDivIdMappedToGivComParam(final ServiceData serviceData, final Long pidcVersId,
      final CommonParamKey commonParamKey)
      throws IcdmException {
    Long pidcDivId = new PidcVersionLoader(serviceData).getDivIdByPidcVersId(pidcVersId);
    String comParamDivIds = new CommonParamLoader(serviceData).getValue(commonParamKey);

    if (CommonUtils.isNotNull(comParamDivIds) && CommonUtils.isNotNull(pidcDivId)) {
      // comma seperated division id's
      String[] divIdsArray = comParamDivIds.split(",");
      return Arrays.asList(divIdsArray).contains(pidcDivId.toString());
    }
    return false;
  }


}

