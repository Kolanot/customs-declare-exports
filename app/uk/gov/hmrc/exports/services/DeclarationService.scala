/*
 * Copyright 2021 HM Revenue & Customs
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

import javax.inject.Inject
import uk.gov.hmrc.exports.models._
import uk.gov.hmrc.exports.models.declaration.ExportsDeclaration
import uk.gov.hmrc.exports.repositories.DeclarationRepository

import scala.concurrent.Future

class DeclarationService @Inject()(declarationRepository: DeclarationRepository) {

  def create(declaration: ExportsDeclaration): Future[ExportsDeclaration] =
    declarationRepository.create(declaration)

  def update(declaration: ExportsDeclaration): Future[Option[ExportsDeclaration]] =
    declarationRepository.update(declaration)

  def find(search: DeclarationSearch, pagination: Page, sort: DeclarationSort): Future[Paginated[ExportsDeclaration]] =
    declarationRepository.find(search, pagination, sort)

  def findOne(id: String, eori: Eori): Future[Option[ExportsDeclaration]] = declarationRepository.find(id, eori)

  def deleteOne(declaration: ExportsDeclaration): Future[Unit] = declarationRepository.delete(declaration)

}
