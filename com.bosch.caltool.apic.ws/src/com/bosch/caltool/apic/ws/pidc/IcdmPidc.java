/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.pidc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.ws.LevelAttrInfo;
import com.bosch.caltool.apic.ws.ProjectIdCardVersInfoType;
import com.bosch.caltool.apic.ws.pidc.adapter.LevelAttrAdapter;
import com.bosch.caltool.apic.ws.pidc.adapter.PidcVersionAdapter;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.logger.WSLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;


/**
 * @author imi2si
 */
public class IcdmPidc extends AbstractPidc {

  /**
   * Logger
   */
  private static final ILoggerAdapter LOG = WSLogger.getInstance();

  Map<Long, PidcVersionAttribute> pidcAttributes;

  private final ServiceData serviceData;

  /**
   * @param serviceData serviceData
   */
  public IcdmPidc(final ServiceData serviceData) {
    super();
    this.serviceData = serviceData;
  }

  /**
   * {@inheritDoc}
   *
   * @throws DataException
   */
  @Override
  public ProjectIdCardVersInfoType fetchPidc(final long pidcId) throws IcdmException {
    LOG.debug("Fetch PIDC from DB for PIDC: {}", pidcId);
    PidcVersion pidcVersion = null;
    pidcVersion = new PidcVersionLoader(this.serviceData).getDataObjectByID(this.pidVersionID);
    return new PidcVersionAdapter(pidcVersion, this.serviceData);
  }

  @Override
  public LevelAttrInfo[] fetchLevelAttrInfo(final long pidcId) throws IcdmException {

    List<LevelAttrInfo> levelAttrInfos = new ArrayList<>();
    fillAttributeValueList();
    AttributeLoader attrLoader = new AttributeLoader(this.serviceData);
    AttributeLoader attrloader = new AttributeLoader(this.serviceData);
    for (Entry<Long, PidcVersionAttribute> entry : this.pidcAttributes.entrySet()) {

      levelAttrInfos.add(new LevelAttrAdapter(attrLoader.getDataObjectByID(entry.getValue().getAttrId()),
          getAttributeValue(attrloader.getDataObjectByID(entry.getValue().getAttrId()).getLevel()), this.serviceData));

    }
    return levelAttrInfos.toArray(new LevelAttrInfo[0]);
  }


  private void fillAttributeValueList() throws IcdmException {
    // ICDM-2162
    PidcVersion activePidcVersion = new PidcVersionLoader(this.serviceData).getActivePidcVersion(this.pidcId);
    this.pidcAttributes =
        new PidcVersionAttributeLoader(this.serviceData).getStructureAttributes(activePidcVersion.getId());
  }


  private Long getAttributeValue(final long pidcAttrId) {
    return this.pidcAttributes.get(pidcAttrId).getValueId();
  }

}
