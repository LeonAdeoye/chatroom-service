package com.leon.services;

import com.leon.models.User;
import com.leon.repositories.RoomRepository;
import com.leon.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RoomServiceTest
{
	@MockBean
	private UserRepository userRepositoryMock;

	@MockBean
	private RoomRepository roomRepositoryMock;

	@Autowired
	private RoomService roomService;

	@Test
	public void whenPassedValidFullName_addUser_shouldCallSaveMethodOfRepositoryMock()
	{
		// Act
		roomService.addUser("Horatio Adeoye");
		// Assert
		verify(userRepositoryMock, times(1)).save(new User("Horatio Adeoye"));
	}

	@Test
	public void whenUsersExist_getAllUsers_shouldCallFindAllMethodOfRepositoryMock()
	{
		// Arrange and Act
		roomService.addUser("Horatio Adeoye");
		// Assert
		verify(userRepositoryMock, times(1)).findAll();
	}
}
