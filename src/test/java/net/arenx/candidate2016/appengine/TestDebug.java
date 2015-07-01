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
		
		Calendar calendar=Calendar.getInstance();
		System.out.println(calendar.getTime());
		
		calendar.add(Calendar.MONTH, -2);
//		calendar.set(Calendar.DAY_OF_MONTH, 1);
//		calendar.set(Calendar.HOUR_OF_DAY, 0);
//		calendar.set(Calendar.MINUTE, 0);
//		
//		calendar.set(Calendar.SECOND, 0);
//		calendar.set(Calendar.MILLISECOND, 0);
		
		System.out.println(calendar.getTime());
	}
	
	public static class TestKey{
		
		@PrimaryKey
		public String name;
	}
}
