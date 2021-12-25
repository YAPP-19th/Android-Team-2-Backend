package com.yapp.sharefood.config.lock;

import com.yapp.sharefood.common.exception.DbUserLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
public class UserlevelLockSql implements UserLevelLock {
    private static final String GET_LOCK = "SELECT GET_LOCK(?, ?)";
    private static final String RELEASE_LOCK = "SELECT RELEASE_LOCK(?)";
    public static final int DEFAULT_USERLEVEL_LOCk_TIME_OUT = 2;
    private static final String LOCK_EXCEPTION_MESSAGE = "LOCK 을 수행하는 중에 오류가 발생하였습니다.";

    private final DataSource userLevelLockDataSource;

    public <T> T executeWithLock(String userLockName,
                                 int timeoutSeconds,
                                 Supplier<T> supplier) {
        try (Connection connection = userLevelLockDataSource.getConnection()) {
            try {
                log.info("start getLock=[{}], timeoutSeconds : [{}], connection=[{}]", userLockName, timeoutSeconds, connection);
                getLock(connection, userLockName, timeoutSeconds);
                log.info("success getLock=[{}], timeoutSeconds : [{}], connection=[{}]", userLockName, timeoutSeconds, connection);
                return supplier.get();

            } finally {
                log.info("start releaseLock=[{}], connection=[{}]", userLockName, connection);
                releaseLock(connection, userLockName);
                log.info("success releaseLock=[{}], connection=[{}]", userLockName, connection);
            }
        } catch (SQLException | DbUserLockException e) {
            log.warn("user level lock exception");
            throw new DbUserLockException(e.getMessage(), e);
        }
    }

    private void getLock(Connection connection,
                         String userLockName,
                         int timeoutseconds) throws SQLException {

        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_LOCK)) {
            preparedStatement.setString(1, userLockName);
            preparedStatement.setInt(2, timeoutseconds);

            checkResultSet(userLockName, preparedStatement, "GetLock_");
        }
    }

    private void releaseLock(Connection connection,
                             String userLockName) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(RELEASE_LOCK)) {
            preparedStatement.setString(1, userLockName);

            checkResultSet(userLockName, preparedStatement, "ReleaseLock_");
        }
    }

    private void checkResultSet(String userLockName,
                                PreparedStatement preparedStatement,
                                String type) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (!resultSet.next()) {
                log.error("USER LEVEL LOCK 쿼리 결과 값이 없습니다. type = [{}], userLockName : [{}], connection=[{}]", type, userLockName, preparedStatement.getConnection());
                throw new DbUserLockException(LOCK_EXCEPTION_MESSAGE);
            }
            int result = resultSet.getInt(1);
            if (result != 1) {
                log.error("USER LEVEL LOCK 쿼리 결과 값이 1이 아닙니다. type = [{}], result : [{}] userLockName : [{}], connection=[{}]", type, result, userLockName, preparedStatement.getConnection());
                throw new DbUserLockException(LOCK_EXCEPTION_MESSAGE);
            }
        }
    }
}
