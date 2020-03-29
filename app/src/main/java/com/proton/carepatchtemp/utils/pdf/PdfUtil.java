package com.proton.carepatchtemp.utils.pdf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.RectangleReadOnly;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.utils.FileUtils;
import com.proton.carepatchtemp.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by luochune on 2018/4/10.
 */

public class PdfUtil {

    BaseFont bfChinese = null;

    /**
     * @param name     报告姓名
     * @param sex      性别
     * @param age      年龄
     * @param tempMax  最高体温
     * @param fever    是否发烧
     * @param shijian  历时
     * @param unit     温度单位
     * @param reportId 报告id
     */
    public void createPdf(Context context, String name, String sex, String age, String tempMax, String fever, String shijian, String unit, String reportId) {
        Document document = new Document(new RectangleReadOnly(842, 595), 20, 20, 20, 20);
        try {
            bfChinese = BaseFont.createFont("assets/fonts/simhei.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            Font font = new Font(bfChinese, 12, Font.NORMAL);
            Font fontTitle = new Font(bfChinese, 16, Font.NORMAL);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(FileUtils.getDirectoryP("pdf") + File.separator + reportId + ".pdf"));
            document.open();
            /**姓名、性别、年龄**/
            Paragraph p1 = new Paragraph(context.getString(R.string.string_carepatch_report), fontTitle);
            Chunk chunk1 = new Chunk(context.getString(R.string.string_real_name) + "：" + name + "   ", font);
            Chunk chunk2 = new Chunk(context.getString(R.string.string_sex) + "：" + sex + "   ", font);
            Chunk chunk3 = new Chunk(context.getString(R.string.string_age) + "：" + age + "   ", font);
            Phrase phrase1 = new Phrase();
            phrase1.add(chunk1);
            phrase1.add(chunk2);
            phrase1.add(chunk3);
            Paragraph p2 = new Paragraph();
            p2.add(phrase1);
            float tempFloat = Float.parseFloat(tempMax);
            //最高温度显示文案
            //格式化温度数字
            String tempMaxStr = Utils.formatTempToStr(tempFloat) + unit;
            /**最高体温、是否发烧、测试时间**/
            Chunk chunk4 = new Chunk(context.getString(R.string.string_the_high_temp) + tempMaxStr + "   ", font);
            Chunk chunk5 = new Chunk(context.getString(R.string.string_is_fever) + fever + "   ", font);
            Chunk chunk6 = new Chunk(context.getString(R.string.string_last_time) + shijian + "   ", font);
            Phrase phrase2 = new Phrase();
            phrase2.add(chunk4);
            phrase2.add(chunk5);
            phrase2.add(chunk6);
            Paragraph p3 = new Paragraph();
            p3.add(phrase2);
            Paragraph pLine = new Paragraph();
            pLine.add(new Chunk(new LineSeparator()));
            Paragraph right = new Paragraph(context.getString(R.string.string_unit) + unit, font);
            right.setAlignment(2);
            float[] widths = {1f, 2f};
            PdfPTable table = new PdfPTable(widths);
            writer.setPageEvent(new PdfPageHelper());
            //设置间距
            p1.setSpacingBefore(50f);
            p2.setSpacingBefore(20f);
            p3.setSpacingBefore(20f);
            pLine.setSpacingBefore(10f);
            right.setSpacingBefore(20f);
            right.setIndentationRight(10f);
            document.add(p1);
            document.add(pLine);
            document.add(p2);
            document.add(p3);
            document.add(right);
            Bitmap bitmap = BitmapFactory.decodeFile(FileUtils.getReport() + File.separator + reportId + ".png");
            // 获得图片的宽高
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            // 设置想要的大小
            int newWidth = 820;
            int newHeight = 390;
            // 计算缩放比例
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // 取得想要缩放的matrix参数
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            // 得到新的图片
            Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,
                    true);

            Image imageNow = Image.getInstance(Bitmap2Bytes(newbm));
            imageNow.scalePercent(100);
            imageNow.setAlignment(1);
            document.add(imageNow);
            document.add(table);
            document.close();
        } catch (Exception error) {
            document.close();
        }
    }

    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 50, baos);
        return baos.toByteArray();
    }

    class PdfPageHelper extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            super.onEndPage(writer, document);
            PdfContentByte cb = writer.getDirectContent();// 得到层
            cb.saveState();
            // 开始
            cb.beginText();
            cb.setFontAndSize(bfChinese, 5);
            float y = document.bottom(-200);
            cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, "第" + writer.getPageNumber() + "页", document.right(), y, 0);
            cb.endText();
            cb.restoreState();
        }
    }
}
