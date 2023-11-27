package com.recky.seal;

import cn.hutool.core.io.FileUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @ClassName:CustomSealUtil
 * @Description:
 * @Auther: zhouruiqi
 * @Date: 2023/11/21
 **/
public class CustomSealUtil {
    private final static int CANVASSIZE = 508; // 画布大小（宽高）
    private final static int BORDERWIDTH = 6;// 圆形边框宽度
    private final static int FONTBASESIZE = 60;// 字体基础大小
    private final static int RGB = 0xFF4141; // 文字颜色、边框颜色、五角星颜色
    private final static int STARRADIO = 80; // 五角星长半径（中心点到顶点距离）
    private final static String FONTFAMILYNAME = "FangSong_GB2312";//字体
    private final static float ZOOMRADIO = 1.4f;//字体y轴缩放比例
    private final static int ANGLE = MyAngleEnum.ANGLE_150.getAngle();//底部文字空白角度


    public static void gennerSeal(String text, String outputFilePath) throws Exception {
        char[] companyName = text.toCharArray();
        int fontTextLen = companyName.length;
        int fontSize = FONTBASESIZE - fontTextLen; // 动态设置大小
        BufferedImage bufferedImage = new BufferedImage(CANVASSIZE, CANVASSIZE, BufferedImage.TYPE_4BYTE_ABGR);
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//其他图形抗锯齿
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setRenderingHints(hints);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0));
        g2d.fillRect(0, 0, CANVASSIZE, CANVASSIZE);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1));
        g2d.setStroke(new BasicStroke(BORDERWIDTH));
        g2d.setPaint(Color.decode(String.valueOf(RGB)));
        // 绘制圆
        g2d.drawOval(BORDERWIDTH / 2, BORDERWIDTH / 2, CANVASSIZE - BORDERWIDTH - 1, CANVASSIZE - BORDERWIDTH - 1);  // 由于计算精度问题，需要多减一。
        // 绘制五角星。五角星分别有5个顶点，1个中心。绘制五个四边形，绘制方法：每个顶点（称为'A'点）连接不相邻的两个顶点（称为'B'点、'C'点）,五角星的正中心点（称为'D'点），'B'点、'C'点分别连接'D'点。
        int canvasHalfSize = CANVASSIZE / 2;
        // 顶点坐标（5个）point0:中心点正上方顶点，从point0右边开始数顶点依次为point1，point2，point3，point4
        int[] point0 = new int[]{canvasHalfSize, canvasHalfSize - STARRADIO};
        int[] point1 = new int[]{canvasHalfSize + (int) (Math.sin(0.4 * Math.PI) * STARRADIO), canvasHalfSize - (int) (Math.cos(0.4 * Math.PI) * STARRADIO)};
        int[] point2 = new int[]{canvasHalfSize + (int) (Math.sin(0.2 * Math.PI) * STARRADIO), canvasHalfSize + (int) (Math.cos(0.2 * Math.PI) * STARRADIO)};
        int[] point3 = new int[]{canvasHalfSize - (int) (Math.sin(0.2 * Math.PI) * STARRADIO), canvasHalfSize + (int) (Math.cos(0.2 * Math.PI) * STARRADIO)};
        int[] point4 = new int[]{canvasHalfSize - (int) (Math.sin(0.4 * Math.PI) * STARRADIO), canvasHalfSize - (int) (Math.cos(0.4 * Math.PI) * STARRADIO)};
        // 中心坐标
        int[] point5 = new int[]{canvasHalfSize, canvasHalfSize};
        // 绘制5个四边形
        Polygon polygon = new Polygon();
        polygon.addPoint(point0[0], point0[1]);
        polygon.addPoint(point2[0], point2[1]);
        polygon.addPoint(point5[0], point5[1]);
        polygon.addPoint(point3[0], point3[1]);
        g2d.fillPolygon(polygon);
        polygon = new Polygon();
        polygon.addPoint(point1[0], point1[1]);
        polygon.addPoint(point3[0], point3[1]);
        polygon.addPoint(point5[0], point5[1]);
        polygon.addPoint(point4[0], point4[1]);
        g2d.fillPolygon(polygon);
        polygon = new Polygon();
        polygon.addPoint(point2[0], point2[1]);
        polygon.addPoint(point4[0], point4[1]);
        polygon.addPoint(point5[0], point5[1]);
        polygon.addPoint(point0[0], point0[1]);
        g2d.fillPolygon(polygon);
        polygon = new Polygon();
        polygon.addPoint(point3[0], point3[1]);
        polygon.addPoint(point0[0], point0[1]);
        polygon.addPoint(point5[0], point5[1]);
        polygon.addPoint(point1[0], point1[1]);
        g2d.fillPolygon(polygon);
        polygon = new Polygon();
        polygon.addPoint(point4[0], point4[1]);
        polygon.addPoint(point2[0], point2[1]);
        polygon.addPoint(point5[0], point5[1]);
        polygon.addPoint(point1[0], point1[1]);
        g2d.fillPolygon(polygon);
        // 绘制文字
        Font font = new Font(FONTFAMILYNAME, Font.ROMAN_BASELINE, fontSize);
        AffineTransform transform = new AffineTransform();
        transform.scale(1.0, ZOOMRADIO); // 将字体在垂直方向拉伸
        Font stretchedFont = font.deriveFont(transform);
        g2d.setFont(stretchedFont);
        int fontHalfSize = fontSize / 2;
        g2d.translate(canvasHalfSize - fontHalfSize, 0);
        g2d.rotate((1+ (double) ANGLE /360)*Math.PI, fontHalfSize, canvasHalfSize);//
        float paddingRate = (ZOOMRADIO * 1.2f);
        double theta = (double) (360 - ANGLE)* Math.PI/(180*companyName.length-180);
        for (int i = 0; i < companyName.length; i++) {
            if (i != 0) {
                g2d.rotate(theta, fontHalfSize, canvasHalfSize);
            }
            g2d.drawString(String.valueOf(companyName[i]), 0, fontSize * paddingRate);
        }
        g2d.dispose();
        store(bufferedImage, outputFilePath);
    }

    private static byte[] buildBytes(BufferedImage image) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outStream);
        return outStream.toByteArray();
    }

    private static void store(BufferedImage srcImage, String targetFilePath) throws Exception {
        store(buildBytes(srcImage), targetFilePath);
    }

    private static void store(byte[] srcBytes, String targetFilePath) throws IOException {
        File file = FileUtil.touch(targetFilePath);
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bos.write(srcBytes);
    }
    private enum MyAngleEnum {
        ANGLE_60(60),
        ANGLE_75(75),
        ANGLE_90(90),
        ANGLE_105(105),
        ANGLE_120(120),
        ANGLE_135(135),
        ANGLE_150(150);
        private int angle;
        MyAngleEnum(int angle) {
            this.angle = angle;
        }
        public int getAngle() {
            return angle;
        }
    }
}
