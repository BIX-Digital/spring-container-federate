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
package com.bix_digital.platform.federate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bix_digital.platform.federate.ContainerAwareAutoWired;
import com.bix_digital.platform.federate.services.ITestService;

@RestController
@ActiveProfiles (profiles= {"remote"})
public class TestController 
{
		
	@ContainerAwareAutoWired 
	public ITestService someTestService;

	@Autowired
	public ITestService localService;

	@RequestMapping("/container")
	public String getContainerWire () {
		return "" + someTestService.isContainerAware();
	}

	@RequestMapping("/noncontainer")
	public String getStandardWire () {
		return "" + localService.isContainerAware();
	}

}