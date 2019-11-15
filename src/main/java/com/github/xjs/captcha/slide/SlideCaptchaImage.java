package com.github.xjs.captcha.slide;

import com.github.xjs.captcha.BaseCaptchaImage;
import lombok.Data;

/**
 * 滑块验证码数据<br/>
 * 包含：一张被扣掉一部分背景大图，一张滑块小图, 抠图在图片上的x和y坐标<br/>
 * x代表与图片左边缘的距离像素点<br/>
 * x代表与图片上边缘的距离像素点<br/>
 *
 * */
@Data
public class SlideCaptchaImage implements BaseCaptchaImage {

    /**背景大图*/
    private byte[] bgImgBytes;
    /**滑块小图*/
    private byte[] slideImgBytes;
    /**抠图的X坐标*/
    private int slideX;
    /**抠图的Y坐标*/
    private int slideY;


}
