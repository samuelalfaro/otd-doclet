function pack(){

	function getMaxTextWidth( item, checkX ){
		var textos = item.getElementsByTagName( "text" ),
			max = 0;
		for (var i = 0; i < textos.length; i++ ) {
			var width = textos.item(i).getBBox().width;
			if( checkX )
				width += parseFloat( textos.item(i).getAttribute("x") );
			if( width > max )
				max = width;
		}
		return max;
	}
	
	function parseFloatBetween( str, char1, char2 ){
		var sub = str.substring( str.indexOf(char1) + 1 );
		return parseFloat( sub.substring(0, sub.indexOf(char2) ) );
	}
	
	function remainder( str, char1, char2 ){
		var sub = str.substring( str.indexOf(char1) + 1 );
		return sub.substring( sub.indexOf(char2) );
	}
	
	function fitBox( box, width, height ){
		var paths = box.childNodes;
		for (var j = 0; j < paths.length; j++ ) {
			var item = paths.item(j);
			if( item instanceof Element && item.tagName == "path" ){
				var d0 = item.getAttribute("d"), d1,
					className = "" + item.getAttribute("class");
				if( className.indexOf("titulo") >= 0 ){
					//<path d="M0,0 v-18 a{r},{r} 0 0,1 {r},-{r} h{width-2r} a{r},{r} 0 0,1 {r},{r} v18" />
					var r = Math.round( parseFloatBetween( d0, "a", "," ) );
					d1 = d0.substring( 0, d0.indexOf("h") + 1 ) + ( width - 2*r ) + remainder( d0, "h", " ");
				}else if( className.indexOf("contenido") >= 0 ){
					var r = Math.round( parseFloatBetween( d0, "a", "," ) );
					if( height > 0) {
						//<path d="M0,0 v{height-r} a{r},{r} 0 0,0 {r},{r} h{width-2r} a{r},{r} 0 0,0 {r},-{r} v-{height-r} z" />
						var aux = d0;
						d1 = aux.substring( 0, aux.indexOf("v") + 1 ) + ( height - r);
						aux = remainder( aux, "v", " ");
						d1 += aux.substring( 0, aux.indexOf("h") + 1 ) + ( width - 2*r );
						aux = remainder( aux, "h", " ");
						d1 += aux.substring( 0, aux.indexOf("v") + 1 ) + ( r - height) + remainder( aux, "v", " ");
					}else{
						d1 = d0.substring( 0, d0.indexOf("h") + 1 ) + ( width - 2*r ) + remainder( d0, "h", " ");
					}
				}else{
					// lines <path d="M0,{y} h{width}" />
					d1 = d0.substring(0, d0.indexOf("h") + 1 ) + width;
				}
				item.setAttribute("d", d1 );
			}
		}
	}
	
	function translateX( group, offsetX ){
		var t0 = group.getAttribute("transform"),
			t1 = t0.substring(0, t0.indexOf("(") + 1 ) + offsetX + remainder( t0, "(", ",");
		group.setAttribute("transform", t1);
	}

	function fitParameters( grupo, width, height, padding ){
		var wp = Math.round( getMaxTextWidth( grupo, false ) + padding * 2 ),
			nodes = grupo.childNodes;
		translateX( grupo, width - wp + 15 );
		for (var i = 0; i < nodes.length; i++ ) {
			if( nodes.item(i) instanceof Element ){
				var node = nodes.item(i);
				if( node.tagName == "path" ){
					var d0 = node.getAttribute("d"),
						d1 = d0.substring(0, d0.indexOf("h") + 1 ) + wp + remainder( d0, "h", " ");
					node.setAttribute("d", d1 );
				}else if(node.tagName == "text"){
					node.setAttribute("x", wp / 2 );
				}
			}
		}
	}
	
	function fitTitledBox( box, width, height, padding ){
		var w = Math.round( getMaxTextWidth( box, false ) + padding * 2 );
		if( w > width)
			width = w;
		var nodes = box.childNodes;
		for (var i = 0; i < nodes.length; i++ ) {
			if( nodes.item(i) instanceof Element ){
				var node = nodes.item(i);
				if( node.tagName == "g" ){
					var className = "" + node.getAttribute("class");
					if( className == "box")
						fitBox( node, width, height);
					if( className == "parameters")
						fitParameters( node, width, height, padding );
				}else if(node.tagName == "text"){
					node.setAttribute("x", width / 2 );
				}
			}
		}
		return width;
	}
	
	function fitEnclosingClasses( padding ){
		var enclosingClasses = document.getElementById( "EnclosingClasses" );
		if( enclosingClasses == null )
			return 0;
		var nodes = enclosingClasses.childNodes,
			xOffset = 0;
		for (var i = 0; i < nodes.length; i++ ) {
			if( nodes.item(i) instanceof Element ){
				var node = nodes.item(i);
				if( node.tagName == "g" ){
					translateX( node, xOffset );
					xOffset += fitTitledBox( node, 100, -1, padding );
				}else if( node.tagName == "path" ){
					var d0 = node.getAttribute("d"),
						d1 = "M" + xOffset + d0.substring( d0.indexOf(",") );
					node.setAttribute("d", d1 );
					xOffset += Math.round( parseFloat( d0.substring( d0.indexOf("h") + 1 ) ) );
				}
			}
		} 
		return xOffset;
	}
	
	function findTitledBox( node ){
		var groups = node.getElementsByTagName("g");
		for (var i = 0; i < groups.length; i++ ) {
			className = "" + groups.item(i).getAttribute("class");
			if( className == "titledBox" )
				return groups.item(i);
		}
		return null;
	}
	
	function fitHierarchy( padding ){
		var hierarchy = document.getElementById( "Hierarchy" );
		if( hierarchy != null ){
			translateX( hierarchy, xOffset );

			var wh = Math.round( getMaxTextWidth( hierarchy, false ) + padding * 2 ),
				groups = hierarchy.childNodes;
			if( wh < 100 )
				wh = 100;
			for (var i = 0; i < groups.length; i++ ) {
				var group = groups.item(i);
				if( group instanceof Element &&  group.tagName == "g" ){
					var titledBox = findTitledBox(group)
					if( titledBox != null )
						fitTitledBox( titledBox, wh, -1, padding );
				}
			}
		}
	}
	
	function fitMainBox( padding ){
		var miembros = document.getElementById( "miembros" ),
			width  = Math.round( getMaxTextWidth( miembros, true ) + padding + 5 ),
			titledBox = findTitledBox( document.getElementById( "Main" ) );
			
		return fitTitledBox( titledBox, width, -1, padding );
	}
	
	function fitImage( img, border ){
		
		function getScale( viewBox, width, height ){
			if( "" + viewBox == "" )
				return 1.0;
			viewBoxArray = viewBox.split(" ");
			
			width += "";
			if( width != "" )
				return parseFloat(width) / ( parseFloat(viewBoxArray[2]) - parseFloat(viewBoxArray[0]) );
				
			height += "";
			if( height != "" )
				return parseFloat(height) / ( parseFloat(viewBoxArray[3]) - parseFloat(viewBoxArray[1]) );
				
			return 1.0;
		}
		
		var background = document.getElementById( "background" );
		if( background != null )
			background.setAttribute( "d", "M" + border + "," + border + " v0 h0 v0 z" );
		
		var bbox = img.getBBox(),
			width = bbox.width + 2 * border,
			height = bbox.height + 2 * border,
			scale = getScale( img.getAttribute("viewBox"), img.getAttribute( "width" ), img.getAttribute( "height" ) );
			
		if( background != null )
			background.setAttribute( "d", "M0,0 v" + height + " h" + width +" v-" + height + " z" );
		
		img.setAttribute( "width", Math.round( width * scale ) );
		img.setAttribute( "height", Math.round( height * scale ) );
		img.setAttribute( "viewBox", "0 0 " + width + " " + height );
	}
	
	var xOffset = fitEnclosingClasses( 10 );
	if( xOffset > 0 ){
		xOffset += 10;
		var hierarchy = document.getElementById( "Hierarchy" );
		if( hierarchy != null )
			translateX( hierarchy, xOffset );
		translateX( document.getElementById( "Main" ), xOffset );
	}else{
		var t = document.getElementById( "Main" ).getAttribute("transform");
		xOffset = Math.round( parseFloatBetween( t, "(", "," ) );
	}
	fitHierarchy( 10 );
	xOffset += fitMainBox( 10 );
	var interfaces = document.getElementById( "Interfaces" );
	if( interfaces != null )
		translateX( interfaces, xOffset );
	
	fitImage( document.documentElement, 10 );
}