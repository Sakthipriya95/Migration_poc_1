package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMCriterionVariants Interface
 */
@IID("{8D25ABE9-40FB-4233-9F28-A29CA362FA62}")
public interface ICDMCriterionVariants extends Com4jObject {
        /**
         * method SetVariant
         */
        @VTID(8)
        void setVariant(
            java.lang.String criterion,
            java.lang.String variant);

        /**
         * method GetCriterions
         */
        @VTID(9)
        java.lang.String[] getCriterions();

        /**
         * method GetVariants
         */
        @VTID(10)
        java.lang.String[] getVariants(
            java.lang.String criterion);

    }
