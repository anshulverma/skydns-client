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
import net.anshulverma.skydns.service.ServiceRegistry;

/**
 * @author anshul.verma86@gmail.com (Anshul Verma)
 */
public class SkydnsClient {

  private final SkydnsConnection   connection;
  private final ServiceRegistrator registrator;

  public SkydnsClient(SkydnsConnection connection, ServiceRegistrator registrator) {
    this.connection = connection;
    this.registrator = registrator;
  }

  public SkydnsConfig getConfig() throws SerializationException, RemoteConnectionException {
    return connection.get("config", SkydnsConfig.class);
  }

  public ServiceRegistry register(SkydnsHost skydnsHost, String... names)
    throws SerializationException, RemoteConnectionException {

    return registrator.register(skydnsHost, names);
  }
}
