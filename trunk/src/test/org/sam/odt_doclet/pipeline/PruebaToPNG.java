/* 
 * PruebaToPNG.java
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
package org.sam.odt_doclet.pipeline;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.TransformerConfigurationException;

import org.sam.odt_doclet.bindings.ClassBinding;
import org.sam.odt_doclet.bindings.ClassBindingFactory;
import org.sam.odt_doclet.bindings.Recorders;
import org.sam.xml.XMLConverter;
import org.sam.xml.XMLWriter;

/**
 * 
 */
public class PruebaToPNG {

	/**
	 * @param args
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws TransformerConfigurationException
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, TransformerConfigurationException {
		
		Class<?> clazz;
		/*
		ClassLoader loader = org.sam.odt_doclet.ClassLoaderTools.getLoader( 
			"/media/DATA/Samuel/Proyectos/jspacewars/bin", 
			"/media/DATA/Samuel/Proyectos/jspacewars/lib");
		clazz = Class.forName( "org.sam.jogl.ObjLoader", false, loader );
		for(Class<?> subClass: clazz.getDeclaredClasses()){
			if(subClass.getCanonicalName().equals("org.sam.jogl.ObjLoader.Primitive")){
				clazz = subClass;
				break;
			}
		}
		/*/
		clazz = pruebas.ClaseDePrueba.class;
		//*/
		ClassBinding clazzBinding = ClassBindingFactory.createBinding( clazz );
		
		XMLConverter converter = new XMLConverter();
		Recorders.register( Recorders.Mode.XML, converter );
		UMLDiagramGenerator generator = new UMLDiagramGenerator();
	
		converter.setWriter( new XMLWriter( (Appendable)System.out ) );
		converter.write( clazzBinding );
		
		System.out.print("\nGenerando gráfico de "+clazz.getSimpleName()+" ...");
		generator.toPNG( clazzBinding, new FileOutputStream( "output/out.png" ) );
		System.out.println("\tok");
	}
}
