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

import com.google.inject.cglib.proxy.MethodProxy;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.lang.reflect.AccessibleObject;

import net.jcip.annotations.ThreadSafe;
import net.jcip.annotations.Immutable;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Oct 9, 2007
 * Time: 2:00:03 PM
 * 
 * <p>
 * Adapter used to transform cglib interceptors to work with guice (aopalliance) interceptors.
 * Used to generate proxies for abstract classes, i.e. ones that are annotated {@code @Finder}.
 * </p>
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@Immutable
class AopAllianceCglibAdapter implements com.google.inject.cglib.proxy.MethodInterceptor {
    private final MethodInterceptor finderInterceptor;

    public AopAllianceCglibAdapter(MethodInterceptor finderInterceptor) {
        this.finderInterceptor = finderInterceptor;
    }


    public Object intercept(final Object object, final Method method, final Object[] args,
                            final MethodProxy methodProxy) throws Throwable {

        //ignore the dispatch for methods that are not finder-annotated
        if (!PersistenceService.isDynamicFinder(method))
            return methodProxy.invokeSuper(object, args);


        //otherwise dispatch to finder logic
        return finderInterceptor.invoke(new MethodInvocation() {

            public Method getMethod() {
                return method;
            }

            public Object[] getArguments() {
                return args;
            }

            public Object proceed() throws Throwable {
                return methodProxy.invoke(object, args);
            }

            public Object getThis() {
                return object;
            }

            public AccessibleObject getStaticPart() {
                return method;
            }
        });
    }
}
