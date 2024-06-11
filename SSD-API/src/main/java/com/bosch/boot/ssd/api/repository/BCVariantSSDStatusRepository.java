/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bosch.boot.ssd.api.entity.TSdomBcVariantSsdStatus;

/**
 * @author GDH9COB
 *
 */
@Repository
public interface BCVariantSSDStatusRepository extends JpaRepository<TSdomBcVariantSsdStatus, Long> {

  /**
   * @param elnummer
   * @param variant
   * @return 
   */
  Optional<TSdomBcVariantSsdStatus> findByElNummerAndVariant(BigDecimal elnummer, String variant);

}
