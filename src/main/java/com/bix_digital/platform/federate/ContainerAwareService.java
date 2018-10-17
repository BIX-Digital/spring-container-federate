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
package com.bix_digital.platform.federate;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.stereotype.Service;

@Retention(RUNTIME)
@Target(TYPE)
@Service
@Documented
/**
 * Denotes the client side annotation, that inherits from autowired
 * @author utschig
 */
public @interface ContainerAwareService 
{

	/**
	 * Number of (pod) instances to be created 
	 * @return the number of instances that are triggered, 1 will stay local
	 */
	public int max_instances () default 1; 
	
	/**
	 * the max response time before a new instance is created
	 * @return the max response time
	 */
	public int max_response_time () default -1;
	
}
