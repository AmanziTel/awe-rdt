
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
    @ASN1Sequence ( name = "RLC_Capability_r5_ext", isSet = false )
    public class RLC_Capability_r5_ext implements IASN1PreparedElement {
            
        @ASN1Element ( name = "totalRLC-AM-BufferSize", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private TotalRLC_AM_BufferSize_r5_ext totalRLC_AM_BufferSize = null;
                
  
        
        public TotalRLC_AM_BufferSize_r5_ext getTotalRLC_AM_BufferSize () {
            return this.totalRLC_AM_BufferSize;
        }

        
        public boolean isTotalRLC_AM_BufferSizePresent () {
            return this.totalRLC_AM_BufferSize != null;
        }
        

        public void setTotalRLC_AM_BufferSize (TotalRLC_AM_BufferSize_r5_ext value) {
            this.totalRLC_AM_BufferSize = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(RLC_Capability_r5_ext.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            