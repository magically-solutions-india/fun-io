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
package global.namespace.fun.io.xz;

import global.namespace.fun.io.api.Filter;
import org.tukaani.xz.FilterOptions;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.UnsupportedOptionsException;

import static java.util.Objects.requireNonNull;
import static org.tukaani.xz.LZMA2Options.PRESET_DEFAULT;

/**
 * This facade provides static factory methods for XZ filters.
 *
 * @author Christian Schlichtherle
 */
public final class XZ {

    private XZ() { }

    /** Returns a filter which compresses/decompresses data using the LZMA format. */
    public static Filter lzma() { return new LZMAFilter(); }

    /**
     * Returns a filter which compresses/decompresses data using the LZMA2 format with the default preset.
     * This method is equivalent to {@code xz(new LZMA2Options())}.
     */
    public static Filter lzma2() { return lzma2(PRESET_DEFAULT); }

    /**
     * Returns a filter which compresses/decompresses data using the LZMA2 format with the given preset.
     * This method is equivalent to {@code xz(new LZMA2Options(preset))}.
     */
    public static Filter lzma2(final int preset) {
        try {
            return xz(new LZMA2Options(preset));
        } catch (UnsupportedOptionsException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Returns a filter which compresses/decompresses data using the XZ format.
     * This method is equivalent to {@code xz(o, org.tukaani.xz.XZ.CHECK_CRC64)}.
     */
    public static Filter xz(FilterOptions o) { return xz(o, org.tukaani.xz.XZ.CHECK_CRC64); }

    /**
     * Returns a filter which compresses/decompresses data using the XZ format.
     * This method is equivalent to {@code xz(new FilterOptions[] { o }, checkType)}.
     * <p>
     * This method does not check the integrity of the provided parameters.
     * Any error will only be detected when the transformed output stream socket gets used.
     *
     * @param checkType the type of the integrity check, e.g. @{code org.tukaani.xz.XZ.CHECK_CRC32}.
     */
    public static Filter xz(FilterOptions o, int checkType) {
        return xz(new FilterOptions[] { requireNonNull(o) }, checkType);
    }

    /**
     * Returns a filter which compresses/decompresses data using the XZ format.
     * This method is equivalent to {@code xz(o, org.tukaani.xz.XZ.CHECK_CRC64)}.
     * <p>
     * This method does not check the integrity of the provided parameter.
     * Any error will only be detected when the transformed output stream socket gets used.
     */
    public static Filter xz(FilterOptions[] o) { return xz(o, org.tukaani.xz.XZ.CHECK_CRC64); }

    /**
     * Returns a filter which compresses/decompresses data using the XZ format.
     * <p>
     * This method does not check the integrity of the provided parameters.
     * Any error will only be detected when the transformed output stream socket gets used.
     *
     * @param checkType the type of the integrity check, e.g. @{code org.tukaani.xz.XZ.CHECK_CRC32}.
     */
    public static Filter xz(FilterOptions[] o, int checkType) { return new XZFilter(o, checkType); }
}
