/**
 * 
 */
package com.bosch.caltool.a2l.jpa;


/**
 * @author dmo5cob
 */
public interface A2lJPAConstants {

  /**
   * Query Hints
   */
  public static final String READ_ONLY = "eclipselink.read-only";

  public static final String FETCH_SIZE = "eclipselink.jdbc.fetch-size";

  public static final String STORE_MODE = "javax.persistence.cache.storeMode";

  public static final String SHARED_CACHE = "eclipselink.cache.shared.default";

}
