package global.namespace.fun.io.it

import java.io.File

import global.namespace.fun.io.api.{ArchiveInput, ArchiveSource, ArchiveStore}
import global.namespace.fun.io.bios.BIOS
import global.namespace.fun.io.bios.BIOS.copy
import global.namespace.fun.io.commons.compress.CommonsCompress
import global.namespace.fun.io.commons.compress.CommonsCompress.jar
import global.namespace.fun.io.delta.Delta.diff
import global.namespace.fun.io.it.ArchiveTransformationSpec._
import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.prop.PropertyChecks._
import org.scalatest.prop.TableFor1

import scala.collection.JavaConverters._

class ArchiveTransformationSpec extends WordSpec {

  "Transforming an archive file from one format to another" should {
    "be lossless" in {
      forAllArchives { original => { implicit factory =>
        withTempArchive { second: ArchiveStore[_] => withTempArchive { clone: ArchiveStore[_] =>
          copy(original, second)
          copy(second, clone)

          val originalEntries: Set[String]  = original applyReader {
            (_: ArchiveInput[_]).asScala.filterNot(_.isDirectory).map(_.name).toSet
          }

          val model = (diff base original update clone).toModel
          model.changedEntries shouldBe empty
          model.addedEntries shouldBe empty
          model.removedEntries shouldBe empty
          model.unchangedEntries.asScala.map(_.name).toSet shouldBe originalEntries
        }(jar)}
      }}
    }
  }
}

private object ArchiveTransformationSpec {

  type ArchiveStoreFactory[E] = File => ArchiveStore[E]

  def forAllArchives(test: ArchiveSource[_] => ArchiveStoreFactory[_] => Any): Unit = {
    forAll(Factories)(factory => test(jar(Test1JarFile))(factory))
    forAll(Factories)(factory => test(jar(Test2JarFile))(factory))
  }

  private val Factories: TableFor1[ArchiveStoreFactory[_]] = Table(
    "archive store factory",
    CommonsCompress.tar _,
    CommonsCompress.jar _,
    CommonsCompress.zip _,
    BIOS.jar _,
    BIOS.zip _
  )

  def withTempArchive[E](test: ArchiveStore[E] => Any)(implicit factory: ArchiveStoreFactory[E]): Unit = {
    val file = File.createTempFile("temp", null)
    file delete ()
    try {
      test(factory(file))
    } finally {
      deleteAll(file)
    }
  }

  private def deleteAll(file: File): Unit = {
    if (file.isDirectory) {
      file listFiles () foreach deleteAll
    }
    file delete ()
  }

  private lazy val Test1JarFile = resourceFile("test1.jar")

  private lazy val Test2JarFile = resourceFile("test2.jar")

  private def resourceFile(resourceName: String): File = {
    new File((classOf[DiffAndPatchSpec] getResource resourceName).toURI)
  }
}
