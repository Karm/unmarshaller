package biz.karms.java.unmarshalling.web;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Michal Karm Babacek
 */
@WebServlet(name = "MarshalerServlet", urlPatterns = {"/marshaler", "/marshaler/*"})
public class Marshaler extends HttpServlet {
    private static final Logger log = Logger.getLogger(Marshaler.class.getName());

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        final String mimeType = "application/octet-stream";
        final String headerKey = "Content-Disposition";
        final String headerValue = String.format("attachment; filename=\"%s\"", Frog.class.getName());
        final Frog frog = new Frog("Karel");
        OutputStream outptStream = null;
        ByteArrayOutputStream output = null;
        ObjectOutputStream objOutput = null;
        try {
            outptStream = httpServletResponse.getOutputStream();
            output = new ByteArrayOutputStream();
            objOutput = new ObjectOutputStream(output);
            objOutput.writeObject(frog);
            final byte[] data = output.toByteArray();
            outptStream.write(data);
            httpServletResponse.setHeader(headerKey, headerValue);
            httpServletResponse.setContentType(mimeType);
            httpServletResponse.setContentLength(data.length);
        } finally {
            if (outptStream != null) outptStream.close();
            if (objOutput != null) objOutput.close();
            if (output != null) output.close();
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final ServletFileUpload upload = new ServletFileUpload();
        try {
            FileItemIterator filesIter = upload.getItemIterator(req);
            while (filesIter.hasNext()) {
                final FileItemStream file = filesIter.next();
                InputStream in = null;
                ByteArrayOutputStream out = null;
                ObjectInputStream objectInputStream = null;
                try {
                    in = file.openStream();
                    out = new ByteArrayOutputStream();
                    byte[] buffer = new byte[4096];
                    int size;
                    while ((size = in.read(buffer, 0, buffer.length)) != -1) {
                        out.write(buffer, 0, size);
                    }
                    objectInputStream = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()));
                    resp.getWriter().print(((Frog) objectInputStream.readObject()).getName());
                } catch (Exception e) {
                    log.log(Level.SEVERE, "Error with deserialization.", e);
                } finally {
                    if (in != null) in.close();
                    if (out != null) out.close();
                    if (objectInputStream != null) objectInputStream.close();
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error with upload.", e);
        }
    }
}
