
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
    @ASN1BoxedType ( name = "START_Value" )
    public class START_Value implements IASN1PreparedElement {
    
            @ASN1BitString( name = "START-Value" )
            
            @ASN1SizeConstraint ( max = 20L )
        
            private BitString value = null;
            
            public START_Value() {
            }

            public START_Value(BitString value) {
                this.value = value;
            }
            
            public void setValue(BitString value) {
                this.value = value;
            }
            
            public BitString getValue() {
                return this.value;
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(START_Value.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

    }
            