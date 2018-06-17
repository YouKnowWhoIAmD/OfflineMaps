package com.junjunguo.pocketmaps.downloader;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import com.junjunguo.pocketmaps.model.listeners.MapDownloadListener;
import com.junjunguo.pocketmaps.util.Constant;
import com.junjunguo.pocketmaps.util.Variable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * Created by Google Deveoper Student Club
 * Varshit Ratna(leader)
 * Devaraj Akhil(Core team)
 */

public class MapDownloader {
    private int timeout;
    private File downloadedFile;
    private boolean startNewDownload;
    private boolean downloadStatusOk = false;
    /**
     * total length of the file
     */
    private long fileLength = 0;

    public MapDownloader() {
        timeout = 9000;
        startNewDownload = true;
    }

    /**
     * @param urlStr           downloadFile url
     * @param toFile           downloadedFile path
     * @param downloadListener downloadFile progress listener
     */
    public void downloadFile(String urlStr, String toFile, String mapName, MapDownloadListener downloadListener) {
        Variable.getVariable().setPausedMapName(mapName);
        HttpURLConnection connection = null;
        InputStream in = null;
        FileOutputStream writer = null;
        long progressLength = 0;
        try {
            prepareDownload(urlStr, toFile);
            connection = createConnection(urlStr);
            Variable.getVariable().setDownloadStatus(Constant.DOWNLOADING);
            if (!startNewDownload) {
                connection.setRequestProperty("Range", "bytes=" + downloadedFile.length() + "-");
            }
            in = new BufferedInputStream(connection.getInputStream(), Constant.BUFFER_SIZE);

            if (!startNewDownload) {
                progressLength += downloadedFile.length();
                // append to exist downloadedFile
                writer = new FileOutputStream(toFile, true);
            } else {
                writer = new FileOutputStream(toFile);
                // save remote last modified data to local
                Variable.getVariable().setMapLastModified(connection.getHeaderField("Last-Modified"));
            }

            if (fileLength <= 0) { fileLength = 10000000000L; }
            byte[] buffer = new byte[Constant.BUFFER_SIZE];
            int count = 0;
            while (Variable.getVariable().getDownloadStatus() == Constant.DOWNLOADING)
            {
              count = in.read(buffer);
              if (count == -1) { break; }
              progressLength += count;
              writer.write(buffer, 0, count);
              // progress....
              downloadListener.progressUpdate((int) (progressLength * 100 / fileLength));
            }
            if (count==-1)
            {
                downloadListener.onStartUnpacking();
                Variable.getVariable().setDownloadStatus(Constant.COMPLETE);
                Variable.getVariable().setPausedMapName("");
                new MapUnzip().unzip(toFile,
                        new File(Variable.getVariable().getMapsFolder(), mapName + "-gh").getAbsolutePath());
                downloadListener.downloadFinished(mapName);
            } else {
                Variable.getVariable().setMapFinishedPercentage((int) (progressLength * 100 / fileLength));
            }

        } catch (IOException e) {
            Variable.getVariable().setDownloadStatus(Constant.PAUSE);
            e.printStackTrace();
            downloadStatusOk = false;
            return;
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (in != null) {
                    in.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        downloadStatusOk = true;
    }
    
    public boolean isDownloadStatusOk() { return downloadStatusOk; }

    /**
     * rend a request to server & decide to start a new download or not
     *
     * @param urlStr string url
     * @param toFile to file path
     * @throws IOException
     */
    private void prepareDownload(String urlStr, String toFile) throws IOException {
        HttpURLConnection conn = createConnection(urlStr);
        downloadedFile = new File(toFile);
        String remoteLastModified = conn.getHeaderField("Last-Modified");
        fileLength = getContentLength(conn);

        startNewDownload = (!downloadedFile.exists() || downloadedFile.length() >= fileLength ||
                !remoteLastModified.equalsIgnoreCase(Variable.getVariable().getMapLastModified()));
        conn.disconnect();
    }
    
    private long getContentLength(HttpURLConnection conn)
    {
      if (Build.VERSION.SDK_INT >= 24)
      {
        return getContentLengthLong(conn);
      }
      else
      {
        return conn.getContentLength();
      }
    }
    
    @TargetApi(24)
    private long getContentLengthLong(HttpURLConnection conn)
    {
      return conn.getContentLengthLong();
    }

    /**
     * @param urlStr url string
     * @return An URLConnection for HTTP
     * @throws IOException
     */
    private HttpURLConnection createConnection(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // Open connection to URL.
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setReadTimeout(timeout);
        conn.setConnectTimeout(timeout);
        return conn;
    }

    public void log(String s) {
        Log.i(this.getClass().getName(), s);
    }
}
