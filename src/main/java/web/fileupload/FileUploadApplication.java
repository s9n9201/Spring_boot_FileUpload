package web.fileupload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import web.fileupload.service.FilesStorageService;

@SpringBootApplication
public class FileUploadApplication implements CommandLineRunner {

    @Autowired
    FilesStorageService filesStorageService;

    public static void main(String[] args) {
        SpringApplication.run(FileUploadApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        filesStorageService.deleteAll();
        filesStorageService.init();
    }
}
