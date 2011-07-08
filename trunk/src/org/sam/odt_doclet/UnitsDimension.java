/* 
 * UnitsDimension.java
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

public final class UnitsDimension {
	
	static final MathContext mc = new MathContext( 3 );
	
	static final BigDecimal INCH_TO_CM = new BigDecimal( "2.54", mc );
	static final BigDecimal INCH_TO_MM = new BigDecimal( "25.4", mc );
	static final BigDecimal INCH_TO_PT = new BigDecimal( "72" );

	public static enum Units{
		
		Centimeters( new BigDecimal[]{
			BigDecimal.ONE,
			BigDecimal.TEN,
			BigDecimal.ONE.divide( INCH_TO_CM, mc ),
			INCH_TO_PT.divide( INCH_TO_CM, mc )
		} ),
		Milimeters( new BigDecimal[]{
			BigDecimal.ONE.divide( BigDecimal.TEN ),
			BigDecimal.ONE,
			BigDecimal.ONE.divide( INCH_TO_MM, mc ),
			INCH_TO_PT.divide( INCH_TO_MM, mc )
		} ),
		Inches( new BigDecimal[]{
			INCH_TO_CM,
			INCH_TO_MM,
			BigDecimal.ONE,
			INCH_TO_PT
		} ),
		Points( new BigDecimal[]{
			INCH_TO_CM.divide( INCH_TO_PT, mc ),
			INCH_TO_MM.divide( INCH_TO_PT, mc ),
			BigDecimal.ONE.divide( INCH_TO_PT, mc ),
			BigDecimal.ONE
		} );
		
		private final BigDecimal[] conversionFactors;
		
		private Units( BigDecimal[] conversionFactors ){
			this.conversionFactors = conversionFactors;
		}
		
		BigDecimal getConversionFactor( Units units ){
			return conversionFactors[units.ordinal()];
		}
	}

	public final BigDecimal width;
	public final BigDecimal height;
	public final Units units;
	
	private UnitsDimension( BigDecimal width, BigDecimal height, Units units ){
		this.width  = width;
		this.height = height;
		this.units = units;
	}
	
	public UnitsDimension( String width, String height, Units units){
		this( new BigDecimal( width, mc ), new BigDecimal( height, mc ), units );
	}
	
	public UnitsDimension( Dimension dim, int dpi, Units units ){
		BigDecimal factor  = Units.Inches.getConversionFactor( units );
		BigDecimal divisor = new BigDecimal( Integer.toString( dpi ) );
		this.width  = new BigDecimal( Integer.toString( dim.width ) ).multiply( factor ).divide( divisor, mc  );
		this.height = new BigDecimal( Integer.toString( dim.height ) ).multiply( factor ).divide( divisor, mc  );
		this.units = units;
	}

	public UnitsDimension to( Units units ){
		BigDecimal factor  = this.units.getConversionFactor( units );
		return new UnitsDimension( 
			this.width.multiply( factor ), this.height.multiply( factor ), units
		);
	}
	
	private static final BigDecimal round = new BigDecimal( "0.5", mc );
	
	public Dimension toPixelsDimension( int dpi ){
		BigDecimal multiplier = new BigDecimal( Integer.toString( dpi ) );
		BigDecimal factor  = Units.Inches.getConversionFactor( this.units );
				
		return new Dimension( 
			this.width.multiply( multiplier ).divide( factor, mc ).add( round ).intValue(),
			this.height.multiply( multiplier ).divide( factor, mc ).add( round ).intValue()
		);
	}
	
	// TODO Testear
}