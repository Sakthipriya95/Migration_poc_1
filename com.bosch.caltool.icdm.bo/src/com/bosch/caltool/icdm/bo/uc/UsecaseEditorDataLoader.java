/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.uc;

import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttrNValueDependencyDetails;
import com.bosch.caltool.icdm.bo.apic.attr.AttrNValueDependencyLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.CharacteristicLoader;
import com.bosch.caltool.icdm.bo.general.LinkLoader;
import com.bosch.caltool.icdm.bo.wp.WpmlWpMasterlistLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.uc.UcpAttr;
import com.bosch.caltool.icdm.model.uc.UsecaseEditorModel;
import com.bosch.caltool.icdm.model.uc.UsecaseModel;

/**
 * @author mkl2cob
 */
public class UsecaseEditorDataLoader extends AbstractSimpleBusinessObject {

  /**
   * @param serviceData ServiceData
   */
  public UsecaseEditorDataLoader(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * @param usecaseId Long
   * @return UsecaseEditorModel
   * @throws DataException exception while creating model object
   */
  public UsecaseEditorModel getUsecaseEditorData(final Long usecaseId) throws DataException {
    UsecaseEditorModel editorModel = new UsecaseEditorModel();
    UseCaseSectionLoader ucsLoader = new UseCaseSectionLoader(getServiceData());
    UcpAttrLoader ucpaLoader = new UcpAttrLoader(getServiceData());
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());

    editorModel.getAttrMap().putAll(attrLoader.getAllAttributes(false));
    ucsLoader.getUsecaseSections(usecaseId, editorModel);
    Map<Long, UcpAttr> ucMappedAttributes = ucpaLoader.getUCMappedAttributes(usecaseId);
    editorModel.getUcpAttr().putAll(ucMappedAttributes);
    Map<Long, Map<Long, Long>> attrToUcpAttrMap = editorModel.getAttrToUcpAttrMap();
    createAttrToUcpaMapping(attrToUcpAttrMap, ucMappedAttributes);

    AttrNValueDependencyDetails attrDependencyDetails =
        new AttrNValueDependencyLoader(getServiceData()).fillAttrDependencyDetails();
    editorModel.getAttrRefDependenciesMap().putAll(attrDependencyDetails.getAttrRefDependenciesMap());
    editorModel.getAttrDepValMap().putAll(attrDependencyDetails.getDepAttrVals());
    editorModel.getLinkSet().addAll(new LinkLoader(getServiceData()).getNodesWithLink(ApicConstants.ATTR_NODE_TYPE));
    editorModel.getCharacteristicMap()
        .putAll(new CharacteristicLoader(getServiceData()).getAttrMappedCharacteristics());
    editorModel.getWpmlWpIdUcSectionIdMap()
        .putAll(new WpmlWpMasterlistLoader(getServiceData()).getWmplWpIdUcSectionIdMap());
    editorModel.getWpmlMcrIdUcSectionIdMap()
        .putAll(new WpmlWpMasterlistLoader(getServiceData()).getWmplMcrIdUcSectionIdMap());
    return editorModel;
  }

  /**
   * @param attrToUcpAttrMap
   * @param ucMappedAttributes
   */
  private void createAttrToUcpaMapping(final Map<Long, Map<Long, Long>> attrToUcpAttrMap,
      final Map<Long, UcpAttr> ucMappedAttributes) {
    for (UcpAttr ucpAttr : ucMappedAttributes.values()) {
      Map<Long, Long> ucpaMappedToAttr = attrToUcpAttrMap.get(ucpAttr.getAttrId());
      if (null == ucpaMappedToAttr) {
        ucpaMappedToAttr = new HashMap<Long, Long>();
        attrToUcpAttrMap.put(ucpAttr.getAttrId(), ucpaMappedToAttr);
      }
      Long ucItemId = ucpAttr.getUseCaseId();
      if (ucpAttr.getSectionId() != null) {
        ucItemId = ucpAttr.getSectionId();
      }
      ucpaMappedToAttr.put(ucItemId, ucpAttr.getId());
    }
  }

  /**
   * @param usecaseId Long
   * @return UsecaseEditorModel
   * @throws DataException exception while creating model object
   */
  public UsecaseModel getUsecaseDetailsData(final Long usecaseId) throws DataException {
    UsecaseModel ucModel = new UsecaseModel();
    UseCaseSectionLoader ucsLoader = new UseCaseSectionLoader(getServiceData());
    UcpAttrLoader ucpaLoader = new UcpAttrLoader(getServiceData());
    getLogger().debug("Fetching Usecase sections and child sections");
    ucsLoader.getUsecaseSections(usecaseId, ucModel);
    getLogger().debug("Fetching UCP Attribute ids");
    ucModel.getUcItemAttrMap().putAll(ucpaLoader.getMappedAttributes(usecaseId, false));
    ucModel.getUcItemAttrMapIncDel().putAll(ucpaLoader.getMappedAttributes(usecaseId, true));
    return ucModel;
  }

}
