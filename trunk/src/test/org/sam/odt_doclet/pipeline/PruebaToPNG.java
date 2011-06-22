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

import org.sam.odt_doclet.bindings.ClassBindingFactory;

/**
 * 
 */
public class PruebaToPNG {

	/**
	 * @param args
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		
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
	
		PipeLine.toXML( ClassBindingFactory.createBinding( clazz ), System.out );
		System.out.print("\nGenerando gráfico de "+clazz.getSimpleName()+" ...");
		PipeLine.toPNG( ClassBindingFactory.createBinding( clazz ), new FileOutputStream( "output/out.png" ) );
		System.out.println("\tok");
	}
}
