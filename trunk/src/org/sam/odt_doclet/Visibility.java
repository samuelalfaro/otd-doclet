/* 
 * Visibility.java
 * 
 * Copyright (c) 2012 Samuel Alfaro Jiménez <samuelalfaro at gmail dot com>.
 * All rights reserved.
 * 
 * This file is part of odt-doclet.
 * 
 * odt-doclet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * odt-doclet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with odt-doclet.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.odt_doclet;

import java.lang.reflect.Modifier;

/**
 * 
 */
public enum Visibility{
	
	Public('+'),
	Protected('#'),
	Package('~'),
	Private('-');
	
	/**
	 * Method fromModifiers.
	 * @param att int
	 * @return Visibility
	 */
	public static Visibility fromModifiers( int att ){
		if( Modifier.isPublic( att ) )
			return Public;
		if( Modifier.isProtected( att ) )
			return Protected;
		if( Modifier.isPrivate( att ) )
			return Private;
		return Package;
	}
	
	private final char c;
	
	private Visibility(char c){
		this.c = c;
	}
	
	/**
	 * Method toChar.
	 * @return char
	 */
	public final char toChar(){
		return c;
	}
}