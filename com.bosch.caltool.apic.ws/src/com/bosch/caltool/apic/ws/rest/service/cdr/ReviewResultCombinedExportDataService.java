/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.cdr.CombinedReportExportHandler;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.cdr.CombinedReviewResultExcelExportData;
import com.bosch.caltool.icdm.model.cdr.CombinedRvwExportInputModel;

/**
 * @author say8cob
 *
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_RESULT_EDITOR_DATA)
public class ReviewResultCombinedExportDataService extends AbstractRestService{

  
  /**
   * Service returns the combined wrapper object contains all the necessary data to combined excel export for Review result and questionnaire response
   * @param exportInputModel as CombinedRvwExportInputModel
   * @return CombinedReviewResultExcelExportData as output
   * @throws IcdmException as exception
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getCombinedReviewAndQnaireEditorData(final CombinedRvwExportInputModel exportInputModel) throws IcdmException {
    CombinedReviewResultExcelExportData exportData = new CombinedReportExportHandler(getServiceData(),exportInputModel).getCombinedEditorDataForExport();
    return Response.ok(exportData).build();
  }
  
}
