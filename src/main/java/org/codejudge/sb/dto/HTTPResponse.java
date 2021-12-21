package org.codejudge.sb.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.Header;

/**
 * Created By Shivanshu Goyal on 19/03/20 Mar, 2020
 */
@Data
@NoArgsConstructor
public class HTTPResponse {

    private int status;
    private String message;
    private String payloadString;
    private Header[] headers;

    public HTTPResponse(int status, String message, String payload) {
        this.message=message;
        this.status=status;
        this.payloadString=payload;
    }

    public HTTPResponse(int status, String message, String payload, Header[] headers) {
        this.message=message;
        this.status=status;
        this.payloadString=payload;
        this.headers=headers;
    }
}
