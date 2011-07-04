/* 
 * ToBufferedImage.java
 * 
 * Copyright (c) 2011 Samuel Alfaro Jiménez <samuelalfaro at gmail dot com>.
 * All rights reserved.
 * 
 * This file is part of odt-doclet.
 * 
 * odt-doclet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * odt-doclet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with odt-doclet.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.odt_doclet.graphics;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URI;

import org.apache.batik.bridge.BaseScriptingEnvironment;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.svg12.SVG12BridgeContext;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.DocumentFactory;
import org.apache.batik.ext.awt.RenderingHintsKeyExt;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.fop.svg.SVGUserAgent;
import org.sam.pipeline.FilterException;
import org.sam.pipeline.SinkAbs;

final class SVGtoBufferedImage extends SinkAbs<BufferedImage>{

	private final String uri;
	private final DocumentFactory factory;
	private final UserAgent userAgent;
	
	private BufferedImage destination;
	
	SVGtoBufferedImage( URI uri ){
		this.uri = uri.toString();
		String parser = XMLResourceDescriptor.getXMLParserClassName();
		factory = new SAXSVGDocumentFactory( parser );
		userAgent = new SVGUserAgent( 1.0f, new AffineTransform() );
	}
	
	protected BridgeContext createBridgeContext( SVGOMDocument doc ){
		if( doc.isSVG12() )
			return new SVG12BridgeContext( userAgent );
		return new BridgeContext( userAgent );
	}

	/* (non-Javadoc)
	 * @see org.sam.pipeline.Sink#process(java.io.InputStream)
	 */
	@Override
	public void process( InputStream in ) throws IOException, FilterException{
		
		SVGOMDocument document = (SVGOMDocument)factory.createDocument(
				SVGConstants.SVG_NAMESPACE_URI,
				SVGConstants.SVG_SVG_TAG,
				uri,
				in
		);

		BridgeContext ctx = createBridgeContext( document );
		ctx.setDynamicState( BridgeContext.DYNAMIC );
		GraphicsNode gvtRoot = new GVTBuilder().build( ctx, document );
		try{
			if( ctx.isDynamic() ){
				BaseScriptingEnvironment se = new BaseScriptingEnvironment( ctx );
				se.loadScripts();
				se.dispatchSVGLoadEvent();
			}
			int width =  (int)( ctx.getDocumentSize().getWidth() + 0.5 );
	        int height = (int)( ctx.getDocumentSize().getHeight() + 0.5 );
	        destination = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
	        
	        Graphics2D gb = (Graphics2D)destination.getGraphics();
	        gb.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
	        gb.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
	        gb.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
	        gb.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON );
	        gb.setRenderingHint( RenderingHintsKeyExt.KEY_BUFFERED_IMAGE, new WeakReference<Image>(destination) );
	        
	        gvtRoot.paint( gb );
	        
		}catch( BridgeException ex ){
			throw new FilterException( "To BufferedImage Error!!!", ex );
		}
	}
	
	/* (non-Javadoc)
	 * @see org.sam.pipeline.Sink#getDestination()
	 */
	@Override
	public BufferedImage getDestination(){
		return destination;
	}
}