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
package global.namespace.fun.io.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import global.namespace.fun.io.api.Codec;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * This facade provides static factory methods for a JSON codec.
 *
 * @author Christian Schlichtherle
 */
public final class Jackson {

    /**
     * Returns a JSON codec using a new object mapper.
     */
    public static Codec json() {
        return json(ObjectMapper::new);
    }

    /**
     * Returns a JSON codec using the given object mapper.
     *
     * @deprecated since Fun I/O 2.2.0 because {@code ObjectMapper} is mutable and therefore should not be shared with
     *             the caller.
     * @see #json(Supplier)
     */
    @Deprecated
    public static Codec json(ObjectMapper mapper) {
        return json(() -> mapper);
    }

    /**
     * Returns a JSON codec using the given object mapper factory.
     */
    public static Codec json(Supplier<ObjectMapper> factory) {
        return new JsonCodec(requireNonNull(factory.get()));
    }

    private Jackson() {
    }
}
