/**********************************************************************
 Copyright (c) 2006 Erik Bengtson and others. All rights reserved.
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 Contributors:
 2007 Andy Jefferson - rewritten to new test.framework/samples
 ...
 **********************************************************************/
package datanucleus.tests;

import datanucleus.model.TenantedObject;
import org.datanucleus.PropertyNames;
import org.junit.Test;

import javax.persistence.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Tests for JPQL "SELECT" queries.
 */
public class EMMultitenantTest {
	private static boolean initialised = false;

	@Test
	public void testCreateFind() throws IOException {
		long id = System.currentTimeMillis();
		Properties ps1 = getProperties();
		Properties ps2 = getProperties();
		EntityManagerFactory emf1 = Persistence.createEntityManagerFactory("JPATest", ps1);
		Properties map1 = new Properties();
		map1.put(PropertyNames.PROPERTY_MAPPING_TENANT_ID, "T1");
		EntityManager em1 = emf1.createEntityManager(map1);
		EntityTransaction tx1 = em1.getTransaction();
		try {
			tx1.begin();
			TenantedObject p = new TenantedObject();
			p.setName("TOTO " + id);
			p.setId(id);
			em1.persist(p);
			tx1.commit();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
			// expected, but would like to eventually have IllegalArgumentException
		} finally {
			if (tx1.isActive()) {
				tx1.rollback();
			}
			em1.close();
		}
		em1 = emf1.createEntityManager(map1);
		tx1 = em1.getTransaction();
		try {
			tx1.begin();
			TenantedObject tenantedObject = em1.find(TenantedObject.class, id);
			assertNotNull(tenantedObject);
		} catch (PersistenceException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			if (tx1.isActive()) {
				tx1.rollback();
			}
			em1.close();
		}
		Properties map2 = new Properties();
		map2.put(PropertyNames.PROPERTY_MAPPING_TENANT_ID, "T2");
		EntityManager em2 = emf1.createEntityManager(map2);
		EntityTransaction tx2 = em2.getTransaction();
		try {
			tx2.begin();
			TenantedObject tenantedObject = em2.find(TenantedObject.class, id);
			assertNull(tenantedObject);
		} catch (PersistenceException e) {
			// expected, but would like to eventually have IllegalArgumentException
		} finally {
			if (tx2.isActive()) {
				tx2.rollback();
			}
			em2.close();
		}
	}

	@Test
	public void testCreateQuery() throws IOException {
		long id = System.currentTimeMillis();
		Properties ps1 = getProperties();
		EntityManagerFactory emf1 = Persistence.createEntityManagerFactory("JPATest", ps1);
		Properties map1 = new Properties();
		map1.put(PropertyNames.PROPERTY_MAPPING_TENANT_ID, "T1");
		EntityManager em1 = emf1.createEntityManager(map1);
		EntityTransaction tx1 = em1.getTransaction();
		try {
			tx1.begin();
			TenantedObject p = new TenantedObject();
			p.setId(id);
			em1.persist(p);
			tx1.commit();
		} catch (PersistenceException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			if (tx1.isActive()) {
				tx1.rollback();
			}
			em1.close();
		}
		em1 = emf1.createEntityManager(map1);
		tx1 = em1.getTransaction();
		try {
			tx1.begin();
			TypedQuery<TenantedObject> query = em1.createQuery("SELECT t FROM TenantedObject t where t.id=:i", TenantedObject.class);
			query.setParameter("i", id);
			TenantedObject singleResult = query.getSingleResult();
			assertNotNull(singleResult);
		} catch (PersistenceException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			if (tx1.isActive()) {
				tx1.rollback();
			}
			em1.close();
		}
		Properties ps2 = getProperties();
		Properties map2 = new Properties();
		map2.put(PropertyNames.PROPERTY_MAPPING_TENANT_ID, "T2");
		EntityManager em2 = emf1.createEntityManager(map2);
		EntityTransaction tx2 = em2.getTransaction();
		try {
			tx2.begin();
			TypedQuery<TenantedObject> query = em2.createQuery("SELECT t FROM TenantedObject t where t.id=:i", TenantedObject.class);
			query.setParameter("i", id);
			TenantedObject singleResult = query.getSingleResult();
			assertNull(singleResult);
		} catch (PersistenceException e) {
			// expected, but would like to eventually have IllegalArgumentException
		} finally {
			if (tx2.isActive()) {
				tx2.rollback();
			}
			em2.close();
		}
	}

	private Properties getProperties() throws IOException {
		String name = getClass().getName().replace(".", "/") + ".properties";
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream resourceAsStream = classLoader.getResourceAsStream(name);
		if(resourceAsStream==null){
			fail("Resource not found: "+name);
		}
		Properties ps1 = new Properties();
		ps1.load(resourceAsStream);
		return ps1;
	}
}