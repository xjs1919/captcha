/** 
 * copyright(c) 2019-2029 mamcharge.com
 */
 
package com.github.xjs.captcha;

import com.github.xjs.captcha.slide.SlideCaptchaImage;
import com.github.xjs.captcha.slide.SlideCaptchaImageUtil;
import com.github.xjs.captcha.util.IOUtil;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @author xujs@mamcharge.com
 * @date 2019/11/15 18:15
 **/
public class SlideCaptchaTest {

    @Test
    public void testSlide()throws Exception{
        InputStream tplStream = SlideCaptchaImageUtil.class.getClassLoader().getResourceAsStream("slide_tpl.png");
        InputStream srcStream = SlideCaptchaTest.class.getClassLoader().getResourceAsStream("1.jpg");
        byte[] tplBytes = IOUtil.readInputStream(tplStream);
        byte[] srcBytes = IOUtil.readInputStream(srcStream);
        SlideCaptchaImage image = SlideCaptchaImageUtil.cutByTemplate(tplBytes, srcBytes, "png", "jpg");
        IOUtil.saveBytes(image.getBgImgBytes(), new FileOutputStream("C:\\Users\\admin\\Desktop\\out-1.jpg"));
        IOUtil.saveBytes(image.getSlideImgBytes(), new FileOutputStream("C:\\Users\\admin\\Desktop\\out-slider.jpg"));

    }
}
