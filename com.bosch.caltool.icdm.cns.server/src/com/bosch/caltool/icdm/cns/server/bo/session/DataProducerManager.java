/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.bo.session;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import com.bosch.caltool.icdm.cns.common.model.ProducerInfo;
import com.bosch.caltool.icdm.cns.server.bo.CnsServiceData;
import com.bosch.caltool.icdm.cns.server.bo.events.ChangeEventManager;
import com.bosch.caltool.icdm.cns.server.exception.CnsServiceException;
import com.bosch.caltool.icdm.cns.server.utils.Utils;

/**
 * @author bne4cob
 */
public enum DataProducerManager {
                                 /**
                                  * Unique instance
                                  */
                                 INSTANCE;

  /**
   * Key - Producer ID<br>
   * Value - Data Producer
   */
  private final ConcurrentMap<String, DataProducer> producerMap = new ConcurrentHashMap<>();

  /**
   * Key - [IP Address] + : + [port]<br>
   * Value - Producer ID
   */
  private final ConcurrentMap<String, String> producerUrlByIdMap = new ConcurrentHashMap<>();

  /**
   * @param ipAddress IP Address
   * @param port port number
   * @param dataSize Data Size
   * @return Producer ID
   */
  public String getOrCreate(final String ipAddress, final int port, final long dataSize) {

    String producerId = this.producerUrlByIdMap.computeIfAbsent(ipAddress + ":" + port, k -> create(ipAddress, port));
    DataProducer producer = this.producerMap.get(producerId);
    producer.addDataSize(dataSize);
    producer.updateLastContactAt();

    return producer.getProducerId();
  }

  /**
   * Create a data producer
   *
   * @param ipAddress IP Address
   * @param port port number
   * @return Producer ID
   */
  private String create(final String ipAddress, final int port) {
    DataProducer p = new DataProducer(ipAddress, port);
    this.producerMap.put(p.getProducerId(), p);
    return p.getProducerId();
  }

  /**
   * @param serviceData CNS Service Data
   * @return list of data producers
   */
  public List<ProducerInfo> getAllProducers(final CnsServiceData serviceData) {
    Comparator<DataProducer> proComp = (p1, p2) -> {
      int res = p1.getIpAddress().compareTo(p2.getIpAddress());
      if (res == 0) {
        res = Integer.compare(p1.getPort(), p2.getPort());
      }
      if (res == 0) {
        res = p1.getProducerId().compareTo(p2.getProducerId());
      }
      return res;
    };

    return this.producerMap.values().stream().sorted(proComp).map(p -> toProducerInfo(p, serviceData))
        .collect(Collectors.toList());

  }

  /**
   * @param prodIdSet set of producer IDs
   * @param serviceData service data
   * @return Map of producer info
   */
  public Map<String, ProducerInfo> getProducerInfoMap(final Set<String> prodIdSet, final CnsServiceData serviceData) {
    return prodIdSet.stream().map(this.producerMap::get).filter(Objects::nonNull)
        .map(p -> toProducerInfo(p, serviceData)).collect(Collectors.toMap(ProducerInfo::getProducerId, p -> p));
  }


  private ProducerInfo toProducerInfo(final DataProducer producer, final CnsServiceData serviceData) {

    ProducerInfo ret = new ProducerInfo();

    ret.setProducerId(producer.getProducerId());
    ret.setIpAddress(producer.getIpAddress());
    ret.setLastContactAt(Utils.instantToString(producer.getLastContactAt(), serviceData.getTimeZoneId()));
    ret.setPort(producer.getPort());
    ret.setTotalDataSize(producer.getTotalDataSize());
    ret.setEventCount(ChangeEventManager.INSTANCE.getEventCountProducer(producer.getProducerId()));

    return ret;
  }

  /**
   * @return total number of data producers
   */
  public int getTotalProducersCount() {
    return this.producerMap.size();
  }

  /**
   * Get the Producer info of the given Producer ID
   *
   * @param serviceData Cns Service Data
   * @param producerId Data Producer ID
   * @return Event Info
   * @throws CnsServiceException if Producer ID is invalid
   */
  public ProducerInfo getProducerInfo(final CnsServiceData serviceData, final String producerId)
      throws CnsServiceException {
    if (producerId == null) {
      throw new CnsServiceException("CNS-4000", "Producer ID cannot be null");
    }
    DataProducer producer = this.producerMap.get(producerId);

    if (producer == null) {
      throw new CnsServiceException("CNS-4001", "Invalid Producer ID " + producerId);
    }

    return toProducerInfo(producer, serviceData);
  }

}
