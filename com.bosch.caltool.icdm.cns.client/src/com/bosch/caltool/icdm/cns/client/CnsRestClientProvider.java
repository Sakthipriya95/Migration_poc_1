package com.bosch.caltool.icdm.cns.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.ws.rs.client.Client;

import com.bosch.caltool.icdm.cns.client.utils.Utils;

final class CnsRestClientProvider {

  private static final ConcurrentMap<String, CnsRestClientThreadLocal> thLclMap = new ConcurrentHashMap<>();

  private CnsRestClientProvider() {
    // Private constructor
  }

  public static Client getClient(final CnsClientConfiguration config) {
    String user = Utils.isNullOrEmpty(config.getUser()) ? "DEFAULT-THCL" : config.getUser();
    CnsRestClientThreadLocal thLcl =
        thLclMap.computeIfAbsent(user, key -> new CnsRestClientThreadLocal(user, config.getPassword()));

    return thLcl.get();
  }

}