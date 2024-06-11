/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.specifications;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.bosch.boot.ssd.api.entity.VLdb2Pavast;

/**
 * @author GDH9COB
 *
 */
public class SSDAPISpecification {
  
  /**
   * @param labels
   * @return
   */
  public static Specification<VLdb2Pavast> withLabels(final List<String> labels) {
      return (root, query, build) -> root.get("label").in(labels);
  }

}
