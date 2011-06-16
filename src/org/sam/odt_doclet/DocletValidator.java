/* 
 * ShowDocumentation.java
 * 
 * Copyright (c) 2011 Samuel Alfaro Jiménez <samuelalfaro at gmail dot com>.
 * All rights reserved.
 * 
 * This file is part of odf-doclet.
 * 
 * odf-doclet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * odf-doclet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with odf-doclet.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.odt_doclet;

import org.sam.odt_doclet.bindings.ClassBindingFactory;

import com.sun.javadoc.DocErrorReporter;

/**
 */
abstract class DocletValidator {
	
	DocletValidator(){}
	
	/**
	 * Method optionLength.
	 * @param option String
	 * @return int
	 */
	public static final int optionLength(String option) {
		if (option.equalsIgnoreCase("-projectClasspath")) 
			return 2;
		if (option.equalsIgnoreCase("-projectLibpath"))
			return 2;
		return 0;
	}
	
	/**
	 * Method validOptions.
	 * @param options String[][]
	 * @param reporter DocErrorReporter
	 * @return boolean
	 */
	public static final boolean validOptions(String options[][], DocErrorReporter reporter) {
		
		String projectClasspath = null;
		String projectLibpath = null;
		
		for(String[] option: options){
			if(option[0].equalsIgnoreCase("-projectClasspath")){
				if(projectClasspath != null){
					reporter.printError("Only one -projectClasspath option allowed.");
					return false;
				}
				projectClasspath = option[1];
			}else if(option[0].equalsIgnoreCase("-projectLibpath")){
				if(projectLibpath != null){
					reporter.printError("Only one -projectLibpath option allowed.");
					return false;
				}
				projectLibpath = option[1];
			}
		}
		ClassBindingFactory.setClassLoader(ClassLoaderTools.getLoader( projectClasspath, projectLibpath ) );
		return true;
	}
}