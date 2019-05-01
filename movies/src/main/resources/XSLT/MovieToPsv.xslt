<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="text"/>
	<xsl:variable name="delimiter" select="'|'"/>	
	<xsl:template match ="/">
		<xsl:text>Title|ImdbId|Gross|Type|Color|Language|AspectRatio|Runtime|Year|ReleaseDate|DvdDate|Rating|Country|Director</xsl:text>
		<xsl:text>&#xa;</xsl:text>
		<xsl:for-each select="Movies/Movie">
			<xsl:value-of select="Title" />			
			<xsl:value-of select="$delimiter"/>
			<xsl:value-of select="ImdbId"/>
			<xsl:value-of select="$delimiter"/>
			<xsl:value-of select="Gross"/>
			<xsl:value-of select="$delimiter"/>
			<xsl:value-of select="Format/Type"/>
			<xsl:value-of select="$delimiter"/>
			<xsl:value-of select="Format/Color"/>
			<xsl:value-of select="$delimiter"/>
			<xsl:value-of select="Format/Language"/>
			<xsl:value-of select="$delimiter"/>
			<xsl:value-of select="Format/AspectRatio"/>        
			<xsl:value-of select="$delimiter"/>
			<xsl:value-of select="Format/Runtime"/>        
			<xsl:value-of select="$delimiter"/>
			<xsl:value-of select="Production/Year"/>        
			<xsl:value-of select="$delimiter"/>
			<xsl:value-of select="Production/ReleaseDate"/>        
			<xsl:value-of select="$delimiter"/>
			<xsl:value-of select="Production/DvdDate"/>        
			<xsl:value-of select="$delimiter"/>
			<xsl:value-of select="Rating"/>        
			<xsl:value-of select="$delimiter"/>
			<xsl:value-of select="Country"/>        
			<xsl:value-of select="$delimiter"/>
			<xsl:value-of select="Director/Name"/>        			
			<!-- newline -->
			<xsl:text>&#xa;</xsl:text>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>