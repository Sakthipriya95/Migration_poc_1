/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.bosch.boot.ssd.api.entity.VLdb2Pavast;
import com.bosch.boot.ssd.api.exception.ParameterInvalidException;
import com.bosch.boot.ssd.api.model.query.Filter;
import com.bosch.boot.ssd.api.repository.VLDB2PavastRepository;
import com.bosch.boot.ssd.api.specifications.SSDAPISpecificationBuilder;

/**
 * @author GDH9COB
 *
 */
@Service
public class SSDFileEditorService {
  
  @Autowired
  private VLDB2PavastRepository vldb2PavastRepository;
  
  /**
   * @param labelFilter
   * @return
   * @throws ParameterInvalidException 
   */
  public Map<String, String> getLabelByType(Filter labelFilter) throws ParameterInvalidException {
    
    Specification<VLdb2Pavast> specification = constructFilterSpecification(labelFilter);
    if(specification != null) {
      return getAllLabelsType(specification);
    }
    throw new ParameterInvalidException();
    
  }

  /**
   * @param specification
   * @return
   */
  private Map<String, String> getAllLabelsType(Specification<VLdb2Pavast> specification) {
    Map<String, String> labelsMap = new HashMap<>();
    if (specification != null) {
        List<VLdb2Pavast> labelList = vldb2PavastRepository.findAll(specification);
        for(VLdb2Pavast pavast: labelList) {
          labelsMap.put(pavast.getLabel(), pavast.getCategory());
        }
        return labelsMap;
    }
    return labelsMap;

  }

  /**
   * @param labels
   * @return
   */
  private Specification<VLdb2Pavast> constructFilterSpecification(Filter labelFilter) {
    Specification<VLdb2Pavast> specification = null;
    
    if(labelFilter != null) {
      SSDAPISpecificationBuilder specificationBuilder = new SSDAPISpecificationBuilder(labelFilter);
      specification = specificationBuilder.build();
    }
    return specification;
  }
  
}
