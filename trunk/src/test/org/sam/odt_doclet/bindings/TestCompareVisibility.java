/* 
 * TestCompareVisibility.java
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

import org.sam.odt_doclet.Visibility;


/**
 */
public class TestCompareVisibility {

	private static <T> void printComparation(Comparable<T> c1, T c2){
		if( c1.compareTo(c2) > 0)
			System.out.println(c1 +" es mayor que "+c2);
		else if( c1.compareTo(c2) == 0 )
			System.out.println(c1 +" es igual a "+c2);
		else
			System.out.println(c1 +" es menor que "+c2);
	}
	
	/**
	 * Method main.
	 * @param args String[]
	 */
	public static void main(String... args) {

		Visibility v1 = Visibility.Public;
		Visibility v2 = Visibility.Protected;
		Visibility v3 = Visibility.Package;
		Visibility v4 = Visibility.Private;
		
		printComparation(v1, Visibility.Public);
		printComparation(v1, Visibility.Protected);
		printComparation(v1, Visibility.Package);
		printComparation(v1, Visibility.Private);
		
		System.out.println();
		printComparation(v2, Visibility.Public);
		printComparation(v2, Visibility.Protected);
		printComparation(v2, Visibility.Package);
		printComparation(v2, Visibility.Private);
		
		System.out.println();
		printComparation(v3, Visibility.Public);
		printComparation(v3, Visibility.Protected);
		printComparation(v3, Visibility.Package);
		printComparation(v3, Visibility.Private);
		
		System.out.println();
		printComparation(v4, Visibility.Public);
		printComparation(v4, Visibility.Protected);
		printComparation(v4, Visibility.Package);
		printComparation(v4, Visibility.Private);

	}
}
