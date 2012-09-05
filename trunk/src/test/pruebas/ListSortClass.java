package pruebas;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;

import org.sam.odt_doclet.bindings.Adapter;

/**
 * Clase que muestra todas las clases accesibles de un paquete.<br/>
 */
public class ListSortClass {
	
	private static final Comparator<File> COMPARADOR_DE_FICHEROS = new Comparator<File>(){
		public int compare( File f1, File f2 ){
			try{
				return f1.getCanonicalPath().compareTo( f2.getCanonicalPath() );
			}catch( IOException e ){
				return 0;
			}
		}
	};
	
	private static final Comparator<Class<?>> COMPARADOR_DE_CLASES = new Comparator<Class<?>>() {
		public int compare( Class<?> e1, Class<?> e2 ){
			return Adapter.getSortingName( e1 ).compareTo( Adapter.getSortingName( e2 ) );
		}
	};
	
	static String getHierarchicalName( Class<?> clazz ){
		String name = clazz.getSimpleName();
		Class<?> superClass = clazz.getSuperclass();
		while( superClass != null && !superClass.equals( Object.class )
				&& clazz.getPackage().equals( superClass.getPackage() ) ){
			name = superClass.getSimpleName() + "\u25C1\u2500" + name;
			superClass = superClass.getSuperclass();
		}
		return name;
	}
	
	private static String getHierarchicalComposeName( Class<?> clazz ){
		String name = getComposeName( clazz );
		Class<?> superClass = clazz.getSuperclass();
		while( superClass != null && !superClass.equals( Object.class )
				&& clazz.getPackage().equals( superClass.getPackage() ) ){
			name = getComposeName( superClass ) + "\u25C1\u2500" + name;
			superClass = superClass.getSuperclass();
		}
		return name;
	}

	private static String getComposeName(Class<?> clazz){
		Package pack = clazz.getPackage();
		if( pack == null )
			return clazz.getCanonicalName();
		return clazz.getCanonicalName().substring( pack.getName().length() + 1 );
	}
	
	private static String getTabs( Class<?> clazz ){
		Class<?> enclosingClass = clazz.getEnclosingClass();
		int level = 0;
		while( enclosingClass != null ){
			enclosingClass = enclosingClass.getEnclosingClass();
			level++;
		}
		if( level == 0 )
			return "    ";
		String tabs = "    ";
		while( level > 1 ){
			tabs += "    ";
			level--;
		}
		return tabs + "  \u2295\u2500";
	}
	
	private static Collection<File> getPackages( File root ){
		SortedSet<File> listadoPackages = new TreeSet<File>( COMPARADOR_DE_FICHEROS );
		Queue<File> directorios = new LinkedList<File>();
		directorios.add( root );
		listadoPackages.add( root );
		while( !directorios.isEmpty() ){
			File directorioActual = directorios.poll();
			for( File archivo: directorioActual.listFiles() )
				if( !archivo.isHidden() && archivo.isDirectory() ){
					directorios.add( archivo );
					listadoPackages.add( archivo );
				}
		}
		return listadoPackages;
	}
	
	private static String getPackageName( File pack, String rootPath ) throws IOException{
		String absolutePath;
		absolutePath = pack.getCanonicalPath();
		if( absolutePath.length() == rootPath.length() )
			return "";
		return absolutePath.substring( rootPath.length() + 1 ).replace( '/', '.' );
	}
	
	private static void add( Class<?> clazz, Collection<Class<?>> classesSet ){
		classesSet.add( clazz );
		for( Class<?> inerClass: clazz.getDeclaredClasses() )
			classesSet.add( inerClass );
	}
	
	private static void getInterfacesAndClasses( File pack, String packageName, Collection<Class<?>> classesSet ){
		
		for( File archivo: pack.listFiles() ){
			if( !archivo.isHidden() && !archivo.isDirectory() ){
				String fileName = archivo.getName();
				if( fileName.endsWith( ".class" ) && fileName.indexOf( '$' ) < 0 && !fileName.contains( "package-info" ) ){
					String className = String.format( "%s%s",
							packageName.length() > 0 ? packageName + '.': "",
							fileName.substring( 0, fileName.length() - ".class".length() ) 
					);
					try{
						Class<?> clazz = Class.forName( className, false, ListSortClass.class.getClassLoader() );
						add( clazz, classesSet);
					}catch( ClassNotFoundException e ){
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * @param args ignorados
	 * @throws IOException 
	 */
	public static void main( String[] args ) throws IOException{
	
		File root = new File( "/data/Samuel/Proyectos/odt-doclet/bin" );
		String rootPath = root.getCanonicalPath();

		SortedSet<Class<?>> listadoDeClasesOrdenado = new TreeSet<Class<?>>( COMPARADOR_DE_CLASES );

		for( File pack: getPackages( root ) ){
			String packageName = getPackageName( pack, rootPath );
			getInterfacesAndClasses( pack, packageName, listadoDeClasesOrdenado );
		}
		Class<?> lastClazz= null;
		for(Class<?> clazz: listadoDeClasesOrdenado ){
			if( lastClazz == null || !lastClazz.getPackage().equals( clazz.getPackage() ))
				System.out.format( "package: %s\n", clazz.getPackage() != null ? clazz.getPackage().getName() : "(default  package)" );
			System.out.println( getTabs( clazz ) + getHierarchicalComposeName( clazz ) );
			lastClazz =clazz;
		}
	}
}
