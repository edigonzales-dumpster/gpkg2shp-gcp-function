package ch.so.agi.functions;

import static java.util.stream.Collectors.toMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.Part;

import com.google.cloud.functions.HttpRequest.HttpPart;

public class HttpPartImpl implements HttpPart {
    private Part part;
    private String fileName;
    private InputStream inputStream;

//    private HttpPartImpl(Part part) {
//        this.part = part;
//    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public Optional<String> getFileName() {
        return Optional.ofNullable(fileName);
    }
    
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return inputStream;
    }

    @Override
    public Optional<String> getContentType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getContentLength() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Optional<String> getCharacterEncoding() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        // TODO Auto-generated method stub
        return null;
    }

//    @Override
//    public Optional<String> getContentType() {
//        return Optional.ofNullable(part.getContentType());
//    }
//
//    @Override
//    public long getContentLength() {
//        return part.getSize();
//    }
//
//    @Override
//    public Optional<String> getCharacterEncoding() {
//        String contentType = getContentType().orElse(null);
//        if (contentType == null) {
//            return Optional.empty();
//        }
//        Pattern charsetPattern = Pattern.compile("(?i).*;\\s*charset\\s*=([^;\\s]*)\\s*(;|$)");
//        Matcher matcher = charsetPattern.matcher(contentType);
//        return matcher.matches() ? Optional.of(matcher.group(1)) : Optional.empty();
//    }
//
//    @Override
//    public BufferedReader getReader() throws IOException {
//        String encoding = getCharacterEncoding().orElse("utf-8");
//        return new BufferedReader(new InputStreamReader(getInputStream(), encoding));
//    }
//
//    @Override
//    public Map<String, List<String>> getHeaders() {
//        return part.getHeaderNames().stream().map(name -> new SimpleEntry<>(name, list(part.getHeaders(name))))
//                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
//    }
//
//    private static <T> List<T> list(Collection<T> collection) {
//        return (collection instanceof List<?>) ? (List<T>) collection : new ArrayList<>(collection);
//    }
}
