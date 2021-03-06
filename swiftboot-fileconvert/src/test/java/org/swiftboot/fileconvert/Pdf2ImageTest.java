package org.swiftboot.fileconvert;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author swiftech
 **/
public class Pdf2ImageTest extends BaseTest {

    // Maven install 失败
    @Test
    public void test() {
        InputStream pdf = this.getClass().getResourceAsStream("/source.pdf");

        try {
            fileConverter.convert(new Source("pdf", pdf), new Target("jpg", new ConvertCallback() {
                @Override
                public OutputStream onPage(int page) throws Exception {
                    System.out.println("write to: " + targetDir);
                    FileOutputStream out = new FileOutputStream(new File(targetDir, "target" + page + ".jpg"));
                    return out;
                }
            }));
        } catch (ConvertException e) {
            e.printStackTrace();
        }

    }
}
