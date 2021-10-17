package com.test.noughtcrosses.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Объект хода игрока")
public class StepInput {

    @ApiModelProperty(value = "Позиция по Х для следующего хода игрока")
    private Short positionX;
    @ApiModelProperty(value = "Позиция по Y для следующего хода игрока")
    private Short positionY;
}
