/* 
 * XMLDoclet.java
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
import org.sam.odt_doclet.bindings.Recorders;
import org.sam.xml.XMLConverter;
import org.sam.xml.XMLWriter;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.RootDoc;

/**
 */
public final class XMLDoclet{
	
	/**
	 * Method optionLength.
	 * @param option String
	 * @return int
	 */
	public static int optionLength( String option ){
		return DocletValidator.optionLength( option );
	}
	
	/**
	 * Method validOptions.
	 * @param options String[][]
	 * @param reporter DocErrorReporter
	 * @return boolean
	 */
	public static boolean validOptions( String options[][], DocErrorReporter reporter ){
		return DocletValidator.validOptions( options, reporter );
	}
	
	/**
	 * Method start.
	 * @param root RootDoc
	 * @return boolean
	 * @throws ClassNotFoundException
	 */
	public static boolean start( RootDoc root ) throws ClassNotFoundException{

		ClassDoc[] classes = root.classes();

		XMLConverter converter = new XMLConverter();
		Recorders.register( Recorders.Mode.XML, converter );
		converter.setWriter( new XMLWriter( System.out, true ) );

		for( ClassDoc classDoc: classes )
			converter.write( ClassBindingFactory.createBinding( classDoc ) );

		return true;
	}
}