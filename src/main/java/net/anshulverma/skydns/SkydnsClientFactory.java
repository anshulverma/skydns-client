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
import net.anshulverma.skydns.service.ServiceRegistrator;

/**
 * A factory class to get an instance of {@link net.anshulverma.skydns.SkydnsClient} for a
 * particular skydns endpoint and a particular domain.
 *
 * @author anshul.verma86@gmail.com (Anshul Verma)
 */
public class SkydnsClientFactory {

  /**
   * Create a new {@link net.anshulverma.skydns.SkydnsClient} backed by etcd.
   *
   * @param etcdMachines An array of host address strings pointing to etcd machines.
   * @return A newly created instance of {@link net.anshulverma.skydns.SkydnsClient}.
   * @throws RemoteConnectionException If unable to connect to etcd.
   * @throws SerializationException    I unable to deserialize the
   *                                   {@link net.anshulverma.skydns.SkydnsConfig}
   */
  public static SkydnsClient newClient(String... etcdMachines)
      throws RemoteConnectionException, SerializationException {

    SkydnsConnection connection = SkydnsConnection.builder()
                                                  .endpoints(etcdMachines)
                                                  .build();
    ServiceRegistrator registrator =
        new ServiceRegistrator(connection, connection.get("config", SkydnsConfig.class));

    return new SkydnsClient(connection, registrator);
  }
}
