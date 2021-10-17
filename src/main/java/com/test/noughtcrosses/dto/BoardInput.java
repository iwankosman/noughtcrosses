package com.test.noughtcrosses.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class BoardInput {

    @Min(3)
    private Short height;
    @Min(3)
    private Short width;
    @Max(20)
    private String playerCrossesName;
    @Max(20)
    private String playerNoughtsName;

}
