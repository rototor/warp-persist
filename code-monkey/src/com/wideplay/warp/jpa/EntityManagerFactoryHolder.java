package com.wideplay.warp.jpa;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

/**
 * Created by IntelliJ IDEA.
 * User: dprasanna
 * Date: 31/05/2007
 * Time: 15:26:06
 * <p/>
 *
 * A placeholder that frees me from having to use statics to make a singleton EM factory,
 * so I can use per-injector singletons vs. per JVM/classloader singletons (which doesnt really work
 * for several reasons).
 *
 * @author dprasanna
 * @since 1.0
 */
class EntityManagerFactoryHolder {
    private volatile EntityManagerFactory entityManagerFactory;

    //A hack to provide the session factory statically to non-guice objects (interceptors), that can be thrown away come guice1.1
    private static volatile EntityManagerFactoryHolder singletonEmFactoryHolder;

    //have to manage the em oursevles--not neat like hibernate =(
    private static final ThreadLocal<EntityManager> entityManager = new ThreadLocal<EntityManager>();

    //store singleton
    public EntityManagerFactoryHolder() {
        singletonEmFactoryHolder = this;
    }

    EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    synchronized void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        if (null != this.entityManagerFactory)
            throw new RuntimeException("Duplicate session factory creation! Only one session factory is allowed per injector");

        this.entityManagerFactory = entityManagerFactory;
    }

    static EntityManagerFactory getCurrentEntityManagerFactory() {
        return singletonEmFactoryHolder.getEntityManagerFactory();
    }


    static EntityManager getCurrentEntityManager() {
        return entityManager.get();
    }

    static void closeCurrentEntityManager() {
        EntityManager em = entityManager.get();

        if (null != em) {
            em.close();
            entityManager.remove();
        }
    }

    static EntityManager openEntityManager() {
        EntityManager em = getCurrentEntityManagerFactory().createEntityManager();

        if (null != entityManager.get())
            throw new PersistenceException("An entity manager was already open when a new one was attempted (check your EM strategy and txn settings)");

        entityManager.set(em);

        return em;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityManagerFactoryHolder that = (EntityManagerFactoryHolder) o;

        return (entityManagerFactory == null ? that.entityManagerFactory == null : entityManagerFactory.equals(that.entityManagerFactory));

    }

    public int hashCode() {
        return (entityManagerFactory != null ? entityManagerFactory.hashCode() : 0);
    }
}
