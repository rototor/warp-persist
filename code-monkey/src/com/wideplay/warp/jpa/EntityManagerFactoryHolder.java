package com.wideplay.warp.jpa;

import javax.persistence.EntityManagerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: dprasanna
 * Date: 31/05/2007
 * Time: 15:26:06
 * <p/>
 *
 * A placeholder that frees me from having to use statics to make a singleton EM factory,
 * so I can use per-injector singletons vs. per JVM/classloader singletons.
 *
 * @author dprasanna
 * @since 1.0
 */
class EntityManagerFactoryHolder {
    private volatile EntityManagerFactory entityManagerFactory;

    //A hack to provide the session factory statically to non-guice objects (interceptors), that can be thrown away come guice1.1
    private static volatile EntityManagerFactoryHolder singletonEmFactoryHolder;

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
