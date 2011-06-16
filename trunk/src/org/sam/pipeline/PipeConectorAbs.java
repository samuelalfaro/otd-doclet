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
	static class RunnablePump implements Runnable{
		
		private final Pump pump;
		private OutputStream out;
		
		/**
		 * Constructor for RunnablePump.
		 * @param pump Pump
		 */
		RunnablePump(Pump pump){
			this.pump = pump;
		}
		
		/**
		 * Method run.
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			try {
				pump.process(out);
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Method setOutput.
		 * @param out OutputStream
		 */
		public void setOutput(OutputStream out){
			this.out = out;
		}
	}
	
	RunnablePump pump;
	
	/**
	 * Constructor for PipeConectorAbs.
	 * @param pump Pump
	 * @throws IOException
	 */
	PipeConectorAbs(Pump pump) throws IOException {
		setPump(pump);
	}
	
	/* (non-Javadoc)
	 * @see org.sam.pipeline.PipeConnector#setPump(org.sam.pipeline.Pump)
	 */
	@Override
	public final void setPump(Pump pump) throws IOException {
		this.pump = new RunnablePump(pump);
	}
}