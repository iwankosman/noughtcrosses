package com.test.noughtcrosses.repository;

import com.test.noughtcrosses.entity.BoardEntity;
import com.test.noughtcrosses.entity.StepEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StepRepository extends JpaRepository<StepEntity, Long> {

    boolean existsByBoardEntityAndPositionXAndPositionY(BoardEntity boardEntity, Short positionX, Short positionY);

    StepEntity findTopByBoardEntityOrderByIdDesc(BoardEntity boardEntity);

    List<StepEntity> findAllByNameAndBoardEntity(String name, BoardEntity boardEntity);

}
