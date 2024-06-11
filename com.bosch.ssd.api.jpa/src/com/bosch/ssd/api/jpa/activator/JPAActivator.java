package com.bosch.ssd.api.jpa.activator;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.jpa.PersistenceProvider;


public class JPAActivator {

  public static Map<String, String> props = new HashMap<String, String>();

  public static EntityManagerFactory emf;
  public static EntityManager em;
  public EntityManager emInstance;

  public static String dbpassword;

  public static String dbUrl;

  public static String user;

  /**
   * @return
   */
  private EntityManager getEmInstance() {
    return emInstance;
  }

  /**
   * @param emInstance
   */
  private void setEmInstance(EntityManager emInstance) {
    this.emInstance = emInstance;
  }

  private static Map<String, Object> properties;

  /**
   * The constructor
   */
  public JPAActivator() {}


  /**
   * @throws Exception
   */
  public void stop() throws Exception {

    if (!em.getTransaction().isActive()) {
      em.getTransaction().begin();
    }
    else {
      em.getTransaction().rollback();
      em.getTransaction().begin();
    }

    em.getTransaction().commit();

  }

  /**
   * @return
   */
  public EntityManager createConnection() {
    try {
      user = "SSDTECHUSER";
      dbpassword = "SSDTECHUSER";
      dbUrl =
          "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=rb0orarac18.de.bosch.com)(PORT=38000)))(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME=dgstest_rb0orarac18.de.bosch.com)))";

      props = new HashMap<String, String>();
      props.put("javax.persistence.jdbc.user", user);
      props.put("javax.persistence.jdbc.password", dbpassword);
      props.put("javax.persistence.jdbc.url", dbUrl);

      emf = new PersistenceProvider().createEntityManagerFactory("com.bosch.ssd.api.jpa", props);
      em = emf.createEntityManager();
      setEmInstance(em);

      properties = em.getProperties();

    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return em;
  }

  /**
   * @return
   */
  public EntityManager getConnection() {
    if (em == null) {
      createConnection();
    }
    if (!em.getTransaction().isActive()) {
      em.getTransaction().begin();
    }
    else {
      em.getTransaction().rollback();
      em.getTransaction().begin();
    }

    return em;
  }

}
