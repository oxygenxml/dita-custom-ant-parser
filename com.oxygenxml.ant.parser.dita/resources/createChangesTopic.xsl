<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:xs="http://www.w3.org/2001/XMLSchema"
 exclude-result-prefixes="xs"
 version="2.0">
 <xsl:output doctype-public="-//OASIS//DTD DITA Topic//EN" doctype-system="topic.dtd" indent="yes" method="xml"/>
 <xsl:template match="/">
  <topic id="changes">
   <title>List of changes since last release</title>
   <body>
    <p>Changes:</p>
    <ul>
     <!-- Load changes from a text file -->
     <xsl:analyze-string select="unparsed-text('changes.txt')" regex="\n">
      <xsl:non-matching-substring>
       <xsl:if test="not(ends-with(., '.ditamap'))">
        <li><xref href="{replace(., 'DITA/', '')}"/></li>
       </xsl:if>
      </xsl:non-matching-substring>
     </xsl:analyze-string>
    </ul>
   </body>
  </topic>
 </xsl:template>
</xsl:stylesheet>