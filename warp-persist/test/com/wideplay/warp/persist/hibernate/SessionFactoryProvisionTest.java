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

package com.wideplay.warp.persist.hibernate;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import com.wideplay.codemonkey.web.startup.Initializer;
import com.wideplay.warp.persist.PersistenceService;
import com.wideplay.warp.persist.UnitOfWork;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Created by IntelliJ IDEA.
 * User: Dhanji R. Prasanna (dhanji@gmail.com)
 * Date: 1/06/2007
 * Time: 11:40:36
 * <p/>
 * TODO: Describe me!
 *
 * @author Dhanji R. Prasanna (dhanji@gmail.com)
 * @since 1.0
 */
public class SessionFactoryProvisionTest {
    private Injector injector;

    @BeforeTest
    public void pre() {
        injector = Guice.createInjector(PersistenceService.usingHibernate()
            .across(UnitOfWork.TRANSACTION)
            .forAll(Matchers.any())
            .buildModule(),
                new AbstractModule() {

                    @Override
					protected void configure() {
                        bind(Configuration.class).toInstance(new AnnotationConfiguration()
                            .addAnnotatedClass(HibernateTestEntity.class)
                            .setProperties(Initializer.loadProperties("spt-persistence.properties")));
                    }
                });
    }


    @AfterClass
    void post() {
        injector.getInstance(SessionFactory.class).close();
    }

    @Test
    public void testSessionCreateOnInjection() {
        // TODO (Robbie) review
        //assert injector.getInstance(SessionFactoryHolder.class).equals(injector.getInstance(SessionFactoryHolder.class));

        assert injector.getInstance(PersistenceService.class).equals(injector.getInstance(PersistenceService.class)) : "SINGLETON VIOLATION " + PersistenceService.class.getName() ;

        //startup persistence
        injector.getInstance(PersistenceService.class)
                .start();

        //obtain session
        assert injector.getInstance(Session.class).isOpen() : "session is not open!";
    }
}
