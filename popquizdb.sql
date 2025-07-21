/*
 Navicat Premium Dump SQL

 Source Server         : DB
 Source Server Type    : MySQL
 Source Server Version : 80300 (8.3.0)
 Source Host           : localhost:3306
 Source Schema         : popquizdb

 Target Server Type    : MySQL
 Target Server Version : 80300 (8.3.0)
 File Encoding         : 65001

 Date: 21/07/2025 14:12:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for answerdata
-- ----------------------------
DROP TABLE IF EXISTS `answerdata`;
CREATE TABLE `answerdata`  (
  `questionId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `userId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `selection` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `isCorrect` int NOT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of answerdata
-- ----------------------------
INSERT INTO `answerdata` VALUES ('12d23312ds1', '3333', 'A', 1);

-- ----------------------------
-- Table structure for commenttable
-- ----------------------------
DROP TABLE IF EXISTS `commenttable`;
CREATE TABLE `commenttable`  (
  `commentId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `questionId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `publisher` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `replyId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `time` datetime NOT NULL,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`commentId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of commenttable
-- ----------------------------
INSERT INTO `commenttable` VALUES ('0', '00', '00', '0', '2025-07-16 22:15:55', '这是系统顶级评论');
INSERT INTO `commenttable` VALUES ('1', '12', 'wede2', '0', '2025-07-15 22:39:14', '哈哈');
INSERT INTO `commenttable` VALUES ('2', '12', 'wede2', '1', '2025-07-17 22:39:28', '哈哈');
INSERT INTO `commenttable` VALUES ('3', '12', 'wede2', '2', '2025-07-15 22:39:47', '哈哈');
INSERT INTO `commenttable` VALUES ('4', '12', 'wede2', '1', '2025-07-15 22:39:57', '哈哈');
INSERT INTO `commenttable` VALUES ('5', '12', 'wede2', '0', '2025-08-13 22:40:17', '哈哈');

-- ----------------------------
-- Table structure for filetable
-- ----------------------------
DROP TABLE IF EXISTS `filetable`;
CREATE TABLE `filetable`  (
  `fileId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `format` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `speechId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`fileId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of filetable
-- ----------------------------

-- ----------------------------
-- Table structure for questiontable
-- ----------------------------
DROP TABLE IF EXISTS `questiontable`;
CREATE TABLE `questiontable`  (
  `questionId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `speechId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `optionA` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `optionB` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `optionC` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `optionD` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `answer` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `isUsed` int NOT NULL,
  PRIMARY KEY (`questionId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of questiontable
-- ----------------------------
INSERT INTO `questiontable` VALUES ('12d23312ds1', '3bcwe3213bdui1', '1+1', '1', '2', '3', '4', 'B', 1);
INSERT INTO `questiontable` VALUES ('2312321d', '3bcwe3213bdui1', '2+2', '1', '2', '5', '4', 'D', 1);
INSERT INTO `questiontable` VALUES ('321d234s1', '3bcwe3213bdui1', '1+2', '2', '3', '4', '5', 'B', 1);

-- ----------------------------
-- Table structure for speechtable
-- ----------------------------
DROP TABLE IF EXISTS `speechtable`;
CREATE TABLE `speechtable`  (
  `speechId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `organizer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `speaker` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `startTime` datetime NOT NULL,
  `endTime` datetime NOT NULL,
  `status` int NOT NULL,
  PRIMARY KEY (`speechId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of speechtable
-- ----------------------------
INSERT INTO `speechtable` VALUES ('321fwe21312', 'J2EE', 'J2EE', '1111', '2222', '2025-07-17 20:59:52', '2025-07-17 20:59:55', 1);
INSERT INTO `speechtable` VALUES ('3bcwe3213bdui1', 'web开发课程演讲', 'web开发结束...', '1111', '2222', '2025-07-15 13:45:25', '2025-07-15 22:00:03', 2);

-- ----------------------------
-- Table structure for spittable
-- ----------------------------
DROP TABLE IF EXISTS `spittable`;
CREATE TABLE `spittable`  (
  `spitId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `speechId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `time` datetime NOT NULL,
  PRIMARY KEY (`spitId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of spittable
-- ----------------------------
INSERT INTO `spittable` VALUES ('lt6i8rhe', '321fwe21312', '演讲质量差1', '2025-07-21 08:06:38');
INSERT INTO `spittable` VALUES ('w23787hg', '321fwe21312', '演讲质量差', '2025-07-21 08:06:38');

-- ----------------------------
-- Table structure for surelation
-- ----------------------------
DROP TABLE IF EXISTS `surelation`;
CREATE TABLE `surelation`  (
  `speechId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `userId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of surelation
-- ----------------------------
INSERT INTO `surelation` VALUES ('3bcwe3213bdui1', '3333');
INSERT INTO `surelation` VALUES ('321fwe21312', '3333');

-- ----------------------------
-- Table structure for tqrelation
-- ----------------------------
DROP TABLE IF EXISTS `tqrelation`;
CREATE TABLE `tqrelation`  (
  `testId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `questionId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tqrelation
-- ----------------------------
INSERT INTO `tqrelation` VALUES ('a045pspx', '2312321d');
INSERT INTO `tqrelation` VALUES ('a045pspx', '321d234s1');

-- ----------------------------
-- Table structure for tsrelation
-- ----------------------------
DROP TABLE IF EXISTS `tsrelation`;
CREATE TABLE `tsrelation`  (
  `testId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `speechId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`testId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tsrelation
-- ----------------------------
INSERT INTO `tsrelation` VALUES ('a045pspx', '3bcwe3213bdui1');

-- ----------------------------
-- Table structure for usertable
-- ----------------------------
DROP TABLE IF EXISTS `usertable`;
CREATE TABLE `usertable`  (
  `userId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `userphone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `role` int NOT NULL,
  PRIMARY KEY (`userId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of usertable
-- ----------------------------
INSERT INTO `usertable` VALUES ('00', '系统账户', '00000000', '系统账户', '00000000', 0);
INSERT INTO `usertable` VALUES ('1111', '437121@qq.com', '123456', '王五', '13123', 0);
INSERT INTO `usertable` VALUES ('2222', '437111@qq.com', '123456', '李四', '13123', 0);
INSERT INTO `usertable` VALUES ('3333', '437110@qq.com', '123456', '张三', '10086', 0);

SET FOREIGN_KEY_CHECKS = 1;
