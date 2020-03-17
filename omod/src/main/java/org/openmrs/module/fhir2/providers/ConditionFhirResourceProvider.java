/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhir2.providers;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.StringOrListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import lombok.AccessLevel;
import lombok.Setter;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.openmrs.module.fhir2.api.FhirConditionService;
import org.openmrs.module.fhir2.util.FhirUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("fhirResources")
@Setter(AccessLevel.PACKAGE)
public class ConditionFhirResourceProvider implements IResourceProvider {
	
	private static final Logger log = LoggerFactory.getLogger(ConditionFhirResourceProvider.class);
	
	@Inject
	private FhirConditionService conditionService;
	
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Condition.class;
	}
	
	@Read
	public Condition getConditionByUuid(@IdParam @NotNull IdType id) {
		Condition condition = conditionService.getConditionByUuid(id.getIdPart());
		if (condition == null) {
			throw new ResourceNotFoundException("Could not find condition with Id " + id.getIdPart());
		}
		return condition;
	}
	
	@Search
	//@SuppressWarnings("unused")
	// TODO the name is obviously incorrect!
	public Bundle searchPatients(@OptionalParam(name = "random_name"/*Patient.SP_NAME*/) StringOrListParam name,
	        @OptionalParam(name = Patient.SP_GIVEN) StringOrListParam given,
	        @OptionalParam(name = Patient.SP_FAMILY) StringOrListParam family, @Sort SortSpec sort) {
		log.info("HERE DEBUG name: " + name);
		//return new Bundle();
		return FhirUtils.convertSearchResultsToBundle(conditionService.searchConditions(name, given, family, sort));
		//				identifier,	gender, birthDate, deathDate, deceased, city, state, postalCode, sort));
	}
}
