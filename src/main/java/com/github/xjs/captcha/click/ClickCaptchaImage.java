package com.github.xjs.captcha.click;

import com.github.xjs.captcha.BaseCaptchaImage;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 图片文字点选验证码。
 * */
@Data
public class ClickCaptchaImage implements BaseCaptchaImage {

    /**图片数据*/
    private byte[] bgImgBytes;

    /**图片的宽度*/
    private int width;

    /**图片的高度*/
    private int height;

    /**图片上汉字的坐标，x代表的是与图片左边缘的距离，y代表的是与图片上边缘的距离*/
    private List<ClickCaptchaImageUtil.Pt> xys;

    public ClickCaptchaImage(){

    }

    public ClickCaptchaImage(Builder builder){
        this.bgImgBytes = builder.bgImgBytes;
        this.width = builder.width;
        this.height = builder.height;
        this.xys = builder.xys;
    }

    public static Builder newBuilder(){
        return new Builder();
    }

    public static class Builder{

        private byte[] bgImgBytes;
        private int width;
        private int height;
        private List<ClickCaptchaImageUtil.Pt> xys;

        public ClickCaptchaImage build(){
            return new ClickCaptchaImage(this);
        }

        public Builder setBgImgBytes(byte[] bgImgBytes) {
            this.bgImgBytes = bgImgBytes;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder addXy(int x, int y){
            if(this.xys == null){
                this.xys = new ArrayList<ClickCaptchaImageUtil.Pt>(6);
            }
            this.xys.add(new ClickCaptchaImageUtil.Pt(x, y));
            return this;
        }

        public Builder setXys(ClickCaptchaImageUtil.Pt[] pts){
            this.xys = Arrays.asList(pts);
            return this;
        }
    }
}
