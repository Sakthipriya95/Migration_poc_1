package com.bosch.caltool.icdm.bo.apic.pidc;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrix;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrixVersion;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.FM_VERS_STATUS;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrix;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersion;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersionAttr;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;


/**
 * Command class for Focus Matrix Version
 *
 * @author MKL2COB
 */
public class FocusMatrixVersionCommand extends AbstractCommand<FocusMatrixVersion, FocusMatrixVersionLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public FocusMatrixVersionCommand(final ServiceData serviceData, final FocusMatrixVersion input,
      final boolean isUpdate, final boolean isDelete) throws IcdmException {
    super(serviceData, input, new FocusMatrixVersionLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TFocusMatrixVersion entity = new TFocusMatrixVersion();


    PidcVersionLoader pidVerLoader = new PidcVersionLoader(getServiceData());
    TPidcVersion tPidcVersion = pidVerLoader.getEntityObject(getInputData().getPidcVersId());
    Set<TFocusMatrixVersion> tFocusMatrixVersions = tPidcVersion.getTFocusMatrixVersions();
    if (null == tFocusMatrixVersions) {
      tFocusMatrixVersions = new HashSet<>();
      tPidcVersion.setTFocusMatrixVersions(tFocusMatrixVersions);
    }
    tFocusMatrixVersions.add(entity);
    entity.setTPidcVersion(tPidcVersion);


    entity.setName(getInputData().getName());
    if (null != getInputData().getStatus()) {
      entity.setStatus(getInputData().getStatus());
      long revNumber = getInputData().getStatus().equals(ApicConstants.FM_VERS_STATUS.WORKING_SET.getDbStatus()) ? 0
          : findRevisionNumber(tPidcVersion) + 1;
      entity.setRevNumber(revNumber);
    }
    entity.setRvwStatus(getInputData().getRvwStatus());

    if (null != getInputData().getReviewedUser()) {
      UserLoader userLoader = new UserLoader(getServiceData());
      entity.setReviewedUser(userLoader.getEntityObject(getInputData().getReviewedUser()));
    }
    if (null != getInputData().getReviewedDate()) {
      try {
        entity.setReviewedDate(string2timestamp(getInputData().getReviewedDate()));
      }
      catch (ParseException exp) {
        throw new CommandException(exp.getMessage(), exp);
      }
    }
    if (null != getInputData().getLink()) {
      entity.setLink(getInputData().getLink());
    }
    if (null != getInputData().getRemark()) {
      entity.setRemark(getInputData().getRemark());
    }
    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
    if (getInputData().getId() != null) {
      // if there exists a reference focus matrix version (happens when we create version from working set or when
      // reviewed flag is changed)
      addFocusMatrixDef(entity);
    }

    if (getInputData().getStatus().equals(FM_VERS_STATUS.OLD.getDbStatus())) {
      // for cases other than working set
      addFmVersAttrs(tPidcVersion, entity);
    }
  }

  /**
   * @param tPidcVersion
   * @param entity
   * @throws IcdmException
   */
  private void addFmVersAttrs(final TPidcVersion tPidcVersion, final TFocusMatrixVersion entity) throws IcdmException {

    FocusMatrixVersionAttrCommand cmdFmVersAttr;


    PidcVersionWithDetails pidcVersionWithDetails =
        new PidcVersionLoader(getServiceData()).getPidcVersionWithDetails(tPidcVersion.getPidcVersId());
    for (PidcVersionAttribute pidcAttr : pidcVersionWithDetails.getPidcVersionAttributeMap().values()) {
      if (pidcAttr.isFocusMatrixApplicable()) {
        // if the pidc attribute is focus matrix applicable
        FocusMatrixVersionAttr focusMatrixAttribute;
        if (pidcAttr.isAtChildLevel()) {
          // variant level
          for (Entry<Long, Map<Long, PidcVariantAttribute>> variantAndAttrs : pidcVersionWithDetails
              .getPidcVariantAttributeMap().entrySet()) {
            PidcVariantAttribute pidcVariantAttribute = variantAndAttrs.getValue().get(pidcAttr.getAttrId());
            if (null != pidcVariantAttribute) {
              // for each variant attributes ,create a fm version attribute
              focusMatrixAttribute = setFieldsForFMVersAttr(entity, pidcAttr);
              focusMatrixAttribute.setVariantId(variantAndAttrs.getKey());
              cmdFmVersAttr = new FocusMatrixVersionAttrCommand(getServiceData(), focusMatrixAttribute, false, false);
              executeChildCommand(cmdFmVersAttr);
            }
          }

          for (Entry<Long, Map<Long, PidcSubVariantAttribute>> variantAndAttrs : pidcVersionWithDetails
              .getPidcSubVariantAttributeMap().entrySet()) {
            // sub variant level attributes
            PidcSubVariantAttribute pidcSubVariantAttribute = variantAndAttrs.getValue().get(pidcAttr.getAttrId());
            if (null != pidcSubVariantAttribute) {
              // for each sub variant attributes ,create a fm version attribute
              focusMatrixAttribute = setFieldsForFMVersAttr(entity, pidcAttr);
              focusMatrixAttribute.setSubVariantId(variantAndAttrs.getKey());
              focusMatrixAttribute.setVariantId(pidcSubVariantAttribute.getVariantId());
              cmdFmVersAttr = new FocusMatrixVersionAttrCommand(getServiceData(), focusMatrixAttribute, false, false);
              executeChildCommand(cmdFmVersAttr);
            }
          }
        }
        else {
          focusMatrixAttribute = setFieldsForFMVersAttr(entity, pidcAttr);
          cmdFmVersAttr = new FocusMatrixVersionAttrCommand(getServiceData(), focusMatrixAttribute, false, false);
          executeChildCommand(cmdFmVersAttr);
        }
      }
    }
  }

  /**
   * @param entity
   * @param pidcAttr
   * @return
   */
  private FocusMatrixVersionAttr setFieldsForFMVersAttr(final TFocusMatrixVersion entity,
      final PidcVersionAttribute pidcAttr) {
    FocusMatrixVersionAttr focusMatrixAttribute = new FocusMatrixVersionAttr();
    focusMatrixAttribute.setAttrId(pidcAttr.getAttrId());
    focusMatrixAttribute.setFmVersId(entity.getFmVersId());
    focusMatrixAttribute.setUsed(pidcAttr.getUsedFlag());
    focusMatrixAttribute.setValueId(pidcAttr.getValueId());
    focusMatrixAttribute.setFmAttrRemarks(pidcAttr.getFmAttrRemark());
    return focusMatrixAttribute;
  }

  /**
   * @param entity
   * @throws IcdmException
   */
  private void addFocusMatrixDef(final TFocusMatrixVersion entity) throws IcdmException {
    FocusMatrixVersionLoader fmVersionLoader = new FocusMatrixVersionLoader(getServiceData());
    FocusMatrixLoader focusMatrixLoader = new FocusMatrixLoader(getServiceData());
    Collection<TFocusMatrix> fmDetailsMap = fmVersionLoader.getEntityObject(getInputData().getId()).getTFocusMatrixs();
    if (CommonUtils.isNotEmpty(fmDetailsMap)) {
      for (TFocusMatrix tFocusMatrix : fmDetailsMap) {
        FocusMatrix focusMatrix = focusMatrixLoader.getDataObjectByID(tFocusMatrix.getFmId());
        createFocusMatrixDef(focusMatrix, entity);
      }
    }

  }

  /**
   * @param entity
   * @param concurrentMap
   * @throws IcdmException
   */
  private void createFocusMatrixDef(final FocusMatrix focusMatrix, final TFocusMatrixVersion entity)
      throws IcdmException {
    if (!focusMatrix.getIsDeleted()) {
      createFmDef(focusMatrix, entity);
    }

  }

  /**
   * @param fmDetails
   * @param entity
   * @throws IcdmException
   */
  private void createFmDef(final FocusMatrix fmDetails, final TFocusMatrixVersion entity) throws IcdmException {
    FocusMatrix fmObj = new FocusMatrix();
    FocusMatrixCommand focusMatrixCommand = new FocusMatrixCommand(getServiceData(), fmObj, false, false);

    fmObj.setAttrId(fmDetails.getAttrId());
    fmObj.setUseCaseId(fmDetails.getUseCaseId());
    fmObj.setSectionId(fmDetails.getSectionId());
    fmObj.setUcpaId(fmDetails.getUcpaId());
    fmObj.setFmVersId(entity.getFmVersId());

    if (getInputData().getId() != null) {
      fmObj.setColorCode(fmDetails.getColorCode());
      fmObj.setComments(fmDetails.getComments());
    }
    else {
      // if new FM version is created for a new PIDC Version, reset color and comment
      fmObj.setColorCode("");
      fmObj.setComments("");
    }
    fmObj.setLink(fmDetails.getLink());
    executeChildCommand(focusMatrixCommand);

  }

  /**
   * @param tPidcVersion TPidcVersion
   * @return long
   */
  private long findRevisionNumber(final TPidcVersion tPidcVersion) {
    long maxRev = 0;
    for (TFocusMatrixVersion fmVers : tPidcVersion.getTFocusMatrixVersions()) {
      if (fmVers.getRevNumber() > maxRev) {
        maxRev = fmVers.getRevNumber();
      }
    }
    return maxRev;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    FocusMatrixVersionLoader loader = new FocusMatrixVersionLoader(getServiceData());
    TFocusMatrixVersion entity = loader.getEntityObject(getInputData().getId());

    if (null != getInputData().getPidcVersId()) {
      PidcVersionLoader pidVerLoader = new PidcVersionLoader(getServiceData());
      entity.setTPidcVersion(pidVerLoader.getEntityObject(getInputData().getPidcVersId()));
    }

    entity.setName(getInputData().getName());
    entity.setRevNumber(getInputData().getRevNum());
    entity.setStatus(getInputData().getStatus());

    UserLoader userLoader = new UserLoader(getServiceData());
    entity.setReviewedUser(userLoader.getEntityObject(getInputData().getReviewedUser()));
    try {
      entity.setReviewedDate(string2timestamp(getInputData().getReviewedDate()));
    }
    catch (ParseException exp) {
      throw new CommandException(exp.getMessage(), exp);
    }
    entity.setLink(getInputData().getLink());
    entity.setRemark(getInputData().getRemark());

    if (CommonUtils.isNotEqual(getInputData().getRvwStatus(), entity.getRvwStatus()) &&
        (getInputData().getRvwStatus().equals(ApicConstants.CODE_YES))) {
      FocusMatrixVersion newFMVersion = new FocusMatrixVersion();
      CommonUtils.shallowCopy(newFMVersion, getInputData());
      // Create a version of focus matrix review
      newFMVersion.setStatus(FM_VERS_STATUS.OLD.getDbStatus());
      newFMVersion.setName("");
      FocusMatrixVersionCommand fmVersCmd = new FocusMatrixVersionCommand(getServiceData(), newFMVersion, false, false);
      executeChildCommand(fmVersCmd);
    }
    entity.setRvwStatus(getInputData().getRvwStatus());

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    FocusMatrixVersionLoader loader = new FocusMatrixVersionLoader(getServiceData());
    TFocusMatrixVersion entity = loader.getEntityObject(getInputData().getId());

    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // TODO Auto-generated method stub
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
    // TODO Auto-generated method stub
  }

}
