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
package net.anshulverma.skydns.service;

import net.anshulverma.skydns.SkydnsConfig;
import net.anshulverma.skydns.SkydnsConnection;
import net.anshulverma.skydns.SkydnsHost;
import net.anshulverma.skydns.error.RemoteConnectionException;
import net.anshulverma.skydns.error.SerializationException;
import static java.util.Arrays.asList;

/**
 * @author anshul.verma86@gmail.com (Anshul Verma)
 */
public class ServiceRegistrator {

  private final SkydnsConnection connection;
  private final SkydnsConfig     config;

  public ServiceRegistrator(SkydnsConnection connection, SkydnsConfig config) {
    this.connection = connection;
    this.config = config;
  }

  public ServiceRegistry register(SkydnsHost skydnsHost, String... names)
    throws RemoteConnectionException, SerializationException {

    return register(skydnsHost, asList(names).stream()
                                             .reduce(new ServiceDomain(config.getDomain()),
                                                     ServiceDomain::subDomain,
                                                     ServiceDomain::combine));
  }

  public ServiceRegistry register(SkydnsHost skydnsHost, ServiceDomain domainName)
    throws RemoteConnectionException, SerializationException {

    return ServiceRegistry.builder()
                          .domainName(domainName)
                          .skydnsHost(connection.set(domainName.getPath(), skydnsHost))
                          .build();
  }
}
