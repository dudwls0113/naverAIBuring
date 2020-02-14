package com.non.sleep.naver.android.src.recommend_ai;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.naver.speech.clientapi.SpeechRecognitionResult;
import com.non.sleep.naver.android.R;
import com.non.sleep.naver.android.src.AudioWriterPCM;
import com.non.sleep.naver.android.src.BaseActivity;
import com.non.sleep.naver.android.src.NaverRecognizer;
import com.non.sleep.naver.android.src.menu_list.MenuListActivitiy;
import com.non.sleep.naver.android.src.recommend.RecommendActivity;
import com.non.sleep.naver.android.src.recommend.interfaces.RecommendRetrofitInterface;
import com.non.sleep.naver.android.src.recommend.interfaces.RecommendView;
import com.non.sleep.naver.android.src.recommend.models.WordResponse;
import com.non.sleep.naver.android.src.recommend_ai.interfaces.interfaces.RecommendAiRetrofitInterface;
import com.non.sleep.naver.android.src.recommend_ai.interfaces.interfaces.RecommendAiView;
import com.non.sleep.naver.android.src.recommend_ai.models.ObjectResponse;
import com.non.sleep.naver.android.src.recommend_yes.RecommendYesActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.non.sleep.naver.android.src.ApplicationClass.MEDIA_TYPE_JSON;
import static com.non.sleep.naver.android.src.ApplicationClass.getRetrofit;

public class RecommendAiActivity extends BaseActivity implements RecommendAiView {

    private Context mContext;

    private MediaPlayer mediaPlayer;

    private Button mButtonYes;

    private static final String TAG = RecommendAiActivity.class.getSimpleName();
    private static final String CLIENT_ID = "g0fd605ajk"; // "내 애플리케이션"에서 Client ID를 확인해서 이곳에 적어주세요.
    private RecommendAiActivity.RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    private TextView txtResult;
    private Button btnStart;
    private String mResult;
    private AudioWriterPCM writer;
    private ImageView mImageViewRecording;
    boolean isRecordingMode = false;

    private RecyclerView mRV;
    public static TextView mTvName1, mTvName2, mTvName3, mTvName4, mTvWon1, mTvWon2, mTvWon3, mTvWon4, mTvType;
    private RecommendAiAdapter adapter;
    private boolean isCPVEnd = false;

    public static int count=0;
    public static int won=0;
    public static TextView mTvTotalPay;

    private Intent intent;

    private void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.clientReady: // 음성인식 준비 가능
//                txtResult.setText("Connected");
                writer = new AudioWriterPCM(Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                break;
            case R.id.audioRecording:
                writer.write((short[]) msg.obj);
                break;
            case R.id.partialResult:
                mResult = (String) (msg.obj);
//                txtResult.setText(mResult);
                Log.d("메세지", mResult);
                break;
            case R.id.finalResult: // 최종 인식 결과
                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                final List<String> results = speechRecognitionResult.getResults();
                StringBuilder strBuf = new StringBuilder();
                final ArrayList<String> similarWord = new ArrayList<>();
                for(String result : results) {
                    strBuf.append(result);
                    strBuf.append("\n");
                    similarWord.add(result);
                }
                mResult = strBuf.toString();
                showCustomToast(mResult);
//                txtResult.setText(mResult);
                postCPV(mResult);
//                postTest(edtTest.getText().toString(), similarWord);
                System.out.println("결과: " + results.get(0));
                postWord(results.get(0));
//                cpvTest(results.get(0));
//                new Thread(){
//                    @Override
//                    public void run() {
//                        testApi(results.get(0));
//                    }
//                }.start();
                break;
            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }
                mResult = "Error code : " + msg.obj.toString();
//                txtResult.setText(mResult);
                Glide.with(mContext).load(R.drawable.ic_speak)
                        .into(mImageViewRecording);
                isRecordingMode = false;
                break;
            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }
                Glide.with(mContext).load(R.drawable.ic_speak)
                        .into(mImageViewRecording);
                isRecordingMode = false;
                break;
            case R.id.recommend_ai_cancel_btn:
                // 취소화면
            case R.id.recommend_ai_pay_iv:
                // 결제화면
                break;
        }
    }

    static class RecognitionHandler extends Handler {
        private final WeakReference<RecommendAiActivity> mActivity;
        RecognitionHandler(RecommendAiActivity activity) {
            mActivity = new WeakReference<RecommendAiActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            RecommendAiActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }

    void postWord(String word){
        showProgressDialog();
        final RecommendAiService recommendAiService = new RecommendAiService(this);
        recommendAiService.postWord(word);
    }

    void postWorldListFun(int age, String gender) {
        showProgressDialog();
        final RecommendAiService recommendAiService = new RecommendAiService(this);
        recommendAiService.postWordList(age, gender);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_recommend);
        mImageViewRecording = findViewById(R.id.activity_main_iv_recording);
        mContext = this;
        permissionCheck();
        init();
        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);

//        String type = getIntent().getStringExtra("type");
//        int age = getIntent().getIntExtra("age", 0);
//        String gender = getIntent().getStringExtra("gender");
//        mTvType.setText(type + " 추천해줘");


        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        if(mRV == null) {
            Log.i("SDVSVD", "SDVSVD");
        }
        mRV.setLayoutManager(mLayoutManager);
        adapter = new RecommendAiAdapter();
        mRV.setAdapter(adapter);

        postWorldListFun(20, "F");

//        final Handler handler = new Handler(){
//            @Override
//            public void handleMessage(@NonNull Message msg) {
//                if(!naverRecognizer.getSpeechRecognizer().isRunning()) {
//                    Log.d("로그", "루프2");
//                    mResult = "";
//                    txtResult.setText("Connecting...");
//                    naverRecognizer.recognize();
//                }
//                else {
//                    Log.d(TAG, "stop and wait Final Result");
//                    naverRecognizer.getSpeechRecognizer().stop();
//                }
//            }
//        };
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
        postCPV("메뉴 추천을 받으시겠습니까?");
        mResult = "";
//        txtResult.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        naverRecognizer.getSpeechRecognizer().initialize();
    }


    void postCPV(final String input){
//        showProgressDialog();
        final RecommendAiService recommendAiService = new RecommendAiService(this);
        new Thread(){
            @Override
            public void run() {
                recommendAiService.postCPV(input);
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
//            isCPVEnd = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void retrofitFailure(String message) {
        hideProgressDialog();
        showCustomToast(message == null || message.isEmpty() ? getString(R.string.network_error) : message);
    }

    @Override
    public void postWordPositiveSuccess() {
        hideProgressDialog();
        Intent intent = new Intent(RecommendAiActivity.this, RecommendYesActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void postWordNegativeSuccess() {

    }

    @Override
    public void postWordList(ArrayList<ObjectResponse> list) {
        Log.i("SDVsd", "SDVDS");
        adapter.mData = list;
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mediaPlayer!=null){
            try{
                mediaPlayer.release();
            }catch (Exception e){
                e.printStackTrace();
            }
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
        if (naverRecognizer.getSpeechRecognizer().isRunning()){
            naverRecognizer.getSpeechRecognizer().stop();
        }
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
                    Glide.with(mContext).load(R.drawable.ic_speak)
                            .into(mImageViewRecording);
                    isRecordingMode = false;
                }
                else{
                    //녹음켜기
//                    showCustomToast("dd");
                    Glide.with(mContext).asGif()
                            .load(R.raw.gif_recoding)
                            .into(mImageViewRecording);
                    isRecordingMode = true;
                    if(!naverRecognizer.getSpeechRecognizer().isRunning()) {
                        Log.d("로그", "루프2");
                        mResult = "";
//                        txtResult.setText("Connecting...");
                        naverRecognizer.recognize();
                    }
                    else {
                        Log.d(TAG, "stop and wait Final Result");
                        naverRecognizer.getSpeechRecognizer().stop();
                    }

                }
                break;
            case R.id.recommend_ai_cancel_btn:
                intent = new Intent(RecommendAiActivity.this, MenuListActivitiy.class);
//                intent.putExtra("",age);
//                intent.putExtra("gender",gender);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.recommend_ai_pay_iv:
//                intent = new Intent(RecommendAiActivity.this, PayActivity.class);
//                intent.putExtra("name1", mTvName1.getText());
//                intent.putExtra("name2", mTvName2.getText());
//                intent.putExtra("name3", mTvName3.getText());
//
//                intent.putExtra("won1", mTvWon1.getText());
//                intent.putExtra("won2", mTvWon2.getText());
//                intent.putExtra("won3", mTvWon3.getText());
//                startActivity(intent);
//                finish();
                break;
        }
    }
}
