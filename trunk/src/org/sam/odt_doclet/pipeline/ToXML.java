package org.sam.odt_doclet.pipeline;

import java.io.IOException;
import java.io.OutputStream;

import org.sam.odt_doclet.bindings.ClassBinding;
import org.sam.odt_doclet.bindings.Recorders;
import org.sam.pipeline.Pump;
import org.sam.xml.XMLConverter;
import org.sam.xml.XMLWriter;

/**
 */
class ToXML implements Pump<ClassBinding>{

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