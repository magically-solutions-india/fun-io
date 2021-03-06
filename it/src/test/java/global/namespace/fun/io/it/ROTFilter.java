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
package global.namespace.fun.io.it;

import global.namespace.fun.io.api.Filter;
import global.namespace.fun.io.api.Socket;

import java.io.*;

/**
 * @author Christian Schlichtherle
 */
public class ROTFilter implements Filter {

    private static final int ALPHABET_LENGTH = 'Z' - 'A' + 1;

    private int[] apply = new int[ALPHABET_LENGTH], unapply = new int[ALPHABET_LENGTH];

    public ROTFilter() {
        this(13);
    }

    public ROTFilter(final int positions) {
        if (positions < 1 || ALPHABET_LENGTH - 1 < positions) {
            throw new IllegalArgumentException(positions + " is not in the range from 1 to " + (ALPHABET_LENGTH - 1) + ".");
        }
        for (int i = 0; i < ALPHABET_LENGTH; i++) {
            apply[i] = (i + positions) % ALPHABET_LENGTH;
            unapply[i] = (ALPHABET_LENGTH + i - positions) % ALPHABET_LENGTH;
        }
    }

    @Override
    public Socket<OutputStream> output(Socket<OutputStream> output) {
        return output.map(out -> new FilterOutputStream(out) {

            @Override
            public void write(int b) throws IOException {
                out.write(apply(b));
            }

            @Override
            public void write(final byte[] b, final int off, final int len) throws IOException {
                if ((off | len | (b.length - (len + off)) | (off + len)) < 0) {
                    throw new IndexOutOfBoundsException();
                }
                final byte[] rotated = new byte[len];
                for (int i = 0; i < len; i++) {
                    rotated[i] = (byte) apply(b[off + i]);
                }
                out.write(rotated);
            }
        });
    }

    @Override
    public Socket<InputStream> input(Socket<InputStream> input) {
        return input.map(in -> new FilterInputStream(in) {

            @Override
            public int read() throws IOException {
                return unapply(in.read());
            }

            @Override
            public int read(final byte[] b, final int off, final int len) throws IOException {
                int read = in.read(b, off, len);
                for (int i = 0; i < read; i++) {
                    b[off + i] = (byte) unapply(b[off + i]);
                }
                return read;
            }
        });
    }

    private int apply(int b) {
        return rotate(b, apply);
    }

    private int unapply(int b) {
        return rotate(b, unapply);
    }

    private static int rotate(final int b, int[] map) {
        if ('A' <= b && b <= 'Z') {
            return 'A' + map[b - 'A'];
        } else if ('a' <= b && b <= 'z') {
            return 'a' + map[b - 'a'];
        } else {
            return b;
        }
    }
}
