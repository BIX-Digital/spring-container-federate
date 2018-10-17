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

import java.lang.reflect.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.bix_digital.platform.federate.ContainerAwareService;
import com.bix_digital.platform.federate.IContainerAwareService;
import com.bix_digital.platform.federate.impl.ContainerAwareServiceProxy;
import com.bix_digital.platform.federate.impl.util.InitialContextHelper;

@Component
/**
 * Bean processor that sets up the (Server)side proxy for an annotated bean
 * @author utschig
 *
 */
public class ContainerAwareBeanPostProcessor implements BeanPostProcessor 
{
    private ConfigurableListableBeanFactory configurableBeanFactory;

    private Logger logger = LoggerFactory.getLogger(ContainerAwareBeanPostProcessor.class);
    
    @Autowired
    private InitialContextHelper context;
    
    @Autowired
    public ContainerAwareBeanPostProcessor(ConfigurableListableBeanFactory beanFactory) throws Exception 
    {
        this.configurableBeanFactory = beanFactory;
    }
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		
		if (bean.getClass().isAnnotationPresent(ContainerAwareService.class)) 
		{
			logger.debug("Found service: '" + beanName + "' for class '" + bean.getClass().getName() + "'");
			try 
			{
				if (bean instanceof IContainerAwareService) {
					((IContainerAwareService)bean).setIsContainerAware();
				}
				
				Object proxy = Proxy.newProxyInstance(
					bean.getClass().getClassLoader(), new Class[] { bean.getClass().getInterfaces()[0] }, 
						  new ContainerAwareServiceProxy(
						        beanName, 
						        bean.getClass().getAnnotation(ContainerAwareService.class),
						        bean));
				
				context.bind(beanName, proxy);
				
				// in case someone else needs it - we need to ensure a bean is registered!
				bean = bean.getClass().newInstance();
	            configurableBeanFactory.autowireBeanProperties(bean, 
	            	AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, true);
	            configurableBeanFactory.registerSingleton(beanName, bean);
				return bean;
			} catch (Exception eSetupServiceException) 
			{
				if (eSetupServiceException instanceof javax.naming.NoInitialContextException) 
				{
					throw new BeanCreationException("Could not setup service '" + beanName 
						+ "' in the container context, are you sure you run containered?!");
				}
				throw new BeanCreationException("Could not setup service '" + beanName 
					+ "' Exception: " + eSetupServiceException.getMessage());
			}	
		}
		// field inject for container wirable services
		Class<?> managedBeanClass = bean.getClass();
        ContainerAwareFieldProcessorCallback fieldCallback = 
          new ContainerAwareFieldProcessorCallback(configurableBeanFactory, bean, context);
        ReflectionUtils.doWithFields(managedBeanClass, fieldCallback);

        return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
	}

}
