create table boards(
                       id bigserial primary key ,
                       height smallint default 3,
                       width smallint default 3,
                       player_name_crosses varchar(20) ,
                       player_name_noughts varchar(20) ,
                       active boolean not null
);

create table steps(
                      id bigserial primary key ,
                      name varchar(20) ,
                      position_x smallint not null ,
                      position_y smallint not null ,
                      symbol varchar(20) not null,
                      id_board bigint not null ,

                      foreign key (id_board) references boards(id)
);

