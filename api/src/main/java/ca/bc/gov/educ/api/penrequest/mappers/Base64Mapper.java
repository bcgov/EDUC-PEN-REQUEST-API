package ca.bc.gov.educ.api.penrequest.mappers;

import java.util.Base64;

public class Base64Mapper {

    public byte[] map(String value) {
        return Base64.getDecoder().decode(value);
    }

    public String map(byte[] value) {
        return new String(Base64.getEncoder().encode(value));
    }
}
