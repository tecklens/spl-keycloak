package org.tecklens.splkeycloak.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tecklens.splkeycloak.util.CommonUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class LoggerInterceptor implements HandlerInterceptor {
    Logger loggerPerformance = LoggerFactory.getLogger(LoggerInterceptor.class);

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // * Auto-generated method stub
        request.setAttribute("arrivedTime", new Date());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // * Auto-generated method stub
        Date dateStart = (Date) request.getAttribute("arrivedTime");
        String jwtToken = (String) request.getAttribute("Authorization");

        String strDate = dateFormat.format(dateStart);
        String userName = CommonUtil.getUserNameFromToken(jwtToken);
        long duration = new Date().getTime() - dateStart.getTime();
        String logString;
        if (userName != null) {
            logString = strDate + "|" + userName + "|" + request.getRemoteAddr() +
                    "|" + request.getRequestURI() + "|" +
                    ((HandlerMethod) handler).getBean().getClass().getName() + "|" +
                    ((HandlerMethod) handler).getMethod().getName() + "|" +
                    "|" + response.getStatus() +
                    "|" + duration;
        } else {
            logString = strDate + "||" + request.getRemoteAddr() +
                    "|" + request.getRequestURI() + "|" +
                    ((HandlerMethod) handler).getBean().getClass().getName() + "|" +
                    ((HandlerMethod) handler).getMethod().getName() + "|" +
                    "|" + response.getStatus() +
                    "|" + duration;
        }
        loggerPerformance.info(logString);

        //nếu url nằm trong danh sách dữ liệu nhạy cảm -> gọi api ghi log dùng chung
//        if(sensitiveDataUrl.contains(request.getRequestURI())) {
//            Parameter parram = Arrays.stream(((HandlerMethod) handler).getMethodParameters()).findFirst().get().getParameter();
//            Field[] className = parram.getType().getDeclaredFields();
//            String json = "{";
//            for (int i = 0 ; i < className.length; i++){
//                json = json + className[i].getName() + ":" + className[i].getName()+",";
//            }
//            json = json +"}";
//            RestTemplate restTemplate = new RestTemplate();
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            JSONObject personJsonObject = new JSONObject();
//            personJsonObject.put("applicationCode", applicationCode);
//            personJsonObject.put("ipPortCurrentNode", request.getServerName());
//            personJsonObject.put("clientIp", request.getRemoteAddr());
//            personJsonObject.put("username", userName);
//            personJsonObject.put("serviceCode", ((HandlerMethod) handler).getMethod().getName());
//            personJsonObject.put("requestContent", json);
//            personJsonObject.put("startTime", dateFormat2.format(dateStart));
//            personJsonObject.put("endTime", dateFormat2.format(new Date()));
//            personJsonObject.put("actionType", "VIEW");
//            System.out.println(personJsonObject.toString());
//            HttpEntity<String> req =
//                    new HttpEntity<String>(personJsonObject.toString(), headers);
//
//            String personResultAsJsonStr =
//                    restTemplate.postForObject(createLogUrl, req, String.class);
//            System.out.println(personResultAsJsonStr);
//        }

    }

}
