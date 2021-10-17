package com.test.noughtcrosses.mapper;

import com.test.noughtcrosses.dto.StepOutput;
import com.test.noughtcrosses.entity.StepEntity;
import org.springframework.stereotype.Component;

@Component
public class StepMapper {

    public StepOutput toOutput(StepEntity stepEntity) {
        StepOutput output = new StepOutput();
        output.setId(stepEntity.getId());
        output.setName(stepEntity.getName());
        output.setPositionX(stepEntity.getPositionX());
        output.setPositionY(stepEntity.getPositionY());
        output.setSymbol(stepEntity.getSymbol());
        return output;
    }
}
