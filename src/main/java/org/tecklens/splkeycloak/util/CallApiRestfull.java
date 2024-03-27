package org.tecklens.splkeycloak.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.Collections;

public class CallApiRestfull<T, Q> implements Serializable {

    private HttpHeaders HEADERS;

    public Q callRestApiCommonService(String url, HttpMethod method, T objectStr, ParameterizedTypeReference<Q> type) {
        try {
            RestTemplate restTemplate = new RestTemplate(this.getSysProxy());
            HttpEntity<T> entity = new HttpEntity<>(objectStr, this.getDefaultHeader());
            ResponseEntity<Q> response = restTemplate.exchange(url, method, entity, type);

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                return response.getBody();
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("Lỗi hệ thống -- : " + e);
        }
        return null;
    }

    private SimpleClientHttpRequestFactory getSysProxy() {
        return new SimpleClientHttpRequestFactory();
    }

    private HttpHeaders getDefaultHeader() {
        if (HEADERS == null) {
            HEADERS = new HttpHeaders();
            HEADERS.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//            HEADERS.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HEADERS.set("ssl_verify", "false");
        }
        return HEADERS;
    }

    public  <T> String logJsonResponse(T res) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            return mapper.writeValueAsString(res);
        } catch (Exception e) {
            return "";
        }
    }
}
