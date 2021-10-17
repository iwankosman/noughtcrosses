package com.test.noughtcrosses.controller;

import com.test.noughtcrosses.dto.BoardInput;
import com.test.noughtcrosses.dto.BoardOutput;
import com.test.noughtcrosses.dto.StepInput;
import com.test.noughtcrosses.dto.StepOutput;
import com.test.noughtcrosses.service.GameService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    @PostMapping("/new")
    @ApiOperation(value = "Старт новой игры с созданием новой доски")
    public BoardOutput startNewGame(@RequestBody @Valid BoardInput board, HttpSession session) {
        return gameService.startGame(board);
    }

    @GetMapping("/{idBoard}/stop")
    @ApiOperation(value = "Выход из игры и деактивация доски")
    public void stopGame(@PathVariable("idBoard") Long idBoard, HttpSession session) {
        gameService.stopGame(idBoard);
    }

    @PostMapping("/{idBoard}/make-step")
    @ApiOperation(value = "Выполнение хода игрока")
    public StepOutput makeStep(@PathVariable("idBoard") Long idBoard, @RequestBody StepInput step,
                               HttpSession session) {
        session.invalidate();
        return gameService.makeStep(idBoard, step);
    }
}
