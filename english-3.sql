-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- 主機： localhost
-- 產生時間： 2022 年 12 月 02 日 09:34
-- 伺服器版本： 10.4.21-MariaDB
-- PHP 版本： 7.3.31

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 資料庫: `english`
--

-- --------------------------------------------------------

--
-- 資料表結構 `date`
--

CREATE TABLE `date` (
  `unit` int(11) NOT NULL,
  `class` int(11) NOT NULL,
  `startYmd` date NOT NULL,
  `endYmd` date NOT NULL,
  `category` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `type` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- 傾印資料表的資料 `date`
--

INSERT INTO `date` (`unit`, `class`, `startYmd`, `endYmd`, `category`, `type`) VALUES
(1, 301, '2022-12-02', '2022-12-03', 'listen', 'vocabulary'),
(1, 301, '2022-12-02', '2022-12-03', 'speak', 'vocabulary'),
(1, 301, '2022-12-02', '2022-12-03', 'speak', 'sentence'),
(1, 301, '2022-12-02', '2022-12-03', 'listen', 'sentence'),
(1, 301, '2022-12-02', '2022-12-03', 'listen', 'phrase'),
(1, 301, '2022-12-02', '2022-12-03', 'speak', 'phrase'),
(2, 301, '2022-12-02', '2022-12-03', 'listen', 'vocabulary'),
(2, 301, '2022-12-02', '2022-12-03', 'listen', 'sentence'),
(2, 301, '2022-12-02', '2022-12-03', 'listen', 'phrase'),
(2, 301, '2022-12-02', '2022-12-03', 'speak', 'phrase'),
(2, 301, '2022-12-02', '2022-12-03', 'speak', 'sentence'),
(2, 301, '2022-12-02', '2022-12-03', 'speak', 'vocabulary');

-- --------------------------------------------------------

--
-- 資料表結構 `error`
--

CREATE TABLE `error` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `category` text NOT NULL,
  `type` text NOT NULL,
  `unit` int(11) NOT NULL,
  `en` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- 傾印資料表的資料 `error`
--

INSERT INTO `error` (`id`, `user_id`, `category`, `type`, `unit`, `en`) VALUES
(61, 2, 'speak', 'vocabulary', 1, 'kite'),
(62, 2, 'speak', 'vocabulary', 1, 'pipe'),
(63, 2, 'speak', 'vocabulary', 1, 'night'),
(67, 2, 'listen', 'vocabulary', 1, 'kit'),
(68, 2, 'listen', 'vocabulary', 1, 'night');

-- --------------------------------------------------------

--
-- 資料表結構 `history`
--

CREATE TABLE `history` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `unit` int(11) NOT NULL,
  `listen_p` int(11) NOT NULL DEFAULT 0,
  `speak_p` int(11) DEFAULT 0,
  `listen_c` int(11) DEFAULT 0,
  `speak_c` int(11) DEFAULT 0,
  `type` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- 傾印資料表的資料 `history`
--

INSERT INTO `history` (`id`, `user_id`, `unit`, `listen_p`, `speak_p`, `listen_c`, `speak_c`, `type`) VALUES
(83, 2, 1, 0, 0, 80, 70, 'vocabulary'),
(84, 3, 1, 0, 0, -1, -1, 'vocabulary'),
(85, 2, 1, 0, 0, -1, -1, 'sentence'),
(86, 3, 1, 0, 0, -1, -1, 'sentence'),
(87, 2, 1, 0, 0, -1, -1, 'phrase'),
(88, 3, 1, 0, 0, -1, -1, 'phrase'),
(89, 2, 2, 0, 0, -1, -1, 'vocabulary'),
(90, 3, 2, 0, 0, -1, -1, 'vocabulary'),
(91, 2, 2, 0, 0, -1, -1, 'sentence'),
(92, 3, 2, 0, 0, -1, -1, 'sentence'),
(93, 2, 2, 0, 0, -1, -1, 'phrase'),
(94, 3, 2, 0, 0, -1, -1, 'phrase');

-- --------------------------------------------------------

--
-- 資料表結構 `textbook`
--

CREATE TABLE `textbook` (
  `id` int(11) NOT NULL,
  `unit` int(11) NOT NULL,
  `class` int(11) NOT NULL,
  `category` text COLLATE utf8_unicode_ci NOT NULL,
  `type` text COLLATE utf8_unicode_ci NOT NULL,
  `en` text COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- 傾印資料表的資料 `textbook`
--

INSERT INTO `textbook` (`id`, `unit`, `class`, `category`, `type`, `en`) VALUES
(5, 1, 302, 'listen', 'vocabulary', 'egg'),
(6, 1, 302, 'listen', 'vocabulary', 'fish'),
(7, 1, 302, 'listen', 'vocabulary', 'girl'),
(8, 1, 302, 'listen', 'vocabulary', 'hand'),
(13, 2, 302, 'listen', 'vocabulary', 'monkey'),
(14, 2, 302, 'listen', 'vocabulary', 'nose'),
(15, 2, 302, 'listen', 'vocabulary', 'orange'),
(16, 2, 302, 'listen', 'vocabulary', 'pencil'),
(17, 2, 302, 'listen', 'vocabulary', 'quiet'),
(24, 2, 302, 'listen', 'vocabulary', 'banana'),
(66, 3, 301, 'listen', 'vocabulary', 'chain'),
(67, 3, 301, 'listen', 'vocabulary', 'chair'),
(68, 3, 301, 'listen', 'vocabulary', 'pail'),
(69, 3, 301, 'listen', 'vocabulary', 'pair'),
(70, 3, 301, 'listen', 'vocabulary', 'cake'),
(71, 3, 301, 'listen', 'vocabulary', 'care'),
(72, 3, 301, 'listen', 'vocabulary', 'hate'),
(73, 3, 301, 'listen', 'vocabulary', 'hare'),
(74, 2, 301, 'listen', 'vocabulary', 'yellow'),
(75, 2, 301, 'listen', 'vocabulary', 'angry'),
(76, 2, 301, 'listen', 'vocabulary', 'fly'),
(77, 2, 301, 'listen', 'vocabulary', 'yummy'),
(78, 2, 301, 'listen', 'vocabulary', 'city'),
(79, 2, 301, 'listen', 'vocabulary', 'cry'),
(80, 2, 301, 'listen', 'vocabulary', 'yucky'),
(81, 2, 301, 'listen', 'vocabulary', 'puppy'),
(82, 2, 301, 'listen', 'vocabulary', 'sky'),
(94, 1, 301, 'listen', 'vocabulary', 'kit'),
(95, 1, 301, 'listen', 'vocabulary', 'kite'),
(96, 1, 301, 'listen', 'vocabulary', 'pip'),
(97, 1, 301, 'listen', 'vocabulary', 'pipe'),
(98, 1, 301, 'listen', 'vocabulary', 'pie'),
(99, 1, 301, 'listen', 'vocabulary', 'night'),
(100, 1, 301, 'listen', 'vocabulary', 'tie'),
(102, 4, 301, 'listen', 'vocabulary', 'book'),
(103, 4, 301, 'listen', 'vocabulary', 'boot'),
(104, 4, 301, 'listen', 'vocabulary', 'foot'),
(105, 4, 301, 'listen', 'vocabulary', 'food'),
(106, 4, 301, 'listen', 'vocabulary', 'look'),
(107, 4, 301, 'listen', 'vocabulary', 'pool'),
(108, 1, 301, 'listen', 'phrase', 'across from'),
(109, 1, 301, 'listen', 'phrase', 'behind'),
(110, 1, 301, 'listen', 'phrase', 'between and'),
(111, 1, 301, 'listen', 'phrase', 'in front of'),
(113, 1, 301, 'speak', 'vocabulary', 'kit'),
(114, 1, 301, 'speak', 'vocabulary', 'kite'),
(115, 1, 301, 'speak', 'vocabulary', 'pip'),
(116, 1, 301, 'speak', 'vocabulary', 'pipe'),
(117, 1, 301, 'speak', 'vocabulary', 'pie'),
(118, 1, 301, 'speak', 'vocabulary', 'night'),
(119, 1, 301, 'speak', 'vocabulary', 'tie'),
(120, 1, 301, 'speak', 'vocabulary', 'light'),
(121, 1, 301, 'speak', 'phrase', 'across from'),
(122, 1, 301, 'speak', 'phrase', 'behind'),
(123, 1, 301, 'speak', 'phrase', 'between and'),
(124, 1, 301, 'speak', 'phrase', 'in front of'),
(125, 1, 301, 'listen', 'sentence', 'where is the ball'),
(126, 1, 301, 'listen', 'sentence', 'It is front of the TV'),
(128, 1, 301, 'listen', 'sentence', 'Is the police station across from the tea shop'),
(130, 1, 301, 'listen', 'sentence', 'It is between the clinic and the bakery'),
(131, 1, 301, 'speak', 'sentence', 'where is the ball'),
(132, 1, 301, 'speak', 'sentence', 'It is front of the TV'),
(133, 1, 301, 'speak', 'sentence', 'Is the police station across from the tea shop'),
(134, 1, 301, 'speak', 'sentence', 'It is between the clinic and the bakery'),
(136, 2, 301, 'speak', 'vocabulary', 'yellow'),
(137, 2, 301, 'speak', 'vocabulary', 'angry'),
(138, 2, 301, 'speak', 'vocabulary', 'fly'),
(139, 2, 301, 'speak', 'vocabulary', 'yummy'),
(140, 2, 301, 'speak', 'vocabulary', 'city'),
(141, 2, 301, 'speak', 'vocabulary', 'cry'),
(142, 2, 301, 'speak', 'vocabulary', 'yucky'),
(143, 2, 301, 'speak', 'vocabulary', 'puppy'),
(144, 2, 301, 'speak', 'vocabulary', 'sky'),
(145, 2, 301, 'listen', 'phrase', 'next to'),
(146, 2, 301, 'listen', 'phrase', 'go to'),
(147, 2, 301, 'listen', 'phrase', 'ask for'),
(148, 2, 301, 'listen', 'phrase', 'look at'),
(149, 2, 301, 'speak', 'phrase', 'next to'),
(150, 2, 301, 'speak', 'phrase', 'go to'),
(151, 2, 301, 'speak', 'phrase', 'ask for'),
(152, 2, 301, 'speak', 'phrase', 'look at'),
(153, 2, 301, 'speak', 'sentence', 'How are you'),
(154, 2, 301, 'speak', 'sentence', 'I am fine'),
(155, 2, 301, 'speak', 'sentence', 'How old are you'),
(156, 2, 301, 'speak', 'sentence', 'I am twelve'),
(157, 2, 301, 'speak', 'sentence', 'How do you feel'),
(158, 2, 301, 'speak', 'sentence', 'I feel sick'),
(159, 2, 301, 'listen', 'sentence', 'How are you'),
(160, 2, 301, 'listen', 'sentence', 'I am fine'),
(161, 2, 301, 'listen', 'sentence', 'How old are you'),
(162, 2, 301, 'listen', 'sentence', 'I am twelve'),
(163, 2, 301, 'listen', 'sentence', 'How do you feel'),
(164, 2, 301, 'listen', 'sentence', 'I feel sick'),
(166, 1, 301, 'listen', 'vocabulary', 'light');

-- --------------------------------------------------------

--
-- 資料表結構 `todaywords`
--

CREATE TABLE `todaywords` (
  `id` int(11) NOT NULL,
  `text` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- 傾印資料表的資料 `todaywords`
--

INSERT INTO `todaywords` (`id`, `text`) VALUES
(1, 'No fear of words, no fear of years.'),
(2, 'Don’t let the past steal your present.'),
(3, 'Stars can’t shine without darkness.');

-- --------------------------------------------------------

--
-- 資料表結構 `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `password` text COLLATE utf8_unicode_ci NOT NULL,
  `identity` text COLLATE utf8_unicode_ci NOT NULL,
  `name` text COLLATE utf8_unicode_ci NOT NULL,
  `myclass` int(11) NOT NULL,
  `gender` text COLLATE utf8_unicode_ci NOT NULL,
  `partner` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- 傾印資料表的資料 `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `identity`, `name`, `myclass`, `gender`, `partner`) VALUES
(1, 'hello', '1234', 'teacher', '小米', 301, '女', 1),
(2, 'student', '1234', 'student', '阿米', 301, '女', 1),
(3, 'student2', '1234', 'student', '阿米2', 301, '女', 3),
(4, 'student3', '1234', 'student', '阿米3', 302, '男', 1);

--
-- 已傾印資料表的索引
--

--
-- 資料表索引 `error`
--
ALTER TABLE `error`
  ADD PRIMARY KEY (`id`);

--
-- 資料表索引 `history`
--
ALTER TABLE `history`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- 資料表索引 `textbook`
--
ALTER TABLE `textbook`
  ADD PRIMARY KEY (`id`);

--
-- 資料表索引 `todaywords`
--
ALTER TABLE `todaywords`
  ADD PRIMARY KEY (`id`);

--
-- 資料表索引 `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- 在傾印的資料表使用自動遞增(AUTO_INCREMENT)
--

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `error`
--
ALTER TABLE `error`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=69;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `history`
--
ALTER TABLE `history`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=95;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `textbook`
--
ALTER TABLE `textbook`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=167;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `todaywords`
--
ALTER TABLE `todaywords`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- 已傾印資料表的限制式
--

--
-- 資料表的限制式 `history`
--
ALTER TABLE `history`
  ADD CONSTRAINT `history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
