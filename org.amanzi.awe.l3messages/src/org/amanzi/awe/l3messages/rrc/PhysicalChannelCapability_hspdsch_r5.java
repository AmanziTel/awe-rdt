
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
    @ASN1Sequence ( name = "PhysicalChannelCapability_hspdsch_r5", isSet = false )
    public class PhysicalChannelCapability_hspdsch_r5 implements IASN1PreparedElement {
            
        
    @ASN1PreparedElement
    @ASN1Choice ( name = "fdd_hspdsch" )
    public static class Fdd_hspdschChoiceType implements IASN1PreparedElement {
            

       @ASN1PreparedElement
       @ASN1Sequence ( name = "supported" , isSet = false )
       public static class SupportedSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "hsdsch-physical-layer-category", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private HSDSCH_physical_layer_category hsdsch_physical_layer_category = null;
                
  @ASN1Boolean( name = "" )
    
        @ASN1Element ( name = "dummy", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private Boolean dummy = null;
                
  @ASN1Boolean( name = "" )
    
        @ASN1Element ( name = "dummy2", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private Boolean dummy2 = null;
                
  
        
        public HSDSCH_physical_layer_category getHsdsch_physical_layer_category () {
            return this.hsdsch_physical_layer_category;
        }

        

        public void setHsdsch_physical_layer_category (HSDSCH_physical_layer_category value) {
            this.hsdsch_physical_layer_category = value;
        }
        
  
        
        public Boolean getDummy () {
            return this.dummy;
        }

        

        public void setDummy (Boolean value) {
            this.dummy = value;
        }
        
  
        
        public Boolean getDummy2 () {
            return this.dummy2;
        }

        

        public void setDummy2 (Boolean value) {
            this.dummy2 = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_SupportedSequenceType;
        }

       private static IASN1PreparedElementData preparedData_SupportedSequenceType = CoderFactory.getInstance().newPreparedElementData(SupportedSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "supported", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private SupportedSequenceType supported = null;
                
  
        @ASN1Null ( name = "unsupported" ) 
    
        @ASN1Element ( name = "unsupported", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject unsupported = null;
                
  
        
        public SupportedSequenceType getSupported () {
            return this.supported;
        }

        public boolean isSupportedSelected () {
            return this.supported != null;
        }

        private void setSupported (SupportedSequenceType value) {
            this.supported = value;
        }

        
        public void selectSupported (SupportedSequenceType value) {
            this.supported = value;
            
                    setUnsupported(null);
                            
        }

        
  
        
        public org.bn.types.NullObject getUnsupported () {
            return this.unsupported;
        }

        public boolean isUnsupportedSelected () {
            return this.unsupported != null;
        }

        private void setUnsupported (org.bn.types.NullObject value) {
            this.unsupported = value;
        }

        
        public void selectUnsupported () {
            selectUnsupported (new org.bn.types.NullObject());
	}
	
        public void selectUnsupported (org.bn.types.NullObject value) {
            this.unsupported = value;
            
                    setSupported(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_Fdd_hspdschChoiceType;
        }

        private static IASN1PreparedElementData preparedData_Fdd_hspdschChoiceType = CoderFactory.getInstance().newPreparedElementData(Fdd_hspdschChoiceType.class);

    }

                
        @ASN1Element ( name = "fdd_hspdsch", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private Fdd_hspdschChoiceType fdd_hspdsch = null;
                
  
        
    @ASN1PreparedElement
    @ASN1Choice ( name = "tdd384_hspdsch" )
    public static class Tdd384_hspdschChoiceType implements IASN1PreparedElement {
            
        @ASN1Element ( name = "supported", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private HSDSCH_physical_layer_category supported = null;
                
  
        @ASN1Null ( name = "unsupported" ) 
    
        @ASN1Element ( name = "unsupported", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject unsupported = null;
                
  
        
        public HSDSCH_physical_layer_category getSupported () {
            return this.supported;
        }

        public boolean isSupportedSelected () {
            return this.supported != null;
        }

        private void setSupported (HSDSCH_physical_layer_category value) {
            this.supported = value;
        }

        
        public void selectSupported (HSDSCH_physical_layer_category value) {
            this.supported = value;
            
                    setUnsupported(null);
                            
        }

        
  
        
        public org.bn.types.NullObject getUnsupported () {
            return this.unsupported;
        }

        public boolean isUnsupportedSelected () {
            return this.unsupported != null;
        }

        private void setUnsupported (org.bn.types.NullObject value) {
            this.unsupported = value;
        }

        
        public void selectUnsupported () {
            selectUnsupported (new org.bn.types.NullObject());
	}
	
        public void selectUnsupported (org.bn.types.NullObject value) {
            this.unsupported = value;
            
                    setSupported(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_Tdd384_hspdschChoiceType;
        }

        private static IASN1PreparedElementData preparedData_Tdd384_hspdschChoiceType = CoderFactory.getInstance().newPreparedElementData(Tdd384_hspdschChoiceType.class);

    }

                
        @ASN1Element ( name = "tdd384_hspdsch", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private Tdd384_hspdschChoiceType tdd384_hspdsch = null;
                
  
        
    @ASN1PreparedElement
    @ASN1Choice ( name = "tdd128_hspdsch" )
    public static class Tdd128_hspdschChoiceType implements IASN1PreparedElement {
            
        @ASN1Element ( name = "supported", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private HSDSCH_physical_layer_category supported = null;
                
  
        @ASN1Null ( name = "unsupported" ) 
    
        @ASN1Element ( name = "unsupported", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject unsupported = null;
                
  
        
        public HSDSCH_physical_layer_category getSupported () {
            return this.supported;
        }

        public boolean isSupportedSelected () {
            return this.supported != null;
        }

        private void setSupported (HSDSCH_physical_layer_category value) {
            this.supported = value;
        }

        
        public void selectSupported (HSDSCH_physical_layer_category value) {
            this.supported = value;
            
                    setUnsupported(null);
                            
        }

        
  
        
        public org.bn.types.NullObject getUnsupported () {
            return this.unsupported;
        }

        public boolean isUnsupportedSelected () {
            return this.unsupported != null;
        }

        private void setUnsupported (org.bn.types.NullObject value) {
            this.unsupported = value;
        }

        
        public void selectUnsupported () {
            selectUnsupported (new org.bn.types.NullObject());
	}
	
        public void selectUnsupported (org.bn.types.NullObject value) {
            this.unsupported = value;
            
                    setSupported(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_Tdd128_hspdschChoiceType;
        }

        private static IASN1PreparedElementData preparedData_Tdd128_hspdschChoiceType = CoderFactory.getInstance().newPreparedElementData(Tdd128_hspdschChoiceType.class);

    }

                
        @ASN1Element ( name = "tdd128_hspdsch", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private Tdd128_hspdschChoiceType tdd128_hspdsch = null;
                
  
        
        public Fdd_hspdschChoiceType getFdd_hspdsch () {
            return this.fdd_hspdsch;
        }

        

        public void setFdd_hspdsch (Fdd_hspdschChoiceType value) {
            this.fdd_hspdsch = value;
        }
        
  
        
        public Tdd384_hspdschChoiceType getTdd384_hspdsch () {
            return this.tdd384_hspdsch;
        }

        

        public void setTdd384_hspdsch (Tdd384_hspdschChoiceType value) {
            this.tdd384_hspdsch = value;
        }
        
  
        
        public Tdd128_hspdschChoiceType getTdd128_hspdsch () {
            return this.tdd128_hspdsch;
        }

        

        public void setTdd128_hspdsch (Tdd128_hspdschChoiceType value) {
            this.tdd128_hspdsch = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(PhysicalChannelCapability_hspdsch_r5.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            