/* 
 * Adapter.java
 * 
 * Copyright (c) 2010 Samuel Alfaro Jiménez <samuelalfaro at gmail dot com>.
 * All rights reserved.
 * 
 * This file is part of tips.
 * 
 * tips is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * tips is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with tips.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.odt_doclet.bindings;

import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Iterator;

import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

/**
 */
public final class Adapter {
	
	private Adapter(){}
	
	private static Class<?> getPrimaryComponentType( Class<?> clazz ){
		if( clazz.isArray() )
			return getPrimaryComponentType( clazz.getComponentType() );
		return clazz;
	}

	/**
	 * Method toString.
	 * @param params TypeVariable<?>[]
	 * @return String
	 */
	public static String toString( TypeVariable<?>[] params ){
		StringBuffer buff = new StringBuffer();
		for( int i = 0; i < params.length; ){
			buff.append( toString( params[i] ) );
			if( ++i < params.length )
				buff.append( ", " );
		}
		return buff.toString();
	}

	/**
	 * Method toString.
	 * @param type TypeVariable<?>
	 * @return String
	 */
	public static String toString( TypeVariable<?> type ){
		StringBuffer buff = new StringBuffer( type.getName() );
		Type[] bounds = type.getBounds();
		if( bounds != null && bounds.length > 0
				&& !( bounds[0] instanceof Class<?> && ( (Class<?>)bounds[0] ).equals( Object.class ) ) ){
			buff.append( " extends " );
			int i = 0;
			while( true ){
				buff.append( toString( bounds[i] ) );
				if( ++i == bounds.length )
					return buff.toString();
				buff.append( " & " );
			}
		}
		return buff.toString();
	}

	/**
	 * Method toString.
	 * @param params Type[]
	 * @return String
	 */
	public static String toString( Type[] params ){
		StringBuffer buff = new StringBuffer();
		for( int i = 0; i < params.length; ){
			buff.append( toString( params[i] ) );
			if( ++i < params.length )
				buff.append( ", " );
		}
		return buff.toString();
	}

	/**
	 * Method toString.
	 * @param type Type
	 * @return String
	 */
	public static String toString( Type type ){
		if( type instanceof Class<?> )
			return toString( (Class<?>)type );
		if( type instanceof ParameterizedType )
			return toString( (ParameterizedType)type );
		if( type instanceof GenericArrayType )
			return toString( ( (GenericArrayType)type ).getGenericComponentType() ) + "[]";
		if( type instanceof WildcardType )
			return toString( (WildcardType)type );
		// Cuando no se especifique explicitamente toString(? typeVariable)):
		// TypeVariable<?> tv = < T extends A & B & C >
		// Type ty = tv
		// toString(tv) --> T extends A & B & C
		// toString(ty) --> T
		if( type instanceof TypeVariable )
			return ( (TypeVariable<?>)type ).getName();

		assert ( false ): "Implementación desconcida";
		return type.toString();
	}

	/**
	 * Method toString.
	 * @param clazz Class<?>
	 * @return String
	 */
	public static String toString( Class<?> clazz ){
		Package pack = clazz.isArray() ? getPrimaryComponentType( clazz ).getPackage(): clazz.getPackage();
		if( pack == null )
			return clazz.getCanonicalName();
		return clazz.getCanonicalName().substring( pack.getName().length() + 1 );
	}

	/**
	 * Method toString.
	 * @param type ParameterizedType
	 * @return String
	 */
	public static String toString( ParameterizedType type ){
		return toString( (Class<?>)type.getRawType() ) + "<" + toString( type.getActualTypeArguments() ) + ">";
	}

	/**
	 * Method toString.
	 * @param type WildcardType
	 * @return String
	 */
	public static String toString( WildcardType type ){
		StringBuffer buff = new StringBuffer( "?" );
		Type[] bounds = type.getUpperBounds();
		if( bounds != null && bounds.length > 0
				&& !( bounds[0] instanceof Class<?> && ( (Class<?>)bounds[0] ).equals( Object.class ) ) ){
			buff.append( " extends " );
			int i = 0;
			while( true ){
				buff.append( toString( bounds[i] ) );
				if( ++i == bounds.length )
					return buff.toString();
				buff.append( ", " );
			}
		}
		bounds = type.getLowerBounds();
		if( bounds != null && bounds.length > 0 ){
			int i = 0;
			buff.append( " super " );
			while( true ){
				buff.append( toString( bounds[i] ) );
				if( ++i == bounds.length )
					return buff.toString();
				buff.append( ", " );
			}
		}
		return buff.toString();
	}

	private static String toString( String name, Class<?>[] params, int offset ){
		StringBuffer buff = new StringBuffer( name );
		buff.append( '(' );
		for( int i = offset; i < params.length; ){
			buff.append( params[i].getCanonicalName() );
			if( ++i < params.length )
				buff.append( ", " );
		}
		buff.append( ')' );
		return buff.toString();
	}

	private static String toString( String name, Parameter[] params ){
		StringBuffer buff = new StringBuffer( name );
		buff.append( '(' );
		for( int i = 0; i < params.length; ){
			com.sun.javadoc.Type t = params[i].type();
			buff.append( t.qualifiedTypeName() );
			buff.append( t.dimension() );
			if( ++i < params.length )
				buff.append( ", " );
		}
		buff.append( ')' );
		return buff.toString();
	}
	
	public static String toString( CommandBinding command ){
		StringBuffer buff = new StringBuffer( command.name );
		buff.append( '(' );
		if( command.params != null ){
			Iterator<ParameterBinding> it = command.params.iterator();
			while( it.hasNext() ){
				ParameterBinding p = it.next();
				buff.append( p.type );
				if( it.hasNext() )
					buff.append( ", " );
			}
		}
		buff.append( ')' );
		return buff.toString();
	}
	
	/**
	 * Method toString.
	 * @param constructor Constructor<?>
	 * @return String
	 */
	public static String toString( Constructor<?> constructor ){
		return toString( constructor.getDeclaringClass().getSimpleName(), constructor.getParameterTypes(), 0 );
	}
	
	/**
	 * Method toString.
	 * @param constructor Constructor<?>
	 * @param hideSyntheticAccesor boolean
	 * @return String
	 */
	public static String toString( Constructor<?> constructor, boolean hideSyntheticAccesor ){
		return toString(
				constructor.getDeclaringClass().getSimpleName(),
				constructor.getParameterTypes(),
				hideSyntheticAccesor ? constructor.getDeclaringClass().isEnum() ? 2 : 1: 0
		);
	}
	
	/**
	 * Method toString.
	 * @param constructor ConstructorDoc
	 * @return String
	 */
	public static String toString( ConstructorDoc constructor ){
		return toString( constructor.containingClass().simpleTypeName(), constructor.parameters() );
	}

	/**
	 * Method toString.
	 * @param method Method
	 * @return String
	 */
	public static String toString( Method method ){
		return toString( method.getName(), method.getParameterTypes(), 0 );
	}

	/**
	 * Method toString.
	 * @param method MethodDoc
	 * @return String
	 */
	public static String toString( MethodDoc method ){
		return toString( method.name(), method.parameters() );
	}

	private static String getComposeName( Class<?> clazz ){
		Package pack = clazz.getPackage();
		if( pack == null )
			return clazz.getCanonicalName();
		return clazz.getCanonicalName().substring( pack.getName().length() + 1 );
	}

	private static String getHierarchicalComposeName( Class<?> clazz ){
		String name = getComposeName( clazz );
		Class<?> superClass = clazz.getSuperclass();
		while( superClass != null && !superClass.equals( Object.class )
				&& clazz.getPackage().equals( superClass.getPackage() ) ){
			name = getComposeName( superClass ) + '<' + name;
			superClass = superClass.getSuperclass();
		}
		return name;
	}
	
	/**
	 * Método que genera una cadena de ordenación compuesta por:<br/>
	 * |Nombre de Paquete|Codigo (interface, clase, enum)|[Nombre jerarquico de la clases contenedoras]|Nombre jerarquico de la clase
	 * @param clazz
	 * @return
	 */
	public static String getSortingName( Class<?> clazz ){
		StringBuffer buff = new StringBuffer();
		Package pack = clazz.getPackage();
		if( pack != null ){
			buff.append( pack.getName() );
		}
		
		String name = getHierarchicalComposeName( clazz );
		
		Class<?> enclosingClass = clazz.getEnclosingClass();
		while( enclosingClass != null ){
			name = getHierarchicalComposeName( enclosingClass ) + ":" + name;
			if ( enclosingClass.getEnclosingClass() == null )
				clazz = enclosingClass;
			enclosingClass = enclosingClass.getEnclosingClass();
		}
		if( clazz.isInterface() )
			buff.append( "#0" );
		else if( Exception.class.isAssignableFrom( clazz ) )
			buff.append( "#1" );
		else if( clazz.isEnum() )
			buff.append( "#2" );
		else
			buff.append( "#3" );

		buff.append( name );
	
		return buff.toString();
	}
	
}
