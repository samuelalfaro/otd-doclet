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
	static class SourceProcessor implements Runnable{

		private final OutputProcessor source;
		private OutputStream out;

		/**
		 * Constructor for RunnablePump.
		 * @param pump Pump
		 */
		SourceProcessor( OutputProcessor source ){
			this.source = source;
		}

		/**
		 * Method run.
		 * @see java.lang.Runnable#run()
		 */
		public void run(){
			try{
				source.process( out );
				out.flush();
				out.close();
			}catch( IOException e ){
				e.printStackTrace();
			}
		}

		/**
		 * Method setOutput.
		 * @param out OutputStream
		 */
		public void setOutput( OutputStream out ){
			this.out = out;
		}
	}

	SourceProcessor sourceProcessor;

	/**
	 * @param source
	 * @throws IOException
	 */
	PipeConectorAbs( OutputProcessor source ) throws IOException{
		setSource( source );
	}

	/* (non-Javadoc)
	 * @see org.sam.pipeline.PipeConnector#setSource(org.sam.pipeline.OutputProcessor)
	 */
	@Override
	public final void setSource( OutputProcessor source ) throws IOException{
		this.sourceProcessor = new SourceProcessor( source );
	}
}