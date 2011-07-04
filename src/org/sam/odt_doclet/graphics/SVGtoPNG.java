package org.sam.odt_doclet.graphics;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.sam.pipeline.FilterAbs;
import org.sam.pipeline.FilterException;

/**
 */
final class SVGtoPNG extends FilterAbs{
	
	private final String uri;
	private final ImageTranscoder transcoder;
	
	/**
	 * 
	 */
	SVGtoPNG( URI uri ){
		this.uri = uri.toString();
		transcoder = new PNGTranscoder();
		transcoder.addTranscodingHint( SVGAbstractTranscoder.KEY_EXECUTE_ONLOAD, Boolean.TRUE );
	}

	/*
	 * (non-Javadoc)
	 * @see org.sam.pipeline.Filter#process(java.io.InputStream, java.io.OutputStream)
	 */
	@Override
	public void process( InputStream in, OutputStream out ) throws IOException, FilterException{
		TranscoderInput input = new TranscoderInput( in );
		input.setURI( uri );
		TranscoderOutput output = new TranscoderOutput( out );
		try{
			transcoder.transcode( input, output );
		}catch( TranscoderException e ){
			throw new FilterException( "Image Transcoder Error!!!", e );
		}
	}
}