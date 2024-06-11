/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dmo5cob
 */
public class PIDCVersionsDataHandler extends AbstractClientDataHandler {


  /**
   * Project id
   */
  private Pidc pidc;
  private PidcVersion activePidcVersn;
  Map<Long, PidcVersion> pidcVerMap = new HashMap<>();

  /**
   * @param pidc project id
   */
  public PIDCVersionsDataHandler(final Pidc pidc) {
    this.pidc = pidc;
    this.pidcVerMap = getAllPidcVersionForPidc();
    this.activePidcVersn = getActivePidcVersion();
  }

  /**
   * @return Map<Long, PidcVersion>
   */
  public Map<Long, PidcVersion> getAllPidcVersionForPidc() {
    Map<Long, PidcVersion> pidcVerMapLcl = null;
    try {
      pidcVerMapLcl = new PidcVersionServiceClient().getAllPidcVersionForPidc(this.pidc.getId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
    }


    return pidcVerMapLcl;
  }

  /**
   * @return active pidcversion
   */
  public PidcVersion getActivePidcVersion() {
    try {
      return new PidcVersionServiceClient().getActivePidcVersion(this.pidc.getId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
      return null;
    }
  }

  /**
   * Method to Update PIDC Object
   *
   * @param pidcObj as Input
   * @return Pidc
   */
  public Pidc updatePidc(final Pidc pidcObj) {
    try {
      return new PidcServiceClient().update(pidcObj);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
      return null;
    }
  }

  private void refreshProjVrsns(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    this.pidcVerMap = getAllPidcVersionForPidc();
    refreshPIDCObject();

  }

  private void refreshActiveVrsn(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    this.activePidcVersn = getActivePidcVersion();
    refreshPIDCObject();

  }

  /**
   * Reloads the pidc Object from DB
   */
  public void refreshPIDCObject() {
    try {
      this.pidc = new PidcServiceClient().getById(this.pidc.getId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {
    registerCns(
        data -> ((PidcVersion) CnsUtils.getModel(data)).getPidcId().longValue() == this.pidc.getId().longValue(),
        this::refreshProjVrsns, MODEL_TYPE.PIDC_VERSION);

    registerCns(data -> ((Pidc) CnsUtils.getModel(data)).getId().longValue() == this.pidc.getId().longValue(),
        this::refreshActiveVrsn, MODEL_TYPE.PIDC);

  }


  /**
   * @return the pidcVerMap
   */
  public Map<Long, PidcVersion> getPidcVerMap() {
    return this.pidcVerMap;
  }


  /**
   * @return the activePidcVersn
   */
  public PidcVersion getActivePidcVersn() {
    return this.activePidcVersn;
  }


  /**
   * @return the pidc
   */
  public Pidc getPidc() {
    return this.pidc;
  }

  /**
   * @param createdDate createdDate
   * @return the formatted date
   */
  public String getCreatedDateFormatted(final String createdDate) {
    SimpleDateFormat dateFormatsource =
        new SimpleDateFormat(DateFormat.DATE_FORMAT_15, Locale.getDefault(Locale.Category.FORMAT));
    Date date = null;

    try {
      date = dateFormatsource.parse(createdDate);
    }
    catch (ParseException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    SimpleDateFormat dateFormatDest =
        new SimpleDateFormat(DateFormat.DATE_FORMAT_04, Locale.getDefault(Locale.Category.FORMAT));
    return dateFormatDest.format(date);
  }
}
