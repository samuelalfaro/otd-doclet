/* 
 * ToSVG.java
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

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class ToSVG{
	
	public static void main( String args[] ) throws Throwable{
		
		/* first, we init the runtime engine. Defaults are fine. */

		Velocity.init();

		Template template = Velocity.getTemplate( "/resources/shared/toSVG.vm", "UTF-8" );
		/* lets make a Context and put data into it */

		VelocityContext context = new VelocityContext();

		context.put( "newLine", "\n" );
		
		/* lets render a template */

		StringWriter writer = new StringWriter();
		
		template.merge( context, writer );
		System.out.println( writer.getBuffer().toString() + "<--" );

	}
}
