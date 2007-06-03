package com.wideplay.warp.jpa;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.google.inject.Provider;
import com.google.inject.Inject;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityManager;

/**
 * Created with IntelliJ IDEA.
 * On: May 26, 2007 2:26:28 PM
 *
 * @author Dhanji R. Prasanna
 */
class EntityManagerProvider implements Provider<EntityManager> {
    private final EntityManagerFactory factory;

    @Inject
    public EntityManagerProvider(EntityManagerFactory factory) {
        this.factory = factory;
    }

    public EntityManager get() {
        return factory.createEntityManager();
    }
}
