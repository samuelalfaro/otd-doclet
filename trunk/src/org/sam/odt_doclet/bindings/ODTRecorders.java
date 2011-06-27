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
import java.util.Collection;

import org.htmlcleaner.ContentNode;
import org.htmlcleaner.TagNode;
import org.sam.html.Cleaner;
import org.sam.html.HTMLFormater;
import org.sam.html.HTMLSerializer;
import org.sam.xml.Recorder;
import org.sam.xml.RecordersMapper;
import org.sam.xml.XMLConverter;
import org.sam.xml.XMLWriter;

final class ODTRecorders extends Recorders{
	
	ODTRecorders(){}
	
	static final HTMLFormater FORMATER = new Cleaner( new HTMLSerializer(){
		
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
						writer.insert( "</text:p><text:p text:style-name=\"Standard\">" );
						serialize( tagNode, writer );
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

					}else if( nodeName.equalsIgnoreCase( "ul" ) ){

					}else if( nodeName.equalsIgnoreCase( "li" ) ){

					}else{
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
		
	} );
	
	static void insertParagraph( String style, String content, XMLWriter writer ) throws IOException{
		if( content != null && content.length() > 0 ){
			writer.openNode( "text:p" );
				writer.addAttribute( "text:style-name", style );
				FORMATER.format( content, writer );
			writer.closeNode();
		}
	}
	
	static <T> void write( String title, Collection<T> collection, XMLWriter writer, RecordersMapper mapper ) throws IOException{
		if( collection != null && collection.size() > 0 ){
			writer.openNode( "text:p" );
				writer.addAttribute( "text:style-name", "Estilo" );
				writer.write( title );
			writer.closeNode();
			for( T t: collection )
				mapper.getRecorder( t.getClass() ).record( t, writer, mapper );
		}
	}
	
	static <T> void writeParagraph( String title, Collection<T> collection, XMLWriter writer, RecordersMapper mapper ) throws IOException{
		if( collection != null && collection.size() > 0 ){
			writer.openNode( "text:p" );
				writer.addAttribute( "text:style-name", "Estilo" );
				writer.write( title );
				for( T t: collection )
					mapper.getRecorder( t.getClass() ).record( t, writer, mapper );
			writer.closeNode();
		}
	}
	
	private static class LinkRecorder implements Recorder<LinkBinding>{
		
		LinkRecorder(){}
		
		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record( LinkBinding l, XMLWriter writer, RecordersMapper mapper ) throws IOException{
			writer.emptyNode( "text:line-break" );
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
			writer.emptyNode( "text:line-break" );
			writer.openNode( "text:span" );
				writer.addAttribute( "text:style-name", "AutoStyleB" );
				writer.write( t.type + " :");
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
			writer.emptyNode( "text:line-break" );
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
			writer.openNode( "text:p" );
				writer.addAttribute( "text:style-name", "Estilo" );
				writer.openNode( "text:span" );
					writer.addAttribute( "text:style-name", "AutoStyleB" );
					writer.write( t.name );
				writer.closeNode();
				if( t.documentation != null && t.documentation.length() > 0 ){
					writer.emptyNode( "text:tab" );
					FORMATER.format( t.documentation, writer );
				}
			writer.closeNode();
			writeParagraph( "Mire También:", t.links, writer, mapper );
		}
	}

	private static class FieldRecorder implements Recorder<FieldBinding>{
		
		FieldRecorder(){}
		
		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record( FieldBinding t, XMLWriter writer, RecordersMapper mapper ) throws IOException{
			
//				writer.addAttribute("visibility", t.visibility.toChar());
//				if(Modifier.isStatic(t.modifiers))
//					writer.addAttribute("isStatic", true);
//				if(Modifier.isTransient(t.modifiers))
//					writer.addAttribute("isTransient", true);
//				if(Modifier.isVolatile(t.modifiers))
//					writer.addAttribute("isVolatile", true);
//				if(Modifier.isFinal(t.modifiers))
//					writer.addAttribute("isFinal", true);
			
			writer.openNode( "text:p" );
				writer.addAttribute( "text:style-name", "Estilo" );
				writer.openNode( "text:span" );
					writer.addAttribute( "text:style-name", "AutoStyleB" );
					writer.write( t.name );
					writer.write( ": " );
					writer.write( t.type );
				writer.closeNode();
				if( t.documentation != null && t.documentation.length() > 0 ){
					writer.emptyNode( "text:tab" );
					FORMATER.format( t.documentation, writer );
				}
			writer.closeNode();
			writeParagraph( "Mire También:", t.links, writer, mapper );
		}
	}

	private static class ConstructorRecorder implements Recorder<ConstructorBinding>{
		
		ConstructorRecorder(){}

		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record( final ConstructorBinding t, XMLWriter writer, RecordersMapper mapper ) throws IOException{
			writer.openNode( "text:p" );
				writer.addAttribute( "text:style-name", "Estilo" );	
				writer.write( t.name );
			writer.closeNode();
			insertParagraph( "Estilo", t.documentation, writer );
//				writer.openNode("Constructor");
//					writer.addAttribute("name", t.name);
//					writer.addAttribute("visibility", t.visibility.toChar());
//					writeNode( "Documentation", FORMATER.format( t.documentation ), writer );
//					writeNode( "TypeParameters", t.typeParams, writer, mapper );
//					writeNode( "Parameters", t.params, writer, mapper );
//					writeNode( "Exceptions", t.exceptions, writer, mapper );
//					writeNode( "Links", t.links, writer, mapper );
//				writer.closeNode();
		}
	}

	private static class MethodRecorder implements Recorder<MethodBinding>{
		
		MethodRecorder(){}
		
		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record( MethodBinding t, XMLWriter writer, RecordersMapper mapper ) throws IOException{
			writer.openNode( "text:p" );
				writer.addAttribute( "text:style-name", "Estilo" );	
				writer.write( t.name );
			writer.closeNode();
			insertParagraph( "Estilo", t.documentation, writer );
			writeParagraph( "Tipos Parametizados:", t.typeParams, writer, mapper );
			writeParagraph( "Parámetros:", t.params, writer, mapper );
			if( t.returnType != null ){
				writer.openNode( "text:p" );
					writer.addAttribute( "text:style-name", "Estilo" );	
					writer.write( "Devuelve:" );
					mapper.getRecorder( ReturnTypeBinding.class ).record( t.returnType, writer, mapper );
				writer.closeNode();
			}
			writeParagraph( "Mire También:", t.links, writer, mapper );
//				writer.openNode("Method");
//					writer.addAttribute("name", t.name);
//					writer.addAttribute("visibility", t.visibility.toChar());
//					if(Modifier.isStatic(t.modifiers))
//						writer.addAttribute("isStatic", true);
//					if(Modifier.isAbstract(t.modifiers))
//						writer.addAttribute("isAbstract", true);
//					if(Modifier.isNative(t.modifiers))
//						writer.addAttribute("isNative", true);
//					if(Modifier.isStrict(t.modifiers))
//						writer.addAttribute("isStrictfp", true);
//					if(Modifier.isFinal(t.modifiers))
//						writer.addAttribute("isFinal", true);
//					if(Modifier.isSynchronized(t.modifiers))
//						writer.addAttribute("isSynchronized", true);
//					writeNode( "Documentation", FORMATER.format( t.documentation ), writer );
//					writeNode( "TypeParameters", t.typeParams, writer, mapper );
//					writeNode( "Parameters", t.params, writer, mapper );
//					if(t.returnType != null)
//						mapper.getRecorder( ReturnTypeBinding.class ).record(t.returnType, writer, mapper );
//					writeNode( "Exceptions", t.exceptions, writer, mapper );
//					writeNode( "Links", t.links, writer, mapper );
//				writer.closeNode();
		}
	}

	private static class InterfaceRecorder implements Recorder<ClassBinding.Interface>{
		
		InterfaceRecorder(){}
		
		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record(ClassBinding.Interface t, XMLWriter writer, RecordersMapper mapper) throws IOException{
//				writer.openNode( "Interface" );
//					writer.addAttribute( "name", t.name );
//					writeNode( "Documentation", FORMATER.format( t.documentation ), writer );
//					writeNode( "TypeParameters", t.parameters, writer, mapper );
//					writeNode( "Links", t.links, writer, mapper );
//					
//					writeNode( "EnclosingClasses", t.enclosingClasses, writer, mapper );
//					writeNode( "Interfaces", t.interfaces, writer, mapper );
//					writeNode( "Fields", t.fields, writer, mapper );
//					writeNode( "Methods", t.methods, writer, mapper );
//				writer.closeNode();
			writer.openNode( "text:p" );
				writer.addAttribute( "text:style-name", "Estilo" );	
				writer.write( "Interface: " + t.name );
			writer.closeNode();
			insertParagraph( "Estilo", t.documentation, writer );
			writeParagraph( "Mire También:", t.links, writer, mapper );
		}
	}

	private static class EnumRecorder implements Recorder<ClassBinding.Enum>{
		
		EnumRecorder(){}
		
		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record( ClassBinding.Enum t, XMLWriter writer, RecordersMapper mapper ) throws IOException{
//				writer.openNode( "Enum" );
//					writer.addAttribute( "name", t.name );
//					writeNode( "Documentation", FORMATER.format( t.documentation ), writer );
//					writeNode( "Links", t.links, writer, mapper );
//					
//					writeNode( "EnclosingClasses", t.enclosingClasses, writer, mapper );
//					writeNode( "Interfaces", t.interfaces, writer, mapper );
			
//					writeNode( "Constants", t.constants, writer, mapper );
//					writeNode( "Fields", t.fields, writer, mapper );
//					writeNode( "Methods", t.methods, writer, mapper );
//				writer.closeNode();
			writer.openNode( "text:p" );
				writer.addAttribute( "text:style-name", "Estilo" );	
				writer.write( "Enum: " + t.name );
			writer.closeNode();
			insertParagraph( "Estilo", t.documentation, writer );
			writeParagraph( "Mire También:", t.links, writer, mapper );
			write( "Elementos:", t.constants, writer, mapper );
			write( "Atributos:", t.fields, writer, mapper );
			write( "Constructores:", t.constructors, writer, mapper );
			write( "Métodos:", t.methods, writer, mapper );
		}
	}

	private static class ClassRecorder implements Recorder<ClassBinding.Clazz>{
		
		ClassRecorder(){}
		
		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record( ClassBinding.Clazz t, XMLWriter writer, RecordersMapper mapper ) throws IOException{
//				writer.openNode( "Class" );
//					writer.addAttribute( "name", t.name );
//					if(t.isAbstract)
//						writer.addAttribute( "isAbstract", t.isAbstract );
//					writeNode( "Documentation", FORMATER.format( t.documentation ), writer );
//					writeNode( "TypeParameters", t.parameters, writer, mapper );
//					writeNode( "Links", t.links, writer, mapper );
//					
//					writeNode( "Hierarchy", t.hierarchy, writer, mapper );
//					writeNode( "EnclosingClasses", t.enclosingClasses, writer, mapper );
//					writeNode( "Interfaces", t.interfaces, writer, mapper );
//					writeNode( "Fields", t.fields, writer, mapper );
//					writeNode( "Constructors", t.constructors, writer, mapper );
//					writeNode( "Methods", t.methods, writer, mapper );
//				writer.closeNode();
			writer.openNode( "text:p" );
				writer.addAttribute( "text:style-name", "Estilo" );	
				writer.write( "Clase: " + t.name );
			writer.closeNode();
			insertParagraph( "Estilo", t.documentation, writer );
			writeParagraph( "Mire También:", t.links, writer, mapper );
			write( "Atributos:", t.fields, writer, mapper );
			write( "Constructores:", t.constructors, writer, mapper );
			write( "Métodos:", t.methods, writer, mapper );
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