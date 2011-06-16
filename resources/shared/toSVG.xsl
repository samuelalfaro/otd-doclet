<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:exslt="http://exslt.org/common" xmlns="http://www.w3.org/2000/svg" xmlns:svg="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
	<xsl:import href="compact.xsl"/>
	
	<xsl:param name="scale">1.0</xsl:param>
	<xsl:param name="background">none</xsl:param>
	<xsl:param name="widthChar1">6.5</xsl:param>
	<xsl:param name="widthChar2">8.5</xsl:param>
	<xsl:param name="defs">shared/defs.svg#</xsl:param>
	<xsl:output method="xml" indent="no"/>
	<xsl:strip-space elements="*"/>

	<xsl:template match="/">
		<xsl:processing-instruction name="xml-stylesheet">type="text/css" href="shared/svg_styles.css"</xsl:processing-instruction>
		<xsl:apply-templates select="Class|Interface|Enum"/>
	</xsl:template>

	<xsl:template match="Class|Interface|Enum">
		<xsl:variable name="compactData">
			<xsl:call-template name="compact"/>
		</xsl:variable>
		<!--<xsl:copy-of select="exslt:node-set($compactData)"/>-->
		<xsl:apply-templates select="exslt:node-set($compactData)" mode="toSVG"/>
	</xsl:template>

	<xsl:template name="calculateWidthBox">
		<xsl:param name="node"/>
		<xsl:variable name="widthText" select="string-length($node/text())*$widthChar2+20"/>
		<xsl:choose>
			<xsl:when test='name($node)="Class"'>
				<xsl:choose>
					<xsl:when test='$widthText &gt; 80'><xsl:number value="$widthText"/></xsl:when>
					<xsl:otherwise>80</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test='name($node)="Interface"'>
				<xsl:choose>
					<xsl:when test='$widthText &gt; 120'><xsl:number value="$widthText"/></xsl:when>
					<xsl:otherwise>120</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test='$widthText &gt; 140'><xsl:number value="$widthText"/></xsl:when>
					<xsl:otherwise>140</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="calculate_xOffsetEnclosingClassesR">
		<xsl:param name="count"/>
		<xsl:param name="xOffsetEnclosingClasses"/>
		<xsl:choose>
			<xsl:when test='$count &lt; count(EnclosingClasses/*)'>
				<xsl:variable name="widthBox">
					<xsl:call-template name="calculateWidthBox">
						<xsl:with-param name="node" select="EnclosingClasses/*[$count+1]"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:call-template name="calculate_xOffsetEnclosingClassesR">
					<xsl:with-param name="count"  select="$count +1"/>
					<xsl:with-param name="xOffsetEnclosingClasses" select="$xOffsetEnclosingClasses + $widthBox + 40"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:number value="$xOffsetEnclosingClasses"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="calculate_xOffsetEnclosingClasses">
		<xsl:choose>
			<xsl:when test='EnclosingClasses'>
				<xsl:call-template name="calculate_xOffsetEnclosingClassesR">
					<xsl:with-param name="count"  select="0"/>
					<xsl:with-param name="xOffsetEnclosingClasses" select="0"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise><xsl:number value="0"/></xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="Class|Interface|Enum" mode="toSVG">
		<xsl:variable name="xOffsetEnclosingClasses">
			<xsl:call-template name="calculate_xOffsetEnclosingClasses"/>
		</xsl:variable>
		<xsl:variable name="xOffsetHierarchy">
			<xsl:choose>
				<xsl:when test='Hierarchy'>
					<xsl:choose>
						<xsl:when test='Hierarchy/Class[1]/text()="..."'><xsl:number value="( count(Hierarchy/Class) -1 )*40 -10"/></xsl:when>
						<xsl:otherwise><xsl:number value="( count(Hierarchy/Class) -1 )*40"/></xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="widthHierarchy">
			<xsl:choose>
				<xsl:when test='Hierarchy'>
					<xsl:for-each select="Hierarchy/Class">
						<xsl:sort select="string-length(.)" order="descending" data-type="number"/>
						<xsl:if test='position() = 1'>
							<xsl:number value="string-length(.) * $widthChar2 + 20"/>
						</xsl:if>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="widthName" select="string-length(@name) * $widthChar2 + 20"/>
		<xsl:variable name="numElements" select="count(Constants/Constant) + count(Fields/Field) + count(Constructors/Constructor) + count(Methods/Method)"/>
		<xsl:variable name="widthElements">
			<xsl:choose>
				<xsl:when test='$numElements &gt; 0'>
					<xsl:for-each select="Constants/Constant|Fields/Field|Constructors/Constructor|Methods/Method">
						<xsl:sort select="string-length(./text())" order="descending" data-type="number"/>
						<xsl:if test='position() = 1'>
							<xsl:number value="string-length(./text()) * $widthChar1 + 40"/>
						</xsl:if>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="widthParameters" select="string-length(Parameters) * $widthChar2 +20"/>
		<xsl:variable name="widthBox">
			<xsl:choose>
				<xsl:when test='$widthName &gt; $widthElements and $widthName &gt; $widthParameters and $widthName &gt; 140'>
					<xsl:number value="$widthName"/>
				</xsl:when>
				<xsl:when test='$widthElements &gt; $widthParameters and $widthElements &gt; 140'>
					<xsl:number value="$widthElements"/>
				</xsl:when>
				<xsl:when test='$widthParameters &gt; 140'>
					<xsl:number value="$widthParameters + 15"/>
				</xsl:when>
				<xsl:otherwise>140</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="widthInterfaces">
			<xsl:choose>
				<xsl:when test='Interfaces'>
					<xsl:for-each select="Interfaces/Interface">
						<xsl:sort select="string-length(.)" order="descending" data-type="number"/>
						<xsl:if test='position() = 1'>
							<xsl:number value="string-length(.) * $widthChar1 + 50"/>
						</xsl:if>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="xRemainderAux">
			<xsl:choose>
				<xsl:when test='$widthInterfaces &gt; 0'><xsl:number value="$widthInterfaces"/></xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test='Parameters'><xsl:number value="15"/></xsl:when>
						<xsl:otherwise>0</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="xRemainder">
			<xsl:choose>
				<xsl:when test='$widthHierarchy - $widthBox  &gt; $xRemainderAux'><xsl:number value="$widthHierarchy - $widthBox"/></xsl:when>
				<xsl:otherwise><xsl:number value="$xRemainderAux"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="xOffset">
			<xsl:choose>
				<xsl:when test='$xOffsetHierarchy  &gt; $xOffsetEnclosingClasses'><xsl:number value="$xOffsetHierarchy"/></xsl:when>
				<xsl:otherwise><xsl:number value="$xOffsetEnclosingClasses"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="widthArea" select="10 + $xOffset + $widthBox + $xRemainder + 10"/>

		<xsl:variable name="heightHierarchy">
			<xsl:choose>
				<xsl:when test='Hierarchy'>
					<xsl:choose>
						<xsl:when test='Hierarchy/Class[1]/text()="..."'><xsl:number value="count(Hierarchy/Class)*55"/></xsl:when>
						<xsl:otherwise><xsl:number value="count(Hierarchy/Class)*55 + 25"/></xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="heightTitle">
			<xsl:choose>
				<xsl:when test='name(.)="Class"'>25</xsl:when>
				<xsl:otherwise>45</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="heightPadding" select="5"/>
		<xsl:variable name="lineSpacing">
			<xsl:choose>
				<xsl:when test='name(.)="Enum"'>20</xsl:when>
				<xsl:otherwise>10</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="heightBox" select="2*$heightPadding + 20*$numElements + $lineSpacing"/>
		<xsl:variable name="heightInterfaces">
			<xsl:choose>
				<xsl:when test='Interfaces'><xsl:value-of select="( count(Interfaces/Interface) - 1) * 30 - 5"/></xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="yOffset">
			<xsl:choose>
				<xsl:when test='Hierarchy'><xsl:number value="$heightHierarchy + $heightTitle"/></xsl:when>
				<xsl:when test='Parameters'><xsl:number value="20 + $heightTitle +10"/></xsl:when>
				<xsl:when test='name(.)="Class" and (EnclosingClasses/Interface|EnclosingClasses/Enum)'><xsl:number value="20 + $heightTitle +10"/></xsl:when>
				<xsl:otherwise><xsl:number value="$heightTitle + 10"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="heightArea">
			<xsl:choose>
				<xsl:when test='$heightBox &gt; $heightInterfaces'>
					<xsl:number value="$yOffset + $heightBox + 10"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="$yOffset + $heightInterfaces + 10"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="estilo">
			<xsl:choose>
				<xsl:when test='name(.)="Class"'>estilo1</xsl:when>
				<xsl:when test='name(.)="Interface"'>estilo2</xsl:when>
				<xsl:otherwise>estilo3</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<svg>
			<xsl:attribute name="width"><xsl:number value="$widthArea * $scale"/></xsl:attribute>
			<xsl:attribute name="height"><xsl:number value="$heightArea * $scale"/></xsl:attribute>
			<xsl:attribute name="viewBox"><xsl:value-of select="concat('0 0 ', $widthArea, ' ', $heightArea)"/></xsl:attribute>
			<xsl:attribute name="onload">pack()</xsl:attribute>
			<xsl:attribute name="version">1.1</xsl:attribute>
			<script type="text/ecmascript" xlink:href="scripts/pack.es"/>

			<xsl:if test='$background !="none"'>
				<path id="background">
					<xsl:attribute name="style"><xsl:value-of select="concat('fill:', $background, '; stroke:none')"/></xsl:attribute>
					<xsl:attribute name="d"><xsl:value-of select='concat( "M0,0 v", $heightArea, " h", $widthArea, " v-", $heightArea, " z" )'/></xsl:attribute>
				</path>
			</xsl:if>

			<xsl:if test='Hierarchy'>
				<g id="Hierarchy">
					<xsl:attribute name="transform">
						<xsl:choose>
							<xsl:when test='Hierarchy/Class[1]/text()="..."'>
								<xsl:value-of select='concat( "translate( ", $xOffset + 10, ", ", 10, " )" )'/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select='concat( "translate( ", $xOffset + 10, ", ", 25 + 10, " )" )'/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					<xsl:call-template name="drawHierarchy">
						<xsl:with-param name="widthBoxes" select="$widthHierarchy"/>
					</xsl:call-template>
				</g>
			</xsl:if>

			<xsl:if test='EnclosingClasses'>
				<g id="EnclosingClasses">
					<xsl:attribute name="transform">translate( 10, <xsl:number value="$yOffset"/> )</xsl:attribute>
					<xsl:call-template name="drawEnclosingClassesR">
						<xsl:with-param name="count"  select="0"/>
						<xsl:with-param name="xOffsetEnclosingClasses" select="0"/>
					</xsl:call-template>
				</g>
			</xsl:if>
			
			<xsl:if test='Interfaces'>
				<xsl:call-template name="drawInterfaces">
					<xsl:with-param name="xOffset" select="10 + $xOffset + $widthBox"/>
					<xsl:with-param name="yOffset" select="$yOffset"/>
				</xsl:call-template>
			</xsl:if>

			<g id="Main">
				<xsl:attribute name="transform">translate( <xsl:number value="10 + $xOffset"/>, <xsl:number value="$yOffset"/> )</xsl:attribute>
				<g class="titledBox">
					<xsl:call-template name="drawBox">
						<xsl:with-param name="width" select="$widthBox"/>
						<xsl:with-param name="heightTitle" select="$heightTitle"/>
						<xsl:with-param name="heightContent" select="$heightBox"/>
						<xsl:with-param name="ySeparator1">
							<xsl:choose>
								<xsl:when test='name(.)="Enum"'>
									<xsl:number value="$heightPadding + count(Constants/Constant)*20 + 5"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:number value="$heightPadding + count(Fields/Field)*20 + 5"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:with-param>
						<xsl:with-param name="ySeparator2">
							<xsl:choose>
								<xsl:when test='name(.)="Enum"'>
									<xsl:number value="$heightPadding + ( count(Constants/Constant) + count(Fields/Field) )*20 + 15"/>
								</xsl:when>
								<xsl:otherwise>0</xsl:otherwise>
							</xsl:choose>
						</xsl:with-param>
						<xsl:with-param name="r" select="2"/>
						<xsl:with-param name="estilo" select="$estilo"/>
						<xsl:with-param name="filter" select="'url(shared/defs.svg#SpecShadow1)'"/>
					</xsl:call-template>
	
					<xsl:if test='Parameters'>
						<xsl:call-template name="Parameters">
							<xsl:with-param name="widthBox" select="$widthBox"/>
							<xsl:with-param name="yOffset" select="-$heightTitle"/>
							<xsl:with-param name="widthParameters" select="$widthParameters"/>
							<xsl:with-param name="estilo" select="$estilo"/>
						</xsl:call-template>
					</xsl:if>
	
					<xsl:call-template name="drawTitle">
						<xsl:with-param name="center" select="$widthBox div 2"/>
						<xsl:with-param name="prefix">
							<xsl:choose>
								<xsl:when test='name(.)="Interface"'>&lt;&lt; interface &gt;&gt;</xsl:when>
								<xsl:when test='name(.)="Enum"'>&lt;&lt; enumeration &gt;&gt;</xsl:when>
							</xsl:choose>
						</xsl:with-param>
						<xsl:with-param name="title" select="@name"/>
						<xsl:with-param name="isAbstract" select="@isAbstract"/>
					</xsl:call-template>
				</g>
				<g id="miembros">
					<xsl:attribute name="transform">translate( 7, <xsl:number value="$heightPadding"/> )</xsl:attribute>
					<xsl:if test='name(.)="Enum"'>
						<xsl:apply-templates select="Constants"/>
					</xsl:if>
					<xsl:apply-templates select="Fields|Constructors|Methods"/>
				</g>
			</g>
		</svg>
	</xsl:template>

	<xsl:template name="drawBox">
		<xsl:param name="width"/>
		<xsl:param name="heightTitle"/>
		<xsl:param name="heightContent"/>
		<xsl:param name="ySeparator1"/>
		<xsl:param name="ySeparator2"/>
		<xsl:param name="r"/>
		<xsl:param name="estilo"/>
		<xsl:param name="filter"/>
		
		<g class="box">
			<xsl:if test='string-length($filter) &gt; 0'>
				<xsl:attribute name="filter"><xsl:value-of select="$filter"/></xsl:attribute>
			</xsl:if>
	
			<xsl:element name="path">
				<xsl:attribute name="class">titulo-<xsl:value-of select="$estilo"/></xsl:attribute>
				<xsl:attribute name="d">
					<xsl:value-of select="concat(
						'M0,0',
						' v-', $heightTitle - $r,
						' a', $r, ',', $r, ' 0 0,1 ', $r, ',-', $r,
						' h', $width - 2 * $r,
						' a', $r, ',', $r, ' 0 0,1 ', $r, ',', $r,
						' v', $heightTitle - $r
					)"/>
				</xsl:attribute>
			</xsl:element>
			<xsl:element name="path">
				<xsl:attribute name="class">contenido-<xsl:value-of select="$estilo"/></xsl:attribute>
				<xsl:attribute name="d">
					<xsl:value-of select="concat(
						'M0,0',
						' v', $heightContent - $r,
						' a', $r, ',', $r, ' 0 0,0 ', $r, ',', $r,
						' h', $width - 2 * $r,
						' a', $r, ',', $r, ' 0 0,0 ', $r, ',-', $r,
						' v-', $heightContent - $r,
						' z'
					)"/>
				</xsl:attribute>
			</xsl:element>
			<xsl:element name="path">
				<xsl:attribute name="d">
					<xsl:value-of select="concat( 'M0,', $ySeparator1, ' h', $width)"/>
				</xsl:attribute>
			</xsl:element>
			<xsl:if test='$ySeparator2 &gt; 0'>
				<xsl:element name="path">
					<xsl:attribute name="d">
						<xsl:value-of select="concat( 'M0,', $ySeparator2, ' h', $width)"/>
					</xsl:attribute>
				</xsl:element>
			</xsl:if>
		</g>
	</xsl:template>

	<xsl:template name="drawTitle">
		<xsl:param name="center"/>
		<xsl:param name="prefix"/>
		<xsl:param name="title"/>
		<xsl:param name="isAbstract"/>
		<xsl:if test='string-length($prefix) &gt; 0'>
			<text>
				<xsl:attribute name="x"><xsl:number value="$center"/></xsl:attribute>
				<xsl:attribute name="y">-28</xsl:attribute>
				<xsl:attribute name="class">centrado</xsl:attribute>
				<xsl:value-of select="$prefix"/>
			</text>
		</xsl:if>
		<text>
			<xsl:attribute name="class">titulo<xsl:if test='$isAbstract="true"'>-abstract</xsl:if></xsl:attribute>
			<xsl:attribute name="x"><xsl:number value="$center"/></xsl:attribute>
			<xsl:attribute name="y">-8</xsl:attribute>
			<xsl:value-of select="$title"/>
		</text>
	</xsl:template>

	<xsl:template name="drawHierarchy">
		<xsl:param name="widthBoxes"/>

		<xsl:for-each select="Hierarchy/Class">
			<g>
				<xsl:attribute name="transform">
					<xsl:value-of select="concat( 'translate( ', ( position() - last() )*40, ', ', ( position() -1 )*55, ' )' )"/>
				</xsl:attribute>
				<xsl:choose>
					<xsl:when test='position() = last()'>
						<path class="hierarchy-conector" d="M20,10 v35" />
					</xsl:when>
					<xsl:otherwise>
						<path class="hierarchy-conector" d="M20,10 v35 h20" />
					</xsl:otherwise>
				</xsl:choose>
				<xsl:choose>
					<xsl:when test='position() = 1 and text()="..."'>
						<text class="titulo" x="20" y="5">...</text>
					</xsl:when>
					<xsl:otherwise>
						<g class="titledBox">
							<xsl:call-template name="drawBox">
								<xsl:with-param name="width"  select="$widthBoxes"/>
								<xsl:with-param name="heightTitle" select="25"/>
								<xsl:with-param name="heightContent" select="10"/>
								<xsl:with-param name="ySeparator1" select="5"/>
								<xsl:with-param name="ySeparator2" select="0"/>
								<xsl:with-param name="r" select="2"/>
								<xsl:with-param name="estilo" select="'estilo1'"/>
								<xsl:with-param name="filter" select="'url(shared/defs.svg#SpecShadow2)'"/>
							</xsl:call-template>
							<xsl:call-template name="drawTitle">
								<xsl:with-param name="center" select="$widthBoxes div 2"/>
								<xsl:with-param name="prefix"/>
								<xsl:with-param name="title" select="text()"/>
								<xsl:with-param name="isAbstract" select="@isAbstract"/>
							</xsl:call-template>
						</g>
					</xsl:otherwise>
				</xsl:choose>
			</g>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="drawEnclosingClassesR">
		<xsl:param name="count"/>
		<xsl:param name="xOffsetEnclosingClasses"/>
		<xsl:if test='$count &lt; count(EnclosingClasses/*)'>
			<xsl:variable name="nodeName" select="name(EnclosingClasses/*[$count+1])"/>
			<xsl:variable name="widthBox">
				<xsl:call-template name="calculateWidthBox">
					<xsl:with-param name="node" select="EnclosingClasses/*[$count+1]"/>
				</xsl:call-template>
			</xsl:variable>
			<g class="titledBox">
				<xsl:attribute name="transform">translate( <xsl:number value="$xOffsetEnclosingClasses"/>, 0 )</xsl:attribute>
				<xsl:call-template name="drawBox">
					<xsl:with-param name="width"  select="$widthBox"/>
					<xsl:with-param name="heightTitle">
						<xsl:choose>
							<xsl:when test='$nodeName="Class"'>25</xsl:when>
							<xsl:otherwise>45</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="heightContent" select="10"/>
					<xsl:with-param name="ySeparator1" select="5"/>
					<xsl:with-param name="ySeparator2" select="0"/>
					<xsl:with-param name="r" select="2"/>
					<xsl:with-param name="estilo">
						<xsl:choose>
							<xsl:when test='$nodeName="Class"'>estilo1</xsl:when>
							<xsl:when test='$nodeName="Interface"'>estilo2</xsl:when>
							<xsl:otherwise>estilo3</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="filter" select="'url(shared/defs.svg#SpecShadow2)'"/>
				</xsl:call-template>
				<xsl:call-template name="drawTitle">
					<xsl:with-param name="center" select="$widthBox div 2"/>
					<xsl:with-param name="prefix">
						<xsl:choose>
							<xsl:when test='$nodeName="Interface"'>&lt;&lt; interface &gt;&gt;</xsl:when>
							<xsl:when test='$nodeName="Enum"'>&lt;&lt; enumeration &gt;&gt;</xsl:when>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="title" select="EnclosingClasses/*[$count+1]/text()"/>
					<xsl:with-param name="isAbstract" select="EnclosingClasses/*[$count+1]/@isAbstract"/>
				</xsl:call-template>
			</g>
			<xsl:element name="path">
				<xsl:attribute name="class">container-conector</xsl:attribute>
				<xsl:attribute name="d">M<xsl:value-of select="$xOffsetEnclosingClasses + $widthBox"/>,-12.5 h40</xsl:attribute>
			</xsl:element>
			<xsl:call-template name="drawEnclosingClassesR">
				<xsl:with-param name="count"  select="$count +1"/>
				<xsl:with-param name="xOffsetEnclosingClasses" select="$xOffsetEnclosingClasses + $widthBox + 40"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="drawInterfaces">
		<xsl:param name="xOffset"/>
		<xsl:param name="yOffset"/>
		<g>
			<xsl:attribute name="id">Interfaces</xsl:attribute>
			<xsl:attribute name="transform">
				<xsl:value-of select="concat('translate( ', $xOffset, ', ', $yOffset, ' )')"/>
			</xsl:attribute>
			<xsl:for-each select="Interfaces/Interface">
				<xsl:element name="path">
					<xsl:attribute name="class">interface-conector</xsl:attribute>
					<xsl:attribute name="d">
						<xsl:choose>
							<xsl:when test='position() = 1'>M0,-12.5 h45</xsl:when>
							<xsl:when test='position() = last()'>
								<xsl:value-of select="concat( 'M15,-12.5 v', ( position() - 1 )*30 - 5, ' l5,5 h25')"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="concat( 'M15,', ( position() -1 )*30 -17.5, ' l5,5 h25')"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
				</xsl:element>
				<xsl:element name="text" xml:space="default">
					<xsl:attribute name="x">50</xsl:attribute>
					<xsl:attribute name="y"><xsl:value-of select="( position() -1 )*30 -8"/></xsl:attribute>
					<xsl:value-of select="text()"/>
				</xsl:element>
			</xsl:for-each>
		</g>
	</xsl:template>

	<xsl:template name="Parameters">
		<xsl:param name="widthBox"/>
		<xsl:param name="yOffset"/>
		<xsl:param name="widthParameters"/>
		<xsl:param name="estilo"/>
		<g class="parameters">
			<xsl:attribute name="transform">
				<xsl:value-of select='concat( "translate( ", $widthBox - $widthParameters + 15, ", ", $yOffset, " )" )'/>
			</xsl:attribute>
			<path>
				<xsl:attribute name="class">parameters-<xsl:value-of select="$estilo"/></xsl:attribute>
				<xsl:attribute name="d">
					<xsl:value-of select="concat( 'M0,5 v-25 h', $widthParameters, ' v25 z' )"/>
				</xsl:attribute>
				<xsl:attribute name="filter">url(shared/defs.svg#SpecShadow2)</xsl:attribute>
			</path>
			<text>
				<xsl:attribute name="class">titulo</xsl:attribute>
				<xsl:attribute name="x"><xsl:value-of select="$widthParameters div 2"/></xsl:attribute>
				<xsl:attribute name="y">-3</xsl:attribute>
				<xsl:value-of select="Parameters/text()"/>
			</text>
		</g>
	</xsl:template>
	
	<xsl:template match="Constants">
		<xsl:for-each select="Constant">
			<xsl:variable name="y"><xsl:number value="( position() -1 ) *20"/></xsl:variable>
			<xsl:element name="use">
				<xsl:attribute name="x">0</xsl:attribute>
				<xsl:attribute name="y"><xsl:number value='$y'/></xsl:attribute>
				<xsl:attribute name="xlink:href"><xsl:value-of select="$defs"/>EnumConstant</xsl:attribute>
			</xsl:element>
			<text>
				<xsl:attribute name="class">static</xsl:attribute>
				<xsl:attribute name="x">23</xsl:attribute>
				<xsl:attribute name="y"><xsl:number value='$y +13'/></xsl:attribute>
				<xsl:value-of select="text()"/>
			</text>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="Modifiers">
		<xsl:choose>
			<xsl:when test='@isStatic="true"'>Static</xsl:when>
			<xsl:when test='@isAbstract="true"'>Abstract</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="Visibility">
		<xsl:choose>
			<xsl:when test='@visibility="+"'>Public</xsl:when>
			<xsl:when test='@visibility="#"'>Protected</xsl:when>
			<xsl:when test='@visibility="~"'>Package</xsl:when>
			<xsl:when test='@visibility="-"'>Private</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="Fields">
		<xsl:variable name="yOffset">
			<xsl:choose>
				<xsl:when test='name(..)="Enum"'>
					<xsl:number value="( count(../Constants/Constant) )*20 + 10"/>
				</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:for-each select="Field">
			<xsl:variable name="y"><xsl:number value="( position() -1 ) *20 + $yOffset"/></xsl:variable>
			<xsl:element name="use">
				<xsl:attribute name="x">0</xsl:attribute>
				<xsl:attribute name="y"><xsl:number value='$y'/></xsl:attribute>
				<xsl:attribute name="xlink:href">
					<xsl:value-of select="$defs"/>
					<xsl:call-template name="Visibility"/>
					<xsl:call-template name="Modifiers"/>Field</xsl:attribute>
			</xsl:element>
			<xsl:if test='@isTransient="true"'>
				<xsl:element name="use">
					<xsl:attribute name="x">0</xsl:attribute>
					<xsl:attribute name="y"><xsl:number value='$y'/></xsl:attribute>
					<xsl:attribute name="xlink:href"><xsl:value-of select="$defs"/>Transient</xsl:attribute>
				</xsl:element>
			</xsl:if>
			<xsl:if test='@isVolatile="true"'>
				<xsl:element name="use">
					<xsl:attribute name="x">0</xsl:attribute>
					<xsl:attribute name="y"><xsl:number value='$y'/></xsl:attribute>
					<xsl:attribute name="xlink:href"><xsl:value-of select="$defs"/>Volatile</xsl:attribute>
				</xsl:element>
			</xsl:if>
			<xsl:if test='@isFinal="true"'>
				<xsl:element name="use">
					<xsl:attribute name="x">0</xsl:attribute>
					<xsl:attribute name="y"><xsl:number value='$y'/></xsl:attribute>
					<xsl:attribute name="xlink:href"><xsl:value-of select="$defs"/>Final</xsl:attribute>
				</xsl:element>
			</xsl:if>
			<text>
				<xsl:if test='@isStatic="true"'>
					<xsl:attribute name="class">static</xsl:attribute>
				</xsl:if>
				<xsl:attribute name="x">23</xsl:attribute>
				<xsl:attribute name="y"><xsl:number value='$y + 13'/></xsl:attribute>
				<xsl:value-of select="text()"/>
			</text>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="Constructors">
		<xsl:variable name="yOffset">
			<xsl:choose>
				<xsl:when test='name(..)="Enum"'>
					<xsl:number value="( count(../Constants/Constant) + count(../Fields/Field) )*20 + 20"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="( count(../Fields/Field) )*20 + 10"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:for-each select="Constructor">
			<xsl:variable name="y"><xsl:number value="( position() -1 ) *20 + $yOffset"/></xsl:variable>
			<xsl:element name="use">
				<xsl:attribute name="x">0</xsl:attribute>
				<xsl:attribute name="y"><xsl:number value='$y'/></xsl:attribute>
				<xsl:attribute name="xlink:href">
					<xsl:value-of select="$defs"/>
					<xsl:call-template name="Visibility"/>Constructor</xsl:attribute>
			</xsl:element>
			<text>
				<xsl:attribute name="x">23</xsl:attribute>
				<xsl:attribute name="y"><xsl:number value='$y + 13'/></xsl:attribute>
				<xsl:value-of select="text()"/>
			</text>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="Methods">
		<xsl:variable name="yOffset">
			<xsl:choose>
				<xsl:when test='name(..)="Enum"'>
					<xsl:number value="( count(../Constants/Constant) + count(../Fields/Field) + count(../Constructors/Constructor) )*20 + 20"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="( count(../Fields/Field) + count(../Constructors/Constructor) )*20 + 10"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:for-each select="Method">
			<xsl:variable name="y"><xsl:number value="( position() -1 ) *20 + $yOffset"/></xsl:variable>
			<xsl:element name="use">
				<xsl:attribute name="x">0</xsl:attribute>
				<xsl:attribute name="y"><xsl:number value='$y'/></xsl:attribute>
				<xsl:attribute name="xlink:href">
					<xsl:value-of select="$defs"/>
					<xsl:call-template name="Visibility"/>
					<xsl:call-template name="Modifiers"/>Method</xsl:attribute>
			</xsl:element>
			<xsl:if test='@isNative="true"'>
				<xsl:element name="use">
					<xsl:attribute name="x">0</xsl:attribute>
					<xsl:attribute name="y"><xsl:number value='$y'/></xsl:attribute>
					<xsl:attribute name="xlink:href"><xsl:value-of select="$defs"/>Native</xsl:attribute>
				</xsl:element>
			</xsl:if>
			<xsl:if test='@isStrictfp="true"'>
				<xsl:element name="use">
					<xsl:attribute name="x">0</xsl:attribute>
					<xsl:attribute name="y"><xsl:number value='$y'/></xsl:attribute>
					<xsl:attribute name="xlink:href"><xsl:value-of select="$defs"/>Strictfp</xsl:attribute>
				</xsl:element>
			</xsl:if>
			<xsl:if test='@isFinal="true"'>
				<xsl:element name="use">
					<xsl:attribute name="x">0</xsl:attribute>
					<xsl:attribute name="y"><xsl:number value='$y'/></xsl:attribute>
					<xsl:attribute name="xlink:href"><xsl:value-of select="$defs"/>Final</xsl:attribute>
				</xsl:element>
			</xsl:if>
			<xsl:if test='@isSynchronized="true"'>
				<xsl:element name="use">
					<xsl:attribute name="x">0</xsl:attribute>
					<xsl:attribute name="y"><xsl:number value='$y'/></xsl:attribute>
					<xsl:attribute name="xlink:href"><xsl:value-of select="$defs"/>Synchronized</xsl:attribute>
				</xsl:element>
			</xsl:if>
			<text>
				<xsl:choose>
					<xsl:when test='@isStatic="true"'>
					  <xsl:attribute name="class">static</xsl:attribute>
					</xsl:when>
					<xsl:when test='@isAbstract="true"'>
					  <xsl:attribute name="class">abstract</xsl:attribute>
					</xsl:when>
				</xsl:choose>
				<xsl:attribute name="x">23</xsl:attribute>
				<xsl:attribute name="y"><xsl:number value='$y + 13'/></xsl:attribute>
				<xsl:value-of select="text()"/>
			</text>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>