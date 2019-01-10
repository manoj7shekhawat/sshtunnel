package com.jumphost;
/*
 * Copyright (c) 2013-2019 and/or its affiliates. All rights reserved.
 */

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by manoj7shekhawat on 01/10/2019.
 */
public class Demo {

    /**
     * Logger
     */
    private static final org.apache.log4j.Logger LOGGER = Logger.getLogger(Demo.class);

    private static final String DB_USER_NAME = "sys as sysdba";
    private static final String DB_PASSWORD = "Welcome";
    private static final String DB_SYSTEM_IDENTIFIER = "primdb.dbhost.oracle.com";
    private static final String CONNECTION_CLASS_NAME = "oracle.jdbc.pool.OracleDataSource";

    private static String SSH_KEY_FILE_PATH = "path/to/id_rsa";

    private static String SSH_USER = "root";
    private static String BASTION_HOST = "bastion.oracle.com";
    private static String REMOTE_HOST = "remote.oracle.com";
    private static String DB_HOST = "database.dbhost.oracle.com";

    public static void main(String[] arg){

        try{

            // Create JSch object and identity file
            JSch jsch = new JSch();
            jsch.addIdentity(SSH_KEY_FILE_PATH);

            Session session = null;
            Session[] sessions = new Session[2];


            LOGGER.info("Attempting connection to BASTION_HOST");
            sessions[0] = session = jsch.getSession(SSH_USER, BASTION_HOST, 22);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            config.put("Compression", "yes");
            config.put("ConnectionAttempts", "2");
            session.setConfig(config);
            session.connect();
            LOGGER.info("Connected to BASTION_HOST");

            // Set port forwarding hop 1
            LOGGER.info("Attempting to start port forwarding to REMOTE_HOST");
            int assignedPort = session.setPortForwardingL(7777, REMOTE_HOST, 22);
            LOGGER.info("Completed port forwarding to REMOTE_HOST on port: " + assignedPort);

            // Open session2
            LOGGER.info("Attempting session2 on localhost");
            sessions[1] = session = jsch.getSession(SSH_USER, "localhost", assignedPort);
            session.setHostKeyAlias(REMOTE_HOST);
            session.setConfig(config);
            session.connect();
            LOGGER.info("Session2 on localhost Connected to port: " + assignedPort);

            // Start another port forwarding hop
            LOGGER.info("Attempting to start port forwarding using localhost session and DB host");
            int assignedPort2 = session.setPortForwardingL(9999, DB_HOST, 1521);
            LOGGER.info("Completed port forwarding  localhost: " + 9999 + " -> "+DB_HOST+":" + 1521 );

            LOGGER.info("Attempting DB connection");
            testDBConnection();
            LOGGER.info("DB connection test SUCCESSFUL");
            // Close tunnel
            LOGGER.info("Closing tunnels");
            for (int i = sessions.length - 1; i >= 0; i--) {
                LOGGER.info("Closing " + sessions[i].getUserName() + "@" + sessions[i].getHost());
                sessions[i].disconnect();
            }

            return;
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    private static DataSource testDBConnection() {

        try {
            Class.forName(CONNECTION_CLASS_NAME);
            Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//localhost:9999/" + DB_SYSTEM_IDENTIFIER, DB_USER_NAME, DB_PASSWORD);
            boolean reachable = conn.isValid(10);
            LOGGER.info("reachable: " + reachable);
        } catch (Exception e) {
            LOGGER.fatal("Exception: " + e);
        }
        return null;
    }

}
