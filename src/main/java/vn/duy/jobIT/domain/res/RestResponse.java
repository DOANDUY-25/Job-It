package vn.duy.jobIT.domain.res;

import lombok.Builder;
import lombok.Data;

@Data
public class RestResponse<T> {
    private int statusCode;
    private String error;
    private Object message;
    private T data;
}
