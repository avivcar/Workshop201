CREATE TABLE IF NOT EXISTS `Complaints` (
  `rel` int(11) NOT NULL,
  `id` int(11) NOT NULL,
  `complainer` varchar(300) NOT NULL,
  `complainee` varchar(300) NOT NULL,
  `complaintMessage` text NOT NULL,
  `date` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8_general_ci;

--NEXT--

CREATE TABLE IF NOT EXISTS `Forums` (
  `id` int(11) NOT NULL,
  `name` varchar(300) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8_general_ci;

--NEXT--

CREATE TABLE IF NOT EXISTS `Messages` (
  `msgRel` int(11) NOT NULL,
  `subforumRel` int(11) NOT NULL,
  `id` int(11) NOT NULL,
  `date` varchar(20) NOT NULL,
  `content` text NOT NULL,
  `title` text NOT NULL,
  `writer` varchar(300) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8_general_ci;

--NEXT--

CREATE TABLE IF NOT EXISTS `Ranks` (
  `rel` int(11) NOT NULL,
  `name` varchar(300) NOT NULL,
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
  `REMOVE_MODERATOR` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8_general_ci;

--NEXT--

CREATE TABLE IF NOT EXISTS `SubForums` (
  `rel` int(11) NOT NULL,
  `id` int(11) NOT NULL,
  `subject` varchar(300) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8_general_ci;

--NEXT--

CREATE TABLE IF NOT EXISTS `Users` (
  `rel` int(11) NOT NULL,
  `mail` varchar(300) NOT NULL,
  `name` varchar(300) NOT NULL,
  `username` varchar(300) NOT NULL,
  `password` varchar(300) NOT NULL,
  `rank` varchar(300) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8_general_ci;

--NEXT--

CREATE TABLE IF NOT EXISTS `_administrators` (
  `ForumId` int(11) NOT NULL,
  `Username` varchar(300) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8_general_ci;

--NEXT--

CREATE TABLE IF NOT EXISTS `_friendRequests` (
  `rel` int(11) NOT NULL,
  `user1` varchar(300) NOT NULL,
  `user2` varchar(300) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8_general_ci;

--NEXT--

CREATE TABLE IF NOT EXISTS `_friends` (
  `rel` int(11) NOT NULL,
  `user1` varchar(300) NOT NULL,
  `user2` varchar(300) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8_general_ci;

--NEXT--

CREATE TABLE IF NOT EXISTS `_moderators` (
  `subforumId` int(11) NOT NULL,
  `username` varchar(300) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8_general_ci;

--NEXT--

CREATE TABLE IF NOT EXISTS `_pendingFriendRequests` (
  `rel` int(11) NOT NULL,
  `user1` varchar(300) NOT NULL,
  `user2` varchar(300) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8_general_ci;

--NEXT--

CREATE TABLE IF NOT EXISTS `_suspended` (
  `subforumId` int(11) NOT NULL,
  `username` varchar(300) NOT NULL,
  `date` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8_general_ci;
