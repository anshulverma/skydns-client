/**
 * Copyright © 2015 Anshul Verma. All Rights Reserved.
 *
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.anshulverma.skydns;

import net.anshulverma.skydns.error.RemoteConnectionException;
import net.anshulverma.skydns.error.SerializationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mousio.etcd4j.EtcdClient;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

/**
 * Keeps track of the connection parameters (like endpoint and domain) for skydns.
 *
 * @author anshul.verma86@gmail.com (Anshul Verma)
 */
public class SkydnsConnection {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private SkydnsEtcdClient client;

  public SkydnsConnection(SkydnsEtcdClient client) {
    this.client = client;
  }

  public static SkydnsConnectionBuilder builder() {
    return new SkydnsConnectionBuilder();
  }

  /**
   * Get value for a node in etcd and deserialize it to a type.
   *
   * {@inheritDoc}
   */
  public <T> T get(String key, Class<T> clazz)
    throws RemoteConnectionException, SerializationException {

    String value = client.get(key);
    return deserialize(value, clazz);
  }

  public <T> T set(String key, T value) throws RemoteConnectionException, SerializationException {
    String response = client.set(key, serialize(value));
    return deserialize(response, value.getClass());
  }

  private String serialize(Object value) throws SerializationException {
    try {
      return OBJECT_MAPPER.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new SerializationException(
        "unable to write value for type: " + value.getClass().getCanonicalName(), e);
    }
  }

  @SuppressWarnings("unchecked")
  private <T> T deserialize(String value, Class clazz) throws SerializationException {
    try {
      return (T) OBJECT_MAPPER.readValue(value, clazz);
    } catch (IOException e) {
      throw new SerializationException(
        "read error for type: " + clazz.getCanonicalName() + " -- value: " + value, e);
    }
  }

  static class SkydnsConnectionBuilder {

    private String[] etcdMachines;

    public SkydnsConnectionBuilder endpoints(String[] etcdMachines) {
      this.etcdMachines = etcdMachines;
      return this;
    }

    public SkydnsConnection build() {
      EtcdClient client = new EtcdClient(Arrays.stream(etcdMachines)
                                               .map(URI::create)
                                               .toArray(URI[]::new));
      return new SkydnsConnection(new SkydnsEtcdClient(client));
    }
  }
}
