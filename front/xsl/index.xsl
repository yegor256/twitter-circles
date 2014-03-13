<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml" version="2.0">
    <xsl:output method="xml" omit-xml-declaration="yes"/>
    <xsl:include href="/xsl/layout.xsl"/>
    <xsl:template match="page" mode="head">
        <title>circles</title>
    </xsl:template>
    <xsl:template match="page" mode="body">
        <h1>
            <xsl:text>Circles</xsl:text>
        </h1>
        <p>
            <xsl:text>Latest tweet on </xsl:text>
            <xsl:value-of select="latest"/>
        </p>
        <xsl:apply-templates select="circles/circle" />
    </xsl:template>
    <xsl:template match="circles/circle">
        <p>
            <a>
                <xsl:attribute name="href">
                    <xsl:value-of select="link[@rel='see']/@href"/>
                </xsl:attribute>
                <xsl:value-of select="city"/>
                <xsl:text> </xsl:text>
                <xsl:text>&quot;</xsl:text>
                <xsl:value-of select="tag"/>
                <xsl:text>&quot;</xsl:text>
            </a>
            <xsl:text>: </xsl:text>
            <xsl:value-of select="tweets"/>
            <xsl:text> </xsl:text>
            <a>
                <xsl:attribute name="href">
                    <xsl:value-of select="link[@rel='delete']/@href"/>
                </xsl:attribute>
                <i class="fa fa-trash-o"></i>
            </a>
        </p>
    </xsl:template>
</xsl:stylesheet>
