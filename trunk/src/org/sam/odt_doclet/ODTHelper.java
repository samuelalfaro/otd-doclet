/* 
 * ODTHelper.java
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
package org.sam.odt_doclet;

import java.io.IOException;
import java.math.BigDecimal;

import org.sam.xml.XMLWriter;

/**
 * 
 */
public class ODTHelper{
	
	public static class TextStyleProperties{
		
		private static final byte IS_SUP_MASK       = 0x20;
		private static final byte IS_SUB_MASK       = 0x10;
		private static final byte IS_MONO_MASK      = 0x08;
		private static final byte IS_BOLD_MASK      = 0x04;
		private static final byte IS_ITALIC_MASK    = 0x02;
		private static final byte IS_UNDERLINE_MASK = 0x01;
		
		private byte properties;
		
		public TextStyleProperties(){
			this.properties = 0x00;
		}
		
		public void setSup( boolean isSup ){
			if( isSup ){
				properties &= ~IS_SUB_MASK;
				properties |= IS_SUP_MASK;
			}else
				properties &= ~IS_SUP_MASK;
		}

		public boolean isSup(){
			return ( properties & IS_SUP_MASK ) != 0;
		}

		public void setSub( boolean isSub ){
			if( isSub ){
				properties &= ~IS_SUP_MASK;
				properties |= IS_SUB_MASK;
			}else
				properties &= ~IS_SUB_MASK;
		}

		public boolean isSub(){
			return ( properties & IS_SUB_MASK ) != 0;
		}

		public void setMono( boolean isMono ){
			if( isMono )
				properties |= IS_MONO_MASK;
			else
				properties &= ~IS_MONO_MASK;
		}

		public boolean isMono(){
			return ( properties & IS_MONO_MASK ) != 0;
		}

		public void setBold( boolean isBold ){
			if( isBold )
				properties |= IS_BOLD_MASK;
			else
				properties &= ~IS_BOLD_MASK;
		}

		public boolean isBold(){
			return ( properties & IS_BOLD_MASK ) != 0;
		}

		public void setItalic( boolean isItalic ){
			if( isItalic )
				properties |= IS_ITALIC_MASK;
			else
				properties &= ~IS_ITALIC_MASK;
		}

		public boolean isItalic(){
			return ( properties & IS_ITALIC_MASK ) != 0;
		}

		public void setUnderline( boolean isUnderline ){
			if( isUnderline )
				properties |= IS_UNDERLINE_MASK;
			else
				properties &= ~IS_UNDERLINE_MASK;
		}

		public boolean isUnderline(){
			return ( properties & IS_UNDERLINE_MASK ) != 0;
		}
		
		public void setProperties( byte properties ){
			this.properties = properties;
		}

		public byte getProperties(){
			return this.properties;
		}
		
		public String toString(){
			StringBuilder builder = new StringBuilder("AutoStyle");
			if( isSup() )
				builder.append( "Sp" );
			else if( isSub() )
				builder.append( "Sb" );
			if( isMono() )
				builder.append( "M" );
			if( isBold() )
				builder.append( "B" );
			if( isItalic() )
				builder.append( "I" );
			if( isUnderline() )
				builder.append( "U" );
			return builder.toString();
		}
	}
	
	private static void generateAutoStyle( XMLWriter writer, TextStyleProperties properties ) throws IOException{
		writer.openNode( "style:style" );
			writer.addAttribute( "style:name", properties.toString() );
			writer.addAttribute( "style:family", "text" );
			writer.openNode( "style:text-properties" );
				if( properties.isSup() )
					writer.addAttribute( "style:text-position", "super 58%" );
				else if( properties.isSup() )
					writer.addAttribute( "style:text-position", "sub 58%" );
				if( properties.isMono() ){
					writer.addAttribute( "style:font-name", "Nimbus Mono L" );
				}
				if( properties.isBold() ){
					writer.addAttribute( "fo:font-weight", "bold" );
					writer.addAttribute( "style:font-weight-asian", "bold" );
					writer.addAttribute( "style:font-weight-complex", "bold" );
				}else{
					writer.addAttribute( "fo:font-weight", "normal" );
					writer.addAttribute( "style:font-weight-asian", "normal" );
					writer.addAttribute( "style:font-weight-complex", "normal" );
				}
				if( properties.isItalic() ){
					writer.addAttribute( "fo:font-style", "italic" );
					writer.addAttribute( "style:font-style-asian", "italic" );
					writer.addAttribute( "style:font-style-complex", "italic" );
				}else{
					writer.addAttribute( "fo:font-style", "normal" );
					writer.addAttribute( "style:font-style-asian", "normal" );
					writer.addAttribute( "style:font-style-complex", "normal" );
				}
				if( properties.isUnderline() ){
					writer.addAttribute( "style:text-underline-style", "solid" );
					writer.addAttribute( "style:text-underline-width", "auto" );
					writer.addAttribute( "style:text-underline-color", "font-color" );
				}else{
					writer.addAttribute( "style:text-underline-style", "none" );
				}
			writer.closeNode();
		writer.closeNode();
	}
	
	private static void generateAutoStyles( XMLWriter writer ) throws IOException{
		TextStyleProperties properties = new TextStyleProperties();
		for( int i = 0; i < 3; i++ ){
			properties.setSup( i == 1 );
			properties.setSub( i == 2 );
			for( int j = 0; j < 2; j++ ){
				properties.setMono( j == 1 );
				for( int k = 0; k < 2; k++ ){
					properties.setBold( k == 1 );
					for( int l = 0; l < 2; l++ ){
						properties.setItalic( l == 1 );
						for( int m = 0; m < 2; m++ ){
							properties.setUnderline( m == 1 );
							generateAutoStyle( writer, properties );
						}
					}
				}
			}
		}
	}
	
	public static void beginDocumenContent( XMLWriter writer ) throws IOException{
		writer.insert( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
		writer.openNode( "office:document-content ");
			writer.addAttribute( "xmlns:office", "urn:oasis:names:tc:opendocument:xmlns:office:1.0" );
			writer.addAttribute( "xmlns:style", "urn:oasis:names:tc:opendocument:xmlns:style:1.0" );
			writer.addAttribute( "xmlns:text", "urn:oasis:names:tc:opendocument:xmlns:text:1.0" );
			writer.addAttribute( "xmlns:table", "urn:oasis:names:tc:opendocument:xmlns:table:1.0" );
			writer.addAttribute( "xmlns:draw", "urn:oasis:names:tc:opendocument:xmlns:drawing:1.0" );
			writer.addAttribute( "xmlns:fo", "urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0" );
			writer.addAttribute( "xmlns:xlink", "http://www.w3.org/1999/xlink" );
			writer.addAttribute( "xmlns:dc", "http://purl.org/dc/elements/1.1/" );
			writer.addAttribute( "xmlns:meta", "urn:oasis:names:tc:opendocument:xmlns:meta:1.0" );
			writer.addAttribute( "xmlns:number", "urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0" );
			writer.addAttribute( "xmlns:svg", "urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0" );
			writer.addAttribute( "xmlns:chart", "urn:oasis:names:tc:opendocument:xmlns:chart:1.0" );
			writer.addAttribute( "xmlns:dr3d", "urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0" );
			writer.addAttribute( "xmlns:math", "http://www.w3.org/1998/Math/MathML" );
			writer.addAttribute( "xmlns:form", "urn:oasis:names:tc:opendocument:xmlns:form:1.0" );
			writer.addAttribute( "xmlns:script", "urn:oasis:names:tc:opendocument:xmlns:script:1.0" );
			writer.addAttribute( "xmlns:ooo", "http://openoffice.org/2004/office" );
			writer.addAttribute( "xmlns:ooow", "http://openoffice.org/2004/writer" );
			writer.addAttribute( "xmlns:oooc", "http://openoffice.org/2004/calc" );
			writer.addAttribute( "xmlns:dom", "http://www.w3.org/2001/xml-events" );
			writer.addAttribute( "xmlns:xforms", "http://www.w3.org/2002/xforms" );
			writer.addAttribute( "xmlns:xsd", "http://www.w3.org/2001/XMLSchema" );
			writer.addAttribute( "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance" );
			writer.addAttribute( "xmlns:rpt", "http://openoffice.org/2005/report" );
			writer.addAttribute( "xmlns:of", "urn:oasis:names:tc:opendocument:xmlns:of:1.2" );
			writer.addAttribute( "xmlns:xhtml", "http://www.w3.org/1999/xhtml" );
			writer.addAttribute( "xmlns:grddl", "http://www.w3.org/2003/g/data-view#" );
			writer.addAttribute( "xmlns:officeooo", "http://openoffice.org/2009/office" );
			writer.addAttribute( "xmlns:tableooo", "http://openoffice.org/2009/table" );
			writer.addAttribute( "xmlns:field", "urn:openoffice:names:experimental:ooo-ms-interop:xmlns:field:1.0" );
			writer.addAttribute( "xmlns:formx", "urn:openoffice:names:experimental:ooxml-odf-interop:xmlns:form:1.0" );
			writer.addAttribute( "xmlns:css3t", "http://www.w3.org/TR/css3-text/" );
			writer.addAttribute( "office:version", "1.2" );
			writer.addAttribute( "grddl:transformation", "http://docs.oasis-open.org/office/1.2/xslt/odf2rdf.xsl" );
		
			writer.openNode( "office:font-face-decls");
				writer.openNode( "style:font-face" );
					writer.addAttribute( "style:name", "Liberation Serif" );
					writer.addAttribute( "svg:font-family", "\'Liberation Serif\'" );
					writer.addAttribute( "style:font-family-generic", "roman" );
					writer.addAttribute( "style:font-pitch", "variable" );
				writer.closeNode();
				writer.openNode( "style:font-face" );
					writer.addAttribute( "style:name", "Nimbus Mono L" );
					writer.addAttribute( "svg:font-family", "\'Nimbus Mono L\'" );
					writer.addAttribute( "style:font-family-generic", "modern" );
					writer.addAttribute( "style:font-pitch", "fixed" );
				writer.closeNode();
			writer.closeNode();
			
			writer.openNode( "office:automatic-styles" );
				writer.openNode( "style:style" );
					writer.addAttribute( "style:name", "P0" );
					writer.addAttribute( "style:family", "paragraph" );
					writer.addAttribute( "style:parent-style-name", "Standard" );
				writer.closeNode();
				writer.openNode( "style:style" );
					writer.addAttribute( "style:name", "P1" );
					writer.addAttribute( "style:family", "paragraph" );
					writer.addAttribute( "style:parent-style-name", "Standard" );
					writer.openNode( "style:paragraph-properties" );
						writer.addAttribute( "fo:break-before", "page" );
					writer.closeNode();
				writer.closeNode();
				
				generateAutoStyles( writer );
				
			writer.closeNode();
			
			writer.openNode( "office:body" );
				writer.openNode( "office:text" );
					writer.addAttribute( "text:use-soft-page-breaks", true );
	}
	
	public static void endDocumenContent( XMLWriter writer ) throws IOException{
				writer.closeNode();
			writer.closeNode();
		writer.closeNode();
	}
	
	public static void insertImage( XMLWriter writer, String style, String path, String name, BigDecimal width,
			BigDecimal height, String units ) throws IOException{
		
		writer.openNode( "draw:frame" );
			writer.addAttribute( "draw:style-name", ( style != null && style.length() > 0 ) ? style: "Graphics" );
			writer.addAttribute( "draw:name", name );
			writer.addAttribute( "text:anchor-type", "as-char" );
			if( units == null || units.length() == 0 )
				units = "pt";
			writer.addAttribute( "svg:width", width+units );
			writer.addAttribute( "svg:height", height+units );
			writer.addAttribute( "draw:z-index", "0" );
			writer.openNode( "draw:image" );
				writer.addAttribute( "xlink:href", path );
				writer.addAttribute( "xlink:type", "simple" );
				writer.addAttribute( "xlink:show", "embed" );
				writer.addAttribute( "xlink:actuate", "onLoad" );
			writer.closeNode();
		writer.closeNode();
	}
	
	public static void insertImage( XMLWriter writer, String style, Graphic g ) throws IOException{
		insertImage( writer, style, g.getPath(), g.getName(), g.getWidth(), g.getHeight(), "mm" );
	}
}
