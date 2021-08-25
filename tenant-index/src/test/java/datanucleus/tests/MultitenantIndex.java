package datanucleus.tests;

import datanucleus.model.TenantedIndexObject;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MultitenantIndex {
	@Test
	public void testCreate() {
		String name = "/" + getClass().getName().replace(".", "/") + ".properties";
		InputStream resourceAsStream = getClass().getResourceAsStream(name);
		Properties properties = new Properties();
		try {
			properties.load(resourceAsStream);
			EntityManagerFactory jpaTest = Persistence.createEntityManagerFactory("JPATest", properties);
			EntityManager entityManager = jpaTest.createEntityManager();
			EntityTransaction transaction = entityManager.getTransaction();
			transaction.begin();
			TenantedIndexObject o = new TenantedIndexObject();
			long id = System.currentTimeMillis();
			o.setId(id);
			entityManager.persist(o);
			transaction.commit();
			entityManager.close();
			jpaTest.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
