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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpRequest.HttpPart;

//import ch.interlis.iox.IoxEvent;
//import ch.interlis.iox.ObjectEvent;
//import ch.interlis.iox_j.EndBasketEvent;
//import ch.interlis.iox_j.EndTransferEvent;
//import ch.interlis.ioxwkf.gpkg.GeoPackageReader;
//import ch.interlis.ioxwkf.shp.ShapeWriter;

import com.google.cloud.functions.HttpResponse;

public class Gpkg2Shp implements HttpFunction {
    Logger log = LoggerFactory.getLogger(Gpkg2Shp.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {        
        // Create folder for the shape files.
        File tmpFolder = Files.createTempDirectory("gpkg2shpws-").toFile();
        if (!tmpFolder.exists()) {
            tmpFolder.mkdirs();
        }
        log.info("tmpFolder {}", tmpFolder.getAbsolutePath());
        
        // Copy uploaded gpkg file to temp folder.
        HttpPart fileHttpPart = request.getParts().get("file");
        String uploadedFileName = fileHttpPart.getFileName().orElse("data.gpkg");
        InputStream is = fileHttpPart.getInputStream();
        
        File uploadedFile = Paths.get(tmpFolder.getAbsolutePath(), uploadedFileName).toFile();
        java.nio.file.Files.copy(
                is, 
                uploadedFile.toPath(), 
                StandardCopyOption.REPLACE_EXISTING);
        is.close();
        
        // Get all geopackage tables that will be converted to shape file.
        List<String> tableNames = new ArrayList<String>();
        String url = "jdbc:sqlite:" + uploadedFile.getAbsolutePath();
        try (Connection conn = DriverManager.getConnection(url); Statement stmt = conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("SELECT tablename FROM T_ILI2DB_TABLE_PROP WHERE setting = 'CLASS'")) {
                while(rs.next()) {
                    tableNames.add(rs.getString("tablename"));
                    log.info(rs.getString("tablename"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new IllegalArgumentException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        // Convert (read -> write) tables
        // TODO: needs GeoTools >= 21.0
//        for (String tableName : tableNames) {
//            ShapeWriter writer = new ShapeWriter(Paths.get(tmpFolder.getAbsolutePath(), tableName+".shp").toFile());
//            writer.setDefaultSridCode("2056");
//            
//            GeoPackageReader reader = new GeoPackageReader(new File(uploadedFileName), tableName);        
//            IoxEvent event = reader.read();
//            while (event instanceof IoxEvent) {
//                if (event instanceof ObjectEvent) {
//                    writer.write(event);
//                }
//                event = reader.read();
//            }
//            
//            writer.write(new EndBasketEvent());
//            writer.write(new EndTransferEvent());
//
//            if (writer != null) {
//                writer.close();
//                writer = null;
//            }
//            if (reader != null) {
//                reader.close();
//                reader = null;
//            }
//        }


        // Send output back to client.
        try (OutputStream out = response.getOutputStream()) {
            response.setContentType("application/zip");
            response.appendHeader("Content-Length:", String.valueOf(uploadedFile.length()));
            String contentDisposition = String.format("attachment; filename=%s", uploadedFile.getName());
            response.appendHeader("Content-Disposition", contentDisposition);
            response.setStatusCode(200);

            Path path = uploadedFile.toPath();
            Files.copy(path, out);
            out.flush();
            
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatusCode(500);
        }
    }   
}
