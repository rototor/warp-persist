package com.wideplay.warp.jpa;

import com.wideplay.warp.persist.PersistenceService;
import com.google.inject.Inject;
import com.google.inject.BindingAnnotation;
import org.hibernate.cfg.Configuration;

import javax.persistence.Persistence;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created with IntelliJ IDEA.
 * On: 30/04/2007
 *
 * @author Dhanji R. Prasanna
 * @since 1.0
 */
class JpaPersistenceService extends PersistenceService {
    private final EntityManagerFactoryHolder emFactoryHolder;
    private final String persistenceUnitName;

    private static final String JTA_USER_TRANSACTION = "jta.UserTransaction";

    @Inject
    public JpaPersistenceService(EntityManagerFactoryHolder sessionFactoryHolder, @JpaUnit String persistenceUnitName) {
        this.emFactoryHolder = sessionFactoryHolder;
        this.persistenceUnitName = persistenceUnitName;
    }

    public void start() {
        emFactoryHolder.setEntityManagerFactory(Persistence.createEntityManagerFactory(persistenceUnitName));

        //if necessary, set the JNDI lookup name of the JTA txn
    }

    @BindingAnnotation
    @Retention(RetentionPolicy.RUNTIME)
    public @interface PersistenceProperties { }


    @Override
    public boolean equals(Object obj) {
        return emFactoryHolder.equals( ((JpaPersistenceService) obj).emFactoryHolder);
    }

    @Override
    public int hashCode() {
        return (emFactoryHolder != null ? emFactoryHolder.hashCode() : 0);
    }
}
