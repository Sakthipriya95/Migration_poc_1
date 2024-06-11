package com.bosch.caltool.icdm.bo.a2l;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lSyscon;
import com.bosch.caltool.icdm.model.a2l.A2lSysconst;


/**
 * Loader class for A2lSyscon
 * 
 * @author pdh2cob
 * 
 */
public class A2lSysconstLoader
    extends AbstractBusinessObject<A2lSysconst, TA2lSyscon>
{
  

  /**
   * @param serviceData serviceData
   */
  public A2lSysconstLoader(final ServiceData serviceData) {
    super(serviceData, "A2lSyscon", TA2lSyscon.class);
  }



    /**
     * {@inheritDoc}
     * 
     */
    @Override
    protected A2lSysconst createDataObject(final TA2lSyscon entity)
        throws DataException
    {
        A2lSysconst object = new A2lSysconst();

        object.setId(entity.getId());
        object.setName(entity.getName());
        object.setValue(entity.getValue());
        object.setModuleId(entity.getModuleId());

        return object;
    }
    
    
    /**
     * @param sysconList from ui
     * @return invalid syscons
     */
    public List<String> getInvalidSystemConstants(List<String> sysconList){
      TypedQuery<TA2lSyscon> tQuery1 = getEntMgr().createNamedQuery(TA2lSyscon.NQ_GET_SYSCON, TA2lSyscon.class);
      
      tQuery1.setParameter("sysconlist", sysconList);
      
      // System constant
      List<?> dbSysConList = tQuery1.getResultList();
      
      List<String> dbSysconNames = new ArrayList<>();
      List<String> invalidSysconList = new ArrayList<>();
      
      for (Object dbSyscon : dbSysConList) {
        if(dbSyscon instanceof String){
        dbSysconNames.add(((String) dbSyscon).trim());
        }
      }
      
      
      for (String syscon : sysconList) {
        if(!dbSysconNames.contains(syscon)){
          invalidSysconList.add(syscon);
        }
        
      }
      
      return invalidSysconList;
    }


}
