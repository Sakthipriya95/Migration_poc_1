/**********************************************************
 * Copyright (c) 2009, Robert Bosch GmbH All rights reserved.
 ***********************************************************/
package com.bosch.calcomp.hexparser.model.handler;

import java.math.BigInteger;

import com.bosch.calcomp.calutil.tools.A2LUtil;
import com.bosch.calcomp.calutil.tools.CalUtil;
import com.bosch.calcomp.hexparser.exception.HexParserException;
import com.bosch.calcomp.hexparser.model.HexMemory;
import com.bosch.calcomp.hexparser.model.HexSegment;

/**
 * @author dec1kor
 * 
 *         <pre>
 * Version  Date            Modified by         Changes
 * ----------------------------------------------------------------------------
 * 0.1      15-Jul-09       Deepa               HEXP-1: First draft.<br>
 *         </pre>
 */

/**
 * This class must be accessed by the other components to get the hex data for the given addresses.
 * 
 * @author dec1kor
 */
public class HexParserDataHandler {

  private HexMemory hexMemory = null;

  // the last used HEX segment
  private HexSegment cachedHexSegment;

  // current address to be read
  private long address;

  // byte order in HEX
  private byte byteOrder;

  // byte alignment
  private byte byteAlignment;

  // word alignment
  private byte wordAlignment;

  // long alignment
  private byte longAlignment;

  // float 32 alignment
  private byte float32Alignment;

  // float 64 alignment
  private byte float64Alignment;

  /**
   * Constructor with the HexMemory.
   * 
   * @param hexMemory HexMemory
   */
  public HexParserDataHandler(HexMemory hexMemory) {
    this.hexMemory = hexMemory;
    this.cachedHexSegment = null;
  }

  /**
   * Get the current address to be read
   * 
   * @return the address
   */
  public long getAddress() {
    return address;
  }

  /**
   * Set the next address to be read
   * 
   * @param address the address
   */
  public void setAddress(long address) {
    this.address = address;
  }

  /**
   * Get the current byte order
   * 
   * @return teh byte order
   */
  public byte getByteOrder() {
    return byteOrder;
  }

  /**
   * Set the byte order in the HEX file
   * 
   * @param byteOrder the byte order
   */
  public void setByteOrder(byte byteOrder) {
    this.byteOrder = byteOrder;
  }

  /**
   * Get one byte at the current address. The address pointer will be incremented.
   * 
   * @return the byte
   */
  public short getByte() {
    short theByte = getByteAt(address);
    address++;

    return theByte;
  }

  /**
   * Get one byte at the given address. This will not modify the internal address pointer. The byte can never be
   * negative. Since JAVA did not support unsgined datatypes, the sign will be calculated in getValueAt or getFloatAt.
   * 
   * @param address
   * @return the byte
   */
  public short getByteAt(long address) {
    HexSegment hexSegment;
    int offset;
    short byteAt = -1;

    hexSegment = null;

    if ((cachedHexSegment != null) &&
        ((address >= cachedHexSegment.getStartAddress()) && (address <= cachedHexSegment.getEndAddress()))) {
      offset = (int) (address - cachedHexSegment.getStartAddress());
      byteAt = cachedHexSegment.getByteAt(offset);
    }
    else {
      if (hexMemory != null) {
        for (int i = 0; i < hexMemory.getSegmentList().size(); i++) {
          hexSegment = (HexSegment) hexMemory.getSegmentList().get(i);
          if ((address >= hexSegment.getStartAddress()) && (address <= hexSegment.getEndAddress())) {
            cachedHexSegment = hexSegment;
            offset = (int) (address - hexSegment.getStartAddress());
            byteAt = hexSegment.getByteAt(offset);
            break;
          }
        }
      }
    }

    if (byteAt < 0) {
      throw new HexParserException("byte could not be read at address: " + CalUtil.toHex(address, 8),
          HexParserException.BYTE_COULD_NOT_BE_READ);

    }
    else {
      return byteAt;
    }
  }


  /**
   * Get one byte at the given HEX address. This will not modify the internal address pointer.
   * 
   * @param hexAddress
   * @return the byte
   */
  public short getByteAt(String hexAddress) {
    long address;

    address = Long.parseLong(hexAddress, 16);
    return getByteAt(address);
  }

  /**
   * Get one HEX byte at the current address. This will increment the internal address pointer.
   * 
   * @return the byte in hexadecimal
   */
  public String getHexByte() {
    String theByte = getHexByteAt(address);
    address++;

    return theByte;
  }

  /**
   * Get one HEX byte ar the given address. This will not modify the internal address pointer.
   * 
   * @param address
   * @return the byte in hexadecimal
   */
  public String getHexByteAt(long address) {
    return CalUtil.toHex(getByteAt(address), 2);
  }

  /**
   * Get one HEX byte at the given HEX address. This will not modify the internal address pointer.
   * 
   * @param hexAddress
   * @return the byte in hexadecimal
   */
  public String getHexByteAt(String hexAddress) {
    return CalUtil.toHex(getByteAt(hexAddress), 2);
  }

  /**
   * Retrieve the int value at the given address for the given datatype.
   * 
   * @param address
   * @param dataType
   * @return long
   */
  private long getIntValueAt(long address, byte dataType) {
    short byteValue = 0;
    long intValue = 0;
    int i;

    if (byteOrder == A2LUtil.MSB_FIRST) {
      for (i = 0; i < A2LUtil.getSize(dataType); i++) {
        byteValue = getByteAt(address + i);
        intValue = (intValue * 256) + byteValue;
      }
    }
    else {
      for (i = A2LUtil.getSize(dataType) - 1; i >= 0; i--) {
        byteValue = getByteAt(address + i);
        intValue = (intValue * 256) + byteValue;
      }
    }
    return intValue;
  }

  /**
   * Retrieve the int value at the given address for the given datatype.
   * 
   * @param address
   * @param dataType
   * @return long
   */
  private BigInteger getIntValueAtUint64(long address, byte dataType) {

    BigInteger intValue = BigInteger.ZERO;
    int i;
    String builder = "";
    if (byteOrder == A2LUtil.MSB_FIRST) {
      for (i = 0; i < A2LUtil.getSize(dataType); i++) {
        builder = getHexValueForByte(address, i, builder);

      }
    }
    else {
      for (i = A2LUtil.getSize(dataType) - 1; i >= 0; i--) {
        builder = getHexValueForByte(address, i, builder);
      }
      if (!builder.isEmpty()) {
        intValue = new BigInteger(builder, 16);
      }

    }
    return intValue;
  }

  /**
   * @param address
   * @param i
   * @param builder
   * @return
   */
  private String getHexValueForByte(long address, int i, String builder) {
    short byteValue;
    byteValue = getByteAt(address + i);
    String str = Integer.toHexString(byteValue);
    if (str.length() == 1) {
      str = "0".concat(str);
    }
    return builder.concat(str);

  }


  /**
   * Retrieves the value from the current address location for the given data type.
   * 
   * @param dataType
   * @return long
   */
  public BigInteger getValue(byte dataType) {
    BigInteger theValue = getValueAt(address, dataType);
    address = address + A2LUtil.getSize(dataType);
    return theValue;
  }

  /**
   * Retrieves the value from the given address location for the given data type.
   * 
   * @param address
   * @param dataType
   * @return long
   */
  private BigInteger getValueAt(long address, byte dataType) {
    long intValue = 0;
    BigInteger finalIntValue = BigInteger.ZERO;
    long tempInt = 0;
    if (A2LUtil.UINT64 != dataType) {

      intValue = getIntValueAt(address, dataType);

      // IF Else interchanged for HEXP-11
      if (dataType == A2LUtil.FLOAT32_IEEE || dataType == A2LUtil.FLOAT64_IEEE) {
        // not a int datatype
        intValue = 0;
      }
      else if (A2LUtil.isSigned(dataType)) {
        tempInt = intValue >> ((A2LUtil.getSize(dataType) * 8) - 1);
        if (tempInt == 1) {
          // value is negative
          intValue = (~intValue) + 1;
          intValue = intValue & (long) (Math.pow(2, (A2LUtil.getSize(dataType) * 8)) - 1);
          intValue *= -1;
        }
      }
      finalIntValue = BigInteger.valueOf(intValue);
    }
    else {
      finalIntValue = getIntValueAtUint64(address, dataType);
    }

    return finalIntValue;
  }

  /**
   * Retrieves the float value from the current address for the given datatype.
   * 
   * @param dataType
   * @return float value
   */
  public double getFloatValue(byte dataType) {
    double theValue = getFloatValueAt(address, dataType);
    address = address + A2LUtil.getSize(dataType);

    return theValue;
  }

  /**
   * Retrieves the float value at the given address for the given datatype.
   * 
   * @param address
   * @param dataType
   * @return float value
   */
  private double getFloatValueAt(long address, byte dataType) {
    long intValue = 0;
    byte significantBits;
    byte exponentBits;
    int exponentAdj;
    double significant;
    int exponent;
    byte sign;
    int i;
    byte bit;
    double floatValue;

    intValue = getIntValueAt(address, dataType);

    if (dataType == A2LUtil.FLOAT32_IEEE || dataType == A2LUtil.FLOAT64_IEEE) {
      if (dataType == A2LUtil.FLOAT32_IEEE) {
        significantBits = 23;
        exponentBits = 8;
        exponentAdj = 127;
      }
      else {
        significantBits = 52;
        exponentBits = 11;
        exponentAdj = 1023;
      }
      significant = 0;
      for (i = 0; i < significantBits; i++) {
        bit = (byte) (intValue & 1);
        significant += bit / Math.pow(2, (significantBits - i));
        intValue >>= 1;
      }
      exponent = 0;
      for (i = 0; i < exponentBits; i++) {
        bit = (byte) (intValue & 1);
        exponent += bit * Math.pow(2, i);
        intValue >>= 1;
      }
      sign = (byte) (intValue & 1);
      if ((significant == 0) && (exponent == 0)) {
        floatValue = 0;
      }
      else {
        floatValue = Math.pow((-1), sign) * Math.pow(2, (exponent - exponentAdj)) * (1 + significant);
      }
    }
    else {
      // not a float datatype
      floatValue = 0;
    }

    return floatValue;
  }

  /**
   * Retrieves the value from the given hexadecimal address for the given datatype.
   * 
   * @param hexAddress
   * @param dataType
   * @return long
   */
  public BigInteger getValueAt(String hexAddress, byte dataType) {

    return getValueAt(Long.parseLong(hexAddress, 16), dataType);
  }

  /**
   * Retrieves the float value from the given hexadecimal address for the given datatype.
   * 
   * @param hexAddress
   * @param dataType
   * @return float value
   */
  public double getFloatValueAt(String hexAddress, byte dataType) {
    return getFloatValueAt(Long.parseLong(hexAddress, 16), dataType);
  }

  /**
   * Align the internal address pointer according to the given data type and the current alignment settings.
   * 
   * @param dataType
   */
  public void alignAddress(byte dataType) {
    long alignment;
    long mismatch;

    // get the alignement depending on the datatype
    switch (dataType) {
      case A2LUtil.UBYTE:
      case A2LUtil.SBYTE:
      case A2LUtil.BYTE:
        alignment = byteAlignment;
        break;

      case A2LUtil.UWORD:
      case A2LUtil.SWORD:
      case A2LUtil.WORD:
        alignment = wordAlignment;
        break;

      case A2LUtil.ULONG:
      case A2LUtil.SLONG:
      case A2LUtil.LONG:
        alignment = longAlignment;
        break;

      case A2LUtil.FLOAT32_IEEE:
        alignment = float32Alignment;
        break;

      case A2LUtil.FLOAT64_IEEE:
        alignment = float64Alignment;
        break;

      default:
        throw new HexParserException("*** ERROR ***: AlignAddress not supported for dataType " + dataType,
            HexParserException.UNEXPECTED_ERROR);
    }

    mismatch = (address % alignment);

    if (mismatch != 0) {
      // address must be aligned
      address += alignment - mismatch;
    }
  }

  /**
   * Get the byteAlignment.
   * 
   * @return byteAlignment
   */
  public byte getByteAlignment() {
    return byteAlignment;
  }

  /**
   * Sets the byte alignment.
   * 
   * @param byteAlignment
   */
  public void setByteAlignment(byte byteAlignment) {
    this.byteAlignment = byteAlignment;
  }

  /**
   * Get the float32Alignment.
   * 
   * @return float32Alignment
   */
  public byte getFloat32Alignment() {
    return float32Alignment;
  }

  /**
   * Set the float32Alignment.
   * 
   * @param float32Alignment
   */
  public void setFloat32Alignment(byte float32Alignment) {
    this.float32Alignment = float32Alignment;
  }

  /**
   * Get float64Alignment
   * 
   * @return float64Alignment
   */
  public byte getFloat64Alignment() {
    return float64Alignment;
  }

  /**
   * Set float64Alignment.
   * 
   * @param float64Alignment
   */
  public void setFloat64Alignment(byte float64Alignment) {
    this.float64Alignment = float64Alignment;
  }

  /**
   * Get the longAlignment.
   * 
   * @return longAlignment
   */
  public byte getLongAlignment() {
    return longAlignment;
  }

  /**
   * Set the longAlignment.
   * 
   * @param longAlignment
   */
  public void setLongAlignment(byte longAlignment) {
    this.longAlignment = longAlignment;
  }

  /**
   * Get the word alignment.
   * 
   * @return wordAlignment
   */
  public byte getWordAlignment() {
    return wordAlignment;
  }

  /**
   * Set the wordAlignment
   * 
   * @param wordAlignment
   */
  public void setWordAlignment(byte wordAlignment) {
    this.wordAlignment = wordAlignment;
  }


}
