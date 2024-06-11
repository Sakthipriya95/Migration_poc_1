/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.testframe;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.logging.log4j.LogManager;
import org.eclipse.persistence.config.PersistenceUnitProperties;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.caltool.testframe.eclipselink.logging.EclipseLinkSessionCustomizer;
import com.bosch.caltool.testframe.exception.InvalidTestDataException;
import com.bosch.caltool.testframe.exception.TestDataException;


/**
 * @author bne4cob
 */
public final class TestUtils {

  /**
   * Length of return array
   */
  private static final int KEYVAL_RETARR_LEN = 2;

  /**
   * Class loader
   */
  private static ClassLoader classLoader;

  /**
   * separator of property value
   */
  private static final String PROPVAL_SEP = "=";

  /**
   * Test Frame logger
   */
  private static ILoggerAdapter tframeLogger;

  /**
   * JPA logger
   */
  private static ILoggerAdapter jpaLogger;

  /**
   * Lock object for thread safety during initialisation
   */
  private static Object initLockObj = new Object();

  /**
   * Private constructor
   */
  private TestUtils() {
    // nothing to do
  }

  /**
   * @param persContxt Persistance context
   * @return EntityManagerFactory
   */
  public static EntityManagerFactory createEMF(final String persContxt) {
    tframeLogger.debug("Creating EntityManagerFactory . . .");

    Map<String, Object> emfProperties = new ConcurrentHashMap<>();
    emfProperties.put(PersistenceUnitProperties.CLASSLOADER, classLoader);
    emfProperties.put("javax.persistence.jdbc.url", CommonProps.DB_SERVER_URL);
    emfProperties.put("javax.persistence.jdbc.password", CommonProps.DB_PASSWORD);
    emfProperties.put("javax.persistence.jdbc.user", CommonProps.DB_USER);
    emfProperties.put("eclipselink.session.customizer", EclipseLinkSessionCustomizer.class.getName());

    return Persistence.createEntityManagerFactory(persContxt, emfProperties);
  }

  /**
   * Intialise the logger
   *
   * @param clas Class from which the method is invoked. To create the file on that class
   */
  public static void createLoggers(final Class<?> clas) {

    synchronized (initLockObj) {
      if (tframeLogger == null) {
        tframeLogger = new Log4JLoggerAdapterImpl(LogManager.getLogger("TFRAME"));
      }

      if (jpaLogger == null) {
        jpaLogger = new Log4JLoggerAdapterImpl(LogManager.getLogger("JPA"));
      }
    }

    tframeLogger.info("----------------Logging started for [{}]-----------------------------------", clas.getName());

  }

  /**
   * Closes the entity manager factory and entity manager
   *
   * @param entMgr entity manager
   * @param emf EntityManagerFactory
   */
  public static void closeJpaResources(final EntityManager entMgr, final EntityManagerFactory emf) {
    if (entMgr != null) {
      entMgr.close();
    }
    if (emf != null) {
      emf.close();
    }
  }

  /**
   * Closes the entity manager factory
   *
   * @param emf EntityManagerFactory
   */
  public static void closeEMF(final EntityManagerFactory emf) {
    if (emf != null) {
      emf.close();
    }
  }

  /**
   * Closes the entity manager
   *
   * @param entMgr EntityManager
   */
  public static void closeEM(final EntityManager entMgr) {
    if (entMgr != null) {
      entMgr.close();
    }
  }

  /**
   * @return the classLoader
   */
  public static ClassLoader getClassLoader() {
    return classLoader;
  }

  /**
   * @param classLoader the classLoader to set
   */
  public static void setClassLoader(final ClassLoader classLoader) {
    TestUtils.classLoader = classLoader;
  }

  /**
   * @param str to check
   * @return true if input is null or empty string
   */
  public static boolean isEmpty(final String str) {
    return ((str == null) || "".equals(str));
  }

  /**
   * Checks whether two objects are equal. This will also consider the input(s) being null.
   *
   * @param <O> Any object
   * @param obj1 first object
   * @param obj2 second object
   * @return true/false
   */
  public static <O> boolean isEqual(final O obj1, final O obj2) {
    if ((obj1 == null) && (obj2 == null)) {
      return true;
    }
    if (obj1 == null) {
      // obj2 is not null now, since first check is not satisfied
      return false;
    }
    return obj1.equals(obj2);

  }

  /**
   * Checks whether two String are equal, case in-sensitive. This will also consider the input(s) being null.
   *
   * @param str1 first String
   * @param str2 second String
   * @return true/false
   */
  public static boolean isEqualIgnoreCase(final String str1, final String str2) {
    if ((str1 == null) && (str2 == null)) {
      return true;
    }
    if (str1 == null) {
      // obj2 is not null now, since first check is not satisfied
      return false;
    }
    return str1.equalsIgnoreCase(str2);

  }

  /**
   * Replaces null string with empty string
   *
   * @param str input string
   * @return empty string if input is null, else input
   */
  public static String checkNull(final String str) {
    return str == null ? "" : str;
  }

  /**
   * @param keyVal keyVal string
   * @return String array : element[0] - key; element[1] - value as text
   * @throws InvalidTestDataException for invalid input text
   */
  public static String[] getKeyAndValue(final String keyVal) throws InvalidTestDataException {
    if (isEmpty(keyVal)) {
      throw new InvalidTestDataException("Key value string cannot be empty");
    }
    String[] retArr = new String[KEYVAL_RETARR_LEN];
    String[] kvArr = keyVal.split(PROPVAL_SEP);
    if (kvArr.length == (KEYVAL_RETARR_LEN - 1)) {// Only one element
      retArr[0] = kvArr[0];
      retArr[1] = "";
    }
    else if (kvArr.length == KEYVAL_RETARR_LEN) {
      System.arraycopy(kvArr, 0, retArr, 0, KEYVAL_RETARR_LEN);
    }
    else {
      throw new InvalidTestDataException("Invalid Key value string pattern - " + keyVal);
    }

    return retArr;


  }

  /**
   * Removes the wrapping at both sides e.g. if input string is {abc}, method returns abc.
   *
   * @param text input
   * @param wrapLeft wrapping string
   * @param wrapRight wrapping string
   * @return unwrapped text
   */
  public static String unwrap(final String text, final String wrapLeft, final String wrapRight) {
    if (isEmpty(text) || (text.length() <= (wrapLeft.length() + wrapRight.length()))) {
      return text;
    }

    if (text.startsWith(wrapLeft) && text.endsWith(wrapRight)) {
      return text.substring(wrapLeft.length(), text.lastIndexOf(wrapRight));
    }

    return text;
  }

  /**
   * Removes the wrapping string from the both sides. e.g. if input string is "abc", method returns abc.
   *
   * @param text input
   * @param wrap wrapping string
   * @return unwrapped text
   */
  public static String unwrap(final String text, final String wrap) {
    return unwrap(text, wrap, wrap);
  }

  /**
   * @return the jpaLogger
   */
  public static ILoggerAdapter getJpaLogger() {
    return jpaLogger;
  }

  /**
   * @return the tframeLogger
   */
  public static ILoggerAdapter getTframeLogger() {
    return tframeLogger;
  }

  /**
   * Returns the field value in the given object
   *
   * @param bean object
   * @param field field name
   * @return value of the field
   * @throws TestDataException exception
   */
  public static Object getPropertyValue(final Object bean, final String field) throws TestDataException {
    Map<String, PropertyDescriptor> propMap = getProperties(bean);
    PropertyDescriptor propDes = propMap.get(field.toUpperCase(Locale.getDefault()));
    if (propDes == null) {
      throw new InvalidTestDataException(
          "The parameter '" + field + "' not found in the entity " + bean.getClass().toString());
    }
    Method getter = propDes.getReadMethod();

    try {
      return getter.invoke(bean);
    }
    catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exp) {
      throw new InvalidTestDataException("Error occured while retrieving field from entity. " + exp.getMessage(), exp);
    }

  }

  /**
   * Get the property descriptors of the bean
   *
   * @param bean bean
   * @return map of property descriptors
   * @throws TestDataException if property descriptors cannot be found
   */
  public static Map<String, PropertyDescriptor> getProperties(final Object bean) throws TestDataException {
    BeanInfo info = null;
    try {
      info = Introspector.getBeanInfo(bean.getClass(), Object.class);
    }
    catch (IntrospectionException e) {
      throw new TestDataException("Property descriptors cannot be found", e);
    }

    Map<String, PropertyDescriptor> propMap = new ConcurrentHashMap<>();

    PropertyDescriptor[] props = info.getPropertyDescriptors();
    for (PropertyDescriptor pd : props) {
      propMap.put(pd.getName().toUpperCase(), pd);
    }

    return propMap;
  }

  /**
   * Sets the property to the object
   *
   * @param propDes PropertyDescriptor of the field to be set
   * @param obj object
   * @param valToSet value to be set
   * @throws TestDataException for errors
   */
  public static void setPropertyValue(final PropertyDescriptor propDes, final Object obj, final Object valToSet)
      throws TestDataException {

    Method setter = propDes.getWriteMethod();
    if (setter == null) {
      throw new TestDataException("This property cannot be set - " + propDes.getName());
    }

    try {
      setter.invoke(obj, valToSet);
    }
    catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exp) {
      throw new TestDataException(CommonProps.DEF_EXPTN_MSG, exp);
    }
  }

  /**
   * Converts the text date in the given format to timestamp object
   *
   * @param txtDate date as string
   * @param format date format
   * @return Timestamp
   * @throws InvalidTestDataException if date string does not match the input format
   */
  public static Timestamp getTimestamp(final String txtDate, final String format) throws InvalidTestDataException {
    Date date;
    try {
      date = new SimpleDateFormat(format, Locale.ENGLISH).parse(txtDate);
    }
    catch (ParseException e) {
      throw new InvalidTestDataException("Invalid date format in test data. Expected format " + format, e);
    }
    Calendar mydate = new GregorianCalendar();
    mydate.setTime(date);
    return new Timestamp(mydate.getTimeInMillis());
  }


}
