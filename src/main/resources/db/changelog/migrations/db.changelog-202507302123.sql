--liquibase formatted sql
--changeset lucas:202507302123
--comment boards table create

create table BOARDS(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
)ENGINE=InnoDB;
--rollback DROP TABLE BOARDS