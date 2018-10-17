package com.bix_digital.platform.federate.services;

import com.bix_digital.platform.federate.ContainerAwareService;

@ContainerAwareService
public class SomeTestService implements ITestService {

	public boolean isContainerAware = false;
	
	@Override
	public void setIsContainerAware() {
		isContainerAware = true;
	}
	
	@Override
	public boolean isContainerAware () {
		return isContainerAware;
	}

}
