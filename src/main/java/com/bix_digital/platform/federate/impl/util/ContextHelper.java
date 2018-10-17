package com.bix_digital.platform.federate.impl.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.stereotype.Service;

@Service
public class ContextHelper 
{
	@Autowired
	public ContextHelper(@Value ("${run.local:true}") boolean runLocal) throws Exception 
	{
		// @ FIXME container vs standalone org.springframework.jndi.JndiTemplate
		if (runLocal)
		{
			SimpleNamingContextBuilder template = new SimpleNamingContextBuilder();
			template.activate();
		}
	}
	
	public void bind (String name, Object object) throws NamingException {
		InitialContext context = new InitialContext();
		context.bind(name, object);
	}
	
	public Object lookup (String name) throws NamingException {
		InitialContext context = new InitialContext();
		return context.lookup(name);
	}

}
