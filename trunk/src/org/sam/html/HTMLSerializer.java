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

	final HTMLSerializer TO_ODT = new HTMLSerializer(){
		public void serialize( TagNode node, XMLWriter writer ) throws IOException{
			for( Object item: node.getChildren() ){
				if( item instanceof ContentNode ){
					writer.write( item.toString() );
				}else if( item instanceof TagNode ){
					TagNode tagNode = (TagNode)item;
					String nodeName = tagNode.getName();

					if( nodeName.equalsIgnoreCase( "br" ) ){
						writer.emptyNode( "text:line-break" );
					}else if( nodeName.equalsIgnoreCase( "b" ) || nodeName.equalsIgnoreCase( "strong" ) ){
						writer.openNode( "text:span" );
							writer.addAttribute( "text:style-name", "TBold" );
							serialize( tagNode, writer );
						writer.closeNode();
					}else if( nodeName.equalsIgnoreCase( "i" ) || nodeName.equalsIgnoreCase( "em" ) ){
						writer.openNode( "text:span" );
							writer.addAttribute( "text:style-name", "TItalic" );
							serialize( tagNode, writer );
						writer.closeNode();
					}else if( nodeName.equalsIgnoreCase( "u" ) ){
						writer.openNode( "text:span" );
							writer.addAttribute( "text:style-name", "TUnderline" );
							serialize( tagNode, writer );
						writer.closeNode();
					}else if( nodeName.equalsIgnoreCase( "tt" ) ){
						writer.openNode( "text:span" );
							writer.addAttribute( "text:style-name", "TMonospaced" );
							serialize( tagNode, writer );
						writer.closeNode();
					}else if( nodeName.equalsIgnoreCase( "code" ) ){
						writer.openNode( "text:span" );
							writer.addAttribute( "text:style-name", "TCode" );
							serialize( tagNode, writer );
						writer.closeNode();
					}else if( nodeName.equalsIgnoreCase( "sup" ) ){
						writer.openNode( "text:span" );
							writer.addAttribute( "text:style-name", "TSup" );
							serialize( tagNode, writer );
						writer.closeNode();
					}else if( nodeName.equalsIgnoreCase( "sub" ) ){
						writer.openNode( "text:span" );
							writer.addAttribute( "text:style-name", "TSub" );
							serialize( tagNode, writer );
						writer.closeNode();
					}else if( nodeName.equalsIgnoreCase( "a" ) ){
						writer.openNode( "text:a" );
							writer.addAttribute( "xlink:type", "simple" );
							writer.addAttribute( "xlink:href", tagNode.getAttributeByName( "href" ) );
							if( !tagNode.hasChildren() )
								writer.write( tagNode.getAttributeByName( "href" ) );
							else
								serialize( tagNode, writer );
						writer.closeNode();
					}else if( nodeName.equalsIgnoreCase( "p" ) ){
						/*
						 * FIXME MODIFICAR esta ñapa que cierra el párrafo abierto anteriormente,
						 * evitando anidar párrafos, dentro del párrafo del comentario. Aunque no
						 * chequea que pueda haber más párrafos anidados dentro del propio comentario.
						 */
						//buff.append( "</text:p><text:p text:style-name=\"Standard\">" );
						serialize( tagNode, writer );
					}else if( nodeName.equalsIgnoreCase( "img" ) ){

					}else if( nodeName.equalsIgnoreCase( "ol" ) ){

					}else if( nodeName.equalsIgnoreCase( "ul" ) ){

					}else if( nodeName.equalsIgnoreCase( "li" ) ){

					}else{
						writer.openNode( "text:span" );
							writer.addAttribute( "text:style-name", "TBold" );
							writer.write( "¡¡¡ Etiqueta " );
							writer.write( nodeName );
							writer.write( " no soportada !!!" );
						writer.closeNode();
						serialize( tagNode, writer );
						writer.openNode( "text:span" );
							writer.addAttribute( "text:style-name", "TBold" );
							writer.write( "¡¡¡ Fin " );
							writer.write( nodeName );
							writer.write( " !!!" );
						writer.closeNode();
					}
				}
			}
		}
	};

	public void serialize( TagNode node, XMLWriter writer ) throws IOException;
}