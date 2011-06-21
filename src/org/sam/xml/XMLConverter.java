/* 
 * XMLConverter.java
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
package org.sam.xml;

import java.io.IOException;

/**
 * 
 */
public class XMLConverter{

	private final RecordersMapper mapper;
	private XMLWriter writer;

	public XMLConverter(){
		this.mapper = new RecordersMapper();
	}

	/**
	 * Method setWriter.
	 * @param writer XMLWriter
	 */
	public void setWriter( XMLWriter writer ){
		this.writer = writer;
	}

	/**
	 * Method register.
	 * @param recorder Recorder<T>
	 */
	public <T>void register( Recorder<T> recorder ){
		mapper.putRecorder( recorder );
	}

	/**
	 * Method write.
	 * @param t T
	 */
	public <T>void write( T t ) throws IOException{
		mapper.getRecorder( t.getClass() ).record( t, writer, mapper );
	}

}
