package com.wisenut.ebk.spring;

import com.wisenut.ebk.spring.vo.GroupVo;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseConnector {

    private static final String URL = "jdbc:mariadb://172.17.208.35:13306";
    private static final String USERNAME = "wisenut";
    private static final String PASSWORD = "wisenut";

    public List<GroupVo> getDataFromDB() {
        List<GroupVo> groupNames  = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "SELECT oid, name, groupcode, parentoid, fullpathindex FROM VFTRGroup";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    GroupVo groupName  = new GroupVo();
                    groupName.setOid(resultSet.getString("oid"));
                    groupName.setName(resultSet.getString("name"));
                    groupName.setGroupcode(resultSet.getString("groupcode"));
                    groupName.setParentoid(resultSet.getString("parentoid"));
                    groupName.setFullpathindex(resultSet.getString("fullpathindex"));

                    groupNames.add(groupName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groupNames;
    }
}
