/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.hexparser.tests;

import static org.junit.Assert.assertNotEquals;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

import com.bosch.calcomp.hexparser.model.HexMemory;

/**
 * @author ICP1COB
 */
public class IHexParserMultiThreadTest {

  /**
   * Test case for Mutlti threading run of HEX Parser
   */
  @Test
  public void iHexParserMultiThreadingTest() {


    // Excecution service for running multiple thread in queue
    ExecutorService executorService = Executors.newFixedThreadPool(3);
    // Intance of class which contains test data
    IHexParserCallable firstFileSet = new IHexParserCallable("TestFiles\\C132430V6HS0CGAI0D570P01_prelim02_HPT.hex");
    IHexParserCallable secondFileSet = new IHexParserCallable("TestFiles\\HEX_EA56HPE6M240LG032KAHA.hex");

    // response or result collection from each data run
    Future<HexMemory> futureOne = executorService.submit(firstFileSet);
    Future<HexMemory> futureTwo = executorService.submit(secondFileSet);

    try {

      assertNotEquals(futureOne.get().getBlockList(), futureTwo.get().getBlockList());
      assertNotEquals(futureOne.get().getFirstHexBlock(), futureTwo.get().getFirstHexBlock());
      assertNotEquals(futureOne.get().getFirstHexSegment(), futureTwo.get().getFirstHexSegment());
      assertNotEquals(futureOne.get().getNextHexSegment(), futureTwo.get().getNextHexSegment());
      assertNotEquals(futureOne.get().getNextHexBlock(), futureTwo.get().getNextHexBlock());
    }
    catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      System.out.println("Multi threading test case failed due to: " + e.getMessage());
    }
    // closing of service
    executorService.shutdown();
  }

}