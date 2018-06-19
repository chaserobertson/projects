package edu.byu.cs.imageeditor;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.spencer.ppmtobitmap.PPMconverter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import edu.byu.cs.imageeditor.studentCode.IImageEditor;
import edu.byu.cs.imageeditor.studentCode.ImageEditor;

public class MainActivity extends AppCompatActivity {

    private enum TYPE { INVERT, GRAYSCALE, EMBOSS, MOTION_BLUR, ORIGINAL };

    private static final int DEFAULT_BLUR_LENGTH = 5;
    private static final String KEY_SELECTED_FILE = "selected_file";
    private static final String KEY_BITMAP = "bitmap";
    private ImageView mImageView;
    private Button mInvertButton;
    private Button mGrayscaleButton;
    private Button mEmbossButton;
    private Button mMotionBlurButton;
    private Button mOriginalButton;
    private EditText mBlurLengthEditText;
    private ProgressBar mProgressBar;

    private Bitmap mInvertedBitmap;
    private Bitmap mGrayscaledBitmap;
    private Bitmap mEmbossedBitmap;
    private Bitmap mMotionBlurredBitmap;
    private Bitmap mOriginalBitmap;

    private int mBlurLength;
    private int mFileIndex = 2;
    private Button mFileSelectorButton;
    private Resources res;
    private AssetManager am;
    private ArrayList<String> fileList;
    private DialogFragment mDialog;
    private String mSelectedFile;
    private IImageEditor mImageEditor;
    private Bitmap mCurrentBitmap;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_SELECTED_FILE, mSelectedFile);
        outState.putParcelable(KEY_BITMAP, mCurrentBitmap);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: initialize the IImageEditor object with your implementation
        mImageEditor = new ImageEditor();

        // initialize default values
        mBlurLength = DEFAULT_BLUR_LENGTH;
        mSelectedFile = "[SELECTED FILE]";
        mCurrentBitmap  = BitmapFactory.decodeResource(getResources(), R.drawable.default_image);

        // initialize saved state from bundle, if necessary
        if(savedInstanceState != null) {
            mSelectedFile = savedInstanceState.getString(KEY_SELECTED_FILE);
            mCurrentBitmap = savedInstanceState.getParcelable(KEY_BITMAP);
        }

        // initialize progress bar
        mProgressBar = (ProgressBar) this.findViewById(R.id.progress_bar);

        // initialize image view
        mImageView = (ImageView) this.findViewById(R.id.ppm_image_view);
        mImageView.setImageBitmap(mCurrentBitmap);

        // initialize button panel and listeners
        mInvertButton = (Button) this.findViewById(R.id.invert_button);
        mInvertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SetImageTask(TYPE.INVERT).execute();
            }
        });
        mGrayscaleButton = (Button) this.findViewById(R.id.grayscale_button);
        mGrayscaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SetImageTask(TYPE.GRAYSCALE).execute();
            }
        });
        mEmbossButton = (Button) this.findViewById(R.id.emboss_button);
        mEmbossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SetImageTask(TYPE.EMBOSS).execute();
            }
        });
        mMotionBlurButton = (Button) this.findViewById(R.id.motion_blur_button);
        mMotionBlurButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SetImageTask(TYPE.MOTION_BLUR).execute();
                }
        });

        mOriginalButton = (Button) this.findViewById(R.id.original_button);
        mOriginalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.mImageView.setImageBitmap(mOriginalBitmap);
            }
        });

        mBlurLengthEditText = (EditText) this.findViewById(R.id.blur_length_edit_text);
        mBlurLengthEditText.setText(mBlurLength+"");
        mBlurLengthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 0) {
                    mBlurLength = Integer.parseInt(s.toString());
                }
                else {
                    mBlurLength = DEFAULT_BLUR_LENGTH;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // button panel disabled until file is selected
        setButtonPanelEnabled(false);

        // initialize file selector button and listener
        mFileSelectorButton = (Button) this.findViewById(R.id.file_selector_button);
        mFileSelectorButton.setText(mSelectedFile);
        mFileSelectorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        intializeFileSelector();
    }

    private void intializeFileSelector() {

        res = getResources();
        am = res.getAssets();

        String[] files = null;
        try {
            files = am.list("");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        fileList = new ArrayList<>();
        if(files != null) {
            for(String file : files) {
                if(file.endsWith(".ppm")) {
                    fileList.add(file);
                }
            }
        }
    }

    public void showDialog() {
        if(mDialog == null) {
            int fileIndex = this.fileList.indexOf(mSelectedFile);
            mDialog = SingleSelectorDialog.newInstance(this.fileList, fileIndex);
        }
        mDialog.show(getSupportFragmentManager(), "dialog");
    }

    public Bitmap readBitmapFromPPM(String ppm) {

        try {
            BufferedInputStream reader = new BufferedInputStream(new ByteArrayInputStream(ppm.getBytes()));
            return PPMconverter.convert(reader);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void onFileSelected(String selectedFile) {



        if(mImageEditor == null){
            Toast.makeText(getApplicationContext(),"Imaged Editor Not Initialized",Toast.LENGTH_SHORT).show();
        }else {
            setButtonPanelEnabled(true);
            mSelectedFile = selectedFile;
            mFileSelectorButton.setText(mSelectedFile);
            new LoadImageTask().execute();
        }
    }

    private void setButtonPanelEnabled(boolean enabled) {
        mInvertButton.setEnabled(enabled);
        mGrayscaleButton.setEnabled(enabled);
        mEmbossButton.setEnabled(enabled);
        mMotionBlurButton.setEnabled(enabled);
        mOriginalButton.setEnabled(enabled);
    }

    private class LoadImageTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            mImageView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                // load file to ImageEditor
                InputStream is1 = am.open(mSelectedFile);
                mImageEditor.load(new BufferedReader(new InputStreamReader(is1, "UTF-8")));

                // load file to imageView
                InputStream is2 = am.open(mSelectedFile);
                byte[] b = new byte[is2.available()];
                is2.read(b);
                return new String(b);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            new SetImageTask(TYPE.ORIGINAL).execute(s);
        }
    }

    private class SetImageTask extends AsyncTask<String, Void, Void> {

        private TYPE mType;

        public SetImageTask(TYPE type) {
            mType = type;
        }

        @Override
        protected void onPreExecute() {
            mImageView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... params) {

            switch (mType) {
                case INVERT:
                    String invertedPPM = mImageEditor.invert();
                    mInvertedBitmap = MainActivity.this.readBitmapFromPPM(invertedPPM);
                    mCurrentBitmap = mInvertedBitmap;
                    break;
                case GRAYSCALE:
                    String grayscaledPPM = mImageEditor.grayscale();
                    mGrayscaledBitmap = MainActivity.this.readBitmapFromPPM(grayscaledPPM);
                    mCurrentBitmap = mGrayscaledBitmap;
                    break;
                case EMBOSS:
                    String embossedPPM = mImageEditor.emboss();
                    mEmbossedBitmap = MainActivity.this.readBitmapFromPPM(embossedPPM);
                    mCurrentBitmap = mEmbossedBitmap;
                    break;
                case MOTION_BLUR:
                    String motionBlurredPPM = mImageEditor.motionblur(mBlurLength);
                    mMotionBlurredBitmap = MainActivity.this.readBitmapFromPPM(motionBlurredPPM);
                    mCurrentBitmap = mMotionBlurredBitmap;
                    break;
                case ORIGINAL:
                    mOriginalBitmap = readBitmapFromPPM(params[0]);
                    mCurrentBitmap = mOriginalBitmap;
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mProgressBar.setVisibility(View.INVISIBLE);
            mImageView.setVisibility(View.VISIBLE);
            mImageView.setImageBitmap(mCurrentBitmap);
        }
    }
}
