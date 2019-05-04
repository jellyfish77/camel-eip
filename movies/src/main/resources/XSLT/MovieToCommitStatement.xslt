<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">

		<Statement>
			<xsl:for-each select="Movie">
				<Field name="title">
					<xsl:value-of select="Title" />
				</Field>
				<Field name="imdb_id">
					<xsl:value-of select="ImdbId" />
				</Field>
				<Field name="gross">
					<xsl:value-of select="Gross" />
				</Field>
				<Field name="type">
					<xsl:value-of select="Format/Type" />
				</Field>
				<Field name="color">
					<xsl:value-of select="Format/Color" />
				</Field>
				<Field name="language">
					<xsl:value-of select="Format/Language" />
				</Field>
				<Field name="aspect">
					<xsl:value-of select="Format/AspectRatio" />
				</Field>
				<Field name="runtime">
					<xsl:value-of select="Format/Runtime" />
				</Field>
				<Field name="budget">
					<xsl:value-of select="Production/Budget" />
				</Field>
				<Field name="year">
					<xsl:value-of select="Production/Year" />
				</Field>
				<Field name="release_date">
					<xsl:value-of select="Production/ReleaseDate" />
				</Field>
				<Field name="dvd_date">
					<xsl:value-of select="Production/DvdDate" />
				</Field>
				<Field name="website">
					<xsl:value-of select="Production/Website" />
				</Field>
				<Field name="plot">
					<xsl:value-of select="Plot/Description" />
				</Field>
				<Field name="poster">
					<xsl:value-of select="Poster" />
				</Field>
				<Field name="rating">
					<xsl:value-of select="Rating" />
				</Field>
				<Field name="imdb_link">
					<xsl:value-of select="ImdbLink" />
				</Field>
				<Field name="country">
					<xsl:value-of select="Country" />
				</Field>
				<Field name="director">
					<xsl:value-of select="Director/Name" />
				</Field>
				<Field name="writer">
					<xsl:value-of select="//Writer" />
				</Field>				
				<Field name="imdb_rating">
					<xsl:value-of select="//Rating[Source='Internet Movie Database']/Value" />
				</Field>
				<Field name="rt_rating">
					<xsl:value-of select="//Rating[Source='Rotten Tomatoes']/Value" />
				</Field>
				<Field name="mc_rating">
					<xsl:value-of select="//Rating[Source='Metacritic']/Value" />
				</Field>
			</xsl:for-each>
		</Statement>

	</xsl:template>
</xsl:stylesheet>