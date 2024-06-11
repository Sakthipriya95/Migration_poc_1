/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.tulservice.client;

/**
 * @author ICP1COB
 */
public class Application {

//  private static ToolUsageLoggerClient tool = new ToolUsageLoggerClient();
//  /**
//   * @param args
//   */
//  public static void main(String[] args) {
//
//    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
//
//    List<ToolUsageStat> statList = new ArrayList<>();
//    for (int i = 1; i <= 1000; i++) {
//      ToolUsageStat stat = new ToolUsageStat();
//      stat.setUsername("VP" + i);
//      statList.add(stat);
//    }
//
//    CountDownLatch countDownLatch = new CountDownLatch(5);
//    for (ToolUsageStat stat : statList) {
//      executor.execute(new ThreadExecutor(stat, tool, countDownLatch));
//    }
//    try {
//      countDownLatch.await();
//        tool.deserialize(ToolUsageLoggerClient.OFFLINE_DATA_FILE);
//    }
//      catch (IOException | InterruptedException e) {
//        // TODO Auto-generated catch block
//        e.printStackTrace();
//      }
//      System.out.println("Completed");
//  }
}
