package org.tecklens.splkeycloak.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.tomcat.util.json.JSONParser;
import org.slf4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtil {
    //    @Value("${sso.url}")
    public static String sendSsoUrl = "https://sso2.viettel.vn:8002/sso/v1/tickets";

    public static String convertDateToString(Date date, String pattern) {
        if (date == null) {
            return "";
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            return dateFormat.format(date);
        }
    }

    public static Date convertStringToDate(String date, String pattern) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            return dateFormat.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getUserNameFromToken(String jwtToken) {
        try {
            if (jwtToken == null) return null;
            String tokenBody = jwtDecode(jwtToken);
            JSONParser jp = new JSONParser(tokenBody);
            String userName = (String) jp.parseObject().get("preferred_username");
            return userName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String jwtDecode(String jwtToken) {
        if (jwtToken == null) return null;
        String[] split_string = jwtToken.split("\\.");
        String base64EncodedBody = split_string[1];
        Base64 base64Url = new Base64(true);
        String body = new String(base64Url.decode(base64EncodedBody));
        return body;
    }

    public static boolean checkPasswordSso(String username, String password, Logger logger) {
        CallApiRestfull<MultiValueMap<String, String>, String> callApiRestfull = new CallApiRestfull<>();

        MultiValueMap<String, String> request= new LinkedMultiValueMap<String, String>();
        request.add("username", username);
        request.add("password", password);

        String jsonString = callApiRestfull.logJsonResponse(request)
                .replaceAll("(\r\n|\n)", "");
        logger.info("body login to sso ---------------- \n" + jsonString);
        logger.info(sendSsoUrl);

        String response = callApiRestfull.callRestApiCommonService
                (sendSsoUrl,
                        HttpMethod.POST,
                        request,
                        new ParameterizedTypeReference<String>() {
                        });

        return !(response == null);
    }
}
