/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipEntry;

import org.apache.commons.io.IOUtils;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.A2lCompliParameterServiceResponse;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;

/**
 * Business class to fetch the parse the a2l file byte array and fetch the compli-parameter from the a2l file model
 *
 * @author svj7cob
 */
// Task 263282
public class A2lCompliParameterLoader extends AbstractSimpleBusinessObject {

  /**
   * Class Instance
   *
   * @param serviceData initialise the service transaction
   */
  public A2lCompliParameterLoader(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * @param fileInputStream fileInputStream
   * @param webFlowId web Flow Id
   * @param fileName input file Name
   * @return the compli-parameter response
   * @throws IcdmException the exception related to A2l Parser, I/O exception in A2l file processing
   */
  public A2lCompliParameterServiceResponse getCompliParameters(final InputStream fileInputStream,
      final String webFlowId, final String fileName)
      throws IcdmException {
    A2lCompliParameterServiceResponse response = null;
    try {
      if (CommonUtils.isEmptyString(webFlowId)) {
        throw new InvalidInputException("A2L_COMPLI_PARAMS.WEBFLOW_ID_MISSING");
      }
      else if (CommonUtils.isNull(fileInputStream)) {
        throw new InvalidInputException("A2L_COMPLI_PARAMS.A2L_MISSING");
      }

      // Convert to byte array
      byte[] byteArray = IOUtils.toByteArray(fileInputStream);
      Map<String, Entry<byte[], ZipEntry>> unzippedMap = ZipUtils.unzipIfZipped2(byteArray, fileName);
      Entry<String, Entry<byte[], ZipEntry>> unzippedEnry = unzippedMap.entrySet().iterator().next();
      byte[] unzippedByteArray = unzippedEnry.getValue().getKey();
      ZipEntry zipDet = unzippedEnry.getValue().getValue();

      // Convert A2L File to model
      A2LFileInfo a2lFileInfo = A2LFileInfoFetcher.INSTANCE.getA2lFileInfo(unzippedByteArray, getLogger());

      // Create service response model
      response = createCompliParamResponse(a2lFileInfo);
      response.setFileName(unzippedEnry.getKey());
      response.setFileSize(unzippedByteArray.length);

      if (zipDet != null) {
        response.setFileName(zipDet.getName());
        response.setFileSize(zipDet.getSize());
        String lastModifiedDate = new SimpleDateFormat(DateFormat.DATE_FORMAT_13, Locale.ENGLISH)
            .format(new Date(zipDet.getLastModifiedTime().toMillis()));
        response.setLastModifiedDate(lastModifiedDate);
        // TODO set date to standard ICDM pojo date format
      }
    }
    catch (IOException exp) {
      throw new InvalidInputException(exp.getMessage(), exp);
    }
    return response;
  }

  /**
   * @param fileId A2L File Id
   * @param pidcA2lId
   * @return the compli-parameter response
   * @throws IcdmException the exception related to A2l Parser, I/O exception in A2l file processing
   */
  public A2lCompliParameterServiceResponse getCompliParametersForPdfAReport(final Long fileId, final Long pidcA2lId)
      throws IcdmException {
    A2lCompliParameterServiceResponse response = null;
    PidcA2l pidcA2l = new PidcA2lLoader(getServiceData()).getDataObjectByID(pidcA2lId);
    A2LFile a2lFile = new A2LFileInfoLoader(getServiceData()).getDataObjectByID(fileId);

    String filename = a2lFile.getFilename();

    A2LFileInfo a2LFileInfo = new A2LFileInfoProvider(getServiceData()).fetchA2LFileInfo(a2lFile);
    if (pidcA2l.getVcdmA2lName() != null) {
      a2LFileInfo.setFileName(pidcA2l.getVcdmA2lName());
      filename = pidcA2l.getVcdmA2lName();
    }
    // Create service response model
    response = createCompliParamResponse(a2LFileInfo);
    response.setFileName(filename);
    response.setFileSize(a2lFile.getA2lfilesize().longValue());
    response.setLastModifiedDate(a2lFile.getFiledate());

    return response;
  }


  /**
   * @param a2lFileInfo a2lFileInfo
   * @return map of parameter name and ssd class name
   */
  private A2lCompliParameterServiceResponse createCompliParamResponse(final A2LFileInfo a2lFileInfo) {
    getLogger().debug("Fetching of Compli Parameters started ...");
    Set<String> allModulesLabels = a2lFileInfo.getAllModulesLabels().keySet();
    ParameterLoader prmLdr = new ParameterLoader(getServiceData());
    Map<String, String> compliParamMap = new TreeMap<>();
    Map<String, String> qSSDParamMap = new TreeMap<>();

    for (String paramName : allModulesLabels) {
      // Get the compli/qssd class of the parameter. If class is not null, the param is of that type

      String ssdClass = prmLdr.getCompliParameterClass(paramName);
      if (ssdClass != null) {
        compliParamMap.put(paramName, ssdClass);
      }

      String qssdClass = prmLdr.getQssdParameterClass(paramName);
      if (qssdClass != null) {
        qSSDParamMap.put(paramName, qssdClass);
      }
    }

    A2lCompliParameterServiceResponse response = new A2lCompliParameterServiceResponse();
    response.getA2lCompliParamMap().putAll(compliParamMap);
    response.setTotalParamSize(allModulesLabels.size());
    response.setCompliParamSize(compliParamMap.size());
    response.setQssdParamSize(qSSDParamMap.size());
    response.setA2lQSSDParamMap(qSSDParamMap);

    getLogger().debug("Fetching of Compli Parameters completed. Number of Compli Parameters = {}/{}",
        response.getCompliParamSize(), response.getTotalParamSize());

    return response;
  }


}

