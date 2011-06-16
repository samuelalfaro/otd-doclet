/* 
 * ClassBindingFactory.java
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
package org.sam.odt_doclet.bindings;

import com.sun.javadoc.ClassDoc;

/**
 */
public final class ClassBindingFactory{

	/**
	 * Method setClassLoader.
	 * @param classLoader ClassLoader
	 */
	public static void setClassLoader(ClassLoader classLoader){
		Utils.setClassLoader(classLoader);
	}

	private static ClassBinding newInstance( Class<?> clazz, ClassDoc classDoc ){
		if(clazz.isInterface())
			return new ClassBinding.Interface( clazz, classDoc );
		if(clazz.isEnum())
			return new ClassBinding.Enum( clazz, classDoc );
		return new ClassBinding.Clazz( clazz, classDoc );
	}
	
	/**
	 * Method createBinding.
	 * @param clazz Class<?>
	 * @return ClassBinding
	 */
	public static ClassBinding createBinding( Class<?> clazz ){
		return newInstance( clazz, null );
	}
	
	/**
	 * Method createBinding.
	 * @param classDoc ClassDoc
	 * @return ClassBinding
	 * @throws ClassNotFoundException
	 */
	public static ClassBinding createBinding( ClassDoc classDoc ) throws ClassNotFoundException{
		return newInstance( Utils.find(classDoc), classDoc );
	}
	
	/**
	 * Method createBinding.
	 * @param clazz Class<?>
	 * @param classDoc ClassDoc
	 * @return ClassBinding
	 * @throws ClassNotFoundException
	 */
	public static ClassBinding createBinding( Class<?> clazz, ClassDoc classDoc ) throws ClassNotFoundException{
		return newInstance( Utils.find( clazz, classDoc.qualifiedName() ), classDoc );
	}
	
}
