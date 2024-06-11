/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.bosch.boot.ssd.api.entity.VLdb2Pavast;

/**
 * @author GDH9COB
 *
 */
@Repository
public interface VLDB2PavastRepository extends JpaRepository<VLdb2Pavast, BigDecimal>, JpaSpecificationExecutor<VLdb2Pavast> {


}
