package Service;

import DBHandling.ComPostDatabase;
import DBHandling.ReportDatabase;
import DBHandling.UserRepository;
import Models.Post;
import Models.Report;
import javafx.concurrent.Task;
import utils.General;
import utils.Session;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ReportService {
    private final ReportDatabase reportDatabase = new ReportDatabase();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public void reportPost(Post post, String reason, Consumer<Boolean> onSucceeded, Consumer<Throwable> onFailed) {
        Task<Boolean> task = new Task() {
            @Override
            protected Object call() throws Exception {
                return reportDatabase.toggleReport(post.getPostId(), Session.getInstance().getUser().getUserId(), reason, new Timestamp(System.currentTimeMillis()));
            }
        };

        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public void deleteReportAsync(Report report, Consumer<Boolean> onSucceeded, Consumer<Throwable> onFailed) {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return reportDatabase.softDelete(report.getReportId());
            }
        };

        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public void getAllReportAsync(Consumer<ArrayList<Report>> onSucceeded, Consumer<Throwable> onFailed) {
        Task<ArrayList<Report>> task = new Task<>() {
            @Override
            protected ArrayList<Report> call() throws Exception {
                return reportDatabase.getAllReports();
            }
        };

        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public void resolvePostAsync(Report report,String resolutionDetails,Consumer<Boolean> onSucceeded, Consumer<Throwable> onFailed) {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return reportDatabase.resolveReport(report.getReportId(), resolutionDetails);
            }
        };

        General.setTask(task, onSucceeded, onFailed, executor);
    }
}
