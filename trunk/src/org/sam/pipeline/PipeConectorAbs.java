/* 
 * PipeConectorAbs.java
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
package org.sam.pipeline;

import java.io.IOException;
import java.io.OutputStream;

/**
 */
abstract class PipeConectorAbs implements PipeConnector{

	/**
	 */
	private static class SourceProcessor implements Runnable{

		OutputProcessor source;
		OutputStream out;
		
		SourceProcessor(){}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run(){
			try{
				source.process( out );
				out.flush();
				out.close();
			}catch( IOException e ){
				e.printStackTrace();
			}
		}
	}

	private final SourceProcessor sourceProcessor;
	
	/**
	 * 
	 */
	PipeConectorAbs(){
		sourceProcessor = new SourceProcessor();
	}

	/* (non-Javadoc)
	 * @see org.sam.pipeline.PipeConnector#setSource(org.sam.pipeline.OutputProcessor)
	 */
	@Override
	public final void setSource( OutputProcessor source ){
		this.sourceProcessor.source = source;
	}
	
	final void setSourceOutput( OutputStream out ){
		this.sourceProcessor.out = out;
	}
	
	private transient Thread sourceThread;
	
	final void processSource(){
		sourceThread = new Thread( sourceProcessor );
		sourceThread.start();
	}
	
	final void joinSource() throws InterruptedException{
		sourceThread.join();
	}
}