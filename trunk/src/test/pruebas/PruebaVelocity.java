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
import java.util.ArrayList;
import java.util.Collection;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class PruebaVelocity{
	
	public static interface Persona{
		
		/**
		 * @return el nombre solicitado.
		 */
		String getNombre();

		/**
		 * @return el apellidos solicitado.
		 */
		String getApellidos();
	}
	
	private static class Tipejo implements Persona{
		
		String nombre;
		String apellidos;
		
		Tipejo( String nombre, String apellidos ){
			this.nombre = nombre;
			this.apellidos = apellidos;
		}
		
		/* (non-Javadoc)
		 * @see pruebas.PruebaVelocity.Persona#getNombre()
		 */
		public String getNombre(){
			return nombre;
		}

		/* (non-Javadoc)
		 * @see pruebas.PruebaVelocity.Persona#getApellidos()
		 */
		public String getApellidos(){
			return apellidos;
		}
	}
	
	private static class Punk implements Persona{
		
		String alias;
		
		Punk( String alias ){
			this.alias = alias;
		}
		
		/* (non-Javadoc)
		 * @see pruebas.PruebaVelocity.Persona#getNombre()
		 */
		public String getNombre(){
			return alias;
		}

		/* (non-Javadoc)
		 * @see pruebas.PruebaVelocity.Persona#getApellidos()
		 */
		public String getApellidos(){
			return null;
		}
	}
	
	public static final class Tools{
		
		private Tools(){}
		
		public static int stringLen(String arg){
			return arg == null ? -1 : arg.length();
		}
		
		public static String uperCase(String arg){
			return arg == null ? null: arg.toUpperCase();
		}
	}
	
	public static void main( String args[] ) throws Throwable{
		
		/* first, we init the runtime engine. Defaults are fine. */

		Velocity.init();

		Template template = Velocity.getTemplate( "template.vm", "UTF-8" );
		/* lets make a Context and put data into it */

		VelocityContext context = new VelocityContext();

		context.put( "name", "Velocity" );
		context.put( "project", "Jakarta" );
		
		Collection<Persona> personas = new ArrayList<Persona>();
		personas.add( new Tipejo( "Mario", "Conde" ) );
		personas.add( new Punk ( "El Tripi") );
		personas.add( new Tipejo( "Emilio", "Botín" ) );
		personas.add( new Punk ( "La Farly") );
		context.put( "personas", personas );
		context.put( "Tools", Tools.class );
		context.put( "Math", Math.class );
		context.put( "newLine", "\n" );
		
		/* lets render a template */

		StringWriter writer = new StringWriter();
		
		template.merge( context, writer );
		System.out.println( "Template:\n-->" + writer.getBuffer().toString() + "<--" );

		/* lets make our own string to render */

		String s = "We are using $project $name to render this.";
		writer = new StringWriter();
		Velocity.evaluate( context, writer, "mystring", s );
		System.out.println( "String:\n-->" + writer.getBuffer().toString() + "<--" );
	}

}
