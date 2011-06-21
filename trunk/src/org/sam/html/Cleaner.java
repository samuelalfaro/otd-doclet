/* 
 * Cleaner.java
 * 
 * Copyright (c) 2011 Samuel Alfaro Jiménez <samuelalfaro at gmail dot com>.
 * All rights reserved.
 * 
 * This file is part of odt-doclet.
 * 
 * odt-doclet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * odt-doclet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with odt-doclet.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.html;

import java.io.IOException;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.sam.xml.XMLWriter;

public final class Cleaner implements HTMLFormater {
	
	private final HtmlCleaner cleaner;
	private final HTMLSerializer serializer;
	
	public Cleaner( HTMLSerializer serializer ){
		CleanerProperties  props = new CleanerProperties();
		// set some properties to non-default values
		props.setTranslateSpecialEntities(true);
		props.setTransResCharsToNCR(true);
		props.setOmitComments(true);
		
		this.cleaner = new HtmlCleaner(props);
		this.serializer = serializer;
	}

	private String toString( TagNode node ) throws IOException{
		StringBuffer buff = new StringBuffer();
		for( Object item: node.getChildren() ){
			if( item instanceof TagNode ){
				if( ((TagNode)item).getName().equals( "body" ) )
					serializer.serialize( (TagNode)item, new XMLWriter( buff ) );
			}
		}
		return buff.toString();
	}
	
	public String format( String s ){
		String result = "";
		if( s != null && s.length() > 0 )
			try{
				result = toString( cleaner.clean( s ) );
			}catch( IOException e ){
				e.printStackTrace();
			}
		return result;
	}
	
}