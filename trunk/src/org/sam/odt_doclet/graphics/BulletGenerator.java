package org.sam.odt_doclet.graphics;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.sam.odt_doclet.BigDecimalDimension;
import org.sam.odt_doclet.Loader;
import org.sam.pipeline.Filter;
import org.sam.pipeline.Pump;
import org.sam.xml.XMLWriter;

/**
 */
public final class BulletGenerator{
	
	public static enum Bullet{
		
		EnumConstant,
		
		PublicStaticField,
		ProtectedStaticField,
		PackageStaticField,
		PrivateStaticField,
		
		PublicField,
		ProtectedField,
		PackageField,
		PrivateField,
		
		PublicConstructor,
		ProtectedConstructor,
		PackageConstructor,
		PrivateConstructor,
		
		PublicStaticMethod,
		ProtectedStaticMethod,
		PackageStaticMethod,
		PrivateStaticMethod,
		
		PublicMethod,
		ProtectedMethod,
		PackageMethod,
		PrivateMethod,
		
		PublicAbstractMethod,
		ProtectedAbstractMethod,
		PackageAbstractMethod,
		
		Transient,
		Volatile,
		Native,
		
		Strictfp,
		Final,
		Synchronized
		
	}
	
	private static class BulletProcessor implements Pump<Bullet>{
		
		private Bullet bullet;
		
		Dimension dim;
		
		BulletProcessor(){
			this.dim = new Dimension( 64, 64 );
		}
		
		/* (non-Javadoc)
		 * @see org.sam.pipeline.Pump#setSource(java.lang.Object)
		 */
		@Override
		public void setSource( Bullet bullet ){
			this.bullet = bullet;
		}
		
		/* (non-Javadoc)
		 * @see org.sam.pipeline.OutputProcessor#process(java.io.OutputStream)
		 */
		@Override
		public void process( OutputStream out ) throws IOException{
			XMLWriter writer = new XMLWriter( out );
			writer.insert( "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" );
			writer.openNode( "svg" );
				writer.addAttribute( "xmlns", "http://www.w3.org/2000/svg" );
				writer.addAttribute( "xmlns:svg", "http://www.w3.org/2000/svg" );
				writer.addAttribute( "xmlns:xlink", "http://www.w3.org/1999/xlink" );
				writer.addAttribute( "version", "1.1" );

				writer.addAttribute( "width", dim.getWidth() );
				writer.addAttribute( "height",  dim.getHeight() );
				writer.addAttribute( "viewBox", "-2.5 -2.5 21 21" ); // 16 x 16 + 3.5 de margen
				
				writer.openNode( "use" );
					writer.addAttribute( "x", 0 );
					writer.addAttribute( "y", 0 );
					writer.addAttribute( "xlink:href", "shared/defs.svg#"+bullet );
				writer.closeNode();
				
			writer.closeNode();
		}
	}

	private final BulletProcessor processor;
	private final Filter          SVGtoPNG;

	public BulletGenerator(){
		processor = new BulletProcessor();
		SVGtoPNG  = new SVGtoPNG( new File( Loader.getRunPath() + "resources" ).toURI() );
		SVGtoPNG.setSource( processor );
	}
	
	public void setDimension( Dimension dim ){
		this.processor.dim = dim;
	}

	/**
	 * @param bullet
	 * @param out
	 * @return
	 * @throws IOException
	 */
	public void write( Bullet bullet, OutputStream out ) throws IOException{
		processor.setSource( bullet );
		toPNG.process( out );
	}
	
	public static void main( String... args ) throws FileNotFoundException, IOException{
		
		BulletGenerator generator = new BulletGenerator();
		BigDecimalDimension cmDim = new BigDecimalDimension( "1", "1" );
		System.out.println( "\t[ " + cmDim.width + " x " + cmDim.height + " ]" );
		generator.setDimension( cmDim.CentimetersToPixels( 300 ) );

		for( Bullet bullet: Bullet.values() ){
			System.out.println( bullet );
			generator.write( bullet, new FileOutputStream( "output/" + bullet + ".png" ) );
		}
	}
}
