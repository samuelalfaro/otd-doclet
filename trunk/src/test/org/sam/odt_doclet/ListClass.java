/* 
 * ListClass.java
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

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

/**
 */
@Deprecated
public class ListClass {
	
	private static final Comparator<PackageDoc> COMPARADOR_DE_PACKAGES = new Comparator<PackageDoc>() {
		/** {@inheritDoc} */
		public int compare(PackageDoc e1, PackageDoc e2) {
			return e1.name().compareTo(e2.name());
		}
	};
	
	private static final Comparator<ClassDoc> COMPARADOR_POR_NOMBRE = new Comparator<ClassDoc>(){
		/** {@inheritDoc} */
		public int compare( ClassDoc e1, ClassDoc e2 ){
			return e1.name().compareTo( e2.name() );
		}
	};
	
	private static final Comparator<ClassDoc> COMPARADOR_DE_CLASES = new Comparator<ClassDoc>() {
		
		private String getHierarchicalName( ClassDoc classDoc ){
			String name = classDoc.simpleTypeName();
			ClassDoc containingClass = classDoc.containingClass();
			while( containingClass != null ){
				if( containingClass.containingClass() != null )
					name = containingClass.simpleTypeName() + "." + classDoc.simpleTypeName();
				else
					name = getHierarchicalName( containingClass ) + "." + name;
				containingClass = containingClass.containingClass();
			}
			ClassDoc superClassDoc = classDoc.superclass();
			if( superClassDoc != null
					&& classDoc.containingPackage().name().equals( superClassDoc.containingPackage().name() ) ){
				name = getHierarchicalName( superClassDoc ) + "/" + name;
			}
			return name;
		}
		
		/** {@inheritDoc} */
		public int compare( ClassDoc e1, ClassDoc e2 ){
			return getHierarchicalName( e1 ).compareTo( getHierarchicalName( e2 ) );
		}
	};
	
	private static final PrintStream OUT = System.out;
	
	private static void print( String title, Collection<ClassDoc> collection ){
		if( collection.size() > 0 ){
			OUT.println( title );
			for( ClassDoc classDoc: collection )
				OUT.println( "\t" + classDoc.name() );
		}
	}
	
	/**
	 * Method start.
	 * @param root RootDoc
	 * @return boolean
	 */
	public static boolean start(RootDoc root) {
		
		ClassDoc[] classes = root.classes();
		for(ClassDoc classDoc: classes){
			if(classDoc.containingPackage().name().equals(""))
				OUT.println("\t"+classDoc.name());
		}

		SortedSet<PackageDoc> sortedPackages = new TreeSet<PackageDoc>(COMPARADOR_DE_PACKAGES);
		sortedPackages.addAll( Arrays.asList(root.specifiedPackages()) );

		for(PackageDoc packageDoc: sortedPackages){
			OUT.println(packageDoc+"\n");
			SortedSet<ClassDoc> sortedInterfaces = new TreeSet<ClassDoc>(COMPARADOR_POR_NOMBRE);
			SortedSet<ClassDoc> sortedClasses    = new TreeSet<ClassDoc>(COMPARADOR_DE_CLASES);
			SortedSet<ClassDoc> sortedException  = new TreeSet<ClassDoc>(COMPARADOR_POR_NOMBRE);
			SortedSet<ClassDoc> sortedEnums      = new TreeSet<ClassDoc>(COMPARADOR_POR_NOMBRE);

			sortedInterfaces.addAll(Arrays.asList(packageDoc.interfaces()));
			sortedException.addAll(Arrays.asList(packageDoc.exceptions()));
			sortedClasses.addAll(Arrays.asList(packageDoc.ordinaryClasses()));
			sortedEnums.addAll(Arrays.asList(packageDoc.enums()));
			
			print("Interfaces:", sortedInterfaces);
			print("Exceptions:", sortedException);
			
			if( sortedClasses.size() > 0 ){
				OUT.println( "Classes:" );
				for( ClassDoc classDoc: sortedClasses ){
					
					if( classDoc.superclass().qualifiedName().equals( "java.lang.Enum" ) ){
						ClassDoc containingClass = classDoc.containingClass();
						while( containingClass != null ){
							OUT.print( '\t' );
							containingClass = containingClass.containingClass();
						}
						OUT.println( "\t" + classDoc.name() );
//						OUT.println(classDoc.isEnum());
//						OUT.println(classDoc.isEnumConstant());
//						OUT.println(classDoc.getRawCommentText());
//						OUT.println(classDoc.enumConstants().length);
						
						for( FieldDoc field: classDoc.fields( true ) )
							if(!field.type().isPrimitive() && field.type().asClassDoc().superclass() != null && field.type().asClassDoc().superclass().qualifiedName().equals("java.lang.Enum")){
								OUT.println("\t<<Constant>>" +field.name());
							}else
								OUT.println("\t"+field.type()+ " " +field.name());
					}
//					OUT.println("\t"+classDoc.name());
//					for(FieldDoc field: classDoc.fields(true))
//						OUT.println("\t"+field.name());
//					for(ConstructorDoc constructor: classDoc.constructors(true))
//						OUT.println("\t"+constructor.name());
//					for(MethodDoc method: classDoc.methods(true))
//						OUT.println("\t"+method.name());
					
				}
			}
			print("Enumerations:", sortedEnums);
			OUT.println();
		}

		return true;
	}
    
}