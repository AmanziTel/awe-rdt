
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
    @ASN1Sequence ( name = "UE_Positioning_OTDOA_Quality", isSet = false )
    public class UE_Positioning_OTDOA_Quality implements IASN1PreparedElement {
            @ASN1BitString( name = "" )
    
            @ASN1SizeConstraint ( max = 2L )
        
        @ASN1Element ( name = "stdResolution", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private BitString stdResolution = null;
                
  @ASN1BitString( name = "" )
    
            @ASN1SizeConstraint ( max = 3L )
        
        @ASN1Element ( name = "numberOfOTDOA-Measurements", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private BitString numberOfOTDOA_Measurements = null;
                
  @ASN1BitString( name = "" )
    
            @ASN1SizeConstraint ( max = 5L )
        
        @ASN1Element ( name = "stdOfOTDOA-Measurements", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private BitString stdOfOTDOA_Measurements = null;
                
  
        
        public BitString getStdResolution () {
            return this.stdResolution;
        }

        

        public void setStdResolution (BitString value) {
            this.stdResolution = value;
        }
        
  
        
        public BitString getNumberOfOTDOA_Measurements () {
            return this.numberOfOTDOA_Measurements;
        }

        

        public void setNumberOfOTDOA_Measurements (BitString value) {
            this.numberOfOTDOA_Measurements = value;
        }
        
  
        
        public BitString getStdOfOTDOA_Measurements () {
            return this.stdOfOTDOA_Measurements;
        }

        

        public void setStdOfOTDOA_Measurements (BitString value) {
            this.stdOfOTDOA_Measurements = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(UE_Positioning_OTDOA_Quality.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            