package org.codejudge.sb.controller;

import io.swagger.annotations.ApiOperation;
import org.codejudge.sb.dto.LogSummaryRequest;
import org.codejudge.sb.dto.ResponseWrapper;
import org.codejudge.sb.service.ProcessLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping
public class AppController {

    @Autowired
    private ProcessLogService processLogService;

    @ApiOperation("This is the hello world api")
    @GetMapping("/log/summary")
    public ResponseWrapper generateLogSummary(@Valid @RequestBody LogSummaryRequest logSummaryRequest) {
        return new ResponseWrapper(processLogService.processLogs(logSummaryRequest));
    }
}
