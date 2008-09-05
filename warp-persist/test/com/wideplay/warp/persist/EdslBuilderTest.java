/**
 * Copyright (C) 2008 Wideplay Interactive.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wideplay.warp.persist;

import com.google.inject.*;
import com.google.inject.matcher.Matchers;
import com.wideplay.codemonkey.web.startup.Initializer;
import com.wideplay.warp.hibernate.HibernatePersistenceStrategy;
import com.wideplay.warp.hibernate.HibernateTestEntity;
import com.wideplay.warp.jpa.JpaPersistenceStrategy;
import com.wideplay.warp.persist.dao.HibernateTestAccessor;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.testng.annotations.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * On: 2/06/2007
 *
 * @author Dhanji R. Prasanna (dhanji@gmail.com)
 * @since 1.0
 */
public class EdslBuilderTest {
    @Test public void testEdslLanguage() {
        PersistenceService.usingHibernate().buildModule();

        PersistenceService.usingHibernate().across(UnitOfWork.REQUEST)
                .addAccessor(HibernateTestAccessor.class)
                .buildModule();

        PersistenceService.usingHibernate().across(UnitOfWork.TRANSACTION).forAll(Matchers.any()).buildModule();
    }

    @Test public void testHibernateConfig() {
        Injector injector = Guice.createInjector(PersistenceService.usingHibernate().across(UnitOfWork.TRANSACTION)
                .transactedWith(TransactionStrategy.LOCAL).buildModule(),
                new AbstractModule() {
                    protected void configure() {
                        bind(Configuration.class).toInstance(new AnnotationConfiguration().addAnnotatedClass(HibernateTestEntity.class)
                                .setProperties(Initializer.loadProperties("spt-persistence.properties")));
                    }
                });

        injector.getInstance(PersistenceService.class).start();

        injector.getInstance(TransactionalObject.class).txnMethod();
    }

//    @Test TODO working on this... 
    public final void testDb4oConfig() {
        Guice.createInjector(PersistenceService.usingDb4o()
                .across(UnitOfWork.TRANSACTION)
                .transactedWith(TransactionStrategy.LOCAL)
                .forAll(Matchers.any(), Matchers.annotatedWith(Transactional.class))
                .buildModule()
        );
    }

    @Test
    public final void testMultimodulesConfigJpa() {
        PersistenceStrategy jpa = JpaPersistenceStrategy.builder()
                                                        .properties(new Properties())
                                                        .unit("myUnit")
                                                        .annotatedWith(Test.class).build();
        Module m = PersistenceService.using(jpa)
                                     .across(UnitOfWork.TRANSACTION)
                                     .transactedWith(TransactionStrategy.LOCAL)
                                     .forAll(Matchers.any(), Matchers.annotatedWith(Transactional.class))
                                     .buildModule();
        
        //Guice.createInjector(m);
    }

    @Test
    public final void testMultimodulesConfigHibernate() {
        PersistenceStrategy h = HibernatePersistenceStrategy.builder()
                                                        .configuration(new Configuration())
                                                        .annotatedWith(MyUnit.class).build();
        Module m = PersistenceService.using(h)
                                     .across(UnitOfWork.TRANSACTION)
                                     .transactedWith(TransactionStrategy.LOCAL)
                                     .forAll(Matchers.any(), Matchers.annotatedWith(Transactional.class))
                                     .buildModule();

        //Guice.createInjector(m);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @BindingAnnotation
    @interface MyUnit {}

    static class TransactionalObject {
        @Transactional public void txnMethod() {
        }
    }
}
