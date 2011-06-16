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
package pruebas;

import java.util.Map;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

/**
 * 
 */
public class PruebaHTMLCleaner{

	private static final HtmlCleaner cleaner;
	    
	static{
		CleanerProperties  props = new CleanerProperties();
		// set some properties to non-default values
		props.setTranslateSpecialEntities(true);
		props.setTransResCharsToNCR(true);
		props.setOmitComments(true);
	
		cleaner = new HtmlCleaner(props);
	}
	
	static void toStringRecursive( TagNode node, StringBuffer buff ){
		for( Object item: node.getChildren() ){
			if( item instanceof ContentNode ){
				buff.append( item.toString() );
			}else if( item instanceof TagNode ){
				TagNode tagNode = (TagNode)item;
				String nodeName = tagNode.getName();
				buff.append( '<' );
				buff.append( nodeName );
				Map<String, String> att = tagNode.getAttributes();
				for( String name: att.keySet() ){
					buff.append( ' ' );
					buff.append( name );
					buff.append( "=\"" );
					buff.append( att.get( name ) );
					buff.append( "\"" );
				}
				if( !tagNode.hasChildren() )
					buff.append( " />" );
				else{
					buff.append( '>' );
					toStringRecursive( tagNode, buff );
					buff.append( "</" );
					buff.append( nodeName );
					buff.append( '>' );
				}
			}
		}
	}

	static String toString( TagNode node ){
		StringBuffer buff = new StringBuffer();
		for( Object item: node.getChildren() ){
			if( item instanceof TagNode ){
				if( ( (TagNode)item ).getName().equals( "body" ) )
					toStringRecursive( (TagNode)item, buff );
			}
		}
		return buff.toString();
	}

	static String clean( String s ){
		if( s == null || s.length() == 0 )
			return "";
		return toString( cleaner.clean( s ) );
	}
	
	/**
	 * @param args
	 */
	public static void main( String[] args ){
		System.out.println( clean(
				"hola <u>qué tal?<b>muy bien</u> tocando <br>\n" +
				"los cojones</b> bla <u>bla <i>bla</u> ldsfas <a href=\"www.dir.org\" fasdf=\"fasdhfkj\">link</a>"
		));
	}
}
