package com.bosch.boot.ssd.api.validaterules.messageinterfaces.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.bosch.boot.ssd.api.service.ValidateRuleInvokerService;
import com.bosch.boot.ssd.api.validaterules.messagesinterfaces.IPrintMessages;

/**
 * @author TUD1COB
 */
class TestIPrintMessages implements IPrintMessages {

  /**
   * {@inheritDoc}
   */
  @Override
  public ValidateRuleInvokerService getInstance() {
    return new ValidateRuleInvokerService();
  }

}

/**
 *
 */
public class IPrintMessagesTest {

  /**
   *
   */
  @Test
  public void test() {
    Map<Integer, String> lineNoAndError = new HashMap<>();
    lineNoAndError.put(10, "Error");
    TestIPrintMessages testIPrintMessages = new TestIPrintMessages();
    testIPrintMessages.setLineNoAndError(lineNoAndError);
    System.out.println(testIPrintMessages.getLineNoAndError());
  }

}
