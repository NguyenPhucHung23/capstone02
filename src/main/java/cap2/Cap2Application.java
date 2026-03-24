package cap2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class  Cap2Application {

	public static void main(String[] args) {
		SpringApplication.run(Cap2Application.class, args);
	}

}
