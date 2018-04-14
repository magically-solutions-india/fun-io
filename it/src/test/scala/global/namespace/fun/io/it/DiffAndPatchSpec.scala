/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.it

import java.io._
import java.security.MessageDigest

import global.namespace.fun.io.api.{ArchiveInput, ArchiveSource, ArchiveStore}
import global.namespace.fun.io.bios.BIOS
import global.namespace.fun.io.bios.BIOS._
import global.namespace.fun.io.commons.compress.CommonsCompress
import global.namespace.fun.io.delta.Delta._
import global.namespace.fun.io.delta.dto.DeltaDTO
import global.namespace.fun.io.delta.model.DeltaModel
import global.namespace.fun.io.it.DiffAndPatchSpec._
import global.namespace.fun.io.scala.api._
import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.prop.PropertyChecks._
import org.scalatest.prop.TableFor1

import scala.collection.JavaConverters._

/** @author Christian Schlichtherle */
class DiffAndPatchSpec extends WordSpec {

  "Diffing two archive files and patching the first with the delta" should {
    "produce a clone of the second archive file" in {
      forAllArchives { (first, second) => { implicit factory =>
        withTempArchive { delta => withTempArchive { clone =>

          diff first first second second digest sha1 to delta
          patch base first delta delta to clone

          val secondEntries: Set[String] = second applyReader {
            (_: ArchiveInput[_]).asScala.filterNot(_.isDirectory).map(_.name).toSet
          }

          val model = (diff base second update clone digest md5).toModel
          model.changedEntries shouldBe empty
          model.addedEntries shouldBe empty
          model.removedEntries shouldBe empty
          model.unchangedEntries.asScala.map(_.name).toSet shouldBe secondEntries
        }}
      }}
    }
  }
}

private object DiffAndPatchSpec {

  type ArchiveStoreFactory[E] = File => ArchiveStore[E]

  def forAllArchives(test: (ArchiveSource[_], ArchiveSource[_]) => ArchiveStoreFactory[_] => Any): Unit = {
    test(directory(deltaModelDirectory), directory(deltaDtoDirectory))(directory(_: File))
    forAll(Factories)(factory => test(factory(Test1JarFile), factory(Test2JarFile))(factory))
  }

  private val Factories: TableFor1[ArchiveStoreFactory[_]] = Table(
    "archive file store factory",
    CommonsCompress.jar(_: File),
    CommonsCompress.zip(_: File),
    BIOS.jar(_: File),
    BIOS.zip(_: File)
  )

  def withTempArchive(test: ArchiveStore[_] => Any)(implicit factory: ArchiveStoreFactory[_]): Unit = {
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

  private lazy val deltaModelDirectory = new File((classOf[DeltaModel] getResource "").toURI)

  private lazy val deltaDtoDirectory = new File((classOf[DeltaDTO] getResource "").toURI)

  private lazy val Test1JarFile = resourceFile("test1.jar")

  private lazy val Test2JarFile = resourceFile("test2.jar")

  private def resourceFile(resourceName: String): File = {
    new File((classOf[DiffAndPatchSpec] getResource resourceName).toURI)
  }

  def sha1: MessageDigest = MessageDigest getInstance "SHA-1"

  def md5: MessageDigest = MessageDigest getInstance "MD5"
}
