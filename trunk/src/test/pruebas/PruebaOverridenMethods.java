/* 
 * PruebaOverridenMethods.java
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
package pruebas;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.SortedSet;
import java.util.TreeSet;

import org.sam.odt_doclet.bindings.Adapter;

public class PruebaOverridenMethods{
	
	static interface Interface1{
		void m1();
	}

	static interface Interface2<S> extends Interface1{
		/* (non-Javadoc)
		 * @see pruebas.PruebaOverridenMethods.Interface1#m1()
		 */
		void m1();
		void m1( S s );
		void m2( S s );
	}
	
	static interface Interface3<T>{
		void m3();
		void m3( T t );
		void m3( T[] a );
		void m3( T t, T[] a );
	}
	
	static interface Interface4<U> extends Interface3<U>, Interface2<U>{
		/* (non-Javadoc)
		 * @see pruebas.PruebaOverridenMethods.Interface2#m1(java.lang.Object)
		 */
		void m1( U u );
		void m4( U u );
	}
	
	static abstract class Clase1<T> implements Interface4<T>{

		/* (non-Javadoc)
		 * @see pruebas.PruebaOverridenMethods.Interface2#m1()
		 */
		@Override
		public void m1(){}
		
		/* (non-Javadoc)
		 * @see pruebas.PruebaOverridenMethods.Interface3#m3()
		 */
		@Override
		public void m3(){}
		
		/* (non-Javadoc)
		 * @see pruebas.PruebaOverridenMethods.Interface3#m3(T[])
		 */
		@Override
		public void m3(T[] t){}
	}
	
	static abstract class Clase2<T extends Number> extends Clase1<T>{

		/* (non-Javadoc)
		 * @see pruebas.PruebaOverridenMethods.Clase1#m3()
		 */
		@Override
		public abstract void m3();

		/* (non-Javadoc)
		 * @see pruebas.PruebaOverridenMethods.Clase1#m3(T[])
		 */
		@Override
		public void m3( T[] t ){}

	}
	
	static class Clase3 extends Clase2<Integer>{

		/* (non-Javadoc)
		 * @see pruebas.PruebaOverridenMethods.Clase1#m1()
		 */
		@Override
		public void m1(){}
		
		/* (non-Javadoc)
		 * @see pruebas.PruebaOverridenMethods.Interface4#m1(java.lang.Object)
		 */
		@Override
		public void m1( Integer u ){}

		/* (non-Javadoc)
		 * @see pruebas.PruebaOverridenMethods.Interface2#m2(java.lang.Object)
		 */
		@Override
		public void m2( Integer t ){}
		
		/* (non-Javadoc)
		 * @see pruebas.PruebaOverridenMethods.Clase2#m3()
		 */
		@Override
		public void m3(){}
		
		/* (non-Javadoc)
		 * @see pruebas.PruebaOverridenMethods.Interface3#m3(java.lang.Object)
		 */
		@Override
		public void m3( Integer i ){}
		
		/* (non-Javadoc)
		 * @see pruebas.PruebaOverridenMethods.Clase2#m3(T[])
		 */
		@Override
		public void m3( Integer[] a ){}
		
		/* (non-Javadoc)
		 * @see pruebas.PruebaOverridenMethods.Interface3#m3(java.lang.Object, T[])
		 */
		@Override
		public void m3( Integer i, Integer[] a ){}
		
		/* (non-Javadoc)
		 * @see pruebas.PruebaOverridenMethods.Interface4#m4(java.lang.Object)
		 */
		@Override
		public void m4(Integer i){}
		
		public void m5(){}

		/* (non-Javadoc)
		 * @see java.lang.Object#clone()
		 */
		@Override
		public Clase3 clone(){
			return null;
		}

	}

	static Class<?> getClass( Type t ) throws ClassCastException{
		if( t instanceof Class<?> )
			return (Class<?>)t;
		if( t instanceof ParameterizedType ){
//			System.err.println( Adapter.toString( ( (ParameterizedType)t ).getActualTypeArguments() ) );
			return (Class<?>)( (ParameterizedType)t ).getRawType();
		}
		throw new ClassCastException();
	}
	
	private static boolean equals( Class<?> t1, Class<?> t2 ){
		if( t1 == t2 )
			return true;
		if( t1 == null )
			return false;
		if( t1.isArray() && t2.isArray() )
			return equals( t1.getComponentType(), t2.getComponentType() );
		return t1.equals( t2 );
	}
		
	private static boolean equals( ParameterizedType t1, ParameterizedType t2 ){
		if( t1 != null && t2 != null )
			return equals( t1.getRawType(), t2.getRawType() );
		return t1 == null && t2 == null;
	}
	
	private static boolean equals( GenericArrayType t1, GenericArrayType t2 ){
		if( t1 != null && t2 != null )
			return equals( t1.getGenericComponentType(), t2.getGenericComponentType() );
		return t1 == null && t2 == null;
	}
	
	private static boolean equals( Class<?> t1, ParameterizedType t2 ){
		return equals( t1, t2.getRawType() );
	}
	
	private static boolean equals( Class<?> t1, GenericArrayType t2 ){
		if( t1 != null && t2 != null ){
			if( !t1.isArray() )
				return false;
			return equals( t1.getComponentType(), t2.getGenericComponentType() );
		}
		return t1 == null && t2 == null;
	}
	
	private static boolean equals( Class<?> t1, Type t2 ){
		if( t2 instanceof ParameterizedType )
			return equals( t1, (ParameterizedType)t2 );
		if( t2 instanceof GenericArrayType )
			return equals( t1, (GenericArrayType)t2 );
		return false;
	}
	
	static boolean equals( Type t1, Type t2 ){
		if( t1 == t2 )
			return true;
		if( t1 == null )
			return false;
		
		if( t1 instanceof Class<?> || t2 instanceof Class<?> ){
			if( t1 instanceof Class<?> && t2 instanceof Class<?> )
				return equals( (Class<?>)t1, (Class<?>)t2 );
			if( t1 instanceof Class<?> )
				return equals( (Class<?>)t1, t2 );
			return equals( (Class<?>)t2, t1 );
		}
		if( t1 instanceof ParameterizedType && t2 instanceof ParameterizedType )
			return equals( (ParameterizedType)t1, (ParameterizedType)t2 );
		if( t1 instanceof GenericArrayType && t2 instanceof GenericArrayType )
			return equals( (GenericArrayType)t1, (GenericArrayType)t2 );
		return false;
	}
	
	static boolean equals( Type[] t1, Type[] t2 ){
		if( t1 == t2 )
			return true;
		if( t1 == null || t2 == null )
			return false;
		int length = t1.length;
		if( t2.length != length )
			return false;
		for( int i = 0; i < length; i++ ){
			if( !equals( t1[i], t2[i] ) )
				return false;
		}
		return true;
	}
	

	//*

	// Con generics

	private static boolean equivalents( Method first, Method second ){
		return  first.getName().equals( second.getName() ) && 
				equals( first.getGenericParameterTypes(), second.getGenericParameterTypes() );
	}
	
	static Collection<Type> getImplementedInterfaces( Class<?> clazz ){
		Deque<Type> interfaces = new ArrayDeque<Type>();
		while( clazz != null ){
			for( Type implementedInterface: clazz.getGenericInterfaces() )
				if( !interfaces.contains( implementedInterface ) )
					interfaces.offerLast( implementedInterface );
			 clazz = clazz.getSuperclass();
		}
		return interfaces;
	}

	static Collection<Type> getAllInterfaces( Class<?> clazz ){
		Collection<Type> interfaces = getImplementedInterfaces( clazz );
		Deque<Type> candidatos = new ArrayDeque<Type>();
		
		candidatos.addAll( interfaces );
		while( candidatos.peek() != null ){
			for( Type i: getClass( candidatos.poll() ).getGenericInterfaces() ){
				if( !interfaces.contains( i ) ){
					interfaces.add( i );
					candidatos.offerLast( i );
				}
			}
		}
		return interfaces;
	}
	
	static Method getOverrridesMethod( Method method ){
		Type t = method.getDeclaringClass().getGenericSuperclass();
		while( t != null ){
			Class<?> superClass = getClass( t );
			for( Method m: superClass.getDeclaredMethods() )
				if( equivalents( method, m ) )
					return m;
			t = superClass.getGenericSuperclass();
		}
		return null;
	}
	
	static Method getDeclaredMethod( Method method ){
		try{
			for( Type t: method.getDeclaringClass().getGenericInterfaces() ){
				for( Method m: getClass( t ).getDeclaredMethods() )
					if( equivalents( method, m ) )
						return m;
			}
			for( Type t: getAllInterfaces( method.getDeclaringClass() ) ){
				for( Method m: getClass( t ).getDeclaredMethods() )
					if( equivalents( method, m ) )
						return m;
			}
		}catch( ClassCastException e ){
			return null;
		}
		return null;
	}
	
	/*/
	
	// Sin generics
	
	private static boolean equivalents( Method first, Method second ){
		return  first.getName().equals( second.getName() ) && 
				equals( first.getParameterTypes(), second.getParameterTypes() );
	}
	
	static Collection<Class<?>> getImplementedInterfaces( Class<?> clazz ){
		Deque<Class<?>> interfaces = new ArrayDeque<Class<?>>();
		while( clazz != null ){
			for( Class<?> implementedInterface: clazz.getInterfaces() )
				if( !interfaces.contains( implementedInterface ) )
					interfaces.offerLast( implementedInterface );
			 clazz = clazz.getSuperclass();
		}
		return interfaces;
	}
	
	static Collection<Class<?>> getAllInterfaces( Class<?> clazz ){
		Collection<Class<?>> interfaces = getImplementedInterfaces( clazz );
		Deque<Class<?>> candidatos = new ArrayDeque<Class<?>>();
		
		candidatos.addAll( interfaces );
		while( candidatos.peek() != null ){
			for( Class<?> i: candidatos.poll().getInterfaces() ){
				if( !interfaces.contains( i ) ){
					interfaces.add( i );
					candidatos.offerLast( i );
				}
			}
		}
		return interfaces;
	}
	
	static Method getOverrridesMethod( Method method ){
		Class<?> superClass = method.getDeclaringClass().getSuperclass();
		while( superClass != null ){
			for( Method m: superClass.getDeclaredMethods() )
				if( equivalents( method, m ) )
					return m;
			superClass = superClass.getSuperclass();
		}
		return null;
	}
	
	static Method getDeclaredMethod( Method method ){
		for( Class<?> t: method.getDeclaringClass().getInterfaces() ){
			for( Method m: t.getDeclaredMethods() )
				if( equivalents( method, m ) )
					return m;
		}
		for( Class<?> t: getAllInterfaces( method.getDeclaringClass() ) ){
			for( Method m: t.getDeclaredMethods() )
				if( equivalents( method, m ) )
					return m;
		}
		return null;
	}
	//*/
	
	static Method getFirtsDeclaredMethod( Method method ){
		Method m1 = null, m2 = getDeclaredMethod( method );
		while( m2 != null ){
			m1 = m2;
			m2 = getDeclaredMethod( m2 );
		}
		return m1;
	}
	
	@Deprecated
	static Method getOverrridesMethod_old( Method method ){
		Class<?> declaringClass = method.getDeclaringClass();
		if( declaringClass.equals( Object.class ) ){
			return null;
		}
		try{
			Class<?> superClass = declaringClass.getSuperclass();
			if( superClass == null )
				return null;
			return superClass.getMethod( method.getName(), method.getParameterTypes() );
		}catch( NoSuchMethodException e ){
			return null;
		}
	}
	
	static String toString( Method m ){
		StringBuffer buff = new StringBuffer( m.getName() );
		buff.append( '(' );
		String paramaters = Adapter.toString( m.getGenericParameterTypes() );
		if( paramaters != null && paramaters.length() > 0 ){
			//buff.append( ' ' );
			buff.append( paramaters );
			//buff.append( ' ' );
		}
		buff.append( ')' );
		return buff.toString();
	}
	
	/**
	 * @param args
	 */
	public static void main( String[] args ){
		Class<?> classes[] = new Class<?>[]{
			Interface1.class,
			Interface2.class,
			Interface3.class,
			Interface4.class,
			Clase1.class,
			Clase2.class,
			Clase3.class			
		};
	
		final SortedSet<Method> methods = new TreeSet<Method>(
			new Comparator<Method>(){
				@Override
				public int compare( Method o1, Method o2 ){
					return PruebaOverridenMethods.toString( o1 ).compareTo( PruebaOverridenMethods.toString( o2 ) );
				}
			}
		);
		for( Class<?> clazz: classes ){
			System.out.print( Adapter.toString( clazz ) );
			TypeVariable<?>[] param = clazz.getTypeParameters();
			if( param != null && param.length > 0 )
				System.out.println( "<" + Adapter.toString( param ) + ">" );
			else
				System.out.println();	
			
			for( Type i : getAllInterfaces( clazz ) ){
				System.out.println(	"\t" + Adapter.toString( i ) );
			}
			methods.clear();
			for( Method method: clazz.getDeclaredMethods() ){
				if( !method.isSynthetic() ) 
					methods.add( method );
			}
			System.out.println( "Métodos:" );
			for( Method method: methods ){
				System.out.println(	"\t" + toString( method ) );
				Method m = getFirtsDeclaredMethod( method );
				if( m != null )
					System.out.println(	"\t\t" +( "Implements: " + Adapter.toString( m.getDeclaringClass() ) + "." + Adapter.toString( m ) ) );
				m = getOverrridesMethod( method );
				if( m != null )
					System.out.println(	"\t\t" +( "Overrides:  " + Adapter.toString( m.getDeclaringClass() ) + "." + Adapter.toString( m ) ) );
			}
		}
	}

}
