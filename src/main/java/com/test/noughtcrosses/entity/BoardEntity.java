package com.test.noughtcrosses.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "boards")
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "height")
    private Short height;

    @Column(name = "width")
    private Short width;

    @Column(name = "player_name_crosses")
    private String playerCrossesName;

    @Column(name = "player_name_noughts")
    private String playerNoughtsName;

    @Column(name = "active")
    private Boolean active;

}
