package org.codejudge.sb.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LogSummaryRequest {
    @NotEmpty
    private List<String> logFiles;
    @NotNull
    private Integer parallelFileProcessingCount;
}
