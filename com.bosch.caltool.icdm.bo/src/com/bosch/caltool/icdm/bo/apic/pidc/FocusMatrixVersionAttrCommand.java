package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrixVersion;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrixVersionAttr;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersionAttr;


/**
 * Command class for Focus Matrix Version Attribute
 *
 * @author MKL2COB
 */
public class FocusMatrixVersionAttrCommand
    extends AbstractCommand<FocusMatrixVersionAttr, FocusMatrixVersionAttrLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public FocusMatrixVersionAttrCommand(final ServiceData serviceData, final FocusMatrixVersionAttr input,
      final boolean isUpdate, final boolean isDelete) throws IcdmException {
    super(serviceData, input, new FocusMatrixVersionAttrLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TFocusMatrixVersionAttr entity = new TFocusMatrixVersionAttr();

    if (null != getInputData().getFmVersId()) {
      FocusMatrixVersionLoader fmVersLoader = new FocusMatrixVersionLoader(getServiceData());
      TFocusMatrixVersion tFocusMatrixVersion = fmVersLoader.getEntityObject(getInputData().getFmVersId());
      entity.setTFocusMatrixVersion(tFocusMatrixVersion);
      Set<TFocusMatrixVersionAttr> fmVersionAttrSet = tFocusMatrixVersion.getTFocusMatrixVersionAttrs();
      if (null == fmVersionAttrSet) {
        // when the attr set is not initialised
        fmVersionAttrSet = new HashSet<>();
        tFocusMatrixVersion.setTFocusMatrixVersionAttrs(fmVersionAttrSet);
      }
      fmVersionAttrSet.add(entity);
    }
    if (null != getInputData().getAttrId()) {
      AttributeLoader attrLoader = new AttributeLoader(getServiceData());
      entity.setTabvAttribute(attrLoader.getEntityObject(getInputData().getAttrId()));
    }
    if (null != getInputData().getFmAttrRemarks()) {
      entity.setFmAttrRemark(getInputData().getFmAttrRemarks());
    }
    if (null != getInputData().getVariantId()) {
      PidcVariantLoader variantLoader = new PidcVariantLoader(getServiceData());
      entity.setTabvProjectVariant(variantLoader.getEntityObject(getInputData().getVariantId()));
    }
    if (null != getInputData().getSubVariantId()) {
      PidcSubVariantLoader subVariantLoader = new PidcSubVariantLoader(getServiceData());
      entity.setTabvProjectSubVariant(subVariantLoader.getEntityObject(getInputData().getSubVariantId()));
    }
    entity.setUsed(getInputData().getUsed());
    if (null != getInputData().getValueId()) {
      AttributeValueLoader attrValueLoader = new AttributeValueLoader(getServiceData());
      entity.setTabvAttrValue(attrValueLoader.getEntityObject(getInputData().getValueId()));
    }


    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    FocusMatrixVersionAttrLoader loader = new FocusMatrixVersionAttrLoader(getServiceData());
    TFocusMatrixVersionAttr entity = loader.getEntityObject(getInputData().getId());

    if (null != getInputData().getFmVersId()) {
      FocusMatrixVersionLoader fmVersLoader = new FocusMatrixVersionLoader(getServiceData());
      entity.setTFocusMatrixVersion(fmVersLoader.getEntityObject(getInputData().getFmVersId()));
    }
    if (null != getInputData().getAttrId()) {
      AttributeLoader attrLoader = new AttributeLoader(getServiceData());
      entity.setTabvAttribute(attrLoader.getEntityObject(getInputData().getAttrId()));
    }
    if (null != getInputData().getVariantId()) {
      PidcVariantLoader variantLoader = new PidcVariantLoader(getServiceData());
      entity.setTabvProjectVariant(variantLoader.getEntityObject(getInputData().getVariantId()));
    }
    if (null != getInputData().getSubVariantId()) {
      PidcSubVariantLoader subVariantLoader = new PidcSubVariantLoader(getServiceData());
      entity.setTabvProjectSubVariant(subVariantLoader.getEntityObject(getInputData().getVariantId()));
    }
    entity.setUsed(getInputData().getUsed());
    if (null != getInputData().getValueId()) {
      AttributeValueLoader attrValueLoader = new AttributeValueLoader(getServiceData());
      entity.setTabvAttrValue(attrValueLoader.getEntityObject(getInputData().getValueId()));
    }

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    FocusMatrixVersionAttrLoader loader = new FocusMatrixVersionAttrLoader(getServiceData());
    TFocusMatrixVersionAttr entity = loader.getEntityObject(getInputData().getId());
    entity.getTFocusMatrixVersion().getTFocusMatrixVersionAttrs().remove(entity);
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
