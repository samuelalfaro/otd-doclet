/* 
 * Graphic.java
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
package org.sam.odt_doclet;

import java.awt.Dimension;
import java.math.BigDecimal;

/**
 * 
 */
public class Graphic{
	
	final String name;
	final String path;
	final BigDecimalDimension dimension;
	
	Graphic( String name, String path, Dimension dimension, int dpi ){
		this.name = name;
		this.path = path;
		this.dimension = BigDecimalDimension.toMilimeters( dimension, dpi );
	}
	
	/**
	 * @return
	 */
	public final String getName(){
		return name;
	}
	
	/**
	 * @return
	 */
	public final String getPath(){
		return path;
	}
	
	/**
	 * @return
	 */
	public final BigDecimal getWidth(){
		return dimension.width;
	}
	
	/**
	 * @return
	 */
	public final BigDecimal getHeight(){
		return dimension.height;
	}
}