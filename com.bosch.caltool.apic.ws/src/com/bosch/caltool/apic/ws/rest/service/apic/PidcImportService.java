/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcExcelDataComparator;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.pidc.PidcImportCompareData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcImportExcelData;

/**
 * @author dja7cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_PIDC_IMPORT)
public class PidcImportService extends AbstractRestService {

  /**
   * @param pidcImportExcelData data from excel
   * @return Response
   * @throws IcdmException Exception in service call
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_COMPARE_EXCEL_PIDC)
  @CompressData
  public Response validateExcelData(final PidcImportExcelData pidcImportExcelData) throws IcdmException {
    PidcExcelDataComparator excelDataComp = new PidcExcelDataComparator(getServiceData());
    PidcImportCompareData pidcImportCompare = excelDataComp.compExcelDataWithPidc(pidcImportExcelData);
    return Response.ok(pidcImportCompare).build();
  }
}
