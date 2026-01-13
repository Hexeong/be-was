-- 1. 테이블 생성
CREATE TABLE IF NOT EXISTS USERS (
                                     userId VARCHAR(50) PRIMARY KEY,
    password VARCHAR(50) NOT NULL,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL
    );

CREATE TABLE IF NOT EXISTS ARTICLE (
                                       articleId VARCHAR(254) PRIMARY KEY,
    content VARCHAR(1023) NOT NULL,
    likeCnt INTEGER NOT NULL,
    writerId VARCHAR(50) NOT NULL,
    writerName VARCHAR(50) NOT NULL,
    createdAt VARCHAR(50) NOT NULL
    );

CREATE TABLE IF NOT EXISTS COMMENT (
                                       commentId VARCHAR(254) PRIMARY KEY,
    content VARCHAR(1023) NOT NULL,
    writerId VARCHAR(50) NOT NULL,
    writerName VARCHAR(50) NOT NULL,
    articleId VARCHAR(254) NOT NULL,
    createdAt VARCHAR(50) NOT NULL
    );


-- 2. 테스트 데이터 초기화

-- USERS
INSERT INTO USERS (userId, password, name, email) VALUES
                                                      ('user1', '1234', 'Alice', 'alice@example.com'),
                                                      ('user2', '1234', 'Bob', 'bob@example.com'),
                                                      ('user3', '1234', 'Charlie', 'charlie@example.com'),
                                                      ('user4', '1234', 'David', 'david@example.com'),
                                                      ('user5', '1234', 'Eve', 'eve@example.com');

-- ARTICLE (commentCnt 값 및 컬럼 제거)
INSERT INTO ARTICLE (articleId, content, likeCnt, writerId, writerName, createdAt) VALUES
                                                                                       ('article1', '안녕하세요, 첫 번째 게시글입니다.', 0, 'user1', 'Alice', '2024-03-01 10:00:00'),
                                                                                       ('article2', '두 번째 게시글 내용입니다.', 3, 'user2', 'Bob', '2024-03-01 11:00:00'),
                                                                                       ('article3', '세 번째 글입니다.', 5, 'user3', 'Charlie', '2024-03-01 12:00:00'),
                                                                                       ('article4', '네 번째 게시글입니다.', 12, 'user4', 'David', '2024-03-01 13:00:00'),
                                                                                       ('article5', '마지막 다섯 번째 게시글입니다.', 25, 'user5', 'Eve', '2024-03-01 14:00:00');

-- COMMENT
-- article1 댓글
INSERT INTO COMMENT (commentId, content, writerId, writerName, articleId, createdAt) VALUES
    ('comment1', 'Alice님 환영합니다!', 'user2', 'Bob', 'article1', '2024-03-01 10:05:00');

-- article2 댓글
INSERT INTO COMMENT (commentId, content, writerId, writerName, articleId, createdAt) VALUES
                                                                                         ('comment2', '좋은 글이네요.', 'user3', 'Charlie', 'article2', '2024-03-01 11:05:00'),
                                                                                         ('comment3', '저도 동감합니다.', 'user4', 'David', 'article2', '2024-03-01 11:10:00');

-- article3 댓글
INSERT INTO COMMENT (commentId, content, writerId, writerName, articleId, createdAt) VALUES
                                                                                         ('comment4', '세 번째 글 잘 읽었습니다.', 'user4', 'David', 'article3', '2024-03-01 12:05:00'),
                                                                                         ('comment5', '유익한 정보 감사합니다.', 'user5', 'Eve', 'article3', '2024-03-01 12:10:00'),
                                                                                         ('comment6', 'Charlie님 화이팅!', 'user1', 'Alice', 'article3', '2024-03-01 12:15:00');

-- article4 댓글
INSERT INTO COMMENT (commentId, content, writerId, writerName, articleId, createdAt) VALUES
                                                                                         ('comment7', '네 번째 글이군요.', 'user5', 'Eve', 'article4', '2024-03-01 13:05:00'),
                                                                                         ('comment8', '내용이 알차네요.', 'user1', 'Alice', 'article4', '2024-03-01 13:10:00'),
                                                                                         ('comment9', '잘 보고 갑니다.', 'user2', 'Bob', 'article4', '2024-03-01 13:15:00'),
                                                                                         ('comment10', '질문이 있습니다.', 'user3', 'Charlie', 'article4', '2024-03-01 13:20:00');

-- article5 댓글
INSERT INTO COMMENT (commentId, content, writerId, writerName, articleId, createdAt) VALUES
                                                                                         ('comment11', '마지막 글이네요 아쉽습니다.', 'user1', 'Alice', 'article5', '2024-03-01 14:05:00'),
                                                                                         ('comment12', 'Eve님 글 솜씨가 좋네요.', 'user2', 'Bob', 'article5', '2024-03-01 14:10:00'),
                                                                                         ('comment13', '다음 글도 기대할게요.', 'user3', 'Charlie', 'article5', '2024-03-01 14:15:00'),
                                                                                         ('comment14', '퍼가요~', 'user4', 'David', 'article5', '2024-03-01 14:20:00'),
                                                                                         ('comment15', '댓글 5개 달성!', 'user1', 'Alice', 'article5', '2024-03-01 14:25:00');