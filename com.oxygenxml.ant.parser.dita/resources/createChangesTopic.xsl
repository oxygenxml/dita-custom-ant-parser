<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:xs="http://www.w3.org/2001/XMLSchema"
 xmlns:oxy="http://www.oxygenxml.com/extensions/functions/"
 exclude-result-prefixes="xs oxy"
 version="2.0">
 <xsl:output doctype-public="-//OASIS//DTD DITA Topic//EN" doctype-system="topic.dtd" indent="yes" method="xml"/>
 <xsl:param name="pathToChangesFile"/>
 <xsl:template match="/">
  <topic id="changes">
   <title>List of changes since last release</title>
   <body>
    <p>Changes:</p>
    <ul>
     <!-- Load changes from a text file -->
     <xsl:analyze-string select="unparsed-text(oxy:toUrl($pathToChangesFile))" regex="\n">
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
 
 <!-- Translates a file path to a file:// URL. -->
 <xsl:function name="oxy:toUrl" as="xs:string">
  <xsl:param name="filepath" as="xs:string"/>
  <xsl:variable name="url" as="xs:string"
   select="if (contains($filepath, '\'))
   then translate($filepath, '\', '/')
   else $filepath
   "
  />
  <xsl:variable name="fileUrl" as="xs:string"
   select="
   if (matches($url, '^[a-zA-Z]:'))
   then concat('file:/', $url)
   else if (starts-with($url, '/')) 
   then concat('file:', $url) 
   else $url
   "
  />
  <xsl:variable name="escapedUrl" 
   select="replace($fileUrl, ' ', '%20')"
  />
  <xsl:sequence select="$escapedUrl"/>
 </xsl:function>
</xsl:stylesheet>