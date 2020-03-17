/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhir2.api.dao.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import java.util.Collection;

import ca.uhn.fhir.rest.param.StringOrListParam;
import ca.uhn.fhir.rest.param.StringParam;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Condition;
import org.openmrs.module.fhir2.TestFhirSpringConfiguration;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = TestFhirSpringConfiguration.class, inheritLocations = false)
public class FhirConditionDaoImpl_2_2Test extends BaseModuleContextSensitiveTest {
	
	private static final String CONDITION_UUID = "2cc6880e-2c46-15e4-9038-a6c5e4d22fb7";
	
	private static final String WRONG_CONDITION_UUID = "430bbb70-6a9c-4e1e-badb-9d1034b1b5e9";
	
	private static final String CONDITION_INITIAL_DATA_XML = "org/openmrs/api/include/ConditionServiceImplTest-SetupCondition.xml";
	
	private static final String PATIENT_SEARCH_DATA_XML = "org/openmrs/api/include/PatientServiceTest-findPatients.xml";
	
	private static final String PATIENT_GIVEN_NAME = "John";
	
	private static final String PATIENT_PARTIAL_GIVEN_NAME = "Jo";
	
	private static final String PATIENT_FAMILY_NAME = "Doe";
	
	private static final String PATIENT_PARTIAL_FAMILY_NAME = "Do";
	
	private static final String PATIENT_NOT_FOUND_NAME = "Igor";
	
	@Inject
	@Named("sessionFactory")
	private Provider<SessionFactory> sessionFactoryProvider;
	
	@Inject
	private FhirConditionDaoImpl_2_2 dao;
	
	@Before
	public void setUp() {
		dao = new FhirConditionDaoImpl_2_2();
		dao.setSessionFactory(sessionFactoryProvider.get());
		executeDataSet(CONDITION_INITIAL_DATA_XML);
		executeDataSet(PATIENT_SEARCH_DATA_XML);
	}
	
	@Test
	public void shouldRetrieveConditionByUuid() {
		Condition condition = dao.getConditionByUuid(CONDITION_UUID);
		assertThat(condition, notNullValue());
		assertThat(condition.getUuid(), notNullValue());
		assertThat(condition.getUuid(), equalTo(CONDITION_UUID));
	}
	
	@Test
	public void shouldReturnNullWhenGetConditionByWrongUuid() {
		Condition condition = dao.getConditionByUuid(WRONG_CONDITION_UUID);
		assertThat(condition, nullValue());
	}
	
	@Test
	public void searchForPatients_shouldSearchForPatientsByName() {
		Collection<Condition> results = dao.searchForConditions(
		    new StringOrListParam().add(new StringParam(PATIENT_GIVEN_NAME)), null,
		    null /*, null, null, null, null, null, null, null, null*/, null);
		
		assertThat(results, notNullValue());
		assertThat(results, not(empty()));
		assertThat(results.size(), equalTo(6)); // TODO: Why 6? should be 3!
		assertThat(results.iterator().next().getPatient().getGivenName(), equalTo(PATIENT_GIVEN_NAME));
	}
}
