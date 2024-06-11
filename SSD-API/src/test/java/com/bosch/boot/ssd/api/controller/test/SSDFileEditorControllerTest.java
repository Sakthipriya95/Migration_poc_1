/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.controller.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import com.bosch.boot.ssd.api.controller.SSDFileEditorController;
import com.bosch.boot.ssd.api.exception.ParameterInvalidException;
import com.bosch.boot.ssd.api.exception.ResourceNotFoundException;
import com.bosch.boot.ssd.api.model.query.Filter;
import com.bosch.boot.ssd.api.service.SSDFileEditorService;

/**
 * @author TAB1JA
 */
@RunWith(MockitoJUnitRunner.class)
public class SSDFileEditorControllerTest {

  @InjectMocks
  private SSDFileEditorController ssdFileEditorController;

  @Mock
  private SSDFileEditorService ssdFileEditorService;

  private final List<String> labelList = new ArrayList<>();

  private final Map<String, String> resultMap = new HashMap<>();

  /**
   *
   */
  @Before()
  public void setUp() {
    this.labelList.add("InjVlv_tiET_MAP");
    this.labelList.add("HPUn_stRailPMonRls_C");

    this.resultMap.put("InjVlv_tiET_MAP", "MAP_INDIVIDUAL");
    this.resultMap.put("HPUn_stRailPMonRls_C", "VALUE");
  }

  /**
   *
   */
  @Test
  public void getLabelCategoryTest() {
    try {
      when(this.ssdFileEditorService.getLabelByType(any(Filter.class))).thenReturn(this.resultMap);
      ResponseEntity<Map<String, String>> result = this.ssdFileEditorController.getLabelCategory(this.labelList);
      assertEquals(200, result.getStatusCodeValue());
    }
    catch (ParameterInvalidException | ResourceNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * @throws Exception ResourceNotFoundException
   */
  @Test(expected = ResourceNotFoundException.class)
  public void getEmptyLabelCategoryTest() throws Exception {
    ResponseEntity<Map<String, String>> result = this.ssdFileEditorController.getLabelCategory(this.labelList);
  }

  /**
   * @throws Exception ParameterInvalidException
   */
  @Test(expected = ParameterInvalidException.class)
  public void getLabelCategoryWithEmptyLabelTest() throws Exception {
    this.labelList.clear();
    ResponseEntity<Map<String, String>> result = this.ssdFileEditorController.getLabelCategory(this.labelList);
  }
}
