
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
    @ASN1Sequence ( name = "UL_TransChCapability", isSet = false )
    public class UL_TransChCapability implements IASN1PreparedElement {
            
        @ASN1Element ( name = "maxNoBitsTransmitted", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private MaxNoBits maxNoBitsTransmitted = null;
                
  
        @ASN1Element ( name = "maxConvCodeBitsTransmitted", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private MaxNoBits maxConvCodeBitsTransmitted = null;
                
  
        @ASN1Element ( name = "turboEncodingSupport", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private TurboSupport turboEncodingSupport = null;
                
  
        @ASN1Element ( name = "maxSimultaneousTransChs", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private MaxSimultaneousTransChsUL maxSimultaneousTransChs = null;
                
  
        
    @ASN1PreparedElement
    @ASN1Choice ( name = "modeSpecificInfo" )
    public static class ModeSpecificInfoChoiceType implements IASN1PreparedElement {
            
        @ASN1Null ( name = "fdd" ) 
    
        @ASN1Element ( name = "fdd", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject fdd = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "tdd" , isSet = false )
       public static class TddSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "maxSimultaneousCCTrCH-Count", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private MaxSimultaneousCCTrCH_Count maxSimultaneousCCTrCH_Count = null;
                
  
        
        public MaxSimultaneousCCTrCH_Count getMaxSimultaneousCCTrCH_Count () {
            return this.maxSimultaneousCCTrCH_Count;
        }

        

        public void setMaxSimultaneousCCTrCH_Count (MaxSimultaneousCCTrCH_Count value) {
            this.maxSimultaneousCCTrCH_Count = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_TddSequenceType;
        }

       private static IASN1PreparedElementData preparedData_TddSequenceType = CoderFactory.getInstance().newPreparedElementData(TddSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "tdd", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private TddSequenceType tdd = null;
                
  
        
        public org.bn.types.NullObject getFdd () {
            return this.fdd;
        }

        public boolean isFddSelected () {
            return this.fdd != null;
        }

        private void setFdd (org.bn.types.NullObject value) {
            this.fdd = value;
        }

        
        public void selectFdd () {
            selectFdd (new org.bn.types.NullObject());
	}
	
        public void selectFdd (org.bn.types.NullObject value) {
            this.fdd = value;
            
                    setTdd(null);
                            
        }

        
  
        
        public TddSequenceType getTdd () {
            return this.tdd;
        }

        public boolean isTddSelected () {
            return this.tdd != null;
        }

        private void setTdd (TddSequenceType value) {
            this.tdd = value;
        }

        
        public void selectTdd (TddSequenceType value) {
            this.tdd = value;
            
                    setFdd(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_ModeSpecificInfoChoiceType;
        }

        private static IASN1PreparedElementData preparedData_ModeSpecificInfoChoiceType = CoderFactory.getInstance().newPreparedElementData(ModeSpecificInfoChoiceType.class);

    }

                
        @ASN1Element ( name = "modeSpecificInfo", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private ModeSpecificInfoChoiceType modeSpecificInfo = null;
                
  
        @ASN1Element ( name = "maxTransmittedBlocks", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private MaxTransportBlocksUL maxTransmittedBlocks = null;
                
  
        @ASN1Element ( name = "maxNumberOfTFC", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private MaxNumberOfTFC_UL maxNumberOfTFC = null;
                
  
        @ASN1Element ( name = "maxNumberOfTF", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private MaxNumberOfTF maxNumberOfTF = null;
                
  
        
        public MaxNoBits getMaxNoBitsTransmitted () {
            return this.maxNoBitsTransmitted;
        }

        

        public void setMaxNoBitsTransmitted (MaxNoBits value) {
            this.maxNoBitsTransmitted = value;
        }
        
  
        
        public MaxNoBits getMaxConvCodeBitsTransmitted () {
            return this.maxConvCodeBitsTransmitted;
        }

        

        public void setMaxConvCodeBitsTransmitted (MaxNoBits value) {
            this.maxConvCodeBitsTransmitted = value;
        }
        
  
        
        public TurboSupport getTurboEncodingSupport () {
            return this.turboEncodingSupport;
        }

        

        public void setTurboEncodingSupport (TurboSupport value) {
            this.turboEncodingSupport = value;
        }
        
  
        
        public MaxSimultaneousTransChsUL getMaxSimultaneousTransChs () {
            return this.maxSimultaneousTransChs;
        }

        

        public void setMaxSimultaneousTransChs (MaxSimultaneousTransChsUL value) {
            this.maxSimultaneousTransChs = value;
        }
        
  
        
        public ModeSpecificInfoChoiceType getModeSpecificInfo () {
            return this.modeSpecificInfo;
        }

        

        public void setModeSpecificInfo (ModeSpecificInfoChoiceType value) {
            this.modeSpecificInfo = value;
        }
        
  
        
        public MaxTransportBlocksUL getMaxTransmittedBlocks () {
            return this.maxTransmittedBlocks;
        }

        

        public void setMaxTransmittedBlocks (MaxTransportBlocksUL value) {
            this.maxTransmittedBlocks = value;
        }
        
  
        
        public MaxNumberOfTFC_UL getMaxNumberOfTFC () {
            return this.maxNumberOfTFC;
        }

        

        public void setMaxNumberOfTFC (MaxNumberOfTFC_UL value) {
            this.maxNumberOfTFC = value;
        }
        
  
        
        public MaxNumberOfTF getMaxNumberOfTF () {
            return this.maxNumberOfTF;
        }

        

        public void setMaxNumberOfTF (MaxNumberOfTF value) {
            this.maxNumberOfTF = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(UL_TransChCapability.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            