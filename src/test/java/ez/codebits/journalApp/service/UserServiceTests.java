package ez.codebits.journalApp.service;

import ez.codebits.journalApp.entity.User;
import ez.codebits.journalApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    @InjectMocks
    private UserRepository userRepository;
    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Disabled
    @ParameterizedTest
    @ArgumentsSource(UserArgumentsProvider.class)
    void testSaveNewUser(User user) {
        assertTrue(userService.saveNewUser(user));
    }

    @Test
    @Disabled
    void testFindByUsername() {
        assertNotNull(userRepository.findByUsername("ram"));
    }
}
