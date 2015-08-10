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
import java.util.concurrent.TimeoutException;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;
import net.anshulverma.skydns.error.RemoteConnectionException;

/**
 * @author anshul.verma86@gmail.com (Anshul Verma)
 */
public class SkydnsEtcdClient {

  private static final String SKYDNS_ROOT_KEY = "skydns/";

  private final EtcdClient etcdClient;

  public SkydnsEtcdClient(EtcdClient etcdClient) {
    this.etcdClient = etcdClient;
  }

  public String get(String key) throws RemoteConnectionException {
    try {
      EtcdResponsePromise<EtcdKeysResponse> promise = etcdClient.get(path(key)).send();
      return promise.get().node.value;
    } catch (TimeoutException | EtcdException | IOException e) {
      throw new RemoteConnectionException("unable to get value of key '" + path(key) + "'", e);
    }
  }

  public String set(String key, String value) throws RemoteConnectionException {
    try {
      EtcdResponsePromise<EtcdKeysResponse> promise = etcdClient.post(path(key), value).send();
      return promise.get().node.value;
    } catch (IOException | EtcdException | TimeoutException e) {
      throw new RemoteConnectionException("unable to set value for key '" + path(key) + "'", e);
    }
  }

  private String path(String key) {
    return SKYDNS_ROOT_KEY + key;
  }
}
