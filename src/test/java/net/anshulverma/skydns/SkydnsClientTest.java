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
import net.anshulverma.skydns.error.DeserializationException;
import net.anshulverma.skydns.error.RemoteConnectionException;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author anshul.verma86@gmail.com (Anshul Verma)
 */
@RunWith(MockitoJUnitRunner.class)
public class SkydnsClientTest {

  @Mock
  private SkydnsEtcdClient mockEtcdClient;

  private SkydnsClient skydnsClient;

  @Before
  public void setUp() throws Exception {
    skydnsClient = new SkydnsClient(new SkydnsConnection(mockEtcdClient));
  }

  @Test
  public void testSkydnsEmptyConfigRetrieval() throws RemoteConnectionException, DeserializationException {
    when(mockEtcdClient.get(anyString())).thenReturn("{}");
    SkydnsConfig config = skydnsClient.getConfig();
    Assert.assertEquals("skydns client returned unexpected configuration",
                        new SkydnsConfig(),
                        config);
  }

  @Test
  public void testSkydnsSampleConfigRetrieval() throws Exception {
    ClassLoader classLoader = getClass().getClassLoader();
    String configJson = IOUtils.toString(classLoader.getResourceAsStream("fixtures/skydns_sample_config.json"));
    when(mockEtcdClient.get(anyString())).thenReturn(configJson);
    SkydnsConfig config = skydnsClient.getConfig();
    Assert.assertEquals("skydns client returned unexpected configuration",
                        new SkydnsConfig("192.168.10.12:53",
                                         "hostmaster@example.com",
                                         "example.com.",
                                         Lists.newArrayList("8.8.8.8:53", "4.4.4.4:53"),
                                         3600),
                        config);
  }
}
