package com.test.noughtcrosses.dto;

import com.test.noughtcrosses.enums.Symbol;
import lombok.Data;

@Data
public class StepOutput {

    private Long id;
    private String name;
    private Short positionX;
    private Short positionY;
    private Symbol symbol;

    private String message;

}
