package org.dbpedia.extraction.server.resources.stylesheets

import xml.Elem
import javax.ws.rs.{GET, Produces, Path}
import org.dbpedia.extraction.destinations.formatters.{Formatter,TriXFormatter}
import java.io.Writer
import org.dbpedia.extraction.server.util.WebUtils

object TriX
{
    /**
     * @param parents number of "../" steps to prepend to the path to "stylesheets/trix.xsl"
     */
    def writeHeader(writer: Writer, parents : Int): Formatter = 
    {
      writer.write("<?xml-stylesheet type=\"text/xsl\" href=\""+("../"*parents)+"stylesheets/trix.xsl\"?>\n")
      new TriXFormatter(true)
    }
}

@Path("/stylesheets/trix.xsl")
class TriX
{
    @GET
    @Produces(Array("text/xsl"))
    def get : Elem =
    {
        <xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:trix="http://www.w3.org/2004/03/trix/trix-1/">
          <xsl:template match="/trix:TriX">
            <html lang="en">
              <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                <link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.1.0/css/bootstrap.min.css" />
                <style type="text/css">{WebUtils.dashboardCss()}</style>
              </head>
              <body>
                {WebUtils.getBootStrapNavBar()}
                <div class="container-fluid">
                  <div class="row">
                    {WebUtils.addSidebar()}
                    <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
                      <h1 class="page-header">DBpedia Extraction Results</h1>
                      <table class="table table-striped table-condensed table-bordered">
                        <thead>
                          <tr>
                            <th>Subject</th>
                            <th>Predicate</th>
                            <th>Object</th>
                            <th>Triple Provenance</th>
                          </tr>
                        </thead>
                        <xsl:for-each select="trix:graph">
                          <xsl:variable name="context" select="trix:uri"/>
                          <xsl:if test="trix:triple[1]/trix:uri[1] != preceding-sibling::trix:graph[1]/trix:triple[1]/trix:uri[1]">
                            <thead>
                              <tr>
                                <th>Subject</th>
                                <th>Predicate</th>
                                <th>Object</th>
                                <th>Triple Provenance</th>
                              </tr>
                            </thead>
                          </xsl:if>
                          <xsl:for-each select="trix:triple">
                            <tr>
                              <xsl:apply-templates/>
                              <td><a href="{$context}" target="_blank"><xsl:value-of select="$context"/></a></td>
                            </tr>
                          </xsl:for-each>
                        </xsl:for-each>
                      </table>
                    </div>
                    </div>
                  </div>
              </body>
            </html>
          </xsl:template>

          <!-- generate links from <uri> elements -->
          <xsl:template match="trix:uri">
            <td><a href="{.}"><xsl:value-of select="."/></a></td>
          </xsl:template>

          <!-- just display text for other elements -->
          <xsl:template match="*">
            <td><xsl:value-of select="."/></td>
          </xsl:template>

        </xsl:stylesheet>
    }
}
