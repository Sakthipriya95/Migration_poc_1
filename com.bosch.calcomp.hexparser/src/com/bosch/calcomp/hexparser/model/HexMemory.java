package com.bosch.calcomp.hexparser.model;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.bosch.calcomp.hexparser.exception.HexParserException;

/**
 * Revision History<br>
 * Version Date Name Description<br>
 * 0.1 unknown Frank Henze First draft.<br>
 * 0.2 15-Jul-09 Deepa HEXP-1: added getSegmentList()<br>
 */

/**
 * To store the hexdata parsed by the HexParser.
 * <p>
 * Consists of 0..* HexSegment records which are stored in segmentList. And consists of 0..* HexBlock records which are
 * stored in blockList.
 * 
 * @author DS/SAE2-Henze
 * @version 1.0.0
 */
public class HexMemory {

  // current HEX segment number for getFirst/getNextHexSegment
  private int hexSegmentNo;

  // current HEX block number for getFirst/getNextHexBlock
  private int hexBlockNo;

  // the HEX segments list
  private final LinkedList<HexSegment> segmentList;

  // the HEX blocks list
  private final LinkedList<HexBlock> blockList;


  /**
   * Creates a HexMemory instance with an empty segmentList and an empty blockList.
   */
  public HexMemory() {
    segmentList = new LinkedList();
    blockList = new LinkedList();
  }

  /**
   * Retrieves the first HexSegment record from the segmentList.
   * 
   * @return HexSegment
   */
  public HexSegment getFirstHexSegment() {
    hexSegmentNo = 0;
    if (segmentList.isEmpty()) {
      return null;
    }
    return (HexSegment) segmentList.get(hexSegmentNo);

  }

  /**
   * Retrieves the next HexSegment record from the segmentList.
   * 
   * @return HexSegment
   */
  public HexSegment getNextHexSegment() {
    hexSegmentNo++;
    if (hexSegmentNo < segmentList.size()) {
      return (HexSegment) segmentList.get(hexSegmentNo);
    }

    hexSegmentNo = -1;
    return null;

  }

  /**
   * Retrieves the first HexBlock record from the blockList.
   * 
   * @return HexBlock
   */
  public HexBlock getFirstHexBlock() {
    hexBlockNo = 0;
    HexBlock hexBlock;

    try {
      hexBlock = (HexBlock) blockList.getFirst();
    }
    catch (NoSuchElementException e) {
      throw new HexParserException("HEX memory is empty! " + e, HexParserException.NO_DATA_IN_HEX, e);
    }

    return hexBlock;
  }

  /**
   * Retrieves the next HexBlock record from the blockList.
   * 
   * @return HexBlock
   */
  public HexBlock getNextHexBlock() {
    hexBlockNo++;
    if (hexBlockNo < blockList.size()) {
      return (HexBlock) blockList.get(hexBlockNo);
    }
    hexBlockNo = -1;
    return null;
  }

  /**
   * Gets the hex segment list.
   * 
   * @return LinkedList of hex segments
   */
  public LinkedList<HexSegment> getSegmentList() {
    return segmentList;
  }

  /**
   * Gets the hex block list.
   * 
   * @return LinkedList of hex blocks
   */
  public LinkedList<HexBlock> getBlockList() {
    return blockList;
  }

}