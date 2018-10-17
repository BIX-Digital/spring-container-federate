package com.bix_digital.platform.federate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.bix_digital.platform.federate.controller.TestController;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes={DemoApplicationConfiguration.class})
public class DemoApplicationTests  {

	@Autowired
	private TestController controller;
	
	@Autowired
	private ApplicationContext context;
	
	@Test
	public void verifyInject () 
	{
		// container aware 
		assertNotNull(controller.someTestService);
		assertTrue(controller.someTestService.isContainerAware());
				
		// container non aware
		assertNotNull(controller.localService);
		assertFalse(controller.localService.isContainerAware());		
	}
}