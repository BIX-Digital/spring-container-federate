package com.bix_digital.platform.federate.impl.util;

import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.stereotype.Service;

@Service
/**
 * Class to ensure local and also inside a spring boot tomcat context binding does work,
 * wraps initial context magic
 * @author utschig
 */
public class InitialContextHelper 
{
	
	private final Logger logger = LoggerFactory.getLogger(InitialContextHelper.class);
	
	@Value ("${application.name:global}")
	private String appContextName;
	
	private final List<String> ownedServiceNames = new ArrayList<>();
	
	@Autowired
	public InitialContextHelper(@Value ("${run.local:true}") boolean runLocal) throws Exception 
	{
		// @ FIXME container vs standalone org.springframework.jndi.JndiTemplate
		if (runLocal)
		{
			SimpleNamingContextBuilder template = new SimpleNamingContextBuilder();
			template.activate();
		}
	}
	
	/**
	 * Bind an object into the context
	 * @param name the unique name
	 * @param object the object to bind
	 * @throws NamingException in case binding does not work
	 */
	public void bind (String name, Object object) throws NamingException {
		InitialContext context = new InitialContext();
		logger.debug("Binding object with name " + name + 
				" namespace: " + this.appContextName + " ctx " + 
				context.getEnvironment());
		context.bind(this.appContextName + "-" + name, object);
		this.ownedServiceNames.add(name);
	}
	
	public Object lookup (String name) throws NamingException {
		InitialContext context = new InitialContext();
		logger.debug("Attempting to find object with name " + name + 
				" namespace: " + this.appContextName + " ctx " + 
				context.getEnvironment());
		return context.lookup(this.appContextName + "-" +name);
	}

	public void unbind (String name) throws NamingException {
		InitialContext context = new InitialContext();
		logger.debug("Attempting to remove object with name " + name + 
				" namespace: " + this.appContextName + " ctx " + 
				context.getEnvironment());
		context.unbind(this.appContextName + "-" + name);
	}	
	
	@EventListener
	public synchronized void closeApplicationContext (ContextClosedEvent event) 
	{
		for (String serviceName : ownedServiceNames) 
		{
			try 
			{
				logger.debug("Unbinding service '" + serviceName + "'");
				unbind(serviceName);
			} catch (NamingException unbindEx) {
				logger.error("Could not unbind service '" + serviceName + "' Error: " + 
					unbindEx.getMessage());
			}
		}
	}
}
