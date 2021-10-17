package com.test.noughtcrosses.dto;

import lombok.Data;

@Data
public class BoardOutput {

    private Long id;
    private Short height;
    private Short width;
    private String playerCrossesName;
    private String playerNoughtsName;
    private Boolean active;

}
