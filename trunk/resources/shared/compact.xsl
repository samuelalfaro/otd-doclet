<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:exslt="http://exslt.org/common" version="1.0">
	<xsl:output method="xml" indent="no" cdata-section-elements="Class Interface Enum Parameters Constant Field Constructor Method"/>
	<xsl:strip-space elements="*"/>
	
	<xsl:template name="compact">
		<xsl:element name="{name(.)}">
			<xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
			<xsl:if test="@isAbstract">
				<xsl:attribute name="isAbstract"><xsl:value-of select="@isAbstract"/></xsl:attribute>
			</xsl:if>
			<xsl:if test="TypeParameters">
				<xsl:element name="Parameters">
					<xsl:apply-templates select="TypeParameters" mode="compact"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="Hierarchy">
				<xsl:copy-of select='./Hierarchy'/> 
			</xsl:if>
			<xsl:if test="EnclosingClasses">
				<xsl:copy-of select='./EnclosingClasses'/> 
			</xsl:if>
			<xsl:if test="Interfaces">
				<xsl:copy-of select='./Interfaces'/> 
			</xsl:if>
			<xsl:apply-templates select="Constants|Fields|Constructors|Methods" mode="compact"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="Parameters|TypeParameters" mode="compact">
		<xsl:for-each select="Parameter">
			<xsl:value-of select="Type/text()"/>
			<xsl:if test="position() != last()">, </xsl:if>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="Constants" mode="compact">
		<xsl:element name="Constants">
			<xsl:for-each select="Constant">
				<xsl:element name="Constant">
					<xsl:value-of select="@name"/>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

	<xsl:template match="Fields" mode="compact">
		<xsl:element name="Fields">
			<xsl:for-each select="Field">
				<xsl:element name="Field">
					<xsl:attribute name="visibility"><xsl:value-of select="@visibility"/></xsl:attribute>
					<xsl:if test="@isStatic">
						<xsl:attribute name="isStatic"><xsl:value-of select="@isStatic"/></xsl:attribute>
					</xsl:if>
					<xsl:if test="@isTransient">
						<xsl:attribute name="isTransient"><xsl:value-of select="@isTransient"/></xsl:attribute>
					</xsl:if>
					<xsl:if test="@isVolatile">
						<xsl:attribute name="isVolatile"><xsl:value-of select="@isVolatile"/></xsl:attribute>
					</xsl:if>
					<xsl:if test="@isFinal">
						<xsl:attribute name="isFinal"><xsl:value-of select="@isFinal"/></xsl:attribute>
					</xsl:if>
					<xsl:value-of select="concat(@name, ': ', Type/text())"/>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

	<xsl:template name="getSimpleClassName">
		<xsl:param name="className"/>
		<xsl:choose>
			<xsl:when test="contains($className, '.')">
				<xsl:call-template name="getSimpleClassName">
					<xsl:with-param name="className" select="substring-after($className, '.')"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$className"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="Constructors" mode="compact">
		<xsl:element name="Constructors">
			<xsl:for-each select="Constructor">
				<xsl:variable name="paramsSeparator">
					<xsl:choose>
						<xsl:when test="Parameters"><xsl:value-of select="' '"/></xsl:when>
						<xsl:otherwise/>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="constructorName">
					<xsl:choose>
						<xsl:when test="@name"><xsl:value-of select="@name"/></xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="getSimpleClassName">
								<xsl:with-param name="className" select="../../@name"/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:element name="Constructor">
					<xsl:attribute name="visibility"><xsl:value-of select="@visibility"/></xsl:attribute>
					<xsl:value-of select="concat($constructorName, '(', $paramsSeparator)"/>
					<xsl:apply-templates select="Parameters" mode="compact"/>
					<xsl:value-of select="concat($paramsSeparator,')')"/>
					<xsl:if test="TypeParameters">
						<xsl:value-of select="' &lt;'"/>
						<xsl:apply-templates select="TypeParameters" mode="compact"/>
						<xsl:value-of select="'&gt;'"/>
					</xsl:if>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

	<xsl:template match="Methods" mode="compact">
		<xsl:element name="Methods">
			<xsl:for-each select="Method">
				<xsl:variable name="paramsSeparator">
					<xsl:choose>
						<xsl:when test="Parameters"><xsl:value-of select="' '"/></xsl:when>
						<xsl:otherwise/>
					</xsl:choose>
				</xsl:variable>
				<xsl:element name="Method">
					<xsl:attribute name="visibility"><xsl:value-of select="@visibility"/></xsl:attribute>
					<xsl:if test="@isStatic">
						<xsl:attribute name="isStatic"><xsl:value-of select="@isStatic"/></xsl:attribute>
					</xsl:if>
					<xsl:if test="@isAbstract">
						<xsl:attribute name="isAbstract"><xsl:value-of select="@isAbstract"/></xsl:attribute>
					</xsl:if>
					<xsl:if test="@isNative">
						<xsl:attribute name="isNative"><xsl:value-of select="@isNative"/></xsl:attribute>
					</xsl:if>
					<xsl:if test="@isStrictfp">
						<xsl:attribute name="isStrictfp"><xsl:value-of select="@isStrictfp"/></xsl:attribute>
					</xsl:if>
					<xsl:if test="@isFinal">
						<xsl:attribute name="isFinal"><xsl:value-of select="@isFinal"/></xsl:attribute>
					</xsl:if>
					<xsl:if test="@isSynchronized">
						<xsl:attribute name="isSynchronized"><xsl:value-of select="@isSynchronized"/></xsl:attribute>
					</xsl:if>
					<xsl:value-of select="concat(@name, '(', $paramsSeparator)"/>
					<xsl:apply-templates select="Parameters" mode="compact"/>
					<xsl:value-of select="concat($paramsSeparator,')')"/>
					<xsl:if test="ReturnType">
						<xsl:value-of select="concat(': ',ReturnType/Type/text())"/>
					</xsl:if>
					<xsl:if test="TypeParameters">
						<xsl:value-of select="' &lt;'"/>
						<xsl:apply-templates select="TypeParameters" mode="compact"/>
						<xsl:value-of select="'&gt;'"/>
					</xsl:if>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>
	
</xsl:transform>