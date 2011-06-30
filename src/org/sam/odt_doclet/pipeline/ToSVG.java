package org.sam.odt_doclet.pipeline;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.sam.odt_doclet.Loader;
import org.sam.pipeline.FilterAbs;
import org.sam.pipeline.FilterException;
import org.sam.pipeline.OutputProcessor;

/**
 */
class ToSVG extends FilterAbs{

	private final Transformer transformer;

	/**
	 * @param source
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerConfigurationException
	 */
	ToSVG( OutputProcessor source ) throws IOException, FileNotFoundException, TransformerFactoryConfigurationError,
			TransformerConfigurationException{
		super( source );

		transformer = TransformerFactory.newInstance().newTransformer(
				Loader.getResourceAsStreamSource( "resources/shared/toSVG.xsl" ) );
		transformer.setParameter( "widthChar1", 6.6 );
		transformer.setParameter( "widthChar2", 9.0 );
	}

	public void setScale( double scale ){
		transformer.setParameter( "scale", scale );
	}

	public void setBackground( String background ){
		transformer.setParameter( "background", background );
	}

	public void setBackground( int r, int g, int b ){
		setBackground( String.format( "#%X%X%X", r, g, b ) );
	}

	public void setBackground( Color color ){
		setBackground( color.getRed(), color.getGreen(), color.getBlue() );
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