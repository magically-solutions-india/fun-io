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
package global.namespace.fun.io.bios;

import global.namespace.fun.io.api.Codec;
import global.namespace.fun.io.api.Decoder;
import global.namespace.fun.io.api.Encoder;
import global.namespace.fun.io.api.Socket;
import global.namespace.fun.io.api.function.XFunction;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.InputStream;
import java.io.OutputStream;

final class XMLCodec implements Codec {

    private final XFunction<? super OutputStream, ? extends XMLEncoder> xmlEncoders;
    private final XFunction<? super InputStream, ? extends XMLDecoder> xmlDecoders;

    XMLCodec(final XFunction<? super OutputStream, ? extends XMLEncoder> xmlEncoders,
             final XFunction<? super InputStream, ? extends XMLDecoder> xmlDecoders) {
        this.xmlEncoders = xmlEncoders;
        this.xmlDecoders = xmlDecoders;
    }

    @Override
    public Encoder encoder(final Socket<OutputStream> output) {
        return obj -> {
            final ZeroToleranceListener ztl = new ZeroToleranceListener();
            output.map(xmlEncoders).accept(enc -> {
                enc.setExceptionListener(ztl);
                enc.writeObject(obj);
            });
            ztl.check();
        };
    }

    @Override
    public Decoder decoder(final Socket<InputStream> input) {
        return new Decoder() {
            @Override
            @SuppressWarnings("unchecked")
            public <T> T decode(final Class<T> expected) throws Exception {
                final ZeroToleranceListener ztl = new ZeroToleranceListener();
                try {
                    return input.map(xmlDecoders).apply(dec -> {
                        dec.setExceptionListener(ztl);
                        return (T) dec.readObject();
                    });
                } finally {
                    ztl.check();
                }
            }
        };
    }

    private static class ZeroToleranceListener implements ExceptionListener {

        Exception e;

        @Override
        public void exceptionThrown(final Exception e) {
            if (null == this.e) {
                this.e = e; // don't overwrite prior exception
            }
        }

        void check() throws Exception {
            if (null != e) {
                throw e;
            }
        }
    }
}
