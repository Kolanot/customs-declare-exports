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

package uk.gov.hmrc.exports.models.declaration

import play.api.libs.json._

case class ProcedureCodes(procedureCode: Option[String], additionalProcedureCodes: Seq[String]) {
  def extractProcedureCode(): (Option[String], Option[String]) =
    (procedureCode.map(_.substring(0, 2)), procedureCode.map(_.substring(2, 4)))
}
object ProcedureCodes {
  implicit val format: OFormat[ProcedureCodes] = Json.format[ProcedureCodes]
}

case class FiscalInformation(onwardSupplyRelief: String)
object FiscalInformation {
  implicit val format: OFormat[FiscalInformation] = Json.format[FiscalInformation]
}

case class AdditionalFiscalReference(country: String, reference: String)
object AdditionalFiscalReference {
  implicit val format: OFormat[AdditionalFiscalReference] = Json.format[AdditionalFiscalReference]
}

case class AdditionalFiscalReferences(references: Seq[AdditionalFiscalReference])
object AdditionalFiscalReferences {
  implicit val format: OFormat[AdditionalFiscalReferences] = Json.format[AdditionalFiscalReferences]
}

case class ItemType(
  combinedNomenclatureCode: String,
  taricAdditionalCode: Seq[String],
  nationalAdditionalCode: Seq[String],
  descriptionOfGoods: String,
  cusCode: Option[String],
  unDangerousGoodsCode: Option[String],
  statisticalValue: String
)
object ItemType {
  implicit val format: OFormat[ItemType] = Json.format[ItemType]
}

sealed abstract class IdentificationTypeCodes(val value: String)
object IdentificationTypeCodes {
  case object CombinedNomenclatureCode extends IdentificationTypeCodes("TSP")
  case object TARICAdditionalCode extends IdentificationTypeCodes("TRA")
  case object NationalAdditionalCode extends IdentificationTypeCodes("GN")
  case object CUSCode extends IdentificationTypeCodes("CV")

  implicit object IdentificationTypeCodesReads extends Reads[IdentificationTypeCodes] {
    def reads(jsValue: JsValue): JsResult[IdentificationTypeCodes] = jsValue match {
      case JsString("TSP") => JsSuccess(CombinedNomenclatureCode)
      case JsString("TRA") => JsSuccess(TARICAdditionalCode)
      case JsString("GN")  => JsSuccess(NationalAdditionalCode)
      case JsString("CV")  => JsSuccess(CUSCode)
      case _               => JsError("Incorrect choice status")
    }
  }

  implicit object IdentificationTypeCodesWrites extends Writes[IdentificationTypeCodes] {
    def writes(code: IdentificationTypeCodes): JsValue = JsString(code.toString)
  }
}

case class PackageInformation(typesOfPackages: String, numberOfPackages: Int, shippingMarks: String)
object PackageInformation {
  implicit val format: OFormat[PackageInformation] = Json.format[PackageInformation]
}

case class CommodityMeasure(supplementaryUnits: Option[String], netMass: String, grossMass: String)
object CommodityMeasure {
  implicit val format: OFormat[CommodityMeasure] = Json.format[CommodityMeasure]
}

case class AdditionalInformation(code: String, description: String) {}
object AdditionalInformation {
  implicit val format: OFormat[AdditionalInformation] = Json.format[AdditionalInformation]
}

case class AdditionalInformations(items: Seq[AdditionalInformation])
object AdditionalInformations {
  implicit val format: OFormat[AdditionalInformations] = Json.format[AdditionalInformations]
}

case class DocumentIdentifierAndPart(documentIdentifier: Option[String], documentPart: Option[String])
object DocumentIdentifierAndPart {
  implicit val format: OFormat[DocumentIdentifierAndPart] = Json.format[DocumentIdentifierAndPart]
}

case class Date(day: Option[Int], month: Option[Int], year: Option[Int])
object Date {
  implicit val format: OFormat[Date] = Json.format[Date]
}

case class DocumentWriteOff(measurementUnit: Option[String], documentQuantity: Option[BigDecimal])
object DocumentWriteOff {
  implicit val format: OFormat[DocumentWriteOff] = Json.format[DocumentWriteOff]
}

case class DocumentProduced(
  documentTypeCode: Option[String],
  documentIdentifierAndPart: Option[DocumentIdentifierAndPart],
  documentStatus: Option[String],
  documentStatusReason: Option[String],
  issuingAuthorityName: Option[String],
  dateOfValidity: Option[Date],
  documentWriteOff: Option[DocumentWriteOff]
)
object DocumentProduced {
  implicit val format: OFormat[DocumentProduced] = Json.format[DocumentProduced]
}

case class DocumentsProduced(documents: Seq[DocumentProduced])

object DocumentsProduced {
  implicit val format: OFormat[DocumentsProduced] = Json.format[DocumentsProduced]
}

case class ExportItem(
  id: String,
  sequenceId: Int = 0,
  procedureCodes: Option[ProcedureCodes] = None,
  fiscalInformation: Option[FiscalInformation] = None,
  additionalFiscalReferencesData: Option[AdditionalFiscalReferences] = None,
  itemType: Option[ItemType] = None,
  packageInformation: List[PackageInformation] = Nil,
  commodityMeasure: Option[CommodityMeasure] = None,
  additionalInformation: Option[AdditionalInformations] = None,
  documentsProducedData: Option[DocumentsProduced] = None
)
object ExportItem {
  implicit val format: OFormat[ExportItem] = Json.format[ExportItem]
}