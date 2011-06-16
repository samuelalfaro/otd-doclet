/* 
 * FilterAbs.java
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
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 */
public abstract class FilterAbs extends PipeConectorAbs implements Filter{
	
	/**
	 * Constructor for FilterAbs.
	 * @param pump Pump
	 * @throws IOException
	 */
	public FilterAbs(Pump pump) throws IOException{
		super(pump);
	}
	
	/* (non-Javadoc)
	 * @see org.sam.pipeline.Pump#process(java.io.OutputStream)
	 */
	@Override
	public final void process(OutputStream out) throws IOException{
		if(pump == null)
			throw new RuntimeException("Source is null");
		
		PipedOutputStream pipeOut = new PipedOutputStream();
		pump.setOutput(pipeOut);
		Thread sourceThread = new Thread(pump);
		
		PipedInputStream pipeIn;
		pipeIn = new PipedInputStream();
		pipeIn.connect(pipeOut);
		
		sourceThread.start();
		process(pipeIn, out);
		try {
			sourceThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}
}