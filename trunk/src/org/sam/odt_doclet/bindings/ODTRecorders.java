/* 
 * ODTRecorders.java
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
package org.sam.odt_doclet.bindings;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.htmlcleaner.ContentNode;
import org.htmlcleaner.TagNode;
import org.sam.html.Cleaner;
import org.sam.html.HTMLFormater;
import org.sam.html.HTMLSerializer;
import org.sam.odt_doclet.ODTHelper;
import org.sam.odt_doclet.ODTHelper.TextStyleProperties;
import org.sam.xml.Recorder;
import org.sam.xml.RecordersMapper;
import org.sam.xml.StringDigester;
import org.sam.xml.XMLConverter;
import org.sam.xml.XMLWriter;

final class ODTRecorders extends Recorders{
	
	ODTRecorders(){}
	
	static boolean isTextSpan( String nodeName ){
		return 
			nodeName.equalsIgnoreCase( "code" ) ||
			nodeName.equalsIgnoreCase( "sup" ) ||
			nodeName.equalsIgnoreCase( "sub" ) ||
			nodeName.equalsIgnoreCase( "tt" ) ||
			nodeName.equalsIgnoreCase( "b" ) ||
			nodeName.equalsIgnoreCase( "strong" ) ||
			nodeName.equalsIgnoreCase( "i" ) ||
			nodeName.equalsIgnoreCase( "em" ) ||
			nodeName.equalsIgnoreCase( "u" );
	}
	
	static final HTMLFormater FORMATER = new Cleaner( new HTMLSerializer(){
		
		StringDigester preDigester = new StringDigester(){

			private final char[] charArray = new char[8192];

			public void digestString( String content, Appendable out ) throws IOException{

				int remainder = content.length();
				int srcBegin = 0;

				while( remainder > 0 ){
					int copiedChars = Math.min( remainder, charArray.length );
					content.getChars( srcBegin, srcBegin + copiedChars, charArray, 0 );
					for( int i = 0; i < copiedChars; i++ )
						switch( charArray[i] ){
						case '&':
							out.append( "&amp;" );
							break;
						case '<':
							out.append( "&lt;" );
							break;
						case '>':
							out.append( "&gt;" );
							break;
						case '\"':
							out.append( "&quot;" );
							break;
						case '\'':
							out.append( "&apos;" );
							break;
						case ' ':
							//*
							out.append( "<text:s/>" );
							/*/
							//TODO hacer
							if( i + 1 < copiedChars && charArray[i + 1] != ' ' )
								out.append( ' ' );
							else{
								out.append( "<text:s " );
								int n = 1;
								i++;
								while( i < copiedChars && charArray[i] != ' ' ){
									n++;
									i++;
								}
								out.append( "text:c=\"" + n + "\"/>" );
							}
							//*/
							break;
						case '\t':
							out.append( "<text:tab/>" );
							break;
						case '\n':
							out.append( "<text:line-break/>" );
							break;

						default:
							out.append( charArray[i] );
						}
					remainder -= copiedChars;
				}
			}
		};

		
		TextStyleProperties properties = new TextStyleProperties();
		
		public void serialize( TagNode node, XMLWriter writer ) throws IOException{
			
			for( Object item: node.getChildren() ){
				if( item instanceof ContentNode ){
					boolean parrafoAuto = !writer.hasParent( "text:p" );
					if( parrafoAuto )
						writer.openNode( "text:p" );
						writer.write( item.toString() );
					if( parrafoAuto )
						writer.closeUntilParent( "text:p" );
				}else if( item instanceof TagNode ){
					TagNode tagNode = (TagNode)item;
					String nodeName = tagNode.getName();
					
					if( nodeName.equalsIgnoreCase( "br" ) ){
						writer.emptyNode( "text:line-break" );
					}else if( isTextSpan( nodeName ) ){
						byte oldProperties = properties.getProperties();
						if( nodeName.equalsIgnoreCase( "code" ) ){
							properties.setMono( true );
							properties.setItalic( true );
						}else if( nodeName.equalsIgnoreCase( "sup" ) )
							properties.setSup( true );
						else if( nodeName.equalsIgnoreCase( "sub" ) )
							properties.setSub( true );
						else if( nodeName.equalsIgnoreCase( "tt" ) )
							properties.setMono( true );
						else if( nodeName.equalsIgnoreCase( "b" ) || nodeName.equalsIgnoreCase( "strong" ) )
							properties.setBold( true );
						else if( nodeName.equalsIgnoreCase( "i" ) || nodeName.equalsIgnoreCase( "em" ) )
							properties.setItalic( true );
						else if( nodeName.equalsIgnoreCase( "u" ) )
							properties.setUnderline( true );
						
						boolean parrafoAuto = !writer.hasParent( "text:p" );
						if( parrafoAuto )
							writer.openNode( "text:p" );
						if( writer.hasParent( "text:span" ) ){
							if( writer.getCurrentNodeName().equalsIgnoreCase( "text:span" ) && !writer.currentNodeHasContent() )
								writer.discardAttributes();
							else{
								writer.closeUntilParent( "text:span" );
							}
						}
						if( !writer.getCurrentNodeName().equalsIgnoreCase( "text:span" ) ){
							writer.openNode( "text:span" );	
						}
							writer.addAttribute( "text:style-name", properties.toString() );
							serialize( tagNode, writer );
							properties.setProperties( oldProperties );
						writer.closeUntilParent( "text:span" );
						if( parrafoAuto )
							writer.closeUntilParent( "text:p" );
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
						writer.closeUntilParent("text:p");
						writer.openNode( "text:p" );
							serialize( tagNode, writer );
						writer.closeUntilParent("text:p");
					}else if( nodeName.equalsIgnoreCase( "img" ) ){
						String src = tagNode.getAttributeByName( "src" );
						if( src != null && src.length() > 0 ){
							writer.openNode( "draw:frame" );
								writer.addAttribute( "draw:style-name", "G0" );
								String alt = tagNode.getAttributeByName( "alt" );
								if( alt != null && alt.length() > 0 )
									writer.addAttribute( "draw:name", alt );
								writer.addAttribute( "text:anchor-type", "as-char" );
								// FIXME evaluar px, % o null, obtener dimensiones cargando src.
								// pixels en puntos --> 72 dpi
								String width = tagNode.getAttributeByName( "width" );
								writer.addAttribute( "svg:width", width+"pt" );
								String height = tagNode.getAttributeByName( "height" );
								writer.addAttribute( "svg:height", height+"pt" );
								
								writer.addAttribute( "draw:z-index", "0" );
								writer.openNode( "draw:image" );
									writer.addAttribute( "xlink:href", src );
									writer.addAttribute( "xlink:type", "simple" );
									writer.addAttribute( "xlink:show", "embed" );
									writer.addAttribute( "xlink:actuate", "onLoad" );
								writer.closeNode();
							writer.closeNode();
						}
					}else if( nodeName.equalsIgnoreCase( "ol" ) ){
						writer.closeUntilParent("text:p");
						writer.openNode( "text:list" );
							serialize( tagNode, writer );
						writer.closeNode();
					}else if( nodeName.equalsIgnoreCase( "ul" ) ){
						writer.closeUntilParent("text:p");
						writer.openNode( "text:list" );
							serialize( tagNode, writer );
						writer.closeNode();
					}else if( nodeName.equalsIgnoreCase( "li" ) ){
						writer.openNode( "text:list-item" );
							serialize( tagNode, writer );
						writer.closeNode();
					}else if( nodeName.equalsIgnoreCase( "pre" ) ){
						writer.openNode( "text:span" );
							writer.addAttribute( "text:style-name", "AutoStyleM" );
							writer.write( tagNode.getText().toString(), preDigester );
						writer.closeNode();
					}else{
						if( tagNode.getChildren().size() == 0 )
							writer.write( "<" + nodeName +">" );
						else{
							writer.openNode( "text:span" );
								writer.addAttribute( "text:style-name", "AutoStyleB" );
								writer.write( "¡¡¡ Etiqueta " );
								writer.write( nodeName );
								writer.write( " no soportada !!!" );
							writer.closeNode();
							serialize( tagNode, writer );
							writer.openNode( "text:span" );
								writer.addAttribute( "text:style-name", "AutoStyleB" );
								writer.write( "¡¡¡ Fin " );
								writer.write( nodeName );
								writer.write( " !!!" );
							writer.closeNode();
						}
					}
				}
			}
		}
		
	} );
	
	static void writeConstantBullet( String width, String height, String units, XMLWriter writer ) throws IOException{
		ODTHelper.insertImage( writer, "SBullet", "Pictures/EnumConstant.png", null, width, height, units );
	}
	
	static void writeBullet( FieldBinding f, String width, String height, String units, XMLWriter writer ) throws IOException{
		StringBuilder bulletName = new StringBuilder("Pictures/");
		bulletName.append( f.visibility.name() );
		if( Modifier.isStatic( f.modifiers ) )
			bulletName.append( "Static" );
		bulletName.append( "Field.png" );
		
		if( Modifier.isTransient( f.modifiers ) || Modifier.isVolatile( f.modifiers ) || Modifier.isFinal( f.modifiers ) ){
			writer.openNode( "draw:g" );
				writer.addAttribute( "text:anchor-type", "as-char" );
				writer.addAttribute( "draw:style-name", "SBullet" );
				ODTHelper.insertUnstyledImage( writer, bulletName.toString(), "0", "0", width, height, units );
				if( Modifier.isTransient( f.modifiers ) )
					ODTHelper.insertUnstyledImage( writer, "Pictures/Transient.png", "0", "0", width, height, units );
				if( Modifier.isVolatile( f.modifiers ) )
					ODTHelper.insertUnstyledImage( writer, "Pictures/Volatile.png", "0", "0", width, height, units );
				if( Modifier.isFinal( f.modifiers ) )
					ODTHelper.insertUnstyledImage( writer, "Pictures/Final.png", "0", "0", width, height, units );
			writer.closeNode();
		}else
			ODTHelper.insertImage( writer, "SBullet", bulletName.toString(), null, width, height, units );
	}
	
	static void writeBullet( ConstructorBinding c, String width, String height, String units, XMLWriter writer ) throws IOException{
		StringBuilder bulletName = new StringBuilder("Pictures/");
		bulletName.append( c.visibility.name() );
		bulletName.append( "Constructor.png" );
		ODTHelper.insertImage( writer, "SBullet", bulletName.toString(), null, width, height, units );
	}
	
	static void writeBullet( MethodBinding m, String width, String height, String units, XMLWriter writer ) throws IOException{
		StringBuilder bulletName = new StringBuilder("Pictures/");
		bulletName.append( m.visibility.name() );
		if( Modifier.isStatic( m.modifiers ) )
			bulletName.append( "Static" );
		else if( Modifier.isAbstract( m.modifiers ) )
			bulletName.append( "Abstract" );
		bulletName.append( "Method.png" );
		
		if( Modifier.isNative( m.modifiers ) || Modifier.isStrict( m.modifiers ) 
				|| Modifier.isFinal( m.modifiers ) || Modifier.isSynchronized( m.modifiers )){
			writer.openNode( "draw:g" );
				writer.addAttribute( "text:anchor-type", "as-char" );
				writer.addAttribute( "draw:style-name", "SBullet" );
				ODTHelper.insertUnstyledImage( writer, bulletName.toString(), "0", "0", width, height, units );
				if( Modifier.isNative( m.modifiers ) )
					ODTHelper.insertUnstyledImage( writer, "Pictures/Native.png", "0", "0", width, height, units );
				if( Modifier.isStrict( m.modifiers ) )
					ODTHelper.insertUnstyledImage( writer, "Pictures/Strictfp.png", "0", "0", width, height, units );
				if( Modifier.isFinal( m.modifiers ) )
					ODTHelper.insertUnstyledImage( writer, "Pictures/Final.png", "0", "0", width, height, units );
				if( Modifier.isSynchronized( m.modifiers ) )
					ODTHelper.insertUnstyledImage( writer, "Pictures/Synchronized.png", "0", "0", width, height, units );
			writer.closeNode();
		}else
			ODTHelper.insertImage( writer, "SBullet", bulletName.toString(), null, width, height, units );
	}
	
	static void insertParagraph( String style, String content, XMLWriter writer ) throws IOException{
		if( content != null && content.length() > 0 ){
			writer.openNode( "text:p" );
				writer.addAttribute( "text:style-name", style );
				FORMATER.format( content, writer );
			writer.closeUntilParent("text:p");
		}
	}
	
	static void writeTitle( String style, String content, XMLWriter writer ) throws IOException{
		if( content != null && content.length() > 0 ){
			writer.openNode( "text:p" );
				writer.addAttribute( "text:style-name", style );
				writer.write( content );
			writer.closeNode();
		}
	}
	
	static void writeTitle( String style, int level, String content, XMLWriter writer ) throws IOException{
		if( content != null && content.length() > 0 ){
			writer.openNode( "text:h" );
				writer.addAttribute( "text:style-name", style );
				writer.addAttribute( "text:outline-level", level );
				writer.write( content );
			writer.closeNode();
		}
	}
	
	static <T> Collection<T> filterCollection( Filter<? super T> filter, Collection<T> collection ){
		if( collection == null || collection.size() == 0 )
			return collection;
		Queue<T> out = new LinkedList<T>();
		for( T t: collection )
			if( filter.validate( t ))
				out.offer( t );
		return out;
	}
	
	static <T> void writeCollection( Collection<T> collection, XMLWriter writer, RecordersMapper mapper ) throws IOException{
		for( T t: collection )
			mapper.getRecorder( t.getClass() ).record( t, writer, mapper );
	}
	
	static <T> void writeCollection( String style, Collection<T> collection, XMLWriter writer, RecordersMapper mapper ) throws IOException{
		writer.openNode( "text:p" );
			writer.addAttribute( "text:style-name", style );
			Iterator<T> it = collection.iterator();
			do{
				T t = it.next();
				mapper.getRecorder( t.getClass() ).record( t, writer, mapper );
				if( it.hasNext() )
					writer.emptyNode( "text:line-break" );
				else
					break;
			}while( true );
		writer.closeUntilParent("text:p");
	}
	
	static <T> void writeTitleCollection( String styleTitle, String title, Collection<T> collection, XMLWriter writer, RecordersMapper mapper ) throws IOException{
		if( collection != null && collection.size() > 0 ){
			writeTitle( styleTitle, title, writer );
			writeCollection( collection, writer, mapper );
		}
	}
	
	static <T> void writeTitleCollection( String styleTitle, String title, String styleCollection, Collection<T> collection, XMLWriter writer, RecordersMapper mapper ) throws IOException{
		if( collection != null && collection.size() > 0 ){
			writeTitle( styleTitle, title, writer );
			writeCollection( styleCollection, collection, writer, mapper );
		}
	}
	
	static <T> void writeTitleCollection( String styleTitle, int level, String title, Collection<T> collection, XMLWriter writer, RecordersMapper mapper ) throws IOException{
		if( collection != null && collection.size() > 0 ){
			writeTitle( styleTitle, level, title, writer );
			writeCollection( collection, writer, mapper );
		}
	}
	
	static <T> void writeTitleCollection( String styleTitle, int level, String title, String styleCollection, Collection<T> collection, XMLWriter writer, RecordersMapper mapper ) throws IOException{
		if( collection != null && collection.size() > 0 ){
			writeTitle( styleTitle, level, title, writer );
			writeCollection( styleCollection, collection, writer, mapper );
		}
	}
	
	private static class LinkRecorder implements Recorder<LinkBinding>{
		
		LinkRecorder(){}
		
		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record( LinkBinding l, XMLWriter writer, RecordersMapper mapper ) throws IOException{

			writer.openNode( "text:a" );
				writer.addAttribute( "xlink:type", "simple" );
				writer.addAttribute( "xlink:href", l.link );
				writer.write( l.link );
			writer.closeNode();
		}
	}
	
	private static class DocumentedTypeRecorder implements Recorder<DocumentedType>{
		
		DocumentedTypeRecorder(){}
		
		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record( DocumentedType t, XMLWriter writer, RecordersMapper mapper ) throws IOException{
			writer.openNode( "text:span" );
				writer.addAttribute( "text:style-name", "AutoStyleB" );
				writer.write( t.type + ": ");
			writer.closeNode();
			if( t.documentation != null && t.documentation.length() > 0 ){
				writer.emptyNode( "text:tab" );
				FORMATER.format( t.documentation, writer );
			}
		}
	}

	private static class ParameterRecorder implements Recorder<ParameterBinding>{
		
		ParameterRecorder(){}
		
		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record( ParameterBinding t, XMLWriter writer, RecordersMapper mapper ) throws IOException{
			writer.openNode( "text:span" );
				writer.addAttribute( "text:style-name", "AutoStyleB" );
				if( t.name != null ){
					writer.write( t.name );
					writer.write( ": " );
				}
				writer.write( t.type );
			writer.closeNode();
			if( t.documentation != null && t.documentation.length() > 0 ){
				writer.emptyNode( "text:tab" );
				FORMATER.format( t.documentation, writer );
			}
		}
	}
	
	private static class ConstantRecorder implements Recorder<ConstantBinding>{
		
		ConstantRecorder(){}
		
		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record( ConstantBinding t, XMLWriter writer, RecordersMapper mapper ) throws IOException{
			writer.openNode( "text:h" );
				writer.addAttribute( "text:style-name", "HDoclet4" );
				writer.addAttribute( "text:outline-level", 6 );
				writeConstantBullet( "14", "14", "pt", writer );
				writer.openNode( "text:span" );
					writer.addAttribute( "text:style-name", "AutoStyleB" );
					writer.write( t.name );
				writer.closeNode();
			writer.closeNode();
			if( t.documentation != null && t.documentation.length() > 0 ){
				writer.openNode( "text:p" );
					writer.addAttribute( "text:style-name", "Estilo1" );
					FORMATER.format( t.documentation, writer );
				writer.closeUntilParent("text:p");
			}
			writeTitleCollection( "Enfatizado", "Mire También:", "Estilo2", t.links, writer, mapper );
		}
	}

	private static class FieldRecorder implements Recorder<FieldBinding>{
		
		FieldRecorder(){}
		
		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record( FieldBinding t, XMLWriter writer, RecordersMapper mapper ) throws IOException{
			writer.openNode( "text:h" );
			writer.addAttribute( "text:style-name", "HDoclet4" );
				writer.addAttribute( "text:outline-level", 6 );
				writeBullet( t, "14", "14", "pt", writer );
				writer.openNode( "text:span" );
					writer.addAttribute( "text:style-name", "AutoStyleB" );
					writer.write( t.name );
					writer.write( ": " );
					writer.write( t.type );
				writer.closeNode();
			writer.closeNode();
			if( t.documentation != null && t.documentation.length() > 0 ){
				writer.openNode( "text:p" );
					writer.addAttribute( "text:style-name", "Estilo1" );
					FORMATER.format( t.documentation, writer );
				writer.closeUntilParent("text:p");
			}
			writeTitleCollection( "Enfatizado", "Mire También:", "Estilo2", t.links, writer, mapper );
		}
	}

	private static class ConstructorRecorder implements Recorder<ConstructorBinding>{
		
		ConstructorRecorder(){}

		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record( ConstructorBinding t, XMLWriter writer, RecordersMapper mapper ) throws IOException{
			writer.openNode( "text:h" );
				writer.addAttribute( "text:style-name", "HDoclet4" );
				writer.addAttribute( "text:outline-level", 6 );
				writeBullet( t, "14", "14", "pt", writer );
				writer.write( t.name );
			writer.closeNode();
			insertParagraph( "Estilo1", t.documentation, writer );
			writeTitleCollection( "Enfatizado", "Tipos Parametizados:", "Estilo2", t.typeParams, writer, mapper );
			writeTitleCollection( "Enfatizado", "Parámetros:", "Estilo2", t.params, writer, mapper );
			writeTitleCollection( "Enfatizado", "Lanza:", "Estilo2", t.exceptions, writer, mapper );
			writeTitleCollection( "Enfatizado", "Mire También:", "Estilo2", t.links, writer, mapper );
		}
	}

	private static class MethodRecorder implements Recorder<MethodBinding>{
		
		MethodRecorder(){}
		
		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record( MethodBinding t, XMLWriter writer, RecordersMapper mapper ) throws IOException{
			writer.openNode( "text:h" );
				writer.addAttribute( "text:style-name", "HDoclet4" );
				writer.addAttribute( "text:outline-level", 6 );
				writeBullet( t, "14", "14", "pt", writer );
				writer.write( t.name );
			writer.closeNode();
			insertParagraph( "Estilo1", t.documentation, writer );
			
			writeTitleCollection( "Enfatizado", "Tipos Parametizados:", "Estilo2", t.typeParams, writer, mapper );
			writeTitleCollection( "Enfatizado", "Parámetros:", "Estilo2", t.params, writer, mapper );
			if( t.returnType != null ){
				writer.openNode( "text:p" );
					writer.addAttribute( "text:style-name", "Enfatizado" );
					writer.write( "Devuelve:" );
				writer.closeNode();
				writer.openNode( "text:p" );
					writer.addAttribute( "text:style-name", "Estilo2" );
					mapper.getRecorder( ReturnTypeBinding.class ).record( t.returnType, writer, mapper );
				writer.closeUntilParent( "text:p" );
			}
			writeTitleCollection( "Enfatizado", "Lanza:", "Estilo2", t.exceptions, writer, mapper );
			writeTitleCollection( "Enfatizado", "Mire También:", "Estilo2", t.links, writer, mapper );
		}
	}

	
	private static class InterfaceRecorder implements Recorder<ClassBinding.Interface>{
		
		InterfaceRecorder(){}
		
		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record( ClassBinding.Interface t, XMLWriter writer, RecordersMapper mapper ) throws IOException{
			writer.openNode( "text:h" );
				writer.addAttribute( "text:style-name", "HDoclet2I" );
				writer.addAttribute( "text:outline-level", 4 );
				writer.write( "Interface: " + t.name );
			writer.closeNode();
			if( t.graphic != null ){
				writer.openNode( "text:p" );
					writer.addAttribute( "text:style-name", "Diagrama" );	
					ODTHelper.insertImage( writer, "Graphics", t.graphic );
				writer.closeNode();
			}
			writeTitleCollection( "Enfatizado", "Parámetros:", "Estilo2", t.parameters, writer, mapper );
			insertParagraph( "Estilo1", t.documentation, writer );
			writeTitleCollection( "Enfatizado", "Mire También:", "Estilo2", t.links, writer, mapper );
			writeTitleCollection( "HDoclet3", 5, "Atributos:",  t.fields, writer, mapper );
			writeTitleCollection( "HDoclet3", 5, "Métodos:",  t.methods, writer, mapper );
		}
	}

	private static class EnumRecorder implements Recorder<ClassBinding.Enum>{

		EnumRecorder(){
		}

		/*
		 * (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record( ClassBinding.Enum t, XMLWriter writer, RecordersMapper mapper ) throws IOException{
			writer.openNode( "text:h" );
				writer.addAttribute( "text:style-name", "HDoclet2E" );
				writer.addAttribute( "text:outline-level", 4 );
				writer.write( "Enumeración: " + t.name );
			writer.closeNode();
			if( t.graphic != null ){
				writer.openNode( "text:p" );
					writer.addAttribute( "text:style-name", "Diagrama" );	
					ODTHelper.insertImage( writer, "Graphics", t.graphic );
				writer.closeNode();
			}
			insertParagraph( "Estilo1", t.documentation, writer );
			writeTitleCollection( "Enfatizado", "Mire También:", "Estilo2", t.links, writer, mapper );
			writeTitleCollection( "HDoclet3", 5, "Constantes:",  t.constants, writer, mapper );
			writeTitleCollection( "HDoclet3", 5, "Atributos:",
					filterCollection( VisibilityFilters.FieldFilter, t.fields ), writer, mapper );
			writeTitleCollection( "HDoclet3", 5, "Constructores:",
					filterCollection( VisibilityFilters.CommandFilter, t.constructors ), writer, mapper );
			writeTitleCollection( "HDoclet3", 5, "Métodos:",
					filterCollection( VisibilityFilters.CommandFilter, t.methods ), writer, mapper );
		}
	}

	private static class ClassRecorder implements Recorder<ClassBinding.Clazz>{

		ClassRecorder(){
		}

		/*
		 * (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record( ClassBinding.Clazz t, XMLWriter writer, RecordersMapper mapper ) throws IOException{
			writer.openNode( "text:h" );
				writer.addAttribute( "text:style-name", "HDoclet2C" );
				writer.addAttribute( "text:outline-level", 4 );
				writer.write( "Clase: " + t.name );
			writer.closeNode();
			if( t.graphic != null ){
				writer.openNode( "text:p" );
					writer.addAttribute( "text:style-name", "Diagrama" );	
					ODTHelper.insertImage( writer, "Graphics", t.graphic );
				writer.closeNode();
			}
			writeTitleCollection( "Enfatizado", "Parámetros:", "Estilo2", t.parameters, writer, mapper );
			insertParagraph( "Estilo1", t.documentation, writer );
			writeTitleCollection( "Enfatizado", "Mire También:", "Estilo2", t.links, writer, mapper );
			writeTitleCollection( "HDoclet3", 5, "Atributos:",
					filterCollection( VisibilityFilters.FieldFilter, t.fields ), writer, mapper );
			writeTitleCollection( "HDoclet3", 5, "Constructores:",
					filterCollection( VisibilityFilters.CommandFilter, t.constructors ), writer, mapper );
			writeTitleCollection( "HDoclet3", 5, "Métodos:",
					filterCollection( VisibilityFilters.CommandFilter, t.methods ), writer, mapper );
		}
	}
	
	void register( XMLConverter converter ){

		converter.register( new LinkRecorder() );
		converter.register( new DocumentedTypeRecorder() );
		converter.register( new ParameterRecorder() );
		converter.register( new ConstantRecorder() );
		converter.register( new FieldRecorder() );
		converter.register( new ConstructorRecorder() );
		converter.register( new MethodRecorder() );
		converter.register( new InterfaceRecorder() );
		converter.register( new EnumRecorder() );
		converter.register( new ClassRecorder() );

	}
	
}