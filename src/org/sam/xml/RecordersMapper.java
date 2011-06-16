/* 
 * RecordersMapper.java
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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 */
public final class RecordersMapper{

	private static final Comparator<Class<?>> comparator = new Comparator<Class<?>>(){
		@Override
		public int compare( Class<?> o1, Class<?> o2 ){
			return o1.hashCode() - o2.hashCode();
		}
	};

	private final Map<Class<?>, Recorder<?>> map;

	RecordersMapper(){
		this.map = new TreeMap<Class<?>, Recorder<?>>( comparator );
	}

	private static Type getActualTypeArgument( Object instance, Class<?> parameterizedType ){
		Class<?> clazz = instance.getClass();
		do{
			for( Type ci: clazz.getGenericInterfaces() )
				if( ci instanceof ParameterizedType ){
					ParameterizedType pci = (ParameterizedType)ci;
					if( pci.getRawType().equals( parameterizedType ) )
						return pci.getActualTypeArguments()[0];
				}
			clazz = clazz.getSuperclass();
		}while( clazz != null );
		return null;
	}

	/**
	 * Method putRecorder.
	 * @param recorder Recorder<?>
	 */
	void putRecorder( Recorder<?> recorder ){
		Type type = getActualTypeArgument( recorder, Recorder.class );
		Class<?> c = (Class<?>)( type instanceof Class ? type: ( (ParameterizedType)type ).getRawType() );
		map.put( c, recorder );
	}

	/**
	 * Method getRecorder.
	 * @param clazz Class<? extends T>
	 * @return Recorder<? super T>
	 * @throws RuntimeException
	 */
	@SuppressWarnings( "unchecked" )
	public <T>Recorder<? super T> getRecorder( Class<? extends T> clazz ) throws RuntimeException{

		Recorder<?> recorder;
		if( ( recorder = map.get( clazz ) ) != null )
			return (Recorder<? super T>)recorder;

		Class<?> c = clazz;
		while( true ){
			for( Type t: c.getInterfaces() )
				if( ( recorder = map.get( t ) ) != null )
					return (Recorder<? super T>)recorder;
			if( ( c = c.getSuperclass() ) == null )
				throw new RuntimeException( "Recorder no found for class: " + c );
			if( ( recorder = map.get( c ) ) != null )
				return (Recorder<? super T>)recorder;
		}
	}
}