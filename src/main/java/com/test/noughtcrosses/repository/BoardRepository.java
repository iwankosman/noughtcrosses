package com.test.noughtcrosses.repository;

import com.test.noughtcrosses.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

    @Modifying
    @Transactional
    @Query("update BoardEntity b set b.active=false where b.id = :idBoard")
    void setBoardToInactive(Long idBoard);

}
