package com.non.sleep.naver.android.src.recommend_ai;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.non.sleep.naver.android.R;
import com.non.sleep.naver.android.src.AudioWriterPCM;
import com.non.sleep.naver.android.src.BaseActivity;
import com.non.sleep.naver.android.src.NaverRecognizer;
import com.non.sleep.naver.android.src.recommend.RecommendService;
import com.non.sleep.naver.android.src.recommend.interfaces.RecommendView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

public class RecommendAiActivity extends BaseActivity implements RecommendView {

    private Context mContext;

    private MediaPlayer mediaPlayer;

    private Button mButtonYes;

    private static final String TAG = RecommendAiActivity.class.getSimpleName();
    private static final String CLIENT_ID = "g0fd605ajk"; // "내 애플리케이션"에서 Client ID를 확인해서 이곳에 적어주세요.
//    private RecommendActivity.RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    private String mResult;
    private AudioWriterPCM writer;
    private ImageView mImageViewRecording;
    boolean isRecordingMode = false;

    private boolean isCPVEnd = false;

    private TextView mTvType;
    private RecyclerView mRV;
    private TextView mTvName1, mTvName2, mTvName3, mTvName4, mTvWon1, mTvWon2, mTvWon3, mTvWon4, mTvTotalPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        mImageViewRecording = findViewById(R.id.activity_main_iv_recording);
        mContext = this;
        permissionCheck();
        init();
//        handler = new RecognitionHandler(this);
//        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(!naverRecognizer.getSpeechRecognizer().isRunning()) {
                    Log.d("로그", "루프2");
                    mResult = "";
                    txtResult.setText("Connecting...");
                    naverRecognizer.recognize();
                }
//                else {
//                    Log.d(TAG, "stop and wait Final Result");
//                    naverRecognizer.getSpeechRecognizer().stop();
//                }
            }
        };
//        new Thread(){
//            @Override
//            public void run() {
//                while (true){
//                    if(isCPVEnd){
//                        Message msg = handler.obtainMessage();
//                        handler.sendMessage(msg);
//                        isCPVEnd = false;
//                        break;
//                    }
//                }
//            }
//        }.start();
    }

    public void permissionCheck(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }


    void init(){
        mTvName1 = findViewById(R.id.recommend_ai_name1_tv);
        mTvName2 = findViewById(R.id.recommend_ai_name2_tv);
        mTvName3 = findViewById(R.id.recommend_ai_name3_tv);
        mTvName4 = findViewById(R.id.recommend_ai_name4_tv);
        mTvWon1 = findViewById(R.id.recommend_ai_won1_tv);
        mTvWon2 = findViewById(R.id.recommend_ai_won2_tv);
        mTvWon3 = findViewById(R.id.recommend_ai_won3_tv);
        mTvWon4 = findViewById(R.id.recommend_ai_won4_tv);
        mTvType = findViewById(R.id.recommend_ai_type_tv);
        mTvTotalPay = findViewById(R.id.recommend_ai_won_tv);
        mRV = findViewById(R.id.recommend_ai_rv);
//        mButtonYes = findViewById(R.id.recommend_btn_yes);
//        mButtonYes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                killMediaPlayer();
//                Intent intent = new Intent(RecommendActivity.this, RecommendYesActivity.class);
//                intent.putExtra("age", 20);
//                intent.putExtra("gender","F");
//                startActivity(intent);
//            }
//        });
//        txtResult = findViewById(R.id.recommend_tv_test);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        postCPV("메뉴 추천을 받으시겠습니까?");
        mResult = "";
//        txtResult.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
//        naverRecognizer.getSpeechRecognizer().initialize();
    }


    void postCPV(final String input){
//        showProgressDialog();
        final RecommendService recommendService = new RecommendService(this);
        new Thread(){
            @Override
            public void run() {
                recommendService.postCPV(input);
            }
        }.start();
    }


    @Override
    public void cpvFailure(String message) {
        hideProgressDialog();
        Looper.prepare();
        showCustomToast(message == null || message.isEmpty() ? getString(R.string.network_error) : message);
        Looper.loop();
    }

    @Override
    public void cpvSuccess(InputStream inputStream) {
        hideProgressDialog();
        int read = 0;
        byte[] bytes = new byte[1024];
        // 랜덤한 이름으로 mp3 파일 생성
        String tempName = Long.valueOf(new Date().getTime()).toString();
        File f = new File(getFilesDir(), tempName + ".mp3");
        System.out.println("file path: " + f.getAbsolutePath());
        try {
            f.createNewFile();
            OutputStream outputStream = new FileOutputStream(f);
            while ((read =inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(f.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            while (mediaPlayer.isPlaying()){
                Log.d("로그", "루프");
            }
            inputStream.close();
            isCPVEnd = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    void killMediaPlayer(){
        if(mediaPlayer!=null){
            try{
                mediaPlayer.release();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        if (naverRecognizer.getSpeechRecognizer().isRunning()){
//            naverRecognizer.getSpeechRecognizer().stop();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        killMediaPlayer();
    }

    public void customOnClick(final View view) {
        switch (view.getId()) {
            case R.id.activity_main_iv_recording:
                showCustomToast("dd");

                if(isRecordingMode){
                    //녹음끄기
//                    showCustomToast("dd");
                    Glide.with(mContext).load(R.raw.gif_recoding)
                            .into(mImageViewRecording);
                    isRecordingMode = true;
                }
                else{
                    //녹음켜기
//                    showCustomToast("dd");
                    Glide.with(mContext).asGif()
                            .load(R.raw.gif_recoding)
                            .into(mImageViewRecording);
                    isRecordingMode = true;

                }
                break;
        }
    }
}
