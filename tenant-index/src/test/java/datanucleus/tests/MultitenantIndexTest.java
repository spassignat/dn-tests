package datanucleus.tests;

import datanucleus.TestAppender;
import datanucleus.model.TenantedIndexObject;
import org.apache.log4j.*;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.fail;

public class MultitenantIndexTest {
	@Test
	public void testCreate() throws IOException {
		Appender newAppender = new TestAppender("CREATE INDEX IDX_TENANT ON TENANTEDINDEXOBJECT ()");
		prepareLogger(newAppender, "DataNucleus.Datastore.Schema");
		String name = getClass().getName().replace(".", "/") + ".properties";
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream resourceAsStream = classLoader.getResourceAsStream(name);
		Properties properties = new Properties();
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
	}

	private void prepareLogger(Appender newAppender, String s) {
		Logger logger = Logger.getLogger(s);
		logger.setLevel(Level.DEBUG);
		logger.addAppender(newAppender);
	}
}
