CREATE TABLE IF NOT EXISTS `Super` (
  `mail` varchar(300) NOT NULL,
  `name` varchar(300) NOT NULL,
  `username` varchar(100) NOT NULL,
  `password` varchar(300) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--NEXT--

CREATE TABLE IF NOT EXISTS `Complaints` (
  `subforumId` int(11) NOT NULL,
  `id` int(11) NOT NULL,
  `complainer` varchar(300) NOT NULL,
  `complainee` varchar(300) NOT NULL,
  `complaintMessage` text NOT NULL,
  `date` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--NEXT--

CREATE TABLE IF NOT EXISTS `Forums` (
  `id` int(11) NOT NULL,
  `name` varchar(300) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--NEXT--

CREATE TABLE IF NOT EXISTS `Messages` (
  `msgRel` int(11) NOT NULL,
  `subforumRel` int(11) NOT NULL,
  `id` int(11) NOT NULL,
  `date` varchar(20) NOT NULL,
  `content` text NOT NULL,
  `title` text NOT NULL,
  `writer` varchar(300) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--NEXT--

CREATE TABLE IF NOT EXISTS `Ranks` (
  `forumId` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `CREATE_FORUM` tinyint(1) NOT NULL,
  `SET_FORUM_PROPERTIES` tinyint(1) NOT NULL,
  `CREATE_SUB_FORUM` tinyint(1) NOT NULL,
  `CREATE_MESSAGE` tinyint(1) NOT NULL,
  `SET_RANKS` tinyint(1) NOT NULL,
  `SET_USER_RANK` tinyint(1) NOT NULL,
  `DELETE_MESSAGE` tinyint(1) NOT NULL,
  `DELETE_SUB_FORUM` tinyint(1) NOT NULL,
  `ADD_ADMIN` tinyint(1) NOT NULL,
  `REMOVE_ADMIN` tinyint(1) NOT NULL,
  `ADD_MODERATOR` tinyint(1) NOT NULL,
  `REMOVE_MODERATOR` tinyint(1) NOT NULL,
  PRIMARY KEY (`forumId`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--NEXT--

CREATE TABLE IF NOT EXISTS `SubForums` (
  `forumId` int(11) NOT NULL,
  `id` int(11) NOT NULL,
  `subject` varchar(300) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--NEXT--

CREATE TABLE IF NOT EXISTS `Users` (
  `forumId` int(11) NOT NULL,
  `mail` varchar(300) NOT NULL,
  `name` varchar(300) NOT NULL,
  `username` varchar(100) NOT NULL,
  `password` varchar(300) NOT NULL,
  `rank` varchar(300) NOT NULL,
  `notifTypes` int(11) NOT NULL,
  PRIMARY KEY (`forumId`, `username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--NEXT--

CREATE TABLE IF NOT EXISTS `_administrators` (
  `ForumId` int(11) NOT NULL,
  `Username` varchar(100) NOT NULL,
  PRIMARY KEY (`ForumId`, `Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--NEXT--

CREATE TABLE IF NOT EXISTS `_friendRequests` (
  `forumId` int(11) NOT NULL,
  `user1` varchar(100) NOT NULL,
  `user2` varchar(100) NOT NULL,
  PRIMARY KEY (`forumId`, `user1`, `user2`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--NEXT--

CREATE TABLE IF NOT EXISTS `_friends` (
  `forumId` int(11) NOT NULL,
  `user1` varchar(100) NOT NULL,
  `user2` varchar(100) NOT NULL,
  PRIMARY KEY (`forumId`, `user1`, `user2`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--NEXT--

CREATE TABLE IF NOT EXISTS `_moderators` (
  `subforumId` int(11) NOT NULL,
  `username` varchar(100) NOT NULL,
  PRIMARY KEY (`subforumId`, `username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--NEXT--

CREATE TABLE IF NOT EXISTS `_pendingFriendRequests` (
  `forumId` int(11) NOT NULL,
  `user1` varchar(100) NOT NULL,
  `user2` varchar(100) NOT NULL,
  PRIMARY KEY (`forumId`, `user1`, `user2`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--NEXT--

CREATE TABLE IF NOT EXISTS `_suspended` (
  `subforumId` int(11) NOT NULL,
  `username` varchar(100) NOT NULL,
  `date` varchar(20) NOT NULL,
  PRIMARY KEY (`subforumId`, `username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
