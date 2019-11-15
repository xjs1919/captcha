/** 
 * copyright(c) 2019-2029 mamcharge.com
 */
 
package com.github.xjs.captcha;

import com.github.xjs.captcha.click.ClickCaptchaImage;
import com.github.xjs.captcha.click.ClickCaptchaImageUtil;
import com.github.xjs.captcha.util.IOUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.InputStream;

/**
 *
 * @author xujs@mamcharge.com
 * @date 2019/11/15 18:15
 **/
public class ClickCaptchaTest {

    @Before
    public void before(){
        ClickCaptchaImageUtil.installFont();
    }

    @Test
    public void testClick()throws Exception{
        InputStream srcStream = ClickCaptchaTest.class.getClassLoader().getResourceAsStream("0.jpg");
        byte[] srcBytes = IOUtil.readInputStream(srcStream);
        ClickCaptchaImage image = ClickCaptchaImageUtil.createClickCaptchaImage(srcBytes,5);
        IOUtil.saveBytes(image.getBgImgBytes(), new FileOutputStream("C:\\Users\\admin\\Desktop\\out.jpg"));
    }
}
