package org.codejudge.sb.service;

import org.codejudge.sb.dto.LogSummaryRequest;
import org.codejudge.sb.dto.LogSummaryResponse;

import java.util.List;

public interface ProcessLogService {
    List<LogSummaryResponse> processLogs(LogSummaryRequest logSummaryRequest);
}
