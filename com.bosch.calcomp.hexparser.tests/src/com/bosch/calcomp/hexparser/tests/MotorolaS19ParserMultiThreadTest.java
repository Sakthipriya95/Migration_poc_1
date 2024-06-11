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
public class MotorolaS19ParserMultiThreadTest {

  /**
   * Test case for Multi threading run of Motorola S19 Parser
   */
  @Test
  public void motorolaS19ParserMultiThreadingTest() {


    // Excecution service for running multiple thread in queue
    ExecutorService executorService = Executors.newFixedThreadPool(3);
    // Intance of class which contains test datas
    MotorolaS19ParserCallable firstFileSet = new MotorolaS19ParserCallable("TestFiles\\D980214D2B53T1BY10.s19");
    MotorolaS19ParserCallable secondFileSet = new MotorolaS19ParserCallable("TestFiles\\S19File_1.s19");

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