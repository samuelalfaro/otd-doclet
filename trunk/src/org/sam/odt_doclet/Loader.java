/* 
 * Loader.java
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
package org.sam.odt_doclet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * 
 */
public class Loader{
	
	private Loader(){}
	
	private static ClassLoader loader = null;
	
	private static ClassLoader getClassLoader(){
		if( loader == null ){
			loader = Thread.currentThread().getContextClassLoader();
		}
		return loader;
	}
	
	private static String runPath = null;
	
	public static String getRunPath(){
		if( runPath == null ){
			String path = Loader.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			if( path.endsWith( ".jar" ) )
				runPath = path.substring( 0, path.lastIndexOf( File.separatorChar ) + 1 );
			else{
				path = path.substring( 0, path.lastIndexOf( File.separatorChar ) );
				runPath = path.substring( 0, path.lastIndexOf( File.separatorChar ) + 1 );
			}
		}
		return runPath;
	}
	
	public static InputStream getResourceAsStream( String name ){
		URL url = getClassLoader().getResource( name );
		try{
			if( url != null )
				return url.openStream();
			
			File file = new File( name );
			if( file.exists() && file.canRead() )
				return new FileInputStream( file );
			
			file = new File( getRunPath() + name );
			if( file.exists() && file.canRead() )
				return new FileInputStream( file );
		}catch( IOException e ){
		}
		return null;
	}
	
	public static URI getResourceAsURI( String name ){
		URL url = getClassLoader().getResource( name );
		if( url != null )
			try{
				return url.toURI();
			}catch( URISyntaxException ignorada ){
			}
			
		File file = new File( name );
		if( file.exists() && file.canRead() )
			return file.toURI();
		
		file = new File( getRunPath() + name );
		if( file.exists() && file.canRead() )
			return file.toURI();
		
		return null;
	}
}
