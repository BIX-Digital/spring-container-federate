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
package com.bix_digital.platform.federate.processor;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import com.bix_digital.platform.federate.ContainerAwareAutoWired;
import com.bix_digital.platform.federate.impl.ContainerAwareWireProxy;
import com.bix_digital.platform.federate.impl.util.ContextHelper;

public class ContainerAwareFieldProcessorCallback implements FieldCallback
{
    private ConfigurableListableBeanFactory configurableBeanFactory;
    private Object bean;
    
    private Logger logger = LoggerFactory.getLogger(ContainerAwareFieldProcessorCallback.class);

    private ContextHelper context;
    
    public ContainerAwareFieldProcessorCallback(ConfigurableListableBeanFactory bf, Object bean, ContextHelper context) {
        configurableBeanFactory = bf;
        this.bean = bean;
        this.context = context;
    }

	@Override
	public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
		ContainerAwareAutoWired wired = field.getAnnotation(ContainerAwareAutoWired.class);
		
        if (wired == null) {
            return;
        }
        
        ReflectionUtils.makeAccessible(field);
        String name = field.getName();

		logger.debug("Setup of containerWire: " + name + " class: " + field.getType());

        try 
        {
        	// setup the proxy on the wire (client) side
			Object proxy = Proxy.newProxyInstance(
				  field.getType().getClassLoader(), new Class[] { field.getType() }, 
				  new ContainerAwareWireProxy(name, field.getAnnotation(ContainerAwareAutoWired.class), context));
    				
            ReflectionUtils.setField(field, bean, proxy);
        } catch (Exception eSetupException) 
        {
        	logger.error("Could not setup container wire: {} \n {}", 
        		eSetupException.getMessage(), eSetupException);
        	throw new IllegalAccessException(eSetupException.getMessage());
        }
	}	
}
