/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;


/**
 * @author dmr1cob
 */
public class AttributeV2 extends Attribute {

  private Long characterId;

  private String characterName;


  /**
   * @return the characterId
   */
  public Long getCharacterId() {
    return this.characterId;
  }


  /**
   * @param characterId the characterId to set
   */
  public void setCharacterId(final Long characterId) {
    this.characterId = characterId;
  }


  /**
   * @return the characterName
   */
  public String getCharacterName() {
    return this.characterName;
  }


  /**
   * @param characterName the characterName to set
   */
  public void setCharacterName(final String characterName) {
    this.characterName = characterName;
  }


}
