package com.bosch.caltool.icdm.bo.cdr.cdfx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2lWpDefnVersionLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.general.IcdmFileDataCommand;
import com.bosch.caltool.icdm.bo.general.IcdmFilesCommand;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.cdfx.TCDFxDelivery;
import com.bosch.caltool.icdm.model.a2l.WpRespLabelResponse;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.caltool.icdm.model.cdr.CdrReport;
import com.bosch.caltool.icdm.model.cdr.ParameterReviewDetails;
import com.bosch.caltool.icdm.model.cdr.cdfx.CDFxDelWpResp;
import com.bosch.caltool.icdm.model.cdr.cdfx.CDFxDelivery;
import com.bosch.caltool.icdm.model.cdr.cdfx.CDFxDeliveryWrapper;
import com.bosch.caltool.icdm.model.cdr.cdfx.CdfxDelvryParam;
import com.bosch.caltool.icdm.model.general.IcdmFileData;
import com.bosch.caltool.icdm.model.general.IcdmFiles;


/**
 * Command class for CDFxDelivery
 *
 * @author pdh2cob
 */
public class CDFxDeliveryCommand extends AbstractCommand<CDFxDelivery, CDFxDeliveryLoader> {

  private CDFxDeliveryWrapper cdfxDeliveryWrapper;

  private static final String NODE_TYPE = "CDFX_DELIVERY";

  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public CDFxDeliveryCommand(final ServiceData serviceData, final CDFxDelivery input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new CDFxDeliveryLoader(serviceData), resolveCommandModeA(isDelete, isUpdate));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TCDFxDelivery entity = new TCDFxDelivery();

    entity.setPidcA2l(new PidcA2lLoader(getServiceData()).getEntityObject(getInputData().getPidcA2lId()));
    entity.setVariant(new PidcVariantLoader(getServiceData()).getEntityObject(getInputData().getVariantId()));
    entity.setWpDefnVersion(
        new A2lWpDefnVersionLoader(getServiceData()).getEntityObject(getInputData().getWpDefnVersId()));
    entity.setScope(getInputData().getScope());
    entity.setReadinessYn(getInputData().getReadinessYn());

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);

    // save CDFxDelWpResp, CdfxDelvryParam
    saveResult(getNewData());

    // save output files in TabvIcdmFiles
    saveFile();

  }

  private void saveFile() throws IcdmException {

    // insert the data into icdm file table
    IcdmFiles icdmFile = new IcdmFiles();
    icdmFile.setFileCount(1L);
    icdmFile.setName(this.cdfxDeliveryWrapper.getOutputZipFileName());
    icdmFile.setNodeId(this.cdfxDeliveryWrapper.getCdfxDeliveryId());
    icdmFile.setNodeType(NODE_TYPE);
    IcdmFilesCommand fileCmd = new IcdmFilesCommand(getServiceData(), icdmFile, false);
    executeChildCommand(fileCmd);

    // insert data into icdm file data table
    IcdmFileData fileData = new IcdmFileData();
    // Get the bytes[] of the file
    try {
      byte[] fileAsBytes = CommonUtils.getFileAsByteArray(this.cdfxDeliveryWrapper.getServiceDirZipFilePath());
      fileData.setFileData(fileAsBytes);
    }
    catch (IOException exp) {
      throw new CommandException(exp.getMessage(), exp);
    }
    IcdmFiles icdmFileNewData = fileCmd.getNewData();
    fileData.setIcdmFileId(icdmFileNewData.getId());
    IcdmFileDataCommand fileDataCmd = new IcdmFileDataCommand(getServiceData(), fileData);
    executeChildCommand(fileDataCmd);

  }


  /**
   * @param wpRespMap
   * @return unique wp resp combinations
   */
  private Map<Long, java.util.Set<Long>> getUniqueWpRespCombinations(final Map<String, WpRespLabelResponse> wpRespMap) {
    Map<Long, java.util.Set<Long>> wpRespIdMap = new HashMap<>();

    for (WpRespLabelResponse wpLabResp : wpRespMap.values()) {
      Set<Long> respIdSet = wpRespIdMap.get(wpLabResp.getWpRespModel().getA2lWpId());
      if (respIdSet == null) {
        respIdSet = new java.util.HashSet<>();
      }
      respIdSet.add(wpLabResp.getWpRespModel().getA2lResponsibility().getId());
      wpRespIdMap.put(wpLabResp.getWpRespModel().getA2lWpId(), respIdSet);
    }
    return wpRespIdMap;
  }

  private void saveResult(final CDFxDelivery newCdfxDelivery) throws IcdmException {


    this.cdfxDeliveryWrapper.setCdfxDeliveryId(newCdfxDelivery.getId());

    List<CDFxDelWpResp> cdfxWpRespList = new ArrayList<>();
    Long variantId = newCdfxDelivery.getVariantId();
    Map<String, WpRespLabelResponse> paramWpRespLabelRespMap = null;
    CdrReport cdrReport = null;
    //This condition will be executed for single & multiple variants
    if (CommonUtils.isNotNull(variantId)) {
      paramWpRespLabelRespMap = this.cdfxDeliveryWrapper.getVariantWpRespLabelRespMap().get(variantId);
      cdrReport = this.cdfxDeliveryWrapper.getCdrReportMap().get(variantId);
    }
   //This condition will be executed for NO Variant Case
    else {
      paramWpRespLabelRespMap = this.cdfxDeliveryWrapper.getRelevantWpRespLabelMap();
      cdrReport = this.cdfxDeliveryWrapper.getCdrReport();
    }
    // if wp is selected, store all selected wp resp combinations
    if (this.cdfxDeliveryWrapper.getInput().getScope() == null) {
      for (WpRespModel wpRespModel : this.cdfxDeliveryWrapper.getInput().getWpRespModelList()) {
        CDFxDelWpResp cdfxDelWpResp = new CDFxDelWpResp();
        cdfxDelWpResp.setCdfxDeliveryId(newCdfxDelivery.getId());
        cdfxDelWpResp.setWpId(wpRespModel.getA2lWpId());
        cdfxDelWpResp.setRespId(wpRespModel.getA2lResponsibility().getId());
        cdfxWpRespList.add(cdfxDelWpResp);
      }
    }

    // if resp type is selected, store only filtered parameters' wp resp combinations
    else {
      for (Entry<Long, Set<Long>> wpRespIdEntry : getUniqueWpRespCombinations(paramWpRespLabelRespMap).entrySet()) {
        Set<Long> respIdList = wpRespIdEntry.getValue();
        for (Long respId : respIdList) {
          CDFxDelWpResp cdfxDelWpResp = new CDFxDelWpResp();
          cdfxDelWpResp.setCdfxDeliveryId(newCdfxDelivery.getId());
          cdfxDelWpResp.setWpId(wpRespIdEntry.getKey());
          cdfxDelWpResp.setRespId(respId);
          cdfxWpRespList.add(cdfxDelWpResp);
        }

      }
    }

    // save wp resp details
    List<CDFxDelWpResp> newCdfxWpRespList = saveWpRespDetails(cdfxWpRespList);

    // save delivery parameters
    List<CdfxDelvryParamCommand> deliveryParamCmdList = new ArrayList<>();

    for (Entry<String, WpRespLabelResponse> paramWpRespEntry : paramWpRespLabelRespMap.entrySet()) {
      CdfxDelvryParam deliveryParam = new CdfxDelvryParam();
      CDFxDelWpResp cdFxDelWpResp =
          getCDFxDelWpResp(newCdfxWpRespList, paramWpRespEntry.getValue().getWpRespModel().getA2lWpId(),
              paramWpRespEntry.getValue().getWpRespModel().getA2lResponsibility().getId());

      if (cdFxDelWpResp != null) {
        deliveryParam.setCdfxDelvryWpRespId(cdFxDelWpResp.getCdfxDelWpRespId());
      }
      deliveryParam.setParamId(paramWpRespEntry.getValue().getParamId());
      String paramName = paramWpRespEntry.getKey();
      List<ParameterReviewDetails> paramRvwDetList = cdrReport.getParamRvwDetMap().get(paramName);

      // paramRvwDetList cannot be null here
      deliveryParam.setRvwResultId(paramRvwDetList.get(0).getRvwID());
      deliveryParamCmdList.add(new CdfxDelvryParamCommand(getServiceData(), deliveryParam, false, false));

    }

    // insert cdfx delivery param details
    for (CdfxDelvryParamCommand cdfxDelvryParamCommand : deliveryParamCmdList) {
      executeChildCommand(cdfxDelvryParamCommand);
    }
  }

  /**
   * @param cdfxWpRespList cdfxWpRespList
   * @return newCdfxWpRespList newCdfxWpRespList
   * @throws IcdmException IcdmException
   */
  private List<CDFxDelWpResp> saveWpRespDetails(final List<CDFxDelWpResp> cdfxWpRespList) throws IcdmException {
    List<CDFxDelWpRespCommand> cmdList = new ArrayList<>();
    for (CDFxDelWpResp cdfxDelWpResp : cdfxWpRespList) {
      cmdList.add(new CDFxDelWpRespCommand(getServiceData(), cdfxDelWpResp, false, false));
    }

    List<CDFxDelWpResp> newCdfxWpRespList = new ArrayList<>();
    for (CDFxDelWpRespCommand cdFxDelWpRespCommand : cmdList) {
      executeChildCommand(cdFxDelWpRespCommand);
      newCdfxWpRespList.add(cdFxDelWpRespCommand.getNewData());
    }
    return newCdfxWpRespList;
  }

  private CDFxDelWpResp getCDFxDelWpResp(final List<CDFxDelWpResp> newCdfxWpRespList, final Long wpId,
      final Long respId) {
    for (CDFxDelWpResp cdFxDelWpResp : newCdfxWpRespList) {
      if (cdFxDelWpResp.getWpId().equals(wpId) && cdFxDelWpResp.getRespId().equals(respId)) {
        return cdFxDelWpResp;
      }
    }
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // NA
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // NA
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // NA
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // NA
  }


  /**
   * @param cdfxDeliveryWrapper the cdfxDeliveryWrapper to set
   */
  public void setCdfxDeliveryWrapper(final CDFxDeliveryWrapper cdfxDeliveryWrapper) {
    this.cdfxDeliveryWrapper = cdfxDeliveryWrapper;
  }


}
