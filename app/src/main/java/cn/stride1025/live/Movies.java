package cn.stride1025.live;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;

import java.io.IOException;

public class Movies {



    public void dispose() {


        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

        mediaMetadataRetriever.setDataSource("");

        MediaExtractor mediaExtractor = new MediaExtractor();

        try {
            mediaExtractor.setDataSource("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        int trackCount = mediaExtractor.getTrackCount();
        for (int i=0;i<trackCount;i++){
            MediaFormat format = mediaExtractor.getTrackFormat(i);

            String mime = format.getString(MediaFormat.KEY_MIME);


            if (mime.startsWith("audio")) {


            } else if (mime.startsWith("video")) {




            }






        }



    }
}
