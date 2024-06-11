package com.bosch.ssd.api.jpa.customeditor;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

/**
 *
 *
 */
public class DateConverter implements Converter {

  private static final long serialVersionUID = 1L;

  @Override
  public Object convertDataValueToObjectValue(final Object arg0, final Session arg1) {
    if (arg0 == null) {
      return null;
    }
    Timestamp time = (Timestamp) arg0;
    long milliseconds = time.getTime() + (time.getNanos() / 1000000);
    Date date = new java.sql.Date(milliseconds);
    SimpleDateFormat sdate = new SimpleDateFormat("dd-MMM-yyyy");

    return sdate.format(date);
  }

  @Override
  public Object convertObjectValueToDataValue(final Object arg0, final Session arg1) {
    return new Timestamp(new java.util.Date().getTime());
  }

  @Override
  public void initialize(final DatabaseMapping arg0, final Session arg1) {
// default initialize
  }

  @Override
  public boolean isMutable() {
    return false;
  }

}
