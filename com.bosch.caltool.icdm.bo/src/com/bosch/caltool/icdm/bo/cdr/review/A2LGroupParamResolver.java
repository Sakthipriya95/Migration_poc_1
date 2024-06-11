/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.review;

import java.util.ArrayList;
import java.util.List;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.A2LGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpMapping;

/**
 * @author bru2cob
 */
public class A2LGroupParamResolver implements IReviewParamResolver {

  final ServiceData serviceData;

  A2LFileInfo a2lFileContents;

  Long pidcA2LId;

  String a2lGroupName;

  /**
   * @param serviceData
   * @param pidcA2lId
   * @param a2lFileContents
   * @param a2lGroupName
   * @deprecated not used
   */
  @Deprecated
  public A2LGroupParamResolver(final ServiceData serviceData, final A2LFileInfo a2lFileContents, final Long pidcA2lId,
      final String a2lGroupName) {
    this.serviceData = serviceData;
    this.a2lFileContents = a2lFileContents;
    this.pidcA2LId = pidcA2lId;
    this.a2lGroupName = a2lGroupName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getParameters() throws IcdmException {

    PidcA2lLoader pidcA2lLoader = new PidcA2lLoader(this.serviceData);
    A2lWpMapping a2lWpMapping = pidcA2lLoader.getWorkpackageMapping(this.pidcA2LId);
    A2LGroup a2lGroup = a2lWpMapping.getA2lGrpMap().get(this.a2lGroupName);
    List<String> a2lLabelMaps = new ArrayList<>();
    a2lLabelMaps.addAll(a2lGroup.getLabelMap().values());
    return a2lLabelMaps;

  }

}
