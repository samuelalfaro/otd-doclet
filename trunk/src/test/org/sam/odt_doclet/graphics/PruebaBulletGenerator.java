/* 
 * PruebaBulletGenerator.java
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.sam.odt_doclet.UnitsDimension;
import org.sam.odt_doclet.UnitsDimension.Units;
import org.sam.odt_doclet.graphics.BulletGenerator.Bullet;

/**
 * 
 */
public class PruebaBulletGenerator{
	
	public static void main( String... args ) throws FileNotFoundException, IOException{

		BulletGenerator generator = new BulletGenerator();
		UnitsDimension dim = new UnitsDimension( "1", ".75", Units.Inches );
		System.out.println( "\t[ " + dim.width + " x " + dim.height + " ]" );
		generator.setDimension( dim.toPixelsDimension( 150 ) );

		for( Bullet bullet: Bullet.values() ){
			System.out.println( bullet );
			generator.toPNG( bullet, new FileOutputStream( "output/" + bullet + ".png" ) );
		}
	}
}
