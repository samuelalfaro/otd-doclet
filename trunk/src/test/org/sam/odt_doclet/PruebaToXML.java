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
package org.sam.odt_doclet;

import java.util.List;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.CleanerTransformations;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagTransformation;

/**
 * 
 */
public class PruebaToXML {

	
	private static void toString(TagNode node, StringBuffer buff) {
		String name = node.getName();
		List<?> childs = node.getChildren();
		if(childs.size() > 0){
			buff.append('<').append(name).append('>');
			for (Object child : childs) {
				if (child instanceof TagNode)
					toString(((TagNode)child), buff);
				else if (child instanceof ContentNode)
					buff.append(child.toString());
			}
			buff.append('<').append('/').append(name).append('>');
		}else
			buff.append('<').append(name).append('/').append('>');
	}
	
	
	private static void toString(TagNode[] nodes, StringBuffer buff) {
		for(TagNode node: nodes)
			toString(node, buff);
	}
	
	/**
	 * Method toString.
	 * @param node TagNode
	 * @return String
	 */
	public static String toString(TagNode node){
		StringBuffer buff = new StringBuffer();
		toString(node.getElementsByName("body", true), buff);
		return buff.toString();
	}

	/**
	 * Method main.
	 * @param args String[]
	 */
	public static void main(String[] args) {

		String s="Esto es un\na <b>prueba<i> de un <p>texto</b> mal form<br>ado</i> y <p>con simbolos raros</p> < y & > en html.";
		
		CleanerProperties  props = new CleanerProperties();
		// set some properties to non-default values
		props.setOmitXmlDeclaration(true);
		props.setOmitComments(true);
		props.setTranslateSpecialEntities(true);
//		props.setTransResCharsToNCR(true);

		HtmlCleaner cleaner;
		cleaner = new HtmlCleaner(props);
		
		TagNode node = cleaner.clean(s);
		System.out.println(toString(node));
		
		CleanerTransformations transformations = new CleanerTransformations();
		
		TagTransformation tt;
		
		tt = new TagTransformation("br", "text:line-break", false);
		transformations.addTransformation(tt);
		
		tt = new TagTransformation("b", "text:span", true);
		tt.addAttributeTransformation(
		    "text:style-name", 
		    "TBold"
		);
		transformations.addTransformation(tt);

		
		tt = new TagTransformation("i", "text:span", true);
		tt.addAttributeTransformation("text:style-name=\"TItalic\"");
		transformations.addTransformation(tt);
		
		cleaner.setTransformations(transformations);
		
		System.out.println(toString(cleaner.clean(toString(node))));
	}
}
