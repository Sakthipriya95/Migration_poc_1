/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.bosch.boot.ssd.api.entity.VLdb2Pavast;
import com.bosch.boot.ssd.api.model.query.Filter;

/**
 * @author GDH9COB
 *
 */
public class SSDAPISpecificationBuilder {

  private final Filter filter;

  /**
   * @param filter
   */
  public SSDAPISpecificationBuilder(Filter filter) {
    this.filter = filter;
  }
  
  public Specification<VLdb2Pavast> build() {
    if(filter == null) {
      return null;
    }
    
    List<Specification<VLdb2Pavast>> specs = new ArrayList<>();
    
    //LabelName predicate
    if(filter.getLabelName() != null && !filter.getLabelName().isEmpty()) {
      specs.add(SSDAPISpecification.withLabels(filter.getLabelName()));
    }
    
    //if(!specs.isEmpty()) {
      return specs.get(0);
      
     /* for(int i = 1; i < specs.size(); i++) {
        result = Specification.where(result).and(specs.get(i));
      }*/
      
      //return result;
    //} return null;
    
    
  }

}
