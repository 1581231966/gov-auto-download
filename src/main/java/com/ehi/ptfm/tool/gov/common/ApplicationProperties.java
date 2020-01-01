package com.ehi.ptfm.tool.gov.common;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

public class ApplicationProperties {

	private static Properties props;

	private ApplicationProperties(){

	}

	private static void init(){
		Resource resource = new ClassPathResource("application.properties");
		try {
			props = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getProperties(String key){
		if (props == null){
			init();
		}
		return props.getProperty(key);
	}
}
