package com.urfu.GorohSlot.database;

import com.urfu.GorohSlot.bot.Bot;
import com.urfu.GorohSlot.bot.User;
import com.urfu.GorohSlot.chat.ChatController;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHandler {

    private static final String URL = "jdbc:sqlite:./resources/playersdb.db";
    private static final String SELECT = "select * from userInfo where userID = ?";
    private static final String INSERT = "insert into userInfo values (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "update userInfo " + "set balance = ?, " + "bet = ?, " +  "mode = ?, "
            + "userName = ?, " + "userFirstName = ?, " + "userLastName = ?" + "where userID = ?";
    private static final String SELECTMODE = "select * from userInfo where mode = ?";
    private static Connection connection;

    public static User getUser(String userId, String userName, String userFirstName, String userLastName){
        if (existUser(userId, connection)) {
            assert connection != null;
            return getDBUser(userId, userName, userFirstName, userLastName);

        } else {
            User user = new User(userId, userFirstName, userLastName, userName, 100, 25, "Автомат 3x3");
            create(user);
            return user;
        }
    }

    private static void connection() {
        try {
            connection = DriverManager.getConnection(URL);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static boolean existUser(String userId, Connection connection) {
        try(PreparedStatement preparedStatement = connection.prepareStatement(SELECT)) {
            preparedStatement.setString(1, userId);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private static User getDBUser(String userId, String userName, String userFirstName, String userLastName) {
        PreparedStatement preparedStatement;
        User user = null;
        try {
            preparedStatement = connection.prepareStatement(SELECT);
            preparedStatement.setString(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                user =  new User(userId, userFirstName,
                        userLastName, userName,
                        Long.parseLong(resultSet.getString("balance")),
                        Integer.parseInt(resultSet.getString("bet")),
                        resultSet.getString("mode"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return user;
    }

    public static void createChatUsers(){
        connection();
        PreparedStatement preparedStatement;
        User user;
        try {
            preparedStatement = connection.prepareStatement(SELECTMODE);
            preparedStatement.setString(1, "Режим Курилка");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                user =  new User(resultSet.getString("userID"), resultSet.getString("userFirstName"),
                        resultSet.getString("userLastName"), resultSet.getString("userName"),
                        Long.parseLong(resultSet.getString("balance")),
                        Integer.parseInt(resultSet.getString("bet")),
                        resultSet.getString("mode"));
                ChatController.chatUsers.add(user);
                Bot.userData.put(user.getUserId(), user);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void update(User user) {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(UPDATE);
            preparedStatement.setString(1, Long.toString(user.getBalance()));
            preparedStatement.setString(2, Long.toString(user.getBet()));
            preparedStatement.setString(3, user.getMode());
            preparedStatement.setString(4, user.getUserName());
            preparedStatement.setString(5, user.getUserFirstname());
            preparedStatement.setString(6, user.getUserLastName());
            preparedStatement.setString(7, user.getUserId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void create(User user){
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(INSERT);
            preparedStatement.setString(1, user.getUserId());
            preparedStatement.setString(2, Long.toString(user.getBalance()));
            preparedStatement.setString(3, Long.toString(user.getBet()));
            preparedStatement.setString(4, user.getMode());
            preparedStatement.setString(5, user.getUserName());
            preparedStatement.setString(6, user.getUserFirstname());
            preparedStatement.setString(7, user.getUserLastName());
            preparedStatement.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }

    }
}