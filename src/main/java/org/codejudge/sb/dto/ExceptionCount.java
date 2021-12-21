package org.codejudge.sb.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"exception"})
public class ExceptionCount implements Comparable<ExceptionCount>{
    private String exception;
    private Integer count;

    public ExceptionCount incrementCount() {
        count++;
        return this;
    }

    @Override
    public int compareTo(ExceptionCount o) {
        return getException().compareTo(o.getException());
    }
}
