/*
 * \ * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.ssd.FeatureValueICDMModel;
import com.bosch.caltool.icdm.model.ssd.SSDFeatureICDMAttrModel;
import com.bosch.caltool.icdm.model.ssd.SSDReleaseIcdmModel;
import com.bosch.ssd.icdm.model.FeatureValueModel;
import com.bosch.ssd.icdm.model.SSDRelease;

/**
 * @author dmr1cob
 */
public class SSDReleaseFetcher extends AbstractSimpleBusinessObject {


  /**
   * error when fetching SSD releases from ssd software version
   */
  private static final String ERROR_FETCHING_SSD_RELEASES = "Error fetching SSD Releases ";

  /**
   * @param serviceData serviceData
   */
  public SSDReleaseFetcher(final ServiceData serviceData) {
    super(serviceData);

  }

  /**
   * @param swVersionId software version id
   * @return SSD Release List
   * @throws IcdmException Exception
   */
  public List<SSDReleaseIcdmModel> getSSDReleaseListbyswVersionId(final long swVersionId) throws IcdmException {
    List<SSDRelease> ssdRelesesBySwVersionId = null;
    try {
      ssdRelesesBySwVersionId = new SSDServiceHandler(getServiceData()).getSSDRelesesBySwVersionId(swVersionId);
    }
    catch (IcdmException e) {
      //Put a diffferent error message in this case
      throw new IcdmException(ERROR_FETCHING_SSD_RELEASES, e);
    }

    List<SSDReleaseIcdmModel> mappedSSDReleases = new ArrayList<>();
    FeatureAttributeAdapterNew featureAttributeAdapterNew = new FeatureAttributeAdapterNew(getServiceData());

    if (ssdRelesesBySwVersionId != null) {
      for (SSDRelease ssdRelease : ssdRelesesBySwVersionId) {
        List<FeatureValueModel> dependencyList = ssdRelease.getDependencyList();
        try {
          featureAttributeAdapterNew.getAttrValSet(dependencyList, getServiceData());
        }
        catch (IcdmException exp) {
          throw new IcdmException(ERROR_FETCHING_SSD_RELEASES, exp);
        }
        mappedSSDReleases.add(convertSSDReleaseToSSDReleaseIcdmModel(ssdRelease));

      }

    }
    return mappedSSDReleases;
  }

  /**
   * @param dependencyList FeatureValueICDMModel list
   * @return SSDFeatureICDMAttrModel list
   * @throws IcdmException Exception
   */
  public List<SSDFeatureICDMAttrModel> getSSDFeatureICDMAttrModel(
      final List<com.bosch.caltool.icdm.model.ssd.FeatureValueICDMModel> dependencyList)
      throws IcdmException {
    List<SSDFeatureICDMAttrModel> ssdFeaAttrList = new ArrayList<>();
    FeatureValueModel featureValueModel = new FeatureValueModel();
    for (com.bosch.caltool.icdm.model.ssd.FeatureValueICDMModel featureValueICDMModel : dependencyList) {
      FeatureAttributeAdapterNew adapter = new FeatureAttributeAdapterNew(getServiceData());
      try {
        BeanUtils.copyProperties(featureValueModel, featureValueICDMModel);
        AttributeValueModel attrValModel = adapter.getAttrValue(getServiceData(), featureValueModel);

        SSDFeatureICDMAttrModel ssdFeatureICDMAttrModel = new SSDFeatureICDMAttrModel();
        ssdFeatureICDMAttrModel.setAttrValModel(attrValModel);
        ssdFeatureICDMAttrModel.setFeaValModel(featureValueICDMModel);
        ssdFeaAttrList.add(ssdFeatureICDMAttrModel);
      }
      catch (Exception exp) {
        throw new IcdmException("Fetching of SSDFeatureICDMAttrModel failed ", exp);
      }
    }
    return ssdFeaAttrList;
  }

  private SSDReleaseIcdmModel convertSSDReleaseToSSDReleaseIcdmModel(final SSDRelease ssdRelease) throws DataException {
    SSDReleaseIcdmModel ssdReleaseIcdmModel = new SSDReleaseIcdmModel();
    ssdReleaseIcdmModel.setBuggyDate(timestamp2String((Timestamp) ssdRelease.getBuggyDate()));
    ssdReleaseIcdmModel.setCreatedUser(ssdRelease.getCreatedUser());
    ssdReleaseIcdmModel.setErrors(ssdRelease.getErrors());
    ssdReleaseIcdmModel.setExternalRelease(ssdRelease.getExternalRelease());
    ssdReleaseIcdmModel.setGlobalByPass(ssdRelease.getGlobalByPass());
    ssdReleaseIcdmModel.setLabelListDesc(ssdRelease.getLabelListDesc());
    ssdReleaseIcdmModel.setRelease(ssdRelease.getRelease());
    ssdReleaseIcdmModel.setReleaseDate(timestamp2String((Timestamp) ssdRelease.getReleaseDate()));
    ssdReleaseIcdmModel.setReleaseDesc(ssdRelease.getReleaseDesc());
    ssdReleaseIcdmModel.setReleaseId(ssdRelease.getReleaseId().longValue());
    ssdReleaseIcdmModel.setDependencyList(getDependencyList(ssdRelease.getDependencyList()));

    return ssdReleaseIcdmModel;
  }

  /**
   * @param dependencyList
   * @return
   * @throws DataException
   */
  private List<com.bosch.caltool.icdm.model.ssd.FeatureValueICDMModel> getDependencyList(
      final List<FeatureValueModel> dependencyList)
      throws DataException {
    List<com.bosch.caltool.icdm.model.ssd.FeatureValueICDMModel> featureValueICDMModelList = new ArrayList<>();
    for (FeatureValueModel featureValueModel2 : dependencyList) {
      FeatureValueICDMModel featureValueICDMModel = new FeatureValueICDMModel();
      try {
        BeanUtils.copyProperties(featureValueICDMModel, featureValueModel2);
      }
      catch (IllegalAccessException | InvocationTargetException e) {
        throw new DataException(e.getLocalizedMessage(), e);
      }

      featureValueICDMModelList.add(featureValueICDMModel);
    }
    return featureValueICDMModelList;
  }


}
