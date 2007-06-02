package com.wideplay.warp.persist;

/**
 * Created with IntelliJ IDEA.
 * On: 2/06/2007
 *
 * @author Dhanji R. Prasanna
 * @since 1.0
 */
public abstract class PersistenceService {
    public abstract void start();

    public static SessionStrategyBuilder usingHibernate() {
        return new PersistenceServiceBuilderImpl(new PersistenceModule(PersistenceModule.PersistenceFlavor.HIBERNATE));
    }
}
