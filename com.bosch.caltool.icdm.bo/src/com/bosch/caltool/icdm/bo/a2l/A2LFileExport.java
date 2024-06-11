/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.A2LFileExportServiceInput;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityModel;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.a2l.WpRespLabelResponse;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.ice.ara2l.a2lparser.sapparser.A2LParser;
import com.bosch.ice.ara2l.a2lparserapi.aom.A2LDocument;

/**
 * @author say8cob
 */
public class A2LFileExport extends AbstractSimpleBusinessObject {

  /**
   *
   */
  private static final String A2L_NAME_APPEND_TXT = "_EXPORT";
  /**
   *
   */
  private static final String A2L_EXT = ".A2L";
  /**
   * server location for A2L Export
   */
  private static final String SERVER_PATH = Messages.getString("SERVICE_WORK_DIR") + "//A2L_EXPORT//";

  /**
   * @param serviceData as input
   */
  public A2LFileExport(final ServiceData serviceData) {
    super(serviceData);
  }


  /**
   * @param a2lExpServInp as input
   * @return string
   * @throws IcdmException exception
   */
  public String constructA2LFileExport(final A2LFileExportServiceInput a2lExpServInp) throws IcdmException {
    String outputZipFilePath;

    Long pidcA2lId = new A2lWpDefnVersionLoader(getServiceData())
        .getDataObjectByID(a2lExpServInp.getWpDefVersId()).getPidcA2lId();
    PidcA2l pidcA2l = new PidcA2lLoader(getServiceData()).getDataObjectByID(pidcA2lId);

    Long pidcVersId = pidcA2l.getPidcVersId();

    PidcVersionLoader versloader = new PidcVersionLoader(getServiceData());
    Long pidcId = versloader.getDataObjectByID(pidcVersId).getPidcId();


    A2lWorkPackageLoader a2lWpLoader = new A2lWorkPackageLoader(getServiceData());
    Map<String, A2lWorkPackage> a2lWorkPackageMap = a2lWpLoader.getByPidcVersionId(pidcVersId);

    A2lResponsibilityLoader a2lRespLoader = new A2lResponsibilityLoader(getServiceData());

    A2lResponsibilityModel a2lRespModel = a2lRespLoader.getByPidc(pidcId);


    A2LFile a2lFile = new A2LFileInfoLoader(getServiceData()).getDataObjectByID(pidcA2l.getA2lFileId());
    // download the a2l file
    A2LFileInfoProvider a2lFileInfoProvider = new A2LFileInfoProvider(getServiceData());

    A2LFileInfo a2lFileInfo = a2lFileInfoProvider.fetchA2LFileInfo(a2lFile);
    Map<String, Characteristic> a2lCharMap = a2lFileInfo.getAllModulesLabels();

    String tempA2lFileName = a2lFile.getFilename().toUpperCase();
    int a2lExtLoc = tempA2lFileName.lastIndexOf(A2L_EXT);
    String expA2lFileName =
        tempA2lFileName.substring(0, a2lExtLoc) + A2L_NAME_APPEND_TXT + tempA2lFileName.substring(a2lExtLoc);

    String filePath = a2lFileInfoProvider.getA2lFilePath(a2lFile);

    String uniqueText = ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_18);
    File rootDir = new File(SERVER_PATH);
    if (!rootDir.exists()) {
      rootDir.mkdir();
    }
    File zipDir = new File(rootDir.getAbsoluteFile() + File.separator + uniqueText);
    zipDir.mkdir();
    String expFolderPath = zipDir.getAbsolutePath();
    if (expA2lFileName.contains(".zip")) {
      expA2lFileName = expA2lFileName.substring(0, expA2lFileName.length() - 8) + ".a2l";
    }
    String expA2lFilePath = expFolderPath + File.separator + expA2lFileName;

    Map<String, Set<String>> workPackageLabelMap = new HashMap<>();
    Map<String, Set<String>> respLabelMap = new HashMap<>();

    constructWpAndRespParamMap(a2lExpServInp, workPackageLabelMap, respLabelMap);

    try {
      A2LParser parser = new A2LParser();
      String fileName = new File(filePath).getCanonicalPath();
      A2LDocument a2lDoc = parser.parse(fileName);


      A2lWpRespWriter writer = new A2lWpRespWriter(a2lExpServInp, a2lCharMap, a2lWorkPackageMap, a2lRespModel);

      writer.writeA2lWpResp(a2lDoc, workPackageLabelMap, respLabelMap, filePath, expA2lFilePath);

      outputZipFilePath = createExportA2lZipFile(expFolderPath);
    }
    catch (Exception exp) {
      throw new IcdmException(exp.getLocalizedMessage(), exp);
    }
    return outputZipFilePath;
  }


  /**
   * @param a2lExpServInp
   * @param workPackageLabelMap
   * @param respLabelMap
   * @throws IcdmException
   */
  private void constructWpAndRespParamMap(final A2LFileExportServiceInput a2lExpServInp,
      final Map<String, Set<String>> workPackageLabelMap, final Map<String, Set<String>> respLabelMap)
      throws IcdmException {
    List<WpRespLabelResponse> wpRespForA2LExport = new A2lWpResponsibilityLoader(getServiceData())
        .getWpRespForA2LExport(a2lExpServInp.getVarGrpId(), a2lExpServInp.getWpDefVersId());


    wpRespForA2LExport.forEach(wpRespLabelResp -> {
      if (null != wpRespLabelResp.getParamName()) {
        if (workPackageLabelMap.containsKey(wpRespLabelResp.getWpRespModel().getWpName())) {
          workPackageLabelMap.get(wpRespLabelResp.getWpRespModel().getWpName()).add(wpRespLabelResp.getParamName());
        }
        else {
          Set<String> paramSet = new HashSet<>();
          paramSet.add(wpRespLabelResp.getParamName());
          workPackageLabelMap.put(wpRespLabelResp.getWpRespModel().getWpName(), paramSet);
        }

        if (respLabelMap.containsKey(wpRespLabelResp.getWpRespModel().getWpRespName())) {
          respLabelMap.get(wpRespLabelResp.getWpRespModel().getWpRespName()).add(wpRespLabelResp.getParamName());
        }
        else {
          Set<String> paramSet = new HashSet<>();
          paramSet.add(wpRespLabelResp.getParamName());
          respLabelMap.put(wpRespLabelResp.getWpRespModel().getWpRespName(), paramSet);
        }
      }
    });
  }

  /**
   * @param expA2lFileName
   * @param a2lBytes
   * @return
   * @throws IOException
   */
  private String createExportA2lZipFile(final String expFolderPath) throws IOException {
    getLogger().info("Creating zip file. . .");
    String outputZipfileName = FilenameUtils.getBaseName(expFolderPath) + ".zip";
    String outputZipFilePath = SERVER_PATH + File.separator + outputZipfileName;
    getLogger().debug("outputZipFilePath : " + outputZipFilePath);
    // Compress output files to a single zip file
    ZipUtils.zip(Paths.get(expFolderPath), Paths.get(outputZipFilePath));
    return outputZipFilePath;
  }

}
