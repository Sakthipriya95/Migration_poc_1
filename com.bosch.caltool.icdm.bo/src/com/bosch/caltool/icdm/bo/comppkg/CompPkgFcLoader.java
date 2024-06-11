package com.bosch.caltool.icdm.bo.comppkg;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.comppkg.TCompPkgBcFc;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.comppkg.CompPkgFc;


/**
 * Loader class for TCompPkgBcFc
 *
 * @author say8cob
 */
public class CompPkgFcLoader extends AbstractBusinessObject<CompPkgFc, TCompPkgBcFc> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public CompPkgFcLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.COMP_PKG_BC_FC, TCompPkgBcFc.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected CompPkgFc createDataObject(final TCompPkgBcFc entity) throws DataException {
    CompPkgFc object = new CompPkgFc();

    setCommonFields(object, entity);
    object.setId(entity.getCompBcFcId());
    object.setCompBcId(entity.getTCompPkgBc().getCompBcId());
    object.setFcName(entity.getFcName());
    object.setVersion(entity.getVersion());
    return object;
  }

  /**
   * Get Title for Functions Property View
   *
   * @param compBcFcId Long
   * @return String
   * @throws DataException error while retrieving data
   */
  public String getPropertyTitle(final Long compBcFcId) throws DataException {
    String fcName = getDataObjectByID(compBcFcId).getFcName();
    CompPkgBcLoader compPkgBcLoader = new CompPkgBcLoader(getServiceData());
    CompPackageLoader compPkgLoader = new CompPackageLoader(getServiceData());
    Long compPkgBcId = getDataObjectByID(compBcFcId).getCompBcId();
    String bcName = compPkgBcLoader.getDataObjectByID(compPkgBcId).getBcName();
    String compPkgName =
        compPkgLoader.getDataObjectByID(compPkgBcLoader.getDataObjectByID(compPkgBcId).getCompPkgId()).getName();
    return "FUNC : " + fcName + " - BC : " + bcName + " - COMPPKG : " + compPkgName;
  }

  /**
   * Get TCompPkgBcFc records using CompBcId
   *
   * @param compBcId
   * @return Map. Key - id, Value - CompPkgFc object
   * @throws DataException error while retrieving data
   */
  public SortedSet<CompPkgFc> getByCompBcId(final Long compBcId) throws DataException {
    SortedSet<CompPkgFc> compbcfcSet = new TreeSet<>();
    TypedQuery<TCompPkgBcFc> tQuery =
        getEntMgr().createNamedQuery("TCompPkgBcFc.findByBCId", TCompPkgBcFc.class).setParameter("compBcId", compBcId);
    List<TCompPkgBcFc> dbObj = tQuery.getResultList();
    for (TCompPkgBcFc entity : dbObj) {
      compbcfcSet.add(createDataObject(entity));
    }
    return compbcfcSet;
  }


}
