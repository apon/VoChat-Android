package me.apon.vochat.features.message

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.*
import android.os.Bundle
import android.os.Handler
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.voice_activity.*
import me.apon.opuscodec.OpusDecoder
import me.apon.opuscodec.OpusEncoder
import me.apon.vochat.R
import me.apon.vochat.app.BaseActivity
import me.apon.vochat.service.callback.MessageReceiver
import android.util.Base64
import android.util.Log
import android.view.View
import com.orhanobut.hawk.Hawk
import me.apon.vochat.model.*
import android.media.MediaPlayer
import android.os.CountDownTimer


/**
 * Created by yaopeng(aponone@gmail.com) on 2019/4/5.
 */
class VoiceActivity:BaseActivity() {

    lateinit var toId: String
    lateinit var toName: String
    lateinit var fromId:String
    lateinit var fromName:String

    private var sender:Boolean = false

    private var voiceReceiver:MessageReceiver? = null
    private var reqHandlerReceiver:MessageReceiver? = null

    private var mRecordThread:Thread? = null
    private var mAudioTrack:AudioTrack? = null
    private var decoder:OpusDecoder? = null

    private var ringtonePlayer:MediaPlayer?=null

    private val voiceTime = VoiceTime(1000*60*60*24,1000)

    @Volatile private var running = false

    companion object {
        fun start(context: Activity, toId: String, chatName: String) {
            val intent = Intent(context, VoiceActivity::class.java)
            intent.putExtra("toId", toId)
            intent.putExtra("chatName", chatName)
            intent.putExtra("sender",true)
            context.startActivity(intent)
        }
        fun startFromService(context: Context, toId: String, chatName: String) {
            val intent = Intent(context, VoiceActivity::class.java)
            intent.putExtra("toId", toId)
            intent.putExtra("chatName", chatName)
            intent.putExtra("sender",false)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        //Opus支持的采样率为(Hz)：8000, 12000, 16000, 24000, 48000
        internal const val SAMPLE_RATE = 8000

        /**
         * FRAME_SIZE的合法值（与采样率有关）
         *
         * 8000/1000 = 8
         *
         * 8 * 2.5 = 20
         * 8 * 5 = 40
         * 8 * 10 = 80
         * 8 * 20 = 160
         * 8 * 40 = 320
         * 8 * 60 = 640
         */
        internal const val FRAME_SIZE = 160

        //声道数
        internal const val NUM_CHANNELS = 1

//        internal const val PERMISSIONS_REQUEST_RECORD_AUDIO = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.voice_activity)

        supportActionBar.apply {
            this?.hide()
        }

        initAudioTrack()

        val loginUser = Hawk.get<LoginUser>("loginUser")
        fromId = loginUser?.id?:return
        fromName = loginUser.name
        sender = intent.getBooleanExtra("sender",false)
        toId = intent.getStringExtra("toId") ?: "0"
        toName = intent.getStringExtra("chatName") ?: "VoChat"

        name_tv.text = toName

        val url = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        ringtonePlayer = MediaPlayer.create(applicationContext, url)
        ringtonePlayer?.isLooping = true



        //
        ok_bt.setOnClickListener {
            reqVoice(REQ_VOICE_TYPE_ACCEPT)//接受请求
            voiceStart()
            it.visibility = View.GONE
        }

        cancel_bt.setOnClickListener {
            reqVoice(REQ_VOICE_TYPE_CANCEL)//取消，结束聊天
            voiceStop()
        }
        ringtonePlayer?.start()
    }

    override fun onServiceConnected(){
        reqHandlerReceiver = setReqHandlerReceiver()

        if(sender){
            status_tv.text = "Connecting..."
            ok_bt.visibility = View.GONE
            reqVoice(REQ_VOICE_TYPE_ASK)//发起请求
        }else{
            status_tv.text = "Calling you..."
            ok_bt.visibility = View.VISIBLE
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        ringtonePlayer?.release()
        mAudioTrack?.release()
    }

    override fun onBackPressed() {//禁用返回键
//        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        reqVoice(REQ_VOICE_TYPE_CANCEL)//取消，结束聊天
        voiceStop()
    }


    private fun voiceStart(){

        if (running){
            return
        }
        running = true
        voiceReceiver = setVoiceReceiver()

        mRecordThread = Thread(recordRunnable)
        mRecordThread!!.start()
        ringtonePlayer?.release()
        voiceTime.start()
    }

    private fun voiceStop(){
        running = false

        voiceReceiver?.let {
            chatNetService?.deleteReceiver(it)
        }
        reqHandlerReceiver?.let {
            chatNetService?.deleteReceiver(it)
        }

        mAudioTrack?.release()
        voiceTime.cancel()
        finish()
    }

    /**
     * 发送聊天请求
     */
    private fun reqVoice(type:Int){
        val req = ReqVoiceReq(fromId,fromName,toId,toName,type)
        chatNetService?.sendBroadcast(req)
    }

    private fun setReqHandlerReceiver():MessageReceiver{
        val reqHandlerReceiver = object:MessageReceiver(CMD_REQ_VOICE){
            override fun onReceive(msg: String) {
                val resp = Json.M.moshi.adapter<ReqVoiceResp>(ReqVoiceResp::class.java).fromJson(msg)
                if (resp?.code == 200) {
                    when(resp.reqType){
                        REQ_VOICE_TYPE_ACCEPT->{//被接受
                            voiceStart()
                        }
                        REQ_VOICE_TYPE_CANCEL->{//被取消、被挂断
                            voiceStop()
                        }
                        REQ_VOICE_TYPE_ASK->{

                        }
                    }
                }
            }
        }
        chatNetService?.addReceiver(reqHandlerReceiver)
        return reqHandlerReceiver
    }

    private fun setVoiceReceiver():MessageReceiver{
        val receiver = object :MessageReceiver(CMD_VOICE_MSG,false){
            override fun onReceive(msg: String) {
                val resp = Json.M.moshi.adapter<VoiceMsgResp>(VoiceMsgResp::class.java).fromJson(msg)
                if (resp?.code == 200&&running) {
                    playVoice(resp.content)
                }
            }

        }
        chatNetService?.addReceiver(receiver)
        return receiver
    }

    //发送语音
    private fun sendVoice(voiceBase64:String){
        val req = VoiceMsgReq(fromId,toId,voiceBase64)
        chatNetService?.sendBroadcast(req)
    }

    //播放语音
    @Synchronized private fun playVoice(voiceBase64:String){
        val outBuf = ShortArray(FRAME_SIZE * NUM_CHANNELS)
        val encBuf2 = Base64.decode(voiceBase64,Base64.NO_WRAP)
        //解码
        decoder?.decode(encBuf2, outBuf)
        //播放
        mAudioTrack?.write(outBuf, 0, outBuf.size)
    }


    private fun initAudioTrack() {
        val minBufSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        mAudioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            minBufSize,
            AudioTrack.MODE_STREAM
        )
        mAudioTrack?.play()
        lineBarWave.setAudioSessionId(mAudioTrack!!.audioSessionId)
        // 解码器
        decoder = OpusDecoder()
        decoder?.init(SAMPLE_RATE, NUM_CHANNELS, FRAME_SIZE)
    }

    private inner class VoiceTime(millis: Long, interval: Long): CountDownTimer(millis, interval){
        override fun onFinish() {
        }

        override fun onTick(millisUntilFinished: Long) {
            val total = 60*60*24 - millisUntilFinished/1000
            status_tv.text = "${total}s"
        }

    }

    private val recordRunnable = Runnable {
        val minBufSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            minBufSize
        )

        // 创建编码器
        val encoder = OpusEncoder()
        encoder.init(SAMPLE_RATE, NUM_CHANNELS, FRAME_SIZE, OpusEncoder.OPUS_APPLICATION_VOIP)

        val pcmBuf = ShortArray(FRAME_SIZE * NUM_CHANNELS)
        val enBuf = ByteArray(100)

        try {
            recorder.startRecording()
            while (running) {

                //将数据读到pcmBuf
                recorder.read(pcmBuf, 0, pcmBuf.size)
                //编码
                val encoded = encoder.encode(pcmBuf, enBuf)
                if (encoded>0){
                    val encBuf2 = enBuf.copyOf(encoded)
                    val voice = Base64.encodeToString(encBuf2,Base64.NO_WRAP)
                    //发送
                    sendVoice(voice)
                    //播放
//                    playVoice(voice)
                }

            }
        }catch (e:Exception){
            e.printStackTrace()
        } finally {
            Log.d("VoiceActivity","=========recorder stop======== $this")
            recorder.stop()
            recorder.release()
        }
    }


}