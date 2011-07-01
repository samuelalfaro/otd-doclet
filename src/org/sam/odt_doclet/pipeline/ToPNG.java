package org.sam.odt_doclet.pipeline;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.sam.odt_doclet.Loader;
import org.sam.pipeline.FilterAbs;
import org.sam.pipeline.FilterException;

/**
 */
class ToPNG extends FilterAbs{

	private final ImageTranscoder transcoder;
	private final String uri;

	/**
	 * 
	 */
	ToPNG(){
		transcoder = new PNGTranscoder();
		transcoder.addTranscodingHint( SVGAbstractTranscoder.KEY_EXECUTE_ONLOAD, Boolean.TRUE );
		// FIXME Sacar fuera del constructor.
		uri = new File( Loader.getRunPath() + "resources" ).toURI().toString();
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
			throw new FilterException( "ToPNG Error!!!", e );
		}
	}
}