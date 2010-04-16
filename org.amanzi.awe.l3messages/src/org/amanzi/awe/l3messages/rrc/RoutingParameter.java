
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
    @ASN1BoxedType ( name = "RoutingParameter" )
    public class RoutingParameter implements IASN1PreparedElement {
    
            @ASN1BitString( name = "RoutingParameter" )
            
            @ASN1SizeConstraint ( max = 10L )
        
            private BitString value = null;
            
            public RoutingParameter() {
            }

            public RoutingParameter(BitString value) {
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(RoutingParameter.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

    }
            