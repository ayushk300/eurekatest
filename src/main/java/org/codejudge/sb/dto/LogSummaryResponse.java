package org.codejudge.sb.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LogSummaryResponse {
    private String timeStamp;
    private List<ExceptionCount> logs;
}
