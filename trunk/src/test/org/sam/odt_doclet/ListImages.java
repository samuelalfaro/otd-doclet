/* 
 * ListImages.java
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

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.sam.odt_doclet.Graphic;
import org.sam.odt_doclet.Loader;

public class ListImages{
	
	static final Charset UTF8 = Charset.forName( "UTF-8" );
	
	private static String readTemplate( InputStream in ) throws IOException {
		StringBuffer template = new StringBuffer();
		byte[] buf = new byte[4096];
		int len;
		while( ( len = in.read( buf ) ) > 0 ){
			template.append( new String( buf, 0, len, UTF8 ) );
		}
		return template.toString();
	}
	
	public static void main( String args[] ) throws Throwable{
		
		/* first, we init the runtime engine. Defaults are fine. */

		Velocity.init();

		String template = readTemplate( Loader.getResourceAsStream( "/resources/toODT.vm" ) );
		/* lets make a Context and put data into it */

		VelocityContext context = new VelocityContext();
		Collection<Graphic> graphics = new LinkedList<Graphic>();
		graphics.add( new Graphic( "nombre1", "ruta1", new Dimension(200,300), 72 ) );
		graphics.add( new Graphic( "nombre2", "ruta2", new Dimension(200,300), 72 ) );
		graphics.add( new Graphic( "nombre3", "ruta3", new Dimension(200,300), 72 ) );
		graphics.add( new Graphic( "nombre4", "ruta4", new Dimension(200,300), 72 ) );
		
		context.put( "graphics", graphics );
		
		/* lets render a template */

		StringWriter writer = new StringWriter();
		
		Velocity.evaluate( context, writer, "toODT.vm", template );
		System.out.println( writer.getBuffer().toString() + "<--" );

	}
}
