/* 
 * BigDecimalDimension.java
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
import java.math.MathContext;

public class BigDecimalDimension {
	
	private static final MathContext mc = new MathContext( 3 );

	private static final BigDecimal INCH_TO_CM = new BigDecimal( "2.54", mc );
	private static final BigDecimal INCH_TO_MM = new BigDecimal( "25.4", mc );
	
	public final BigDecimal width;
	public final BigDecimal height;
	
	private BigDecimalDimension( BigDecimal width, BigDecimal height ){
		this.width  = width;
		this.height = height;
	}
	
	public static BigDecimalDimension toCentimeters( Dimension dim, int dpi ){
		BigDecimal divisor = new BigDecimal( Integer.toString( dpi ) );
		
		return new BigDecimalDimension( 
			new BigDecimal( Integer.toString( dim.width ) ).multiply( INCH_TO_CM ).divide( divisor, mc  ),
			new BigDecimal( Integer.toString( dim.height ) ).multiply( INCH_TO_CM ).divide( divisor, mc  )
		);
	}

	public static BigDecimalDimension toMilimeters( Dimension dim, int dpi ){
		BigDecimal divisor = new BigDecimal( Integer.toString( dpi ) );
		
		return new BigDecimalDimension( 
			new BigDecimal( Integer.toString( dim.width ) ).multiply( INCH_TO_MM ).divide( divisor, mc  ),
			new BigDecimal( Integer.toString( dim.height ) ).multiply( INCH_TO_MM ).divide( divisor, mc  )
		);
	}
	
	
	public static BigDecimalDimension toInches( Dimension dim, int dpi ){
		BigDecimal divisor = new BigDecimal( Integer.toString( dpi ) );
		
		return new BigDecimalDimension(
			new BigDecimal( Integer.toString( dim.width ) ).divide( divisor, mc  ),
			new BigDecimal( Integer.toString( dim.height ) ).divide( divisor, mc  )
		);
	}
}