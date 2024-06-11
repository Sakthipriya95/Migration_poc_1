package com.bosch.caltool.icdm.ws.rest.client.a2l;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.bosch.caltool.icdm.model.a2l.A2lSysconst;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;


/**
 * Service Client Test for A2lSyscon
 * 
 * @author pdh2cob
 * 
 */
public class A2lSysconstServiceClientTest
    extends AbstractRestClientTest
{

    private final static Long A2LSYSCONST_ID = 37755L;
    /**
     * Expected exception
     * 
     */
    public final ExpectedException thrown = ExpectedException.none();

    /**
     * Test method for {@link com.bosch.caltool.icdm.rest.client.a2l.A2lSysconstServiceClientTest#get()} 
     * 
     */
    @Test
    public void testGet() {
        A2lSysconstServiceClient servClient = new A2lSysconstServiceClient();
        try {
            A2lSysconst ret = servClient.get(A2LSYSCONST_ID);
            assertFalse("Response should not be null", (ret == null));
        } catch (Exception excep) {
            LOG.error("Error in WS call", excep);
            assertNull("Error in WS call", excep);
        }
    }
    
    /**
     * Test method for {@link com.bosch.caltool.icdm.rest.client.a2l.A2lSysconstServiceClientTest#getInvalidSystemConstants()} 
     * 
     */
    @Test
    public void testInvalidSyscons(){

      A2lSysconstServiceClient servClient = new A2lSysconstServiceClient();
      try {
        List<String> sysconNames = new ArrayList<>();
        sysconNames.add("CRCTL_DVRESABVG4_MAP_Y");
        sysconNames.add("SY_CANNOHK");
        sysconNames.add("test");
        
          List<String> ret = servClient.getInvalidSystemConstants(sysconNames);
          
          System.out.println(ret);
          
      } catch (Exception excep) {
          LOG.error("Error in WS call", excep);
          assertNull("Error in WS call", excep);
      }
  
    }


}
