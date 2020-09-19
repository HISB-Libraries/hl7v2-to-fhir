package edu.gatech.chai.hl7.v2.parser.fhir;

public abstract class BaseHL7v2FHIRParser implements IHL7v2FHIRParser {
	private String sendingFacilityName = null;
	private String receivingFacilityName = null;
	
	public void setSendingFacilityName(String sendingFacilityName) {
		this.sendingFacilityName = sendingFacilityName;
	}
	
	public String getSendingFacilityName() {
		return sendingFacilityName;
	}

	public void setReceivingFacilityName(String receivingFacilityName) {
		this.receivingFacilityName = receivingFacilityName;
	}
	
	public String getReceivingFacilityName() {
		return receivingFacilityName;
	}
}
