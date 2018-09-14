/*
 * Copyright © 2017 Schlichtherle IT Services
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package global.namespace.fun.io.it.bios

import java.util.zip.ZipEntry

import global.namespace.fun.io.bios.BIOS
import global.namespace.fun.io.it.DiffAndPatchSpecSuite

class JarDiffAndPatchSpec extends DiffAndPatchSpecSuite[ZipEntry] {

  override def archiveFileFactory: ArchiveFileFactory[ZipEntry] = BIOS.jar
}