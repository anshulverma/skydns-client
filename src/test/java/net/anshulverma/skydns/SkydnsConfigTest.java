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
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author anshul.verma86@gmail.com (Anshul Verma)
 */
@RunWith(MockitoJUnitRunner.class)
public class SkydnsConfigTest {

  @Mock
  private SkydnsEtcdClient mockEtcdClient;

  private SkydnsClient skydnsClient;

  @Before
  public void setUp() throws Exception {
    skydnsClient = new SkydnsClient(new SkydnsConnection(mockEtcdClient), null);
  }

  @Test
  public void testSkydnsEmptyConfigRetrieval()
      throws RemoteConnectionException, SerializationException {

    when(mockEtcdClient.get(anyString())).thenReturn("{}");
    SkydnsConfig config = skydnsClient.getConfig();
    Assert.assertEquals("skydns client returned unexpected configuration",
                        new SkydnsConfig(),
                        config);
  }

  @Test
  public void testSkydnsSampleConfigRetrieval() throws Exception {
    ClassLoader classLoader = getClass().getClassLoader();
    String configJson =
        IOUtils.toString(classLoader.getResourceAsStream("fixtures/skydns_sample_config.json"));
    when(mockEtcdClient.get(anyString())).thenReturn(configJson);
    SkydnsConfig config = skydnsClient.getConfig();
    Assert.assertEquals("skydns client returned unexpected configuration",
                        SkydnsConfig.builder()
                                    .dnsAddress("192.168.10.12:53")
                                    .hostmaster("hostmaster@example.com")
                                    .domain("example.com.")
                                    .nameservers(Lists.newArrayList("8.8.8.8:53", "4.4.4.4:53"))
                                    .timeToLive(3600)
                                    .build(),
                        config);
  }
}
