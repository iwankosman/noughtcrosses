package com.test.noughtcrosses.mapper;

import com.test.noughtcrosses.dto.BoardInput;
import com.test.noughtcrosses.dto.BoardOutput;
import com.test.noughtcrosses.entity.BoardEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BoardMapper {

    private final Environment environment;

    public BoardEntity fromInput(BoardInput input) {
        BoardEntity entity = new BoardEntity();
        entity.setWidth(input.getWidth());
        entity.setHeight(input.getHeight());
        if (input.getPlayerCrossesName() != null && !input.getPlayerCrossesName().isEmpty()) {
            entity.setPlayerCrossesName(input.getPlayerCrossesName());
        } else {
            entity.setPlayerCrossesName(environment.getProperty("player.name.crosses.default"));
        }
        if (input.getPlayerNoughtsName() != null && !input.getPlayerNoughtsName().isEmpty()) {
            entity.setPlayerNoughtsName(input.getPlayerNoughtsName());
        } else {
            entity.setPlayerNoughtsName(environment.getProperty("player.name.noughts.default"));
        }
        entity.setActive(true);
        return entity;
    }

    public BoardOutput toOutput(BoardEntity entity) {
        BoardOutput output = new BoardOutput();
        output.setId(entity.getId());
        output.setWidth(entity.getWidth());
        output.setHeight(entity.getHeight());
        output.setPlayerNoughtsName(entity.getPlayerNoughtsName());
        output.setPlayerCrossesName(entity.getPlayerCrossesName());
        output.setActive(entity.getActive());
        return output;
    }

}
