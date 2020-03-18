/** 
 * copyright(c) 2019-2029 mamcharge.com
 */
 
package com.github.xjs.captcha.click;

import com.github.xjs.captcha.util.IOUtil;
import com.github.xjs.captcha.util.OSUtil;
import com.github.xjs.captcha.util.RandomUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *  图片文字点选验证码工具类<br/>
 *
 *  图片宽度640，高度320
 *
 * @author xujs@mamcharge.com
 * @date 2019/11/13 15:57
 **/
@Slf4j
public class ClickCaptchaImageUtil {

    /**字体大小*/
    public static final int FONT_SIZE = 46;
    /**字体大小*/
    public static final int FONT_SIZE_TIP = 30;
    /**1个汉字的宽度*/
    public static final int CHAR_WIDTH = FONT_SIZE + 20;
    /**1个汉字的高度*/
    public static final int CHAR_HEIGHT = FONT_SIZE + 20;
    /**提示信息的高度*/
    public static final int TIP_HEIGHT = 80;
    /**上边和下边留白*/
    public static final int PADDING_Y = 10;
    /**所有的备选汉字*/
    private static String CN_CHARS = "\u7684\u4e00\u4e86\u662f\u6211\u4e0d\u5728\u4eba\u4eec\u6709\u6765\u4ed6\u8fd9\u4e0a\u7740\u4e2a\u5730\u5230\u5927\u91cc\u8bf4\u5c31\u53bb\u5b50\u5f97\u4e5f\u548c\u90a3\u8981\u4e0b\u770b\u5929\u65f6\u8fc7\u51fa\u5c0f\u4e48\u8d77\u4f60\u90fd\u628a\u597d\u8fd8\u591a\u6ca1\u4e3a\u53c8\u53ef\u5bb6\u5b66\u53ea\u4ee5\u4e3b\u4f1a\u6837\u5e74\u60f3\u751f\u540c\u8001\u4e2d\u5341\u4ece\u81ea\u9762\u524d\u5934\u9053\u5b83\u540e\u7136\u8d70\u5f88\u50cf\u89c1\u4e24\u7528\u5979\u56fd\u52a8\u8fdb\u6210\u56de\u4ec0\u8fb9\u4f5c\u5bf9\u5f00\u800c\u5df1\u4e9b\u73b0\u5c71\u6c11\u5019\u7ecf\u53d1\u5de5\u5411\u4e8b\u547d\u7ed9\u957f\u6c34\u51e0\u4e49\u4e09\u58f0\u4e8e\u9ad8\u624b\u77e5\u7406\u773c\u5fd7\u70b9\u5fc3\u6218\u4e8c\u95ee\u4f46\u8eab\u65b9\u5b9e\u5403\u505a\u53eb\u5f53\u4f4f\u542c\u9769\u6253\u5462\u771f\u5168\u624d\u56db\u5df2\u6240\u654c\u4e4b\u6700\u5149\u4ea7\u60c5\u8def\u5206\u603b\u6761\u767d\u8bdd\u4e1c\u5e2d\u6b21\u4eb2\u5982\u88ab\u82b1\u53e3\u653e\u513f\u5e38\u6c14\u4e94\u7b2c\u4f7f\u5199\u519b\u5427\u6587\u8fd0\u518d\u679c\u600e\u5b9a\u8bb8\u5feb\u660e\u884c\u56e0\u522b\u98de\u5916\u6811\u7269\u6d3b\u90e8\u95e8\u65e0\u5f80\u8239\u671b\u65b0\u5e26\u961f\u5148\u529b\u5b8c\u5374\u7ad9\u4ee3\u5458\u673a\u66f4\u4e5d\u60a8\u6bcf\u98ce\u7ea7\u8ddf\u7b11\u554a\u5b69\u4e07\u5c11\u76f4\u610f\u591c\u6bd4\u9636\u8fde\u8f66\u91cd\u4fbf\u6597\u9a6c\u54ea\u5316\u592a\u6307\u53d8\u793e\u4f3c\u58eb\u8005\u5e72\u77f3\u6ee1\u65e5\u51b3\u767e\u539f\u62ff\u7fa4\u7a76\u5404\u516d\u672c\u601d\u89e3\u7acb\u6cb3\u6751\u516b\u96be\u65e9\u8bba\u5417\u6839\u5171\u8ba9\u76f8\u7814\u4eca\u5176\u4e66\u5750\u63a5\u5e94\u5173\u4fe1\u89c9\u6b65\u53cd\u5904\u8bb0\u5c06\u5343\u627e\u4e89\u9886\u6216\u5e08\u7ed3\u5757\u8dd1\u8c01\u8349\u8d8a\u5b57\u52a0\u811a\u7d27\u7231\u7b49\u4e60\u9635\u6015\u6708\u9752\u534a\u706b\u6cd5\u9898\u5efa\u8d76\u4f4d\u5531\u6d77\u4e03\u5973\u4efb\u4ef6\u611f\u51c6\u5f20\u56e2\u5c4b\u79bb\u8272\u8138\u7247\u79d1\u5012\u775b\u5229\u4e16\u521a\u4e14\u7531\u9001\u5207\u661f\u5bfc\u665a\u8868\u591f\u6574\u8ba4\u54cd\u96ea\u6d41\u672a\u573a\u8be5\u5e76\u5e95\u6df1\u523b\u5e73\u4f1f\u5fd9\u63d0\u786e\u8fd1\u4eae\u8f7b\u8bb2\u519c\u53e4\u9ed1\u544a\u754c\u62c9\u540d\u5440\u571f\u6e05\u9633\u7167\u529e\u53f2\u6539\u5386\u8f6c\u753b\u9020\u5634\u6b64\u6cbb\u5317\u5fc5\u670d\u96e8\u7a7f\u5185\u8bc6\u9a8c\u4f20\u4e1a\u83dc\u722c\u7761\u5174\u5f62\u91cf\u54b1\u89c2\u82e6\u4f53\u4f17\u901a\u51b2\u5408\u7834\u53cb\u5ea6\u672f\u996d\u516c\u65c1\u623f\u6781\u5357\u67aa\u8bfb\u6c99\u5c81\u7ebf\u91ce\u575a\u7a7a\u6536\u7b97\u81f3\u653f\u57ce\u52b3\u843d\u94b1\u7279\u56f4\u5f1f\u80dc\u6559\u70ed\u5c55\u5305\u6b4c\u7c7b\u6e10\u5f3a\u6570\u4e61\u547c\u6027\u97f3\u7b54\u54e5\u9645\u65e7\u795e\u5ea7\u7ae0\u5e2e\u5566\u53d7\u7cfb\u4ee4\u8df3\u975e\u4f55\u725b\u53d6\u5165\u5cb8\u6562\u6389\u5ffd\u79cd\u88c5\u9876\u6025\u6797\u505c\u606f\u53e5\u533a\u8863\u822c\u62a5\u53f6\u538b\u6162\u53d4\u80cc\u7ec6";
    /**字体库文件名*/
    private static final String[] FONT_FILE_NAMES = new String[]{"FZJZJW.TTF", "DroidSansFallback.ttf"};
    /**提示信息的字体*/
    private static final String FONT_NAME_TIP = "Droid Sans Fallback" ;
    /**图片上的文字的字体*/
    private static final String FONT_NAME_CHAR = "方正剪纸简体";
    /**
     * 安装字体库
     * */
    public synchronized static void installFont(){
        if(!OSUtil.isUnixLikeSystem()){
            return;
        }
        try{
            File fontDir = new File("/usr/share/fonts");
            if(!fontDir.exists()){
                fontDir.mkdirs();
            }
            for(String fileName: FONT_FILE_NAMES){
                installFont(fontDir, fileName);
            }
        }catch(Exception e){
            e.printStackTrace();
            log.error("安装自定义的字体库异常", e);
        }
    }

    private static void installFont(File fontDir, String fontFileName)throws Exception{
        File fontFile = new File(fontDir, fontFileName);
        if(fontFile.exists()){
            return;
        }
        InputStream in = ClickCaptchaImageUtil.class.getClassLoader().getResourceAsStream(fontFileName);
        IOUtil.saveInputStream(in, fontFile);
        log.info("安装字体库：{}", fontFile.getAbsolutePath());
    }

    /**
     * 获取在min（include）和max（include）之间的任意一个整数
     * @param min
     * @param max
     * */
    public static int random(Random random, int min, int max){
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * 获取任意一个汉字
     * */
    public static char getRandomChinese(Random random) {
        return CN_CHARS.charAt(random(random,0, CN_CHARS.length()-1));
    }

    /**
     * 获取任意length个汉字
     * @param length
     * */
    public static List<Character> getRandomChinese(Random random, int length) {
        List<Character> chars = new ArrayList<>(length);
        while(chars.size() < length){
            char ch = getRandomChinese(random);
            if(chars.contains(ch)){
                continue;
            }else{
                chars.add(ch);
            }
        }
        return chars;
    }

    /**
     * 根据本地文件生成redis数据。<br/>
     * 一张图，上面添加6个不同角度旋转的汉字，汉字位置随机，输出4个
     * @param localFile 本地文件
     * */
    public static ClickCaptchaImage createClickCaptchaImage(File localFile) throws Exception {
        return createClickCaptchaImage(localFile, 6, 4);
    }

    /**
     * 根据本地文件生成redis数据。<br/>
     * 一张图，上面添加totalCount个不同角度旋转的汉字，汉字位置随机，输出outCount个
     * @param localFile 本地文件
     * @param totalCount 图片上汉字的总个数
     * @param outCount 让用户选的汉字的个数
     * */
    public static ClickCaptchaImage createClickCaptchaImage(File localFile, int totalCount, int outCount) throws Exception {
        byte[] imgBytes = IOUtil.getFileBytes(localFile);
        return createClickCaptchaImage(imgBytes, totalCount, outCount);
    }

    /**
     * 根据本地文件生成redis数据。<br/>
     * 一张图，上面添加totalCount个不同角度旋转的汉字，汉字位置随机，输出outCount个
     * @param imgBytes  图片文件字节
     * @param totalCount 图片上汉字的总个数
     * @param outCount 让用户选的汉字的个数
     * */
    public static ClickCaptchaImage createClickCaptchaImage(byte[] imgBytes, int totalCount, int outCount) throws Exception {
        if(outCount > totalCount){
            outCount = totalCount;
        }
        Random rnd = new Random();
        BufferedImage bgImg = ImageIO.read(new ByteArrayInputStream(imgBytes));
        int bgImgWidth = bgImg.getWidth();
        int bgImgHeight = bgImg.getHeight();
        //生成totalCount个汉字
        List<Character> chars = getRandomChinese(rnd, totalCount);
        //生成totalCount个坐标点
        Pt[] pts = getRandomPoints(rnd, bgImgWidth, bgImgHeight, totalCount);
        //生成要输出的坐标
        Pt[] outPts = new ArrayList<Pt>(Arrays.asList(pts)).subList(0,outCount).toArray(new Pt[0]);
        List<Character> outChars = chars.subList(0, outCount);
        Graphics2D graphics = bgImg.createGraphics();
        //把图片底部填充成成灰色
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, bgImgHeight-TIP_HEIGHT, bgImgWidth, 15);
        graphics.setColor(new Color(231,232,233));
        graphics.fillRect(0, bgImgHeight-TIP_HEIGHT + 15, bgImgWidth, TIP_HEIGHT-15);
        //写上黑色的提示信息
        String text = "请依次点击"+format(outChars);
        Font mFont = new Font(FONT_NAME_TIP, Font.PLAIN, FONT_SIZE_TIP);
        graphics.setFont(mFont);
        graphics.setColor(Color.black);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        FontMetrics fm = graphics.getFontMetrics();
        int x = (bgImgWidth - fm.stringWidth(text)) / 2;
        graphics.drawString(text, x, bgImgHeight - TIP_HEIGHT + FONT_SIZE_TIP + (TIP_HEIGHT-FONT_SIZE_TIP)/2 );
        //写totalCount个汉字
        for(int i=0; i<chars.size(); i++){
            char ch = chars.get(i);
            Pt pt = pts[i];
            //每一个字单独写在一个BufferedImage上，方便做旋转
            BufferedImage charImage = new BufferedImage(CHAR_WIDTH,CHAR_HEIGHT,BufferedImage.TYPE_INT_RGB);
            //先设置背景透明
            Graphics2D g = charImage.createGraphics();
            charImage = g.getDeviceConfiguration().createCompatibleImage(CHAR_WIDTH,CHAR_HEIGHT,Transparency.TRANSLUCENT);
            g.dispose();
            //然后再进行写字
            g = charImage.createGraphics();
            //设置颜色
            Color color = new Color(100+rnd.nextInt(150),100+rnd.nextInt(150),100+rnd.nextInt(150));
            g.setColor(color);
            //设置字体
            Font font = new Font(FONT_NAME_CHAR, Font.PLAIN, FONT_SIZE);
            g.setFont(font);
            //以中心点旋转
            g.rotate(random(rnd,0,60) * Math.PI / 180, CHAR_WIDTH/2, CHAR_HEIGHT/2);
            //写字
            g.drawString(""+ch, 10,CHAR_HEIGHT - 10);
            g.dispose();
            //把整个的BufferedImage写出去
            graphics.drawImage(charImage, null, pt.x, pt.y);
            //把坐标修正成汉字圆心的坐标,否则是左上角的坐标
            pt.x = pt.x + CHAR_WIDTH/2;
            pt.y = pt.y + CHAR_HEIGHT/2;
        }
        graphics.dispose();
        //输出
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(bgImg, "JPEG", out);
        byte[] retBytes = out.toByteArray();
        return ClickCaptchaImage.newBuilder().setBgImgBytes(retBytes)
                .setWidth(bgImgWidth)
                .setHeight(bgImgHeight)
                .setXys(outPts)
                .build();
    }

    /**
     * 随机生成count个坐标点，相互不能有重叠<br/>
     * x方向根据汉字的宽度划分成小的格子，用完一个格子就划掉，保证x不重叠
     * */
    private static Pt[] getRandomPoints(Random rnd, int bgImgWidth, int bgImgHeight, int count) {
        int cellsX = bgImgWidth/CHAR_WIDTH;
        List<String> xlist = new ArrayList<String>(cellsX);
        for(int i=0;i<cellsX;i++){
            xlist.add(i+"");
        }
        Pt[] ret = new Pt[count];
        for(int i=0;i<count;i++){
            int x = getX(xlist, rnd, bgImgWidth);
            int y = random(rnd, PADDING_Y + CHAR_HEIGHT, bgImgHeight- PADDING_Y - TIP_HEIGHT - CHAR_HEIGHT);
            ret[i] = new Pt(x,y);
        }
        return ret;
    }

    private static int getX(List<String> xlist, Random rnd, int bgImgWidth){
        int xIdx = rnd.nextInt(xlist.size());
        int value = Integer.parseInt(xlist.get(xIdx));
        xlist.remove(""+value);
        return value*CHAR_WIDTH;
    }

    private static String format(List<Character> chars){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<chars.size();i++){
            char ch = chars.get(i);
            sb.append("“").append(ch).append("”");
        }
        return sb.toString();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Pt{
        private int x;
        private int y;
    }

}
