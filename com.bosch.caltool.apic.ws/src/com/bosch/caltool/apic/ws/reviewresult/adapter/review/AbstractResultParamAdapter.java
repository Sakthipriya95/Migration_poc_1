/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.reviewresult.adapter.review;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.axis2.databinding.types.HexBinary;

import com.bosch.caltool.apic.ws.db.IWebServiceAdapter;
import com.bosch.caltool.apic.ws.pidc.AbstractPidc;
import com.bosch.caltool.apic.ws.reviewresult.bo.ReviewResultsTypeWsBo;
import com.bosch.caltool.apic.ws.session.Session;
import com.bosch.caltool.icdm.common.exception.IcdmException;


/**
 * @author imi2si
 */
public abstract class AbstractResultParamAdapter extends ReviewResultsTypeWsBo implements IWebServiceAdapter {


  /**
   *
   */
  protected final AbstractPidc pidc;

  /**
   * @param session
   * @param pidc
   */
  public AbstractResultParamAdapter(final Session session, final AbstractPidc pidc) {
    super(session);
    this.pidc = pidc;
  }

  public abstract void adapt() throws IcdmException, ClassNotFoundException, IOException;

  /**
   * @param o
   * @return
   * @throws IOException
   */
  public HexBinary objectToHexBinary(final Object o) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ObjectOutputStream oos;
    oos = new ObjectOutputStream(outputStream);
    oos.writeObject(o);
    return new HexBinary(outputStream.toByteArray());
  }
}
