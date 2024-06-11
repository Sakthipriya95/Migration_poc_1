package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.uc.UcpAttrLoader;
import com.bosch.caltool.icdm.bo.uc.UseCaseLoader;
import com.bosch.caltool.icdm.bo.uc.UseCaseSectionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrix;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrixVersion;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrix;


/**
 * Command class for Focus Matrix
 *
 * @author MKL2COB
 */
public class FocusMatrixCommand extends AbstractCommand<FocusMatrix, FocusMatrixLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public FocusMatrixCommand(final ServiceData serviceData, final FocusMatrix input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new FocusMatrixLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TFocusMatrix entity = new TFocusMatrix();

    if (null != getInputData().getUcpaId()) {
      UcpAttrLoader ucpaLoader = new UcpAttrLoader(getServiceData());
      entity.setTabvUcpAttr(ucpaLoader.getEntityObject(getInputData().getUcpaId()));
    }
    entity.setColorCode(getInputData().getColorCode());
    entity.setComments(getInputData().getComments());
    entity.setLink(getInputData().getLink());
    if (null != getInputData().getUseCaseId()) {
      UseCaseLoader usecaseLoader = new UseCaseLoader(getServiceData());
      entity.setTabvUseCas(usecaseLoader.getEntityObject(getInputData().getUseCaseId()));
    }

    if (null != getInputData().getSectionId()) {
      UseCaseSectionLoader ucSectionLoader = new UseCaseSectionLoader(getServiceData());
      entity.setTabvUseCaseSection(ucSectionLoader.getEntityObject(getInputData().getSectionId()));
    }
    if (null != getInputData().getAttrId()) {
      AttributeLoader attrLoader = new AttributeLoader(getServiceData());
      entity.setTabvAttribute(attrLoader.getEntityObject(getInputData().getAttrId()));

    }
    entity.setDeletedFlag(getInputData().getIsDeleted() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);
    if (null != getInputData().getFmVersId()) {
      FocusMatrixVersionLoader fmverLoader = new FocusMatrixVersionLoader(getServiceData());
      TFocusMatrixVersion fmVersObj = fmverLoader.getEntityObject(getInputData().getFmVersId());
      entity.setTFocusMatrixVersion(fmVersObj);
      Set<TFocusMatrix> fmSet = fmVersObj.getTFocusMatrixs();
      if (fmSet == null) {
        fmSet = new HashSet<>();
        fmVersObj.setTFocusMatrixs(fmSet);
      }
      fmSet.add(entity);
    }

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    FocusMatrixLoader loader = new FocusMatrixLoader(getServiceData());
    TFocusMatrix entity = loader.getEntityObject(getInputData().getId());


    UcpAttrLoader ucpaLoader = new UcpAttrLoader(getServiceData());
    entity.setTabvUcpAttr(ucpaLoader.getEntityObject(getInputData().getUcpaId()));

    entity.setColorCode(getInputData().getColorCode());
    entity.setComments(getInputData().getComments());
    entity.setLink(getInputData().getLink());
    if (null != getInputData().getUseCaseId()) {
      UseCaseLoader usecaseLoader = new UseCaseLoader(getServiceData());
      entity.setTabvUseCas(usecaseLoader.getEntityObject(getInputData().getUseCaseId()));
    }

    if (null != getInputData().getSectionId()) {
      UseCaseSectionLoader ucSectionLoader = new UseCaseSectionLoader(getServiceData());
      entity.setTabvUseCaseSection(ucSectionLoader.getEntityObject(getInputData().getSectionId()));
    }
    if (null != getInputData().getAttrId()) {
      AttributeLoader attrLoader = new AttributeLoader(getServiceData());
      entity.setTabvAttribute(attrLoader.getEntityObject(getInputData().getAttrId()));

    }
    entity.setDeletedFlag(getInputData().getIsDeleted() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);
    if (null != getInputData().getFmVersId()) {
      FocusMatrixVersionLoader fmverLoader = new FocusMatrixVersionLoader(getServiceData());
      entity.setTFocusMatrixVersion(fmverLoader.getEntityObject(getInputData().getFmVersId()));
    }


    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    FocusMatrixLoader loader = new FocusMatrixLoader(getServiceData());
    TFocusMatrix entity = loader.getEntityObject(getInputData().getId());
    entity.setDeletedFlag(ApicConstants.CODE_YES);
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
