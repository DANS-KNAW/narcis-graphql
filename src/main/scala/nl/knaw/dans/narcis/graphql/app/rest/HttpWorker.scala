/**
 * Copyright (C) 2019 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.narcis.graphql.app.rest

import nl.knaw.dans.lib.logging.DebugEnhancedLogging
import nl.knaw.dans.narcis.graphql.HttpException
import org.json4s._
import org.json4s.native.{JsonMethods, Serialization}
import scalaj.http.BaseHttp

import scala.util.Try

class HttpWorker(appVersion: String) extends DebugEnhancedLogging {
  object Http extends BaseHttp(userAgent = s"narcis-graphql/$appVersion")

  def getJsonData[T: Manifest](url: String, connTimeoutMs: Int = Int.MaxValue,
                               readTimeoutMs: Int = Int.MaxValue)
                              (implicit formats: Formats): Try[T] = Try {
    logger.info(s"fetching data from $url")

    val response = Http(url)
      .timeout(connTimeoutMs, readTimeoutMs)
      .asString

    if (response.isSuccess) {
      val body = response.body

      if (logger.underlying.isDebugEnabled)
        logger.debug(s"fetched body:\n$body")

      JsonMethods.parse(body).extract[T]
    }
    else {
      if (logger.underlying.isDebugEnabled)
        logger.debug(s"fetching data from $url failed: ${ response.code } - ${ response.body }")

      throw HttpException("GET", url, response.code, response.body)
    }
  }

  def postJsonData[T <: AnyRef](url: String, data: T, connTimeoutMs: Int = Int.MaxValue,
                                readTimeoutMs: Int = Int.MaxValue)
                               (implicit formats: Formats): Try[Unit] = Try {
    logger.info(s"posting data to $url")

    val response = Http(url)
      .timeout(connTimeoutMs, readTimeoutMs)
      .postData(Serialization.write[T](data))
      .header("content-type", "application/json")
      .asString

    if (response.isSuccess) {
      val body = response.body

      if (logger.underlying.isDebugEnabled)
        logger.debug(s"fetched body:\n$body")
    }
    else {
      if (logger.underlying.isDebugEnabled)
        logger.debug(s"posting data to $url failed: ${ response.code } - ${ response.body }")

      throw HttpException("POST", url, response.code, response.body)
    }
  }
}
