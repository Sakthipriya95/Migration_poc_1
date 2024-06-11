package com.bosch.caltool.icdm.bo.apic;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TWebFlowElement;
import com.bosch.caltool.icdm.model.apic.WebflowElement;


/**
 * Loader class for Webflow Element
 *
 * @author dja7cob
 */
public class WebflowElementLoader extends AbstractBusinessObject<WebflowElement, TWebFlowElement> {

  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public WebflowElementLoader(final ServiceData serviceData) {
    super(serviceData, "TWebFlowElement", TWebFlowElement.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected WebflowElement createDataObject(final TWebFlowElement entity) throws DataException {
    WebflowElement object = new WebflowElement();

    object.setElementId(entity.getElementID());
    object.setVariantId(entity.getVariantID());
    object.setIsDeleted("Y".equals(entity.getIsDeleted()));
    setCommonFields(object, entity);

    return object;
  }

  /**
   * @param elementID
   * @return
   * @throws DataException
   */
  public List<WebflowElement> getWebFlowElements(final long elementID) throws DataException {
    TypedQuery<TWebFlowElement> tQuery =
        getEntMgr().createNamedQuery(TWebFlowElement.NQ_GET_VAR_BY_ELE_ID, TWebFlowElement.class);
    tQuery.setParameter("elementId", elementID);
    List<WebflowElement> webFlowEleList = new ArrayList<>();
    for (TWebFlowElement entity : tQuery.getResultList()) {
      webFlowEleList.add(createDataObject(entity));
    }

    return webFlowEleList;
  }

  /**
   * @param elementID
   * @return
   * @throws DataException
   */
  public List<TWebFlowElement> getTWebFlowElement(final long elementID) throws DataException {
    TypedQuery<TWebFlowElement> tQuery =
        getEntMgr().createNamedQuery(TWebFlowElement.NQ_GET_VAR_BY_ELE_ID, TWebFlowElement.class);
    tQuery.setParameter("elementId", elementID);
    return tQuery.getResultList();
  }
}
