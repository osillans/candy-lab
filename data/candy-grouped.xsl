<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    <xsl:strip-space elements="*"/>

    <xsl:key name="sweets-by-type" match="Sweet" use="Type"/>

    <xsl:template match="/Candy">
        <Candy>
            <xsl:for-each
                select="Sweet[generate-id() =
                              generate-id(key('sweets-by-type', Type)[1])]">
                <xsl:sort select="Type"/>
                <xsl:element name="{Type}">
                    <xsl:for-each select="key('sweets-by-type', Type)">
                        <xsl:sort select="Name"/>
                        <xsl:copy-of select="."/>
                    </xsl:for-each>
                </xsl:element>
            </xsl:for-each>
        </Candy>
    </xsl:template>

</xsl:stylesheet>