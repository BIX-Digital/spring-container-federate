/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.bix_digital.platform.federate.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bix_digital.platform.federate.ContainerAwareAutoWired;
import com.bix_digital.platform.federate.impl.util.ContextHelper;

/**
 * This is the proxy on the consumer (Wire) side 
 * @author utschig
 */
public class ContainerAwareWireProxy implements InvocationHandler 
{
    private static Logger logger = LoggerFactory.getLogger(
    		ContainerAwareWireProxy.class);
     
    private final ContainerAwareAutoWired containerWireAnnotation;
    
    private final String targetName;
        
    private final Map<String, List<String>> podMapping =
    	new HashMap<String, List<String>>();
    
    private ContextHelper context;
    
    public ContainerAwareWireProxy(String name, ContainerAwareAutoWired annotation, ContextHelper context) 
    		throws Exception
    {
        this.containerWireAnnotation = annotation;
        this.targetName = name;
        this.context = context;
    }
 
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) 
      throws Throwable 
    {
		Object remote = context.lookup(targetName);
		if (logger.isDebugEnabled())
		{
			logger.debug("Wire: " + targetName + " method: " + method.getName() + " target: " + remote);
		}
        long start = System.nanoTime();
        
        Method m = remote.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
        Object result = m.invoke(remote, args);
        
        long elapsed = System.nanoTime() - start;
 
        logger.info("Executing wire {} _ {} finished in {} ns - returned {}", targetName, method.getName(), 
        		elapsed, result != null ? result : "void");
 
        return result;
    }
}

