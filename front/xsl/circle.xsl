<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml" version="2.0">
    <xsl:output method="xml" omit-xml-declaration="yes"/>
    <xsl:include href="/xsl/layout.xsl"/>
    <xsl:template match="page" mode="head">
        <title>
            <xsl:text>Circle #</xsl:text>
            <xsl:value-of select="circle/@id"/>
        </title>
    </xsl:template>
    <xsl:template match="page" mode="body">
        <h1>
            <xsl:text>Users</xsl:text>
        </h1>
        <xsl:apply-templates select="ranks/rank" />
    </xsl:template>
    <xsl:template match="ranks/rank">
        <p>
            <a>
                <xsl:attribute name="href">
                    <xsl:text>https://twitter.com/</xsl:text>
                    <xsl:value-of select="user"/>
                </xsl:attribute>
                <xsl:text>@</xsl:text>
                <xsl:value-of select="user"/>
            </a>
            <xsl:text>: </xsl:text>
            <xsl:value-of select="value"/>
        </p>
    </xsl:template>
</xsl:stylesheet>
