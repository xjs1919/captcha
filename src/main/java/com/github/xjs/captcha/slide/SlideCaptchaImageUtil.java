package com.github.xjs.captcha.slide;

import com.github.xjs.captcha.util.IOUtil;
import com.github.xjs.captcha.util.RandomUtil;
import com.github.xjs.captcha.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.*;
import java.text.NumberFormat;
import java.util.*;

import static java.lang.System.out;


/***
 *
 *  滑块验证码工具类<br/>
 *  图片宽度640，高度320，根据模板图来进行抠图
 *
 * 参考：
 * https://blog.csdn.net/jiabeis/article/details/86477706
 * https://blog.csdn.net/a183400826/article/details/95075732
 * @author xujs@mamcharge.com
 * @date 2019/11/13 15:57
 * */
@Slf4j
public class SlideCaptchaImageUtil {
    /**
     * 根据模板切图
     *
     * @param templateFile
     * @param bgFile
     * @return
     * @throws Exception
     */
    public static SlideCaptchaImage cutByTemplate(File templateFile, File bgFile) throws Exception {
        if(!templateFile.exists()){
            throw new RuntimeException("抠图模板文件不存在");
        }
        if(!bgFile.exists()){
            throw new RuntimeException("背景图文件不存在");
        }
        // 文件类型
        String templateFileType = getFileType(templateFile);
        String bgFileType = getFileType(bgFile);
        if (StringUtil.isEmpty(templateFileType) || StringUtil.isEmpty(bgFileType)) {
            throw new RuntimeException("文件类型不能为空");
        }
        return cutByTemplate(IOUtil.getFileBytes(templateFile), IOUtil.getFileBytes(bgFile),templateFileType,bgFileType);
    }

    /**
     * 根据模板切图
     *
     * @param templateFile
     * @param bgFile
     * @param templateFileType
     * @param bgFileType
     * @return
     * @throws Exception
     */
    public static SlideCaptchaImage cutByTemplate(byte[] templateFile, byte[] bgFile, String templateFileType, String bgFileType) throws Exception {
        if (StringUtil.isEmpty(templateFileType) || StringUtil.isEmpty(bgFileType)) {
            throw new RuntimeException("file type is empty");
        }
        SlideCaptchaImage result = new SlideCaptchaImage();
        // 模板图
        BufferedImage imageTemplate = ImageIO.read(new ByteArrayInputStream(templateFile));
        int sliderTplWidth = imageTemplate.getWidth();
        int sliderTplHeight = imageTemplate.getHeight();
        // 背景图
        BufferedImage imageBg = ImageIO.read(new ByteArrayInputStream(bgFile));
        int bgImgWidth = imageBg.getWidth();
        int bgImgHeight = imageBg.getHeight();
        // 随机生成抠图坐标
        int[] sliderCoordinate = generateSliderCoordinates(bgImgWidth, bgImgHeight, sliderTplWidth, sliderTplHeight);
        int slideX = sliderCoordinate[0];
        int slideY = sliderCoordinate[1];
        // 生成抠图
        BufferedImage resultSliderImage = new BufferedImage(sliderTplWidth, sliderTplHeight, imageTemplate.getType());
        Graphics2D graphics = resultSliderImage.createGraphics();
        resultSliderImage = graphics.getDeviceConfiguration().createCompatibleImage(sliderTplWidth, sliderTplHeight, Transparency.TRANSLUCENT);
        graphics.dispose();
        graphics = resultSliderImage.createGraphics();
        int bold = 5;
        // 获取背景图上抠图的目标区域
        BufferedImage slideAreaImageInBg = getSliderArea(slideX, slideY, sliderTplWidth, sliderTplHeight, new ByteArrayInputStream(bgFile), bgFileType);
        // 根据模板图片抠图
        createSliderPictureByTemplate(slideAreaImageInBg, imageTemplate, resultSliderImage, slideX, slideY);
        // 设置“抗锯齿”的属性
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setStroke(new BasicStroke(bold, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        graphics.drawImage(resultSliderImage, 0, 0, null);
        graphics.dispose();
        //生成滑块图
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(resultSliderImage, templateFileType, os);
        byte[] sliderImageBytes = os.toByteArray();
        result.setSlideImgBytes(sliderImageBytes);
        // 背景图添加遮罩
        BufferedImage bgImageWithMask = ImageIO.read(new ByteArrayInputStream(bgFile));
        byte[] bgImageWithMaskBytes = createBgImageWithMask(bgImageWithMask, imageTemplate, slideX, slideY);
        result.setBgImgBytes(bgImageWithMaskBytes);
        result.setSlideX(slideX);
        result.setSlideY(slideY);
        return result;
    }

    /**
     * 抠图后原图生成
     *
     * @param bgImage
     * @param templateImage
     * @param x
     * @param y
     * @return
     * @throws Exception
     */
    private static byte[] createBgImageWithMask(BufferedImage bgImage, BufferedImage templateImage, int x,
                                                int y) throws Exception {
        // 源文件备份图像矩阵,否则多线程处理同一个图片就会出错
        BufferedImage copyImage = new BufferedImage(bgImage.getWidth(), bgImage.getHeight(), bgImage.getType());
        // 源文件图像矩阵
        int[][] oriImageData = getData(bgImage);
        // 模板图像矩阵
        int[][] templateImageData = getData(templateImage);
        // 复制
        for (int i = 0; i < oriImageData.length; i++) {
            for (int j = 0; j < oriImageData[0].length; j++) {
                int rgb = bgImage.getRGB(i, j);
                copyImage.setRGB(i, j, rgb);
            }
        }
        int[][] martrix = new int[3][3];
        int[] values = new int[9];
        //对源文件图像(x+i,y+j)的抠图坐标点设置成白色
        for (int i = 0; i < templateImageData.length; i++) {
            for (int j = 0; j < templateImageData[0].length ; j++) {
                int rgb = templateImage.getRGB(i, j);

                boolean hasRight = i<templateImageData.length-1;
                int rightRgb = hasRight?templateImage.getRGB(i + 1, j):0;

                boolean hasDown = j<templateImageData[0].length-1;
                int downRgb = hasDown?templateImage.getRGB(i, j + 1):0;

                boolean hasLeft = i>0;
                int leftRgb = hasLeft?templateImage.getRGB(i - 1, j):0;

                boolean hasUp = j > 0;
                int upRgb = hasUp?templateImage.getRGB(i , j-1):0;

                try{
                    //对源文件图像(x+i,y+j)的抠图坐标点设置成白色
                    if (rgb != 16777215 && rgb <= 0) {
                        //描边处理，,取带像素和无像素的界点，判断该点是不是临界轮廓点,如果是设置该坐标像素是白色
                        if(!hasRight || !hasDown || !hasUp || !hasLeft){
                            copyImage.setRGB(x + i, y + j,Color.white.getRGB());
                        }else{
                            if(rightRgb >= 0 ||downRgb >= 0||leftRgb >= 0 || upRgb >= 0) {
                                copyImage.setRGB(x + i, y + j,Color.white.getRGB());
                            } else{
                                // copyImage.setRGB(x + i, y + j, Color.GRAY.getRGB());
                                //抠图区域高斯模糊
                                readPixel(bgImage, x + i, y + j, values);
                                fillMatrix(martrix, values);
                                copyImage.setRGB(x + i, y + j, avgMatrix(martrix));
                            }
                        }
                    } else {
                        //do nothing
                    }
                }catch(ArrayIndexOutOfBoundsException e){
                    log.error("X:"+ (x+i) + "||Y:"+ (y+j),e);
                }
            }
        }
        try{
            //对图片做压缩，降低图片质量，控制原图的size，不再做压缩
            // return compressImage2(copyImage, 0.4f);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(copyImage, "jpg", os);
            return os.toByteArray();
        }catch(Exception e){
            log.error(e.getMessage(), e);
            //压缩异常，直接输出
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(copyImage, "jpg", os);
            return os.toByteArray();
        }
    }

    /**
     * 根据模板图片抠图
     *
     * @param bgImage 背景图上的抠图部分
     * @param templateImage 模板图
     * @param resultImage 要生成的抠图
     * @return
     */

    private static BufferedImage  createSliderPictureByTemplate(BufferedImage bgImage, BufferedImage templateImage,
                                                               BufferedImage resultImage, int sliderX, int sliderY) throws Exception {
        // 源文件图像矩阵
        int[][] oriImageData = getData(bgImage);
        // 模板图像矩阵
        int[][] templateImageData = getData(templateImage);
        // 模板图像宽度
        try {
            for (int i = 0; i < templateImageData.length; i++) {
                // 模板图片高度
                for (int j = 0; j < templateImageData[0].length; j++) {
                    // 如果模板图像当前像素点不是透明 copy源文件信息到目标图片中
                    int rgb = templateImageData[i][j];
                    if (rgb != 16777215 && rgb < 0) {
                        resultImage.setRGB(i, j, oriImageData[i][j]);
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {/*数组越界错误处理，这样页面就不会返回图像问题。*/
            log.error("X:"+ sliderX + "||Y:"+ sliderY,e);
        } catch (Exception e) {
            log.error("X:"+ sliderX + "||Y:"+ sliderY,e);
        }
        return resultImage;
    }


    /**
     * 获取目标区域
     *
     * @param x            随机切图坐标x轴位置
     * @param y            随机切图坐标y轴位置
     * @param targetWidth  切图后目标宽度
     * @param targetHeight 切图后目标高度
     * @param bgInputStream 背景图文件输入流
     * @return
     * @throws Exception
     */
    private static BufferedImage getSliderArea(int x, int y, int targetWidth, int targetHeight, InputStream bgInputStream,
                                               String bgFileType) throws Exception {
        Iterator<ImageReader> imageReaderList = ImageIO.getImageReadersByFormatName(bgFileType);
        ImageReader imageReader = imageReaderList.next();
        // 获取图片流
        ImageInputStream iis = ImageIO.createImageInputStream(bgInputStream);
        // 输入源中的图像将只按顺序读取
        imageReader.setInput(iis, true);
        ImageReadParam param = imageReader.getDefaultReadParam();
        Rectangle rec = new Rectangle(x, y, targetWidth, targetHeight);
        param.setSourceRegion(rec);
        BufferedImage targetImage = imageReader.read(0, param);
        return targetImage;
    }

    /**
     * 生成图像矩阵
     *
     * @param
     * @return
     * @throws Exception
     */
    private static int[][] getData(BufferedImage bimg) throws Exception {
        int[][] data = new int[bimg.getWidth()][bimg.getHeight()];
        for (int i = 0; i < bimg.getWidth(); i++) {
            for (int j = 0; j < bimg.getHeight(); j++) {
                data[i][j] = bimg.getRGB(i, j);
            }
        }
        return data;
    }

    /**
     * 随机生成抠图坐标
     */
    private static int[] generateSliderCoordinates(int bgImgWidth, int bgImgHeight, int sliderTplWidth, int sliderTplHeight) {
        int sliderX = 0, sliderY = 0;
        Random random = new Random();
        int widthDifference = bgImgWidth - sliderTplWidth;
        int heightDifference = bgImgHeight - sliderTplHeight;
        if (widthDifference <= 0) {
            sliderX = 5;
        } else {
            sliderX = RandomUtil.random(random, sliderTplWidth+100, bgImgWidth - sliderTplWidth-20);
        }
        if (heightDifference <= 0) {
            sliderY = 5;
        } else {
            sliderY = random.nextInt(bgImgHeight - sliderTplHeight);
        }
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        return new int[]{sliderX, sliderY};
    }

    private static String getFileType(File file){
        String fileName = file.getName();
        return fileName.substring(fileName.indexOf(".")+1);
    }

    /**https://www.iteye.com/blog/java-mzd-730504*/
    private static byte[] compressImage2(BufferedImage image, float quality) {
        // 如果图片空，返回空
        if (image == null) {
            return null;
        }
        // 得到指定Format图片的writer
        Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
        ImageWriter writer = (ImageWriter) iter.next();
        // 得到指定writer的输出参数设置(ImageWriteParam )
        ImageWriteParam iwp = writer.getDefaultWriteParam();
        // 设置可否压缩
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        // 设置压缩质量参数
        iwp.setCompressionQuality(quality);
        iwp.setProgressiveMode(ImageWriteParam.MODE_DISABLED);
        ColorModel colorModel = ColorModel.getRGBdefault();
        // 指定压缩时使用的色彩模式
        iwp.setDestinationType(new ImageTypeSpecifier(colorModel, colorModel.createCompatibleSampleModel(16, 16)));
        // 开始打包图片，写入byte[]
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IIOImage iIamge = new IIOImage(image, null, null);
        try {
            // 此处因为ImageWriter中用来接收write信息的output要求必须是ImageOutput
            // 通过ImageIo中的静态方法，得到byteArrayOutputStream的ImageOutput
            writer.setOutput(ImageIO.createImageOutputStream(byteArrayOutputStream));
            writer.write(null, iIamge, iwp);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("压缩异常", e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private static void readPixel(BufferedImage img, int x, int y, int[] pixels) {
        int xStart = x - 1;
        int yStart = y - 1;
        int current = 0;
        for (int i = xStart; i < 3 + xStart; i++)
            for (int j = yStart; j < 3 + yStart; j++) {
                int tx = i;
                if (tx < 0) {
                    tx = -tx;
                } else if (tx >= img.getWidth()) {
                    tx = x;
                }
                int ty = j;
                if (ty < 0) {
                    ty = -ty;
                } else if (ty >= img.getHeight()) {
                    ty = y;
                }
                pixels[current++] = img.getRGB(tx, ty);
            }
    }

    private static void fillMatrix(int[][] matrix, int[] values) {
        int filled = 0;
        for (int i = 0; i < matrix.length; i++) {
            int[] x = matrix[i];
            for (int j = 0; j < x.length; j++) {
                x[j] = values[filled++];
            }
        }
    }

    private static int avgMatrix(int[][] matrix) {
        int r = 0;
        int g = 0;
        int b = 0;
        for (int i = 0; i < matrix.length; i++) {
            int[] x = matrix[i];
            for (int j = 0; j < x.length; j++) {
                if (j == 1) {
                    continue;
                }
                Color c = new Color(x[j]);
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }
        return new Color(r / 8, g / 8, b / 8).getRGB();
    }
}