package com.bosch.caltool.icdm.bo.comppkg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.bc.SdomBcLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.comppkg.TCompPkgBc;
import com.bosch.caltool.icdm.database.entity.comppkg.TCompPkgBcFc;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.bc.SdomBc;
import com.bosch.caltool.icdm.model.comppkg.CompPkgBc;
import com.bosch.caltool.icdm.model.comppkg.CompPkgFc;


/**
 * Loader class for TCompPkgBc
 *
 * @author say8cob
 */
public class CompPkgBcLoader extends AbstractBusinessObject<CompPkgBc, TCompPkgBc> {


  private Map<String, SdomBc> bcMap = new HashMap<>();

  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public CompPkgBcLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.COMP_PKG_BC, TCompPkgBc.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected CompPkgBc createDataObject(final TCompPkgBc entity) throws DataException {
    CompPkgBc bcObj = new CompPkgBc();
    SortedSet<CompPkgFc> fcList = new TreeSet<>();
    setCommonFields(bcObj, entity);
    bcObj.setId(entity.getCompBcId());
    bcObj.setCompPkgId(entity.getTCompPkg().getCompPkgId());
    bcObj.setBcName(entity.getBcName());
    bcObj.setBcSeqNo(entity.getBcSeqNo());
    CompPkgFcLoader compPkgFcLoader = new CompPkgFcLoader(getServiceData());
    for (TCompPkgBcFc fc : entity.getTCompPkgBcFcs()) {
      fcList.add(compPkgFcLoader.getDataObjectByID(fc.getCompBcFcId()));
    }
    if (this.bcMap.containsKey(entity.getBcName())) {
      bcObj.setDescription(this.bcMap.get(entity.getBcName()).getDescription());
    }
    bcObj.setVersion(entity.getVersion());

    bcObj.setFcList(fcList);
    return bcObj;
  }


  /**
   * Get all TCompPkgBc records in system based on the ComponentPackageId
   *
   * @param compPkgId object's ID
   * @return Map. Key - id, Value - CompPkgBc object
   * @throws DataException error while retrieving data
   */
  public SortedSet<CompPkgBc> getBCByCompId(final Long compPkgId) throws DataException {
    SdomBcLoader sdomBcLoader = new SdomBcLoader(getServiceData());
    this.bcMap = sdomBcLoader.getMapofDistinctBcs();

    SortedSet<CompPkgBc> compbcset = new TreeSet<>();
    TypedQuery<TCompPkgBc> tQuery = getServiceData().getEntMgr()
        .createNamedQuery("TCompPkgBc.findByCompId", TCompPkgBc.class).setParameter("compPkgId", compPkgId);
    List<TCompPkgBc> dbObj = tQuery.getResultList();
    for (TCompPkgBc entity : dbObj) {
      compbcset.add(createDataObject(entity));
    }
    return compbcset;
  }


}
