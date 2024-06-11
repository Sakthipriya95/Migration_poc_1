/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.wp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TWpmlWpMasterlist;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseSection;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.wp.WpmlWpMasterlist;


/**
 * Loader class for WpmlWpMasterlist
 *
 * @author UKT1COB
 */
public class WpmlWpMasterlistLoader extends AbstractBusinessObject<WpmlWpMasterlist, TWpmlWpMasterlist> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public WpmlWpMasterlistLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.WPML_WP_MASTERLIST, TWpmlWpMasterlist.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected WpmlWpMasterlist createDataObject(final TWpmlWpMasterlist tWpmlWPMasterList) throws DataException {
    WpmlWpMasterlist wpMasterlist = new WpmlWpMasterlist();

    setCommonFields(wpMasterlist, tWpmlWPMasterList);

    wpMasterlist.setMcrWpiNumber(tWpmlWPMasterList.getMcrWpiNumber());
    wpMasterlist.setWpMcrName(tWpmlWPMasterList.getWpMcrName());
    wpMasterlist.setWpLongname(tWpmlWPMasterList.getWpLongname());
    wpMasterlist.setWpDescriptionInternal(tWpmlWPMasterList.getWpDescriptionInternal());
    wpMasterlist.setMcrId(tWpmlWPMasterList.getMcrId());
    wpMasterlist.setMcrNameOld(tWpmlWPMasterList.getMcrNameOld());
    wpMasterlist.setGsIdOld(tWpmlWPMasterList.getGsIdOld());
    wpMasterlist.setCtdLink(tWpmlWPMasterList.getCtdLink());
    wpMasterlist.setLinkToInfo(tWpmlWPMasterList.getLinkToInfo());
    wpMasterlist.setEmissionRelevantCrpPfa(tWpmlWPMasterList.getEmissionRelevantCrpPfa());
    wpMasterlist.setEmissionRelevantForTdec(tWpmlWPMasterList.getEmissionRelevantForTdec());
    wpMasterlist.setCocId(tWpmlWPMasterList.getCocId());
    wpMasterlist.setWpRespTeNtUser(tWpmlWPMasterList.getWpRespTeNtUser());
    wpMasterlist.setWpDescriptionExternal(tWpmlWPMasterList.getWpDescriptionExternal());
    wpMasterlist.setObdRelevantCrp(tWpmlWPMasterList.getObdRelevantCrp());
    wpMasterlist.setObdRelevantPfa(tWpmlWPMasterList.getObdRelevantPfa());
    wpMasterlist.setCocComment(tWpmlWPMasterList.getCocComment());
    wpMasterlist.setCompany(tWpmlWPMasterList.getCompany());
    wpMasterlist.setDataReviewMandatory(tWpmlWPMasterList.getDatareviewMandatory());
    wpMasterlist.setEu6eImpact(tWpmlWPMasterList.getEu6eImpact());
    wpMasterlist.setEu6eImpactDesc(tWpmlWPMasterList.getEu6eImpactDesc());
    wpMasterlist.setIsDeleted(tWpmlWPMasterList.getIsDeleted());
    wpMasterlist.setIsValid(tWpmlWPMasterList.getIsValid());
    wpMasterlist.setLinkToWpTraining(tWpmlWPMasterList.getLinkToWpTraining());
    wpMasterlist.setMandatoryTestdriveFind(tWpmlWPMasterList.getMandatoryTestdriveFind());
    wpMasterlist.setMandatoryTestdriveRelease(tWpmlWPMasterList.getMandatoryTestdriveRelease());
    wpMasterlist.setMcrAdditionalInfo(tWpmlWPMasterList.getMcrAdditionalInfo());

    return wpMasterlist;
  }

  /**
   * Method to fetch the TWpmlWpMasterlist using mcrId
   *
   * @return List<TWpmlWpMasterlist> list of TWpmlWpMasterlisthaving given mcrId
   */
  public List<TWpmlWpMasterlist> getWmplByMcrId(final String mcrId) {
    TypedQuery<TWpmlWpMasterlist> tQuery =
        getEntMgr().createNamedQuery(TWpmlWpMasterlist.NQ_GET_TWPML_WP_MASTERLIST_BY_MCRID, TWpmlWpMasterlist.class);
    tQuery.setParameter("mcrId", mcrId);
    return tQuery.getResultList();
  }


  /**
   * Method to fetch the Wp ID from TWpmlWpMasterlist and its corresponding Use Case sections
   *
   * @return Map<String, List<Long>> Wp Id of TWpmlWpMasterlist as key and mapped use case section Ids List as value
   * @throws DataException error while retrieving data
   */
  public Map<String, Set<Long>> getWmplWpIdUcSectionIdMap() {
    TypedQuery<TWpmlWpMasterlist> query =
        getEntMgr().createNamedQuery(TWpmlWpMasterlist.NQ_GET_ALL_TWPML_WP_MASTERLIST, TWpmlWpMasterlist.class);
    Map<String, Set<Long>> retMap = new HashMap<>();
    for (TWpmlWpMasterlist tWpmlWpMasterlist : query.getResultList()) {
      Set<Long> ucSectionIdList = new HashSet<>();
      List<TabvUseCaseSection> ucSectionList = tWpmlWpMasterlist.getTabvUsecaseSection();
      if (CommonUtils.isNotEmpty(ucSectionList)) {
        for (TabvUseCaseSection tabvUseCaseSection : ucSectionList) {
          ucSectionIdList.add(tabvUseCaseSection.getSectionId());
        }
      }
      retMap.put(tWpmlWpMasterlist.getMcrWpiNumber(), ucSectionIdList);
    }
    return retMap;
  }

  /**
   * Method to fetch the Mcr ID from TWpmlWpMasterlist and its corresponding Use Case sections
   *
   * @return Map<String, List<Long>> Mcr Id of TWpmlWpMasterlist as key and mapped use case section Ids List as value
   */
  public Map<String, Set<Long>> getWmplMcrIdUcSectionIdMap() {
    TypedQuery<TWpmlWpMasterlist> query =
        getEntMgr().createNamedQuery(TWpmlWpMasterlist.NQ_GET_ALL_TWPML_WP_MASTERLIST, TWpmlWpMasterlist.class);
    Map<String, Set<Long>> retMap = new HashMap<>();
    for (TWpmlWpMasterlist tWpmlWpMasterlist : query.getResultList()) {
      Set<Long> ucSectionIdList = new HashSet<>();
      List<TabvUseCaseSection> ucSectionList = tWpmlWpMasterlist.getTabvUsecaseSection();
      if (CommonUtils.isNotEmpty(ucSectionList)) {
        for (TabvUseCaseSection tabvUseCaseSection : ucSectionList) {
          ucSectionIdList.add(tabvUseCaseSection.getSectionId());
        }
      }
      if (CommonUtils.isNotNull(tWpmlWpMasterlist.getMcrId())) {
        retMap.put(tWpmlWpMasterlist.getMcrId(), ucSectionIdList);
      }
    }
    return retMap;
  }

}
