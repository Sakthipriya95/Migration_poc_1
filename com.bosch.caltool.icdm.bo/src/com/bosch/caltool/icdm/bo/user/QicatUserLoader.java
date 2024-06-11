/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.database.entity.a2l.VQicatUser;
import com.bosch.caltool.icdm.database.entity.apic.GttObjectName;
import com.bosch.caltool.icdm.model.user.QicatUser;

/**
 * @author nip4cob
 */
public class QicatUserLoader extends AbstractSimpleBusinessObject {

  /**
   * @param serviceData serviceData
   */
  public QicatUserLoader(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * @param entity VQicatUser
   * @return QicatUser
   */
  protected QicatUser createDataObject(final VQicatUser entity) {
    QicatUser user = new QicatUser();
    user.setCiDepartment(entity.getCiDepartment());
    user.setCiFirstname(entity.getCiFirstname());
    user.setCiGender(entity.getCiGender());
    user.setCiLastname(entity.getCiLastname());
    user.setCiTitle(entity.getCiTitle());
    user.setCostcenter(entity.getCostcenter());
    user.setCountryCode(entity.getCountryCode());
    user.setCountryDial(entity.getCountryDial());
    user.setCountryName(entity.getCountryName());
    user.setDomain(entity.getDomain());
    user.setEmail(entity.getEmail());
    user.setFax(entity.getFax());
    user.setPhone(entity.getPhone());
    user.setName(entity.getUsername());
    user.setDescription(new UserLoader(getServiceData()).formDisplayName(entity.getCiFirstname(),
        entity.getCiLastname(), entity.getCiDepartment(), entity.getUsername()));
    return user;
  }


  /**
   * @param userNameList - List of NTIDs
   * @return Map<String,String> - key - username , value - department
   */
  public Map<String, QicatUser> getQicatUsersByNtId(final List<String> userNameList) {
    Map<String, QicatUser> userDivMap = new HashMap<>();
    if (!userNameList.isEmpty()) {

      try (ServiceData sdata = new ServiceData()) {

        getServiceData().copyTo(sdata, true);
        sdata.getEntMgr().getTransaction().begin();
        Long id = 0L;
        for (String username : userNameList) {
          GttObjectName temp = new GttObjectName();
          temp.setObjName(username);
          temp.setId(id++);
          sdata.getEntMgr().persist(temp);
        }

        // Fetch user
        TypedQuery<VQicatUser> query =
            sdata.getEntMgr().createNamedQuery(VQicatUser.NQ_GET_BY_NTID_LIST, VQicatUser.class);
        List<VQicatUser> resultList = query.getResultList();
        for (VQicatUser entity : resultList) {
          userDivMap.put(entity.getUsername(), createDataObject(entity));
        }
        sdata.getEntMgr().getTransaction().rollback();
      }
    }
    return userDivMap;
  }

}
