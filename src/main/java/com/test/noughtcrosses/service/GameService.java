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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final BoardRepository boardRepository;
    private final StepRepository stepRepository;
    private final BoardMapper boardMapper;
    private final StepMapper stepMapper;

    public BoardOutput startGame(BoardInput boardInput) {
        return boardMapper.toOutput(boardRepository.save(boardMapper.fromInput(boardInput)));
    }

    public void stopGame(Long idBoard) {
        boardRepository.setBoardToInactive(idBoard);
    }

    public StepOutput makeStep(Long idBoard, StepInput step) {
        BoardEntity board = boardRepository.findById(idBoard).orElseThrow(() -> new EntityNotFoundException(
                "Доска с id=" + idBoard + " не существует"
        ));

        if (!board.getActive()) {
            throw new IllegalStateException("Доска с id=" + idBoard + " закрыта для игры. Откройте пожалуйста новую.");
        }

        if (stepRepository.existsByBoardEntityAndPositionXAndPositionY(board, step.getPositionX(), step.getPositionY())) {
            throw new IllegalArgumentException("Такой ход уже сделан.");
        }

        StepEntity stepEntity = new StepEntity();
        String nextPlayerName;
        StepEntity topStep = stepRepository.findTopByBoardEntityOrderByIdDesc(board);
        if (topStep == null || topStep.getName().equals(board.getPlayerNoughtsName())) {
            stepEntity.setName(board.getPlayerCrossesName());
            stepEntity.setSymbol(Symbol.CROSS);
            nextPlayerName = board.getPlayerNoughtsName();
        } else {
            stepEntity.setName(board.getPlayerNoughtsName());
            stepEntity.setSymbol(Symbol.NOUGHT);
            nextPlayerName = board.getPlayerCrossesName();
        }

        stepEntity.setPositionX(step.getPositionX());
        stepEntity.setPositionY(step.getPositionY());
        stepEntity.setBoardEntity(board);


        StepOutput stepOutput = stepMapper.toOutput(stepRepository.save(stepEntity));
        String winMessage = winnerDetermine(stepEntity.getName(), board);
        if (winMessage != null) {
            stepOutput.setMessage("Игрок " + stepEntity.getName() + " " + winMessage);
            stopGame(board.getId());
            return stepOutput;
        }
        stepOutput.setMessage("Следующий ход  за " + nextPlayerName);

        return stepOutput;
    }

    private String winnerDetermine(String name, BoardEntity board) {
        List<StepEntity> steps = stepRepository.findAllByNameAndBoardEntity(name, board);
        int boardX = board.getHeight() * (board.getHeight() + 1) / 2;
        int boardY = board.getWidth() * (board.getWidth() + 1) / 2;

        if (steps.size() < board.getHeight() || steps.size() < board.getWidth()) {
            return null;
        }

        if (byDiagonal(steps, boardX, boardY)) {
            return "выиграл по диагонали";
        }

        if (byVertical(steps, boardY)) {
            return "выиграл по вертикали";
        }

        if (byHorizontal(steps, boardX)) {
            return "выиграл по горизонтали";
        }

        return null;

    }

    private boolean byHorizontal(List<StepEntity> steps, int boardX) {
        int stepX = 0;
        int stepY = steps.get(0).getPositionY();
        for (StepEntity step : steps) {
            if (step.getPositionY() != stepY) {
                return false;
            }
            stepX += step.getPositionX();
        }
        return stepX == boardX;
    }

    private boolean byVertical(List<StepEntity> steps, int boardY) {
        int stepY = 0;
        int stepX = steps.get(0).getPositionX();
        for (StepEntity step : steps) {
            if (step.getPositionX() != stepX) {
                return false;
            }
            stepY += step.getPositionY();
        }
        return stepY == boardY;
    }

    private boolean byDiagonal(List<StepEntity> steps, int boardX, int boardY) {
        int stepX = 0;
        int stepY = 0;
        for (StepEntity step : steps) {
            stepX += step.getPositionX();
            stepY += step.getPositionY();
        }
        return stepX == boardX && stepY == boardY;
    }
}
