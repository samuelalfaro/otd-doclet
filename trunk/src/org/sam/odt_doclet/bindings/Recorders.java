/* 
 * Recorders.java
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


import org.sam.xml.XMLConverter;

/**
 * 
 */
public abstract class Recorders {
	
	public enum Mode{
		SVG, XHTML, ODT, XML
	}
	
	Recorders(){}
	
	abstract void register( XMLConverter converter );
	
	public static void register( Mode mode, XMLConverter converter ){
		switch(mode){
		case SVG:
			throw new UnsupportedOperationException();
		case XHTML:
			throw new UnsupportedOperationException();
		case ODT:
			new ODTRecorders().register( converter );
			break;
		default:
			new XMLRecorders().register( converter );
		}
	}
}
