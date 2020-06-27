package ch.so.agi.functions;

import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.cloud.functions.HttpRequest.HttpPart;

import ch.interlis.iox.IoxException;

@RunWith(JUnit4.class)
public class Gpkg2ShpTest {
    @Mock private HttpRequest request;
    @Mock private HttpResponse response;  
    
    File targetDir;
    File zipFile;
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
        
    // Falls mehrere Unit-Tests kann/muss/darf das in die Test-Methoden
    // wandern.
    @Before
    public void beforeTest() throws IOException {
        MockitoAnnotations.initMocks(this);

        String path = "./src/test/data/ch.so.agi.av-gb-administrative-einteilung.gpkg";
        File gpkgFile = new File(path);
        FileInputStream gpkgFileInputStream = new FileInputStream(gpkgFile);
        
        Map<String, HttpPart> parts = new HashMap<String, HttpPart>();
        HttpPartImpl fileHttpPart = new HttpPartImpl();
        fileHttpPart.setFileName("ch.so.agi.av-gb-administrative-einteilung.gpkg");
        fileHttpPart.setInputStream(gpkgFileInputStream);
        parts.put("file", fileHttpPart);
        when(request.getParts()).thenReturn(parts);

        targetDir = folder.getRoot();
//        targetDir = new File("/Users/stefan/tmp/");

        zipFile = Paths.get(targetDir.getAbsolutePath(), "ch.so.agi.av-gb-administrative-einteilung.shp.zip").toFile();
        FileOutputStream zipFileOutputStream = new FileOutputStream(zipFile);
        when(response.getOutputStream()).thenReturn(zipFileOutputStream);
      }

    @Test
    public void gpkg2Shp_Ok() throws IoxException, IOException  {
      new Gpkg2Shp().service(request, response);
      
      UnzipFile unzipFile = new UnzipFile();
      unzipFile.exec(targetDir.toPath(), zipFile.toPath());
            
      FileDataStore dataStore = FileDataStoreFinder.getDataStore(new java.io.File(targetDir, "nachfuehrngskrise_gemeinde.shp"));
      SimpleFeatureSource featuresSource = dataStore.getFeatureSource();
      assertEquals(109, featuresSource.getFeatures().size());
      dataStore.dispose();
    }
}
