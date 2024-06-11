package com.bosch.caltool.icdm.ws.rest.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.ws.rs.client.Client;

import com.bosch.caltool.icdm.ws.rest.client.util.Utils;

/**
 * Provides the Jersey client instance for iCDM services
 *
 * @author bne4cob
 */
final class IcdmRestClientProvider {

  /**
   * Stores the thread local against user name. One thread local per user name
   */
  private static final ConcurrentMap<String, IcdmRestClientThreadLocal> thLclMap = new ConcurrentHashMap<>();

  /**
   * Private constructor, to avoid creating instances
   */
  private IcdmRestClientProvider() {
    // Private constructor
  }

  /**
   * Get the jersey client for the given configuration
   * 
   * @param config client configuration
   * @return jersey client
   */
  public static Client getClient(final ClientConfiguration config) {
    String user = Utils.isEmptyString(config.getUserName()) ? "DEFAULT-ICDM-THCL" : config.getUserName();
    IcdmRestClientThreadLocal thLcl =
        thLclMap.computeIfAbsent(user, key -> new IcdmRestClientThreadLocal(user, config.getPassword()));

    return thLcl.get();
  }

}