
package org.amanzi.awe.l3messages.rrc;
//
// This file was generated by the BinaryNotes compiler.
// See http://bnotes.sourceforge.net 
// Any modifications to this file will be lost upon recompilation of the source ASN.1. 
//

import org.bn.*;
import org.bn.annotations.*;
import org.bn.annotations.constraints.*;
import org.bn.coders.*;
import org.bn.types.*;




    @ASN1PreparedElement
    @ASN1BoxedType ( name = "UE_RX_TX_TimeDifferenceType2" )
    public class UE_RX_TX_TimeDifferenceType2 implements IASN1PreparedElement {
    
            @ASN1Integer( name = "UE-RX-TX-TimeDifferenceType2" )
            @ASN1ValueRangeConstraint ( 
		
		min = 0L, 
		
		max = 8191L 
		
	   )
	   
            private Integer value;
            
            public UE_RX_TX_TimeDifferenceType2() {
            }

            public UE_RX_TX_TimeDifferenceType2(Integer value) {
                this.value = value;
            }
            
            public void setValue(Integer value) {
                this.value = value;
            }
            
            public Integer getValue() {
                return this.value;
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(UE_RX_TX_TimeDifferenceType2.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            