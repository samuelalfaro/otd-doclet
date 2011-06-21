/* 
 * ClaseDePrueba.java
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
package pruebas;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Clase para tocar las narices y confundirla con java.lang.Float.
 */
class Float{
	
}

/**
 * Documentacion de la clase ClaseDePrueba.

 */
public class ClaseDePrueba<E> {
	
	/**
	 *  Documentacion de la clase InerClassAbs.
	 */
	private static abstract class InerClassAbs{
		/**
		 *  Documentacion de la clase InerInerClass.
		 */
		private static class InerInerClass {
			
		}
		
		/**
		 * Método abstrastracto metodo1.
		 * @param a parametro.
		 */
		abstract void metodo1(int a);
		
		/**
		
		 * @param t1
		 * @param t2
		
		 * @return bla bla bla bla */
		abstract <T> T metodo2(T t1, T t2);
	}
	
	/**
	 *  Documentacion de la clase InerClass.
	 */
	@SuppressWarnings({"unused","synthetic-access"})
	private class InerClass extends InerClassAbs{
		
		/**
		 * Documentación del constructor {@code InerClass(ClaseDePrueba<?> a1, boolean a2)}.
		
		 * @param a1 primer argumento.
		 * @param a2 segundo argumento.
		
		 * @throws IllegalArgumentException exepción lanzada cuando bla bla bla */
		<T> InerClass(ClaseDePrueba<T> a1, boolean a2) throws IllegalArgumentException{
		}
		
		/**
		 * Documentacion del constructor {@code InerClass()}.
		 * @throws IllegalArgumentException exepción lanzada cuando bla bla bla
		 * @throws IOException exepción IO bla bla bla
		 */
		InerClass() throws IllegalArgumentException, IOException{
			this(null,false);
		}
		
		/* (non-Javadoc)
		 * @see pruebas.ClaseDePrueba.InerClassAbs#metodo1(int)
		 */
		@Override
		void metodo1(int a){
			System.out.println(a);
		}
		
		/* (non-Javadoc)
		 * @see pruebas.ClaseDePrueba.InerClassAbs#metodo2(java.lang.Object, java.lang.Object)
		 */
		/**
		 * Method metodo2.
		 * @param t1 T
		 * @param t2 T
		 * @return T
		 */
		@Override
		<T> T metodo2(T t1, T t2) {
			return null;
		}
		
		/**
		 * Método que fuerza el acceso sintético.
		 */
		public void metodo3(){
			System.out.println(a1);
		}
	}
	
	/**
	 *  Documentacion de la enumeración DiasDeLaSemana.
	 */
	public static enum DiasDeLaSemana{
		/** Lunes
		 * @see bla bla bla bla
		 */
		Monday("you can fall apart"),
		/** Martes */
		Tuesday("break my heart"),
		/** Miércoles */
		Wednesday("break my heart"),
		/** Jueves */
		Thursday("doesn't even start"),
		/** Viernes */
		Friday("I'm in love"),
		/** Sábado */
		Saturday("wait"),
		/** Domingo */
		Sunday("always comes too late");

		private final String accion;
		
		/**
		 * Documentación del constructor.
		 * @param accion documentación del parámetro.
		 */
		DiasDeLaSemana(String accion){
			this.accion = accion;
		}
		/**
		 * Documentación del método {@code accion}.
		
		 * @return valor devuelto: */
		public final String getAccion(){
			return accion;
		}
	}
	
	/**
	 * Documentación de la enumeración {@code ColorPrimario}.
	 */
	public static enum ColorPrimario{
		/** {@code ColorPrimario} que representa el color: ROJO*/
		ROJO, 
		/** {@code ColorPrimario} que representa el color: VERDE*/
		VERDE,
		/** {@code ColorPrimario} que representa el color: AZUL*/
		AZUL;
	}
	
	/** Documentación de a1 */
	private int a1;
	/** Documentación de a2 */
	int[][] a2;
	/** Documentación de a3 */
	protected Map< Collection<? extends E>, Collection<? super E> >[] a3;
	/** Documentación de a4 */
	public InerClass a4[];
	
	/**
	 * Documentación del constructor {@link java.lang.Math#sqrt(double) Math.sqrt}.
	 * Con varias líneas de 
	 * comentarios. Bla bla bla {@value 4} bla {@linkplain www.wikipedia.es} bla {@code a, b, c}.
	 * @param parametro1 primer {@code a, b, c} argumento.<br/>
	 * sigue el comentario en otra lista {@linkplain #metodo2(pruebas.ClaseDePrueba.InerClassAbs.InerInerClass)}.
	 * @param parametro2 segundo argumento.
	 * @see #metodo1(int, float, Collection)
	 */
	private ClaseDePrueba(InerClass[] parametro1, int parametro2){
	}
	
	/**
	 * Documentación del método {@code metodo1}.
	 * @param a1 primer argumento.
	 * @param a2 segundo argumento.
	 * @param a3 tercer argumento.
	 * @return
	 * <ul><li>{@code true} si se cumple la condición.</li>
	 * <li>{@code false} en caso contrario.</li></ul> */
	public boolean metodo1(int a1, float a2, Collection<?> a3){
		return false;
	}
	
	/**
	 * Documentación del método {@code metodo2}.
	 * @param a primer argumento.
	
	 * @throws IllegalArgumentException causa de la excepción. */
	void metodo2(InerClassAbs.InerInerClass a) throws IllegalArgumentException{
	}
	
	/**
	 * Documentación del método {@code metodo3}.
	 * 
	 * @param <T> Documentación del parámetro genérico T del método.
	 * @param <U> Documentación del parámetro genérico U del método.
	 * @param a1 primer argumento.
	 * @param a2 segundo argumento.
	 * @param a3 tercer argumento.
	 * @return valor devuelto.
	 */
	private native <T extends Number & Cloneable, U> E metodo3(T a1, Map<? super Number, ? extends InerClass> a2, U... a3);
	
	/**
	 * Método con argumento de la clase java.lang.Float.
	 * @param f1 documentación del argumento.
	 */
	strictfp void metodo4(java.lang.Float f1) {
	}
	
	/**
	 * Método con argumento de la clase pruebas.Float.
	 * @param f2 documentación del argumento.
	 */
	protected final void metodo4(Float f2) {
	}
	
	/**
	 * @param f2
	 */
	public synchronized void metodo5(Float f2){
	}
}
