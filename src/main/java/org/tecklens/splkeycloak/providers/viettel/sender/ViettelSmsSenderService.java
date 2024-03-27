package org.tecklens.splkeycloak.providers.viettel.sender;

import jakarta.validation.ValidationException;
import jakarta.xml.soap.*;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.tecklens.splkeycloak.providers.keycloak.phone.providers.spi.FullSmsSenderAbstractService;
import org.tecklens.splkeycloak.providers.viettel.model.SendOTPResponseModel;
import org.tecklens.splkeycloak.providers.viettel.model.SmsData;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jakarta.ws.rs.BadRequestException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;

public class ViettelSmsSenderService extends FullSmsSenderAbstractService {

    private static final Logger logger = Logger.getLogger(ViettelSmsSenderService.class);
    private final Config.Scope config;

    //    private final String smsEndpoint;
//    private final String namespace;
//    private final String namespaceURI;
//    private final String configBodyUser;
//    private final String configBodyPassword;
//    private final String configBodyCpCode;
//    private final String configBodyRequestId;
//    private final String configBodyServiceIdNotViettel;
//    private final String configBodyServiceIdViettel;
//    private final String configBodyCommandCode;
//    private final String configBodyContentType;
//    private final int smsMinValuesRandomOtp;
//    private final int smsMaxValuesRandomOtp;
    private String smsEndpoint;

    private String namespace;

    private String namespaceURI;

    private String configBodyUser;

    private String configBodyPassword;

    private String configBodyCpCode;

    private String configBodyRequestId;

    private String configBodyServiceIdNotViettel;

    private String configBodyServiceIdViettel;

    private String configBodyCommandCode;

    private String configBodyContentType;

    private int smsMinValuesRandomOtp;

    private int smsMaxValuesRandomOtp;
    private static final String[] LIST_VIETTEL_NUMBER = {"096", "097", "098", "086", "032", "033", "034", "035", "036", "037", "038", "039",
            "8496", "8497", "8498", "8486", "8432", "8433", "8434", "8435", "8436", "8437", "8438", "8439"};

    private static final String[] LIST_VINA_NUMBER = {"091", "094", "081", "082", "083", "084", "085", "086", "087", "088", "089",
            "8491", "8494", "8481", "8482", "8483", "8484", "8485", "8486", "8487", "8488", "8489"};

    private static final String[] LIST_MOBI_NUMBER = {"090", "093", "099", "070", "076", "077", "078", "079",
            "8490", "8493", "8499", "8470", "8476", "8477", "8478", "8479"};

    private static final String[] LIST_VIETNAM_MOBILE_NUMBER = {"092", "052", "056", "058", "059",
            "8492", "8452", "8456", "8458", "8459"};

    public ViettelSmsSenderService(String realmDisplay, Config.Scope config) {
        super(realmDisplay);
        this.config = config;

        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(new ClassPathResource("application.yml"));
        Properties env = factory.getObject();

        this.smsEndpoint = env.getProperty("sms.api.endpoint");
        this.namespace = env.getProperty("sms.api.namespace");
        this.namespaceURI = env.getProperty("sms.api.namespaceURI");
        this.configBodyUser = env.getProperty("sms.api.configBody.user");
        this.configBodyPassword = env.getProperty("sms.api.configBody.password");
        this.configBodyCpCode = env.getProperty("sms.api.configBody.cpCode");
        this.configBodyRequestId = env.getProperty("sms.api.configBody.requestId");
        this.configBodyServiceIdNotViettel = env.getProperty("sms.api.configBody.serviceId.notViettel");
        this.configBodyServiceIdViettel = env.getProperty("sms.api.configBody.serviceId.viettel");
        this.configBodyCommandCode = env.getProperty("sms.api.configBody.commandCode");
        this.configBodyContentType = env.getProperty("sms.api.configBody.contentType");
        this.smsMinValuesRandomOtp = Integer.parseInt(env.getProperty("sms.minValuesRandomOtp"));
        this.smsMaxValuesRandomOtp = Integer.parseInt(env.getProperty("sms.maxValuesRandomOtp"));
    }

    @Override
    public void sendMessage(String phoneNumber, String message) {

        try {
            if (!validateViettelNumber(phoneNumber)) {
                throw new ValidationException("Số điện thoại sai định dạng");
            }
            sendSmsOtp(new SmsData(phoneNumber, message));
        } catch (Exception ex) {
            String msg = "Không thể gửi tin nhắn thông qua viettel";
            logger.error(msg, ex);
            throw new BadRequestException(msg, ex);
        }
    }

    @Override
    public void close() {
    }

    public void sendSmsOtp(SmsData dto) {
        String contactPhone = dto.getReceivePhone();

        if (contactPhone.startsWith("0")) {
            dto.setReceivePhone("84" + contactPhone.substring(1));
        }
        dto.setContent(dto.getContent());
        logger.info(dto.getContent());
        this.callSoapWebService(dto);
    }

    private void createSoapEnvelope(SOAPMessage soapMessage, SmsData dto) throws SOAPException {
        SOAPPart soapPart = soapMessage.getSOAPPart();

        String myNamespace = namespace;
        String myNamespaceURI = namespaceURI;

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement("wsCpMt", myNamespace);
        SOAPElement elementUser = soapBodyElem.addChildElement("User");
        elementUser.addTextNode(configBodyUser);

        SOAPElement elementPassword = soapBodyElem.addChildElement("Password");
        elementPassword.addTextNode(configBodyPassword);

        SOAPElement elementCPCode = soapBodyElem.addChildElement("CPCode");
        elementCPCode.addTextNode(configBodyCpCode);

        SOAPElement elementRequestID = soapBodyElem.addChildElement("RequestID");
        elementRequestID.addTextNode(configBodyRequestId);

        SOAPElement elementUserID = soapBodyElem.addChildElement("UserID");
        elementUserID.addTextNode(dto.getReceivePhone());

        SOAPElement elementReceiverID = soapBodyElem.addChildElement("ReceiverID");
        elementReceiverID.addTextNode(dto.getReceivePhone());

        SOAPElement elementServiceID = soapBodyElem.addChildElement("ServiceID");
        elementServiceID.addTextNode(configBodyServiceIdNotViettel);
//        if(validateViettelNumber(dto.getReceivePhone())) {
//            elementServiceID.addTextNode(configBodyServiceIdViettel);
//        } else {
//            elementServiceID.addTextNode(configBodyServiceIdNotViettel);
//        }

        SOAPElement elementCommandCode = soapBodyElem.addChildElement("CommandCode");
        elementCommandCode.addTextNode(configBodyCommandCode);

        SOAPElement elementContent = soapBodyElem.addChildElement("Content");
        elementContent.addTextNode(dto.getContent());

        SOAPElement elementContentType = soapBodyElem.addChildElement("ContentType");
        elementContentType.addTextNode(configBodyContentType);
    }

    public void callSoapWebService(SmsData dto) {
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(dto), smsEndpoint);
            SOAPBody soapBody = soapResponse.getSOAPBody();
            NodeList nodeList = soapBody.getElementsByTagName("return");
            String bodyStr = nodeToString(nodeList.item(0));
            String replaceHeadTag = bodyStr.replaceAll("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
            SendOTPResponseModel objectResponse = this.convertStringXmlToObject(replaceHeadTag);
            if (objectResponse.getResult().equals("0")) {
                logger.error("Lỗi : " + objectResponse.getMessage());
                logger.error(objectResponse.getMessage());
                throw new BadRequestException("Lỗi : " + objectResponse.getMessage());
            } else {
                logger.error("Response : " + objectResponse.getMessage());
            }
            soapConnection.close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Gửi mã OTP thất bại" + e.getMessage());
            logger.error("Gửi thông tin cho khách hàng thất bại. Xin vui lòng liên hệ CSKH (1900989868) để được hỗ trợ. Xin cảm ơn !");
            throw new BadRequestException("Gửi OTP thất bại");
        }
    }


    private SOAPMessage createSOAPRequest(SmsData dto) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        createSoapEnvelope(soapMessage, dto);

        soapMessage.saveChanges();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        soapMessage.writeTo(out);
        String strMsg = new String(out.toByteArray());
        logger.info("request : " + strMsg);
        /* Print the request message, just for debugging purposes */
        soapMessage.writeTo(System.out);
        System.out.println("\n");

        return soapMessage;
    }

    private SendOTPResponseModel convertStringXmlToObject(String xmlString) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(SendOTPResponseModel.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            InputStream reader = new ByteArrayInputStream(xmlString.getBytes());
            Source source = new StreamSource(reader);
            JAXBElement<SendOTPResponseModel> root = unmarshaller.unmarshal(source, SendOTPResponseModel.class);
            logger.info(root.getValue().getMessage());
            logger.info("--------------------------");
            logger.info(root.getValue().getResult());
            return root.getValue();

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Lỗi : " + ex.getMessage());
        }
        return new SendOTPResponseModel();
    }

    private String nodeToString(Node node) {
        StringWriter sw = new StringWriter();
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException te) {
            logger.error("Lỗi : " + "nodeToString Transformer Exception");
        }
        return sw.toString();
    }

    private boolean validateViettelNumber(String number) {
        for (String str : LIST_VIETTEL_NUMBER) {
            if (number.contains(str)) {
                return true;
            }
        }

        for (String str : LIST_VINA_NUMBER) {
            if (number.contains(str)) {
                return true;
            }
        }

        for (String str : LIST_MOBI_NUMBER) {
            if (number.contains(str)) {
                return true;
            }
        }

        for (String str : LIST_VIETNAM_MOBILE_NUMBER) {
            if (number.contains(str)) {
                return true;
            }
        }
        return false;
    }
}
