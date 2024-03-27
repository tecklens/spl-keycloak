package org.tecklens.splkeycloak.providers.viettel.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsData {

    private String receivePhone;
    private String content;
}
