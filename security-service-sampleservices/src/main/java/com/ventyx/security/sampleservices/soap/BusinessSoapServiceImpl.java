package com.ventyx.security.sampleservices.soap;

import org.springframework.stereotype.Component;

import javax.jws.WebService;

/**
 * Some test business logic requiring security
 */

@WebService(endpointInterface = "com.ventyx.security.sampleservices.soap.BusinessSoapService")
@Component
public class BusinessSoapServiceImpl implements BusinessSoapService {

    @Override
    public String doSomethingSecure() {
        return "You have infiltrated the system";
    }
}
