/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.exports.services

import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.mvc.Result
import play.api.mvc.Results._
import uk.gov.hmrc.exports.metrics.ExportsMetrics
import uk.gov.hmrc.exports.metrics.MetricIdentifiers.notificationMetric
import uk.gov.hmrc.exports.models.declaration.Submission
import uk.gov.hmrc.exports.models.declaration.notifications.Notification
import uk.gov.hmrc.exports.repositories.{NotificationsRepository, SubmissionRepository}
import uk.gov.hmrc.wco.dec.Response

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class NotificationService @Inject()(
  submissionRepository: SubmissionRepository,
  notificationsRepository: NotificationsRepository,
  metrics: ExportsMetrics
) {

  private val logger = Logger(this.getClass)

  def save(notification: Notification): Future[Result] = {
//    val eori = notification.eori
    val convId = notification.conversationId

    for {
      oldNotification <- getTheNewestExistingNotification(convId)
      notificationSaved <- notificationsRepository.save(notification)
      shouldBeUpdated = true // oldNotification.forall(notification.isOlderThan)
      _ <- if (shouldBeUpdated) updateMrnAndStatus(notification) else Future.successful(None)
    } yield
      if (notificationSaved) {
        metrics.incrementCounter(notificationMetric)
        logger.debug("Notification saved successfully")
        Accepted
      } else {
        metrics.incrementCounter(notificationMetric)
        logger.error("There was a problem during saving notification")
        Accepted
      }
  }

  def getTheNewestExistingNotification(convId: String): Future[Option[Notification]] = ???

  private def updateMrnAndStatus(notification: Notification): Future[Option[Submission]] =
    submissionRepository.updateMrn("", notification.conversationId)(notification.mrn)

  val PositionFunctionCode = "11"
  val NameCodeGranted = "39"
  val NameCodeDenied = "41"

  private def buildStatus(responses: Seq[Response]): Option[String] =
    responses.map { response =>
      (response.functionCode, response.status.flatMap(_.nameCode).headOption) match {
        case (PositionFunctionCode, Some(nameCode)) if nameCode == NameCodeGranted || nameCode == NameCodeDenied =>
          s"11$nameCode"
        case _ => response.functionCode
      }
    }.headOption
}
