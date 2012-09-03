/* 
 * ODTDoclet.java
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

import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Queue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.transform.TransformerConfigurationException;

import org.sam.odt_doclet.UnitsDimension.Units;
import org.sam.odt_doclet.bindings.ClassBinding;
import org.sam.odt_doclet.bindings.ClassBindingFactory;
import org.sam.odt_doclet.bindings.Recorders;
import org.sam.odt_doclet.graphics.BulletGenerator;
import org.sam.odt_doclet.graphics.UMLDiagramGenerator;
import org.sam.xml.XMLConverter;
import org.sam.xml.XMLWriter;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.standard.Standard;

/**
 */
public final class ODTDoclet extends Standard{

	static final Charset UTF8 = Charset.forName( "UTF-8" );
	static final int dpi = 300;
	static final double scaleFactor = dpi / 120.0;

	private static class ManifestGenerator{

		private BufferedReader reader;
		private boolean havePicturesDir;
		private boolean addPicturesDir;
		private String breakLine;
		private final StringWriter newManifest;
		private final PrintWriter writer;

		ManifestGenerator( InputStream in ) throws IOException{
			reader = new BufferedReader( new StringReader( readOldManifest( in ) ) );
			newManifest = new StringWriter();
			writer = new PrintWriter( new BufferedWriter( newManifest ) );
			havePicturesDir = addPicturesDir = false;
			wirteBeginOldManifest();
		}

		private String readOldManifest( InputStream in ) throws IOException{
			StringBuffer oldManifest = new StringBuffer();
			byte[] buf = new byte[4096];
			int len;
			while( ( len = in.read( buf ) ) > 0 ){
				oldManifest.append( new String( buf, 0, len, UTF8 ) );
			}
			return oldManifest.toString();
		}

		private void wirteBeginOldManifest() throws IOException{
			while( true ){
				breakLine = reader.readLine();
				if( breakLine == null || breakLine.contains( "manifest:full-path=\"Pictures/\"" )
						|| breakLine.contains( "manifest:full-path=\"content.xml\"" )
						|| breakLine.contains( "</manifest:manifest>" ) ){
					if( breakLine.contains( "manifest:full-path=\"Pictures/\"" ) )
						havePicturesDir = true;
					break;
				}
				writer.println( breakLine );
			}
		}

		private void wirteEndOldManifest() throws IOException{
			if( addPicturesDir )
				addEntry( "", "Pictures/" );
			do{
				writer.println( breakLine );
				breakLine = reader.readLine();
			}while( breakLine != null );
			writer.flush();
		}

		void addEntry( String type, String path ){
			writer.format( " <manifest:file-entry manifest:media-type=\"%s\" manifest:full-path=\"%s\"/>\n", type, path );
		}

		void addImage( String name ){
			if( !havePicturesDir && !addPicturesDir )
				addPicturesDir = true;
			addEntry( "image/png", name );
		}

		byte[] getBytes() throws IOException{
			wirteEndOldManifest();
			return newManifest.toString().getBytes( UTF8 );
		}
	}

	/**
	 * @param plantilla
	 * @param resultFile
	 * @param root
	 * @throws IOException
	 * @throws TransformerConfigurationException 
	 */
	public static void generarODT( InputStream plantilla, File resultFile, RootDoc root ) throws IOException, TransformerConfigurationException{

		byte[] buf = new byte[4096];

		ZipInputStream zin = new ZipInputStream( new BufferedInputStream( plantilla ) );
		ZipOutputStream out = new ZipOutputStream( new BufferedOutputStream( new FileOutputStream( resultFile ) ) );

		ZipEntry entry = zin.getNextEntry();

		ManifestGenerator manifest = null;
		while( entry != null ){
			String name = entry.getName();
			if( name.equalsIgnoreCase( "META-INF/manifest.xml" ) ){
				manifest = new ManifestGenerator( zin );
			}else if( !name.equalsIgnoreCase( "content.xml" ) ){
				out.putNextEntry( new ZipEntry( name ) );
				int len;
				while( ( len = zin.read( buf ) ) > 0 ){
					out.write( buf, 0, len );
				}
			}
			entry = zin.getNextEntry();
		}
		zin.close();
		
		BulletGenerator bulletGenerator = new BulletGenerator();
		
		UnitsDimension ptDim = new UnitsDimension( "14", "14", Units.Points );
		bulletGenerator.setDimension( ptDim.toPixelsDimension( dpi ) );
		
		for( BulletGenerator.Bullet bullet: BulletGenerator.Bullet.values() ){
			String pictPath = "Pictures/" + bullet + ".png";
			System.out.println( bullet );
			out.putNextEntry( new ZipEntry( pictPath ) );
				manifest.addImage( pictPath );
				bulletGenerator.toPNG( bullet, out );
			out.closeEntry();
		}

		Queue<ClassBinding> classes = new LinkedList<ClassBinding>();
		UMLDiagramGenerator generator = new UMLDiagramGenerator();
		generator.setScale( scaleFactor );
		
		for( ClassDoc classDoc: root.classes() )
			try{
				ClassBinding clazz = ClassBindingFactory.createBinding( classDoc );
				
				String pictName = classDoc.qualifiedName();
				String pictPath = "Pictures/" + pictName + ".png";
				
				out.putNextEntry( new ZipEntry( pictPath ) );
					manifest.addImage( pictPath );
					Dimension dim = generator.toPNG( clazz, out );
				out.closeEntry();
				
				clazz.setGraphic( new Graphic( pictName, pictPath, dim, dpi ) );
				classes.offer( clazz );
			}catch( ClassNotFoundException e ){
				e.printStackTrace();
			}
			
		out.putNextEntry( new ZipEntry( "content.xml" ) );
		manifest.addEntry( "text/xml", "content.xml" );
		
		XMLWriter writer = new XMLWriter( out );
		XMLConverter converter = new XMLConverter();
		Recorders.register( Recorders.Mode.ODT, converter );
		converter.setWriter( writer );
		
		ODTHelper.beginDocumenContent( writer );
		for( ClassBinding clazz: classes )
			converter.write( clazz );

		ODTHelper.endDocumenContent( writer );

		out.putNextEntry( new ZipEntry( "META-INF/manifest.xml" ) );
		out.write( manifest.getBytes() );
		out.close();

	}

	/**
	 * Method optionLength.
	 * @param option String
	 * @return int
	 */
	public static int optionLength( String option ){
		return DocletValidator.optionLength( option );
	}

	/**
	 * Method validOptions.
	 * @param options String[][]
	 * @param reporter DocErrorReporter
	 * @return boolean
	 */
	public static boolean validOptions( String options[][], DocErrorReporter reporter ){
		return DocletValidator.validOptions( options, reporter ) || Standard.validOptions( options, reporter );
	}

	/**
	 * Method start.
	 * @param root RootDoc
	 * @return boolean
	 */
	public static boolean start( RootDoc root ){
		System.out.println("Generando documentanción...");
		try{
			File fileOutput = new File( "output/result.odt" );
			generarODT( Loader.getResourceAsStream( "resources/plantilla.odt" ), fileOutput, root );
			return true;
		}catch( IOException e ){
			e.printStackTrace();
			return false;
		}catch( TransformerConfigurationException e ){
			e.printStackTrace();
			return false;
		}
	}
}