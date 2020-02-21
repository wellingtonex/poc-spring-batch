CREATE TABLE IF NOT EXISTS conta (
  id INT NOT NULL AUTO_INCREMENT ,
  saldo DECIMAL(10,2) NOT NULL ,
  ultimaAtualizacao TIMESTAMP NOT NULL,
  PRIMARY KEY (id) )
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS operacao
(
    id int auto_increment primary key,
    tx   varchar(256) not null
)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS movimento
(
    id int auto_increment primary key,
    valor   decimal(10, 2) not null,
    contaId int not null,
    operacaoId int not null,
    data TIMESTAMP NOT NULL,
    constraint movimento_conta_fk
        foreign key (contaId) references conta (id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION,
    constraint movimento_operacao_fk
        foreign key (operacaoId) references operacao (id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
)
ENGINE = InnoDB;

