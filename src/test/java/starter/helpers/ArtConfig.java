package starter.helpers;

import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(
    initializers = {ConfigDataApplicationContextInitializer.class}
)
@ComponentScan
public class ArtConfig {
}
