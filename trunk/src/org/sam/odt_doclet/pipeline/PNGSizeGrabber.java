package org.sam.odt_doclet.pipeline;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 */
final class PNGSizeGrabber extends OutputStream{

	private final OutputStream out;

	private final byte[] widthBytes;
	private final byte[] heightBytes;

	private int writtenBytes;

	PNGSizeGrabber( OutputStream out ){
		this.out = out;
		widthBytes = new byte[4];
		heightBytes = new byte[4];
		writtenBytes = 0;
	}

	private static int toInt( byte[] bytes ){
		return ( ( bytes[0] << 24 ) & 0xFF000000 ) | ( ( bytes[1] << 16 ) & 0x00FF0000 )
				| ( ( bytes[2] << 8 ) & 0x0000FF00 ) | ( ( bytes[3] ) & 0x000000FF );
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write( int b ) throws IOException{
		out.write( b );
		// cabecera PNG: 0:[ ... ]16:[ whidth ]20:[ height ]24:[ ...
		if( writtenBytes < 24 ){
			if( writtenBytes >= 16 ){
				if( writtenBytes < 20 )
					widthBytes[writtenBytes - 16] = (byte)b;
				else
					heightBytes[writtenBytes - 20] = (byte)b;
			}
			writtenBytes++;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	@Override
	public void write( byte bytes[], int off, int len ) throws IOException{
		out.write( bytes, off, len );
		// cabecera PNG: 0:[ ... ]16:[ whidth ]20:[ height ]24:[ ... 
		int i = 0;
		while( writtenBytes < 24 && i < len ){
			if( writtenBytes >= 16 ){
				if( writtenBytes < 20 )
					widthBytes[writtenBytes - 16] = bytes[i + off];
				else
					heightBytes[writtenBytes - 20] = bytes[i + off];
			}
			writtenBytes++;
			i++;
		}
	}

	int getWidth(){
		return toInt( widthBytes );
	}

	int getHeight(){
		return toInt( heightBytes );
	}
}