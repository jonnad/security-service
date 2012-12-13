package com.ventyx.security.sampleservices.soap;

import org.springframework.stereotype.Component;

import javax.jws.WebService;

/**
 * Some test business logic requiring security
 */

@WebService
@Component
public interface BusinessSoapService {

    public String doSomethingSecure();

}
