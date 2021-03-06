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
        <xsl:apply-templates select="circle" />
        <xsl:apply-templates select="ranks/rank" />
    </xsl:template>
    <xsl:template match="circle">
        <p>
            <xsl:value-of select="id"/>
            <xsl:text>: </xsl:text>
            <xsl:value-of select="city"/>
            <xsl:text> &quot;</xsl:text>
            <xsl:value-of select="tag"/>
            <xsl:text>&quot;</xsl:text>
        </p>
    </xsl:template>
    <xsl:template match="ranks/rank">
        <p>
            <a>
                <xsl:attribute name="href">
                    <xsl:value-of select="link[@rel='twitter']/@href"/>
                </xsl:attribute>
                <xsl:text>@</xsl:text>
                <xsl:value-of select="user"/>
            </a>
            <xsl:text>: </xsl:text>
            <xsl:value-of select="value"/>
            <xsl:text> </xsl:text>
            <a>
                <xsl:attribute name="href">
                    <xsl:value-of select="link[@rel='spam']/@href"/>
                </xsl:attribute>
                <i class="fa fa-trash-o"></i>
            </a>
        </p>
    </xsl:template>
</xsl:stylesheet>
