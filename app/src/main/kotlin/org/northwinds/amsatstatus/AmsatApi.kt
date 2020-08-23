package org.northwinds.amsatstatus

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

const val AMSAT_API_URL = "https://amsat.org/status/api/v1/sat_info.php"

class AmsatApi(client: CloseableHttpClient) {

    val client = client

    constructor() : this(HttpClients.createDefault())

    fun getReport(name: String, hours: Int) {
        val charset = Charset.forName(StandardCharsets.UTF_8.name())
        val uri = URIBuilder(AMSAT_API_URL, charset)
        uri.addParameter("name", name)
        uri.addParameter("hours", hours.toString())
        val httpGet = HttpGet(uri.build())
        this.client.execute(httpGet).use { response1 ->
            println(response1.getStatusLine())
            val entity1 = response1.getEntity()
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity1)
        }
    }
}
