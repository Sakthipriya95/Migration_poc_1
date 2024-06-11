/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.reviewresult.adapter.review;

import java.io.IOException;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.apic.ws.pidc.AbstractPidc;
import com.bosch.caltool.apic.ws.pidc.adapter.PidVariantAdapter;
import com.bosch.caltool.apic.ws.session.Session;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.cdr.CDRResultParameterLoader;
import com.bosch.caltool.icdm.bo.cdr.CDRReviewResultLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParameter;
import com.bosch.caltool.icdm.model.apic.ApicConstants.READY_FOR_SERIES;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;


/**
 * @author imi2si
 */
public class CDRResultParamAdapter extends AbstractResultParamAdapter {

  private final CDRResultParameter param;
  private final ServiceData serviceData;
  private final CDRResultParameterLoader cdrResultParameterLoader;
  private final CDRReviewResultLoader rvwResultLoader;
  private final PidcVersionLoader pidcVersionLoader;

  /**
   * @param session
   * @param cdrResult
   * @param pidc
   * @param serviceData
   * @param cdrResultParameterLoader
   * @param rvwResultLoader
   * @param pidcVersionLoader
   * @throws IcdmException
   * @throws IOException
   * @throws ClassNotFoundException
   * @throws DataException
   */
  public CDRResultParamAdapter(final Session session, final CDRResultParameter cdrResult, final AbstractPidc pidc,
      final ServiceData serviceData, final CDRResultParameterLoader cdrResultParameterLoader,
      final CDRReviewResultLoader rvwResultLoader, final PidcVersionLoader pidcVersionLoader)
      throws IcdmException, ClassNotFoundException, IOException {
    super(session, pidc);
    this.param = cdrResult;
    this.serviceData = serviceData;
    this.cdrResultParameterLoader = cdrResultParameterLoader;
    this.rvwResultLoader = rvwResultLoader;
    this.pidcVersionLoader = pidcVersionLoader;
    adapt();
  }

  @Override
  public void adapt() throws IcdmException, ClassNotFoundException, IOException {
    TRvwParameter cdRReultPameterEntity = this.cdrResultParameterLoader.getEntityObject(this.param.getId());

    CDRReviewResult cdrRvwResult;
    cdrRvwResult = this.rvwResultLoader.getDataObjectByID(this.param.getResultId());
    super.setReviewId(this.param.getResultId());
    setPidc(cdrRvwResult);
    setVariant(cdrRvwResult);
    super.setParameterName(cdRReultPameterEntity.getTParameter().getName());
    super.setReviewName(cdrRvwResult.getName());
    super.setReviewResult(CDRConstants.RESULT_FLAG.getType(this.param.getResult()).getUiType());
    super.setComment(this.param.getRvwComment());
    super.setCheckedValue(super.objectToHexBinary(getCheckedValueObj()));
    super.setCheckeValueString(getCheckedValueString());
    super.setUnit(this.param.getCheckUnit());
    super.setReviewDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, cdrRvwResult.getCreatedDate()));
    super.setReviewType(CDRConstants.REVIEW_TYPE.getType(cdrRvwResult.getReviewType()).getUiType());
    super.setReviewMethod(READY_FOR_SERIES.getType(cdRReultPameterEntity.getRvwMethod()).getUiType());
    super.setReviewStatus(CDRConstants.REVIEW_STATUS.getType(cdrRvwResult.getRvwStatus()).getUiType());
    super.setReviewDescription(cdrRvwResult.getDescription());
  }

  /**
   * Return the string representation of checked value
   *
   * @return String
   * @throws IOException
   * @throws ClassNotFoundException
   */
  public String getCheckedValueString() throws ClassNotFoundException, IOException {

    if (this.param.getCheckedValue() != null) {
      return getCheckedValueObj().getCalDataPhy().getSimpleDisplayValue();
    }
    return "";
  }

  /**
   * Method to get CheckedValue object
   *
   * @return actual review output
   * @throws IOException
   * @throws ClassNotFoundException
   */
  public CalData getCheckedValueObj() throws ClassNotFoundException, IOException {
    return getCDPObj(this.param.getCheckedValue());
  }

  /**
   * Method to convert byte array to CaldataPhy object
   *
   * @return actual review output
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private CalData getCDPObj(final byte[] dbData) throws ClassNotFoundException, IOException {
    return CalDataUtil.getCalDataObj(dbData);
  }

  private void setPidc(final CDRReviewResult cdrRvwResult) throws IcdmException {
    // get the Pidc id from active version
    this.pidc.setPidVersionID(cdrRvwResult.getPidcVersionId());
    this.pidc.setPidcId(this.pidcVersionLoader.getDataObjectByID(cdrRvwResult.getPidcVersionId()).getPidcId());
    this.pidc.createWsResponse();
    super.setPidcId(this.pidc.getWsResponse());
  }

  private void setVariant(final CDRReviewResult cdrRvwResult) throws IcdmException {
    if (null != cdrRvwResult.getPrimaryVariantId()) {
      PidVariantAdapter variant = new PidVariantAdapter(cdrRvwResult.getPrimaryVariantId(), this.serviceData);

      // The variant must only be added if it is valid, thaht means if all mandatory information are available
      // (according
      // to WSDL-file)
      if (variant.isValid()) {
        super.setVariantId(variant);
      }
    }
  }
}
