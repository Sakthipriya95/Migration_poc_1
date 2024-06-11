package com.bosch.caltool.icdm.bo.cdr.qnaire;

import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireRespVersion;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireResponse;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;


/**
 * Loader class for RvwQnaireRespVersion
 *
 * @author say8cob
 */
public class RvwQnaireRespVersionLoader extends AbstractBusinessObject<RvwQnaireRespVersion, TRvwQnaireRespVersion> {

  /**
   * Revision number of 'Working Set' response version
   */
  public static final long QNAIRE_RESP_WORKING_SET_REV_NUM = 0L;

  /**
   * Response version name format. Pattern : revision : date - name
   */
  private static final String RESP_VERS_NAME_FORMAT = "{} : {} - {}";

  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public RvwQnaireRespVersionLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.RVW_QNAIRE_RESP_VERSION, TRvwQnaireRespVersion.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RvwQnaireRespVersion createDataObject(final TRvwQnaireRespVersion entity) throws DataException {
    RvwQnaireRespVersion object = new RvwQnaireRespVersion();

    setCommonFields(object, entity);

    object.setQnaireRespId(entity.getTRvwQnaireResponse().getQnaireRespId());
    object.setName(buildName(entity));
    object.setVersionName(entity.getName());
    object.setDescription(entity.getDescription());
    object.setQnaireVersionId(entity.getTQuestionnaireVersion().getQnaireVersId());
    object.setRevNum(entity.getRevNum());
    object.setQnaireRespVersStatus(entity.getQnaireVersStatus());
    object.setReviewedDate(timestamp2String(entity.getReviewedDate()));
    object.setReviewedUser(entity.getReviewedUser());

    return object;
  }


  /**
   * @param entity
   */
  private String buildName(final TRvwQnaireRespVersion entity) {
    return isWorkingSet(entity) ? ApicConstants.WORKING_SET_NAME : buildNameForBaselines(entity);
  }

  /**
   * @param entity
   * @return
   */
  private String buildNameForBaselines(final TRvwQnaireRespVersion entity) {
    return CommonUtils.buildString(RESP_VERS_NAME_FORMAT, entity.getRevNum(),
        timestamp2String(entity.getReviewedDate(), DateFormat.DATE_FORMAT_10), entity.getName());
  }

  /**
   * Get RvwQnaireRespVersions by RespID
   *
   * @param qnaireRespId as input
   * @return Map RvwQnaireRespVersion object
   * @throws DataException error while retrieving data
   */
  public Map<Long, RvwQnaireRespVersion> getQnaireRespVersionsByRespId(final Long qnaireRespId) throws DataException {
    RvwQnaireResponseLoader respLoader = new RvwQnaireResponseLoader(getServiceData());

    // Validate Rvw Qnaire Resp Id
    respLoader.validateId(qnaireRespId);

    Map<Long, RvwQnaireRespVersion> allQnaireRespVersionsMap = new HashMap<>();
    for (TRvwQnaireRespVersion entity : respLoader.getEntityObject(qnaireRespId).getTRvwQnaireRespVersions()) {
      allQnaireRespVersionsMap.put(entity.getQnaireRespVersId(), createDataObject(entity));
    }
    return allQnaireRespVersionsMap;
  }

  /**
   * Method returns working set of RvwQnaireRespVersions object
   *
   * @param qnaireRespId as input
   * @return RvwQnaireRespVersion
   * @throws DataException as exception
   */
  public RvwQnaireRespVersion getQnaireRespVersWorkingSet(final Long qnaireRespId) throws DataException {
    RvwQnaireRespVersion rvwQnaireRespVersion = new RvwQnaireRespVersion();
    TRvwQnaireRespVersion tRvwQnaireRespVersion = fetchWorkingSet(qnaireRespId);
    if (null != tRvwQnaireRespVersion) {
      rvwQnaireRespVersion = createDataObject(tRvwQnaireRespVersion);
    }
    return rvwQnaireRespVersion;
  }

  /**
   * @param qnaireRespId
   * @param rvwQnaireRespVersion
   * @return
   * @throws DataException
   */
  private TRvwQnaireRespVersion fetchWorkingSet(final Long qnaireRespId) {
    TRvwQnaireResponse tRvwQnaireResponse = new RvwQnaireResponseLoader(getServiceData()).getEntityObject(qnaireRespId);
    for (TRvwQnaireRespVersion tRvwQnaireRespVersion : tRvwQnaireResponse.getTRvwQnaireRespVersions()) {
      if (isWorkingSet(tRvwQnaireRespVersion)) {
        return tRvwQnaireRespVersion;
      }
    }
    return null;
  }

  /**
   * Method to identify a questionnaire response is available for division id To prevent the editing of Division
   * attribute on PIDC level
   *
   * @param qnaireRespId as input
   * @param valueId as input
   * @return true if there is qnaire resp version created for the input division id
   */
  public boolean isQnaireConfigValUsedInQnaireRespVers(final Long qnaireRespId, final Long valueId) {
    TRvwQnaireRespVersion tRvwQnaireRespVersion = fetchWorkingSet(qnaireRespId);
    if (null != tRvwQnaireRespVersion) {
      TabvAttrValue tabvAttrValue = tRvwQnaireRespVersion.getTQuestionnaireVersion().getTQuestionnaire()
          .getTWorkpackageDivision().getTabvAttrValue();
      return CommonUtils.isEqual(tabvAttrValue.getValueId(), valueId);
    }
    return false;
  }

  /**
   * @param tQnaireRespVers TRvwQnaireRespVersion
   * @return true if this entity is working set version
   */
  static boolean isWorkingSet(final TRvwQnaireRespVersion tQnaireRespVers) {
    return tQnaireRespVers.getRevNum() == QNAIRE_RESP_WORKING_SET_REV_NUM;
  }

  /**
   * @param qnaireRespVers Qnaire Response Version
   * @return true if this model is working set version
   */
  public static boolean isWorkingSet(final RvwQnaireRespVersion qnaireRespVers) {
    return qnaireRespVers.getRevNum() == QNAIRE_RESP_WORKING_SET_REV_NUM;
  }

}
