package net.arenx.candidate2016.appengine;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import net.arenx.candidate2016.jdo.LocationEntity;
import net.arenx.candidate2016.jdo.statistics.VotedCount;

public class TestDebug {

	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
		
		for(Field f:LocationEntity.class.getFields()){
			System.out.println(f);
		}
		
		TestKey l =new TestKey();
		
		for(Field f:TestKey.class.getDeclaredFields()){
			System.out.println(f);
			if(f.isAnnotationPresent(PrimaryKey.class)){
				System.out.println("get key field");
				System.out.println(f.get(l));
			}
		}
	}
	
	public static class TestKey{
		
		@PrimaryKey
		public String name;
	}
}
