/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.statistics.adapter.client;

import java.util.Calendar;

import com.bosch.caltool.apic.ws.Attribute;
import com.bosch.caltool.apic.ws.AttributeValue;
import com.bosch.caltool.apic.ws.GetAllAttributes;
import com.bosch.caltool.apic.ws.GetAllAttributesFaultException;
import com.bosch.caltool.apic.ws.GetAllAttributesResponseType;
import com.bosch.caltool.apic.ws.GetAllProjectIdCards;
import com.bosch.caltool.apic.ws.GetAllProjectIdCardsFaultException;
import com.bosch.caltool.apic.ws.GetAllProjectIdCardsResponseType;
import com.bosch.caltool.apic.ws.GetAttributeValues;
import com.bosch.caltool.apic.ws.GetAttributeValuesResponseType;
import com.bosch.caltool.apic.ws.GetPidcVersionStatisticsFaultException;
import com.bosch.caltool.apic.ws.GetPidcVersionStatisticsResponse;
import com.bosch.caltool.apic.ws.GetProjectIdCard;
import com.bosch.caltool.apic.ws.GetProjectIdCardResponseType;
import com.bosch.caltool.apic.ws.PidcVersionStatisticsReq;
import com.bosch.caltool.apic.ws.ProjectIdCardInfoType;
import com.bosch.caltool.apic.ws.ProjectIdCardType;
import com.bosch.caltool.apic.ws.ValueList;
import com.bosch.caltool.apic.ws.db.ApicWebServiceDBImpl;
import com.bosch.caltool.apic.ws.session.Session;


/**
 * @author imi2si
 */
public class ApicWebServiceClientAdapter {

  private final ApicWebServiceDBImpl apicServer;
  private final String sessionId;

  public ApicWebServiceClientAdapter(final ApicWebServiceDBImpl apicServer, final Session sessionId) {
    this.apicServer = apicServer;
    this.sessionId = sessionId.getSessionId();
  }

  public ProjectIdCardInfoType[] getAllPidc() throws GetAllProjectIdCardsFaultException {
    GetAllProjectIdCards getAllProjectIdCards = new GetAllProjectIdCards();

    getAllProjectIdCards.setSessID(this.sessionId);

    GetAllProjectIdCardsResponseType response;

    response = this.apicServer.getAllProjIdCard(getAllProjectIdCards);


    return response.getProjectIdCards();
  }

  public Calendar getLastConfirmationDate() {
    return null;
  }

  public Attribute[] getAllAttributes() throws GetAllAttributesFaultException {

    GetAllAttributes getAllAttributes = new GetAllAttributes();

    getAllAttributes.setSessID(this.sessionId);

    GetAllAttributesResponseType response = this.apicServer.getAllAttr(getAllAttributes);

    return response.getAttributesList().getAttributes();

  }

  public AttributeValue getAttributeValueByID(final long attributeId, final long valueId) throws Exception {
    ValueList[] values = getAttrValues(new long[] { attributeId });

    for (ValueList valueList : values) {
      for (AttributeValue value : valueList.getValues()) {
        if (value.getValueID() == valueId) {
          return value;
        }
      }
    }

    return null;
  }

  public ValueList[] getAttrValues(final long attrID) throws Exception {
    return getAttrValues(new long[] { attrID });
  }

  /**
   * @param attrID
   * @return
   * @throws Exception
   */
  public ValueList[] getAttrValues(final long[] attrIDs) throws Exception {
    GetAttributeValues getAttributeValues = new GetAttributeValues();

    getAttributeValues.setSessID(this.sessionId);
    getAttributeValues.setAttributeIDs(attrIDs);

    GetAttributeValuesResponseType response = this.apicServer.getAttrValues(getAttributeValues);

    ValueList[] valueLists = response.getValueLists();

    // To avoid returning null as AttributeValue array
    for (ValueList valueList : valueLists) {
      if (valueList.getValues() == null) {
        valueList.setValues(new AttributeValue[0]);
      }
    }

    return valueLists;
  }

  public ProjectIdCardType getPidcDetails(final Long pidcID) throws Exception {
    GetProjectIdCard getProjectIdCard = new GetProjectIdCard();

    getProjectIdCard.setSessID(this.sessionId);
    getProjectIdCard.setProjectIdCardID(pidcID.longValue());

    GetProjectIdCardResponseType response = this.apicServer.getProjIdCard(getProjectIdCard);

    return response.getProjectIdCard();
  }

  /**
   * @return
   */
  public String getPidcOwnerMail() {
    StringBuilder pidcOwner = new StringBuilder();

    // Also check made for string Index out of bounds.
    return (pidcOwner.length() == 0) ? "" : pidcOwner.substring(0, pidcOwner.lastIndexOf(";"));
  }

  public GetPidcVersionStatisticsResponse getPidcHeaderStatistics(final Long pidcId) {
    PidcVersionStatisticsReq req = new PidcVersionStatisticsReq();
    req.setSessID(this.sessionId);
    req.setPidcID(pidcId);
    req.setType("pidc");

    try {
      GetPidcVersionStatisticsResponse pIdcVersionStatistics = this.apicServer.getPIdcVersionStatistics(req);
      return pIdcVersionStatistics;
    }
    catch (GetPidcVersionStatisticsFaultException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
