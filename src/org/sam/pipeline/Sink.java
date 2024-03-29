/* 
 * Sink.java
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
import java.io.InputStream;

/**
 * 
 */
public interface Sink<T> extends PipeConnector {
	
	/**
	 * Method process.
	 * @throws IOException
	 */
	void process() throws IOException;
	
	/**
	 * Method process.
	 * @param in InputStream
	 * @throws IOException
	 * @throws FilterException
	 */
	void process(InputStream in) throws IOException, FilterException;
	
	/**
	 * @return El objeto producto del proceso.
	 */
	T getDestination();
	
}
