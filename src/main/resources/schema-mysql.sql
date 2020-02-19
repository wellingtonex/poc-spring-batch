CREATE TABLE IF NOT EXISTS conta (
  id INT NOT NULL AUTO_INCREMENT ,
  saldo DECIMAL(10,2) NOT NULL ,
  ultimaAtualizacao TIMESTAMP NOT NULL,
  PRIMARY KEY (id) )
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS movimento
(
    id int auto_increment primary key,
    valor   decimal(10, 2) not null,
    contaId int not null,
    data TIMESTAMP NOT NULL,
    constraint movimento_conta__fk
        foreign key (contaId) references conta (id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
)
ENGINE = InnoDB;