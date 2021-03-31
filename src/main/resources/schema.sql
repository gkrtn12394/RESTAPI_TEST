DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
    `id`  INTEGER PRIMARY KEY AUTOINCREMENT,
    `user_name` varchar(20) NOT NULL,
    `password` varchar(10) NOT NULL
);

DROP TABLE IF EXISTS `contents`;
CREATE TABLE `contents` (
    `id`  INTEGER PRIMARY KEY AUTOINCREMENT,
    `type` varchar(20) NOT NULL,
    `url` varchar(20) NOT NULL,
    `text` varchar(20) NOT NULL
);

DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages` (
    `id`  INTEGER PRIMARY KEY AUTOINCREMENT,
    `sender` varchar(20) NOT NULL,
    `recipient` varchar(20) NOT NULL,
    `contentId` INTEGER NOT NULL,
    `time_stamp` DATE NOT NULL
);
