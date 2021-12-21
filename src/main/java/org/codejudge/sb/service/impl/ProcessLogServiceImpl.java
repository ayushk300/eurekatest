package org.codejudge.sb.service.impl;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.codejudge.sb.dto.ExceptionCount;
import org.codejudge.sb.dto.HTTPResponse;
import org.codejudge.sb.dto.LogSummaryRequest;
import org.codejudge.sb.dto.LogSummaryResponse;
import org.codejudge.sb.service.ProcessLogService;
import org.codejudge.sb.util.AppErrorCode;
import org.codejudge.sb.util.CommonUtil;
import org.codejudge.sb.util.CustomException;
import org.codejudge.sb.util.HttpClientCustom;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
public class ProcessLogServiceImpl implements ProcessLogService {
    private static Integer SECONDS_TO_WAIT_FOR_ENDPOINT_RESPONSE = 30;

    @Override
    public List<LogSummaryResponse> processLogs(LogSummaryRequest logSummaryRequest) {
        if(CollectionUtils.isEmpty(logSummaryRequest.getLogFiles())) {
            throw new CustomException(AppErrorCode.BAD_REQUEST, "Log files not provided");
        }
        if(Objects.isNull(logSummaryRequest.getParallelFileProcessingCount()) || logSummaryRequest.getParallelFileProcessingCount() > 15 || logSummaryRequest.getParallelFileProcessingCount() < 0) {
            throw new CustomException(AppErrorCode.BAD_REQUEST, "Parallel File Processing count must be greater than zero!");
        }

        Map<String, Map<String , ExceptionCount>> exceptionCountsGroupedByTime = new HashMap<>();

        ExecutorService executors = Executors.newFixedThreadPool(15);
        Map<String, Future<Map<String, Map<String, ExceptionCount>>>> futureMap = new HashMap<>();
        for(String logFilePath: logSummaryRequest.getLogFiles()) {
            futureMap.put(logFilePath, executors.submit(() -> process(logFilePath)));
        }

        Map<String, Map<String, ExceptionCount>> responseMap = new HashMap<>();
        try {
            for (Map.Entry<String, Future<Map<String, Map<String, ExceptionCount>>>> x : futureMap.entrySet()) {
                Map<String, Map<String , ExceptionCount>> intermediateMap = x.getValue().get(SECONDS_TO_WAIT_FOR_ENDPOINT_RESPONSE, TimeUnit.SECONDS);
                mergeMaps(responseMap, intermediateMap);
            }
        } catch (InterruptedException | TimeoutException e) {
            log.error(CommonUtil.getDetailedExceptionMessage(e));
        } catch (ExecutionException e) {
            log.error(CommonUtil.getDetailedExceptionMessage(e));
            Thread.currentThread().interrupt();
        } finally {
            executors.shutdown();
        }
        return transformToResponse(responseMap);
    }

    private List<LogSummaryResponse> transformToResponse(Map<String, Map<String, ExceptionCount>> responseMap) {
        List<LogSummaryResponse> responses = new ArrayList<>();
        for(Map.Entry<String, Map<String , ExceptionCount>> entry1 : responseMap.entrySet()) {
            List<ExceptionCount> logs = Lists.newArrayList(entry1.getValue().values());
            Collections.sort(logs);
            responses.add(new LogSummaryResponse(entry1.getKey(), logs));
        }
        return responses;
    }

    private void mergeMaps(Map<String, Map<String, ExceptionCount>> responseMap,
                           Map<String, Map<String , ExceptionCount>> intermediateMap) {
        for(Map.Entry<String, Map<String , ExceptionCount>> entry1 : intermediateMap.entrySet()) {
            Map<String, ExceptionCount> responseTempMap = responseMap.computeIfAbsent(entry1.getKey(), x-> new HashMap<>());

            for (Map.Entry<String , ExceptionCount> entry2 : entry1.getValue().entrySet()) {
                responseTempMap.computeIfAbsent(entry2.getKey(), x -> new ExceptionCount(entry2.getKey(), 0))
                        .incrementCount();
            }
        }
    }

    private Map<String, Map<String, ExceptionCount>> process(String filePath) {
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Content-Type", "application/json");
        HTTPResponse httpResponse = HttpClientCustom.getInstance().httpGet("https://codejudge-question-artifacts.s3.ap-south-1.amazonaws.com/q-120/log1.txt", requestHeaders);
        String response = httpResponse.getPayloadString();

        Map<String, Map<String, ExceptionCount>> exceptionCountMap = new HashMap<>();
        List<String> logs = Arrays.asList(response.split("/n"));
        for(String logEntry : logs) {
            String timeStampString = logEntry.split(" ")[1];
            String exception = logEntry.split(" ")[2];
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String date = sdf.format(new Date(new Long(timeStampString)));
            Integer hour = Integer.parseInt(date.split(" ")[1].split(":")[0]) + 12 ;
            Integer min =  Integer.parseInt(date.split(" ")[1].split(":")[1]);

            String range = getTimeRangeStr(hour, min);

            exceptionCountMap.computeIfAbsent(range, x -> new HashMap<>())
                    .computeIfAbsent(exception, x-> new ExceptionCount(exception, 0))
                    .incrementCount();
        }
        return exceptionCountMap;
    }

    private String getTimeRangeStr(Integer hour, Integer min) {
        if(min > 0 && min < 15){
            return hour+":"+"00-"+hour+":"+"15";
        } else if(min > 15 && min < 30){
            return hour+":"+"15-"+hour+":"+"30";
        } else if(min > 30 && min < 45){
            return hour+":"+"30-"+hour+":"+"45";
        } else if(min > 45 && min < 60){
            return hour+":"+"30-"+hour+":"+"45";
        } else {
            throw new CustomException(AppErrorCode.BAD_REQUEST, "time range invalid");
        }
    }
}
