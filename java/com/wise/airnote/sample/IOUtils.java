package com.wise.airnote.sample;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;

public class IOUtils {

    private static final int DEFAULT_BUFFER_SIZE = 8192;

	public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    public static long copyLarge(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

	public static String readContent(Uri fileUri, ContentResolver cr) {
        if (fileUri == null) {
        	return null;
        }
            
        InputStream is = null;
        try {
            is = cr.openInputStream(fileUri);
            ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
            IOUtils.copy(is, out);
            out.flush();
            out.close();
            String s = out.toString();
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
	}

}
