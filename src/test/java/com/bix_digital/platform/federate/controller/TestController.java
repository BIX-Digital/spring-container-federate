package com.bix_digital.platform.federate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bix_digital.platform.federate.ContainerAwareAutoWired;
import com.bix_digital.platform.federate.services.ITestService;

@RestController
public class TestController {
	
	public TestController() {
		// TODO Auto-generated constructor stub
	}
	
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