package com.example.mhaq.ocrdemo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

/**
 * Created by mhaq on 5/1/17.
 */

public class TextDetector {
    /**
     * Creates a Text Recognizer and converts the image file to a bitmap, then recognizes the text and displays it on
     * the screen
     */
    protected static String detectText(Context context, ImageView photo){

        // Create the TextRecognizer
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();

        // Check if the TextRecognizer is operational.
        if (!textRecognizer.isOperational()) {

            // Check for low storage and if there is low storage then print an error message
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = context.registerReceiver(null, lowstorageFilter) != null;
            if (hasLowStorage) {
                Log.d("TEXTRECOGNIZER", Constants.LOW_STORAGE_ERROR);
                return Constants.LOW_STORAGE_ERROR;
            }

            //otherwise print a generic error message
            Log.d("TEXTRECOGNIZER", Constants.TEXT_RECOGNIZER_UNAVAILABLE);
            return Constants.TEXT_RECOGNIZER_UNAVAILABLE;
        }

        //converts the image file to a bitmap and detects whatever text is present
        Bitmap imageBitmap = ((BitmapDrawable)photo.getDrawable()).getBitmap();
        Frame.Builder frame = new Frame.Builder();
        frame.setBitmap(imageBitmap);
        SparseArray<TextBlock> detectedTextArray = textRecognizer.detect(frame.build());
        String detectedText = "";
        for (int keyIndex = 0; keyIndex < detectedTextArray.size(); keyIndex++) {
            detectedText += detectedTextArray.valueAt(keyIndex).getValue();
        }
        if (detectedText.isEmpty())
            return "";
        return detectedText;
    }
}
