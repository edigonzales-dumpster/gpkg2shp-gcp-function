package ch.so.agi.functions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpRequest.HttpPart;

import com.google.cloud.functions.HttpResponse;

public class Gpkg2Shp implements HttpFunction {
    Logger log = LoggerFactory.getLogger(Gpkg2Shp.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {        
        HttpPart fileHttpPart = request.getParts().get("file");
        String fileName = fileHttpPart.getFileName().orElse("data.gpkg");
        InputStream is = fileHttpPart.getInputStream();
                       
        File tmpFolder = Files.createTempDirectory("gpkg2shpws-").toFile();
        if (!tmpFolder.exists()) {
            tmpFolder.mkdirs();
        }
        log.info("tmpFolder {}", tmpFolder.getAbsolutePath());

        File targetFile = Paths.get(tmpFolder.getAbsolutePath(), fileName).toFile();
        java.nio.file.Files.copy(
                is, 
                targetFile.toPath(), 
                StandardCopyOption.REPLACE_EXISTING);
        is.close();

        try (OutputStream out = response.getOutputStream()) {
            response.setContentType("application/zip");
            response.appendHeader("Content-Length:", String.valueOf(targetFile.length()));
            String contentDisposition = String.format("attachment; filename=%s", targetFile.getName());
            response.appendHeader("Content-Disposition", contentDisposition);
            response.setStatusCode(200);

            Path path = targetFile.toPath();
            Files.copy(path, out);
            out.flush();
            
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatusCode(500);
        }
    }   
}
