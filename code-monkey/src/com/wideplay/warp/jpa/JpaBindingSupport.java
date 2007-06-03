package com.wideplay.warp.jpa;

import com.google.inject.Binder;
import com.google.inject.Singleton;
import com.wideplay.warp.persist.PersistenceService;
import com.wideplay.warp.persist.TransactionStrategy;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.aopalliance.intercept.MethodInterceptor;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityManager;

/**
 * Created with IntelliJ IDEA.
 * On: 2/06/2007
 *
 * @author Dhanji R. Prasanna
 * @since 1.0
 */
public class JpaBindingSupport {

    private JpaBindingSupport() {
    }

    public static void addBindings(Binder binder) {

        binder.bind(EntityManagerFactoryHolder.class).in(Singleton.class);

        binder.bind(EntityManagerFactory.class).toProvider(EntityManagerFactoryProvider.class);
        binder.bind(EntityManager.class).toProvider(EntityManagerProvider.class);

        binder.bind(PersistenceService.class).to(JpaPersistenceService.class).in(Singleton.class);
    }

    public static MethodInterceptor getInterceptor(TransactionStrategy transactionStrategy) {
        switch (transactionStrategy) {
            case LOCAL:
//                return new HibernateLocalTxnInterceptor();
            case JTA:
//                return new HibernateJtaTxnInterceptor();
        }

        return null;
    }
}
