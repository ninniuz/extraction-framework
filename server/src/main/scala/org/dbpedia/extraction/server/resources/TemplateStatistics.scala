package org.dbpedia.extraction.server.resources

import javax.ws.rs._
import org.dbpedia.extraction.server.{ServerExtractionContext, Server}
import collection.immutable.ListMap
import org.dbpedia.extraction.mappings._
import org.dbpedia.extraction.util.{WikiUtil, Language}
import org.dbpedia.extraction.server.util.CreateMappingStats.{WikipediaStats, MappingStats}
import java.io.{FileNotFoundException, File}
import org.dbpedia.extraction.server.util.{IgnoreList, CreateMappingStats}
import org.openrdf.query.algebra.Var
import xml.include.sax.EncodingHeuristics.EncodingNames

@Path("/templatestatistics/{lang}/{template}")
class TemplateStatistics(@PathParam("lang") langCode: String, @PathParam("template") template: String) extends Base
{
    private val language = Language.fromWikiCode(langCode)
                .getOrElse(throw new WebApplicationException(new Exception("invalid language "+langCode), 404))

    if (!Server.config.languages.contains(language)) throw new WebApplicationException(new Exception("language "+langCode+" not defined in server"), 404)

    private var wikipediaStatistics: WikipediaStats = null
    if (new File(CreateMappingStats.serializeFileName).isFile)
    {
        println("Loading serialized object from " + CreateMappingStats.serializeFileName)
        wikipediaStatistics = CreateMappingStats.deserialize(CreateMappingStats.serializeFileName)
    }
    else
    {
        println("Can not load WikipediaStats from " + CreateMappingStats.serializeFileName)
        throw new FileNotFoundException("Can not load WikipediaStats from " + CreateMappingStats.serializeFileName)
    }

    private val mappings = getClassMappings()
    private val statistics = CreateMappingStats.countMappedStatistics(mappings, wikipediaStatistics)
    private val ignoreList: IgnoreList = CreateMappingStats.loadIgnorelist()

    @GET
    @Produces(Array("application/xhtml+xml"))
    def get =
    {
        var statsMap: Map[MappingStats, Int] = Map()
        for (mappingStat <- statistics)
        {
            statsMap += ((mappingStat, mappingStat.templateCount))
        }
        val ms: MappingStats = getMappingStats(WikiUtil.wikiDecode(template))
        if (ms.==(null))
        {
            throw new IllegalArgumentException("Could not find template: " + WikiUtil.wikiDecode(template))
        }
        else
        {
            val propMap: Map[String, (Int, Boolean)] = ms.properties
            val sortedPropMap = ListMap(propMap.toList.sortBy
            {
                case (key, (value1, value2)) => -value1
            }: _*)

            val percentageMappedProps: String = "%2.2f".format(ms.getRatioOfMappedProperties() * 100)
            val percentageMappedPropOccurrences: String = "%2.2f".format(ms.getRatioOfMappedPropertyOccurrences() * 100)
            println("ratioTemp: " + percentageMappedProps)
            println("ratioTempUses: " + percentageMappedPropOccurrences)
            <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
                <body>
                    <h2 align="center">Template Statistics (
                        {WikiUtil.wikiDecode(template)}
                        )</h2>
                    <p align="center">
                        {percentageMappedProps}
                        % properties are mapped (
                        {ms.getNumberOfMappedProperties()}
                        of
                        {ms.getNumberOfProperties()}
                        ).</p>
                    <p align="center">
                        {percentageMappedPropOccurrences}
                        % of all property occurrences in Wikipedia (
                        {langCode}
                        ) are mapped (
                        {ms.getNumberOfMappedPropertyOccurrences()}
                        of
                        {ms.getNumberOfPropertyOccurrences()}
                        ).</p>
                    <table align="center">
                    <caption>The color codes:</caption>
                    <tr>
                        <td bgcolor="#4E9258">property is mapped</td>
                    </tr>
                    <tr>
                        <td bgcolor="#C24641">property is not mapped</td>
                    </tr>
                    </table>
                    <table align="center">
                        <tr>
                            <td>occurrence</td> <td>property</td>
                        </tr>{for ((name, (occurrence, isMapped)) <- sortedPropMap) yield
                    {
                        var bgcolor: String = ""
                        if (isMapped)
                        {
                            bgcolor = "#4E9258"
                        }
                        else
                        {
                            bgcolor = "#C24641"
                        }

                        var isIgnored: Boolean = false
                        var ignoreMsg: String = "add to ignore list"
                        if (ignoreList.isPropertyIgnored(WikiUtil.wikiDecode(template), name))
                        {
                            isIgnored = true
                            ignoreMsg = "remove from ignore list"
                            bgcolor = "#808080"
                        }

                        <tr bgcolor={bgcolor}>
                            <td align="right">
                                {occurrence}
                            </td> <td>
                            {name}
                        </td>
                            <!--<td>
                                <a href={WikiUtil.wikiEncode(template) + "/" + WikiUtil.wikiEncode(name) + "/" + isIgnored.toString()}>
                                    {ignoreMsg}
                                </a>
                            </td>-->
                        </tr>
                    }}
                    </table>
                </body>
            </html>
        }
    }

    @GET
    @Path("/{property}/{ignorelist}")
    @Produces(Array("application/xhtml+xml"))
    def ignoreListAction(@PathParam("property") property: String, @PathParam("ignorelist") ignored: String) =
    {
        if (ignored == "true")
        {
            ignoreList.removeProperty(WikiUtil.wikiDecode(template), WikiUtil.wikiDecode(property))
            <h2>removed from ignore list</h2>
        }
        else
        {
            ignoreList.addProperty(WikiUtil.wikiDecode(template), WikiUtil.wikiDecode(property))
            <h2>added to ignore list</h2>
        }
        val html =
            <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
                <head>
                    <script type="text/javascript">

                   window.location.href="http://localhost:9999/statistics/en/{template}/";

                    </script>
                </head>
                <body>
                </body>
            </html>
        CreateMappingStats.saveIgnorelist(ignoreList)
        html
    }

    def getMappingStats(templateName: String) =
    {
        var mapStat: MappingStats = null
        for (mappingStat <- statistics)
        {
            if (mappingStat.templateName.contentEquals(templateName))
            {
                mapStat = mappingStat
            }
        }
        mapStat
    }

    def getClassMappings() =
    {
        val (templateMappings, tableMappings, conditionalMappings) = MappingsLoader.load(new ServerExtractionContext(language, Server.extractor))
        templateMappings ++ conditionalMappings
    }
}