/* 
 * Recorders.java
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

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.sam.xml.Recorder;
import org.sam.xml.RecordersMapper;
import org.sam.xml.XMLConverter;
import org.sam.xml.XMLWriter;

/**
 * 
 */
public abstract class Recorders {
	
	public enum Mode{
		SVG, XHTML, ODT, XML
	}
	
	Recorders(){}
	
	interface HTMLSerializer{
		public void serialize( TagNode node, StringBuffer buff );
	}
	
	private static final class SimpleSerializer implements HTMLSerializer {
		
		SimpleSerializer(){}

		public void serialize( TagNode node, StringBuffer buff ){
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
						serialize( tagNode, buff );
						buff.append( "</" );
						buff.append( nodeName );
						buff.append( '>' );
					}
				}
			}
		}
	}
	
	interface HTMLFormater{
		public String format( String html );
	}
	
	private static final class Cleaner implements HTMLFormater {
		
		private final HtmlCleaner cleaner;
		private final HTMLSerializer serializer;
		
		Cleaner( HTMLSerializer serializer ){
			CleanerProperties  props = new CleanerProperties();
			// set some properties to non-default values
			props.setTranslateSpecialEntities(true);
			props.setTransResCharsToNCR(true);
			props.setOmitComments(true);
			
			this.cleaner = new HtmlCleaner(props);
			this.serializer = serializer;
		}

		private String toString( TagNode node ){
			StringBuffer buff = new StringBuffer();
			for( Object item: node.getChildren() ){
				if( item instanceof TagNode ){
					if( ((TagNode)item).getName().equals( "body" ) )
						serializer.serialize( (TagNode)item, buff );
				}
			}
			return buff.toString();
		}
		
		public String format(String s){
			if(s == null || s.length() == 0)
				return "";
			return toString( cleaner.clean(s) );
		}
		
	}
	
	private static final class XMLRecorders extends Recorders{
		
		XMLRecorders(){}
	
		static final HTMLFormater FORMATER = new Cleaner( new SimpleSerializer() );
		
		/**
		 * Method writeNode.
		 * @param nodeName String
		 * @param content String
		 * @param writer XMLWriter
		 */
		static void writeNode( String nodeName, String content, XMLWriter writer ) {
			if( content != null && content.length() > 0 ){
				writer.openNode(nodeName);
					writer.writeCDATA(content);
				writer.closeNode();
			}
		}
		
		/**
		 * Method writeNode.
		 * @param nodeName String
		 * @param collection Collection<T>
		 * @param writer XMLWriter
		 * @param mapper RecordersMapper
		 */
		static <T> void writeNode( String nodeName, Collection<T> collection, XMLWriter writer, RecordersMapper mapper ) {
			if( collection != null && collection.size() > 0 ){
				writer.openNode( nodeName );
				for( T t: collection )
					mapper.getRecorder(t.getClass()).record(t, writer, mapper);
				writer.closeNode();
			}
		}
		
		private static class SimpleInterfaceRecorder implements Recorder<SimpleClassBinding.Interface>{
			
			SimpleInterfaceRecorder(){}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record(SimpleClassBinding.Interface t, XMLWriter writer, RecordersMapper mapper){
				writeNode( "Interface", t.name, writer );
			}
		}
	
		private static class SimpleEnumRecorder implements Recorder<SimpleClassBinding.Enum>{
			
			SimpleEnumRecorder(){}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record(SimpleClassBinding.Enum t, XMLWriter writer, RecordersMapper mapper){
				writeNode( "Enum", t.name, writer );
			}
		}
	
		private static class SimpleClassRecorder implements Recorder<SimpleClassBinding.Clazz>{
			
			SimpleClassRecorder(){}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record(SimpleClassBinding.Clazz t, XMLWriter writer, RecordersMapper mapper){
				writer.openNode("Class");
					if(t.isAbstract)
						writer.addAttribute("isAbstract", t.isAbstract);
					writer.writeCDATA(t.name);
				writer.closeNode();
			}
		}
	
		private static class DocumentedTypeRecorder implements Recorder<DocumentedType>{
			
			DocumentedTypeRecorder(){}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record(DocumentedType t, XMLWriter writer, RecordersMapper mapper){
				String nodeName = null;
				if( t instanceof TypeParamBinding)
					nodeName = "Parameter";
				else if( t instanceof ReturnTypeBinding)
					nodeName = "ReturnType";
				else if( t instanceof ExceptionBinding)
					nodeName = "Exception";
				assert(nodeName != null): "!!!Tipo: "+ t.getClass().toString() + " desconocido";
				
				writer.openNode(nodeName);
					writeNode( "Type", t.type, writer );
					writeNode( "Documentation", FORMATER.format( t.documentation ), writer );
				writer.closeNode();
			}
		}
	
		private static class ParameterRecorder implements Recorder<ParameterBinding>{
			
			ParameterRecorder(){}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record(ParameterBinding t, XMLWriter writer, RecordersMapper mapper){
				writer.openNode("Parameter");
					if(t.name!=null)
						writer.addAttribute("name", t.name);
					writeNode( "Type", t.type, writer );
					writeNode( "Documentation", FORMATER.format( t.documentation ), writer );
				writer.closeNode();
			}
		}
		
		private static class LinkRecorder implements Recorder<LinkBinding>{
			
			LinkRecorder(){}
	
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record(LinkBinding t, XMLWriter writer, RecordersMapper mapper){
				writeNode( "Link", t.link,  writer );
			}
		}
	
		private static class ConstantRecorder implements Recorder<ConstantBinding>{
			
			ConstantRecorder(){}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record(ConstantBinding t, XMLWriter writer, RecordersMapper mapper){
				writer.openNode("Constant");
					writer.addAttribute("name", t.name);
					writeNode( "Documentation", FORMATER.format( t.documentation ), writer );
					writeNode( "Links", t.links, writer, mapper );
				writer.closeNode();
			}
		}
	
		private static class FieldRecorder implements Recorder<FieldBinding>{
			
			FieldRecorder(){}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record(FieldBinding t, XMLWriter writer, RecordersMapper mapper){
				writer.openNode("Field");
					writer.addAttribute("name", t.name);
					writer.addAttribute("visibility", t.visibility.toChar());
					if(Modifier.isStatic(t.modifiers))
						writer.addAttribute("isStatic", true);
					if(Modifier.isTransient(t.modifiers))
						writer.addAttribute("isTransient", true);
					if(Modifier.isVolatile(t.modifiers))
						writer.addAttribute("isVolatile", true);
					if(Modifier.isFinal(t.modifiers))
						writer.addAttribute("isFinal", true);
					writeNode( "Type", t.type, writer );
					writeNode( "Documentation", FORMATER.format( t.documentation ), writer );
					writeNode( "Links", t.links, writer, mapper );
				writer.closeNode();
			}
		}
	
		private static class ConstructorRecorder implements Recorder<ConstructorBinding>{
			
			ConstructorRecorder(){}
	
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record(final ConstructorBinding t, XMLWriter writer, RecordersMapper mapper){
				writer.openNode("Constructor");
					writer.addAttribute("name", t.name);
					writer.addAttribute("visibility", t.visibility.toChar());
					writeNode( "Documentation", FORMATER.format( t.documentation ), writer );
					writeNode( "TypeParameters", t.typeParams, writer, mapper );
					writeNode( "Parameters", t.params, writer, mapper );
					writeNode( "Exceptions", t.exceptions, writer, mapper );
					writeNode( "Links", t.links, writer, mapper );
				writer.closeNode();
			}
		}
	
		private static class MethodRecorder implements Recorder<MethodBinding>{
			
			MethodRecorder(){}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record(MethodBinding t, XMLWriter writer, RecordersMapper mapper){
				writer.openNode("Method");
					writer.addAttribute("name", t.name);
					writer.addAttribute("visibility", t.visibility.toChar());
					if(Modifier.isStatic(t.modifiers))
						writer.addAttribute("isStatic", true);
					if(Modifier.isAbstract(t.modifiers))
						writer.addAttribute("isAbstract", true);
					if(Modifier.isNative(t.modifiers))
						writer.addAttribute("isNative", true);
					if(Modifier.isStrict(t.modifiers))
						writer.addAttribute("isStrictfp", true);
					if(Modifier.isFinal(t.modifiers))
						writer.addAttribute("isFinal", true);
					if(Modifier.isSynchronized(t.modifiers))
						writer.addAttribute("isSynchronized", true);
					writeNode( "Documentation", FORMATER.format( t.documentation ), writer );
					writeNode( "TypeParameters", t.typeParams, writer, mapper );
					writeNode( "Parameters", t.params, writer, mapper );
					if(t.returnType != null)
						mapper.getRecorder( ReturnTypeBinding.class ).record(t.returnType, writer, mapper );
					writeNode( "Exceptions", t.exceptions, writer, mapper );
					writeNode( "Links", t.links, writer, mapper );
				writer.closeNode();
			}
		}
	
		private static class InterfaceRecorder implements Recorder<ClassBinding.Interface>{
			
			InterfaceRecorder(){}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record(ClassBinding.Interface t, XMLWriter writer, RecordersMapper mapper){
				writer.openNode( "Interface" );
					writer.addAttribute( "name", t.name );
					writeNode( "Documentation", FORMATER.format( t.documentation ), writer );
					writeNode( "TypeParameters", t.parameters, writer, mapper );
					writeNode( "Links", t.links, writer, mapper );
					
					writeNode( "EnclosingClasses", t.enclosingClasses, writer, mapper );
					writeNode( "Interfaces", t.interfaces, writer, mapper );
					writeNode( "Fields", t.fields, writer, mapper );
					writeNode( "Methods", t.methods, writer, mapper );
				writer.closeNode();
			}
		}
	
		private static class EnumRecorder implements Recorder<ClassBinding.Enum>{
			
			EnumRecorder(){}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record(ClassBinding.Enum t, XMLWriter writer, RecordersMapper mapper){
				writer.openNode( "Enum" );
					writer.addAttribute( "name", t.name );
					writeNode( "Documentation", FORMATER.format( t.documentation ), writer );
					writeNode( "Links", t.links, writer, mapper );
					
					writeNode( "EnclosingClasses", t.enclosingClasses, writer, mapper );
					writeNode( "Interfaces", t.interfaces, writer, mapper );
					writeNode( "Constants", t.constants, writer, mapper );
					writeNode( "Fields", t.fields, writer, mapper );
					writeNode( "Methods", t.methods, writer, mapper );
				writer.closeNode();
			}
		}
	
		private static class ClassRecorder implements Recorder<ClassBinding.Clazz>{
			
			ClassRecorder(){}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record(ClassBinding.Clazz t, XMLWriter writer, RecordersMapper mapper){
				writer.openNode( "Class" );
					writer.addAttribute( "name", t.name );
					if(t.isAbstract)
						writer.addAttribute( "isAbstract", t.isAbstract );
					writeNode( "Documentation", FORMATER.format( t.documentation ), writer );
					writeNode( "TypeParameters", t.parameters, writer, mapper );
					writeNode( "Links", t.links, writer, mapper );
					
					writeNode( "Hierarchy", t.hierarchy, writer, mapper );
					writeNode( "EnclosingClasses", t.enclosingClasses, writer, mapper );
					writeNode( "Interfaces", t.interfaces, writer, mapper );
					writeNode( "Fields", t.fields, writer, mapper );
					writeNode( "Constructors", t.constructors, writer, mapper );
					writeNode( "Methods", t.methods, writer, mapper );
				writer.closeNode();
			}
		}
		
		void register( XMLConverter converter ){

			converter.register( new SimpleInterfaceRecorder() );
			converter.register( new SimpleEnumRecorder() );
			converter.register( new SimpleClassRecorder() );
			converter.register( new DocumentedTypeRecorder() );
			converter.register( new ParameterRecorder() );
			converter.register( new LinkRecorder() );
			converter.register( new ConstantRecorder() );
			converter.register( new FieldRecorder() );
			converter.register( new ConstructorRecorder() );
			converter.register( new MethodRecorder() );
			converter.register( new InterfaceRecorder() );
			converter.register( new EnumRecorder() );
			converter.register( new ClassRecorder() );

		}
		
	}
	
	private static final class ODTRecorders extends Recorders{
		
		ODTRecorders(){}
	
		static final HTMLFormater FORMATER = new Cleaner( new SimpleSerializer() );
		
		/**
		 * Method writeNode.
		 * @param nodeName String
		 * @param content String
		 * @param writer XMLWriter
		 */
		static void writeNode( String nodeName, String content, XMLWriter writer ) {
			if( content != null && content.length() > 0 ){
				writer.openNode(nodeName);
					writer.writeCDATA(content);
				writer.closeNode();
			}
		}
		
		/**
		 * Method writeNode.
		 * @param nodeName String
		 * @param collection Collection<T>
		 * @param writer XMLWriter
		 * @param mapper RecordersMapper
		 */
		static <T> void writeNode( String nodeName, Collection<T> collection, XMLWriter writer, RecordersMapper mapper ) {
			if( collection != null && collection.size() > 0 ){
				writer.openNode( nodeName );
				for( T t: collection )
					mapper.getRecorder(t.getClass()).record(t, writer, mapper);
				writer.closeNode();
			}
		}
		
		private static class SimpleInterfaceRecorder implements Recorder<SimpleClassBinding.Interface>{
			
			SimpleInterfaceRecorder(){}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record(SimpleClassBinding.Interface t, XMLWriter writer, RecordersMapper mapper){
				writeNode( "Interface", t.name, writer );
			}
		}
	
		private static class SimpleEnumRecorder implements Recorder<SimpleClassBinding.Enum>{
			
			SimpleEnumRecorder(){}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record(SimpleClassBinding.Enum t, XMLWriter writer, RecordersMapper mapper){
				writeNode( "Enum", t.name, writer );
			}
		}
	
		private static class SimpleClassRecorder implements Recorder<SimpleClassBinding.Clazz>{
			
			SimpleClassRecorder(){}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record(SimpleClassBinding.Clazz t, XMLWriter writer, RecordersMapper mapper){
				writer.openNode("Class");
					if(t.isAbstract)
						writer.addAttribute("isAbstract", t.isAbstract);
					writer.writeCDATA(t.name);
				writer.closeNode();
			}
		}
	
		private static class DocumentedTypeRecorder implements Recorder<DocumentedType>{
			
			DocumentedTypeRecorder(){}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record(DocumentedType t, XMLWriter writer, RecordersMapper mapper){
				String nodeName = null;
				if( t instanceof TypeParamBinding)
					nodeName = "Parameter";
				else if( t instanceof ReturnTypeBinding)
					nodeName = "ReturnType";
				else if( t instanceof ExceptionBinding)
					nodeName = "Exception";
				assert(nodeName != null): "!!!Tipo: "+ t.getClass().toString() + " desconocido";
				
				writer.openNode(nodeName);
					writeNode( "Type", t.type, writer );
					writeNode( "Documentation", FORMATER.format( t.documentation ), writer );
				writer.closeNode();
			}
		}
	
		private static class ParameterRecorder implements Recorder<ParameterBinding>{
			
			ParameterRecorder(){}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record(ParameterBinding t, XMLWriter writer, RecordersMapper mapper){
				writer.openNode("Parameter");
					if(t.name!=null)
						writer.addAttribute("name", t.name);
					writeNode( "Type", t.type, writer );
					writeNode( "Documentation", FORMATER.format( t.documentation ), writer );
				writer.closeNode();
			}
		}
		
		private static class LinkRecorder implements Recorder<LinkBinding>{
			
			LinkRecorder(){}
	
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record(LinkBinding t, XMLWriter writer, RecordersMapper mapper){
				writeNode( "Link", t.link,  writer );
			}
		}
	
		private static class ConstantRecorder implements Recorder<ConstantBinding>{
			
			ConstantRecorder(){}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record(ConstantBinding t, XMLWriter writer, RecordersMapper mapper){
				writer.openNode("Constant");
					writer.addAttribute("name", t.name);
					writeNode( "Documentation", FORMATER.format( t.documentation ), writer );
					writeNode( "Links", t.links, writer, mapper );
				writer.closeNode();
			}
		}
	
		private static class FieldRecorder implements Recorder<FieldBinding>{
			
			FieldRecorder(){}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record(FieldBinding t, XMLWriter writer, RecordersMapper mapper){
				writer.openNode("Field");
					writer.addAttribute("name", t.name);
					writer.addAttribute("visibility", t.visibility.toChar());
					if(Modifier.isStatic(t.modifiers))
						writer.addAttribute("isStatic", true);
					if(Modifier.isTransient(t.modifiers))
						writer.addAttribute("isTransient", true);
					if(Modifier.isVolatile(t.modifiers))
						writer.addAttribute("isVolatile", true);
					if(Modifier.isFinal(t.modifiers))
						writer.addAttribute("isFinal", true);
					writeNode( "Type", t.type, writer );
					writeNode( "Documentation", FORMATER.format( t.documentation ), writer );
					writeNode( "Links", t.links, writer, mapper );
				writer.closeNode();
			}
		}
	
		private static class ConstructorRecorder implements Recorder<ConstructorBinding>{
			
			ConstructorRecorder(){}
	
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record(final ConstructorBinding t, XMLWriter writer, RecordersMapper mapper){
				writer.openNode("Constructor");
					writer.addAttribute("name", t.name);
					writer.addAttribute("visibility", t.visibility.toChar());
					writeNode( "Documentation", FORMATER.format( t.documentation ), writer );
					writeNode( "TypeParameters", t.typeParams, writer, mapper );
					writeNode( "Parameters", t.params, writer, mapper );
					writeNode( "Exceptions", t.exceptions, writer, mapper );
					writeNode( "Links", t.links, writer, mapper );
				writer.closeNode();
			}
		}
	
		private static class MethodRecorder implements Recorder<MethodBinding>{
			
			MethodRecorder(){}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record( MethodBinding t, XMLWriter writer, RecordersMapper mapper ){
				writer.openNode("Method");
					writer.addAttribute("name", t.name);
					writer.addAttribute("visibility", t.visibility.toChar());
					if(Modifier.isStatic(t.modifiers))
						writer.addAttribute("isStatic", true);
					if(Modifier.isAbstract(t.modifiers))
						writer.addAttribute("isAbstract", true);
					if(Modifier.isNative(t.modifiers))
						writer.addAttribute("isNative", true);
					if(Modifier.isStrict(t.modifiers))
						writer.addAttribute("isStrictfp", true);
					if(Modifier.isFinal(t.modifiers))
						writer.addAttribute("isFinal", true);
					if(Modifier.isSynchronized(t.modifiers))
						writer.addAttribute("isSynchronized", true);
					writeNode( "Documentation", FORMATER.format( t.documentation ), writer );
					writeNode( "TypeParameters", t.typeParams, writer, mapper );
					writeNode( "Parameters", t.params, writer, mapper );
					if(t.returnType != null)
						mapper.getRecorder( ReturnTypeBinding.class ).record(t.returnType, writer, mapper );
					writeNode( "Exceptions", t.exceptions, writer, mapper );
					writeNode( "Links", t.links, writer, mapper );
				writer.closeNode();
			}
		}
	
		private static class InterfaceRecorder implements Recorder<ClassBinding.Interface>{
			
			InterfaceRecorder(){}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record(ClassBinding.Interface t, XMLWriter writer, RecordersMapper mapper){
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
			}
		}
	
		private static class EnumRecorder implements Recorder<ClassBinding.Enum>{
			
			EnumRecorder(){}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record(ClassBinding.Enum t, XMLWriter writer, RecordersMapper mapper){
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
			}
		}
	
		private static class ClassRecorder implements Recorder<ClassBinding.Clazz>{
			
			ClassRecorder(){}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
			 */
			@Override
			public void record( ClassBinding.Clazz t, XMLWriter writer, RecordersMapper mapper ){
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
				writer.write( "Class: " + t.name );
				writer.emptyNode( "text:line-break" );
				writer.write( t.documentation );
				writer.closeNode();
			}
		}
		
		void register( XMLConverter converter ){

			converter.register( new SimpleInterfaceRecorder() );
			converter.register( new SimpleEnumRecorder() );
			converter.register( new SimpleClassRecorder() );
			converter.register( new DocumentedTypeRecorder() );
			converter.register( new ParameterRecorder() );
			converter.register( new LinkRecorder() );
			converter.register( new ConstantRecorder() );
			converter.register( new FieldRecorder() );
			converter.register( new ConstructorRecorder() );
			converter.register( new MethodRecorder() );
			converter.register( new InterfaceRecorder() );
			converter.register( new EnumRecorder() );
			converter.register( new ClassRecorder() );

		}
		
	}
	
	abstract void register( XMLConverter converter );
	
	public static void register( Mode mode, XMLConverter converter ){
		switch(mode){
		case SVG:
			throw new UnsupportedOperationException();
		case XHTML:
			throw new UnsupportedOperationException();
		case ODT:
			new ODTRecorders().register( converter );
			break;
		default:
			new XMLRecorders().register( converter );
		}
	}
}
