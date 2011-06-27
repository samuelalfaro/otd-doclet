/* 
 * PruebaHTMLCleaner.java
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

import java.io.IOException;

import org.sam.html.Cleaner;
import org.sam.html.HTMLFormater;
import org.sam.html.HTMLSerializer;
import org.sam.xml.XMLWriter;

/**
 * 
 */
public class PruebaHTML_TO_ODT{

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main( String[] args ) throws IOException{
		String html =
			"<b><i>hola </i>/</b> <u>qué  tal?<b>muy bien</u> tocando <br>\n" +
			"los cojones</b> bla <u>bla <i>bla</u> ldsfas <a href=\"www.dir.org\" fasdf=\"fasdhfkj\">link</a>";
		HTMLFormater FORMATER = new Cleaner( HTMLSerializer.Default );
		System.out.println( FORMATER.format( html ) );
		XMLWriter writer = new XMLWriter((Appendable)System.out);
		writer.openNode( "text:p" );
		ODTRecorders.FORMATER.format( html, writer );
		writer.closeNode();
	}
}
