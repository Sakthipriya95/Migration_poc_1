package com.bosch.boot.ssd.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author GDH9COB
 */
@SpringBootApplication
@EnableScheduling
public class SsdApiApplication extends SpringBootServletInitializer {

  /**
   * @param args
   */
  public static void main(final String[] args) {
    SpringApplication.run(SsdApiApplication.class, args);
  }
}