/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.api;

import java.io.Closeable;

/**
 * An abstraction for writing archive entries to an archive file.
 *
 * @see ArchiveInput
 * @author Christian Schlichtherle
 */
public interface ArchiveOutput<E> extends Closeable {

    /** Returns {@code true} if and only if this is a JAR file. */
    boolean isJar();

    /**
     * Returns a sink for writing the archive entry with the given name.
     *
     * @param name the archive entry name, where elements are separated by {@code /} and directory names end with a
     *             {@code /}.
     * @throws IllegalArgumentException if the normalized form of the given archive entry name is absolute or empty or
     *                                  has {@code ..} as its first path segment.
     * @see ArchiveEntryNames#requireInternal(String)
     */
    ArchiveEntrySink<E> sink(String name);
}
