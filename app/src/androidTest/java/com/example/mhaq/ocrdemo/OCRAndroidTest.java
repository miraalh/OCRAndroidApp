package com.example.mhaq.ocrdemo;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ImageView;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class OCRAndroidTest {
    @Test
    public void testTextDetector() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        ImageView testImage = new ImageView(appContext);
        testImage.setImageResource(R.drawable.test);
        String expected = "Tomorrow, and";
        assertEquals(expected, TextDetector.detectText(appContext, testImage));
    }

    @Test
    public void testTextDetectorHarderImage() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        ImageView testImage = new ImageView(appContext);
        testImage.setImageResource(R.drawable.test2);
        String expected = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        TextDetector.detectText(appContext, testImage);
        assertEquals(expected, TextDetector.detectText(appContext, testImage));
    }

    @Test
    public void testTextDetectorEvenHarderImage() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        ImageView testImage = new ImageView(appContext);
        testImage.setImageResource(R.drawable.test3);
        String expected = "text free";
        TextDetector.detectText(appContext, testImage);
        assertEquals(expected, TextDetector.detectText(appContext, testImage));
    }
}
