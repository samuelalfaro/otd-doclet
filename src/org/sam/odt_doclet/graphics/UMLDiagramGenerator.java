/* 
 * UMLDiagramGenerator.java
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
package org.sam.odt_doclet.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.sam.odt_doclet.Loader;
import org.sam.odt_doclet.bindings.ClassBinding;
import org.sam.odt_doclet.bindings.Recorders;
import org.sam.pipeline.Filter;
import org.sam.pipeline.FilterAbs;
import org.sam.pipeline.FilterException;
import org.sam.pipeline.Pump;
import org.sam.pipeline.Sink;
import org.sam.xml.XMLConverter;
import org.sam.xml.XMLWriter;

/**
 */
public final class UMLDiagramGenerator{
	
	private static class ToXML implements Pump<ClassBinding>{

		private final XMLConverter converter;
		private ClassBinding source;

		ToXML(){
			converter = new XMLConverter();
			Recorders.register( Recorders.Mode.XML, converter );
		}

		/* (non-Javadoc)
		 * @see org.sam.pipeline.Pump#setSource(java.lang.Object)
		 */
		@Override
		public void setSource( ClassBinding source ){
			this.source = source;
		}

		/*
		 * (non-Javadoc)
		 * @see org.sam.pipeline.Pump#process(java.io.OutputStream)
		 */
		@Override
		public void process( OutputStream out ) throws IOException{
			converter.setWriter( new XMLWriter( out ) );
			converter.write( source );
		}
	}
	
	private static class ToSVG extends FilterAbs{

		final Transformer transformer;

		/**
		 * @throws TransformerConfigurationException
		 */
		ToSVG() throws TransformerConfigurationException{

			transformer = TransformerFactory.newInstance().newTransformer(
					Loader.getResourceAsStreamSource( "resources/shared/toSVG.xsl" ) );
			transformer.setParameter( "widthChar1", 6.6 );
			transformer.setParameter( "widthChar2", 9.0 );
		}

		/*
		 * (non-Javadoc)
		 * @see org.sam.pipeline.Filter#process(java.io.InputStream, java.io.OutputStream)
		 */
		@Override
		public void process( InputStream in, OutputStream out ) throws IOException, FilterException{
			try{
				transformer.transform( new StreamSource( in ), new StreamResult( out ) );
			}catch( TransformerException e ){
				throw new FilterException( "ToSVG Error!!!", e );
			}
		}
	}

	private final Pump<ClassBinding>  toXML;
	private final ToSVG               toSVG;
	private final Filter              toPNG;
	private final Sink<BufferedImage> toIMG;

	public UMLDiagramGenerator() throws TransformerConfigurationException{
		toXML = new ToXML();
		toSVG = new ToSVG();
		toSVG.setSource( toXML );
		toPNG = new SVGtoPNG( new File( Loader.getRunPath() + "resources" ).toURI() );
		toPNG.setSource( toSVG );
		toIMG = new SVGtoBufferedImage( new File( Loader.getRunPath() + "resources" ).toURI() );
		toIMG.setSource( toSVG );
		
		setScale(1.0);
	}
	
	public void setScale( double scale ){
		toSVG.transformer.setParameter( "scale", scale );
	}

	public void setBackground( String background ){
		toSVG.transformer.setParameter( "background", background );
	}

	public void setBackground( int r, int g, int b ){
		setBackground( String.format( "#%X%X%X", r, g, b ) );
	}

	public void setBackground( Color color ){
		setBackground( color.getRed(), color.getGreen(), color.getBlue() );
	}

	/**
	 * @param clazz
	 * @param out
	 * @throws IOException
	 */
	public void toSVG( ClassBinding clazz, OutputStream out ) throws IOException{
		toXML.setSource( clazz );
		toSVG.process( out );
	}

	/**
	 * @param clazz
	 * @param out
	 * @return Dimension
	 * @throws IOException
	 */
	public Dimension toPNG( ClassBinding clazz, OutputStream out ) throws IOException{
		toXML.setSource( clazz );
		PNGSizeGrabber grabber = new PNGSizeGrabber( out );
		toPNG.process( grabber );
		return new Dimension( grabber.getWidth(), grabber.getHeight() );
	}

	/**
	 * @param clazz
	 * @return BufferedImage
	 * @throws IOException
	 */
	public BufferedImage toIMG( ClassBinding clazz ) throws IOException{
		toXML.setSource( clazz );
		toIMG.process();
		return toIMG.getDestination();
	}
}
