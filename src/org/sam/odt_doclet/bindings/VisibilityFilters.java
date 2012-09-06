/* 
 * VisibilityFilters.java
 * 
 * Copyright (c) 2012 Samuel Alfaro Jiménez <samuelalfaro at gmail dot com>.
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
package org.sam.odt_doclet.bindings;

import org.sam.odt_doclet.Visibility;

public class VisibilityFilters{

	public static final Filter<FieldBinding> FieldFilter = new Filter<FieldBinding>(){
		@Override
		public boolean validate( FieldBinding t ){
			return t.visibility.compareTo( v ) <= 0;
		}
	};

	public static final Filter<CommandBinding> CommandFilter = new Filter<CommandBinding>(){
		@Override
		public boolean validate( CommandBinding t ){
			return t.visibility.compareTo( v ) <= 0;
		}
	};

	static Visibility v = Visibility.Package;

	public static void setVisibility( Visibility v ){
		VisibilityFilters.v = v;
	}

	private VisibilityFilters(){
	}
}