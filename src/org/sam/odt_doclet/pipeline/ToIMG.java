package org.sam.odt_doclet.pipeline;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.sam.pipeline.FilterException;
import org.sam.pipeline.SinkAbs;

/**
 */
class ToIMG extends SinkAbs<BufferedImage>{

	private BufferedImage destination;

	/*
	 * (non-Javadoc)
	 * @see org.sam.pipeline.Sink#process(java.io.InputStream)
	 */
	@Override
	public void process( InputStream in ) throws IOException, FilterException{
		destination = ImageIO.read( in );
	}

	/* (non-Javadoc)
	 * @see org.sam.pipeline.Sink#getDestination()
	 */
	@Override
	public BufferedImage getDestination(){
		return destination;
	}
}