package com.bosch.caltool.icdm.bo.a2l;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWorkPackage;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;


/**
 * Loader class for A2l Workpackage
 *
 * @author pdh2cob
 */
public class A2lWorkPackageLoader extends AbstractBusinessObject<A2lWorkPackage, TA2lWorkPackage> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public A2lWorkPackageLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.A2L_WORK_PACKAGE, TA2lWorkPackage.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public A2lWorkPackage createDataObject(final TA2lWorkPackage entity) throws DataException {
    A2lWorkPackage object = new A2lWorkPackage();

    setCommonFields(object, entity);

    object.setName(entity.getWpName());
    object.setDescription(entity.getWpDesc());
    object.setNameCustomer(entity.getWpNameCust());
    object.setPidcVersId(entity.getPidcVersion().getPidcVersId());
    if (entity.getParentTA2lWorkPackage() != null) {
      object.setParentA2lWpId(entity.getParentTA2lWorkPackage().getA2lWpId());
    }

    return object;
  }

  /**
   * @param pidcVersId
   * @return
   * @throws DataException
   */
  public Map<String, A2lWorkPackage> getByPidcVersionId(final Long pidcVersId) throws DataException {
    // load all the workpackages associated to a PIDC version
    // key-A2LWorkpackage name,value-A2LWorkpackage
    Map<String, A2lWorkPackage> wpPalMap = new HashMap<>();
    PidcVersionLoader pidcVersLoader = new PidcVersionLoader(getServiceData());
    List<TA2lWorkPackage> wpPalList = pidcVersLoader.getEntityObject(pidcVersId).getTA2lWorkPackageList();
    for (TA2lWorkPackage wpPal : wpPalList) {
      wpPalMap.put(wpPal.getWpName().toUpperCase(), createDataObject(wpPal));
    }
    return wpPalMap;
  }

  /**
   * @param pidcVersId pidcVersId
   * @return Map of Long, A2lWorkPackage
   * @throws IcdmException error while retrieving data
   */
  public Map<Long, A2lWorkPackage> getWpByPidcVers(final Long pidcVersId) throws IcdmException {
    List<TA2lWorkPackage> dbA2lWpList =
        new PidcVersionLoader(getServiceData()).getEntityObject(pidcVersId).getTA2lWorkPackageList();
    Map<Long, A2lWorkPackage> retMap = new HashMap<>();
    A2lWorkPackageLoader loader = new A2lWorkPackageLoader(getServiceData());
    for (TA2lWorkPackage entity : dbA2lWpList) {
      A2lWorkPackage a2lWp = loader.getDataObjectByID(entity.getA2lWpId());
      retMap.put(a2lWp.getId(), a2lWp);
    }
    return retMap;
  }
}
