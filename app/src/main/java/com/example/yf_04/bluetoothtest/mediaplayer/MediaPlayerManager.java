package com.example.yf_04.bluetoothtest.mediaplayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by YF-04 on 2017/9/26.
 */

public class MediaPlayerManager {

    private static final String TAG = "MediaPlayerManager";

    private MediaPlayer mediaPlayer;
    private Context mContext;
    private List<String> musicNames;
    private MyPreparedListener myPreparedListener;

    public MediaPlayerManager(Context context){
        mContext=context;
        initBackgroundMusic();

    }

    public MyPreparedListener getMyPreparedListener() {
        return myPreparedListener;
    }

    public void setMyPreparedListener(MyPreparedListener myPreparedListener) {
        this.myPreparedListener = myPreparedListener;
    }

    /**
     * 初始化背景音乐,获取所有背景音乐的文件路径
     */
    private void initBackgroundMusic() {
        //读取assects 音乐文件 文件名
        String[] fileNames=null;

        try {
            fileNames=mContext.getResources().getAssets().list("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int length=0;length<fileNames.length;length++){
            Log.d(TAG, "fileName: "+fileNames[length]);
        }
        Log.d(TAG, "----------------------------------------: ");

        if (musicNames==null){
            musicNames=new LinkedList<>();
        }
        if (!musicNames.isEmpty()){
            musicNames.clear();
        }


        for (int i=0;i<fileNames.length;i++){
            if (isMusicName(fileNames[i])){
                musicNames.add(fileNames[i]);
            }
        }
        for (int index=0;index<musicNames.size();index++){
            Log.d(TAG, "musicName: "+musicNames.get(index));
        }

    }


    private boolean isMusicName(String fileName) {
        boolean isMusic = false;
        if (fileName.endsWith(".mp3") || fileName.endsWith(".wav")) {
            isMusic = true;
        }
        return isMusic;
    }

    public void executeSong(int position)throws Exception{
        Log.d(TAG, "executeSong(int position): ");
        if (position>=musicNames.size() || position<0){
            Log.e(TAG, "illegal position: " );
            throw new Exception("Illegal position to get musicName!" );
        }

        String musicName=musicNames.get(position);
        Log.d(TAG, "musicName: "+musicName);

        try {
            executeSong(musicName);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "executeSong e: "+e.getMessage() );
        }


    }

    private AssetManager assetManager;
    public void executeSong(String fileName) throws IOException {
        Log.d(TAG, "executeSong(String fileName): ");
        Log.d(TAG, "fileName: "+fileName);
        assetManager = mContext.getAssets();
        AssetFileDescriptor fileDescriptor = assetManager.openFd(fileName);

        if (isEmpty(mediaPlayer)) {
            mediaPlayer = createNetMp3(fileDescriptor);
        } else{
            mediaPlayer.release();//释放音频资源
            mediaPlayer = createNetMp3(fileDescriptor);
        }
        Log.d(TAG, "mediaPlayer==null: "+(mediaPlayer==null));
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //当播放完音频资源时，会触发onCompletion事件，可以在该事件中释放音频资源，
        //以便其他应用程序可以使用该资源:
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "mediaPlayer.setOnCompletionListener onCompletion: ");

                mp.release();//释放音频资源

            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d(TAG, "mediaPlayer.setOnErrorListener : ");
                Log.d(TAG, "what: "+what);
                Log.d(TAG, "extra: "+extra);
                return false;
            }
        });
        //在播放音频资源之前，必须调用Prepare方法完成些准备工作
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "mediaPlayer.setOnPreparedListener onPrepared: ");

                if (myPreparedListener!=null){
                    myPreparedListener.onPrepared(mp);
                }


            }
        });

    }

    /**
     * 创建本地（assets）mp3
     * @return
     */
    public MediaPlayer createNetMp3(AssetFileDescriptor fd) {
        Log.d(TAG, "createNetMp3: ");
        MediaPlayer mp = new MediaPlayer();
        try {
            long offset=fd.getStartOffset();
            long length=fd.getLength();
            long declarelength=fd.getDeclaredLength();
            Log.d(TAG, "offset: "+offset);
            Log.d(TAG, "length: "+length);
            Log.d(TAG, "declarelength: "+declarelength);

            mp.setDataSource(fd.getFileDescriptor(),offset,length);

        } catch (IllegalArgumentException e) {
            return null;
        } catch (IllegalStateException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        return mp;
    }

    private boolean isEmpty(Object obj) {
        return null == obj || "".equals(obj.toString().trim());
    }

    public List<String> getMusicNames() {
        return musicNames;
    }

    public void setMusicNames(List<String> musicNames) {
        this.musicNames = musicNames;
    }

    public interface  MyPreparedListener{
        public void onPrepared(MediaPlayer mp);
    }

}
