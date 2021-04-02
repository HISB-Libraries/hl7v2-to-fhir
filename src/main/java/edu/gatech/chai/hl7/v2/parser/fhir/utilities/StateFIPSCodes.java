package edu.gatech.chai.hl7.v2.parser.fhir.utilities;

public enum StateFIPSCodes {
	ALABAMA("Alabama","AL","01"),
	ALASKA("Alaska","AK","02"),
	ARIZONA("Arizona","AZ","04"),
	ARKANSAS("Arkansas","AR","05"),
	CALIFORNIA("California","CA","06"),
	COLORADO("Colorado","CO","08"),
	CONNECTICUT("Connecticut","CT","09"),
	DELAWARE("Delaware","DE","10"),
	FLORIDA("Florida","FL","12"),
	GEORGIA("Georgia","GA","13"),
	HAWAII("Hawaii","HI","15"),
	IDAHO("Idaho","ID","16"),
	ILLINOIS("Illinois","IL","17"),
	INDIANA("Indiana","IN","18"),
	IOWA("Iowa","IA","19"),
	KANSAS("Kansas","KS","20"),
	KENTUCKY("Kentucky","KY","21"),
	LOUISIANA("Louisiana","LA","22"),
	MAINE("Maine","ME","23"),
	MARYLAND("Maryland","MD","24"),
	MASSACHUSETTS("Massachusetts","MA","25"),
	MICHIGAN("Michigan","MI","26"),
	MINNESOTA("Minnesota","MN","27"),
	MISSISSIPPI("Mississippi","MS","28"),
	MISSOURI("Missouri","MO","29"),
	MONTANA("Montana","MT","30"),
	NEBRASKA("Nebraska","NE","31"),
	NEVADA("Nevada","NV","32"),
	NEW_HAMPSHIRE("New Hampshire","NH","33"),
	NEW_JERSEY("New Jersey","NJ","34"),
	NEW_MEXICO("New Mexico","NM","35"),
	NEW_YORK("New York","NY","36"),
	NORTH_CAROLINA("North Carolina","NC","37"),
	NORTH_DAKOTA("North Dakota","ND","38"),
	OHIO("Ohio","OH","39"),
	OKLAHOMA("Oklahoma","OK","40"),
	OREGON("Oregon","OR","41"),
	PENNSYLVANIA("Pennsylvania","PA","42"),
	RHODE_ISLAND("Rhode Island","RI","44"),
	SOUTH_CAROLINA("South Carolina","SC","45"),
	SOUTH_DAKOTA("South Dakota","SD","46"),
	TENNESSEE("Tennessee","TN","47"),
	TEXAS("Texas","TX","48"),
	UTAH("Utah","UT","49"),
	VERMONT("Vermont","VT","50"),
	VIRGINIA("Virginia","VA","51"),
	WASHINGTON("Washington","WA","53"),
	WEST_VIRGINIA("West Virginia","WV","54"),
	WISCONSIN("Wisconsin","WI","55"),
	WYOMING("Wyoming","WY","56"),
	AMERICAN_SAMOA("American Samoa","AS","60"),
	GUAM("Guam","GU","66"),
	NORTHERN_MARIANA_ISLANDS("Northern Mariana Islands","MP","69"),
	PUERTO_RICO("Puerto Rico","PR","72"),
	VIRGIN_ISLANDS("Virgin Islands","VI","78");
	
	String stateFullName;
	String state2Letter;
	String fipsCode;
	
	StateFIPSCodes(String stateFullName, String state2Letter, String fipsCode) {
		this.stateFullName = stateFullName;
		this.state2Letter = state2Letter;
		this.fipsCode = fipsCode;
	}
	
	public String getStateFullName() {
		return this.stateFullName;
	}
	
	public void setStateFullName(String stateFullName) {
		this.stateFullName = stateFullName;
	}
	
	public String getState2Letter() {
		return this.state2Letter;
	}
	
	public void setState2Letter(String state2Letter) {
		this.state2Letter = state2Letter;
	}
	
	public String getFipsCode() {
		return this.fipsCode;
	}
	
	public void setFipsCode(String fipsCode) {
		this.fipsCode = fipsCode;
	}
	
	public static String get2LetterFromFips(String fipsCode) {
		if (ALABAMA.getFipsCode().equals(fipsCode)) {
			return ALABAMA.getState2Letter();
		} else if (ALASKA.getFipsCode().equals(fipsCode)) {
			return ALASKA.getState2Letter();
		} else if (ARIZONA.getFipsCode().equals(fipsCode)) {
			return ARIZONA.getState2Letter();
		} else if (ARKANSAS.getFipsCode().equals(fipsCode)) {
			return ARKANSAS.getState2Letter();
		} else if (CALIFORNIA.getFipsCode().equals(fipsCode)) {
			return CALIFORNIA.getState2Letter();
		} else if (COLORADO.getFipsCode().equals(fipsCode)) {
			return COLORADO.getState2Letter();
		} else if (CONNECTICUT.getFipsCode().equals(fipsCode)) {
			return CONNECTICUT.getState2Letter();
		} else if (DELAWARE.getFipsCode().equals(fipsCode)) {
			return DELAWARE.getState2Letter();
		} else if (FLORIDA.getFipsCode().equals(fipsCode)) {
			return FLORIDA.getState2Letter();
		} else if (GEORGIA.getFipsCode().equals(fipsCode)) {
			return GEORGIA.getState2Letter();
		} else if (HAWAII.getFipsCode().equals(fipsCode)) {
			return HAWAII.getState2Letter();
		} else if (IDAHO.getFipsCode().equals(fipsCode)) {
			return IDAHO.getState2Letter();
		} else if (ILLINOIS.getFipsCode().equals(fipsCode)) {
			return ILLINOIS.getState2Letter();
		} else if (INDIANA.getFipsCode().equals(fipsCode)) {
			return INDIANA.getState2Letter();
		} else if (IOWA.getFipsCode().equals(fipsCode)) {
			return IOWA.getState2Letter();
		} else if (KANSAS.getFipsCode().equals(fipsCode)) {
			return KANSAS.getState2Letter();
		} else if (KENTUCKY.getFipsCode().equals(fipsCode)) {
			return KENTUCKY.getState2Letter();
		} else if (LOUISIANA.getFipsCode().equals(fipsCode)) {
			return LOUISIANA.getState2Letter();
		} else if (MAINE.getFipsCode().equals(fipsCode)) {
			return MAINE.getState2Letter();
		} else if (MARYLAND.getFipsCode().equals(fipsCode)) {
			return MARYLAND.getState2Letter();
		} else if (MASSACHUSETTS.getFipsCode().equals(fipsCode)) {
			return MASSACHUSETTS.getState2Letter();
		} else if (MICHIGAN.getFipsCode().equals(fipsCode)) {
			return MICHIGAN.getState2Letter();
		} else if (MINNESOTA.getFipsCode().equals(fipsCode)) {
			return MINNESOTA.getState2Letter();
		} else if (MISSISSIPPI.getFipsCode().equals(fipsCode)) {
			return MISSISSIPPI.getState2Letter();
		} else if (MISSOURI.getFipsCode().equals(fipsCode)) {
			return MISSOURI.getState2Letter();
		} else if (MONTANA.getFipsCode().equals(fipsCode)) {
			return MONTANA.getState2Letter();
		} else if (NEBRASKA.getFipsCode().equals(fipsCode)) {
			return NEBRASKA.getState2Letter();
		} else if (NEVADA.getFipsCode().equals(fipsCode)) {
			return NEVADA.getState2Letter();
		} else if (NEW_HAMPSHIRE.getFipsCode().equals(fipsCode)) {
			return NEW_HAMPSHIRE.getState2Letter();
		} else if (NEW_JERSEY.getFipsCode().equals(fipsCode)) {
			return NEW_JERSEY.getState2Letter();
		} else if (NEW_MEXICO.getFipsCode().equals(fipsCode)) {
			return NEW_MEXICO.getState2Letter();
		} else if (NEW_YORK.getFipsCode().equals(fipsCode)) {
			return NEW_YORK.getState2Letter();
		} else if (NORTH_CAROLINA.getFipsCode().equals(fipsCode)) {
			return NORTH_CAROLINA.getState2Letter();
		} else if (NORTH_DAKOTA.getFipsCode().equals(fipsCode)) {
			return NORTH_DAKOTA.getState2Letter();
		} else if (OHIO.getFipsCode().equals(fipsCode)) {
			return OHIO.getState2Letter();
		} else if (OKLAHOMA.getFipsCode().equals(fipsCode)) {
			return OKLAHOMA.getState2Letter();
		} else if (OREGON.getFipsCode().equals(fipsCode)) {
			return OREGON.getState2Letter();
		} else if (PENNSYLVANIA.getFipsCode().equals(fipsCode)) {
			return PENNSYLVANIA.getState2Letter();
		} else if (RHODE_ISLAND.getFipsCode().equals(fipsCode)) {
			return RHODE_ISLAND.getState2Letter();
		} else if (SOUTH_CAROLINA.getFipsCode().equals(fipsCode)) {
			return SOUTH_CAROLINA.getState2Letter();
		} else if (SOUTH_DAKOTA.getFipsCode().equals(fipsCode)) {
			return SOUTH_DAKOTA.getState2Letter();
		} else if (TENNESSEE.getFipsCode().equals(fipsCode)) {
			return TENNESSEE.getState2Letter();
		} else if (TEXAS.getFipsCode().equals(fipsCode)) {
			return TEXAS.getState2Letter();
		} else if (UTAH.getFipsCode().equals(fipsCode)) {
			return UTAH.getState2Letter();
		} else if (VERMONT.getFipsCode().equals(fipsCode)) {
			return VERMONT.getState2Letter();
		} else if (VIRGINIA.getFipsCode().equals(fipsCode)) {
			return VIRGINIA.getState2Letter();
		} else if (WASHINGTON.getFipsCode().equals(fipsCode)) {
			return WASHINGTON.getState2Letter();
		} else if (WEST_VIRGINIA.getFipsCode().equals(fipsCode)) {
			return WEST_VIRGINIA.getState2Letter();
		} else if (WISCONSIN.getFipsCode().equals(fipsCode)) {
			return WISCONSIN.getState2Letter();
		} else if (WYOMING.getFipsCode().equals(fipsCode)) {
			return WYOMING.getState2Letter();
		} else if (AMERICAN_SAMOA.getFipsCode().equals(fipsCode)) {
			return AMERICAN_SAMOA.getState2Letter();
		} else if (GUAM.getFipsCode().equals(fipsCode)) {
			return GUAM.getState2Letter();
		} else if (NORTHERN_MARIANA_ISLANDS.getFipsCode().equals(fipsCode)) {
			return NORTHERN_MARIANA_ISLANDS.getState2Letter();
		} else if (PUERTO_RICO.getFipsCode().equals(fipsCode)) {
			return PUERTO_RICO.getState2Letter();
		} else if (VIRGIN_ISLANDS.getFipsCode().equals(fipsCode)) {
			return VIRGIN_ISLANDS.getState2Letter();
		} else {
			return "";
		}
	}
}
