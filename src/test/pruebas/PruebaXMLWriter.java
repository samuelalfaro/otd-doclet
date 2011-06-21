/* 
 * PruebaXMLWriter.java
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
package pruebas;

import java.io.IOException;

import org.sam.xml.XMLWriter;

public class PruebaXMLWriter{

	public static void main( String... args ) throws IOException{
		XMLWriter writer = new XMLWriter( (Appendable)System.out, 4 );
		
		writer.openNode( "text:p" );
		writer.addAttribute( "text:style-name", "<&~Estilo>" );
		writer.write( "bla bla bla bla << \" ' > &" );
		writer.emptyNode( "text:line-break" );
		writer.writeCDATA( "bla bla bla 2" );
		writer.insert( "<bla>bla bla bla</bla>" );
		writer.closeNode();
	}
}
