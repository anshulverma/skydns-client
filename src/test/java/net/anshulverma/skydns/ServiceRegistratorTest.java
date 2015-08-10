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

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.google.common.collect.Lists;
import net.anshulverma.skydns.service.ServiceRegistrator;
import net.anshulverma.skydns.service.ServiceRegistry;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author anshul.verma86@gmail.com (Anshul Verma)
 */
@RunWith(MockitoJUnitRunner.class)
public class ServiceRegistratorTest {

  @Mock
  private SkydnsEtcdClient mockEtcdClient;

  private SkydnsClient skydnsClient;

  @Before
  public void setUp() throws Exception {
    SkydnsConnection connection = new SkydnsConnection(mockEtcdClient);
    SkydnsConfig skydnsConfig = new SkydnsConfig("192.168.10.12:53",
                                                 "hostmaster@example.com",
                                                 "example.com.",
                                                 Lists.newArrayList("8.8.8.8:53", "4.4.4.4:53"),
                                                 3600);
    skydnsClient = new SkydnsClient(connection, new ServiceRegistrator(connection, skydnsConfig));
  }

  @Test
  public void testSkydnsEmptyConfigRetrieval() throws Exception {
    ClassLoader classLoader = getClass().getClassLoader();
    String hostJson = IOUtils.toString(classLoader.getResourceAsStream("fixtures/service_host_sample.json"));
    when(mockEtcdClient.set(anyString(), anyString())).thenReturn(hostJson);
    SkydnsHost skydnsHost = SkydnsHost.builder()
                                      .host("192.168.65.54")
                                      .port(31001)
                                      .timeToLive(5)
                                      .build();
    ServiceRegistry serviceRegistry = skydnsClient.register(skydnsHost, "prod", "db");
    Assert.assertEquals("unexpected service host returned", skydnsHost, serviceRegistry.getSkydnsHost());
    verify(mockEtcdClient).set("com/example/prod/db",
                               "{\"host\":\"192.168.65.54\",\"port\":31001,\"priority\":0,\"weight\":0,\"ttl\":5}");
  }
}
