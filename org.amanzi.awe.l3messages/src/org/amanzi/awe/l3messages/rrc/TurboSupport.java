
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
    @ASN1Choice ( name = "TurboSupport" )
    public class TurboSupport implements IASN1PreparedElement {
            
        @ASN1Null ( name = "notSupported" ) 
    
        @ASN1Element ( name = "notSupported", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject notSupported = null;
                
  
        @ASN1Element ( name = "supported", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private MaxNoBits supported = null;
                
  
        
        public org.bn.types.NullObject getNotSupported () {
            return this.notSupported;
        }

        public boolean isNotSupportedSelected () {
            return this.notSupported != null;
        }

        private void setNotSupported (org.bn.types.NullObject value) {
            this.notSupported = value;
        }

        
        public void selectNotSupported () {
            selectNotSupported (new org.bn.types.NullObject());
	}
	
        public void selectNotSupported (org.bn.types.NullObject value) {
            this.notSupported = value;
            
                    setSupported(null);
                            
        }

        
  
        
        public MaxNoBits getSupported () {
            return this.supported;
        }

        public boolean isSupportedSelected () {
            return this.supported != null;
        }

        private void setSupported (MaxNoBits value) {
            this.supported = value;
        }

        
        public void selectSupported (MaxNoBits value) {
            this.supported = value;
            
                    setNotSupported(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(TurboSupport.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            