package biz.karms.java.unmarshalling.web;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
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
@MultipartConfig
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
    public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        final Part filePart = httpServletRequest.getPart("filedata");
        ByteArrayOutputStream out = null;
        InputStream filecontent = null;
        ObjectInputStream objectInputStream = null;
        try {
            out = new ByteArrayOutputStream();
            filecontent = filePart.getInputStream();
            int read = 0;
            final byte[] bytes = new byte[4096];

            while ((read = filecontent.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            objectInputStream = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()));
            httpServletResponse.getWriter().print(((Frog) objectInputStream.readObject()).getName());
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error with deserialization.", e);
        } finally {
            if (filecontent != null) filecontent.close();
            if (out != null) out.close();
            if (objectInputStream != null) objectInputStream.close();
        }
    }
}
