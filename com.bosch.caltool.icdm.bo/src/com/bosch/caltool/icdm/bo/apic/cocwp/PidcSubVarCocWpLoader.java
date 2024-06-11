package com.bosch.caltool.icdm.bo.apic.cocwp;


import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcSubVariantLoader;
import com.bosch.caltool.icdm.bo.wp.WorkPackageDivisionLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectSubVariant;
import com.bosch.caltool.icdm.database.entity.apic.cocwp.TPidcSubVarCocWp;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcSubVarCocWp;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;


/**
 * Loader class for PidcSuVarCocWp
 *
 * @author UKT1COB
 */
public class PidcSubVarCocWpLoader extends AbstractBusinessObject<PidcSubVarCocWp, TPidcSubVarCocWp> {

  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public PidcSubVarCocWpLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.PIDC_SUB_VAR_COC_WP, TPidcSubVarCocWp.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected PidcSubVarCocWp createDataObject(final TPidcSubVarCocWp tPidcSubVarCocWp) throws DataException {
    PidcSubVarCocWp pidcSubVarCocWp = new PidcSubVarCocWp();

    setCommonFields(pidcSubVarCocWp, tPidcSubVarCocWp);

    pidcSubVarCocWp.setPidcSubVarId(tPidcSubVarCocWp.getTabvprojsubvar().getSubVariantId());

    WorkPackageDivision wrkPckgDiv = new WorkPackageDivisionLoader(getServiceData())
        .getDataObjectByID(tPidcSubVarCocWp.getTwrkpkgdiv().getWpDivId());
    pidcSubVarCocWp.setWPDivId(wrkPckgDiv.getId());
    pidcSubVarCocWp.setDeleted(yOrNToBoolean(wrkPckgDiv.getDeleted()));

    pidcSubVarCocWp.setName(wrkPckgDiv.getWpName());
    pidcSubVarCocWp.setDescription(wrkPckgDiv.getWpDesc());
    pidcSubVarCocWp.setUsedFlag(tPidcSubVarCocWp.getUsedFlag());

    return pidcSubVarCocWp;
  }

  /**
   * Method return set of PidcSubVarCocWp based on Sub variant id
   * @param subVarId as Input
   * @return Set<PidcSubVarCocWp>
   * @throws DataException as Exception
   */
  public Set<PidcSubVarCocWp> getAllPidcSubVarCocWpBySubVarId(final Long subVarId) throws DataException {
    Set<PidcSubVarCocWp> pidcSubVarCocWpSet = new HashSet<>();
    TabvProjectSubVariant tabvProjectSubVariant = new PidcSubVariantLoader(getServiceData()).getEntityObject(subVarId);
    for (TPidcSubVarCocWp tPidcSubVarCocWp : tabvProjectSubVariant.gettPidcSubVarCocWp()) {
      pidcSubVarCocWpSet.add(createDataObject(tPidcSubVarCocWp));
    }
    return pidcSubVarCocWpSet;
  }

}
