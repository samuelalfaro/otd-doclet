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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 
 */
public final class XMLWriter{

	private static Appendable getAppendable( OutputStream out ){
		if( Appendable.class.isAssignableFrom( out.getClass() ) )
			return (Appendable)out;
		try{
			return new PrintStream( out, true, "UTF-8" );
		}catch( UnsupportedEncodingException ignorada ){
			assert ( false ): ignorada.toString();
			return null;
		}
	}

	private interface TabsManager{

		/**
		 * {@code TabsManager} que no hace nada.
		 */
		static final TabsManager Default = new TabsManager(){

			/*
			 * (non-Javadoc)
			 * @see org.sam.xml.XMLWriter.TabsManager#add()
			 */
			@Override
			final public void add(){
			}

			/*
			 * (non-Javadoc)
			 * @see org.sam.xml.XMLWriter.TabsManager#remove()
			 */
			@Override
			final public void remove(){
			}

			/*
			 * (non-Javadoc)
			 * @see org.sam.xml.XMLWriter.TabsManager#get()
			 */
			@Override
			final public String get(){
				return "";
			}
		};

		/**
		 * {@code TabsManager} que tabula el contenido usando tabuladores.
		 */
		static final class Tabulate implements TabsManager{

			private String tabs = "";

			/*
			 * (non-Javadoc)
			 * @see org.sam.xml.XMLWriter.TabsManager#add()
			 */
			@Override
			final public void add(){
				tabs = tabs.concat( "\t" );
			}

			/*
			 * (non-Javadoc)
			 * @see org.sam.xml.XMLWriter.TabsManager#remove()
			 */
			@Override
			final public void remove(){
				tabs = tabs.substring( 1 );
			}

			/*
			 * (non-Javadoc)
			 * @see org.sam.xml.XMLWriter.TabsManager#get()
			 */
			@Override
			final public String get(){
				return tabs;
			}
		}

		/**
		 * {@code TabsManager} que tabula el contenido usando espacios.
		 */
		static final class Spaces implements TabsManager{

			private final String spacesTab;
			private String tabs = "";

			Spaces( int nSpaces ){
				StringBuilder builder = new StringBuilder();
				for( int i = 0; i < nSpaces; i++ )
					builder.append( ' ' );
				spacesTab = builder.toString();
			}

			/*
			 * (non-Javadoc)
			 * @see org.sam.xml.XMLWriter.TabsManager#add()
			 */
			@Override
			final public void add(){
				tabs = tabs.concat( spacesTab );
			}

			/*
			 * (non-Javadoc)
			 * @see org.sam.xml.XMLWriter.TabsManager#remove()
			 */
			@Override
			final public void remove(){
				tabs = tabs.substring( spacesTab.length() );
			}

			/*
			 * (non-Javadoc)
			 * @see org.sam.xml.XMLWriter.TabsManager#get()
			 */
			@Override
			final public String get(){
				return tabs;
			}
		}

		/**
		 * Añade una tabulación.
		 */
		void add();

		/**
		 * Elimina una tabulación.
		 */
		void remove();

		/**
		 * @return La cadena con las tabulaciones del nivel actual.
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

	private final Appendable out;
	private final Deque<Node> nodeStack;
	private final StringBuffer attributes;
	private final TabsManager tabs;
	private final String eol;

	private transient boolean isFirstCall = true;
	private transient boolean isClosedStartNode = true;
	private transient boolean hasContent = false;

	private XMLWriter( Appendable out, TabsManager manager ){
		this.out = out;
		this.nodeStack = new ArrayDeque<Node>();
		this.attributes = new StringBuffer( 512 );
		this.attributes.setLength( 0 );
		this.tabs = manager;
		this.eol = manager != TabsManager.Default ? System.getProperty( "line.separator" ): "";
	}

	/**
	 * Constructor for XMLWriter.
	 * @param out
	 * @param tabulate
	 */
	public XMLWriter( Appendable out, boolean tabulate ){
		this( out, tabulate ? new TabsManager.Tabulate(): TabsManager.Default );
	}

	/**
	 * Constructor for XMLWriter.
	 * @param out
	 * @param tabulateSpaces
	 */
	public XMLWriter( Appendable out, int tabulateSpaces ){
		this( out, tabulateSpaces > 0 ? new TabsManager.Spaces( tabulateSpaces ): TabsManager.Default );
	}

	/**
	 * Constructor for XMLWriter.
	 * @param out
	 */
	public XMLWriter( Appendable out ){
		this( out, TabsManager.Default );
	}

	/**
	 * Constructor for XMLWriter.
	 * @param out
	 * @param tabulate
	 */
	public XMLWriter( OutputStream out, boolean tabulate ){
		this( getAppendable( out ), tabulate ? new TabsManager.Tabulate(): TabsManager.Default );
	}

	/**
	 * Constructor for XMLWriter.
	 * @param out
	 * @param tabulateSpaces
	 */
	public XMLWriter( OutputStream out, int tabulateSpaces ){
		this( getAppendable( out ), tabulateSpaces > 0 ? new TabsManager.Spaces( tabulateSpaces ): TabsManager.Default );
	}

	/**
	 * Constructor for XMLWriter.
	 * @param out
	 */
	public XMLWriter( OutputStream out ){
		this( getAppendable( out ), TabsManager.Default );
	}

	private void flushAttributes() throws IOException{
		if( attributes.length() > 0 ){
			out.append( attributes.toString() );
			attributes.setLength( 0 );
		}
	}

	/**
	 * Method openNode.
	 * @param nodeName String
	 */
	public void openNode( String nodeName ) throws IOException{
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
	public void addAttribute( String name, String value ) throws IOException{
		if( isClosedStartNode )
			throw new IllegalStateException();
		if( value != null && value.length() > 0 ){
			attributes.append( ' ' ).append( name ).append( "=\"" );
			StringDigester.XMLCharsFilter.digestString( value, attributes );
			attributes.append( '\"' );
		}
	}

	/**
	 * Method addAttribute.
	 * @param name String
	 * @param value boolean
	 * @throws IOException
	 */
	public void addAttribute( String name, boolean value ) throws IOException{
		if( isClosedStartNode )
			throw new IllegalStateException();
		attributes.append( ' ' ).append( name ).append( "=\"" ).append( value ).append( '\"' );
	}

	/**
	 * Method addAttribute.
	 * @param name String
	 * @param value char
	 * @throws IOException
	 */
	public void addAttribute( String name, char value ) throws IOException{
		if( isClosedStartNode )
			throw new IllegalStateException();
		attributes.append( ' ' ).append( name ).append( "=\"" );
		switch( value ){
		case '&':
			attributes.append( "&amp;" );
			break;
		case '<':
			attributes.append( "&lt;" );
			break;
		case '>':
			attributes.append( "&gt;" );
			break;
		case '\"':
			attributes.append( "&quot;" );
			break;
		case '\'':
			attributes.append( "&apos;" );
			break;
		default:
			attributes.append( value );
		}
		attributes.append( '\"' );
	}

	/**
	 * Method addAttribute.
	 * @param name String
	 * @param value int
	 * @throws IOException
	 */
	public void addAttribute( String name, int value ) throws IOException{
		if( isClosedStartNode )
			throw new IllegalStateException();
		attributes.append( ' ' ).append( name ).append( "=\"" ).append( value ).append( '\"' );
	}

	/**
	 * Method addAttribute.
	 * @param name String
	 * @param value long
	 * @throws IOException
	 */
	public void addAttribute( String name, long value ) throws IOException{
		if( isClosedStartNode )
			throw new IllegalStateException();
		attributes.append( ' ' ).append( name ).append( "=\"" ).append( value ).append( '\"' );
	}

	/**
	 * Method addAttribute.
	 * @param name String
	 * @param value float
	 * @throws IOException
	 */
	public void addAttribute( String name, float value ) throws IOException{
		if( isClosedStartNode )
			throw new IllegalStateException();
		attributes.append( ' ' ).append( name ).append( "=\"" ).append( value ).append( '\"' );
	}

	/**
	 * Method addAttribute.
	 * @param name String
	 * @param value double
	 * @throws IOException
	 */
	public void addAttribute( String name, double value ) throws IOException{
		if( isClosedStartNode )
			throw new IllegalStateException();
		attributes.append( ' ' ).append( name ).append( "=\"" ).append( value ).append( '\"' );
	}

	/**
	 * Method addAttribute.
	 * @param name String
	 * @param value Object
	 */
	public void addAttribute( String name, Object value ) throws IOException{
		if( value != null )
			addAttribute( name, value.toString() );
	}

	public void discardAttributes(){
		if( isClosedStartNode )
			throw new IllegalStateException();
		attributes.setLength( 0 );
	}

	public void write( String content, StringDigester digester ) throws IOException{
		hasContent = content != null && content.length() > 0;
		if( hasContent ){
			if( !isClosedStartNode ){
				flushAttributes();
				out.append( '>' );
				isClosedStartNode = true;
			}
			digester.digestString( content, out );
			assert ( nodeStack.peek() != null ): "Escribiendo en nodo vacio: " + content;
			if( nodeStack.peek() != null )
				nodeStack.peek().isSingleLineNode = true;
		}
	}

	/**
	 * Method writeCDATA.
	 * @param content String
	 * TODO cambiar a insertCDATANode
	 */
	public void writeCDATA( String content ) throws IOException{
		write( content, StringDigester.CDATA );
	}

	/**
	 * @param content
	 */
	public void write( String content ) throws IOException{
		write( content, StringDigester.XMLCharsFilter );
	}

	/**
	 * @param content
	 */
	public void insert( String content ) throws IOException{
		write( content, StringDigester.Default );
	}

	public void closeNode() throws IOException{
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

	public void emptyNode( String nodeName ) throws IOException{
		openNode( nodeName );
		closeNode();
	}

	public String getCurrentNodeName(){
		return nodeStack.size() == 0 ? "": nodeStack.peek().name;
	}

	public boolean currentNodeHasContent(){
		return hasContent;
	}

	public boolean hasParent( String nodeName ){
		for( Node node: nodeStack )
			if( node.name.equals( nodeName ) )
				return true;
		return false;
	}

	public void closeUntilParent( String nodeName ) throws IOException{
		if( hasParent( nodeName ) ){
			while( !nodeStack.peek().name.equals( nodeName ) )
				closeNode();
			closeNode();
		}
	}
}