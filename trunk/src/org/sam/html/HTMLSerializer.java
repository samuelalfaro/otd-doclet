/* 
 * HTMLSerializer.java
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
import java.util.Map;

import org.htmlcleaner.ContentNode;
import org.htmlcleaner.TagNode;
import org.sam.xml.XMLWriter;

public interface HTMLSerializer{

	final HTMLSerializer Default = new HTMLSerializer(){
		
		public void serialize( TagNode node, XMLWriter writer ) throws IOException{
			for( Object item: node.getChildren() ){
				if( item instanceof ContentNode ){
					writer.write( item.toString() );
				}else if( item instanceof TagNode ){
					TagNode tagNode = (TagNode)item;
					String nodeName = tagNode.getName();
					writer.openNode( nodeName );
						Map<String, String> att = tagNode.getAttributes();
						for( String name: att.keySet() )
							writer.addAttribute( name, att.get( name ) );
						if( tagNode.hasChildren() )
							serialize( tagNode, writer );
					writer.closeNode();
				}
			}
		}
	};

	public void serialize( TagNode node, XMLWriter writer ) throws IOException;
}