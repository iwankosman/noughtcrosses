package com.test.noughtcrosses.service;

import com.test.noughtcrosses.dto.BoardInput;
import com.test.noughtcrosses.dto.BoardOutput;
import com.test.noughtcrosses.dto.StepInput;
import com.test.noughtcrosses.dto.StepOutput;
import com.test.noughtcrosses.entity.BoardEntity;
import com.test.noughtcrosses.entity.StepEntity;
import com.test.noughtcrosses.enums.Symbol;
import com.test.noughtcrosses.mapper.BoardMapper;
import com.test.noughtcrosses.mapper.StepMapper;
import com.test.noughtcrosses.repository.BoardRepository;
import com.test.noughtcrosses.repository.StepRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private BoardRepository boardRepository;
    @Mock
    private StepRepository stepRepository;
    @Mock
    private Environment environment;
    private BoardMapper boardMapper;
    private StepMapper stepMapper;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        boardMapper = new BoardMapper(environment);
        stepMapper = new StepMapper();
        gameService = new GameService(boardRepository, stepRepository, boardMapper, stepMapper);

    }

    @Test
    void shouldStartGame() {
        BoardInput input = new BoardInput();
        input.setHeight((short) 4);
        input.setWidth((short) 4);
        input.setPlayerNoughtsName("Василий");
        input.setPlayerCrossesName("Иванович");

        BoardEntity entity = new BoardEntity();
        entity.setId(1L);
        entity.setActive(true);
        entity.setWidth((short) 4);
        entity.setHeight((short) 4);
        entity.setPlayerNoughtsName("Василий");
        entity.setPlayerCrossesName("Иванович");

        when(boardRepository.save(boardMapper.fromInput(input))).thenReturn(entity);
        BoardOutput output = gameService.startGame(input);
        assertThat(output.getId()).isEqualTo(entity.getId());
        assertThat(output.getActive()).isEqualTo(true);
        assertThat(output.getPlayerNoughtsName()).isEqualTo(input.getPlayerNoughtsName());
        assertThat(output.getPlayerCrossesName()).isEqualTo(input.getPlayerCrossesName());
        assertThat(output.getHeight()).isEqualTo(input.getHeight());
        assertThat(output.getWidth()).isEqualTo(input.getWidth());
    }

    @Test
    void shouldStartWithDefaultPlayerNames() {
        BoardInput input = new BoardInput();
        input.setHeight((short) 4);
        input.setWidth((short) 4);

        BoardEntity entity = new BoardEntity();
        entity.setId(1L);
        entity.setActive(true);
        entity.setWidth((short) 4);
        entity.setHeight((short) 4);
        entity.setPlayerNoughtsName("crosses");
        entity.setPlayerCrossesName("noughts");

        when(environment.getProperty("player.name.crosses.default")).thenReturn("crosses");
        when(environment.getProperty("player.name.noughts.default")).thenReturn("noughts");

        when(boardRepository.save(boardMapper.fromInput(input))).thenReturn(entity);
        BoardOutput output = gameService.startGame(input);
        assertThat(output.getId()).isEqualTo(entity.getId());
        assertThat(output.getActive()).isEqualTo(true);
        assertThat(output.getPlayerNoughtsName()).isEqualTo("crosses");
        assertThat(output.getPlayerCrossesName()).isEqualTo("noughts");
        assertThat(output.getHeight()).isEqualTo(input.getHeight());
        assertThat(output.getWidth()).isEqualTo(input.getWidth());
    }

    @Test
    void shouldThrowBoardNotFound() {
        Long id = 1L;
        when(boardRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> gameService.makeStep(id, new StepInput()));

        assertThat(exception.getMessage()).isEqualTo("Доска с id=" + id + " не существует");
    }

    @Test
    void shouldThrowThatInactiveBoard() {
        Long id = 1L;
        BoardEntity entity = new BoardEntity();
        entity.setId(id);
        entity.setActive(false);
        entity.setWidth((short) 4);
        entity.setHeight((short) 4);
        entity.setPlayerNoughtsName("crosses");
        entity.setPlayerCrossesName("noughts");

        when(boardRepository.findById(id)).thenReturn(Optional.of(entity));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> gameService.makeStep(1L, new StepInput()));
        assertThat(exception.getMessage())
                .isEqualTo("Доска с id=" + id + " закрыта для игры. Откройте пожалуйста новую.");
    }

    @Test
    void shouldThrowThatMoveAlreadyDone() {
        Long id = 1L;
        BoardEntity entity = new BoardEntity();
        entity.setId(id);
        entity.setActive(true);
        entity.setWidth((short) 4);
        entity.setHeight((short) 4);
        entity.setPlayerNoughtsName("crosses");
        entity.setPlayerCrossesName("noughts");

        StepInput stepInput = new StepInput();
        stepInput.setPositionY((short) 1);
        stepInput.setPositionX((short) 1);

        when(boardRepository.findById(id)).thenReturn(Optional.of(entity));
        when(stepRepository.existsByBoardEntityAndPositionXAndPositionY(
                entity, stepInput.getPositionX(), stepInput.getPositionY())
        ).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> gameService.makeStep(id, stepInput));

        assertThat(exception.getMessage()).isEqualTo("Такой ход уже сделан.");
    }

    @Test
    void shouldMakeFirstStep() {
        Long id = 1L;
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setId(id);
        boardEntity.setActive(true);
        boardEntity.setWidth((short) 4);
        boardEntity.setHeight((short) 4);
        boardEntity.setPlayerNoughtsName("crosses");
        boardEntity.setPlayerCrossesName("noughts");

        StepInput stepInput = new StepInput();
        stepInput.setPositionY((short) 1);
        stepInput.setPositionX((short) 1);

        StepEntity stepEntity = new StepEntity(1L, "crosses", (short) 1, (short) 1, Symbol.CROSS, boardEntity);

        when(boardRepository.findById(id)).thenReturn(Optional.of(boardEntity));
        when(stepRepository.existsByBoardEntityAndPositionXAndPositionY(
                boardEntity, stepInput.getPositionX(), stepInput.getPositionY())
        ).thenReturn(false);

        when(stepRepository.findTopByBoardEntityOrderByIdDesc(boardEntity)).thenReturn(null);

        when(stepRepository.findAllByNameAndBoardEntity(any(), any())).thenReturn(List.of());
        when(stepRepository.save(any())).thenReturn(stepEntity);
        StepOutput output = gameService.makeStep(id, stepInput);
        assertThat(output.getMessage()).isEqualTo("Следующий ход  за " + boardEntity.getPlayerNoughtsName());
        assertThat(output.getSymbol()).isEqualTo(Symbol.CROSS);
        assertThat(output.getPositionX()).isEqualTo(stepEntity.getPositionX());
        assertThat(output.getPositionY()).isEqualTo(stepEntity.getPositionY());
        assertThat(output.getId()).isEqualTo(stepEntity.getId());
        assertThat(output.getName()).isEqualTo(stepEntity.getName());
    }

    @Test
    void shouldMakeNextStep() {
        Long id = 1L;
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setId(id);
        boardEntity.setActive(true);
        boardEntity.setWidth((short) 4);
        boardEntity.setHeight((short) 4);
        boardEntity.setPlayerNoughtsName("crosses");
        boardEntity.setPlayerCrossesName("noughts");

        StepInput stepInput = new StepInput();
        stepInput.setPositionY((short) 1);
        stepInput.setPositionX((short) 1);

        StepEntity previousStepEntity = new StepEntity(1L, "noughts", (short) 1, (short) 1, Symbol.NOUGHT, boardEntity);
        StepEntity stepEntity = new StepEntity(2L, "crosses", (short) 1, (short) 1, Symbol.CROSS, boardEntity);

        when(boardRepository.findById(id)).thenReturn(Optional.of(boardEntity));
        when(stepRepository.existsByBoardEntityAndPositionXAndPositionY(
                boardEntity, stepInput.getPositionX(), stepInput.getPositionY())
        ).thenReturn(false);

        when(stepRepository.findTopByBoardEntityOrderByIdDesc(boardEntity)).thenReturn(previousStepEntity);

        when(stepRepository.findAllByNameAndBoardEntity(any(), any())).thenReturn(List.of());
        when(stepRepository.save(any())).thenReturn(stepEntity);

        StepOutput output = gameService.makeStep(id, stepInput);
        assertThat(output.getMessage()).isEqualTo("Следующий ход  за " + boardEntity.getPlayerCrossesName());
        assertThat(output.getSymbol()).isEqualTo(Symbol.CROSS);
        assertThat(output.getPositionX()).isEqualTo(stepEntity.getPositionX());
        assertThat(output.getPositionY()).isEqualTo(stepEntity.getPositionY());
        assertThat(output.getId()).isEqualTo(stepEntity.getId());
        assertThat(output.getName()).isEqualTo(stepEntity.getName());
    }

    @Test
    void shouldWinByVertical() {
        Long id = 1L;
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setId(id);
        boardEntity.setActive(true);
        boardEntity.setWidth((short) 3);
        boardEntity.setHeight((short) 3);
        boardEntity.setPlayerNoughtsName("crosses");
        boardEntity.setPlayerCrossesName("noughts");

        StepInput stepInput = new StepInput();
        stepInput.setPositionY((short) 1);
        stepInput.setPositionX((short) 1);

        StepEntity previousStepEntity = new StepEntity(1L, "noughts", (short) 1, (short) 1, Symbol.NOUGHT, boardEntity);
        StepEntity stepEntity = new StepEntity(2L, "crosses", (short) 1, (short) 1, Symbol.CROSS, boardEntity);

        when(boardRepository.findById(id)).thenReturn(Optional.of(boardEntity));
        when(stepRepository.existsByBoardEntityAndPositionXAndPositionY(
                boardEntity, stepInput.getPositionX(), stepInput.getPositionY())
        ).thenReturn(false);

        when(stepRepository.findTopByBoardEntityOrderByIdDesc(boardEntity)).thenReturn(previousStepEntity);

        List<StepEntity> stepEntityList = List.of(
                new StepEntity(3L, "noughts", (short) 1, (short) 1, Symbol.NOUGHT, boardEntity),
                new StepEntity(4L, "noughts", (short) 2, (short) 1, Symbol.NOUGHT, boardEntity),
                new StepEntity(5L, "noughts", (short) 3, (short) 1, Symbol.NOUGHT, boardEntity)
        );

        when(stepRepository.findAllByNameAndBoardEntity(stepEntity.getName(), boardEntity)).thenReturn(stepEntityList);
        when(stepRepository.save(any())).thenReturn(stepEntity);

        StepOutput output = gameService.makeStep(id, stepInput);
        assertThat(output.getMessage()).isEqualTo("Игрок " + stepEntity.getName() + " " + "выиграл по горизонтали");
        assertThat(output.getSymbol()).isEqualTo(Symbol.CROSS);
        assertThat(output.getPositionX()).isEqualTo(stepEntity.getPositionX());
        assertThat(output.getPositionY()).isEqualTo(stepEntity.getPositionY());
        assertThat(output.getId()).isEqualTo(stepEntity.getId());
        assertThat(output.getName()).isEqualTo(stepEntity.getName());
    }

    @Test
    void shouldWinByHorizontal() {
        Long id = 1L;
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setId(id);
        boardEntity.setActive(true);
        boardEntity.setWidth((short) 3);
        boardEntity.setHeight((short) 3);
        boardEntity.setPlayerNoughtsName("crosses");
        boardEntity.setPlayerCrossesName("noughts");

        StepInput stepInput = new StepInput();
        stepInput.setPositionY((short) 1);
        stepInput.setPositionX((short) 1);

        StepEntity previousStepEntity = new StepEntity(1L, "noughts", (short) 1, (short) 1, Symbol.NOUGHT, boardEntity);
        StepEntity stepEntity = new StepEntity(2L, "crosses", (short) 1, (short) 1, Symbol.CROSS, boardEntity);

        when(boardRepository.findById(id)).thenReturn(Optional.of(boardEntity));
        when(stepRepository.existsByBoardEntityAndPositionXAndPositionY(
                boardEntity, stepInput.getPositionX(), stepInput.getPositionY())
        ).thenReturn(false);

        when(stepRepository.findTopByBoardEntityOrderByIdDesc(boardEntity)).thenReturn(previousStepEntity);

        List<StepEntity> stepEntityList = List.of(
                new StepEntity(3L, "noughts", (short) 1, (short) 1, Symbol.NOUGHT, boardEntity),
                new StepEntity(4L, "noughts", (short) 1, (short) 2, Symbol.NOUGHT, boardEntity),
                new StepEntity(5L, "noughts", (short) 1, (short) 3, Symbol.NOUGHT, boardEntity)
        );

        when(stepRepository.findAllByNameAndBoardEntity(stepEntity.getName(), boardEntity)).thenReturn(stepEntityList);
        when(stepRepository.save(any())).thenReturn(stepEntity);

        StepOutput output = gameService.makeStep(id, stepInput);
        assertThat(output.getMessage()).isEqualTo("Игрок " + stepEntity.getName() + " " + "выиграл по вертикали");
        assertThat(output.getSymbol()).isEqualTo(Symbol.CROSS);
        assertThat(output.getPositionX()).isEqualTo(stepEntity.getPositionX());
        assertThat(output.getPositionY()).isEqualTo(stepEntity.getPositionY());
        assertThat(output.getId()).isEqualTo(stepEntity.getId());
        assertThat(output.getName()).isEqualTo(stepEntity.getName());
    }

    @Test
    void shouldWinByDiagonal() {
        Long id = 1L;
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setId(id);
        boardEntity.setActive(true);
        boardEntity.setWidth((short) 3);
        boardEntity.setHeight((short) 3);
        boardEntity.setPlayerNoughtsName("crosses");
        boardEntity.setPlayerCrossesName("noughts");

        StepInput stepInput = new StepInput();
        stepInput.setPositionY((short) 1);
        stepInput.setPositionX((short) 1);

        StepEntity previousStepEntity = new StepEntity(1L, "noughts", (short) 1, (short) 1, Symbol.NOUGHT, boardEntity);
        StepEntity stepEntity = new StepEntity(2L, "crosses", (short) 1, (short) 1, Symbol.CROSS, boardEntity);

        when(boardRepository.findById(id)).thenReturn(Optional.of(boardEntity));
        when(stepRepository.existsByBoardEntityAndPositionXAndPositionY(
                boardEntity, stepInput.getPositionX(), stepInput.getPositionY())
        ).thenReturn(false);

        when(stepRepository.findTopByBoardEntityOrderByIdDesc(boardEntity)).thenReturn(previousStepEntity);

        List<StepEntity> stepEntityList = List.of(
                new StepEntity(3L, "noughts", (short) 1, (short) 1, Symbol.NOUGHT, boardEntity),
                new StepEntity(4L, "noughts", (short) 2, (short) 2, Symbol.NOUGHT, boardEntity),
                new StepEntity(5L, "noughts", (short) 3, (short) 3, Symbol.NOUGHT, boardEntity)
        );

        when(stepRepository.findAllByNameAndBoardEntity(stepEntity.getName(), boardEntity)).thenReturn(stepEntityList);
        when(stepRepository.save(any())).thenReturn(stepEntity);

        StepOutput output = gameService.makeStep(id, stepInput);
        assertThat(output.getMessage()).isEqualTo("Игрок " + stepEntity.getName() + " " + "выиграл по диагонали");
        assertThat(output.getSymbol()).isEqualTo(Symbol.CROSS);
        assertThat(output.getPositionX()).isEqualTo(stepEntity.getPositionX());
        assertThat(output.getPositionY()).isEqualTo(stepEntity.getPositionY());
        assertThat(output.getId()).isEqualTo(stepEntity.getId());
        assertThat(output.getName()).isEqualTo(stepEntity.getName());
    }



}
