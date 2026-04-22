package Service;

import DBHandling.ComPostDatabase;
import DBHandling.UserRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReportService {
    private final ComPostDatabase postRepository = new ComPostDatabase();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);


}
