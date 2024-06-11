/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.service;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bosch.boot.ssd.api.entity.VSdomBcSsdinfo;
import com.bosch.boot.ssd.api.exception.ResourceNotFoundException;
import com.bosch.boot.ssd.api.model.SSD2BCInfo;
import com.bosch.boot.ssd.api.repository.VSdomBcSSDInfoRepository;

/**
 * @author GDH9COB
 */
@Service
public class SSD2BCService {

  @Autowired
  private VSdomBcSSDInfoRepository vSdomBcSSDInfoRepository;


  /**
   * @param elnummer BC Number
   * @param variant BC Variant
   * @return SSD2BCInfo that contains BC Information and list of assigned nodes
   * @throws ResourceNotFoundException
   */
  public SSD2BCInfo getSSD2BCInfoByBcNummerAndVariant(final BigDecimal elnummer, final String variant)
      throws ResourceNotFoundException {

    Optional<List<VSdomBcSsdinfo>> data = this.vSdomBcSSDInfoRepository.findByBcNumberAndBcVariant(elnummer, variant);
    return generateSSD2BCInfo(data);
  }


  /**
   * @param bcName BC Name
   * @param variant BC Variant
   * @return SSD2BCInfo that contains BC Information and list of assigned nodes
   * @throws ResourceNotFoundException
   */
  public SSD2BCInfo getSSD2BCInfoByBcNameAndVariant(final String bcName, final String variant)
      throws ResourceNotFoundException {
    Optional<List<VSdomBcSsdinfo>> data = this.vSdomBcSSDInfoRepository.findByBcNameAndBcVariant(bcName, variant);
    return generateSSD2BCInfo(data);
  }


  /**
   * @param List of VSdomBcSsdinfo
   * @return SSD2BCInfo that contains BC Information and list of assigned nodes
   * @throws ResourceNotFoundException
   */
  private SSD2BCInfo generateSSD2BCInfo(final Optional<List<VSdomBcSsdinfo>> data) throws ResourceNotFoundException {

    if (data.isPresent() && !data.get().isEmpty()) {
      List<VSdomBcSsdinfo> bcInfoList = data.get();
      VSdomBcSsdinfo vInfo = bcInfoList.get(0);

      SSD2BCInfo info = new SSD2BCInfo();
      info.setBcName(vInfo.getBcName());
      info.setBcNumber(vInfo.getBcNumber());
      info.setBcRevision(vInfo.getBcRevision());
      info.setBcVariant(vInfo.getBcVariant());
      info.setBcStatus(vInfo.getSsdStatus());
      info.setVarSsdStatus(vInfo.getVarSsdStatus().equals("Y") ? "SSD" : "NO SSD");

      info.getAssignedNodes().addAll(bcInfoList.stream().map(VSdomBcSsdinfo::getNodeName).collect(Collectors.toSet()));
      Iterator<String> itr = info.getAssignedNodes().iterator();
      if (itr.hasNext() && (itr.next() == null)) {
        info.getAssignedNodes().clear();
        info.getAssignedNodes().add("No SSD node assigned");
      }
      return info;
    }
    throw new ResourceNotFoundException("Entered BC name or variant is not found in SSD database");
  }
}
