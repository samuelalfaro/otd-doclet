/* 
 * ODFDoclet.java
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
import java.util.Collection;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.sam.odt_doclet.bindings.ClassBinding;
import org.sam.odt_doclet.bindings.ClassBindingFactory;
import org.sam.odt_doclet.pipeline.PipeLine;
import org.sam.xml.XMLWriter;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.RootDoc;

/**
 */
public final class ODTDoclet{

	static final Charset UTF8 = Charset.forName( "UTF-8" );
	static final int dpi = 120;
	static final double scaleFactor = 1.0;

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

	private static class ContentGenerator{

		private static String readTemplate( InputStream in ) throws IOException{
			StringBuffer template = new StringBuffer();
			byte[] buf = new byte[4096];
			int len;
			while( ( len = in.read( buf ) ) > 0 ){
				template.append( new String( buf, 0, len, UTF8 ) );
			}
			return template.toString();
		}

		private final String template;
		private final VelocityContext context;
		private final Collection<Graphic> graphics;

		ContentGenerator() throws IOException{
			Velocity.init();
			template = readTemplate( Loader.getResourceAsStream( "/resources/toODT.vm" ) );
			context = new VelocityContext();
			graphics = new LinkedList<Graphic>();
			context.put( "graphics", graphics );
		}

		void addGraphic( String name, String path, Dimension dim, int dpi ){
			graphics.add( new Graphic( name, path, dim, dpi ) );
		}

		byte[] getBytes(){
			StringWriter writer = new StringWriter();
			Velocity.evaluate( context, writer, "toODT.vm", template );
			return writer.getBuffer().toString().getBytes( UTF8 );
		}
	}
	
	private void insertImage(XMLWriter writer) throws IOException{
		writer.openNode( "draw:frame" );
			writer.addAttribute( "draw:style-name", "G0" );
			writer.addAttribute( "draw:name", "$graphic.name" );
			writer.addAttribute( "text:anchor-type", "as-char" );
			writer.addAttribute( "svg:width", "${graphic.width}mm" );
			writer.addAttribute( "svg:height", "${graphic.height}mm" );
			writer.addAttribute( "draw:z-index", "0" );
			writer.openNode( "draw:image" );
				writer.addAttribute( "xlink:href", "$graphic.path" );
				writer.addAttribute( "xlink:type", "simple" );
				writer.addAttribute( "xlink:show", "embed" );
				writer.addAttribute( "xlink:actuate", "onLoad" );
			writer.closeNode();
		writer.closeNode();
	}

	private void beginDocumenContent( XMLWriter writer ) throws IOException{
		
		writer.openNode( "office:document-content ");
		writer.addAttribute( "xmlns:office", "urn:oasis:names:tc:opendocument:xmlns:office:1.0" );
		writer.addAttribute( "xmlns:style", "urn:oasis:names:tc:opendocument:xmlns:style:1.0" );
		writer.addAttribute( "xmlns:text", "urn:oasis:names:tc:opendocument:xmlns:text:1.0" );
		writer.addAttribute( "xmlns:table", "urn:oasis:names:tc:opendocument:xmlns:table:1.0" );
		writer.addAttribute( "xmlns:draw", "urn:oasis:names:tc:opendocument:xmlns:drawing:1.0" );
		writer.addAttribute( "xmlns:fo", "urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0" );
		writer.addAttribute( "xmlns:xlink", "http://www.w3.org/1999/xlink" );
		writer.addAttribute( "xmlns:dc", "http://purl.org/dc/elements/1.1/" );
		writer.addAttribute( "xmlns:meta", "urn:oasis:names:tc:opendocument:xmlns:meta:1.0" );
		writer.addAttribute( "xmlns:number", "urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0" );
		writer.addAttribute( "xmlns:svg", "urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0" );
		writer.addAttribute( "xmlns:chart", "urn:oasis:names:tc:opendocument:xmlns:chart:1.0" );
		writer.addAttribute( "xmlns:dr3d", "urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0" );
		writer.addAttribute( "xmlns:math", "http://www.w3.org/1998/Math/MathML" );
		writer.addAttribute( "xmlns:form", "urn:oasis:names:tc:opendocument:xmlns:form:1.0" );
		writer.addAttribute( "xmlns:script", "urn:oasis:names:tc:opendocument:xmlns:script:1.0" );
		writer.addAttribute( "xmlns:ooo", "http://openoffice.org/2004/office" );
		writer.addAttribute( "xmlns:ooow", "http://openoffice.org/2004/writer" );
		writer.addAttribute( "xmlns:oooc", "http://openoffice.org/2004/calc" );
		writer.addAttribute( "xmlns:dom", "http://www.w3.org/2001/xml-events" );
		writer.addAttribute( "xmlns:xforms", "http://www.w3.org/2002/xforms" );
		writer.addAttribute( "xmlns:xsd", "http://www.w3.org/2001/XMLSchema" );
		writer.addAttribute( "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance" );
		writer.addAttribute( "xmlns:rpt", "http://openoffice.org/2005/report" );
		writer.addAttribute( "xmlns:of", "urn:oasis:names:tc:opendocument:xmlns:of:1.2" );
		writer.addAttribute( "xmlns:xhtml", "http://www.w3.org/1999/xhtml" );
		writer.addAttribute( "xmlns:grddl", "http://www.w3.org/2003/g/data-view#" );
		writer.addAttribute( "xmlns:officeooo", "http://openoffice.org/2009/office" );
		writer.addAttribute( "xmlns:tableooo", "http://openoffice.org/2009/table" );
		writer.addAttribute( "xmlns:field", "urn:openoffice:names:experimental:ooo-ms-interop:xmlns:field:1.0" );
		writer.addAttribute( "xmlns:formx", "urn:openoffice:names:experimental:ooxml-odf-interop:xmlns:form:1.0" );
		writer.addAttribute( "xmlns:css3t", "http://www.w3.org/TR/css3-text/" );
		writer.addAttribute( "office:version", "1.2" );
		writer.addAttribute( "grddl:transformation", "http://docs.oasis-open.org/office/1.2/xslt/odf2rdf.xsl" );
		
		writer.openNode( "office:font-face-decls");
			writer.openNode( "style:font-face" );
				writer.addAttribute( "style:name", "Liberation Serif" );
				writer.addAttribute( "svg:font-family", "\'Liberation Serif\'" );
				writer.addAttribute( "style:font-family-generic", "roman" );
				writer.addAttribute( "style:font-pitch", "variable" );
			writer.closeNode();
			writer.openNode( "style:font-face" );
				writer.addAttribute( "style:name", "Nimbus Mono L" );
				writer.addAttribute( "svg:font-family", "\'Nimbus Mono L\'" );
				writer.addAttribute( "style:font-family-generic", "modern" );
				writer.addAttribute( "style:font-pitch", "fixed" );
			writer.closeNode();
		writer.closeNode();
		
		writer.openNode( "office:automatic-styles" );
			writer.openNode( "style:style" );
				writer.addAttribute( "style:name", "P0" );
				writer.addAttribute( "style:family", "paragraph" );
				writer.addAttribute( "style:parent-style-name", "Standard" );
			writer.closeNode();
			writer.openNode( "style:style" );
				writer.addAttribute( "style:name", "P1" );
				writer.addAttribute( "style:family", "paragraph" );
				writer.addAttribute( "style:parent-style-name", "Standard" );
				writer.openNode( "style:paragraph-properties" );
					writer.addAttribute( "fo:break-before", "page" );
				writer.closeNode();
			writer.closeNode();
		writer.closeNode();
	}
	
	
	/**
	 * @param plantilla
	 * @param resultFile
	 * @param root
	 * @throws IOException
	 */
	public static void generarODT( InputStream plantilla, File resultFile, RootDoc root ) throws IOException{

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

		ContentGenerator content = new ContentGenerator();

		// FIXME 
		/* Almacenar el classBinding junto a las dimensiones de la imagen generada
		 * Usar el writer, para insertar la imagen, y el título.
		 */
		ClassDoc[] classes = root.classes();
		for( ClassDoc classDoc: classes ){
			String pictName = classDoc.qualifiedName();
			String pictPath = "Pictures/" + pictName + ".png";
			manifest.addImage( pictPath );

			out.putNextEntry( new ZipEntry( pictPath ) );
			try{
				ClassBinding clazz = ClassBindingFactory.createBinding( classDoc );
				content.addGraphic( pictName, pictPath, PipeLine.toPNG( clazz, scaleFactor, out ), dpi );
			}catch( ClassNotFoundException e ){
				e.printStackTrace();
			}
			out.closeEntry();
		}

		out.putNextEntry( new ZipEntry( "content.xml" ) );
		manifest.addEntry( "text/xml", "content.xml" );
		out.write( content.getBytes() );

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
		return DocletValidator.validOptions( options, reporter );
	}

	/**
	 * Method start.
	 * @param root RootDoc
	 * @return boolean
	 */
	public static boolean start( RootDoc root ){
		try{
			File fileOutput = new File( "output/result.odt" );
			generarODT( Loader.getResourceAsStream( "resources/plantilla.odt" ), fileOutput, root );
			return true;
		}catch( IOException e ){
			e.printStackTrace();
			return false;
		}
	}
}