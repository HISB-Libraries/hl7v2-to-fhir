package edu.gatech.chai.hl7.v2.parser.fhir.utilities;

public enum V2FHIRCodeSystem {
	ICD10("I10", "http://hl7.org/fhir/sid/icd-10"),
	ICD9("I9", "http://hl7.org/fhir/sid/icd-9-cm"),
	ICD9CM("I9C", "http://hl7.org/fhir/sid/icd-9-cm"),
	ISBT("IBT", "ISBT"),
	LN("LN", "http://loinc.org"),
	NDC("NDC", "http://hl7.org/fhir/sid/ndc"),
	SCT("SCT", "http://snomed.info/sct"),
	SCTMMG("SCT", "http://snomed.info/sct"),
	UCUM("UCUM", "http://unitsofmeasure.org");
	
	
	String v2CodeSystem;
	String fhirCodeSystem;
	
	V2FHIRCodeSystem(String v2CodingSystem, String fhirCodeSystem) {
		this.v2CodeSystem = v2CodingSystem;
		this.fhirCodeSystem = fhirCodeSystem;
	}
	
	public String getV2CodeSystem() {
		return this.v2CodeSystem;
	}
	
	public void setV2CodeSystem(String v2CodeSystem) {
		this.v2CodeSystem = v2CodeSystem;
	}
	
	public String getFhirCodeSystem() {
		return this.fhirCodeSystem;
	}
	
	public void setFhirCodeSystem(String fhirCodeSystem) {
		this.fhirCodeSystem = fhirCodeSystem;
	}
	
	public static String getFhirFromV2(String v2CodeSystem) {
		String fhirCodeSystem = v2CodeSystem;
		
		if (ICD10.getV2CodeSystem().equals(v2CodeSystem)) {
			fhirCodeSystem = ICD10.getFhirCodeSystem();
		} else if (ICD9.getV2CodeSystem().equals(v2CodeSystem)) {
			fhirCodeSystem = ICD9.getFhirCodeSystem();			
		} else if (ICD9CM.getV2CodeSystem().equals(v2CodeSystem)) {
			fhirCodeSystem = ICD9CM.getFhirCodeSystem();			
		} else if (ISBT.getV2CodeSystem().equals(v2CodeSystem)) {
			fhirCodeSystem = ISBT.getFhirCodeSystem();			
		} else if (LN.getV2CodeSystem().equals(v2CodeSystem)) {
			fhirCodeSystem = LN.getFhirCodeSystem();			
		} else if (NDC.getV2CodeSystem().equals(v2CodeSystem)) {
			fhirCodeSystem = NDC.getFhirCodeSystem();			
		} else if (SCT.getV2CodeSystem().equals(v2CodeSystem)) {
			fhirCodeSystem = SCT.getFhirCodeSystem();			
		} else if (SCTMMG.getV2CodeSystem().equals(v2CodeSystem)) {
			fhirCodeSystem = SCTMMG.getFhirCodeSystem();			
		} else if (UCUM.getV2CodeSystem().equals(v2CodeSystem)) {
			fhirCodeSystem = UCUM.getFhirCodeSystem();			
		}
		
		return fhirCodeSystem;
	}
	
	public static String getV2FromFHIR(String fhirCodeSystem) {
		String v2CodeSystem = fhirCodeSystem;
		
		if (ICD10.getV2CodeSystem().equals(fhirCodeSystem)) {
			v2CodeSystem = ICD10.getV2CodeSystem();
		} else if (ICD9.getV2CodeSystem().equals(fhirCodeSystem)) {
			v2CodeSystem = ICD9.getV2CodeSystem();			
		} else if (ICD9CM.getV2CodeSystem().equals(fhirCodeSystem)) {
			v2CodeSystem = ICD9CM.getV2CodeSystem();			
		} else if (ISBT.getV2CodeSystem().equals(fhirCodeSystem)) {
			v2CodeSystem = ISBT.getV2CodeSystem();			
		} else if (LN.getV2CodeSystem().equals(fhirCodeSystem)) {
			v2CodeSystem = LN.getV2CodeSystem();			
		} else if (NDC.getV2CodeSystem().equals(fhirCodeSystem)) {
			v2CodeSystem = NDC.getV2CodeSystem();			
		} else if (SCT.getV2CodeSystem().equals(fhirCodeSystem)) {
			v2CodeSystem = SCT.getV2CodeSystem();			
		} else if (SCTMMG.getV2CodeSystem().equals(fhirCodeSystem)) {
			v2CodeSystem = SCTMMG.getV2CodeSystem();			
		}  else if (UCUM.getV2CodeSystem().equals(fhirCodeSystem)) {
			v2CodeSystem = UCUM.getV2CodeSystem();			
		}
		
		return v2CodeSystem;
	}
}
