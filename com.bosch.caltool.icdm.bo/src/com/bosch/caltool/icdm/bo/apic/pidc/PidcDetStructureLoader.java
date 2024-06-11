package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvPidcDetStructure;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDetStructure;


/**
 * Loader class for PidcDetailsStructure
 *
 * @author pdh2cob
 */
public class PidcDetStructureLoader extends AbstractBusinessObject<PidcDetStructure, TabvPidcDetStructure> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public PidcDetStructureLoader(final ServiceData serviceData) {
    super(serviceData, "PidcDetailsStructure", TabvPidcDetStructure.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected PidcDetStructure createDataObject(final TabvPidcDetStructure entity) throws DataException {
    PidcDetStructure object = new PidcDetStructure();

    setCommonFields(object, entity);

    object.setAttrId(entity.getTabvAttribute().getAttrId());
    object.setPidAttrLevel(entity.getPidAttrLevel());
    object.setPidcVersId(entity.getTPidcVersion().getPidcVersId());

    return object;
  }


  /**
   * @param pidcVersionId Long
   * @return Map<Long, PidcDetStructure>
   * @throws DataException
   */
  public Map<Long, PidcDetStructure> getDetStructureForVersion(final Long pidcVersionId) throws DataException {
    Map<Long, PidcDetStructure> objMap = new ConcurrentHashMap<>();
    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    TPidcVersion tabvPidcVersion = pidcVersionLoader.getEntityObject(pidcVersionId);
    List<TabvPidcDetStructure> tabvPidcDetStructures = tabvPidcVersion.getTabvPidcDetStructures();
    for (TabvPidcDetStructure tabvPidcDetStructure : tabvPidcDetStructures) {
      PidcDetStructure pidcDetStructure = createDataObject(tabvPidcDetStructure);
      objMap.put(pidcDetStructure.getPidAttrLevel(), pidcDetStructure);
    }
    return objMap;
  }

}
