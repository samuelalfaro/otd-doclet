/* 
 * DocumentedElement.java
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
package org.sam.odt_doclet.bindings;

import java.util.Collection;

import com.sun.javadoc.Doc;

/**
 */
public abstract class DocumentedElement {
	
	final String name;
	final String documentation;
	final Collection<LinkBinding> links;

	/**
	 * Constructor for DocumentedElement.
	 * @param name String
	 * @param doc Doc
	 */
	DocumentedElement( String name, Doc doc ){
		this.name = name;
		this.documentation = Utils.getDocumentation( doc );
		this.links = Utils.getLinks( doc );
	}

	/**
	 * Constructor for DocumentedElement.
	 * @param name String
	 */
	DocumentedElement( String name ){
		this( name, null );
	}
}