package com.tech.integrationTest.dao;

import com.tech.dao.UsersDAO;
import com.tech.entities.Users;
import com.tech.integrationTest.BaseDatabaseTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsersDAOTest extends BaseDatabaseTest {
    @Autowired
    private UsersDAO usersDAO;
    @Disabled
    @Test
    void testFindAll() {
        List<Users> users = usersDAO.findAll();
        assertEquals(0, users.size());
    }
}