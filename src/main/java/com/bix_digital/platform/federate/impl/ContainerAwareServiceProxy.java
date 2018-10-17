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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import com.bix_digital.platform.federate.ContainerAwareService;

/**
 * Proxy on the server (pod) side
 * @author utschig
 */
public class ContainerAwareServiceProxy implements InvocationHandler
{

    private static Logger logger = LoggerFactory.getLogger(
    		ContainerAwareServiceProxy.class);
     
    private final ContainerAwareService containerServiceAnnotation;
    
    private final String targetName;
    
    private final Object target;
    
    public ContainerAwareServiceProxy (String name, ContainerAwareService annotation, Object target) 
    		throws Exception
    {
        this.containerServiceAnnotation = annotation;
        this.targetName = name;
        this.target = target;
    }
 
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) 
      throws Throwable 
    {
		logger.debug("Service: " + targetName + " method: " + method.getName() + " target: " + target);
    		
        long start = System.nanoTime();
        
        Method m = target.getClass().getMethod(method.getName(), method.getParameterTypes());
        
        ReflectionUtils.makeAccessible(m);
        
        Object result = m.invoke(target, args);
        
        long elapsed = System.nanoTime() - start;
 
        logger.info("Service {} executing {} finished in {} ns - returned {}", targetName, method.getName(), 
        		elapsed, result != null ? result : "void");
 
        return result;
    }

}
