package edu.gatech.chai.hl7.v2.parser.fhir;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Composition.CompositionStatus;
import org.hl7.fhir.r4.model.Composition.SectionComponent;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Enumerations.MessageEvent;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.MessageHeader;
import org.hl7.fhir.r4.model.MessageHeader.MessageDestinationComponent;
import org.hl7.fhir.r4.model.MessageHeader.MessageSourceComponent;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Observation.ObservationComponentComponent;
import org.hl7.fhir.r4.model.Observation.ObservationReferenceRangeComponent;
import org.hl7.fhir.r4.model.Observation.ObservationStatus;
import org.hl7.fhir.r4.model.Quantity.QuantityComparator;
import org.hl7.fhir.r4.model.ServiceRequest.ServiceRequestIntent;
import org.hl7.fhir.r4.model.ServiceRequest.ServiceRequestStatus;
import org.hl7.fhir.r4.model.codesystems.ObservationCategory;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Range;
import org.hl7.fhir.r4.model.Ratio;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.SimpleQuantity;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.r4.model.UriType;
import org.hl7.fhir.r4.model.Address.AddressUse;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Varies;
import ca.uhn.hl7v2.model.v23.datatype.CE;
import ca.uhn.hl7v2.model.v23.datatype.CM_MSG;
import ca.uhn.hl7v2.model.v23.datatype.CX;
import ca.uhn.hl7v2.model.v23.datatype.EI;
import ca.uhn.hl7v2.model.v23.datatype.FT;
import ca.uhn.hl7v2.model.v23.datatype.HD;
import ca.uhn.hl7v2.model.v23.datatype.ID;
import ca.uhn.hl7v2.model.v23.datatype.IS;
import ca.uhn.hl7v2.model.v23.datatype.NM;
import ca.uhn.hl7v2.model.v23.datatype.ST;
import ca.uhn.hl7v2.model.v23.datatype.TS;
import ca.uhn.hl7v2.model.v23.datatype.TX;
import ca.uhn.hl7v2.model.v23.group.ORU_R01_OBSERVATION;
import ca.uhn.hl7v2.model.v23.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v23.group.ORU_R01_PATIENT;
import ca.uhn.hl7v2.model.v23.group.ORU_R01_RESPONSE;
import ca.uhn.hl7v2.model.v23.segment.MSH;
import ca.uhn.hl7v2.model.v23.segment.NTE;
import ca.uhn.hl7v2.model.v23.segment.OBR;
import ca.uhn.hl7v2.model.v23.segment.OBX;
import ca.uhn.hl7v2.model.v23.datatype.ED;
import ca.uhn.hl7v2.model.v23.datatype.XAD;
import ca.uhn.hl7v2.model.v23.datatype.DT;
import ca.uhn.hl7v2.model.v23.datatype.SN;
import ca.uhn.hl7v2.model.v23.datatype.DR;
import ca.uhn.hl7v2.model.v23.datatype.CQ;
import ca.uhn.hl7v2.model.v23.datatype.XCN;
import edu.gatech.chai.hl7.v2.parser.fhir.utilities.V2FHIRCodeSystem;

public class HL7v23FhirR4Parser extends BaseHL7v2FHIRParser {
	// Logger setup
	final static Logger LOGGER = Logger.getLogger(HL7v23FhirR4Parser.class.getName());

	MessageHeader messageHeader = null;
	Composition composition = null;

	List<Resource> resources;

	private void initialize(Message msg) {
		mapMessageHeader((ca.uhn.hl7v2.model.v23.message.ORU_R01) msg);
		mapComposition((ca.uhn.hl7v2.model.v23.message.ORU_R01) msg);
	}

	public HL7v23FhirR4Parser() {
	}

	private Reference addToBundle(Bundle bundle, Resource resource, String resourceFullUrl) {
		Reference ret;

		resource.setId(new IdType(resourceFullUrl.substring(9)));

		BundleEntryComponent bundleEntry = new BundleEntryComponent();
		bundleEntry.setResource(resource);
		bundleEntry.setFullUrl(resourceFullUrl);

		ret = new Reference(resourceFullUrl);
		// messageHeader.addFocus(ret);

		// Add composition section entry
		SectionComponent compositionSection = composition.getSectionFirstRep();
		compositionSection.addEntry(ret);

		bundle.addEntry(bundleEntry);

		resources.add(resource);

		return ret;
	}

	private boolean areIdentifiersSame(List<Identifier> ids1, List<Identifier> ids2) {
		if (ids1.size() == 0 || ids2.size() == 0) {
			return false;
		}

		for (Identifier id1 : ids1) {
			List<Coding> codings1 = id1.getType().getCoding();
			for (Coding coding1 : codings1) {
				for (Identifier id2 : ids2) {
					List<Coding> codings2 = id2.getType().getCoding();
					for (Coding coding2 : codings2) {
						if ((coding1.getSystem() == null && coding2.getSystem() == null) ||
								coding1.getSystem().equals(coding2.getSystem()) &&
										(coding1.getCode() == null && coding2.getCode() == null)
								||
								coding1.getCode().equals(coding2.getCode())) {
							if (id1.getValue().equals(id2.getValue())) {
								return true;
							}
						}
					}
				}
			}

			if (codings1.size() == 0) {
				for (Identifier id2 : ids2) {
					if (id2.getType().getCoding().size() == 0) {
						if (id1.getValue().equals(id2.getValue())) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	private Resource exists(Resource resource, List<Identifier> identifierResource) {
		for (Resource resourceExist : resources) {
			if (resourceExist.getClass().isInstance(resource)) {
				Method getIdentifierMethod;
				try {
					getIdentifierMethod = resourceExist.getClass().getMethod("getIdentifier");

					@SuppressWarnings("unchecked")
					List<Identifier> identifiersExist = (List<Identifier>) getIdentifierMethod.invoke(resourceExist);

					if (areIdentifiersSame(identifierResource, identifiersExist)) {
						return resourceExist;
					}
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	public List<IBaseBundle> executeParser(Message msg) {
		ca.uhn.hl7v2.model.v23.message.ORU_R01 oruR01Message = (ca.uhn.hl7v2.model.v23.message.ORU_R01) msg;
		List<IBaseBundle> bundles = new ArrayList<IBaseBundle>();
		Patient subject = null;

		// First clear up the list.
		initialize(msg);
		resources = new ArrayList<Resource>();

		int numberOfResponses = oruR01Message.getRESPONSEReps();
		for (int i = 0; i < numberOfResponses; i++) {
			/*
			 * Each PATIENT_RESULT generates one Message Bundle. This message bundle will be
			 * sent or written separately.
			 */
			Bundle messageBundle = new Bundle();
			messageBundle.setType(Bundle.BundleType.MESSAGE);
			messageBundle.setId(UUID.randomUUID().toString());
			messageBundle.setTimestamp(new Date());

			// set up message header ID and bundle entry with fullURL
			String messageHeaderId = UUID.randomUUID().toString();
			messageHeader.setId(messageHeaderId);
			BundleEntryComponent bundleEntryMessageHeader = new BundleEntryComponent();
			bundleEntryMessageHeader.setResource(messageHeader);
			bundleEntryMessageHeader.setFullUrl("urn:uuid:" + messageHeaderId);
			messageBundle.addEntry(bundleEntryMessageHeader);

			// Document bundle - this is main bundle. Add this to messageBundle.entry
			Bundle documentBundle = new Bundle();
			documentBundle.setType(Bundle.BundleType.DOCUMENT);
			String documenteBundleId = UUID.randomUUID().toString();
			documentBundle.setId(documenteBundleId);
			documentBundle.setTimestamp(new Date());
			BundleEntryComponent bundleEntryDocument = new BundleEntryComponent();
			bundleEntryDocument.setResource(documentBundle);
			bundleEntryDocument.setFullUrl("urn:uuid:" + documenteBundleId);

			// add document bundle reference to messageHeader.focus. And, Document resource
			// to message bundle entry
			messageHeader.addFocus(new Reference("urn:uuid:" + documenteBundleId));
			messageBundle.addEntry(bundleEntryDocument);

			// set up composition id and bundle entry with full URL
			String compositionId = UUID.randomUUID().toString();
			composition.setId(compositionId);
			BundleEntryComponent bundleEntryComposition = new BundleEntryComponent();
			bundleEntryComposition.setResource(composition);
			bundleEntryComposition.setFullUrl("urn:uuid:" + compositionId);
			documentBundle.addEntry(bundleEntryComposition);

			ORU_R01_RESPONSE patientResult = oruR01Message.getRESPONSE(i);

			subject = mapPatients(patientResult);
			String patientReference = null;
			if (subject != null) {
				Patient patientExist = (Patient) exists(subject, subject.getIdentifier());
				if (patientExist == null) {
					// Save this patientReference for a future use
					patientReference = "urn:uuid:" + UUID.randomUUID().toString();
					addToBundle(documentBundle, subject, patientReference);
				} else {
					patientReference = "urn:uuid:" + patientExist.getIdElement().getIdPart();
					subject = patientExist;
				}
			} else {
				// We must have a patient.
				return null;
			}

			composition.setSubject(new Reference(patientReference));

			// For each message bundle, add diagnostic report, observations, specimens.
			int totalNumOfOrderObservation = patientResult.getORDER_OBSERVATIONReps();
			for (int j = 0; j < totalNumOfOrderObservation; j++) {
				ORU_R01_ORDER_OBSERVATION orderObservation = patientResult.getORDER_OBSERVATION(j);

				// Add DiagnosticReport
				DiagnosticReport diagnosticReport = mapDiagnosticReport(orderObservation, patientReference);
				if (diagnosticReport != null) {
					DiagnosticReport diagnosticReportExist = (DiagnosticReport) exists(diagnosticReport,
							diagnosticReport.getIdentifier());
					if (diagnosticReportExist == null) {
						addToBundle(documentBundle, diagnosticReport, "urn:uuid:" + UUID.randomUUID().toString());

						// TODO: This diagnostic report may need to be inserted to another bundle like
						// document.
					} else {
						// overwrite the diagnosticReport with existing one.
						diagnosticReport = diagnosticReportExist;
					}
				}

				// Create ServiceRequest here.
				ServiceRequest serviceRequest = mapServiceRequest(orderObservation, patientReference);
				Reference serviceRequestReference = null;
				if (diagnosticReport != null) {
					ServiceRequest serviceRequestExist = (ServiceRequest) exists(serviceRequest,
							serviceRequest.getIdentifier());
					if (serviceRequestExist == null) {
						serviceRequestReference = addToBundle(documentBundle, serviceRequest,
								"urn:uuid:" + UUID.randomUUID().toString());
						diagnosticReport.addBasedOn(serviceRequestReference);
					} else {
						// Overwrite the serviceRequest with this existing one as this serviceRequest is
						// used in other places.
						serviceRequest = serviceRequestExist;
						serviceRequestReference = new Reference(
								"urn:uuid:" + serviceRequestExist.getIdElement().getIdPart());
					}
				}

				// Add Practitioner (for ordering provider).
				// OBR-16: Ordering provider. This needs to be in ServiceRequest. And,
				// ServiceRequest
				// only allows 1 requester. So, for now, we just take the first one.
				// TODO: monitor the mapping.
				int totalNumOfOrderingPartner = orderObservation.getOBR().getObr16_OrderingProviderReps();
				if (totalNumOfOrderingPartner > 0) {
					XCN orderingProvider = orderObservation.getOBR().getObr16_OrderingProvider(0);
					Practitioner practitioner = getPractitionerFromXCN(orderingProvider, patientReference);
					Practitioner practitionerExist = (Practitioner) exists(practitioner, practitioner.getIdentifier());
					if (practitionerExist == null) {
						Reference practitionerReference = addToBundle(documentBundle, practitioner,
								"urn:uuid:" + UUID.randomUUID().toString());
						serviceRequest.setRequester(practitionerReference);
					}
				}

				// Add Observation.
				List<Observation> returnedObservations = mapObservations(orderObservation, patientReference,
						documentBundle);
				for (Observation observation : returnedObservations) {
					Reference observationReference = null;
					Observation observationExist = (Observation) exists(observation, observation.getIdentifier());
					if (observationExist == null) {
						observationReference = addToBundle(documentBundle, observation,
								"urn:uuid:" + UUID.randomUUID().toString());
						// Add result to diagnoticReport if exists.
						if (diagnosticReport != null) {
							diagnosticReport.addResult(observationReference);
						}
					}
				}

				// Add Specimen - 2.3 DOES NOT SUPPORT SPECIMEN
			}
			bundles.add(messageBundle);
		}

		return bundles;

	}

	private Patient mapPatients(ORU_R01_RESPONSE response) {
		Patient patient = new Patient();
		try {
			ORU_R01_PATIENT patientHL7 = response.getPATIENT();
			ca.uhn.hl7v2.model.v23.segment.PID pid = patientHL7.getPID();

			patient.setId(UUID.randomUUID().toString());

			// PID-2 to patient ID.
			// This is the accession number of NMS' client.
			CX pid2 = pid.getPid2_PatientIDExternalID();
			if (pid2 != null && !pid2.isEmpty()) {
				ST id = pid2.getCx1_ID();
				if (id != null && !id.isEmpty()) {
					// patient.setId(id.getValue());
					Identifier identifier = new Identifier();
					identifier.setSystem("External_Patient_ID");
					identifier.setValue(id.getValue());

					// Set the type to custom CMS type.
					CodeableConcept typeCodeableConcept = new CodeableConcept();
					Coding typeCoding = new Coding("urn:mdi:temporary:code", "1000007", "Case Number");
					typeCodeableConcept.addCoding(typeCoding);
					identifier.setType(typeCodeableConcept);

					patient.addIdentifier(identifier);
				} else {
					// PID2 is required for NMS outbound message.
					return null;
				}
			}

			// PID-3 to patient.identifier.
			// This is NMS Workorder number assigned by NMS
			int numberOfPid3 = pid.getPid3_PatientIDInternalIDReps();
			for (int j = 0; j < numberOfPid3; j++) {
				ca.uhn.hl7v2.model.v23.datatype.CX pid3 = pid.getPid3_PatientIDInternalID(j);
				ca.uhn.hl7v2.model.v23.datatype.ST id = pid3.getCx1_ID();
				Identifier identifier = new Identifier();
				if (id != null && !id.isEmpty()) {
					identifier.setValue(id.getValue());
				}

				ca.uhn.hl7v2.model.v23.datatype.IS identifierTypeCode = pid3.getCx5_IdentifierTypeCode();
				if (identifierTypeCode != null && !identifierTypeCode.isEmpty()) {
					CodeableConcept codeableConcept = new CodeableConcept();
					Coding coding = new Coding();
					coding.setSystem("http://hl7.org/fhir/v2/0203");
					coding.setCode(identifierTypeCode.getValue());
					codeableConcept.addCoding(coding);
					identifier.setType(codeableConcept);
				}

				if (!identifier.isEmpty()) {
					patient.addIdentifier(identifier);
				}
			}

			// PID-5, PID-9 to patient.name
			int numberOfPid5 = pid.getPid5_PatientNameReps();
			for (int k = 0; k < numberOfPid5; k++) {
				ca.uhn.hl7v2.model.v23.datatype.XPN pid5 = pid.getPid5_PatientName(k);
				if (pid5 != null && !pid5.isEmpty()) {
					HumanName humanName = new HumanName();
					ca.uhn.hl7v2.model.v23.datatype.ST familyName = pid5.getFamilyName();
					if (familyName != null && !familyName.isEmpty()) {
						humanName.setFamily(familyName.getValue());
					}
					ca.uhn.hl7v2.model.v23.datatype.ST givenName = pid5.getGivenName();
					if (givenName != null && !givenName.isEmpty()) {
						humanName.addGiven(givenName.getValue());
					}

					patient.addName(humanName);
					break;
				}
			}

			// PID-7 to patient.birthDate
			TS pid7 = pid.getPid7_DateOfBirth();
			if (pid7 != null && !pid7.isEmpty()) {
				Date dob = pid7.getTs1_TimeOfAnEvent().getValueAsDate();
				patient.setBirthDate(dob);
			}

			// PID-8 to patient.gender
			ca.uhn.hl7v2.model.v23.datatype.IS pid8 = pid.getPid8_Sex();
			if (pid8 != null && !pid8.isEmpty()) {
				AdministrativeGender adminstrativeGender;
				if (pid8.getValue().equals("F")) {
					adminstrativeGender = AdministrativeGender.FEMALE;
				} else if (pid8.getValue().equals("M")) {
					adminstrativeGender = AdministrativeGender.MALE;
				} else if (pid8.getValue().equals("O")) {
					adminstrativeGender = AdministrativeGender.OTHER;
				} else {
					adminstrativeGender = AdministrativeGender.UNKNOWN;
				}
				patient.setGender(adminstrativeGender);
			}

			// PID-10 Race
			IS racePid10 = pid.getPid10_Race();
			if (!racePid10.isEmpty()) {
				Extension raceExt = new Extension();
				raceExt.setUrl("urn:mmg:cdc-race");

				String textValue = racePid10.getValue();
				Extension raceTextExt = new Extension();
				raceTextExt.setUrl("text");
				raceTextExt.setValue(new StringType(textValue));
				raceExt.addExtension(raceTextExt);

				patient.addExtension(raceExt);
			}

			// PID-11: Address
			int totalNumOfAddresses = pid.getPid11_PatientAddressReps();
			for (int i = 0; i < totalNumOfAddresses; i++) {
				XAD addressPid = pid.getPid11_PatientAddress(i);
				Address address = getAddressFromXAD(addressPid);
				if (address != null) {
					address.setUse(AddressUse.HOME);
					patient.addAddress(address);
				}
			}

			// PID-22 Ethnicity
			// FHIR can handle only one Race.
			IS ethnicPid22 = pid.getPid22_EthnicGroup();
			if (!ethnicPid22.isEmpty()) {
				Extension ethnicExt = new Extension();
				ethnicExt.setUrl("urn:mmg:cdc-ethnicity");

				String textValue = ethnicPid22.getValue();
				Extension ethnicTextExt = new Extension();
				ethnicTextExt.setUrl("text");
				ethnicTextExt.setValue(new StringType(textValue));
				ethnicExt.addExtension(ethnicTextExt);

				patient.addExtension(ethnicExt);
			}

		} catch (HL7Exception e) {
			e.printStackTrace();
		}

		return patient;
	}

	public ServiceRequest mapServiceRequest(ORU_R01_ORDER_OBSERVATION orderObservation, String subjectReference) {
		OBR obr = orderObservation.getOBR();
		try {
			if (obr.isEmpty()) {
				return null;
			}
		} catch (HL7Exception e1) {
			e1.printStackTrace();
			return null;
		}

		ServiceRequest serviceRequest = new ServiceRequest();

		// set static values.
		serviceRequest.setStatus(ServiceRequestStatus.COMPLETED);
		serviceRequest.setIntent(ServiceRequestIntent.ORDER);

		// If OBR-31 is available, add them to reasonCode.
		int totalNumOfReasonForStudies = obr.getObr31_ReasonForStudyReps();
		for (int i = 0; i < totalNumOfReasonForStudies; i++) {
			try {
				CE obrReason = obr.getObr31_ReasonForStudy(i);
				if (obrReason.isEmpty()) {
					continue;
				}

				CodeableConcept reasonCode = getCodeableConceptFromCE(obrReason);
				serviceRequest.addReasonCode(reasonCode);
			} catch (HL7Exception e) {
				e.printStackTrace();
			}
		}

		return serviceRequest;
	}

	public DiagnosticReport mapDiagnosticReport(ORU_R01_ORDER_OBSERVATION orderObservation, String subjectReference) {
		DiagnosticReport diagnosticReport = new DiagnosticReport();
		OBR obr = orderObservation.getOBR();

		try {
			if (obr.isEmpty()) {
				// if we have nothing in OBR, then we return null.
				return null;
			}

			// Set the patient.
			Reference patientReference = new Reference(subjectReference);
			diagnosticReport.setSubject(patientReference);

			// OBR 2: Placer Order Number
			EI obr_2 = obr.getObr2_PlacerOrderNumber(0);
			if (!obr_2.isEmpty()) {
				Identifier identifier1 = getIdentifierFromEI(obr_2, "http://terminology.hl7.org/CodeSystem/v2-0203",
						"PLAC");
				if (identifier1 != null) {
					diagnosticReport.addIdentifier(identifier1);
				}
			}

			// OBR-3: Filter Order Number
			EI obr_3 = obr.getObr3_FillerOrderNumber();
			if (!obr_3.isEmpty()) {
				Identifier identifier2 = getIdentifierFromEI(obr_3, "http://terminology.hl7.org/CodeSystem/v2-0203",
						"FILL");
				if (identifier2 != null) {
					diagnosticReport.addIdentifier(identifier2);
				}
			}

			// OBR-4: Universal Service Identifier. In MMG Lab, Test Ordered Name
			if (!obr.getObr4_UniversalServiceIdentifier().isEmpty()) {
				CodeableConcept codeCodeable = getCodeableConceptFromCE(obr.getObr4_UniversalServiceIdentifier());
				diagnosticReport.setCode(codeCodeable);
			}

			// OBR-7: Observation Date/Time. In MMG Lab, The clinically relevant date/time
			// of the observation
			TS timeValue = obr.getObr7_ObservationDateTime();
			if (!timeValue.isEmpty()) {
				diagnosticReport.setEffective(new DateTimeType(timeValue.getTs1_TimeOfAnEvent().getValueAsDate()));
			}

			// OBR-11: Specimen Action Code. In MMG Lab, The action to be taken with respect
			// to the specimens
			// that accompany or precede this order
			if (!obr.getObr11_SpecimenActionCode().isEmpty()) {
				Extension specimenActionExt = new Extension();
				specimenActionExt.setUrl("http://terminology.hl7.org/CodeSystem/v2-0065");
				specimenActionExt.setValue(new CodeType(obr.getObr11_SpecimenActionCode().getValue()));
				diagnosticReport.addExtension(specimenActionExt);
			}

			// OBR-16: Ordering Provider: We map this to Practitioner. So,
			// this will be handled separately.

			// OBR-22: Results Rpt/Status Chng - Date/Time
			if (!obr.getObr22_ResultsRptStatusChngDateTime().isEmpty()) {
				diagnosticReport
						.setIssued(obr.getObr22_ResultsRptStatusChngDateTime().getTs1_TimeOfAnEvent().getValueAsDate());
			}

			// OBR-25: Result Status. In MMG Lab, The status of results for this order
			if (!obr.getObr25_ResultStatus().isEmpty()) {
				switch (obr.getObr25_ResultStatus().getValue()) {
					case "F":
						diagnosticReport.setStatus(DiagnosticReportStatus.FINAL);
						break;
					case "A":
						diagnosticReport.setStatus(DiagnosticReportStatus.PARTIAL);
						break;
					case "P":
						diagnosticReport.setStatus(DiagnosticReportStatus.PRELIMINARY);
						break;
					case "C":
						diagnosticReport.setStatus(DiagnosticReportStatus.CORRECTED);
						break;
					default:
						diagnosticReport.setStatus(DiagnosticReportStatus.FINAL);
				}
			}

			// OBR-26 & 29 are skipped as v2-fhir hasn't mapped it.

			// OBR-31: Reason for Study
			// This will be mapped with ServiceRequest

		} catch (DataTypeException e) {
			e.printStackTrace();
		} catch (HL7Exception e) {
			e.printStackTrace();
		}

		return diagnosticReport;
	}

	private Quantity setUnitQuantity(Quantity quantity, String system, String code, String unit) {
		if (quantity == null) {
			quantity = new Quantity();
		}

		if (system != null && !system.isEmpty()) {
			quantity.setSystem(V2FHIRCodeSystem.getFhirFromV2(system));
		}

		if (unit != null && !unit.isEmpty()) {
			quantity.setUnit(unit);
		}

		if (code != null && !code.isEmpty()) {
			quantity.setCode(code);
		}

		return quantity;
	}

	public List<Observation> mapObservations(ORU_R01_ORDER_OBSERVATION orderObservation, String subjectReference,
			Bundle bundle) {
		// Mapping Document:
		// https://confluence.icl.gtri.org/pages/viewpage.action?pageId=22678246#NMStoFHIR(HL7v2.3toFHIR)-ForensicFormatHL7v2.3Specification

		List<Observation> retVal = new ArrayList<Observation>();

		try {
			// OBR obr = orderObservation.getOBR();

			// CE obr4 = obr.getObr4_UniversalServiceIdentifier();
			// String serviceDesc = null;
			// if (obr4 != null && !obr4.isEmpty()) {
			// ST serviceDescST = obr4.getCe2_Text();
			// if (serviceDescST != null && !serviceDescST.isEmpty()) {
			// serviceDesc = serviceDescST.getValue();
			// }
			// }

			int totalNumberOfObservation = orderObservation.getOBSERVATIONReps();
			for (int i = 0; i < totalNumberOfObservation; i++) {
				Observation observation = new Observation();

				// if (serviceDesc != null) {
				// Identifier identifier = new Identifier();
				// identifier.setValue(serviceDesc);
				// observation.addIdentifier(identifier);
				// }

				CodeableConcept typeConcept = new CodeableConcept();

				// This is Lab result
				Coding typeCoding = new Coding("http://hl7.org/fhir/observation-category", "laboratory", "");
				typeConcept.addCoding(typeCoding);
				observation.addCategory(typeConcept);

				ORU_R01_OBSERVATION hl7Observation = orderObservation.getOBSERVATION(i);

				// Set the subject from the patient ID.
				// IdType IdType = new IdType("Patient", subject.getId());
				Reference reference = new Reference(subjectReference);
				observation.setSubject(reference);

				OBX obx = hl7Observation.getOBX();

				// OBX-3: Test Performed Name. In MMG lab, The lab test or analysis
				// that was performed on the specimen (also referred as "Resulted Test Name")
				CE obx3 = obx.getObx3_ObservationIdentifier();
				CodeableConcept codeableConcept = getCodeableConceptFromCE(obx3);
				observation.setCode(codeableConcept);

				// OBX-4: Observation Sub-ID. In MMG lab, Distinguishes
				// between multiple OBX segments with the same observation ID organized
				// under one OBR.
				// In MMG this is a sub-id for multipble OBXes.
				ST obx4 = obx.getObx4_ObservationSubID();
				if (!obx4.isEmpty()) {
					Extension subIdExt = new Extension();
					subIdExt.setUrl("ext-sub-id");
					subIdExt.setValue(new StringType(obx4.getValue()));
					observation.addExtension(subIdExt);
				}

				// OBX-6: Units. If we have OBX-6 value, it means the value is Quantity.
				// However, value type will choose the FHIR data type. We just parse
				// OBX-6 first so that we have this unit available for all v2 type need
				// the unit.
				CE unit = obx.getObx6_Units();
				String unitString = new String();
				String unitCodeString = new String();
				String unitSystemString = new String();
				if (!unit.isEmpty()) {
					ID id = unit.getCe1_Identifier();
					if (id != null && !id.isEmpty()) {
						unitString = id.getValue().replace("mcg", "ug").replace(" Creat", "{creat}");
						unitCodeString = id.getValue().replace("mcg", "ug").replace(" Creat", "{creat}");
					}

					ST system = unit.getCe3_NameOfCodingSystem();
					if (system != null && !system.isEmpty()) {
						unitSystemString = system.getValue();
					}
				}

				// OBX-2: Value Type. This is used for observation.value[x] and
				// observation.referenceRange
				// OBX-5: Observation Value. This has result value. Parsed based on the value
				// type.
				ID valueType = obx.getObx2_ValueType();
				int totalNumOfObx5 = obx.getObx5_ObservationValueReps();

				for (int j = 0; j < totalNumOfObx5; j++) {
					Type obsValue = null;
					boolean addFhirValue = true;
					Varies observationValue = obx.getObx5_ObservationValue(j);
					if (valueType.getValue().equals("NM")) {
						// This should be valueQuantity
						// obx5 is the ST value in NMS message
						Quantity quantity = new Quantity();
						NM numericValue = (NM) observationValue.getData();
						quantity.setValue(Double.parseDouble(numericValue.getValue()));

						// Set the unit if available from OBX-6
						if (!unitString.isEmpty()) {
							quantity.setUnit(unitString);
							quantity.setCode(unitCodeString);
						}
						if (!unitSystemString.isEmpty()) {
							quantity.setSystem(unitSystemString);
						}

						obsValue = quantity;
					} else if (valueType.getValue().equals("CE")) {
						CE ceValue = (CE) observationValue.getData();

						obsValue = getCodeableConceptFromCE(ceValue);
					} else if (valueType.getValue().equals("TS")) {
						TS timeValue = (TS) observationValue.getData();

						obsValue = new DateTimeType(timeValue.getTs1_TimeOfAnEvent().getValueAsDate());
					} else if (valueType.getValue().equals("SN")) {
						SN snValue = (SN) observationValue.getData();

						Quantity quantity1 = new Quantity();
						Quantity quantity2 = new Quantity();

						NM sn1 = snValue.getNum1();
						NM sn2 = snValue.getNum2();

						if (!sn1.isEmpty()) {
							quantity1.setValue(Double.parseDouble(sn1.getValue()));
						}

						if (!sn2.isEmpty()) {
							quantity2.setValue(Double.parseDouble(sn2.getValue()));
						}

						ST comparator = snValue.getComparator();
						ST separatorSuffix = snValue.getSeparatorOrSuffix();

						if (!comparator.isEmpty()) {
							quantity1.setComparator(QuantityComparator.valueOf(comparator.getValue()));
							quantity2.setComparator(QuantityComparator.valueOf(comparator.getValue()));
						}
						if (":".equals(separatorSuffix.getValue()) || "/".equals(separatorSuffix.getValue())) {
							// valueRatio
							Ratio ratio = new Ratio();
							ratio.setNumerator(quantity1);
							ratio.setDenominator(quantity2);

							obsValue = ratio;
						} else if ("-".equals(separatorSuffix.getValue())) {
							// This is range.
							Range range = new Range();
							range.setLow(quantity1);
							range.setHigh(quantity2);

							obsValue = range;
						} else if ("+".equals(separatorSuffix.getValue())) {
							// String combination
							StringType strType = new StringType(comparator.getValue() + " " + quantity1.getValue() + " "
									+ separatorSuffix.getValue() + " " + quantity2.getValue());

							obsValue = strType;
						} else {
							obsValue = quantity1;
						}
					} else if (valueType.getValue().equals("DT")) {
						DT dtValue = (DT) observationValue.getData();

						obsValue = new StringType(dtValue.getValue());
					} else if (valueType.getValue().equals("ED")) {
						ED edValue = (ED) observationValue.getData();

						ID dataType = edValue.getEd2_TypeOfData();
						ID encodingType = edValue.getEd4_Encoding();
						ST data = edValue.getEd5_Data();

						Attachment attachment = new Attachment();
						if (!dataType.isEmpty()) {
							attachment.setContentType(dataType.getValue());
						}

						if (!encodingType.isEmpty()) {
							if ("Base64".equalsIgnoreCase(encodingType.getValue())) {
								if (!data.isEmpty()) {
									attachment.setData(data.getValue().getBytes());
								}
							}
						}

						if (!attachment.isEmpty()) {
							Extension edExt = new Extension();
							edExt.setUrl("http://hl7.org/StructureDefinition/ext-valueAttachment");
							edExt.setValue(attachment);
							observation.addExtension(edExt);
						}
						addFhirValue = false;

					} else if (valueType.getValue().equals("CX")) {
						// CX maps to identifier. CX does not have enough information to create
						// organization. So, we consider this as a system in the identifier.
						Identifier identifier = new Identifier();
						CX cxValue = (CX) observationValue.getData();
						ST cx1IdNum = cxValue.getCx1_ID();
						if (!cx1IdNum.isEmpty()) {
							identifier.setValue(cx1IdNum.getValue());
						}

						ST cx2CheckDigit = cxValue.getCx2_CheckDigit();
						if (!cx2CheckDigit.isEmpty()) {
							Extension ext = new Extension();
							ext.setUrl("ext-checkDigit");
							ext.setValue(new StringType(cx2CheckDigit.getValue()));
							identifier.addExtension(ext);
						}

						ID cx3CheckDigistScheme = cxValue.getCx3_CodeIdentifyingTheCheckDigitSchemeEmployed();
						if (!cx3CheckDigistScheme.isEmpty()) {
							Extension ext = new Extension();
							ext.setUrl("checkDigit");
							ext.setValue(new StringType(cx3CheckDigistScheme.getValue()));
							identifier.addExtension(ext);
						}

						HD cx4AssignAuth = cxValue.getCx4_AssigningAuthority();
						if (!cx4AssignAuth.isEmpty()) {
							identifier.setSystemElement(getUriFromHD(cx4AssignAuth));
						}

						IS cx5IdTypeCode = cxValue.getCx5_IdentifierTypeCode();
						if (!cx5IdTypeCode.isEmpty()) {
							CodeableConcept cx5CodeableConcept = new CodeableConcept();
							Coding cx5TypeCoding = cx5CodeableConcept.getCodingFirstRep();
							cx5TypeCoding.setCode(cx5IdTypeCode.getValue());
							identifier.setType(cx5CodeableConcept);
						}

						HD cx6AssignFac = cxValue.getCx6_AssigningFacility();
						if (cx6AssignFac.isEmpty()) {
							Location location = getLocationFromHD(cx6AssignFac);
							String locationRefId = "urn:uuid:" + UUID.randomUUID().toString();
							Reference locationRef = addToBundle(bundle, location, locationRefId);
							Extension ext = new Extension();
							ext.setUrl("ext-assigningFacility");
							ext.setValue(locationRef);
							identifier.addExtension(ext);
						}

						addFhirValue = false;
					} else { // ST and TX - we treat both ST and TX as String
						StringType valueString = null;
						if (valueType.getValue().equals("ST")) {
							ST stringValue = (ST) observationValue.getData();
							valueString = new StringType(stringValue.getValue());
						} else {
							TX textValue = (TX) observationValue.getData();
							valueString = new StringType(textValue.getValue());
						}

						obsValue = valueString;
					}

					// If we only have one value, we use observation.value. If more than one,
					// we use component.
					if (addFhirValue == true) {
						if (totalNumOfObx5 > 1) {
							// Component
							ObservationComponentComponent obsCompComp = new ObservationComponentComponent();
							obsCompComp.setCode(observation.getCode());
							obsCompComp.setValue(obsValue);
							observation.addComponent(obsCompComp);
						} else {
							observation.setValue(obsValue);
						}
					}
				}

				// OBX-7: References Range. In MMG lab, Identifies the upper and lower limits or
				// bounds of test result values
				ST obx7 = obx.getObx7_ReferencesRange();
				if (!obx7.isEmpty()) {
					String obx7StringValue = obx7.getValue();
					ObservationReferenceRangeComponent referenceRangeComponent = new ObservationReferenceRangeComponent();

					try {
						double obx7Value = Double.parseDouble(obx7StringValue);
						SimpleQuantity simpleQuantity = new SimpleQuantity();
						simpleQuantity.setValue(obx7Value);
						if (!unitString.isEmpty()) {
							simpleQuantity.setUnit(unitString);
							simpleQuantity.setCode(unitCodeString);
						}
						if (!unitSystemString.isEmpty()) {
							simpleQuantity.setSystem(unitSystemString);
						}
						referenceRangeComponent.setHigh(simpleQuantity);
					} catch (NumberFormatException nfe) {
						referenceRangeComponent.setText(obx7StringValue);
					}

					observation.addReferenceRange(referenceRangeComponent);
				}

				// OBX-8: Abnormal Flags. In MMG lab, Abnormal flags used for
				// laboratory result interpretation by the lab (not epidemiologist's
				// interpretation).
				// The interpretation flag identifies a result that is not typical
				// as well as how its not typical. Examples: susceptible, resistant, normal,
				// above
				// upper panic limits, below absolute low.
				int totalNumOfAbnormalFlag = obx.getObx8_AbnormalFlagsReps();
				for (int j = 0; j < totalNumOfAbnormalFlag; j++) {
					ID abnormalFlag = obx.getObx8_AbnormalFlags(j);
					if (!abnormalFlag.isEmpty()) {
						observation.addInterpretation(new CodeableConcept(
								new Coding("urn:oid:2.16.840.1.113883.12.78", abnormalFlag.getValue(), "")));
					}
				}

				// observation.status from OBX-11
				ID obx11 = obx.getObx11_ObservResultStatus();
				if (obx11 != null && !obx11.isEmpty()) {
					String hl7Status = obx11.getValue();
					if ("F".equals(hl7Status)) {
						// Final Result.
						observation.setStatus(ObservationStatus.FINAL);
					} else if ("C".equals(hl7Status)) {
						// Record coming over is a correction and thus replaces a final result.
						observation.setStatus(ObservationStatus.AMENDED);
					} else if ("X".equals(hl7Status)) {
						// Record coming over is a correction and thus replaces a final result.
						observation.setStatus(ObservationStatus.CANCELLED);
					} else if ("P".equals(hl7Status)) {
						// Record coming over is a correction and thus replaces a final result.
						observation.setStatus(ObservationStatus.PRELIMINARY);
					} else {
						LOGGER.info("OBX received with status = " + hl7Status + ". (Table:" + obx11.getTable() + ")");
						observation.setStatus(ObservationStatus.UNKNOWN);
					}
				}

				// OBX-14: Specimen Collection Date. In MMG lab, Date/time of observation in OBX
				// segment for ELR infers
				// the specimen collection date
				try {
					TS observationDateTime = obx.getObx14_DateTimeOfTheObservation();
					DateTimeType dateTimeType = new DateTimeType(
							observationDateTime.getTs1_TimeOfAnEvent().getValueAsDate());
					observation.setEffective(dateTimeType);
				} catch (DataTypeException e) {
					e.printStackTrace();
					// we have an error In this case, we have no Effective time.
					// However, it's required in Registry. So, we put new Date(0).
					observation.setEffective(new DateTimeType(new Date(0L)));
				}

				// OBX-17: Observation Method. In MMG lab, The technique or method used to
				// perform the test and
				// obtain the test results (Examples: serum neutralization, titration, dipstick,
				// test strip, anaerobic culture)
				// This is repeating value. Bur, FHIR can handdle only one method.
				int totalNumOfMethods = obx.getObx17_ObservationMethodReps();
				if (totalNumOfMethods > 0) {
					CE methodCE = obx.getObx17_ObservationMethod(0);
					CodeableConcept methodCodeable = getCodeableConceptFromCE(methodCE);
					observation.setMethod(methodCodeable);
				}

				int totalNumberOfNTE = hl7Observation.getNTEReps();
				List<Annotation> annotations = new ArrayList<Annotation>();
				for (int k = 0; k < totalNumberOfNTE; k++) {
					Annotation annotation = new Annotation();
					NTE nte = hl7Observation.getNTE(k);
					if (nte != null && !nte.isEmpty()) {
						ID sourceOfCommentID = nte.getNte2_SourceOfComment();
						if (!sourceOfCommentID.isEmpty()) {
							Extension sourceOfCommentExt = new Extension();
							sourceOfCommentExt.setUrl("http://hl7.org/StructureDefinition/ext-sourceOfComment");
							sourceOfCommentExt.setValue(new Coding("http://terminology.hl7.org/CodeSystem/v2-0105",
									sourceOfCommentID.getValue(), ""));
							annotation.addExtension(sourceOfCommentExt);
						}
						int totalNumberOfNTEComment = nte.getNte3_CommentReps();
						String allComments = new String();
						for (int n = 0; n < totalNumberOfNTEComment; n++) {
							FT nte3 = nte.getNte3_Comment(n);
							if (nte3 != null && !nte3.isEmpty()) {
								ID nte2 = nte.getNte2_SourceOfComment();
								if (!nte2.isEmpty() && "METHOD".equalsIgnoreCase(nte2.getValue())) {
									// NMS specific. We put this in method element in
									// FHIR.
									CodeableConcept methodCodeableConcept = new CodeableConcept();
									methodCodeableConcept.setText(nte3.getValue());
									observation.setMethod(methodCodeableConcept);
								} else {
									allComments.concat(" " + nte3.getValue());
								}
							}
						}

						annotation.setText(allComments.trim());
						annotations.add(annotation);

					}
				}

				if (annotations != null && !annotations.isEmpty()) {
					observation.setNote(annotations);
				}

				retVal.add(observation);
			}

		} catch (HL7Exception e) {
			e.printStackTrace();
			return null;
		}

		return retVal;
	}

	private String getValueOfHD(HD hd) {
		IS hd1 = hd.getHd1_NamespaceID();
		ST hd2 = hd.getHd2_UniversalID();
		ID hd3 = hd.getHd3_UniversalIDType();

		try {
			if (hd2 != null && !hd2.isEmpty()) {
				return hd2.getValue();
			} else if (hd1 != null && !hd1.isEmpty()) {
				return hd1.getValue();
			} else {
				return hd3.getValue();
			}
		} catch (HL7Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Practitioner getPractitionerFromXCN(XCN xcn, String subjectReference) {
		Practitioner practitioner = new Practitioner();

		try {
			if (xcn.isEmpty()) {
				// if we have nothing in OBR, then we return null.
				return null;
			}

			// xcn.1 for ID
			Identifier identifier = new Identifier();
			ST personIdentifier = xcn.getXcn1_IDNumber();
			if (!personIdentifier.isEmpty()) {
				identifier.setValue(personIdentifier.getValue());
				practitioner.addIdentifier(identifier);
			}

			// xcn.2 - 7 name and title
			ST familyName = xcn.getXcn2_FamilyName();
			HumanName name = new HumanName();
			if (!familyName.isEmpty()) {
				name.setFamily(familyName.getValue());
			}

			ST givenName = xcn.getXcn3_GivenName();
			if (!givenName.isEmpty()) {

			}
			name.addGiven(givenName.getValue());

			ST secGivenName = xcn.getXcn4_MiddleInitialOrName();
			if (!secGivenName.isEmpty()) {
				name.addGiven(secGivenName.getValue());
			}

			ST suffix = xcn.getXcn5_SuffixEgJRorIII();
			if (!suffix.isEmpty()) {
				name.addSuffix(suffix.getValue());
			}

			ST prefix = xcn.getXcn6_PrefixEgDR();
			if (!prefix.isEmpty()) {
				name.addPrefix(prefix.getValue());
			}

			ST degree = xcn.getXcn7_DegreeEgMD();
			if (!degree.isEmpty()) {
				name.addPrefix(degree.getValue());
			}

			// XCN.10 for name type.
			ID use = xcn.getXcn10_NameType();
			if (!use.isEmpty()) {
				switch (use.getValue()) {
					case "L":
						name.setUse(NameUse.OFFICIAL);
						break;
					case "M":
						name.setUse(NameUse.MAIDEN);
						break;
					case "N":
						name.setUse(NameUse.NICKNAME);
						break;
					case "S":
						name.setUse(NameUse.ANONYMOUS);
						break;
					default:
						name.setUse(NameUse.USUAL);
				}
			}

			if (!name.isEmpty()) {
				practitioner.addName(name);
			}

			// xcn.9 : Assigning Authority. In MMG Lab, The physician / provider who ordered
			// the test
			if (!xcn.getXcn9_AssigningAuthority().isEmpty()) {
				identifier.addExtension(getExtensionFromHD(xcn.getXcn9_AssigningAuthority(),
						"ext-assigningauthority"));
			}

			// xcn.13: Identifier Type Code.
			if (!xcn.getXcn13_IdentifierTypeCode().isEmpty()) {
				Coding code = new Coding();
				code.setCode(xcn.getXcn13_IdentifierTypeCode().getValue());
				identifier.setType(new CodeableConcept(code));
			}

			// xcn.14: Assigning Facility.
			if (!xcn.getXcn14_AssigningFacilityID().isEmpty()) {
				identifier.addExtension(getExtensionFromHD(xcn.getXcn14_AssigningFacilityID(),
						"ext-assigningFacility"));
			}

			// xcn.21: Professional Suffix.

		} catch (HL7Exception e) {
			e.printStackTrace();
		}

		return practitioner;
	}

	private Address getAddressFromXAD(XAD xad) {
		Address address = new Address();
		ST streetXad1 = xad.getXad1_StreetAddress();
		ST cityXad3 = xad.getXad3_City();
		ST stateXad4 = xad.getXad4_StateOrProvince();
		ST zipXad5 = xad.getXad5_ZipOrPostalCode();
		ID countryXad6 = xad.getXad6_Country();
		IS censusTractXad10 = xad.getXad10_CensusTract();

		try {
			if (!streetXad1.isEmpty()) {
				address.addLine(streetXad1.getValue());
			}

			if (!cityXad3.isEmpty()) {
				address.setCity(cityXad3.getValue());
			}

			if (!stateXad4.isEmpty()) {
				address.setState(stateXad4.getValue());
			}

			if (!zipXad5.isEmpty()) {
				address.setPostalCode(zipXad5.getValue());
			}

			if (!countryXad6.isEmpty()) {
				address.setCountry(countryXad6.getValue());
			}

			if (!censusTractXad10.isEmpty()) {
				Extension censusTractExt = new Extension();
				censusTractExt.setUrl("urn:mmg:census-tract");
				censusTractExt.setValue(new StringType(censusTractXad10.getValue()));
				address.addExtension(censusTractExt);
			}

		} catch (HL7Exception e) {
			e.printStackTrace();
		}

		return address;
	}

	private Location getLocationFromHD(HD hD) {
		Location location = new Location();
		try {
			if (!hD.isEmpty()) {
				IS hd_1 = hD.getHd1_NamespaceID();
				ST hd_2 = hD.getHd2_UniversalID();
				ID hd_3 = hD.getHd3_UniversalIDType();

				if (!hd_1.isEmpty()) {
					location.setName(hd_1.getValue());
				}

				if (!hd_2.isEmpty()) {
					Identifier locationIdentifier = new Identifier();
					if ("ISO".equals(hd_3.getValue())) {
						locationIdentifier.setValue("urn:oid:" + hd_2.getValue());
					} else if ("UUID".equals(hd_3.getValue())) {
						locationIdentifier.setValue("urn:uuid:" + hd_2.getValue());
					} else {
						CodeableConcept phyCodeableConcept = location.getPhysicalType();
						Coding phyCoding = phyCodeableConcept.getCodingFirstRep();
						phyCoding.setCode("si");
						phyCoding.setSystem("http://terminology.hl7.org/CodeSystem/location-physical-type");
						location.setPhysicalType(phyCodeableConcept);

						locationIdentifier.setValue(hd_2.getValue());
					}

					if (!locationIdentifier.isEmpty()) {
						location.addIdentifier(locationIdentifier);
					}
				}
			}
		} catch (HL7Exception e) {
			e.printStackTrace();
		}

		return location;
	}

	private UriType getUriFromHD(HD hD) {
		UriType uriType = new UriType();

		try {
			if (!hD.isEmpty()) {
				IS hd_1 = hD.getHd1_NamespaceID();
				ST hd_2 = hD.getHd2_UniversalID();
				ID hd_3 = hD.getHd3_UniversalIDType();

				if ("ISO".equals(hd_3.getValue())) {
					if (hd_1.isEmpty()) {
						uriType.setValue("urn:oid:" + hd_2.getValue());
					} else {
						uriType.setValue("urn:mmg:id:" + hd_1.getValue() + ":" + hd_2.getValue());
					}
				} else if ("UUID".equals(hd_3.getValue())) {
					if (hd_1.isEmpty()) {
						uriType.setValue("urn:uuid:" + hd_2.getValue());
					} else {
						uriType.setValue("urn:mmg:id:" + hd_1.getValue() + ":" + hd_2.getValue());
					}
				} else {
					uriType.setValue("urn:mmg:id:" + hd_1.getValue() + ":" + hd_2.getValue() + ":" + hd_3.getValue());
				}
			}
		} catch (HL7Exception e) {
			e.printStackTrace();
		}

		return uriType;
	}

	private Extension getExtensionFromHD(HD hD, String url) {
		Extension extension = new Extension();

		try {
			extension.setUrl(url);

			if (!hD.isEmpty()) {
				IS hd_1 = hD.getHd1_NamespaceID();
				ST hd_2 = hD.getHd2_UniversalID();
				ID hd_3 = hD.getHd3_UniversalIDType();

				if (!hd_1.isEmpty()) {
					Extension extension_1 = new Extension();
					extension_1.setUrl("urn:mmg:nameSpaceID");
					extension_1.setValue(new StringType(hd_1.getValue()));
					extension.addExtension(extension_1);
				}

				if (!hd_2.isEmpty()) {
					Extension extension_1 = new Extension();
					extension_1.setUrl("urn:mmg:universalID");
					extension_1.setValue(new StringType(hd_2.getValue()));
					extension.addExtension(extension_1);
				}

				if (!hd_3.isEmpty()) {
					Extension extension_1 = new Extension();
					extension_1.setUrl("urn:mmg:universalIDType");
					extension_1.setValue(new StringType(hd_3.getValue()));
					extension.addExtension(extension_1);
				}
			}

		} catch (HL7Exception e) {
			e.printStackTrace();
		}

		return extension;
	}

	private Identifier getIdentifierFromEI(EI eI, String typeSystem, String typeCode) {
		Identifier identifier = null;
		try {

			if (eI != null && !eI.isEmpty()) {
				ST eI_1 = eI.getEi1_EntityIdentifier();
				IS eI_2 = eI.getEi2_NamespaceID();
				ST eI_3 = eI.getEi3_UniversalID();
				ID eI_4 = eI.getEi4_UniversalIDType();

				identifier = new Identifier();
				Coding typeCoding = new Coding(typeSystem, typeCode, null);
				CodeableConcept typeCodeable = new CodeableConcept(typeCoding);
				identifier.setType(typeCodeable);

				if (eI_1 != null && !eI_1.isEmpty()) {
					identifier.setValue(eI_1.getValue());
				}

				// Create Extension
				Extension eIExtension = new Extension();
				if (eI_2 != null && !eI_2.isEmpty()) {
					Extension eI_2_extension = new Extension();
					eI_2_extension.setUrl("urn:mmg:nameSpaceID");
					eI_2_extension.setValue(new StringType(eI_2.getValue()));
					eIExtension.addExtension(eI_2_extension);
				}

				if (eI_3 != null && !eI_3.isEmpty()) {
					Extension eI_3_extension = new Extension();
					eI_3_extension.setUrl("urn:mmg:universalID");
					eI_3_extension.setValue(new StringType(eI_3.getValue()));
					eIExtension.addExtension(eI_3_extension);
				}

				if (eI_4 != null && !eI_4.isEmpty()) {
					Extension eI_4_extension = new Extension();
					eI_4_extension.setUrl("urn:mmg:universalIDType");
					eI_4_extension.setValue(new CodeType(eI_4.getValue()));
					eIExtension.addExtension(eI_4_extension);
				}

				if (!eIExtension.isEmpty()) {
					eIExtension.setUrl("ext-assigningauthority");

					identifier.addExtension(eIExtension);
				}

			}

		} catch (HL7Exception e) {
			e.printStackTrace();
		}

		return identifier;

	}

	private CodeableConcept getCodeableConceptFromCE(CE codeElement) {
		CodeableConcept retVal = new CodeableConcept();

		Coding coding = new Coding();
		try {
			ID id = codeElement.getCe1_Identifier();
			if (id != null && !id.isEmpty()) {
				coding.setCode(id.getValue());
			}
			ST display = codeElement.getCe2_Text();
			if (display != null && !display.isEmpty()) {
				coding.setDisplay(display.getValue());
			}
			ST system = codeElement.getCe3_NameOfCodingSystem();
			if (system != null && !system.isEmpty()) {
				coding.setSystem(V2FHIRCodeSystem.getFhirFromV2(system.getValue()));
			} else {
				// Put facility name for the system if available.
				// For now, we put NMS Labs if sending facility is NMS.
				if (getSendingFacilityName().equalsIgnoreCase("NMS"))
					coding.setSystem("NMS Labs");
			}
			if (!coding.isEmpty()) {
				retVal.addCoding(coding);
			}
		} catch (HL7Exception e) {
			e.printStackTrace();
		}

		return retVal;
	}

	private Quantity getQuantityFromCQ(CQ cq) {
		try {
			if (cq == null || cq.isEmpty())
				return null;
		} catch (HL7Exception e) {
			e.printStackTrace();
		}

		NM quantity = cq.getQuantity();
		CE unit = cq.getUnits();

		Quantity retQuantity = new Quantity();
		retQuantity.setValue(Double.valueOf(quantity.getValue()));
		CodeableConcept unitCodeable = getCodeableConceptFromCE(unit);

		retQuantity.setCode(unitCodeable.getCodingFirstRep().getCode());
		retQuantity.setUnit(unitCodeable.getCodingFirstRep().getDisplay());
		retQuantity.setSystem(unitCodeable.getCodingFirstRep().getSystem());

		return null;
	}

	public Type getDateFromDR(DR dr) {
		Type retVal = null;
		try {
			if (dr == null || dr.isEmpty()) {
				return null;
			}
		} catch (HL7Exception e) {
			e.printStackTrace();
		}

		TS dr1 = dr.getDr1_RangeStartDateTime();
		TS dr2 = dr.getDr2_RangeEndDateTime();

		try {
			if (!dr1.isEmpty() && !dr2.isEmpty()) {
				// This is period.
				Period period = new Period();
				Date startDateTime = dr1.getTs1_TimeOfAnEvent().getValueAsDate();
				Date endDateTime = dr2.getTs1_TimeOfAnEvent().getValueAsDate();

				period.setStart(startDateTime);
				period.setEnd(endDateTime);

				return (Type) period;
			} else if (!dr1.isEmpty()) {
				DateTimeType dt = new DateTimeType(dr1.getTs1_TimeOfAnEvent().getValueAsDate());

				return (Type) dt;
			}
		} catch (HL7Exception e) {
			e.printStackTrace();
		}

		return retVal;
	}

	public MessageHeader mapMessageHeader(ca.uhn.hl7v2.model.v23.message.ORU_R01 oruR01message) {
		MSH msh = oruR01message.getMSH();
		if (messageHeader == null)
			messageHeader = new MessageHeader();

		HD sendingFacility = msh.getSendingFacility();
		// HD receivingFacility = msh.getReceivingFacility();
		try {
			if (!sendingFacility.isEmpty()) {
				IS sendingFacilityName = sendingFacility.getHd1_NamespaceID();
				if (!sendingFacilityName.isEmpty()) {
					setSendingFacilityName(sendingFacilityName.getValue());
				}
			}

			// messageheader.event from MSH9.2. MSH9.2 is triggering event.
			// For ELR, it's R01. We need to map this to FHIR message-event, which
			// can be observation-provide.
			Coding eventCoding = new Coding();
			CM_MSG msh9 = msh.getMsh9_MessageType();
			if (msh9 != null && !msh9.isEmpty()) {
				ID msh9_1 = msh9.getCm_msg1_MessageType();
				ID msh9_2 = msh9.getCm_msg2_TriggerEvent();
				if (!msh9_1.isEmpty() && !msh9_2.isEmpty()) {
					if ("ORU".equals(msh9_2.getValue()) && "R01".equals(msh9_2.getValue())) {
						eventCoding.setSystem(ObservationCategory.LABORATORY.getSystem());
						eventCoding.setCode(ObservationCategory.LABORATORY.toCode());
						eventCoding.setDisplay(ObservationCategory.LABORATORY.getDisplay());
					} else {
						eventCoding.setCode(msh9_1.getValue() + "_" + msh9_2.getValue());
					}
				}
			}

			if (eventCoding.isEmpty()) {
				// We couldn't get any info from triggering event. Just set this to our default
				// R01 event mapping.
				MessageEvent messageEvent = MessageEvent.valueOf("OBSERVATIONPROVIDE");
				eventCoding.setSystem(messageEvent.getSystem());
				eventCoding.setCode(messageEvent.toCode());
				eventCoding.setDisplay(messageEvent.getDisplay());
			}

			messageHeader.setEvent(eventCoding);

			// messageevent.destination.name from MSH5 and MSH6
			MessageDestinationComponent messageDestination = new MessageDestinationComponent();
			HD msh5 = msh.getMsh5_ReceivingApplication();
			String destinationName = getValueOfHD(msh5);
			if (destinationName != null && !destinationName.isEmpty()) {
				messageDestination.setName(destinationName);
			}

			HD msh6 = msh.getMsh6_ReceivingFacility();
			String destinationEndpoint = getValueOfHD(msh6);
			if (destinationEndpoint != null && !destinationEndpoint.isEmpty()) {
				messageDestination.setName(destinationEndpoint);
				setReceivingFacilityName(destinationEndpoint);
			}

			if (!messageDestination.isEmpty()) {
				messageHeader.addDestination(messageDestination);
			}

			// messageHeader.timestamp from MSH-7
			// TS msh7 = msh.getMsh7_DateTimeOfMessage();
			// if (msh7 != null && !msh7.isEmpty()) {
			// TSComponentOne timeOfEvent = msh7.getTs1_TimeOfAnEvent();
			// if (timeOfEvent != null && !timeOfEvent.isEmpty()) {
			// messageHeader.setTimestamp(timeOfEvent.getValueAsDate());
			// }
			// }

			// messageHeader.source from MSH-3
			HD msh3 = msh.getMsh3_SendingApplication();
			if (!msh3.isEmpty()) {
				String sourceDestination = getValueOfHD(msh3);
				if (sourceDestination != null && !sourceDestination.isEmpty()) {
					MessageSourceComponent messageSource = new MessageSourceComponent();
					messageSource.setEndpoint(sourceDestination);
					messageHeader.setSource(messageSource);
				}
			}
		} catch (HL7Exception e) {
			e.printStackTrace();
			return null;
		}

		return messageHeader;
	}

	public Composition mapComposition(ca.uhn.hl7v2.model.v23.message.ORU_R01 oruR01message) {
		MSH msh = oruR01message.getMSH();
		if (composition == null)
			composition = new Composition();

		composition.setStatus(CompositionStatus.FINAL);

		try {
			// messageheader.event from MSH9.2. MSH9.2 is triggering event.
			// For ELR, it's R01. We need to map this to FHIR message-event, which
			// can be observation-provide.
			CodeableConcept typeCodeable = composition.getType();
			Coding typeCoding = new Coding();
			CM_MSG msh9 = msh.getMsh9_MessageType();

			if (msh9 != null && !msh9.isEmpty()) {
				// Object msh9_1 = msh9.getMsg1_MessageCode();
				ID msh9_1 = msh9.getCm_msg1_MessageType();
				ID msh9_2 = msh9.getCm_msg2_TriggerEvent();
				if (!msh9_1.isEmpty() && !msh9_2.isEmpty()) {
					if ("ORU".equals(msh9_1.getValue()) && "R01".equals(msh9_2.getValue())) {
						typeCoding.setSystem("http://loinc.org");
						typeCoding.setCode("11502-2");
						typeCoding.setDisplay("Laboratory report");
					} else {
						typeCoding.setDisplay("NOT AVAILABLE");
					}
				}
			}

			if (typeCoding.isEmpty()) {
				// We couldn't get any info from triggering event. Just set this to our default
				// R01 event mapping.
				typeCoding.setSystem("http://loinc.org");
				typeCoding.setCode("11502-2");
				typeCoding.setDisplay("Laboratory report");
			}

			typeCodeable.addCoding(typeCoding);

		} catch (HL7Exception e) {
			e.printStackTrace();
			return null;
		}

		return composition;
	}

	public MessageHeader getMessageHeader() {
		return null;
	}

	public Composition getComposition() {
		return composition;
	}

}
