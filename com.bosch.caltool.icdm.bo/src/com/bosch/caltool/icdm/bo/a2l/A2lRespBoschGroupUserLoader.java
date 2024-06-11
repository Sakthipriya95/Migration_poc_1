package com.bosch.caltool.icdm.bo.a2l;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lResponsibility;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lResponsiblityBshgrpUsr;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2lRespBoschGroupUser;
import com.bosch.caltool.icdm.model.user.User;


/**
 * Loader class for A2L Responsibility Bosch Group User
 *
 * @author PDH2COB
 */
public class A2lRespBoschGroupUserLoader
    extends AbstractBusinessObject<A2lRespBoschGroupUser, TA2lResponsiblityBshgrpUsr> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public A2lRespBoschGroupUserLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.A2L_RESPONSIBLITY_BSHGRP_USR, TA2lResponsiblityBshgrpUsr.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected A2lRespBoschGroupUser createDataObject(final TA2lResponsiblityBshgrpUsr entity) throws DataException {
    A2lRespBoschGroupUser object = new A2lRespBoschGroupUser();

    setCommonFields(object, entity);

    UserLoader userLoader = new UserLoader(getServiceData());

    User user = userLoader.getDataObjectByID(entity.getTabvApicUser().getUserId());

    TA2lResponsibility ta2lResponsibility = entity.getTA2lResponsibility();
    object.setA2lRespId(ta2lResponsibility.getA2lRespId());
    object.setUserId(entity.getTabvApicUser().getUserId());
    object.setProjectId(ta2lResponsibility.getTabvProjectidcard().getProjectId());
    object.setName(user.getName());
    object.setDescription(user.getDescription());

    return object;
  }

  /**
   * @param respId A2l Resp Id for bosch dept/grp
   * @return Map<Long,A2lRespBoschGroupUser>
   * @throws DataException exception if data not found
   */
  public Map<Long, A2lRespBoschGroupUser> getBoschGrpRespUsers(final Long respId) throws DataException {
    Map<Long, A2lRespBoschGroupUser> a2lRespBoschGroupUserMap = new HashMap<>();
    List<TA2lResponsiblityBshgrpUsr> ta2lResponsiblityBshgrpUsrs =
        new A2lResponsibilityLoader(getServiceData()).getEntityObject(respId).getTa2lResponsiblityBshgrpUsrList();
    if (CommonUtils.isNotEmpty(ta2lResponsiblityBshgrpUsrs)) {
      for (TA2lResponsiblityBshgrpUsr ta2lResponsiblityBshgrpUsr : ta2lResponsiblityBshgrpUsrs) {
        A2lRespBoschGroupUser a2lBshGrpUsr = getDataObjectByID(ta2lResponsiblityBshgrpUsr.getA2lrespBshgrpUsrId());
        a2lRespBoschGroupUserMap.put(a2lBshGrpUsr.getId(), a2lBshGrpUsr);
      }
    }
    return a2lRespBoschGroupUserMap;
  }


}
