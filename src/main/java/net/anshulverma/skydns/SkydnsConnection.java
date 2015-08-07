/**
 * Copyright 2015 Anshul Verma. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.anshulverma.skydns;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import com.fasterxml.jackson.databind.ObjectMapper;
import mousio.etcd4j.EtcdClient;
import net.anshulverma.skydns.error.DeserializationException;
import net.anshulverma.skydns.error.RemoteConnectionException;

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

  public <T> T get(String key, Class<T> clazz) throws RemoteConnectionException, DeserializationException {
    String value = client.get(key);
    try {
      return OBJECT_MAPPER.readValue(value, clazz);
    } catch (IOException e) {
      throw new DeserializationException("read error for type: " + clazz.getCanonicalName() + " -- value: " + value, e);
    }
  }

  static class SkydnsConnectionBuilder {

    private String[] endpoints;

    public SkydnsConnectionBuilder endpoints(String[] endpoints) {
      this.endpoints = endpoints;
      return this;
    }

    public SkydnsConnection build() {
      EtcdClient client = new EtcdClient(Arrays.stream(endpoints)
                                               .map(URI::create)
                                               .toArray(URI[]::new));
      return new SkydnsConnection(new SkydnsEtcdClient(client));
    }
  }
}
