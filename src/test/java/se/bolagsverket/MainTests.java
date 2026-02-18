package se.bolagsverket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.bolagsverket.db.AbstractPostgresContainer;

@ActiveProfiles("test")
@SpringBootTest
class MainTests extends AbstractPostgresContainer {

    @Test
    void contextLoads() {
    }

}
