/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bosch.boot.ssd.api.entity.VSdomBcSsdinfo;


/**
 * @author GDH9COB
 *
 */
@Repository
public interface VSdomBcSSDInfoRepository extends JpaRepository<VSdomBcSsdinfo, String> {


  /**
   * @param elnummer
   * @param variant
   * @return List of VSdomBcSsdinfo
   */
  Optional<List<VSdomBcSsdinfo>> findByBcNumberAndBcVariant(BigDecimal elnummer, String variant);

  /**
   * @param bcName
   * @param variant
   * @return List of VSdomBcSsdinfo
   */
  Optional<List<VSdomBcSsdinfo>> findByBcNameAndBcVariant(String bcName, String variant);

}
