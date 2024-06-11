package com.bosch.caltool.icdm.bo.cdr;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.DataNotFoundException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TWpArchival;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.WpArchival;


/**
 * Loader class for WpArchival
 *
 * @author msp5cob
 */
public class WpArchivalLoader extends AbstractBusinessObject<WpArchival, TWpArchival> {


  /**
   *
   */
  private static final String FOR_PIDC_A2L_ID = " for Pidc A2L Id : ";
  /**
   *
   */
  private static final String AND_WP_ID = " and WP Id: ";
  /**
   *
   */
  private static final String FETCHING_WP_BASELINES_FOR_GIVEN_VARIANT_ID =
      "Fetching WP Baselines for given Variant Id: ";
  /**
  *
  */
  private static final String FETCHING_WP_BASELINES_RESP_ID = "and Resp Id: ";
  /**
  *
  */
  private static final String FETCHING_WP_BASELINES_FOR_GIVEN_PIDC_VERSION_ID =
      "Fetching WP Baselines for given Pidc Veriosn Id: ";
  /**
   *
   */
  private static final String NO_DATA_FOR_GIVEN_INPUT = "No Work Package Baselines Found for given input parameters";
  /**
   *
   */
  private static final String NO_DATA_FOR_PIDC_A2L = "No Work Package Baselines Found for given PIDC A2L ID : ";
  /**
   *
   */
  private static final String INPUT_PARAMETER_MISSING_MSG = "Input Parameter 'pidca2lid' is mandatory";
  /**
   *
   */
  private static final String RESP_ID = "respId";
  /**
  *
  */
  private static final String WP_NAME = "wpName";
  /**
   *
   */
  private static final String VARIANT_ID = "variantId";
  /**
   *
   */
  private static final String PIDC_A2L_ID = "pidcA2lId";
  /**
  *
  */
  private static final String PIDC_VERSION_ID = "pidcVersId";

  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public WpArchivalLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.WP_ARCHIVAL, TWpArchival.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected WpArchival createDataObject(final TWpArchival entity) throws DataException {
    WpArchival object = new WpArchival();

    object.setId(entity.getWpArchivalId());
    object.setBaselineName(entity.getBaselineName());
    object.setPidcVersId(entity.getPidcVersId());
    object.setPidcVersFullname(entity.getPidcVersFullname());
    object.setPidcA2lId(entity.getPidcA2lId());
    object.setA2lFilename(entity.getA2lFilename());
    object.setVariantId(entity.getVariantId());
    object.setVariantName(entity.getVariantName());
    object.setRespId(entity.getRespId());
    object.setRespName(entity.getRespName());
    object.setWpId(entity.getWpId());
    object.setWpName(entity.getWpName());
    object.setWpDefnVersId(entity.getWpDefnVersId());
    object.setWpDefnVersName(entity.getWpDefnVersName());
    object.setFileArchivalStatus(entity.getFileArchivalStatus());

    setCommonFields(object, entity);

    return object;
  }

  /**
   * @param pidcA2lId pidcA2lId
   * @return Set<WpArchival>
   * @throws DataException exception when data not found
   */
  public Set<WpArchival> getBaselinesForPidcA2l(final Long pidcA2lId) throws DataException {
    if (CommonUtils.isNull(pidcA2lId)) {
      throw new InvalidInputException(INPUT_PARAMETER_MISSING_MSG);
    }
    TypedQuery<TWpArchival> query =
        getEntMgr().createNamedQuery(TWpArchival.GET_BASELINES_FOR_PIDC_A2L_ID, TWpArchival.class);
    query.setParameter(PIDC_A2L_ID, pidcA2lId);
    Set<WpArchival> wpArchivalSet = new HashSet<>();
    for (TWpArchival tWpArchival : query.getResultList()) {
      wpArchivalSet.add(createDataObject(tWpArchival));
    }

    if (CommonUtils.isNullOrEmpty(wpArchivalSet)) {
      throw new DataNotFoundException(NO_DATA_FOR_PIDC_A2L + pidcA2lId);
    }

    return wpArchivalSet;
  }


  /**
   * @param pidcVersId pidcVersionId (Optional)
   * @param pidcA2lId pidcA2lId (Optional)
   * @param variantId variantId (Optional)
   * @param respId respId (Optional)
   * @param wpId wpId (Optional)
   * @return WpArchival set
   * @throws DataException DataException
   */
  public Set<WpArchival> getFilteredBaselinesForPidc(final Long pidcVersId, final Long pidcA2lId, final Long variantId,
      final Long respId, final String wpId, final String nodeName)
      throws DataException {
    Set<WpArchival> wpArchivalSet = new HashSet<>();
    TypedQuery<TWpArchival> query;
    query = getFilteredQueryForPidcNoA2l(pidcVersId, pidcA2lId, variantId, respId, wpId, nodeName);

    for (TWpArchival tWpArchival : query.getResultList()) {
      wpArchivalSet.add(createDataObject(tWpArchival));
    }

    if (CommonUtils.isNullOrEmpty(wpArchivalSet)) {
      throw new DataNotFoundException(NO_DATA_FOR_GIVEN_INPUT);
    }

    return wpArchivalSet;
  }


  /**
   * @param pidcA2lId (Optional)
   * @param variantId (Optional)
   * @param respId (Optional)
   * @param wpName (Optional)
   * @param nodeName
   * @return
   */
  private TypedQuery<TWpArchival> getFilteredQueryForPidcNoA2l(final Long pidcVersId, final Long pidcA2lId,
      final Long variantId, final Long respId, final String wpName, final String nodeName) {

    switch (nodeName) {
      case "RVW_WORKPACAKGES_TITLE_NODE":
      case "RVW_RESPONSIBILITIES_TITLE_NODE":
        getLogger().info(FETCHING_WP_BASELINES_FOR_GIVEN_VARIANT_ID + variantId);
        if ((variantId != null) && variantId.equals(-1L)) {
          /* GET_BASELINES_FOR_NO_VARIANT_ID_ONLY */
          TypedQuery<TWpArchival> noVariantAlone =
              getEntMgr().createNamedQuery(TWpArchival.GET_BASELINES_FOR_NO_VARIANT_ID_ONLY, TWpArchival.class);
          noVariantAlone.setParameter(PIDC_VERSION_ID, pidcVersId);
          noVariantAlone.setParameter(VARIANT_ID, variantId);
          return noVariantAlone;
        }
        /* GET_BASELINES_FOR_VARIANT_ID_ONLY */
        TypedQuery<TWpArchival> variantAlone =
            getEntMgr().createNamedQuery(TWpArchival.GET_BASELINES_FOR_VARIANT_ID_ONLY, TWpArchival.class);
        variantAlone.setParameter(VARIANT_ID, variantId);
        return variantAlone;

      case "RVW_RESPONSIBILITY_NODE":
        getLogger()
            .info(FETCHING_WP_BASELINES_FOR_GIVEN_VARIANT_ID + variantId + FETCHING_WP_BASELINES_RESP_ID + respId);
        /* GET_BASELINES_FOR_VARIANT_ID_AND_RESP_ID */
        TypedQuery<TWpArchival> variantAndResp =
            getEntMgr().createNamedQuery(TWpArchival.GET_BASELINES_FOR_VARIANT_ID_AND_RESP_ID, TWpArchival.class);
        variantAndResp.setParameter(VARIANT_ID, variantId);
        variantAndResp.setParameter(RESP_ID, respId);
        return variantAndResp;
      case "RVW_RESP_WP_NODE":
        getLogger()
            .info(FETCHING_WP_BASELINES_FOR_GIVEN_VARIANT_ID + variantId + ", Resp Id: " + respId + AND_WP_ID + wpName);
        /* GET_BASELINES_FOR_VARIANT_ID_AND_RESP_ID_AND_WP_ID */
        TypedQuery<TWpArchival> varaiantRespAndWpName = getEntMgr()
            .createNamedQuery(TWpArchival.GET_BASELINES_FOR_VARIANT_ID_AND_RESP_ID_AND_WP_ID, TWpArchival.class);
        varaiantRespAndWpName.setParameter(VARIANT_ID, variantId);
        varaiantRespAndWpName.setParameter(RESP_ID, respId);
        varaiantRespAndWpName.setParameter(WP_NAME, wpName);
        return varaiantRespAndWpName;
      case "PIDC_A2L_VAR_NODE":
        getLogger().info(FETCHING_WP_BASELINES_FOR_GIVEN_VARIANT_ID + variantId + FOR_PIDC_A2L_ID + pidcA2lId);
        /* GET_BASELINES_FOR_VARIANT_ID */
        TypedQuery<TWpArchival> a2lAndVaraiant =
            getEntMgr().createNamedQuery(TWpArchival.GET_BASELINES_FOR_VARIANT_ID, TWpArchival.class);
        a2lAndVaraiant.setParameter(PIDC_A2L_ID, pidcA2lId);
        a2lAndVaraiant.setParameter(VARIANT_ID, variantId);
        return a2lAndVaraiant;
      case "PIDC_A2L_RESPONSIBILITY_NODE":
        getLogger().info(FETCHING_WP_BASELINES_FOR_GIVEN_VARIANT_ID + variantId + FETCHING_WP_BASELINES_RESP_ID +
            respId + FOR_PIDC_A2L_ID + pidcA2lId);
        /* GET_BASELINES_FOR_RESP_ID */
        TypedQuery<TWpArchival> a2lVariantAndResp =
            getEntMgr().createNamedQuery(TWpArchival.GET_BASELINES_FOR_RESP_ID, TWpArchival.class);
        a2lVariantAndResp.setParameter(PIDC_A2L_ID, pidcA2lId);
        a2lVariantAndResp.setParameter(VARIANT_ID, variantId);
        a2lVariantAndResp.setParameter(RESP_ID, respId);
        return a2lVariantAndResp;
      case "PIDC_A2L_WP_NODE":
        getLogger().info(FETCHING_WP_BASELINES_FOR_GIVEN_VARIANT_ID + variantId + FETCHING_WP_BASELINES_RESP_ID +
            respId + FOR_PIDC_A2L_ID + pidcA2lId);
        /* GET_BASELINES_FOR_WP_RESP_NAME */
        TypedQuery<TWpArchival> a2lVarAndRespAndWpName =
            getEntMgr().createNamedQuery(TWpArchival.GET_BASELINES_FOR_WP_RESP_NAME, TWpArchival.class);
        a2lVarAndRespAndWpName.setParameter(PIDC_A2L_ID, pidcA2lId);
        a2lVarAndRespAndWpName.setParameter(VARIANT_ID, variantId);
        a2lVarAndRespAndWpName.setParameter(RESP_ID, respId);
        a2lVarAndRespAndWpName.setParameter(WP_NAME, wpName);
        return a2lVarAndRespAndWpName;
      case "REV_RES_WP_GRP_NODE":
        getLogger().info(FETCHING_WP_BASELINES_FOR_GIVEN_VARIANT_ID + variantId + " WP Name: " + wpName);
        /* GET_BASELINES_FOR_VARIANT_ID_AND_WP_NAME */
        TypedQuery<TWpArchival> VariantAndWpName =
            getEntMgr().createNamedQuery(TWpArchival.GET_BASELINES_FOR_VARIANT_ID_AND_WP_NAME, TWpArchival.class);
        VariantAndWpName.setParameter(VARIANT_ID, variantId);
        VariantAndWpName.setParameter(WP_NAME, wpName);
        return VariantAndWpName;
      case "PIDC_A2L":
        getLogger().info(FETCHING_WP_BASELINES_FOR_GIVEN_PIDC_VERSION_ID + pidcVersId);
        /* GET_BASELINES_FOR_PIDC_VERSION_ID_AND_PIDC_A2L */
        TypedQuery<TWpArchival> pidcVersAndA2l =
            getEntMgr().createNamedQuery(TWpArchival.GET_BASELINES_FOR_PIDC_VERSION_ID_AND_PIDC_A2L, TWpArchival.class);
        pidcVersAndA2l.setParameter(PIDC_VERSION_ID, pidcVersId);
        pidcVersAndA2l.setParameter(PIDC_A2L_ID, pidcA2lId);
        return pidcVersAndA2l;
      case "ACTIVE_PIDC_VERSION":
        getLogger()
            .info(FETCHING_WP_BASELINES_FOR_GIVEN_PIDC_VERSION_ID + pidcVersId + " and " + FOR_PIDC_A2L_ID + pidcA2lId);
        /* GET_BASELINES_FOR_PIDC_VERSION_ID */
        TypedQuery<TWpArchival> pidcVersAlone =
            getEntMgr().createNamedQuery(TWpArchival.GET_BASELINES_FOR_PIDC_VERSION_ID, TWpArchival.class);
        pidcVersAlone.setParameter(PIDC_VERSION_ID, pidcVersId);
        return pidcVersAlone;
      default:
        throw new IllegalArgumentException("Invalid nodeName: " + nodeName);
    }


  }


}
