/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huaweicloud.sample;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.huaweicloud.common.schema.ServiceCombSwaggerHandler;

import io.swagger.models.Swagger;
import io.swagger.util.Yaml;


/**
 * Class for testing schema generator
 */
@RestController
public class SchemaController {
  @Autowired
  ServiceCombSwaggerHandler serviceCombSwaggerHandler;

  @Autowired
  private RestTemplate restTemplate;

  @RequestMapping("/testSchemaGeneratorSpringCloud")
  public String testSchemaGeneratorSpringCloud() {
    return restTemplate.getForObject("http://price/testSchemaGenerator", String.class);
  }

  @RequestMapping("/testSchemaGeneratorServiceComb")
  public String testSchemaGeneratorServiceComb() throws Exception {
    List<String> schemas = serviceCombSwaggerHandler.getSchemaIds();
    assertThat(schemas.size()).isGreaterThan(3);
    Map<String, String> schemaContents = serviceCombSwaggerHandler.getSchemasMap();
    assertThat(schemaContents.size()).isGreaterThan(3);

    String a1 = schemaContents.get("SchemaContentController");
    String a2 = readFile("SchemaContentController.yaml");

    Swagger swagger2 = Yaml.mapper().readValue(a2, Swagger.class);
    Swagger swagger1 = Yaml.mapper().readValue(a1, Swagger.class);
    if (swagger1.equals(swagger2)) {
      return "success";
    } else {
      return a1;
    }
  }

  private String readFile(String restController) {
    // test code, make simple
    try {
      InputStream inputStream = this.getClass().getResource("/" + restController).openStream();
      byte[] buffer = new byte[2048 * 10];
      int len = inputStream.read(buffer);
      assertThat(len).isLessThan(2048 * 10);
      inputStream.close();
      return new String(buffer, 0, len, Charset.forName("UTF-8"));
    } catch (IOException e) {
      Assertions.fail(e.getMessage());
      return null;
    }
  }
}
