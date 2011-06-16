/* 
 * XMLWriter.java
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
package org.sam.xml;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 
 */
public final class XMLWriter{
	
	/*
	private static class StringPrintStream extends PrintStream{
		StringPrintStream(){
			super( new java.io.ByteArrayOutputStream() );
		}
		
		public String toString(String enconding) throws java.io.IOException{
			out.flush();
			return ((java.io.ByteArrayOutputStream)out).toString(enconding);
		}
		
		public String toString(){
			try {
				return toString("UTF8");
			} catch (java.io.IOException e) {
				return ((java.io.ByteArrayOutputStream)out).toString();
			}
		}
	}//*/
	
	private interface TabsManager{
		
		/**
		 * {@code TabsManager} que no hace nada.
		 */
		static final TabsManager Default = new TabsManager(){

			/* (non-Javadoc)
			 * @see org.sam.xml.XMLWriter.TabsManager#add()
			 */
			@Override
			final public void add(){}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.XMLWriter.TabsManager#remove()
			 */
			@Override
			final public void remove(){}

			/* (non-Javadoc)
			 * @see org.sam.xml.XMLWriter.TabsManager#get()
			 */
			@Override
			final public String get(){
				return "";
			}
		};
		
		/**
		 * {@code TabsManager} que gestiona las tabulaciones mediante tabuladores.
		 */
		static final class Tabulate implements TabsManager{
			
			private String tabs = "";
			
			/* (non-Javadoc)
			 * @see org.sam.xml.XMLWriter.TabsManager#add()
			 */
			@Override
			final public void add(){
				tabs = tabs.concat("\t");
			}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.XMLWriter.TabsManager#remove()
			 */
			@Override
			final public void remove(){
				tabs = tabs.substring(1);
			}

			/* (non-Javadoc)
			 * @see org.sam.xml.XMLWriter.TabsManager#get()
			 */
			@Override
			final public String get(){
				return tabs;
			}
		}
		
		/**
		 * {@code TabsManager} que gestiona las tabulaciones mediante tabuladores.
		 */
		static final class Spaces implements TabsManager{
			
			private final String spacesTab;
			private String tabs = "";
			
			Spaces(int nSpaces){
				StringBuilder builder = new StringBuilder();
				for( int i = 0; i < nSpaces; i++ )
					builder.append( ' ' );
				spacesTab = builder.toString();
			}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.XMLWriter.TabsManager#add()
			 */
			@Override
			final public void add(){
				tabs = tabs.concat(spacesTab);
			}
			
			/* (non-Javadoc)
			 * @see org.sam.xml.XMLWriter.TabsManager#remove()
			 */
			@Override
			final public void remove(){
				tabs = tabs.substring(spacesTab.length());
			}

			/* (non-Javadoc)
			 * @see org.sam.xml.XMLWriter.TabsManager#get()
			 */
			@Override
			final public String get(){
				return tabs;
			}
		}
		
		/**
		 * Añade un tabulador.
		 */
		void add();
		
		/**
		 * Elimina un tabulador.
		 */
		void remove();

		/**
		 * @return Los tabuladores del nivel actual.
		 */
		String get();
	}
	
	private static class Node{
		public final String name;
		public boolean isSingleLineNode;

		Node( String name, boolean isSingleLineNode ){
			this.name = name;
			this.isSingleLineNode = isSingleLineNode;
		}
	}
	
	private static PrintStream toPrintStream( OutputStream out ){
		if( out instanceof PrintStream )
			return (PrintStream)out;
		try{
			return new PrintStream( out, false, "UTF-8" );
		}catch( UnsupportedEncodingException ignorada ){
			assert ( false ): ignorada.toString();
			return null;
		}
	}
	
	private final PrintStream out;
	private final Deque<Node> nodeStack;
	private final StringBuffer attributes;
	private final TabsManager tabs;
	private final String eol;

	private XMLWriter( OutputStream out, TabsManager manager ){
		this.out = toPrintStream( out );
		this.nodeStack = new ArrayDeque<Node>();
		this.attributes = new StringBuffer( 512 );
		this.attributes.setLength( 0 );
		this.tabs = manager;
		this.eol = manager != TabsManager.Default ? System.getProperty( "line.separator" ): "";
	}
	
	/**
	 * Constructor for XMLWriter.
	 * @param out OutputStream
	 * @param tabulate boolean
	 */
	public XMLWriter( OutputStream out, boolean tabulate ){
		this( out, tabulate ? new TabsManager.Tabulate(): TabsManager.Default );
	}

	public XMLWriter( OutputStream out, int tabulateSpaces ){
		this( out, tabulateSpaces > 0 ? new TabsManager.Spaces( tabulateSpaces ): TabsManager.Default );
	}
	
	/**
	 * Constructor for XMLWriter.
	 * @param out OutputStream
	 */
	public XMLWriter( OutputStream out ){
		this( out, false );
	}
	
	private boolean isFirstCall       = true;
	private boolean isClosedStartNode = true;
	private boolean hasContent        = false;
	
	private void flushAttributes(){
		if( attributes.length() > 0 ){
			out.append( attributes.toString() );
			attributes.setLength( 0 );
		}
	}
	
	/**
	 * Method openNode.
	 * @param nodeName String
	 */
	public void openNode( String nodeName ){
		if( !isClosedStartNode ){
			flushAttributes();
			out.append( '>' );
		}
		if( isFirstCall )
			isFirstCall = false;
		else if( nodeStack.peek() == null || !nodeStack.peek().isSingleLineNode )
			out.append( eol ).append( tabs.get() );
		
		out.append( '<' ).append( nodeName );
		isClosedStartNode = false;
		hasContent = false;
		nodeStack.push( new Node( nodeName, false ) );
		tabs.add();
	}
	
	/**
	 * Method addAttribute.
	 * @param name String
	 * @param value String
	 */
	public void addAttribute(String name, String value) {
		if( isClosedStartNode )
			throw new IllegalStateException();
		attributes.append(' ').append(name).append("=\"").append(value).append('\"');
	}
	
	/**
	 * Method addAttribute.
	 * @param name String
	 * @param value boolean
	 */
	public void addAttribute(String name, boolean value) {
		if( isClosedStartNode )
			throw new IllegalStateException();
		attributes.append(' ').append(name).append("=\"").append(value).append('\"');
	}
	
	/**
	 * Method addAttribute.
	 * @param name String
	 * @param value char
	 */
	public void addAttribute(String name, char value) {
		if( isClosedStartNode )
			throw new IllegalStateException();
		attributes.append(' ').append(name).append("=\"").append(value).append('\"');
	}
	
	/**
	 * Method addAttribute.
	 * @param name String
	 * @param value int
	 */
	public void addAttribute(String name, int value) {
		if( isClosedStartNode )
			throw new IllegalStateException();
		attributes.append(' ').append(name).append("=\"").append(value).append('\"');
	}
	
	/**
	 * Method addAttribute.
	 * @param name String
	 * @param value long
	 */
	public void addAttribute(String name, long value) {
		if( isClosedStartNode )
			throw new IllegalStateException();
		attributes.append(' ').append(name).append("=\"").append(value).append('\"');
	}
	
	/**
	 * Method addAttribute.
	 * @param name String
	 * @param value float
	 */
	public void addAttribute(String name, float value) {
		if( isClosedStartNode )
			throw new IllegalStateException();
		attributes.append(' ').append(name).append("=\"").append(value).append('\"');
	}
	
	/**
	 * Method addAttribute.
	 * @param name String
	 * @param value double
	 */
	public void addAttribute(String name, double value) {
		if( isClosedStartNode )
			throw new IllegalStateException();
		attributes.append(' ').append(name).append("=\"").append(value).append('\"');
	}
	
	/**
	 * Method addAttribute.
	 * @param name String
	 * @param value Object
	 */
	public void addAttribute(String name, Object value) {
		if( isClosedStartNode )
			throw new IllegalStateException();
		attributes.append(' ').append(name).append("=\"").append(value).append('\"');
	}
	
	/**
	 * Method writeCDATA.
	 * @param content String
	 */
	public void writeCDATA( String content ){
		hasContent = content != null && content.length() > 0;
		if( hasContent ){
			if( !isClosedStartNode ){
				flushAttributes();
				out.append( '>' );
				isClosedStartNode = true;
			}
			out.append( "<![CDATA[" ).append( content ).append( "]]>" );
			nodeStack.peek().isSingleLineNode = true;
		}
	}

	/**
	 * Method writeCDATA.
	 * @param content String
	 */
	public void write( String content ){
		hasContent = content != null && content.length() > 0;
		if( hasContent ){
			if( !isClosedStartNode ){
				flushAttributes();
				out.append( '>' );
				isClosedStartNode = true;
			}
			out.append( content );
			nodeStack.peek().isSingleLineNode = true;
		}
	}
	
	public void closeNode(){
		tabs.remove();
		Node previous = nodeStack.pop();
	
		if( hasContent ){
			if( !previous.isSingleLineNode )
				out.append( eol ).append( tabs.get() );
			out.append( "</" ).append( previous.name ).append( '>' );
		}else{
			flushAttributes();
			out.append( " />" );
		}
		isClosedStartNode = true;
		hasContent = true;
	}
	
	public void emptyNode( String nodeName ){
		openNode( nodeName );
		closeNode();
	}
}