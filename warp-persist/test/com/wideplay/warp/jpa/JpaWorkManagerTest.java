package com.wideplay.warp.jpa;

import com.google.inject.Injector;
import com.google.inject.Guice;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.matcher.Matchers;
import com.wideplay.warp.persist.*;

import java.util.Date;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.EntityManagerFactory;

/**
 * Created with IntelliJ IDEA.
 * On: 2/06/2007
 *
 * @author Dhanji R. Prasanna <a href="mailto:dhanji@gmail.com">email</a>
 * @since 1.0
 */
public class JpaWorkManagerTest {
    private Injector injector;
    private static final String UNIQUE_TEXT_3 = JpaWorkManagerTest.class.getSimpleName()
            + "CONSTRAINT_VIOLATING some other unique text" + new Date();

    @BeforeClass
    public void pre() {
        injector = Guice.createInjector(PersistenceService.usingJpa()
            .across(UnitOfWork.REQUEST)
            .transactedWith(TransactionStrategy.LOCAL)
            .forAll(Matchers.any())
            .buildModule(),
                new AbstractModule() {

                    protected void configure() {
                        bindConstant().annotatedWith(JpaUnit.class).to("testUnit");
                    }
                });

        //startup persistence
        injector.getInstance(PersistenceService.class)
                .start();
    }

    @AfterClass public void post() {
        injector.getInstance(EntityManagerFactory.class).close();
    }

    
    @Test
    public void workManagerSessionTest() {
        injector.getInstance(WorkManager.class).beginWork();
        try {
            injector.getInstance(TransactionalObject.class).runOperationInTxn();
        } finally {
            injector.getInstance(WorkManager.class).endWork();

        }


        injector.getInstance(WorkManager.class).beginWork();
        injector.getInstance(EntityManager.class).getTransaction().begin();
        try {
            final Query query = injector.getInstance(EntityManager.class).createQuery("from JpaTestEntity where text = :text");

            query.setParameter("text", UNIQUE_TEXT_3);
            final Object o = query.getSingleResult();

            assert null != o : "no result!!";
            assert o instanceof JpaTestEntity : "Unknown type returned " + o.getClass();
            JpaTestEntity ent = (JpaTestEntity)o;

            assert UNIQUE_TEXT_3.equals(ent.getText()) : "Incorrect result returned or not persisted properly"
                    + ent.getText();

        } finally {
            injector.getInstance(EntityManager.class).getTransaction().commit();
            injector.getInstance(WorkManager.class).endWork();
        }
    }




    public static class TransactionalObject {
        @Inject
        EntityManager em;

        @Transactional
        public void runOperationInTxn() {
            JpaTestEntity testEntity = new JpaTestEntity();

            testEntity.setText(UNIQUE_TEXT_3);
            em.persist(testEntity);
        }

        @Transactional
        public void runOperationInTxnError() {
            
            JpaTestEntity testEntity = new JpaTestEntity();

            testEntity.setText(UNIQUE_TEXT_3 + "transient never in db!" + hashCode());
            em.persist(testEntity);
        }
    }
}