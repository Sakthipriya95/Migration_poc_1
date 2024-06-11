package com.bosch.caltool.icdm.bo.apic.cocwp;


import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.wp.WorkPackageDivisionLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.database.entity.apic.cocwp.TPidcVariantCocWp;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVariantCocWp;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;


/**
 * Loader class for PidcVariantCocWp
 *
 * @author UKT1COB
 */
public class PidcVariantCocWpLoader extends AbstractBusinessObject<PidcVariantCocWp, TPidcVariantCocWp> {

  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public PidcVariantCocWpLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.PIDC_VARIANT_COC_WP, TPidcVariantCocWp.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected PidcVariantCocWp createDataObject(final TPidcVariantCocWp tPidcVarCocWp) throws DataException {
    PidcVariantCocWp pidcVarCocWp = new PidcVariantCocWp();

    setCommonFields(pidcVarCocWp, tPidcVarCocWp);

    pidcVarCocWp.setPidcVariantId(tPidcVarCocWp.getTabvprojvar().getVariantId());

    WorkPackageDivision wrkPckgDiv =
        new WorkPackageDivisionLoader(getServiceData()).getDataObjectByID(tPidcVarCocWp.getTwrkpkgdiv().getWpDivId());
    pidcVarCocWp.setWPDivId(wrkPckgDiv.getId());
    pidcVarCocWp.setDeleted(yOrNToBoolean(wrkPckgDiv.getDeleted()));

    pidcVarCocWp.setName(wrkPckgDiv.getWpName());
    pidcVarCocWp.setDescription(wrkPckgDiv.getWpDesc());
    pidcVarCocWp.setUsedFlag(tPidcVarCocWp.getUsedFlag());
    pidcVarCocWp.setAtChildLevel(yOrNToBoolean(tPidcVarCocWp.getIsAtChildLevel()));

    return pidcVarCocWp;
  }

  /**
   * Method return set of PidcVariantCocWp based on variant id
   * @param varId as Input
   * @return Set<PidcVariantCocWp>
   * @throws DataException as Exception
   */
  public Set<PidcVariantCocWp> getAllPidcVarCocWpByVarId(final Long varId) throws DataException {
    Set<PidcVariantCocWp> pidcVarCocWpSet = new HashSet<>();
    TabvProjectVariant tabvProjectVariant = new PidcVariantLoader(getServiceData()).getEntityObject(varId);
    for (TPidcVariantCocWp tPidcVarCocWp : tabvProjectVariant.gettPidcVarCocWp()) {
      pidcVarCocWpSet.add(createDataObject(tPidcVarCocWp));
    }
    return pidcVarCocWpSet;
  }

}
