package org.sam.odt_doclet.pipeline;

import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.sam.odt_doclet.BigDecimalDimension;
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
		
		BulletProcessor(){}
		
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

				writer.addAttribute( "width", "64" );
				writer.addAttribute( "height", "64" );
				writer.addAttribute( "viewBox", "-2 -2 20 20" );
				
				writer.openNode( "use" );
					writer.addAttribute( "x", 0 );
					writer.addAttribute( "y", 0 );
					writer.addAttribute( "xlink:href", "shared/defs.svg#"+bullet );
				writer.closeNode();
				
			writer.closeNode();
		}
	}

	private final BulletProcessor processor;
	private final Filter          toPNG;

	public BulletGenerator(){
		processor = new BulletProcessor();
		toPNG     = new ToPNG();
		toPNG.setSource( processor );
	}

	/**
	 * @param bullet
	 * @param out
	 * @return
	 * @throws IOException
	 */
	public Dimension write( Bullet bullet, OutputStream out ) throws IOException{
		PNGSizeGrabber grabber = new PNGSizeGrabber( out );
		processor.setSource( bullet );
		toPNG.process( grabber );
		return new Dimension( grabber.getWidth(), grabber.getHeight() );
	}
	
	public static void main( String... args ) throws FileNotFoundException, IOException{
		
		BulletGenerator generator = new BulletGenerator();

		for( Bullet bullet: Bullet.values() ){
			System.out.print( bullet );
			Dimension d = generator.write( bullet, new FileOutputStream( "output/" + bullet + ".png" ) );
			BigDecimalDimension cmDim = BigDecimalDimension.toCentimeters( d, 300 );
			System.out.println( "\t[ " + cmDim.width + " x " + cmDim.height + " ]" );
		}
	}
}
