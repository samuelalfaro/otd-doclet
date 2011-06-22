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

import org.sam.html.Cleaner;
import org.sam.html.HTMLSerializer;
import org.sam.xml.Recorder;
import org.sam.xml.RecordersMapper;
import org.sam.xml.XMLConverter;
import org.sam.xml.XMLWriter;

final class ODTRecorders extends Recorders{
	
	ODTRecorders(){}

	static final Cleaner FORMATER = new Cleaner( HTMLSerializer.TO_ODT );
	
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
				writer.addAttribute( "text:style-name", "TBold" );
				writer.write( t.type + ":");
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
				writer.addAttribute( "text:style-name", "TBold" );
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
					writer.addAttribute( "text:style-name", "TBold" );
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
					writer.addAttribute( "text:style-name", "TBold" );
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