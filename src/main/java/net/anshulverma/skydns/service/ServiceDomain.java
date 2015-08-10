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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.google.common.collect.Lists;

/**
 * @author anshul.verma86@gmail.com (Anshul Verma)
 */
public class ServiceDomain {

  private static final String DEFAULT_DOMAIN = "skydns.local";

  private final String domain;

  public ServiceDomain(String domain) {
    if (StringUtils.isBlank(domain)) {
      this.domain = DEFAULT_DOMAIN;
    } else {
      this.domain = domain;
    }
  }

  public ServiceDomain subDomain(String name) {
    return new ServiceDomain(name + "." + domain);
  }

  public String getPath() {
    List<String> hostnameParts = Lists.reverse(Arrays.asList(domain.split("\\.")));
    return hostnameParts.stream()
                        .filter(StringUtils::isNotBlank)
                        .collect(Collectors.joining("/"));
  }

  public ServiceDomain combine(ServiceDomain other) {
    return subDomain(other.domain);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }
}
