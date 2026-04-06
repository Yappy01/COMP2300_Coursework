package Service;

import DBHandling.StiDatabase;
import Models.StiEntry;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class StiService {
    private final StiDatabase stiDatabase = new StiDatabase();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public void searchByNameAsync(List<StiEntry> data, String keyword, Consumer<ArrayList<StiEntry>> onSucceeded, Consumer<Throwable> onFailed) {
        Task<ArrayList<StiEntry>> task = new Task<ArrayList<StiEntry>>() {
            @Override
            protected ArrayList<StiEntry> call() throws Exception {
                if (keyword == null || keyword.isBlank()) return new ArrayList<>(data);
                return data.stream()
                        .filter(sti -> sti.getName() != null &&
                                sti.getName().toLowerCase().contains(keyword.toLowerCase()))
                        .collect(Collectors.toCollection(ArrayList::new));
            }
        };
        task.setOnSucceeded(e -> {
            onSucceeded.accept(task.getValue());
        });
        task.setOnFailed(e -> {
            onFailed.accept(task.getException());
        });
        executor.submit(task);
    }

    public void searchBySymptomsAsync(List<StiEntry> data, String keyword, Consumer<ArrayList<StiEntry>> onSucceeded, Consumer<Throwable> onFailed) {
        Task<ArrayList<StiEntry>> task = new Task<ArrayList<StiEntry>>() {
            @Override
            protected ArrayList<StiEntry> call() throws Exception {
                if (keyword == null || keyword.isBlank()) return new ArrayList<>(data);
                return data.stream()
                        .filter(sti -> sti.getName() != null &&
                                sti.getSymptoms().toLowerCase().contains(keyword.toLowerCase()))
                        .collect(Collectors.toCollection(ArrayList::new));
            }
        };
        task.setOnSucceeded(e -> {
            onSucceeded.accept(task.getValue());
        });
        task.setOnFailed(e -> {
            onFailed.accept(task.getException());
        });
        executor.submit(task);
    }

    public void searchByRiskLevelAsync(List<StiEntry> data, String keyword, Consumer<ArrayList<StiEntry>> onSucceeded, Consumer<Throwable> onFailed) {
        Task<ArrayList<StiEntry>> task = new Task<ArrayList<StiEntry>>() {
            @Override
            protected ArrayList<StiEntry> call() throws Exception {
                if (keyword == null || keyword.isBlank()) return new ArrayList<>(data);
                try {
                    int risk = Integer.parseInt(keyword);

                    return data.stream()
                            .filter(sti -> sti.getRiskLevel() == risk)
                            .collect(Collectors.toCollection(ArrayList::new));

                } catch (NumberFormatException e) {
                    return new ArrayList<>(); // return empty ArrayList
                }
            }
        };
        task.setOnSucceeded(e -> {
            onSucceeded.accept(task.getValue());
        });
        task.setOnFailed(e -> {
            onFailed.accept(task.getException());
        });
        executor.submit(task);
    }

    public void getAllAsync(Consumer<ArrayList<StiEntry>> onSucceeded, Consumer<Throwable> onFailed){
        Task<ArrayList<StiEntry>> task = new Task<ArrayList<StiEntry>>() {
            @Override
            protected ArrayList<StiEntry> call() throws Exception {
                return stiDatabase.getAll();
            }
        };
        task.setOnSucceeded(e -> {
            onSucceeded.accept(task.getValue());
        });
        task.setOnFailed(e -> {
            onFailed.accept(task.getException());
        });
        executor.submit(task);
    }
}
