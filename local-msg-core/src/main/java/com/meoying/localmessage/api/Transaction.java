package com.meoying.localmessage.api;

import java.sql.Connection;
import java.sql.SQLException;

public interface Transaction {
    Connection getConnection() throws SQLException;
}
