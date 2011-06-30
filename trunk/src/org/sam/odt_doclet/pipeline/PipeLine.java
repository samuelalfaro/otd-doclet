/* 
 * PipeLine.java
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
package org.sam.odt_doclet.pipeline;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import org.sam.odt_doclet.bindings.ClassBinding;
import org.sam.pipeline.Filter;
import org.sam.pipeline.Pump;
import org.sam.pipeline.Sink;

/**
 */
public final class PipeLine{

	private static Pump<ClassBinding>  toXML;
	private static ToSVG               toSVG;
	private static Filter              toPNG;
	private static Sink<BufferedImage> toIMG;

	static{
		try{
			toXML = new ToXML();
			toSVG = new ToSVG( toXML );
			toPNG = new ToPNG( toSVG );
			toIMG = new ToIMG( toPNG );
		}catch( Throwable e ){
			e.printStackTrace();
		}
	}

	private PipeLine(){
	}

	/**
	 * @param clazz
	 * @param out
	 * @throws IOException
	 */
	public static void toXML( ClassBinding clazz, OutputStream out ) throws IOException{
		toXML.setSource( clazz );
		toXML.process( out );
	}

	/**
	 * @param clazz
	 * @param scale
	 * @param out
	 * @throws IOException
	 */
	public static void toSVG( ClassBinding clazz, double scale, OutputStream out ) throws IOException{
		toXML.setSource( clazz );
		toSVG.setScale( scale );
		toSVG.process( out );
	}

	/**
	 * @param clazz
	 * @param out
	 * @throws IOException
	 */
	public static void toSVG( ClassBinding clazz, OutputStream out ) throws IOException{
		toSVG( clazz, 1.0, out );
	}

	/**
	 * @param clazz
	 * @param scale
	 * @param out
	 * @return
	 * @throws IOException
	 */
	public static Dimension toPNG( ClassBinding clazz, double scale, OutputStream out ) throws IOException{
		toXML.setSource( clazz );
		toSVG.setScale( scale );
		PNGSizeGrabber grabber = new PNGSizeGrabber( out );
		toPNG.process( grabber );
		return new Dimension( grabber.getWidth(), grabber.getHeight() );
	}

	/**
	 * @param clazz
	 * @param out
	 * @return
	 * @throws IOException
	 */
	public static Dimension toPNG( ClassBinding clazz, OutputStream out ) throws IOException{
		return toPNG( clazz, 1.0, out );
	}

	/**
	 * @param clazz
	 * @return BufferedImage
	 * @throws IOException
	 */
	public static BufferedImage toIMG( ClassBinding clazz ) throws IOException{
		toXML.setSource( clazz );
		toIMG.process();
		return toIMG.getDestination();
	}
}
