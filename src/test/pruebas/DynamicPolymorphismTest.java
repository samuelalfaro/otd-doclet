// $codepro.audit.disable
package pruebas;

import java.lang.Float;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 */
interface I<T>{
	/**
	 * Method bar.
	 * @param t T
	 */
	void bar(T t);
}

/**
 */
public class DynamicPolymorphismTest {

	private static final Comparator<Class<?>> comparator = new Comparator<Class<?>>(){
		@Override
		public int compare(Class<?> o1, Class<?> o2) {
			return o1.hashCode() - o2.hashCode();
		}
	};
	
	final Map<Class<?>, I<?>> map;
	
	DynamicPolymorphismTest(){
		map = new TreeMap<Class<?>, I<?>>(comparator);
	}
	
	/**
	 * Method getActualTypeArgument.
	 * @param instance Object
	 * @param parameterizedType Class<?>
	 * @return Type
	 */
	Type getActualTypeArgument(Object instance, Class<?> parameterizedType){
		Class<?> clazz= instance.getClass();
		do{
			for( Type ci: clazz.getGenericInterfaces() )
				if(ci instanceof ParameterizedType){
					ParameterizedType pci = (ParameterizedType)ci;
					if( pci.getRawType().equals(parameterizedType) )
						return pci.getActualTypeArguments()[0];
				}
			clazz = clazz.getSuperclass();
		}while( clazz != null );
		return null;
	}
	
	/**
	 * Method put.
	 * @param i I<?>
	 */
	void put( I<?> i ){
		Type type = getActualTypeArgument(i, I.class);
		Class<?> c =  (Class<?>)( type instanceof Class ? type : ((ParameterizedType)type).getRawType() );
		map.put( c, i );
	}
	
	/**
	 * Method get.
	 * @param clazz Class<? extends T>
	 * @return I<? super T>
	 */
	@SuppressWarnings("unchecked")
	<T> I<? super T> get( Class<? extends T> clazz ){
		
		I<?> i;
		if( ( i = map.get(clazz) ) != null )
			return (I<? super T>)i;
		
		Class<?> c =  clazz;
		while( true ){
			for( Type t: c.getInterfaces() )
				if( ( i = map.get(t) ) != null )
					return (I<? super T>)i;
			if( ( c = c.getSuperclass() ) == null )
				return null;
			if( ( i = map.get(c) ) != null )
				return (I<? super T>)i;
		}
	}
	
	/**
	 * Method foo.
	 * @param t T
	 */
	<T> void foo(T t){
		Class<?> clazz = t.getClass();
		if( clazz.isArray() ){
			System.out.println("Array:");
			Class<?> componentType = t.getClass().getComponentType();
			if( componentType.isPrimitive() ){
				if(componentType.equals(Boolean.TYPE)){
					boolean ba[] = (boolean[])t;
					I<? super Boolean> i = get( Boolean.class );
					for(boolean b: ba)
						i.bar(b);
				} else if(componentType.equals(Character.TYPE)){
					char ca[] = (char[])t;
					I<? super Character> i = get( Character.class );
					for(char c: ca)
						i.bar(c);
				} else if(componentType.equals(Byte.TYPE)){
					byte ba[] = (byte[])t;
					I<? super Byte> i = get( Byte.class );
					for(byte b: ba)
						i.bar(b);
				}else if(componentType.equals(Short.TYPE)){
					short sa[] = (short[])t;
					I<? super Short> i = get( Short.class );
					for(short s: sa)
						i.bar(s);
				}else if(componentType.equals(Integer.TYPE)){
					int ia[] = (int[])t;
					I<? super Integer> i = get( Integer.class );
					for(int j: ia)
						i.bar(j);
				}else if(componentType.equals(Long.TYPE)){
					long la[] = (long[])t;
					I<? super Long> i = get( Long.class );
					for(long l: la)
						i.bar(l);
				}else if(componentType.equals(Float.TYPE)){
					float fa[] = (float[])t;
					I<? super Float> i = get( Float.class );
					for(float f: fa)
						i.bar(f);
				}else if(componentType.equals(Double.TYPE)){
					double da[] = (double[])t;
					I<? super Double> i = get( Double.class );
					for(double d: da)
						i.bar(d);
				}
			}else
				for(Object s: (Object[])t)
					foo(s);
		}else if(Iterable.class.isAssignableFrom(clazz)){
			System.out.println("Iterable:");
			for(Object s: (Iterable<?>)t)
				foo(s);
		}else
			get( clazz ).bar(t);
	}
	
	void prueba(){
		put( new I<Object>(){
			@Override
			public void bar(Object t) {
				System.out.println("Object:" + t);
			}
		});
		put( new I<Number>(){
			@Override
			public void bar(Number t) {
				System.out.println("Number:" + t);
			}
		});
		put( new I<Integer>(){
			@Override
			public void bar(Integer t) {
				System.out.println("Integer:" + t);
			}
		});
		put( new I<Float>(){
			@Override
			public void bar(Float t) {
				System.out.println("Float:" + t);
			}
		});
		put( new I<String>(){
			@Override
			public void bar(String t) {
				System.out.println("String:" + t);
			}
		});
		
		foo(new Byte((byte)10));
		foo((byte)10);
		foo(new Short((short)10));
		foo((short)10);
		foo(new Integer(10));
		foo(new Long(10));
		foo(10);
		foo(new Float(10));
		foo(new Double(10));
		foo(10.0);
		foo(10.0f);
		foo("10");
//		foo(this);
//		foo('c');
		foo( new float[][][]{{{1,2,3,4},{1},{5,6,7}}} );
		foo( new Object[]{1,"Hola",19.0f,'c'} );
		foo( Arrays.asList(new Object[]{1,"Hola",19.0,'c'}) );
	}
	
	/**
	 * Method main.
	 * @param args String[]
	 */
	public static void main(String... args){
		new DynamicPolymorphismTest().prueba();
	}
}
