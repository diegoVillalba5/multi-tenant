CREATE SCHEMA tenant_1;
CREATE SCHEMA tenant_2;

USE tenant_1;
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`       int  NOT NULL AUTO_INCREMENT,
    `username` text NOT NULL,
    `name`     text NOT NULL,
    `email`    text NOT NULL,
    `password` text NOT NULL,
    `active`   tinyint(1) NOT NULL DEFAULT '1',
    PRIMARY KEY (`id`)
);
INSERT INTO `user`
VALUES (1, 'user1@tenant_1', 'User One', 'user1@tenant.com',
        '$2a$10$wBjpEtVGugMphJ8ImBN08.IiMvvie1u7Tn0dD4wRGo5g5LFu69ko6', 1);

USE tenant_2;
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`       int  NOT NULL AUTO_INCREMENT,
    `username` text NOT NULL,
    `name`     text NOT NULL,
    `email`    text NOT NULL,
    `password` text NOT NULL,
    `active`   tinyint(1) NOT NULL DEFAULT '1',
    PRIMARY KEY (`id`)
);
INSERT INTO `user`
VALUES (1, 'user2@tenant_2', 'User Two', 'user2@tenant.com',
        '$2a$10$xllTx5DU3k8ZaioEiq2HN.rmEbVT1aZzI9kt807FRQQ7qrp5Oyqey', 1);
