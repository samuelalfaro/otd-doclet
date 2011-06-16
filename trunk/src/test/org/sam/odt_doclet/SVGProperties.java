/* 
 * SVGProperties.java
 * 
 * Copyright (c) 2010 Samuel Alfaro Jiménez <samuelalfaro at gmail dot com>.
 * All rights reserved.
 * 
 * This file is part of tips.
 * 
 * tips is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * tips is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with tips.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.odt_doclet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 */
@Deprecated
//TODO mirar plantillas velocity
public class SVGProperties{
	
	private SVGProperties() {
	}
	
	private static final Properties RESOURCES = new Properties();
	private static final Properties TEMPLATES = new Properties();
	
	static{
		FileInputStream in;
		try {
			in = new FileInputStream("SVGProperties.properties");
			RESOURCES.load(in);
			in.close();
			in = new FileInputStream("SVGTemplates.properties");
			TEMPLATES.load(in);
			in.close();
//			test
//			for( java.util.Map.Entry<Object,Object> entry: TEMPLATES.entrySet() )
//				System.out.println( entry.getKey() + ": " + entry.getValue() );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method getProperty.
	 * @param key String
	 * @return String
	 */
	public static String getProperty(String key) {
		return RESOURCES.getProperty(key);
	}
	
	/**
	 * Method getProperty.
	 * @param key String
	 * @param defaultValue int
	 * @return int
	 */
	public static int getProperty(String key, int defaultValue) {
		try {
			return Integer.parseInt( RESOURCES.getProperty(key) );
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Method getProperty.
	 * @param key String
	 * @param defaultValue float
	 * @return float
	 */
	public static float getProperty(String key, float defaultValue) {
		try {
			return Float.parseFloat( RESOURCES.getProperty(key) );
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Method getTemplate.
	 * @param key String
	 * @return String
	 */
	public static String getTemplate(String key) {
		return TEMPLATES.getProperty(key);
	}
}
