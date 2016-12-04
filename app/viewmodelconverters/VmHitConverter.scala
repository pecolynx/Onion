package viewmodelconverters

import com.kujilabo.models.elasticsearch.Hit
import viewmodels.VmHit

object VmHitConverter {

  def toViewModel(hit: Hit): VmHit = {
    new VmHit(VmDocumentConverter.toViewModel(hit.document), hit.title, hit.score)
  }
}
