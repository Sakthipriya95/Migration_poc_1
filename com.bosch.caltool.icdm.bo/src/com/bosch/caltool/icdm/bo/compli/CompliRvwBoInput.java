/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.compli;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.icdm.model.cdr.CompliReviewInputMetaData;

/**
 * @author apj4cob
 */
public class CompliRvwBoInput {

  final String a2lFileName;
  final Long a2lFileId;
  final CompliReviewInputMetaData srvInput;
  final CompliDataReview dataReview;
  final ConcurrentMap<String, InputStream> hexFileInputStreamMap;
  final Map<String, byte[]> hexByteMapCheckSum;
  final byte[] a2lBytes;

  /**
   * @param a2lFileName a2l File name
   * @param a2lFileId a2l file id
   * @param srvInput service input-meta data
   * @param dataReview CompliDataReview
   * @param hexFileInputStreamMap hexFileInputStreamMap
   * @param hexByteMapCheckSum hexByteCheckSum Map
   * @param a2lBytes a2l bytes
   */
  public CompliRvwBoInput(final String a2lFileName, final Long a2lFileId, final CompliReviewInputMetaData srvInput,
      final CompliDataReview dataReview, final ConcurrentMap<String, InputStream> hexFileInputStreamMap,
      final Map<String, byte[]> hexByteMapCheckSum, final byte[] a2lBytes) {
    this.a2lFileName = a2lFileName;
    this.a2lFileId = a2lFileId;
    this.a2lBytes = a2lBytes;
    this.hexByteMapCheckSum = hexByteMapCheckSum;
    this.hexFileInputStreamMap = hexFileInputStreamMap;
    this.srvInput = srvInput;
    this.dataReview = dataReview;

  }

  /**
   * @return the a2lFileName
   */
  public String getA2lFileName() {
    return this.a2lFileName;
  }

  /**
   * @return the a2lFileId
   */
  public Long getA2lFileId() {
    return this.a2lFileId;
  }

  /**
   * @return the srvInput
   */
  public CompliReviewInputMetaData getSrvInput() {
    return this.srvInput;
  }

  /**
   * @return the dataReview
   */
  public CompliDataReview getDataReview() {
    return this.dataReview;
  }

  /**
   * @return the hexFileInputStreamMap
   */
  public ConcurrentMap<String, InputStream> getHexFileInputStreamMap() {
    return this.hexFileInputStreamMap;
  }

  /**
   * @return the hexByteMapCheckSum
   */
  public Map<String, byte[]> getHexByteMapCheckSum() {
    return this.hexByteMapCheckSum;
  }

  /**
   * @return the a2lBytes
   */
  public byte[] getA2lBytes() {
    return this.a2lBytes;
  }


}
