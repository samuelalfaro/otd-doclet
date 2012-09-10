/* 
 * StringDigester.java
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
package org.sam.xml;

import java.io.IOException;

public interface StringDigester{

	StringDigester Default = new StringDigester(){
		public void digestString( String content, Appendable out ) throws IOException{
			out.append( content );
		}
	};

	StringDigester CDATA = new StringDigester(){
		public void digestString( String content, Appendable out ) throws IOException{
			out.append( "<![CDATA[" ).append( content ).append( "]]>" );
		}
	};

	StringDigester XMLCharsFilter = new StringDigester(){

		private final char[] charArray = new char[8192];

		public void digestString( String content, Appendable out ) throws IOException{

			int remainder = content.length();
			int srcBegin = 0;

			while( remainder > 0 ){
				int copiedChars = Math.min( remainder, charArray.length );
				content.getChars( srcBegin, srcBegin + copiedChars, charArray, 0 );
				for( int i = 0; i < copiedChars; i++ )
					switch( charArray[i] ){
					case '&':
						out.append( "&amp;" );
						break;
					case '<':
						out.append( "&lt;" );
						break;
					case '>':
						out.append( "&gt;" );
						break;
					case '\"':
						out.append( "&quot;" );
						break;
					case '\'':
						out.append( "&apos;" );
						break;
					default:
						out.append( charArray[i] );
					}
				remainder -= copiedChars;
			}
		}
	};

	void digestString( String content, Appendable out ) throws IOException;
}