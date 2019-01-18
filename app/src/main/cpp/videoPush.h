//
// Created by chengjiang on 2019/1/7.
//

#ifndef LIVE_VIDEOPUSH_H
#define LIVE_VIDEOPUSH_H


class videoPush {


private:
    int videoWidth;
    int videoHeight;
    int bitrate;
    int fps;


public:
    videoPush();
    ~videoPush();
    void setVideoOptions();

};


#endif //LIVE_VIDEOPUSH_H
