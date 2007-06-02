package com.wideplay.warp.persist;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.wideplay.warp.hibernate.*;
import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.aopalliance.intercept.MethodInterceptor;

/**
 * Created with IntelliJ IDEA.
 * On: 30/04/2007
 *
 * @author Dhanji R. Prasanna
 * @since 1.0
 */
class PersistenceModule extends AbstractModule {

    private static final Logger log = Logger.getLogger(PersistenceModule.class);

    private final PersistenceFlavor flavor;
    private UnitOfWork unitOfWork;
    private TransactionStrategy transactionStrategy;
    private Matcher<? super Class<?>> classMatcher;


    PersistenceModule(PersistenceFlavor flavor) {
        this.flavor = flavor;

        //set defaults (do *not* make these final!!)
        classMatcher = Matchers.any();
        transactionStrategy = TransactionStrategy.LOCAL;
        unitOfWork = UnitOfWork.TRANSACTION;
    }

    protected void configure() {
        MethodInterceptor txnInterceptor = null;
        switch (flavor) {
            case HIBERNATE:
                HibernateBindingSupport.addBindings(binder());
                txnInterceptor = HibernateBindingSupport.getInterceptor(transactionStrategy);
                break;
            case JPA:
        }

        //bind the chosen txn interceptor
        bindInterceptor(classMatcher, Matchers.annotatedWith(Transactional.class),
                txnInterceptor);
    }

    static enum PersistenceFlavor { HIBERNATE, JPA }


    //builder config hooks
    void setUnitOfWork(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    void setTransactionStrategy(TransactionStrategy transactionStrategy) {
        this.transactionStrategy = transactionStrategy;
    }

    void setClassMatcher(Matcher<? super Class<?>> classMatcher) {
        this.classMatcher = classMatcher;
    }
}
