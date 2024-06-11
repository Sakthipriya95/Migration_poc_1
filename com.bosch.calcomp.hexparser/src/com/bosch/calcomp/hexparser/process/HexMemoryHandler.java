/**********************************************************
 * Copyright (c) 2009, Robert Bosch GmbH All rights reserved.
 ***********************************************************/
package com.bosch.calcomp.hexparser.process;

import java.util.LinkedList;

import com.bosch.calcomp.hexparser.model.HexBlock;
import com.bosch.calcomp.hexparser.model.HexMemory;
import com.bosch.calcomp.hexparser.model.HexSegment;

/**
 * @author dec1kor
 * 
 *         <pre>
 * Version 	Date			Modified by			Changes
 * ----------------------------------------------------------------------------
 * 0.1		09-Sep-2009		Deepa				HEXP-1: First draft.<br>
 *         </pre>
 */
/**
 * A handler class which handles the hex data and builds it in HexMemory.
 * 
 * @author dec1kor
 */
public class HexMemoryHandler {

  /**
   * HexMemory instance.
   */
  private HexMemory hexMemory = null;

  /**
   * Constructor.
   * 
   * @param hexMemory HexMemory
   */
  public HexMemoryHandler(HexMemory hexMemory) {
    this.hexMemory = hexMemory;
  }

  /**
   * Add data to the memory
   * 
   * @param binData      the data array
   * @param startAddress the start address of the array
   * @param length       the number of bytes in the array
   */
  public void addData(short[] binData, long startAddress, int length) {
    HexSegment newHexSegment = new HexSegment(binData, startAddress, length);
    int segmentListSize;
    LinkedList<HexSegment> segmentList = hexMemory.getSegmentList();

    if (segmentList.isEmpty()) {
      segmentList.add(newHexSegment);
    }
    else {
      // find the insert position
      segmentListSize = segmentList.size();
      for (int i = 0; i < segmentListSize; i++) {
        if (((HexSegment) segmentList.get(i)).getStartAddress() > startAddress) {
          // insert segment before
          segmentList.add(i, newHexSegment);
          break;
        }
        else if (i == (segmentListSize - 1)) {
          // end of list, append at the end
          segmentList.add(newHexSegment);
        }
      }
    }
    genBlockList();
  }

  /**
   * Generates the list of blocks.
   * <p>
   * A hex block is considered to be a continous sequence of hex segments without any memory gap. If two hex segments
   * are placed in the memory one after the other, it is considered to be a single hex block. <br>
   * If there is a gap between two hex segments, then its considered to be two hex blocks.
   */
  private void genBlockList() {
    long segmentStartAddress;
    long blockStartAddress = -1;
    long blockSize = -1;
    HexBlock hexBlock;

    LinkedList<HexBlock> blockList = hexMemory.getBlockList();
    LinkedList segmentList = hexMemory.getSegmentList();

    blockList.clear();

    for (int i = 0; i < segmentList.size(); i++) {
      if (i == 0) {
        blockStartAddress = ((HexSegment) segmentList.get(i)).getStartAddress();
        blockSize = ((HexSegment) segmentList.get(i)).getSize();
      }
      else {
        segmentStartAddress = ((HexSegment) segmentList.get(i)).getStartAddress();
        if (segmentStartAddress == (blockStartAddress + blockSize)) {
          // enxtend the block
          blockSize += ((HexSegment) segmentList.get(i)).getSize();
        }
        else {
          // save the block
          hexBlock = new HexBlock(blockStartAddress, blockSize);
          // hexBlock.addData(blockStartAddress, blockSize)
          blockList.add(hexBlock);
          // start a new block
          blockStartAddress = ((HexSegment) segmentList.get(i)).getStartAddress();
          blockSize = ((HexSegment) segmentList.get(i)).getSize();
        }
      }
    }
    if (!segmentList.isEmpty()) {
      // save the last block
      hexBlock = new HexBlock(blockStartAddress, blockSize);
      // hexBlock.addData(blockStartAddress, blockSize)
      blockList.add(hexBlock);
    }
  }

}
