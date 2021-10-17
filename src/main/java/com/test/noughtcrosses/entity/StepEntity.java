package com.test.noughtcrosses.entity;

import com.test.noughtcrosses.enums.Symbol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "steps")
public class StepEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "position_x")
    private Short positionX;

    @Column(name = "position_y")
    private Short positionY;

    @Column(name = "symbol")
    @Enumerated(EnumType.STRING)
    private Symbol symbol;

    @ManyToOne
    @JoinColumn(name = "id_board")
    private BoardEntity boardEntity;

}
