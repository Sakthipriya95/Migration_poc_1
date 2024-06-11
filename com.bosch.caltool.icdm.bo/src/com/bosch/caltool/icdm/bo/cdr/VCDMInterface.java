package com.bosch.caltool.icdm.bo.cdr;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.PasswordServiceWrapper;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CompliRvwMetaDataWithDstInp;
import com.bosch.caltool.icdm.model.cdr.VcdmHexFileData;
import com.bosch.caltool.security.Decryptor;
import com.bosch.easee.eASEEcdm_Service.EASEEService;
import com.bosch.easee.eASEEcdm_Service.EASEEServiceException;
import com.bosch.easee.eASEEcdm_Service.model.vCDMProcessResult;
import com.vector.easee.application.cdmservice.ObjInfoEntryType;
import com.vector.easee.application.cdmservice.WSAttrMapList;

/**
 * This class handles the download of A2L file from vCDM And to avoid cyclic dependency due to jpa plugins
 *
 * @author hef2fe
 */
public class VCDMInterface {

  /**
   * The ILoggerAdapter instance for logging EASEE messages.
   */
  private final ILoggerAdapter easeeLogger;

  // the vCDM service handle with super user credentials
  private final EASEEService easeeSuperService;

  /**
   * This constructor user VCDM super user credentials to log into VCDM This constructor is used for loading the
   * a2lFiles
   *
   * @param easeeLogger the logger to be used for EaseeService
   * @throws Exception exception thrown if the WebService can not be initialized
   */
  public VCDMInterface(final ILoggerAdapter easeeLogger) throws Exception {

    this.easeeLogger = easeeLogger;
    final String wsUser = Messages.getString("EASEEService.USER_NAME");
    // Get the passord from the Web service
    String passwordKey = Messages.getString(CommonUtilConstants.EASEE_SERVICE_USER_PASS);

    final PasswordServiceWrapper passWordWrapper = new PasswordServiceWrapper(this.easeeLogger);
    final String wsPassword =
        Decryptor.getInstance().decrypt(passWordWrapper.getPassword(passwordKey), this.easeeLogger);
    String wsDomain = Messages.getString("EASEEService.DOMAAIN_NAME");

    int serverType = EASEEService.DGS_CDM_PRO;
    if ("DGS_CDM_QUA".equals(Messages.getString("EASEEService.WS_SERVER"))) {
      serverType = EASEEService.DGS_CDM_QUA;
    }

    this.easeeSuperService = new EASEEService(easeeLogger);

    this.easeeSuperService.init(wsUser, wsPassword, wsDomain, serverType);

    if (!this.easeeSuperService.isWebServiceLoggedIn()) {
      throw new Exception("Web service login failed");
    }
  }


  /**
   * The hex file is fetched using versNumber and stored in temp folder.
   *
   * @param versNumber version number of the delivered HEX file or NULL in case of erors
   * @param outputDirectory the output directory
   * @return hexfile path
   */
  public VcdmHexFileData loadHexFile(final long versNumber, final String outputDirectory) {
    VcdmHexFileData hexFileData = new VcdmHexFileData();
    this.easeeLogger.info("Get Hex file ");

    List<ObjInfoEntryType> listOfHex = this.easeeSuperService.getContent("HEX", String.valueOf(versNumber));

    ObjInfoEntryType hexFileObj = listOfHex.get(0);

    WSAttrMapList attr = this.easeeSuperService.getVersionAttributes(hexFileObj.getVersionNo());
    String hexFileName = "";
    if (null != attr) {
      List<String> hexFileList = attr.get("ORIGINAL FILE");
      if ((null != hexFileList) && (null != hexFileList.get(0))) {
        hexFileName = hexFileList.get(0);
      }
    }
    hexFileName = hexFileName.substring(0, hexFileName.length() - 4);
    String hexFilePath =
        Messages.getString("SERVICE_WORK_DIR") + File.separator + hexFileName + "_" + versNumber + ".hex";
    if (CommonUtils.isNotEmptyString(outputDirectory)) {
      if (!new File(outputDirectory).exists()) {
        new File(outputDirectory).mkdir();
      }
      hexFilePath = outputDirectory + File.separator + hexFileName + "_" + versNumber + ".hex";
    }

    this.easeeLogger.info("Hex file path : " + hexFilePath);
    FileInputStream fileInputStream = null;
    byte[] bytesArray = null;
    int bytesRead = 0;
    // Download the A2L file using the web service.
    try {
      this.easeeSuperService.getEaseeFile(hexFileObj, hexFilePath);
      File file = new File(hexFilePath);
      bytesArray = new byte[(int) file.length()];

      // read file into bytes[]
      fileInputStream = new FileInputStream(file);
      bytesRead = fileInputStream.read(bytesArray);
      if (bytesRead > 0) {
        hexFileData.setHexFileBytes(bytesArray);
      }
      hexFileData.setFileName(hexFileName + "_" + versNumber + ".hex");
    }
    catch (EASEEServiceException e) {
      this.easeeLogger.error("Error loading the  hex file file : ", e);
    }
    catch (IOException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
    }
    finally {
      if (fileInputStream != null) {
        try {
          fileInputStream.close();
        }
        catch (IOException e) {
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
        }
      }

    }

    return hexFileData;
  }

  /**
   * @param outputPath path where hex file will be downloaded
   * @param metaDataWithDstInp input for hex file download
   * @return VcdmHexFileData
   */
  public VcdmHexFileData exportHexFromDst(final String outputPath,
      final CompliRvwMetaDataWithDstInp metaDataWithDstInp) {

    VcdmHexFileData hexFileData = new VcdmHexFileData();

    if (metaDataWithDstInp.isCheckedIn()) {
      this.easeeLogger.debug("Download hex file from vcdm dst for checked-in dataset with dst id {}:",
          metaDataWithDstInp.getVcdmDstId());
      hexFileData = loadHexFile(metaDataWithDstInp.getVcdmDstId(), null);
    }
    else {
      if (!new File(outputPath).exists()) {
        new File(outputPath).mkdir();
      }

      this.easeeLogger.debug("Download hex file from vcdm dst for checked-out dataset with dst id {}:",
          metaDataWithDstInp.getVcdmDstId());
      vCDMProcessResult vCdmProcessResult =
          this.easeeSuperService.exportDST(metaDataWithDstInp.getAprjName(), metaDataWithDstInp.getAprjRevision(),
              metaDataWithDstInp.getDstName(), metaDataWithDstInp.getDstRevision(), outputPath);
      File hexFile = new File(vCdmProcessResult.getFilePath());
      hexFileData.setFileName(hexFile.getName());
      try {
        hexFileData.setHexFileBytes(Files.readAllBytes(hexFile.toPath()));
      }
      catch (IOException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
      }
    }
    return hexFileData;

  }


}
