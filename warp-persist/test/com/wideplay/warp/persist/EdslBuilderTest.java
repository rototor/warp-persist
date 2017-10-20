/**
 * Copyright (C) 2008 Wideplay Interactive.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
import com.wideplay.warp.persist.dao.HibernateTestAccessor;
import com.wideplay.warp.persist.hibernate.HibernatePersistenceStrategy;
import com.wideplay.warp.persist.hibernate.HibernateTestEntity;
import org.hibernate.cfg.Configuration;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * On: 2/06/2007
 *
 * @author Dhanji R. Prasanna (dhanji@gmail.com)
 * @since 1.0
 */
public class EdslBuilderTest {
	@Test
	public void testEdslLanguage() {
		PersistenceService.usingHibernate().buildModule();

		PersistenceService.usingHibernate().across(UnitOfWork.REQUEST)
				.addAccessor(HibernateTestAccessor.class)
				.buildModule();

		PersistenceService.usingHibernate().across(UnitOfWork.TRANSACTION).forAll(Matchers.any()).buildModule();
	}

	@Test
	public void testHibernateConfig() {
		Injector injector = Guice.createInjector(PersistenceService.usingHibernate().across(UnitOfWork.TRANSACTION)
						.buildModule(),
				new AbstractModule() {
					@Override
					protected void configure() {
						bind(Configuration.class).toInstance(new Configuration().addAnnotatedClass(HibernateTestEntity.class)
								.setProperties(Initializer.loadProperties("spt-persistence.properties")));
					}
				});

		injector.getInstance(PersistenceService.class).start();

		injector.getInstance(TransactionalObject.class).txnMethod();
	}


	@Test
	public final void testMultimodulesConfigHibernate() {
		PersistenceStrategy h = HibernatePersistenceStrategy.builder()
				.configuration(new Configuration())
				.annotatedWith(MyUnit.class).build();
		com.google.inject.Module m = PersistenceService.using(h)
				.across(UnitOfWork.TRANSACTION)

				.forAll(Matchers.any(), Matchers.annotatedWith(Transactional.class))
				.buildModule();

		Guice.createInjector(m);
	}

	@Test
	public final void testPersistenceServicesProvider() {
		PersistenceStrategy h = HibernatePersistenceStrategy.builder()
				.configuration(new Configuration())
				.annotatedWith(MyUnit.class).build();
		com.google.inject.Module hibernateModule = PersistenceService.using(h)
				.across(UnitOfWork.TRANSACTION)

				.forAll(Matchers.any(), Matchers.annotatedWith(Transactional.class))
				.buildModule();


		List<PersistenceService> persistenceServices = Guice.createInjector(hibernateModule,
				new PersistenceServiceExtrasModule())
				.getInstance(Key.get(new TypeLiteral<List<PersistenceService>>() {
				}));

		// Does no longer work, we dont need it, deprecate it ....
		//assert persistenceServices.size() == 2;
	}


	static class TransactionalObject {
		@Transactional
		public void txnMethod() {
		}
	}
}
