package cardinalblue.com.opencvgrabcut;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ImageView preview = (ImageView) findViewById(R.id.preview);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
//                Bitmap result = resize(bitmap);
//                Bitmap result = canny(bitmap);
                Bitmap result = grabCutIter(bitmap);
                preview.setImageBitmap(result);

            }
        });
    }

    public static Bitmap grabCutIter(Bitmap bitmap){
        IplImage inputImage = IplImage.create(bitmap.getWidth(), bitmap.getHeight(), IPL_DEPTH_8U, 4);
        bitmap.copyPixelsToBuffer(inputImage.getByteBuffer());
        Mat inputMat = new Mat(inputImage);
        IplImage result = IplImage.create(bitmap.getWidth(), bitmap.getHeight(), IPL_DEPTH_8U, 1);
        Mat matResult = new Mat(result);
        Rect boundingRectangle = new Rect(10, 10, 20, 20);
//        IplImage bgModel = IplImage.create(bitmap.getWidth(), bitmap.getHeight(), IPL_DEPTH_8U, 1);
        Mat bgModelMat = new Mat();
//        IplImage fgModel = IplImage.create(bitmap.getWidth(), bitmap.getHeight(), IPL_DEPTH_8U, 1);
        Mat fgModelMat = new Mat();
        grabCut(inputMat, matResult, boundingRectangle, bgModelMat, fgModelMat, 1, GC_INIT_WITH_RECT);
        Bitmap outputBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        outputBitmap.copyPixelsFromBuffer(matResult.getByteBuffer());
        cvRelease(inputImage);
//        cvRelease(bgModel);
//        cvRelease(fgModel);
        return outputBitmap;
    }

    public static Bitmap canny(Bitmap bitmap) {
        IplImage image = IplImage.create(bitmap.getWidth(), bitmap.getHeight(), IPL_DEPTH_8U, 4);
        IplImage image2 = IplImage.create(bitmap.getWidth(), bitmap.getHeight(), IPL_DEPTH_8U, 4);
        IplImage output = IplImage.create(bitmap.getWidth(), bitmap.getHeight(), IPL_DEPTH_8U, 1);
        IplImage grayImage = IplImage.create(bitmap.getWidth(), bitmap.getHeight(), IPL_DEPTH_8U, 1);
        IplImage grayOutputImage = IplImage.create(bitmap.getWidth(), bitmap.getHeight(), IPL_DEPTH_8U, 4);
        cvCvtColor(image, image2, CV_BGR2BGRA);
        bitmap.copyPixelsToBuffer(image2.getByteBuffer());
        cvCvtColor(image2, grayImage, CV_RGBA2GRAY);
        cvCanny(grayImage, output, 100, 250);
        cvCvtColor(output, grayOutputImage, CV_GRAY2RGBA);
        Bitmap outputBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        outputBitmap.copyPixelsFromBuffer(grayOutputImage.getByteBuffer());
        cvReleaseImage(image);
        return outputBitmap;
    }

    public static Bitmap resize(Bitmap bitmap) {
        IplImage image = IplImage.create(bitmap.getWidth(), bitmap.getHeight(), IPL_DEPTH_8U, 4);
        bitmap.copyPixelsToBuffer(image.getByteBuffer());
        IplImage resizedImage = IplImage.create(20, 20, IPL_DEPTH_8U, 4);
        cvResize(image, resizedImage);

        Bitmap outputBitmap = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888);
        outputBitmap.copyPixelsFromBuffer(resizedImage.getByteBuffer());
        cvReleaseImage(image);
        return outputBitmap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
