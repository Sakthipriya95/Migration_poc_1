package com.bosch.caltool.icdm.bo.a2l;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeLoader;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.general.SystemInfoProvider;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.bo.a2l.A2lResponsibilityCommon;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lResponsibility;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectidcard;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2lRespBoschGroupUser;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityModel;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.user.User;


/**
 * Loader class for A2lResponsibility
 *
 * @author pdh2cob
 */
public class A2lResponsibilityLoader extends AbstractBusinessObject<A2lResponsibility, TA2lResponsibility> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public A2lResponsibilityLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.A2L_RESPONSIBILITY, TA2lResponsibility.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public A2lResponsibility createDataObject(final TA2lResponsibility entity) throws DataException {
    A2lResponsibility object = new A2lResponsibility();

    setCommonFields(object, entity);

    object.setProjectId(entity.getTabvProjectidcard().getProjectId());
    object.setRespType(entity.getRespType());

    if (entity.getTabvApicUser() != null) {
      object.setUserId(entity.getTabvApicUser().getUserId());
    }

    object.setLLastName(entity.getLLastName());
    object.setLFirstName(entity.getLFirstName());
    object.setLDepartment(entity.getLDepartment());
    object.setAliasName(entity.getAliasName());

    resolveNameNDesc(object);

    // set deleted flag
    object.setDeleted(yOrNToBoolean(entity.getDeletedFlag()));

    return object;
  }


  private void resolveNameNDesc(final A2lResponsibility object) throws DataException {

    WpRespType respType = WpRespType.getType(object.getRespType());

    // set name as display name for default responsibility
    if (respType.getAliasBase().equals(object.getAliasName())) {
      object.setName(respType.getDispName());
    }
    else if ((respType == WpRespType.RB) && (object.getUserId() != null)) {
      User user = new UserLoader(getServiceData()).getDataObjectByID(object.getUserId());
      object.setName(user.getDescription());
      object.setDescription(user.getName());
    }
    else {
      // If user id is null then name will be resolved same as non bosch user
      setNameFromLFields(object);
    }
    if (object.getName() == null) {
      object.setName(object.getAliasName());
    }

  }


  /**
   * @param entity
   * @param object
   */
  private void setNameFromLFields(final A2lResponsibility object) {

    // resolve name from inputs (First name, Last name and Department)

    StringBuilder name = new StringBuilder();

    if (CommonUtils.isNotEmptyString(object.getLLastName())) {
      name.append(object.getLLastName());
    }
    if (CommonUtils.isNotEmptyString(object.getLFirstName())) {
      if (name.length() > 0) {
        name.append(", ");
      }
      name.append(object.getLFirstName());
    }
    if (CommonUtils.isNotEmptyString(object.getLDepartment())) {
      if (name.length() > 0) {
        name.append(" ");
      }
      name.append("(");
      name.append(object.getLDepartment());
      name.append(')');
    }

    if (name.length() > 0) {
      object.setName(name.toString());
    }


  }


  /**
   * @param pidcId - project id card id
   * @return key - A2lResponsibility id, value A2lResponsibility
   * @throws DataException exception
   */
  public A2lResponsibilityModel getByPidc(final long pidcId) throws DataException {
    A2lResponsibilityModel a2lRespModel = new A2lResponsibilityModel();
    TabvProjectidcard pidc = new PidcLoader(getServiceData()).getEntityObject(pidcId);
    List<TA2lResponsibility> a2lRespEntityList = pidc.getTA2lResponsibilityList();
    A2lRespBoschGroupUserLoader a2lRespBoschGroupUserLoader = new A2lRespBoschGroupUserLoader(getServiceData());

    UserLoader userLoader = new UserLoader(getServiceData());
    if (CommonUtils.isNotEmpty(a2lRespEntityList)) {

      for (TA2lResponsibility dbResp : a2lRespEntityList) {

        A2lResponsibility a2lResp = getDataObjectByID(dbResp.getA2lRespId());
        a2lRespModel.getA2lResponsibilityMap().put(dbResp.getA2lRespId(), a2lResp);

        if (dbResp.getTabvApicUser() != null) {
          a2lRespModel.getUserMap().put(dbResp.getTabvApicUser().getUserId(),
              userLoader.getDataObjectByID(dbResp.getTabvApicUser().getUserId()));
        }

        if (A2lResponsibilityCommon.isDefaultResponsibility(a2lResp)) {
          a2lRespModel.getDefaultA2lRespMap().put(a2lResp.getRespType(), a2lResp);
        }

        constructA2lBoschGrpUserMap(a2lRespModel, a2lRespBoschGroupUserLoader, a2lResp);
      }

      addBoschGrpUserToUserMap(a2lRespModel, userLoader);

    }


    return a2lRespModel;
  }

  /**
   * @param a2lRespModel
   * @param userLoader
   * @throws DataException
   */
  private void addBoschGrpUserToUserMap(final A2lResponsibilityModel a2lRespModel, final UserLoader userLoader)
      throws DataException {
    if (CommonUtils.isNotEmpty(a2lRespModel.getA2lBoschGrpUserMap())) {
      for (Map<Long, A2lRespBoschGroupUser> bshGrpUsrMap : a2lRespModel.getA2lBoschGrpUserMap().values()) {
        for (A2lRespBoschGroupUser a2lRespBoschGroupUser : bshGrpUsrMap.values()) {
          if (!a2lRespModel.getUserMap().containsKey(a2lRespBoschGroupUser.getUserId())) {
            a2lRespModel.getUserMap().put(a2lRespBoschGroupUser.getUserId(),
                userLoader.getDataObjectByID(a2lRespBoschGroupUser.getUserId()));
          }
        }
      }
    }
  }

  /**
   * @param a2lRespModel
   * @param a2lRespBoschGroupUserLoader
   * @param a2lResp
   * @throws DataException
   */
  private void constructA2lBoschGrpUserMap(final A2lResponsibilityModel a2lRespModel,
      final A2lRespBoschGroupUserLoader a2lRespBoschGroupUserLoader, final A2lResponsibility a2lResp)
      throws DataException {
    // if bosch dept/grp, add to new map
    if (CommonUtils.isEqual(a2lResp.getRespType(), WpRespType.RB.getCode()) && (a2lResp.getLFirstName() == null) &&
        (a2lResp.getLLastName() == null)) {
      Map<Long, A2lRespBoschGroupUser> userMap = a2lRespBoschGroupUserLoader.getBoschGrpRespUsers(a2lResp.getId());
      if (CommonUtils.isNotEmpty(userMap)) {
        Map<Long, A2lRespBoschGroupUser> existingMap = a2lRespModel.getA2lBoschGrpUserMap().get(a2lResp.getId());
        if (CommonUtils.isNotEmpty(existingMap)) {
          existingMap.putAll(userMap);
        }
        else {
          a2lRespModel.getA2lBoschGrpUserMap().put(a2lResp.getId(), userMap);
        }
      }
    }
  }

  /**
   * @param pidcId - project id card id
   * @return key - a2lResp id, value a2lResp
   * @throws DataException exception
   */
  public Map<String, A2lResponsibility> getDefaultA2lResp(final long pidcId) throws DataException {
    Map<String, A2lResponsibility> pidcRespMap = new HashMap<>();
    TabvProjectidcard pidc = new PidcLoader(getServiceData()).getEntityObject(pidcId);
    List<TA2lResponsibility> a2lRespEntityList = pidc.getTA2lResponsibilityList();
    if (CommonUtils.isNotEmpty(a2lRespEntityList)) {
      for (TA2lResponsibility dbResp : a2lRespEntityList) {
        A2lResponsibility a2lResp = getDataObjectByID(dbResp.getA2lRespId());
        if (A2lResponsibilityCommon.isDefaultResponsibility(a2lResp)) {
          pidcRespMap.put(a2lResp.getRespType(), a2lResp);
        }
      }
    }
    return pidcRespMap;
  }

  /**
   * @param pidcId - project id card id
   * @return key - a2lResp id, value a2lResp
   * @throws DataException exception
   */
  public Map<String, A2lResponsibility> getRBAndBEGResp(final long pidcId) throws DataException {
    Map<String, A2lResponsibility> rbBegRespMap = new HashMap<>();
    TabvProjectidcard pidc = new PidcLoader(getServiceData()).getEntityObject(pidcId);
    List<TA2lResponsibility> a2lRespEntityList = pidc.getTA2lResponsibilityList();
    if (CommonUtils.isNotEmpty(a2lRespEntityList)) {
      for (TA2lResponsibility dbResp : a2lRespEntityList) {
        A2lResponsibility a2lResp = getDataObjectByID(dbResp.getA2lRespId());
        if (ApicConstants.ALIAS_NAME_RB_BEG.equals(a2lResp.getAliasName()) ||
            (ApicConstants.ALIAS_NAME_RB.equals(a2lResp.getAliasName()) &&
                A2lResponsibilityCommon.isDefaultResponsibility(a2lResp))) {
          rbBegRespMap.put(a2lResp.getAliasName(), a2lResp);
        }
      }
    }
    return rbBegRespMap;
  }

  /**
   * @param isTakeOverA2L take over from another a2l
   * @param pidcVersId pidc version id
   * @param pidcId pidc id
   * @param a2lRespForPidc a2l responsibility model
   * @return A2lResponsibility to be created
   * @throws IcdmException Exception in craeting A2lResponsibility obj
   */
  public A2lResponsibility createBegRespIfApplicable(final boolean isTakeOverA2L, final long pidcVersId,
      final long pidcId, final A2lResponsibilityModel a2lRespForPidc)
      throws IcdmException {
    A2lResponsibility a2lRespBEG = null;
    if (isTakeOverA2L) {
      return a2lRespBEG;
    }
    boolean isBegResp = false;
    AttributeValue attrVal = new PidcVersionAttributeLoader(getServiceData()).getPidcQnaireConfigAttrVal(pidcVersId);

    if (isQnaireConfigBEG(attrVal)) {
      for (A2lResponsibility a2lResp : a2lRespForPidc.getA2lResponsibilityMap().values()) {
        if (ApicConstants.ALIAS_NAME_RB_BEG.equals(a2lResp.getAliasName())) {
          isBegResp = true;
          break;
        }
      }
      if (!isBegResp) {
        a2lRespBEG = createDefaultA2lRespObjBEG(pidcId, WpRespType.RB, new AttributeValueLoader(getServiceData())
            .getDataObjectByID(
                Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.BEG_CAL_PROJ_ATTR_VAL_ID)))
            .getName());
      }
    }
    return a2lRespBEG;
  }

  /**
   * @param attrVal
   * @return
   */
  private boolean isQnaireConfigBEG(final AttributeValue attrVal) {
    return (null != attrVal) && new CommonParamLoader(getServiceData())
        .getValue(CommonParamKey.BEG_CAL_PROJ_ATTR_VAL_ID).equals(attrVal.getId().toString());
  }

  /**
   * @param pidcId
   * @param department
   * @param rb
   * @return
   * @throws IcdmException
   */
  private A2lResponsibility createDefaultA2lRespObjBEG(final long pidcId, final WpRespType respType,
      final String department) {
    A2lResponsibility a2lRespBEG = createA2LRespObj(pidcId, respType);
    a2lRespBEG.setLDepartment(department);
    a2lRespBEG.setAliasName(null);
    return a2lRespBEG;
  }

  /**
   * @param pidcId pidc id
   * @param respType a2l resp type
   * @return A2lResponsibility
   */
  public A2lResponsibility createA2LRespObj(final long pidcId, final WpRespType respType) {
    A2lResponsibility a2lResp = new A2lResponsibility();
    a2lResp.setLFirstName(null);
    a2lResp.setLLastName(null);
    a2lResp.setProjectId(pidcId);
    a2lResp.setRespType(respType.getCode());
    a2lResp.setAliasName(respType.getAliasBase());
    a2lResp.setLDepartment(respType.getAliasBase());
    return a2lResp;
  }

  /**
   * @param executionId execution id is used to find folder path
   * @return input files byte stream
   * @throws IcdmException exception
   */
  public String fetchA2lRespMergeInputData(final String executionId) throws IcdmException {
    List<String> serverPathsList = new SystemInfoProvider(getServiceData()).getServerGroupWorkPaths();
    getLogger().debug("Server paths to search : {}", serverPathsList);

    for (String serverPath : serverPathsList) {
      String folderPath = serverPath + File.separator + ApicConstants.A2L_RESP_MERGE + File.separator + executionId;
      File file = new File(folderPath);

      if (file.exists()) {
        String pathName = folderPath + File.separator + ApicConstants.A2L_RESP_MERGE_DATA_JSON_NAME;
        file = new File(pathName);

        if (file.exists()) {
          getLogger().debug("A2L Resp Merge input data for execution ID '{}' found. Path : {}", executionId, pathName);
          return pathName;
        }
      }
    }

    throw new IcdmException("A2L_RESP_MERGE.FILES_NOT_AVAILABLE", executionId);
  }
}
